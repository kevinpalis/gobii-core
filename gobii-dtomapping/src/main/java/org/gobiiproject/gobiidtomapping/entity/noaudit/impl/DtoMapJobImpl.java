package org.gobiiproject.gobiidtomapping.entity.noaudit.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gobiiproject.gobiidao.resultset.access.RsJobDao;
import org.gobiiproject.gobiidao.resultset.core.ParamExtractor;
import org.gobiiproject.gobiidao.resultset.core.listquery.DtoListQueryColl;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapDataSet;
import org.gobiiproject.gobiidtomapping.entity.noaudit.DtoMapJob;
import org.gobiiproject.gobiimodel.cvnames.JobPayloadType;
import org.gobiiproject.gobiimodel.cvnames.JobType;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.DateUtils;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by VCalaminos on 9/4/2017.
 */
public class DtoMapJobImpl implements DtoMapJob {

    Logger LOGGER = LoggerFactory.getLogger(DtoMapJobImpl.class);

    @Autowired
    private RsJobDao rsJobDao;
    @Autowired
    private DtoMapDataSet dtoMapDataSet = null;

    @Autowired
    private DtoListQueryColl dtoListQueryColl;


    @SuppressWarnings("unchecked")
    @Override
    public List<JobDTO> getJobs() throws GobiiDtoMappingException {

        List<JobDTO> returnVal = new ArrayList<>();

        try {

            returnVal = (List<JobDTO>) dtoListQueryColl.getList(ListSqlId.QUERY_ID_JOB_ALL);

        } catch (Exception e) {
            LOGGER.error("GObii Mapping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JobDTO getJobDetailsByJobName(String jobName) throws GobiiDtoMappingException {

        JobDTO returnVal = new JobDTO();

        try {

            Map<String, Object> jdbcParameters = new HashMap<>();
            jdbcParameters.put("jobName", jobName);
            List<JobDTO> jobs = (List<JobDTO>) dtoListQueryColl.getList(ListSqlId.QUERY_ID_JOB_BY_JOBNAME, jdbcParameters);

            if(jobs.size() > 0 ) {
                returnVal = jobs.get(0);
            }


        } catch (Exception e) {
            LOGGER.error("Gobii Mapping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;

    }


    @Override
    public JobDTO createJob(JobDTO jobDTO) throws GobiiDtoMappingException, ParseException {

        //check if JobName is not null

        if(LineUtils.isNullOrEmpty(jobDTO.getJobName())){ // if empty or null, indicate a new jobName.
            jobDTO.setJobName(DateUtils.makeDateIdString());
        }

        JobDTO returnVal = jobDTO;

        // check if the payload type of the job being submitted is a matrix
        // if it is a matrix, the datasetId of the JobDTO should not be empty

        if (jobDTO.getPayloadType().equals(JobPayloadType.CV_PAYLOADTYPE_MATRIX.getCvName())
                && (null == jobDTO.getDatasetIds())) {

            throw new GobiiDtoMappingException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "Missing dataset ID for job: " +
                            jobDTO.getJobName() + " with payload type matrix.");

        }


        Map<String, Object> parameters = ParamExtractor.makeParamVals(returnVal);
        Integer jobId = rsJobDao.createJobWithCvTerms(parameters);
        returnVal.setJobId(jobId);

        if (
                (returnVal.getType().equals(JobType.CV_JOBTYPE_LOAD.getCvName())
                        || returnVal.getType().equals(JobType.CV_JOBTYPE_EXTRACT.getCvName()))

                        && returnVal.getPayloadType().equals(JobPayloadType.CV_PAYLOADTYPE_MATRIX.getCvName())
                ) {


            if (jobDTO.getDatasetIds() == null || jobDTO.getDatasetIds().size() <= 0) {
                throw new GobiiDtoMappingException("Matrix load job does not have a dataset id: " + returnVal.getDatasetIds());
            }


            DataSetDTO dataSetDTO = dtoMapDataSet.get(jobDTO.getDatasetIds().get(0));

//            String[] datePattern = {"yyyy-MM-dd"};
//
//            Date parsedDate;
//
//            try {
//
//                parsedDate = DateUtils.parseDateStrictly(dataSetDTO.getCreatedDate().toString(), datePattern);
//
//            } catch (Exception e) {
//
//                throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
//                        GobiiValidationStatusType.NONE,
//                        "Something went wrong with setting the createdDate of the datasetDTO");
//            }

            dataSetDTO.setJobId(jobDTO.getJobId());
            dtoMapDataSet.updateDatasetForJobInfo(jobDTO,dataSetDTO);

        }

        return returnVal;

    }

    @Override
    public JobDTO replaceJob(String jobName, JobDTO jobDTO) throws GobiiDtoMappingException {

        JobDTO returnVal = jobDTO;

        Map<String, Object> parameters = ParamExtractor.makeParamVals(returnVal);
        parameters.put("jobName", jobName);
        rsJobDao.updateJobWithCvTerms(parameters);

        if( jobDTO.getDatasetIds() != null ) {

            for( Integer currentDatsetId : jobDTO.getDatasetIds() ) {

                DataSetDTO dataSetDTO = dtoMapDataSet.get(currentDatsetId);
                if( dataSetDTO.getDataSetId() != null && dataSetDTO.getDataSetId() > 0 ) {
                    this.dtoMapDataSet.updateDatasetForJobInfo(jobDTO, dataSetDTO);
                }
            }
        }

        return returnVal;

    }

    @SuppressWarnings("unchecked")
    @Override
    public JobDTO getJobDetailsByDatasetId(Integer datasetId) throws GobiiDtoMappingException {

        JobDTO returnVal = new JobDTO();

        try {

            Map<String, Object> jdbcParameters = new HashMap<>();
            jdbcParameters.put("datasetId", datasetId);
            List<JobDTO> jobs = (List<JobDTO>) dtoListQueryColl.getList(ListSqlId.QUERY_ID_JOB_BY_DATASET_ID, jdbcParameters);

            if(jobs.size() > 0 ) {
                returnVal = jobs.get(0);
            }

        } catch (Exception e) {
            throw new GobiiDtoMappingException(e);
        }
        return returnVal;


    }
}
