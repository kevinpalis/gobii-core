package org.gobiiproject.gobiidomain.services.gdmv3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.DnaRunUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.*;
import org.gobiiproject.gobiimodel.entity.*;
import org.gobiiproject.gobiimodel.types.*;
import org.gobiiproject.gobiimodel.utils.IntegerUtils;
import org.gobiiproject.gobiisampletrackingdao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
public class DnaRunServiceImpl implements DnaRunService {

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ExperimentDao experimentDao;

    @Autowired
    private JobService jobService;

    final ObjectMapper mapper = new ObjectMapper();

    final String loadType = "SAMPLES";

    final String propertyGroupSeparator = ".";

    final HashSet<String> propertyFields = new HashSet<>(Arrays.asList(
        "dnaRunProperties",
        "dnaSampleProperties",
        "germplasmProperties"));

    final Map<String, CvGroupTerm> propertyFieldsCvGroupMap = Map.of(
        "dnaRunProperties", CvGroupTerm.CVGROUP_DNARUN_PROP,
        "dnaSampleProperties", CvGroupTerm.CVGROUP_DNASAMPLE_PROP,
        "germplasmProperties", CvGroupTerm.CVGROUP_GERMPLASM_PROP);

    /**
     * Uploads dnaruns in the file to the database.
     * Also, loads dna samples and germplasms when provided in the same file.
     *
     * @param inputFileStream       Dnarun input file stream
     * @param dnaRunUploadRequest   Request object with meta data and template
     * @param cropType              Crop type to which the dnaruns need to uploaded
     * @return {@link JobDTO}   when dnarun loader job is successfully submitted.
     * @throws GobiiException   Gobii Exception for bad request or if any run time system error
     */
    @Override
    public JobDTO loadDnaRuns(DnaRunUploadRequestDTO dnaRunUploadRequest,
                              String cropType) throws GobiiException {


        LoaderInstruction3 loaderInstruction = new LoaderInstruction3();
        loaderInstruction.setLoadType(loadType);
        loaderInstruction.setAspects(new HashMap<>());

        loaderInstruction.setCropType(cropType);

        // Get user submitting the load
        String userName = ContactService.getCurrentUserName();
        Contact createdBy = contactDao.getContactByUsername(userName);

        // Set contact email in loader instruction
        loaderInstruction.setContactEmail(createdBy.getEmail());

        // Check if input files are found
        if(dnaRunUploadRequest.getInputFiles().size() == 0) {
            throw new InvalidException("request: no input files");
        }

        // Check whether input file paths are valid
        Utils.checkIfInputFilesAreValid(dnaRunUploadRequest.getInputFiles());

        // Set Experiment Id in DnaRun Table Aspects
        if(!IntegerUtils.isNullOrZero(dnaRunUploadRequest.getExperimentId())) {
            Experiment experiment =
                experimentDao.getExperiment(dnaRunUploadRequest.getExperimentId());
            if (experiment == null) {
                throw new GobiiDomainException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "Invalid Experiment");
            }
        }
        
        // Set Project Id in DnaRun and DnaSample Table Aspects
        if(!IntegerUtils.isNullOrZero(dnaRunUploadRequest.getProjectId())) {
            Project project = projectDao.getProject(dnaRunUploadRequest.getProjectId());
            if (project == null) {
                throw new GobiiDomainException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "Invalid Project");
            }
        }

        // After all validations are done, get a new Job object for samples loading
        JobDTO jobDTO = new JobDTO();
        jobDTO.setPayload(GobiiLoaderPayloadTypes.SAMPLES.getTerm());
        jobDTO.setJobMessage("Submitted dnarun load job.");
        JobDTO job = jobService.createLoaderJob(jobDTO);
        String jobName = job.getJobName();
        
        //Set output dir
        String outputFilesDir = Utils.getOutputDir(jobName, cropType);
        loaderInstruction.setOutputDir(outputFilesDir);

        loaderInstruction.setUserRequest(dnaRunUploadRequest);

        loaderInstruction.setJobName(jobName);

        // Write instruction file
        Utils.writeInstructionFile(loaderInstruction, jobName, cropType);

        return jobDTO;
    }

}
