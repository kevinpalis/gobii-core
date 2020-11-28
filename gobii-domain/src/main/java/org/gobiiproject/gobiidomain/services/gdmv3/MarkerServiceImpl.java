package org.gobiiproject.gobiidomain.services.gdmv3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.cvnames.JobType;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.templates.MarkerTemplateDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.*;
import org.gobiiproject.gobiimodel.entity.*;
import org.gobiiproject.gobiimodel.modelmapper.AspectMapper;
import org.gobiiproject.gobiimodel.modelmapper.EntityFieldBean;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.*;
import org.gobiiproject.gobiimodel.utils.IntegerUtils;
import org.gobiiproject.gobiisampletrackingdao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("unchecked")
@Transactional
public class MarkerServiceImpl implements MarkerService {

    @Autowired
    private LoaderTemplateDao loaderTemplateDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private PlatformDao platformDao;

    @Autowired
    private MapsetDao mapsetDao;

    @Autowired
    private CvDao cvDao;

    @Autowired
    private JobDao jobDao;

    final ObjectMapper mapper = new ObjectMapper();

    final String loadType = "MARKER";

    /**
     * Uploads markers in the file to the database.
     * Also, loads marker positions and linkage groups when provided in the same file.
     *
     * @param markerFile            Input marker file
     * @param markerUploadRequest   Request object with meta data and template
     * @param cropType              Crop type to which the markers need to uploaded
     * @return  {@link JobDTO}
     * @throws GobiiException   Gobii Exception for bad request or if any run time system error
     */
    @Override
    public JobDTO uploadMarkerFile(byte[] markerFile,
                                   MarkerUploadRequestDTO markerUploadRequest,
                                   String cropType) throws GobiiException {

        BufferedReader br;
        LoaderInstruction loaderInstruction = new LoaderInstruction();
        loaderInstruction.setLoadType(loadType);
        loaderInstruction.setAspects(new HashMap<>());

        String fileHeader;
        Map<String, Object> markerTemplateMap;
        MarkerTemplateDTO markerTemplate;

        // Set Marker Aspects
        Map<String, Object> aspects = new HashMap<>();

        loaderInstruction.setCropType(cropType);

        // Tables loaded in marker upload
        MarkerTable markerTable = new MarkerTable();
        LinkageGroupTable linkageGroupTable = new LinkageGroupTable();
        MarkerLinkageGroupTable markerLinkageGroupTable = new MarkerLinkageGroupTable();

        // Get tables names in database
        String markerTableName = DaoUtils.getTableName(MarkerTable.class);
        String linkageGroupTableName = DaoUtils.getTableName(LinkageGroupTable.class);
        String markerLinkageTableName = DaoUtils.getTableName(MarkerLinkageGroupTable.class);

        aspects.put(markerTableName, markerTable);
        aspects.put(linkageGroupTableName, linkageGroupTable);
        aspects.put(markerLinkageTableName, markerLinkageGroupTable);

        // Set Platform Id in Table Aspects
        if(!IntegerUtils.isNullOrZero(markerUploadRequest.getPlatformId())) {
            Platform platform = platformDao.getPlatform(markerUploadRequest.getPlatformId());
            if (platform == null) {
                throw new GobiiDomainException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "Invalid Platform");
            }
            markerTable.setPlatformId(platform.getPlatformId().toString());
            markerLinkageGroupTable.setPlatformId(platform.getPlatformId().toString());
        }

        // Set mapSet for linkage group and marker linkage group
        if(!IntegerUtils.isNullOrZero(markerUploadRequest.getMapId())) {
            Mapset mapset = mapsetDao.getMapset(markerUploadRequest.getMapId());
            if(mapset == null) {
                throw new GobiiDomainException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "Invalid mapset id");
            }
            linkageGroupTable.setMapId(mapset.getMapsetId().toString());
            markerLinkageGroupTable.setMapId(mapset.getMapsetId().toString());
        }

        //Set new status for marker table
        Cv status = cvDao.getCvs(
            "new",
            CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);
        markerTable.setStatus(status.getCvId().toString());

        // Read marker template
        LoaderTemplate loaderTemplate = loaderTemplateDao.getById(
            markerUploadRequest.getMarkerTemplateId());
        try {
            markerTemplateMap = mapper.treeToValue(
                loaderTemplate.getTemplate(), HashMap.class);
            markerTemplate = mapper.treeToValue(
                loaderTemplate.getTemplate(),
                MarkerTemplateDTO.class);
        }
        catch (JsonProcessingException jE) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Invalid marker template object.");
        }

        // Get user submitting the load
        String userName = ContactService.getCurrentUser();
        Contact createdBy;
        try {
            createdBy = contactDao.getContactByUsername(userName);
        }
        catch (Exception e) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Unable to find user in system");
        }

        // Get a new Job object
        Job job = getNewJob();
        job.setStatus(status);
        job.setSubmittedBy(createdBy);
        String jobName = job.getJobName();

        // Set contact email in loader instruction
        loaderInstruction.setContactEmail(createdBy.getEmail());

        //Set Input file
        String inputFilePath = writeInputFile(markerFile, jobName, cropType);
        loaderInstruction.setInputFile(inputFilePath);

        //Set output dir
        String outputFilesDir = getOutputDir(jobName, cropType);
        loaderInstruction.setOutputDir(outputFilesDir);

        //Get API fields Entity Mapping
        Map<String, String> fileColumnsApiFieldsMap =
            this.getFileColumnsApiFieldsMap(markerTemplateMap);

        Map<String, Object> aspectValues = new HashMap<>();

        //Read Header
        InputStream fileInputStream = new ByteArrayInputStream(markerFile);
        try {
            br = new BufferedReader(
                new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
            fileHeader = br.readLine();
        }
        catch (IOException io) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "No able to read file header");
        }

        String[] fileColumns = fileHeader.split("\t");
        Map<String, Cv> markerPropertiesCvsMap = new HashMap<>();
        Map<String, ColumnAspect> markerPropertiesAspects = new HashMap<>();

        for(int i = 0; i < fileColumns.length; i++) {
            String fileColumn = fileColumns[i];
            ColumnAspect columnAspect = new ColumnAspect(1, i);

            // Check for properties fields
            if(fileColumnsApiFieldsMap.get(fileColumn).startsWith("markerProperties.")) {
                if(markerTable.getMarkerProperties() == null) {
                    List<Cv> markerPropertiesCvList = cvDao.getCvListByCvGroup(
                        CvGroupTerm.CVGROUP_MARKER_PROP.getCvGroupName(),
                        null);
                    markerPropertiesCvsMap = Utils.mapCvNames(markerPropertiesCvList);
                }
                String propertyName = fileColumnsApiFieldsMap
                    .get(fileColumn)
                    .replace("markerProperties.", "");

                if(markerPropertiesCvsMap.containsKey(propertyName)) {
                    String propertyId = markerPropertiesCvsMap
                        .get(propertyName)
                        .getCvId().toString();
                    markerPropertiesAspects.put(propertyId, columnAspect);
                }
            }
            else {
                aspectValues.put(
                    fileColumnsApiFieldsMap.get(fileColumn),
                    columnAspect);
            }
        }

        AspectMapper.mapTemplateToAspects(markerTemplate, markerTable, aspectValues);
        AspectMapper.mapTemplateToAspects(markerTemplate, linkageGroupTable, aspectValues);
        AspectMapper.mapTemplateToAspects(markerTemplate, markerLinkageGroupTable, aspectValues);

        validateMarkerTable(markerTable);
        validateLinkageGroupTable(linkageGroupTable);
        validateMarkerLinkageGroupTable(markerLinkageGroupTable);

        //Set JsonAspect for marker properties
        if(markerPropertiesAspects.size() > 0) {
            JsonAspect jsonAspect = new JsonAspect();
            jsonAspect.setJsonMap(markerPropertiesAspects);
            markerTable.setMarkerProperties(jsonAspect);
        }
        loaderInstruction.setAspects(aspects);

        // Write instruction file
        writeInstructionFile(loaderInstruction, jobName, cropType);

        JobDTO jobDTO = new JobDTO();
        jobDao.create(job);
        ModelMapper.mapEntityToDto(job, jobDTO);
        return jobDTO;
    }

    private Job getNewJob() {
        String jobName = UUID.randomUUID().toString().replace("-", "");
        Job job = new Job();
        job.setJobName(jobName);
        job.setMessage("Submitted marker upload job");
        // Get payload type
        Cv payloadType = cvDao.getCvs(GobiiLoaderPayloadTypes.MARKERS.getTerm(),
            CvGroupTerm.CVGROUP_PAYLOADTYPE.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);
        job.setPayloadType(payloadType);
        job.setSubmittedDate(new Date());
        // Get load type
        Cv jobType = cvDao.getCvs(
            JobType.CV_JOBTYPE_LOAD.getCvName(),
            CvGroupTerm.CVGROUP_JOBTYPE.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);
        job.setType(jobType);
        return job;
    }

    /**
     *
     * @param markerFile    byte array of input file to be loaded.
     * @param jobName       name of the job
     * @param cropType      type of the crop
     * @return path of the input file
     */
    private String writeInputFile(byte[] markerFile,
                                  String jobName,
                                  String cropType) throws GobiiDomainException {

        String markerFileName = "markers.txt";
        String rawFilesDir = Utils.getProcessDir(cropType, GobiiFileProcessDir.RAW_USER_FILES);
        String inputFilePath = Paths.get(rawFilesDir, jobName, markerFileName).toString();
        Utils.writeByteArrayToFile(inputFilePath, markerFile);
        return inputFilePath;
    }

    /**
     * Creates output directory for given job and crop type.
     * @param jobName   Name of the job
     * @param cropType  Crop type for which the data is loaded.
     * @return  output directory where digest files needs to be created for the loading job.
     */
    private String getOutputDir(String jobName, String cropType) {
        String interMediateFilesDir = Utils.getProcessDir(cropType,
            GobiiFileProcessDir.LOADER_INTERMEDIATE_FILES);
        String outputFilesDir = Paths.get(interMediateFilesDir, jobName).toString();
        Utils.makeDir(outputFilesDir);
        return outputFilesDir;
    }

    private void writeInstructionFile(LoaderInstruction loaderInstruction,
                                      String jobName,
                                      String cropType) throws GobiiDomainException {
        try {
            String loaderInstructionText = mapper.writeValueAsString(loaderInstruction);
            String instructionFileName = jobName + ".json";

            String instructionFilesDir = Utils.getProcessDir(
                cropType,
                GobiiFileProcessDir.LOADER_INSTRUCTIONS);

            String instructionFilePath = Paths.get(
                instructionFilesDir,
                instructionFileName).toString();

            Utils.writeByteArrayToFile(
                instructionFilePath,
                loaderInstructionText.getBytes());
        }
        catch (JsonProcessingException jE) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Unable to submit job file");
        }
    }

    private void validateMarkerTable(MarkerTable markerTable) throws GobiiDomainException {
        if(markerTable.getPlatformId() == null && markerTable.getPlatformName() == null) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Neither PlatformId nor Platform name mapped");
        }
    }

    private void validateLinkageGroupTable(
        LinkageGroupTable linkageGroupTable) throws GobiiDomainException {
        if(linkageGroupTable.getMapId() == null && linkageGroupTable.getMapName() == null) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Neither MapId nor Genome Map name ");
        }
    }

    private void validateMarkerLinkageGroupTable(
        MarkerLinkageGroupTable markerLinkageGroupTable) throws GobiiDomainException {
        if(markerLinkageGroupTable.getPlatformId() == null &&
            markerLinkageGroupTable.getPlatformName() == null) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Neither PlatformId nor Platform name mapped");
        }
        if(markerLinkageGroupTable.getMapId() == null &&
            markerLinkageGroupTable.getMapName() == null) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Neither MapId nor Genome Map name mapped");
        }
    }

    private Map<String, String> getFileColumnsApiFieldsMap(
        Map<String, Object> markerTemplateMap) {
        Map<String, String> fileColumnsApiFieldsMap = new HashMap<>();
        List<String> fileField;
        for(String apiField : markerTemplateMap.keySet()) {
            if(apiField.equals("markerProperties")) {
                Map<String, List<String>> markerProperties =
                    (HashMap<String, List<String>>) markerTemplateMap.get(apiField);
                for(String property : markerProperties.keySet()) {
                    fileField = markerProperties.get(property);
                    if(fileField.size() > 0) {
                        fileColumnsApiFieldsMap.put(
                            fileField.get(0),
                            apiField+"."+property);
                    }
                }
            }
            else {

                fileField = (List<String>) markerTemplateMap.get(apiField);
                if (fileField.size() > 0) {
                    fileColumnsApiFieldsMap.put(
                        fileField.get(0),
                        apiField);
                }
            }
        }
        return fileColumnsApiFieldsMap;
    }

}
