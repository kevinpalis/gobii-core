package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;

import org.gobiiproject.gobiimodel.cvnames.JobType;
import org.gobiiproject.gobiimodel.entity.Job;

public interface JobDao {

    Job create(Job job) throws GobiiDaoException;
    Job update(Job job) throws GobiiDaoException;
    Job getById(Integer jobId);
    Job getByName(String jobName) throws GobiiDaoException;
	List<Job> getJobs(
        Integer page, Integer pageSizeToUse, 
        Integer contactId, String username,
        List<JobType> jobTypes
    );
}
