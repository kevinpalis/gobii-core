package org.gobiiproject.gobiiprocess.digester.digest3;

import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.DnaRunUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.templates.DnaRunTemplateDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.DigesterResult;
import org.gobiiproject.gobiimodel.dto.instructions.loader.MasticatorResult;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.*;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.entity.Project;
import org.gobiiproject.gobiimodel.modelmapper.AspectMapper;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.AspectUtils;
import org.gobiiproject.gobiimodel.utils.GobiiFileUtils;
import org.gobiiproject.gobiimodel.utils.IntegerUtils;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;
import org.gobiiproject.gobiisampletrackingdao.ExperimentDao;
import org.gobiiproject.gobiisampletrackingdao.ProjectDao;

import java.io.File;
import java.util.*;

@Slf4j
public class SamplesDigest extends Digest3 {

    final String digestType = "SAMPLES";

    final HashSet<String> propertyFields = new HashSet<>(Arrays.asList(
        "dnaRunProperties",
        "dnaSampleProperties",
        "germplasmProperties"));

    final Map<String, CvGroupTerm> propertyFieldsCvGroupMap = Map.of(
        "dnaRunProperties", CvGroupTerm.CVGROUP_DNARUN_PROP,
        "dnaSampleProperties", CvGroupTerm.CVGROUP_DNASAMPLE_PROP,
        "germplasmProperties", CvGroupTerm.CVGROUP_GERMPLASM_PROP);

    private ProjectDao projectDao;
    private ExperimentDao experimentDao;
    private DnaRunUploadRequestDTO uploadRequest;
    private DnaRunTemplateDTO dnaRunTemplate;

    /**
     * Constructor
     * 
     * @param loaderInstruction {@link LoaderInstructionV3} for loading dnaruns, samples 
     * and germplasms.
     * @param configSettings    Congiguration settings of GDM
     * @throws GobiiException
     */
    public SamplesDigest(LoaderInstruction3 loaderInstruction,
                  ConfigSettings configSettings) throws GobiiException {
        super(loaderInstruction, configSettings);

        // If loadtype is not samples throw exception
        if(!loaderInstruction.getLoadType().equals(digestType)) {
            throw new GobiiException("Invalid digest for instruction");
        }
        this.projectDao = SpringContextLoaderSingleton.getInstance().getBean(ProjectDao.class);
        this.experimentDao =
            SpringContextLoaderSingleton.getInstance().getBean(ExperimentDao.class);
        this.uploadRequest = 
            mapper.convertValue(loaderInstruction.getUserRequest(), DnaRunUploadRequestDTO.class);
        this.dnaRunTemplate = (DnaRunTemplateDTO) getLoaderTemplate(
            uploadRequest.getDnaRunTemplateId(),
            DnaRunTemplateDTO.class);


    }

    /**
     * Digests the loader instruction and creates intermediate files to be consumed by 
     * Ifl.
     * @return {@link DigesterResult} with map to intermediate files for each table to be loaded.  
     */
    @Override
    public DigesterResult digest() throws GobiiException {

        List<File> filesToDigest = new ArrayList<>();
        Map<String, File> intermediateDigestFileMap = new HashMap<>();
        Map<String, MasticatorResult> masticatedFilesMap = new HashMap<>();

    
        try {

            filesToDigest = getFilesToDigest(uploadRequest.getInputFiles());

            // Digested files are merged for each table.
            for(File fileToDigest : filesToDigest) {

                // Ignore non text file
                if(!GobiiFileUtils.isFileTextFile(fileToDigest)) {
                    continue;
                }

                Map<String, Table> aspects = getAspects(fileToDigest);

                // Masticate and set the output.
                masticatedFilesMap = 
                    masticate(fileToDigest, dnaRunTemplate.getFileSeparator(), aspects);

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

    /**
     * @return Map of table names to and aspect map of columns in the table to load to the 
     * respective crop database.
     */
    private Map<String, Table> getAspects(File fileToDigest) throws GobiiException {

        // Set Marker Aspects
        Map<String, Table> aspects = new HashMap<>();


        DnaRunTable dnaRunTable = new DnaRunTable();
        DnaSampleTable dnaSampleTable = new DnaSampleTable();
        GermplasmTable germplasmTable = new GermplasmTable();

        // Get file header columns
        FileHeader fileHeader = 
            getFileHeaderByLineNumber(fileToDigest, dnaRunTemplate.getHeaderLineNumber());
        
        //Get API fields Entity Mapping
        Map<String, List<String>> fileColumnsApiFieldsMap =
            getFileColumnsApiFieldsMap(dnaRunTemplate, propertyFields);

        // To memoize cv for each property group for each table.
        Map<String, Map<String, Cv>> propertiesCvMaps = new HashMap<>();

        // Map for Aspect values to each api field.
        Map<String, Object> aspectValues = new HashMap<>();

        // Set Aspect for each file column
        for(int i = 0; i < fileHeader.getHeaders().length; i++) {
            String fileHeaderColumn = fileHeader.getHeaders()[i];
            ColumnAspect columnAspect = 
                new ColumnAspect(fileHeader.getHeaderLineNumber()+1, i);

            for(String apiFieldName : fileColumnsApiFieldsMap.get(fileHeaderColumn)) {

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

        // Set Project Id in DnaRun and DnaSample Table Aspects
        if(!IntegerUtils.isNullOrZero(uploadRequest.getProjectId())) {
            Project project = projectDao.getProject(uploadRequest.getProjectId());
            if (project == null) {
                throw new GobiiDomainException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "Invalid Project");
            }
            dnaRunTable.setProjectId(project.getProjectId().toString());
            dnaSampleTable.setProjectId(project.getProjectId().toString());
        }

        // Set Experiment Id in DnaRun Table Aspects
        if(!IntegerUtils.isNullOrZero(uploadRequest.getExperimentId())) {
            Experiment experiment =
                experimentDao.getExperiment(uploadRequest.getExperimentId());
            if (experiment == null) {
                throw new GobiiDomainException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "Invalid Experiment");
            }
            dnaRunTable.setExperimentId(experiment.getExperimentId().toString());
        }

        boolean dnaRunMapped =
            AspectMapper.mapTemplateToAspects(dnaRunTemplate, dnaRunTable, aspectValues);

        boolean dnaSampleMapped =
            AspectMapper.mapTemplateToAspects(dnaRunTemplate, dnaSampleTable, aspectValues);

        boolean germplasmMapped =
            AspectMapper.mapTemplateToAspects(dnaRunTemplate, germplasmTable, aspectValues);

        if(dnaRunMapped) {
            String dnaRunTableName = AspectUtils.getTableName(DnaRunTable.class);
            dnaRunTable.setStatus(newStatus.getCvId().toString());
            aspects.put(dnaRunTableName, dnaRunTable);
        }

        if(dnaSampleMapped) {
            String dnaSampleTableName = AspectUtils.getTableName(DnaSampleTable.class);
            dnaSampleTable.setStatus(newStatus.getCvId().toString());
            aspects.put(dnaSampleTableName, dnaSampleTable);
        }

        if(germplasmMapped) {
            String germplasmTableName = AspectUtils.getTableName(GermplasmTable.class);
            germplasmTable.setStatus(newStatus.getCvId().toString());
            aspects.put(germplasmTableName, germplasmTable);
        }

        return aspects;

    }


}
