package org.gobiiproject.gobiidtomapping.entity.noaudit;

import java.text.ParseException;
import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;

/**
 * Created by VCalaminos on 9/4/2017.
 */
public interface DtoMapJob {

    List<JobDTO> getJobs() throws GobiiDtoMappingException;
    JobDTO getJobDetailsByJobName(String jobName) throws GobiiDtoMappingException;
    JobDTO createJob(JobDTO jobDTO) throws GobiiDtoMappingException, ParseException;
    JobDTO replaceJob(String jobName, JobDTO jobDTO) throws GobiiDtoMappingException;
    JobDTO getJobDetailsByDatasetId(Integer datasetId);

}
