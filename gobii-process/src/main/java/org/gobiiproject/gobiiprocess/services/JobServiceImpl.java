package org.gobiiproject.gobiiprocess.services;

import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Job;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.GobiiDaoException;
import org.gobiiproject.gobiisampletrackingdao.JobDao;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class JobServiceImpl implements JobService {


    public Job update(Job job) throws GobiiDaoException {
        JobDao jobDao = SpringContextLoaderSingleton.getInstance().getBean(JobDao.class);
        CvDao cvDao = SpringContextLoaderSingleton.getInstance().getBean(CvDao.class);
        List<Cv> statuses = cvDao.getCvs(job.getStatus().getTerm(),
            CvGroupTerm.CVGROUP_JOBSTATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM);
        if(statuses.size() != 1) {
            throw new GobiiDaoException("Invalid Job Status Type");
        }
        Cv statusCv = statuses.get(0);
        job.setStatus(statusCv);
        return jobDao.update(job);
    }

    public Job getByName(String name) {
        JobDao jobDao = SpringContextLoaderSingleton.getInstance().getBean(JobDao.class);
        return jobDao.getByName(name);
    }


}
