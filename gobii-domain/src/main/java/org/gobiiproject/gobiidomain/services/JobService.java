package org.gobiiproject.gobiidomain.services;

import java.text.ParseException;
import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.DnaSampleDTO;

/**
 * Created by VCalaminos on 8/30/2017.
 */
public interface JobService {

    JobDTO createJob(JobDTO jobDTO) throws GobiiDomainException, ParseException;
    JobDTO replaceJob(String jobName, JobDTO jobDTO) throws GobiiDomainException;
    List<JobDTO> getJobs() throws GobiiDomainException;
    JobDTO getJobByJobName(String jobName) throws GobiiDomainException;
    JobDTO submitDnaSamplesByJobName(String jobName, List<DnaSampleDTO> dnaSampleDTOList) throws GobiiDomainException;

}
