package org.gobiiproject.gobiidtomapping.instructions.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapContact;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapDataSet;
import org.gobiiproject.gobiidtomapping.entity.noaudit.DtoMapJob;
import org.gobiiproject.gobiidtomapping.instructions.DtoMapExtractorInstructions;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.JobPayloadType;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.cvnames.JobType;
import org.gobiiproject.gobiimodel.dto.auditable.ContactDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.ExtractorInstructionFilesDTO;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.GobiiDataSetExtract;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.GobiiExtractorInstruction;
import org.gobiiproject.gobiimodel.types.GobiiExtractFilterType;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.types.GobiiFileType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.InstructionFileAccess;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType.CV_PROGRESSSTATUS_COMPLETED;

/**
 * Created by Phil on 4/12/2016.
 * Modified by Angel 12/12/2016
 */
public class DtoMapExtractorInstructionsImpl implements DtoMapExtractorInstructions {

    Logger LOGGER = LoggerFactory.getLogger(DtoMapExtractorInstructionsImpl.class);

    private final String INSTRUCTION_FILE_EXT = ".json";

    @Autowired
    DtoMapContact dtoMapContact;

    @Autowired
    DtoMapDataSet dtoMapDataSet;

    @Autowired
    private DtoMapJob dtoMapJob = null;


    private InstructionFileAccess<GobiiExtractorInstruction> instructionFileAccess = new InstructionFileAccess<>(GobiiExtractorInstruction.class);


    private String makeDestinationDirectoryName(String userEmail,
                                                GobiiExtractFilterType gobiiExtractFilterType,
                                                GobiiFileType getGobiiFileType,
                                                String parentDirectory,
                                                String jobId) {

        String returnVal;

        //$outputdir/pdg66/hapmap/whole_dataset/timestamp/<files>

        String userSegment = userEmail.substring(0, userEmail.indexOf('@'));
        String formatName = getGobiiFileType.toString().toLowerCase();


        returnVal = parentDirectory
                + userSegment
                + "/"
                + formatName
                + "/"
                + gobiiExtractFilterType.toString().toLowerCase()
                + "/"
                + jobId;

        return returnVal;
    }

    @Override
    public ExtractorInstructionFilesDTO writeInstructions(String cropType, ExtractorInstructionFilesDTO extractorInstructionFilesDTO) throws GobiiException {

        ExtractorInstructionFilesDTO returnVal = extractorInstructionFilesDTO;

        try {

            ConfigSettings configSettings = new ConfigSettings();

            String instructionFileDirectory = configSettings.getProcessingPath(cropType,
                    GobiiFileProcessDir.EXTRACTOR_INSTRUCTIONS);

            instructionFileAccess.createDirectory(instructionFileDirectory);

            String instructionFileFqpn = instructionFileDirectory
                    + extractorInstructionFilesDTO.getInstructionFileName()
                    + INSTRUCTION_FILE_EXT;

            List<Integer> datasetIds = new ArrayList<>();
            Integer contactId = null;
            for (GobiiExtractorInstruction currentExtractorInstruction :
                    extractorInstructionFilesDTO.getGobiiExtractorInstructions()) {

                if (LineUtils.isNullOrEmpty(returnVal.getInstructionFileName())) {
                    throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.MISSING_REQUIRED_VALUE,
                            "instruction file name is missing");
                }


                if (LineUtils.isNullOrEmpty(currentExtractorInstruction.getGobiiCropType())) {

                    currentExtractorInstruction.setGobiiCropType(cropType);
                }

                if (null != currentExtractorInstruction.getContactId() && currentExtractorInstruction.getContactId() > 0) {

                    ContactDTO contactDTO = dtoMapContact.get(currentExtractorInstruction.getContactId());

                    contactId = contactDTO.getContactId();

                    if (!LineUtils.isNullOrEmpty(contactDTO.getEmail())) {

                        currentExtractorInstruction.setContactEmail(contactDTO.getEmail());

                    } else {
                        throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                                GobiiValidationStatusType.MISSING_REQUIRED_VALUE,
                                "The contact record for contactId "
                                        + currentExtractorInstruction.getContactId()
                                        + " does not have an email address");
                    }

                } else {
                    throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.MISSING_REQUIRED_VALUE,
                            "contactId is missing");
                }


                String extractorFileDestinationLocation = null;


                for (Integer idx = 0;
                     idx < currentExtractorInstruction.getDataSetExtracts().size();
                     idx++) {

                    GobiiDataSetExtract currentGobiiDataSetExtract = currentExtractorInstruction.getDataSetExtracts().get(idx);

                    if (currentGobiiDataSetExtract.getDataSet() != null && currentGobiiDataSetExtract.getDataSet().getId() > 0) {
                        datasetIds.add(currentGobiiDataSetExtract.getDataSet().getId());
                    }

                    if (currentGobiiDataSetExtract.getListFileName() != null) {

                        String presumptiveListFileFqpn = instructionFileDirectory + currentGobiiDataSetExtract.getListFileName();

                        if (this.instructionFileAccess.doesPathExist(presumptiveListFileFqpn)) {
                            currentGobiiDataSetExtract.setListFileName(presumptiveListFileFqpn);
                        } else {

                            throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                                    GobiiValidationStatusType.MISSING_REQUIRED_VALUE,
                                    "The specified list file name does not exist on the server: " + presumptiveListFileFqpn);
                        }
                    }

                    if (currentGobiiDataSetExtract.getGobiiExtractFilterType()
                            .equals(GobiiExtractFilterType.WHOLE_DATASET)) {
                        // check that we have all required values
                        if (currentGobiiDataSetExtract.getDataSet() == null) {
                            throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                                    GobiiValidationStatusType.MISSING_REQUIRED_VALUE,
                                    "DataSet is missing");
                        }

                    } else if (currentGobiiDataSetExtract.getGobiiExtractFilterType()
                            .equals(GobiiExtractFilterType.BY_SAMPLE)) {

                        if ((currentGobiiDataSetExtract.getProject() == null)
                                && (currentGobiiDataSetExtract.getPrincipleInvestigator() == null)
                                && (currentGobiiDataSetExtract.getListFileName() == null)
                                && ((currentGobiiDataSetExtract.getSampleList() == null) ||
                                (currentGobiiDataSetExtract.getSampleList().size() <= 0))) {

                            throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                                    GobiiValidationStatusType.MISSING_REQUIRED_VALUE,
                                    "The specified extract type is "
                                            + currentGobiiDataSetExtract.getGobiiExtractFilterType()
                                            + ". Please provide at least one of the following: " +
                                            "Principal Investigator, Project, Sample list, or sample file.");


                        }

                    } else if (currentGobiiDataSetExtract.getGobiiExtractFilterType()
                            .equals(GobiiExtractFilterType.BY_MARKER)) {

                        if ((currentGobiiDataSetExtract.getListFileName() == null)
                                && ((currentGobiiDataSetExtract.getMarkerList() == null) ||
                                (currentGobiiDataSetExtract.getMarkerList().size() <= 0))
                                && ((currentGobiiDataSetExtract.getMarkerGroups() == null)
                                || (currentGobiiDataSetExtract.getMarkerGroups().size() <= 0))) {

                            if (currentGobiiDataSetExtract.getPlatforms() == null ||
                                    currentGobiiDataSetExtract.getPlatforms().size() <= 0) {

                                throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                                        GobiiValidationStatusType.MISSING_REQUIRED_VALUE,
                                        "The specified extract type is "
                                                + currentGobiiDataSetExtract.getGobiiExtractFilterType()
                                                + " but no markers and no platforms are specified");
                            }
                        }

                    } else {
                        throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                                GobiiValidationStatusType.UNKNOWN_ENUM_VALUE,
                                "The specified extraction type is unknown: "
                                        + currentGobiiDataSetExtract.getGobiiExtractFilterType());
                    }

                    String extractionFileDestinationPath;

                    if (!currentExtractorInstruction.isQcCheck()) {
                        extractionFileDestinationPath = configSettings.getProcessingPath(cropType, GobiiFileProcessDir.EXTRACTOR_OUTPUT);
                    } else {
                        extractionFileDestinationPath = configSettings.getProcessingPath(cropType, GobiiFileProcessDir.QC_OUTPUT);
                    }

                    extractorFileDestinationLocation = this.makeDestinationDirectoryName(currentExtractorInstruction.getContactEmail(),
                            currentGobiiDataSetExtract.getGobiiExtractFilterType(),
                            currentGobiiDataSetExtract.getGobiiFileType(),
                            extractionFileDestinationPath,
                            extractorInstructionFilesDTO.getInstructionFileName());

                    if (currentExtractorInstruction.getDataSetExtracts().size() > 1) {
                        Integer dataSetIndexSegment = idx + 1;
                        extractorFileDestinationLocation += "/" + dataSetIndexSegment.toString();
                    }

                    if (!instructionFileAccess.doesPathExist(extractorFileDestinationLocation)) {

                        instructionFileAccess.makeDirectory(extractorFileDestinationLocation);

                    } else {
                        instructionFileAccess.verifyDirectoryPermissions(extractorFileDestinationLocation);
                    }

                    currentGobiiDataSetExtract.setExtractDestinationDirectory(extractorFileDestinationLocation);

                }
            } // iterate instructions/files

            if (!instructionFileAccess.doesPathExist(instructionFileFqpn)) {

                InstructionFileAccess<List<GobiiExtractorInstruction>> instructionFileAccess = new InstructionFileAccess<>(GobiiExtractorInstruction.class);


                // write job record
                if (instructionFileAccess.writeInstructions(instructionFileFqpn,
                        returnVal.getGobiiExtractorInstructions())) {


                    returnVal.setJobId(extractorInstructionFilesDTO.getInstructionFileName());


                    JobDTO jobDTOExisting = dtoMapJob.getJobDetailsByJobName(extractorInstructionFilesDTO.getInstructionFileName());
                    if (jobDTOExisting.getJobId() == null || jobDTOExisting.getJobId() <= 0) {


                        JobPayloadType jobPayloadType;
                        boolean thereAreSamples = false;
                        if (extractorInstructionFilesDTO
                                .getGobiiExtractorInstructions()
                                .stream()
                                .filter(gei -> gei.getDataSetExtracts()
                                        .stream()
                                        .filter(dse -> (dse.getSampleList() != null && dse.getSampleList().size() > 0)
                                                || (dse.getListFileName() != null && dse.getListFileName().length() > 0)).count() > 0)
                                .count() > 0
                                ) {
                            thereAreSamples = true;
                        }

                        boolean thereAreMarkers = false;
                        if (extractorInstructionFilesDTO
                                .getGobiiExtractorInstructions()
                                .stream()
                                .filter(gei -> gei.getDataSetExtracts()
                                        .stream()
                                        .filter(dse -> (dse.getMarkerList() != null && dse.getMarkerList().size() > 0)
                                                || (dse.getListFileName() != null && dse.getListFileName().length() > 0)).count() > 0)
                                .count() > 0
                                ) {
                            thereAreMarkers = true;
                        }


                        boolean thereAreDatasets = datasetIds.size() > 0;

                        boolean thereAreMarkerGroups = extractorInstructionFilesDTO
                                .getGobiiExtractorInstructions()
                                .stream()
                                .filter(gei -> gei.getDataSetExtracts()
                                        .stream()
                                        .filter(dse -> dse.getMarkerGroups() != null && dse.getMarkerGroups().size() > 0).count() > 0)
                                .count() > 0;

                        boolean thereIsAProject = extractorInstructionFilesDTO
                                .getGobiiExtractorInstructions()
                                .stream()
                                .filter(gei -> gei.getDataSetExtracts()
                                        .stream()
                                        .filter(dse -> dse.getProject() != null
                                                && dse.getProject().getId() != null && dse.getProject().getId() > 0).count() > 0)
                                .count() > 0;

                        boolean thereIsAPi = extractorInstructionFilesDTO
                                .getGobiiExtractorInstructions()
                                .stream()
                                .filter(gei -> gei.getDataSetExtracts()
                                        .stream()
                                        .filter(dse -> dse.getPrincipleInvestigator() != null
                                                && dse.getPrincipleInvestigator().getId() != null
                                                && dse.getPrincipleInvestigator().getId() > 0).count() > 0)
                                .count() > 0;

                        boolean thereIsAPlatform = extractorInstructionFilesDTO
                                .getGobiiExtractorInstructions()
                                .stream()
                                .filter(gei -> gei.getDataSetExtracts()
                                        .stream()
                                        .filter(dse -> dse.getPlatforms() != null
                                                && dse.getPlatforms().size() > 0).count() > 0)
                                .count() > 0;

                        if (!thereAreMarkers &&
                                (thereAreSamples || thereIsAProject || thereIsAPi)) {
                            jobPayloadType = JobPayloadType.CV_PAYLOADTYPE_SAMPLES;
                        } else if (!thereAreSamples &&
                                (thereAreMarkers || thereIsAPlatform)) {
                            jobPayloadType = JobPayloadType.CV_PAYLOADTYPE_MARKERS;
                        } else if (thereAreSamples && thereAreMarkers) {
                            jobPayloadType = JobPayloadType.CV_PAYLOADTYPE_MARKERSAMPLES;
                        } else if (thereAreSamples && thereAreMarkers) {
                            jobPayloadType = JobPayloadType.CV_PAYLOADTYPE_MARKERSAMPLES;
                        } else if (thereAreDatasets) {
                            jobPayloadType = JobPayloadType.CV_PAYLOADTYPE_MATRIX;
                        } else if (thereAreMarkerGroups) {
                            jobPayloadType = JobPayloadType.CV_PAYLOADTYPE_MARKERS;
                        } else {
                            throw new GobiiException("The instructions for job "
                                    + extractorInstructionFilesDTO.getInstructionFileName()
                                    + " does not have any samples, markers, datasets, or marker groups specified");
                        }


                        JobDTO jobDTONew = new JobDTO();

                        jobDTONew.setJobName(extractorInstructionFilesDTO.getInstructionFileName());
                        jobDTONew.setSubmittedBy(contactId);
                        jobDTONew.setMessage("Instruction file written by web services");
                        jobDTONew.setStatus(JobProgressStatusType.CV_PROGRESSSTATUS_PENDING.getCvName());
                        jobDTONew.setType(JobType.CV_JOBTYPE_EXTRACT.getCvName());
                        jobDTONew.setPayloadType(jobPayloadType.getCvName());
                        jobDTONew.setSubmittedDate(new Date());

                        if (thereAreDatasets) {
                            jobDTONew.setDatasetIds(datasetIds);
                        }

                        dtoMapJob.createJob(jobDTONew);


                    } else {

                        throw new GobiiException("The specified extractor job already exists: " + extractorInstructionFilesDTO.getInstructionFileName());

                    }// if-else a job with that name already exists

                }
            } else {
                throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.VALIDATION_NOT_UNIQUE,
                        "The specified instruction file already exists: " + instructionFileFqpn);
            }


        } catch (GobiiException e) {
            LOGGER.error("Gobii Maping Error", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiException(e);
        }


        return returnVal;

    } // writeInstructions

    @Override
    public ExtractorInstructionFilesDTO getStatus(String cropType, String instructionFileName) throws GobiiException {

        ExtractorInstructionFilesDTO returnVal = new ExtractorInstructionFilesDTO();

        JobStatusReporter jobStatusReporter = new JobStatusReporter(instructionFileName, dtoMapJob, INSTRUCTION_FILE_EXT);

        JobProgressStatusType jobProgressStatus = jobStatusReporter.getJobProgressStatusType();

        try {

            returnVal.setJobId(instructionFileName);

            returnVal.setInstructionFileName(instructionFileName);
            String fileDirExtractorDoneFqpn = jobStatusReporter.getExtractorInstructionFileFqpn(cropType);


            //All we care about here is getting the instruction file and we let the JobStatusReporter figure that
            //out for us. What do not ever do is figure out job status based on the location of the
            //instruction file.
            returnVal.setGobiiExtractorInstructions(setGobiiExtractorInstructionStatus(fileDirExtractorDoneFqpn, jobProgressStatus));

            if (jobProgressStatus.equals(JobProgressStatusType.CV_PROGRESSSTATUS_FAILED)) {

                String logErrorMessage = jobStatusReporter.getLogErrorMessage();

                setStatus(returnVal.getGobiiExtractorInstructions(), logErrorMessage, jobProgressStatus);

            }

        } catch (GobiiException e) {
            LOGGER.error("Gobii Maping Error", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiException(e);
        }

        return returnVal;

    } // getStatus()

    /**
     * Sets the status for a list of gdm extractor instructions
     *
     * @param gobiiExtractorInstructionList list of extractor instructions
     * @param logMessage content of the log file
     * @param jobProgressStatus status of the job
     */

    private void setStatus(List<GobiiExtractorInstruction> gobiiExtractorInstructionList, String logMessage, JobProgressStatusType jobProgressStatus) {

        for (GobiiExtractorInstruction instruction : gobiiExtractorInstructionList) {
            List<GobiiDataSetExtract> dataSetExtracts = instruction.getDataSetExtracts();
            for (GobiiDataSetExtract dataSetExtract : dataSetExtracts) {
                dataSetExtract.setLogMessage(logMessage);
                dataSetExtract.setGobiiJobStatus(jobProgressStatus);
            }
        }

    }


    /**
     * Returns a list of gdm extractor instruction(technically 1). Sets the job status for the data-sets under inspection
     *
     * @param instructionFileFqpn Instruction file path
     * @param jobProgressStatus   job progress status.
     * @return extractor instruction status.
     */
    private List<GobiiExtractorInstruction> setGobiiExtractorInstructionStatus(String instructionFileFqpn, JobProgressStatusType jobProgressStatus) {
        List<GobiiExtractorInstruction> gobiiExtractorInstructionsFromFile = instructionFileAccess.getInstructions(instructionFileFqpn, GobiiExtractorInstruction[].class);
        for (GobiiExtractorInstruction instruction : gobiiExtractorInstructionsFromFile) {
            List<GobiiDataSetExtract> dataSetExtracts = instruction.getDataSetExtracts();
            for (GobiiDataSetExtract dataSetExtract : dataSetExtracts) {
                dataSetExtract.setGobiiJobStatus(jobProgressStatus);
                setExtractedFiles(dataSetExtract);
            }
        }
        return gobiiExtractorInstructionsFromFile;
    }


    /**
     * If the status of the job is completed, all the files in extracted directory are set. Else a list of size 0 is added.
     *
     * @param dataSetExtract Data set
     */
    private void setExtractedFiles(GobiiDataSetExtract dataSetExtract) {
        if (dataSetExtract.getGobiiJobStatus().equals(CV_PROGRESSSTATUS_COMPLETED)) {
            try {
                List<File> filesInFolder = Files.walk(Paths.get(dataSetExtract.getExtractDestinationDirectory()))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                dataSetExtract.setExtractedFiles(filesInFolder);
            } catch (IOException e) {
                //File not found exception
                throw new GobiiDtoMappingException(e);
            }
        } else {
            dataSetExtract.setExtractedFiles(new ArrayList<>());
        }
    }
}
