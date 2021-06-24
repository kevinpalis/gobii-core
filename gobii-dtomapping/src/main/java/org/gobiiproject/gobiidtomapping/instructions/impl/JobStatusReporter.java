package org.gobiiproject.gobiidtomapping.instructions.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.noaudit.DtoMapJob;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

public class JobStatusReporter {

    public JobStatusReporter(String jobId, DtoMapJob dtoMapJob, String fileExtension) {
        this.jobId = jobId;
        this.dtoMapJob = dtoMapJob;
        this.fileExtension = fileExtension;
        this.configSettings = new ConfigSettings();
    }

    private DtoMapJob dtoMapJob;
    private String jobId;
    private String fileExtension;
    private ConfigSettings configSettings;


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public JobProgressStatusType getJobProgressStatusType() throws GobiiDtoMappingException {

        JobProgressStatusType returnVal = null;

        JobDTO jobDTO = dtoMapJob.getJobDetailsByJobName(this.jobId);
        JobProgressStatusType jobProgressStatus = JobProgressStatusType.byValue(jobDTO.getStatus());

        if (jobDTO.getStatus() != null) {
            returnVal = JobProgressStatusType.byValue(jobDTO.getStatus());

        } else {
            throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "The specified instruction file does not exist: " +
                            this.jobId);
        }

        return returnVal;
    }

    /***
     * Gets the extractor instruction from wherever it is located. Notice that this inquiry for the file
     * is completely distinct and separate from job status. Job status is something that we get form the
     * job table because the extractor process is regularly updating that table. We cannot rely on the status
     * in the job table to figure out where the instruction file is because there can be a race condition
     * wherein the file just isn't yet (or isn't any more) located where you would think it should be based
     * on status. Once upon a time, were determined job status based on instruction file location. Those days
     * are long gone. The only thing we care about now is just getting the instruction file from wherever it
     * lives.
     * @param cropType
     * @return
     * @throws Exception
     */
    public String getExtractorInstructionFileFqpn(String cropType) throws Exception {


        String returnVal = configSettings.getProcessingPath(cropType, GobiiFileProcessDir.EXTRACTOR_INSTRUCTIONS)
                + this.jobId
                + this.fileExtension;

        if (!new File(returnVal).exists()) {

            returnVal = configSettings.getProcessingPath(cropType, GobiiFileProcessDir.EXTRACTOR_INPROGRESS)
                    + this.jobId
                    + this.fileExtension;

            if (!new File(returnVal).exists()) {

                returnVal = configSettings.getProcessingPath(cropType, GobiiFileProcessDir.EXTRACTOR_INSTRUCTIONS)
                        + this.jobId
                        + this.fileExtension;

                if (!new File(returnVal).exists()) {
                    returnVal = configSettings.getProcessingPath(cropType, GobiiFileProcessDir.EXTRACTOR_DONE)
                            + this.jobId
                            + this.fileExtension;
                }
            }
        }

        return returnVal;
    }

    /***
     * See commenton getExtractorInstructionFileFqpn
     * @param cropType
     * @return
     * @throws Exception
     */
    public String getLoaderInstructionFileFqpn(String cropType) throws Exception {


        String returnVal = configSettings.getProcessingPath(cropType, GobiiFileProcessDir.LOADER_INSTRUCTIONS)
                + this.jobId
                + this.fileExtension;

        if (!new File(returnVal).exists()) {

            returnVal = configSettings.getProcessingPath(cropType, GobiiFileProcessDir.LOADER_INPROGRESS_FILES)
                    + this.jobId
                    + this.fileExtension;

            if (!new File(returnVal).exists()) {
                returnVal = configSettings.getProcessingPath(cropType, GobiiFileProcessDir.LOADER_INTERMEDIATE_FILES)
                        + this.jobId
                        + this.fileExtension;

                if (!new File(returnVal).exists()) {
                    returnVal = configSettings.getProcessingPath(cropType, GobiiFileProcessDir.LOADER_DONE)
                            + this.jobId
                            + this.fileExtension;

                }
            }
        }

        return returnVal;
    }

    public String getLogErrorMessage() throws GobiiDtoMappingException {

        String returnVal = null;

        try {

            String logDir = configSettings.getFileSystemLog();

            if (logDir.endsWith("/")) {
                logDir = logDir.substring(0, logDir.length() - 1);
            }
            String logFile = logDir + "/" + this.jobId + ".log";

            // check if log file exists
            File logFileObj = new File(logFile);

            if (logFileObj.exists() && !logFileObj.isDirectory()) {

                returnVal = new String(Files.readAllBytes(Paths.get(logFile)));

            } else {

                throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "The log file for this job does not exist");
            }
        } catch (IOException e) {
            throw new GobiiDtoMappingException("Error retrieving log messages for job " + this.jobId + ": " + e.getMessage());
        }

        return returnVal;
    }


}
