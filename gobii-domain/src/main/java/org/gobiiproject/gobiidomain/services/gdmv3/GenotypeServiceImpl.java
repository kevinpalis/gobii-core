package org.gobiiproject.gobiidomain.services.gdmv3;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.GenotypeUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.LoaderInstruction3;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.types.GobiiLoaderPayloadTypes;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.springframework.beans.factory.annotation.Autowired;

public class GenotypeServiceImpl implements GenotypeService {

    final String loadType = "MATRIX";

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private JobService jobService;

    public JobDTO loadGenotypes(GenotypeUploadRequestDTO genotypesUploadRequest,
                                String cropType) throws GobiiException {

   
        validateGenotypeUploadRequest(genotypesUploadRequest);

        LoaderInstruction3 loaderInstruction = new LoaderInstruction3();
        
        loaderInstruction.setLoadType(loadType);
        loaderInstruction.setCropType(cropType);

        // Get user submitting the load
        String userName = ContactService.getCurrentUserName();
        Contact createdBy = contactDao.getContactByUsername(userName);

        // Set contact email in loader instruction
        loaderInstruction.setContactEmail(createdBy.getEmail());
        
        // Check if input files are found
        if(genotypesUploadRequest.getInputFiles().size() == 0) {
            throw new InvalidException("request: no input files.");
        }

        // Check whether input file paths are valid
        Utils.checkIfInputFilesAreValid(genotypesUploadRequest.getInputFiles());

        // Get a new Job object for samples loading
        JobDTO jobDTO = new JobDTO();
        jobDTO.setPayload(GobiiLoaderPayloadTypes.MATRIX.getTerm());
        jobDTO.setJobMessage("Submitted genotype matrix load job.");
        JobDTO job = jobService.createLoaderJob(jobDTO);

        String jobName = job.getJobName();

        loaderInstruction.setJobName(jobName);

        //Set output dir
        String outputFilesDir = Utils.getOutputDir(jobName, cropType);
        loaderInstruction.setOutputDir(outputFilesDir);

        loaderInstruction.setUserRequest(genotypesUploadRequest);

        // Write instruction file
        Utils.writeInstructionFile(loaderInstruction, jobName, cropType);

        return jobDTO;

    }

    private void validateGenotypeUploadRequest(GenotypeUploadRequestDTO genotypeUploadRequest
    ) throws GobiiException {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<GenotypeUploadRequestDTO>> violations = 
            validator.validate(genotypeUploadRequest);


        if(violations.size() > 0) {
            String errorMessage = "";

            for(ConstraintViolation<GenotypeUploadRequestDTO> violation : violations) {
                errorMessage += violation.getMessage() + ";";
            }

            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                errorMessage
            );        
        }
    }

}
