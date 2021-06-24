package org.gobiiproject.gobiidomain.services.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.JobService;
import org.gobiiproject.gobiidtomapping.DtoMapSample;
import org.gobiiproject.gobiidtomapping.entity.noaudit.DtoMapJob;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.DnaSampleDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by VCalaminos on 8/30/2017.
 */
public class JobServiceImpl implements JobService {

    Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    private DtoMapJob dtoMapJob = null;

    @Autowired
    private DtoMapSample dtoMapSample = null;

    @Override
    public JobDTO createJob(JobDTO jobDTO) throws GobiiDomainException, ParseException{

        JobDTO returnVal;

        returnVal = dtoMapJob.createJob(jobDTO);

        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);


        return returnVal;
    }

    @Override
    public JobDTO replaceJob(String jobName, JobDTO jobDTO) throws GobiiDomainException {

        JobDTO returnVal;

        if (null == jobDTO.getJobName() || jobDTO.getJobName().equals(jobName)) {

            JobDTO existingJobDTO = dtoMapJob.getJobDetailsByJobName(jobName);

            if (null != existingJobDTO.getJobName() && existingJobDTO.getJobName().equals(jobName)) {

                returnVal = dtoMapJob.replaceJob(jobName, jobDTO);
                returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
                returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

            } else {

                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "The job name specified in the dto ("
                                + jobDTO.getJobName()
                                + ") does not match the job name passed as a parameter "
                                + "("
                                + jobName
                                + ")");

            }

        } else {

            throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "The jobId specified in the dto ("
                            + jobDTO.getJobId()
                            + ") does not match the jobId passed as a parameter "
                            + "("
                            + jobName
                            + ")");

        }

        return returnVal;
    }

    @Override
    public List<JobDTO> getJobs() throws GobiiDomainException {

        List<JobDTO> returnVal;

        returnVal = dtoMapJob.getJobs();

        for (JobDTO currentJobDTO : returnVal) {

            currentJobDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
            currentJobDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
        }

        if (null == returnVal) {

            returnVal = new ArrayList<>();

        }

        return returnVal;

    }

    @Override
    public JobDTO getJobByJobName(String jobName) throws GobiiDomainException {

        JobDTO returnVal;

        returnVal = dtoMapJob.getJobDetailsByJobName(jobName);
        if (null == returnVal) {
            throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "The specified jobId ("
                            + jobName
                            + ") does not match an existing job");

        }

        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        return returnVal;

    }


    @Override
    public JobDTO submitDnaSamplesByJobName(String jobName, List<DnaSampleDTO> dnaSampleDTOList) throws GobiiDomainException {

        JobDTO returnVal;

        returnVal = dtoMapSample.submitDnaSamplesByJobName(jobName, dnaSampleDTOList);

        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.CREATE);


        return returnVal;

    }

}
