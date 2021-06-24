package org.gobiiproject.gobiiprocess.digester.digest3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.templates.MarkerTemplateDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.DigesterResult;
import org.gobiiproject.gobiimodel.dto.instructions.loader.MasticatorResult;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.ColumnAspect;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.FileHeader;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.LinkageGroupTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.LoaderInstruction3;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MarkerGroupTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MarkerLinkageGroupTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MarkerTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.Table;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Mapset;
import org.gobiiproject.gobiimodel.entity.Platform;
import org.gobiiproject.gobiimodel.modelmapper.AspectMapper;
import org.gobiiproject.gobiimodel.utils.AspectUtils;
import org.gobiiproject.gobiimodel.utils.GobiiFileUtils;
import org.gobiiproject.gobiimodel.utils.IntegerUtils;
import org.gobiiproject.gobiimodel.validators.FieldValidator;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;
import org.gobiiproject.gobiisampletrackingdao.MapsetDao;
import org.gobiiproject.gobiisampletrackingdao.PlatformDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkersDigest extends Digest3 {

    final String loadType = "MARKER";

    private PlatformDao platformDao;

    private MapsetDao mapsetDao;

    private MarkerUploadRequestDTO markerUploadRequest;
   
    private MarkerTemplateDTO markerTemplate;

    final HashSet<String> propertyFields = new HashSet<>(Arrays.asList("markerProperties")); 
    
    final Map<String, CvGroupTerm> propertyFieldsCvGroupMap = Map.of(
        "markerProperties", CvGroupTerm.CVGROUP_MARKER_PROP);

    public MarkersDigest(LoaderInstruction3 loaderInstruction,
                         ConfigSettings configSettings) throws GobiiException {
        super(loaderInstruction, configSettings);
        this.platformDao = SpringContextLoaderSingleton.getInstance().getBean(PlatformDao.class);
        this.mapsetDao = SpringContextLoaderSingleton.getInstance().getBean(MapsetDao.class);
        this.markerUploadRequest = 
            mapper.convertValue(loaderInstruction.getUserRequest(), MarkerUploadRequestDTO.class);
        
        // Read marker template
        this.markerTemplate = (MarkerTemplateDTO)
            getLoaderTemplate(markerUploadRequest.getMarkerTemplateId(), MarkerTemplateDTO.class);
    }

    public DigesterResult digest() throws GobiiException {
        List<File> filesToDigest = new ArrayList<>();
        Map<String, File> intermediateDigestFileMap = new HashMap<>();
        Map<String, MasticatorResult> masticatedFilesMap = new HashMap<>();

        try {

            filesToDigest = getFilesToDigest(markerUploadRequest.getInputFiles());

            // Digested files are merged for each table.
            for(File fileToDigest : filesToDigest) {

                // Ignore non text file
                if(!GobiiFileUtils.isFileTextFile(fileToDigest)) {
                    continue;
                }

                Map<String, Table> aspects = getAspects(fileToDigest);

                // Masticate and set the output.
                masticatedFilesMap = masticate(fileToDigest, 
                                               markerTemplate.getFileSeparator(), 
                                               aspects);

                // Update the intermediate file map incase if there is any new table
                masticatedFilesMap.forEach((table, masticatorResult) -> {
                    intermediateDigestFileMap.put(table, masticatorResult.getOutputFile());
                });
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new GobiiException(e);
        }

        // Get the load order from ifl config
        List<String> loadOrder = getLoadOrder();
        if(loadOrder == null || loadOrder.size() <= 0) {
            loadOrder = new ArrayList<>(intermediateDigestFileMap.keySet());
        }

        DigesterResult digesterResult = new DigesterResult
                .Builder()
                .setSuccess(true)
                .setSendQc(false)
                .setCropType(loaderInstruction.getCropType())
                .setCropConfig(cropConfig)
                .setIntermediateFilePath(loaderInstruction.getOutputDir())
                .setLoadType(loaderInstruction.getLoadType())
                .setLoaderInstructionsMap(intermediateDigestFileMap)
                .setLoaderInstructionsList(loadOrder)
                .setDatasetType(null) // Dataset type is only required for matrix upload
                .setJobStatusObject(jobStatus)
                .setDatasetId(null)
                .setJobName(loaderInstruction.getJobName())
                .setContactEmail(loaderInstruction.getContactEmail())
                .build();
            
        return digesterResult;

    }

    private Map<String, Table> getAspects(File fileToDigest) {
        
        // Set Marker Aspects
        Map<String, Table> aspects = new HashMap<>();

        // Tables loaded in marker upload
        MarkerTable markerTable = new MarkerTable();
        LinkageGroupTable linkageGroupTable = new LinkageGroupTable();
        MarkerLinkageGroupTable markerLinkageGroupTable = new MarkerLinkageGroupTable();
        MarkerGroupTable markerGroupTable = new MarkerGroupTable();

        // Get tables names in database
        String markerTableName = AspectUtils.getTableName(MarkerTable.class);
        String linkageGroupTableName = AspectUtils.getTableName(LinkageGroupTable.class);
        String markerLinkageTableName = AspectUtils.getTableName(MarkerLinkageGroupTable.class);
        String markerGroupTableName = AspectUtils.getTableName(MarkerGroupTable.class);


        // Set Platform Id in Table Aspects
        if(!IntegerUtils.isNullOrZero(markerUploadRequest.getPlatformId())) {
            Platform platform = platformDao.getPlatform(markerUploadRequest.getPlatformId());
            if (platform == null) {
                throw new GobiiException("Invalid Platform");
            }
            markerTable.setPlatformId(platform.getPlatformId().toString());
            markerLinkageGroupTable.setPlatformId(platform.getPlatformId().toString());
            markerGroupTable.setPlatformId(platform.getPlatformId().toString());
        }

        // Set mapSet for linkage group and marker linkage group
        if(!IntegerUtils.isNullOrZero(markerUploadRequest.getMapId())) {
            Mapset mapset = mapsetDao.getMapset(markerUploadRequest.getMapId());
            if(mapset == null) {
                throw new GobiiException("Invalid mapset id");
            }
            linkageGroupTable.setMapId(mapset.getMapsetId().toString());
            markerLinkageGroupTable.setMapId(mapset.getMapsetId().toString());
        }
        
        // To memoize cv for each property group for each table.
        Map<String, Map<String, Cv>> propertiesCvMaps = new HashMap<>();

        Map<String, Object> aspectValues = new HashMap<>();

        //Get API fields Entity Mapping
        Map<String, List<String>> fileColumnsApiFieldsMap =
            getFileColumnsApiFieldsMap(markerTemplate, propertyFields);

        // Get Header
        FileHeader fileHeader = 
            getFileHeaderByLineNumber(fileToDigest, markerTemplate.getHeaderLineNumber());

        for(int i = 0; i < fileHeader.getHeaders().length; i++) {
            String fileColumn = fileHeader.getHeaders()[i];
            ColumnAspect columnAspect = new ColumnAspect(fileHeader.getHeaderLineNumber()+1, i);

            // ignore file columns not mapped by template.
            if(!fileColumnsApiFieldsMap.containsKey(fileColumn)) {
                continue;
            }

            for(String apiFieldName : fileColumnsApiFieldsMap.get(fileColumn)) {
                String propertyGroupName = null;
                if(apiFieldName.contains(propertyGroupSeparator)) {
                    propertyGroupName =
                        apiFieldName.substring(0, apiFieldName.indexOf(propertyGroupSeparator));
                }

                // Check for dna run properties fields
                if (propertyFields.contains(propertyGroupName)) {
                    String propertyName =
                        apiFieldName.replace(propertyGroupName+propertyGroupSeparator, "");
                    setPropertyAspect(
                        aspectValues,
                        columnAspect,
                        propertiesCvMaps,
                        propertyName,
                        propertyGroupName,
                        propertyFieldsCvGroupMap);
                } else {
                    aspectValues.put(apiFieldName, columnAspect);
                }

            }
        }

        boolean markerTableMapped =
            AspectMapper.mapTemplateToAspects(markerTemplate, markerTable, aspectValues);

        boolean linkageGroupTableMapped =
            AspectMapper.mapTemplateToAspects(markerTemplate, linkageGroupTable, aspectValues);

        AspectMapper.mapTemplateToAspects(markerTemplate, markerGroupTable, aspectValues);

        if(markerTableMapped) {
            validateMarkerTable(markerTable);
            aspects.put(markerTableName, markerTable);
        }

        if(linkageGroupTableMapped) {
            validateLinkageGroupTable(linkageGroupTable);
            aspects.put(linkageGroupTableName, linkageGroupTable);
        }

        if(markerTableMapped && linkageGroupTableMapped) {
            AspectMapper.mapTemplateToAspects(
                markerTemplate,
                markerLinkageGroupTable,
                aspectValues);
            validateMarkerLinkageGroupTable(markerLinkageGroupTable);
            aspects.put(markerLinkageTableName, markerLinkageGroupTable);
        }

        if(markerTableMapped && markerGroupTable.getMarkerGroupName() != null) {
            FieldValidator.validate(markerGroupTable);
            aspects.put(markerGroupTableName, markerGroupTable);
        }

        return aspects;

    }

    private void validateMarkerTable(MarkerTable markerTable) throws GobiiException {
        if(markerTable.getPlatformId() == null && markerTable.getPlatformName() == null) {
            throw new GobiiException("Neither PlatformId nor Platform name mapped");
        }
        FieldValidator.validate(markerTable);
    }

    private void validateLinkageGroupTable(LinkageGroupTable linkageGroupTable
    ) throws GobiiException {
        if(linkageGroupTable.getMapId() == null && linkageGroupTable.getMapName() == null) {
            throw new GobiiException("Neither MapId nor Genome Map name ");
        }
        FieldValidator.validate(linkageGroupTable);
    }

    private void validateMarkerLinkageGroupTable(
        MarkerLinkageGroupTable markerLinkageGroupTable
    ) throws GobiiException {
        if(markerLinkageGroupTable.getPlatformId() == null &&
            markerLinkageGroupTable.getPlatformName() == null) {
            throw new GobiiException("Neither PlatformId nor Platform name mapped");
        }
        if(markerLinkageGroupTable.getMapId() == null &&
            markerLinkageGroupTable.getMapName() == null) {
            throw new GobiiException("Neither MapId nor Genome Map name mapped");
        }
        FieldValidator.validate(markerLinkageGroupTable);
    }



}
