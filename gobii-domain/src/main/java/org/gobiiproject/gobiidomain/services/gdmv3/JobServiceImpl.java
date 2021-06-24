package org.gobiiproject.gobiidomain.services.gdmv3;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.cvnames.JobType;
import org.gobiiproject.gobiimodel.dto.gdmv3.ContactDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Job;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.*;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.JobDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j;

import java.util.Date;
import java.util.List;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

@Transactional
@Log4j
public class JobServiceImpl implements JobService {
    public static final String EXTRACT = "extract";
    public static final String LOAD = "load";

    public static final String COMPLETED = "completed";
    public static final String FAILED = "failed";

    public static final String IN_PROGRESS = "in_progress";
    public static final String PENDING = "pending";

    private String jobFilesDownloadUrlFormat = "/jobs/{0}/files/{1}.zip";

    @Autowired
    private JobDao jobDao;

    @Autowired
    private CvDao cvDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private ConfigSettings configSettings;

    @Override
    public JobDTO getJobById(Integer jobId, String cropType) throws GobiiException {
        Job job = jobDao.getById(jobId);
        return mapJobDTO(job, cropType);
    }

    private JobDTO mapJobDTO(Job job, String cropType) throws GobiiException {
        JobDTO jobDTO = new JobDTO();
        ModelMapper.mapEntityToDto(job, jobDTO);

        // Set Submitted by entity
        ContactDTO submittedBy = new ContactDTO();
        jobDTO.setSubmittedBy(submittedBy);
        ModelMapper.mapEntityToDto(job.getSubmittedBy(), submittedBy);
     
        if(getJobStatusDirectory(cropType, job) != null) { 
            String jobFilesDownloadUrl = 
                MessageFormat.format(jobFilesDownloadUrlFormat, job.getJobId(), job.getJobName()); 
            jobDTO.setJobFilesDownloadUrl(jobFilesDownloadUrl);
        }
        
        return jobDTO;
    }

    public JobDTO createLoaderJob(JobDTO jobDTO) throws GobiiException {

        Job job = new Job();

        if (jobDTO.getPayload().isEmpty()) {
            throw new GobiiDomainException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.BAD_REQUEST,
                    "Required payload type");
        }

        // Set a unique job name if not set
        if(StringUtils.isEmpty(jobDTO.getJobName())) {
            String jobName = Utils.getUniqueName();
            job.setJobName(jobName);
        }

        job.setMessage(jobDTO.getJobMessage());

        // Set current user as job submitter
        Contact submittedBy = contactDao.getContactByUsername(ContactService.getCurrentUserName());
        job.setSubmittedBy(submittedBy);

        // Get payload type
        Cv payloadType = cvDao.getCvs(jobDTO.getPayload(), CvGroupTerm.CVGROUP_PAYLOADTYPE.getCvGroupName(),
                GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);

        job.setPayloadType(payloadType);

        // Get jobstatus as pending
        Cv jobStatus = cvDao.getCvs(GobiiJobStatus.PENDING.getCvTerm(), CvGroupTerm.CVGROUP_JOBSTATUS.getCvGroupName(),
                GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);
        job.setStatus(jobStatus);

        job.setSubmittedDate(new Date());
        // Get load type
        Cv jobType = cvDao.getCvs(JobType.CV_JOBTYPE_LOAD.getCvName(), CvGroupTerm.CVGROUP_JOBTYPE.getCvGroupName(),
                GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);

        job.setType(jobType);
        jobDao.create(job);
        ModelMapper.mapEntityToDto(job, jobDTO);

        return jobDTO;

    }


    @Override
    public File getJobStatusDirectory(String cropType, Integer jobId) throws GobiiException {
        Job job = jobDao.getById(jobId);
        return getJobStatusDirectory(cropType, job);
    }
    
    private File getJobStatusDirectory(String cropType, Job job) throws GobiiException {
        if (job == null) {
            return null;
        }

        JobProgressStatusType status =  
            JobProgressStatusType.byValue(job.getStatus().getTerm());
        
        if(status == null) {
            return null;
        }

        String type = job.getType().getTerm();

        GobiiFileProcessDir dir = null;
        if (type.equals(EXTRACT)) {
            switch (status) {
                case CV_PROGRESSSTATUS_COMPLETED: 
                    dir = GobiiFileProcessDir.EXTRACTOR_OUTPUT; break;
                default:
                    dir = null; break; 
            }
        } else if (type.equals(LOAD)) {
            switch(status) {
                case CV_PROGRESSSTATUS_COMPLETED: 
                case CV_PROGRESSSTATUS_FAILED: 
                    dir = GobiiFileProcessDir.LOADER_INTERMEDIATE_FILES; break;
                default: 
                    dir = null; break; 
            }
        } else {
            throw new GobiiException(GobiiStatusLevel.ERROR, 
                                     GobiiValidationStatusType.BAD_REQUEST, 
                                     "Job type not supported");
        }

        if (dir == null) return null;

        try {
            String path = configSettings.getProcessingPath(cropType, dir);
            log.debug("Path for job is " + path + "/" + job.getJobName());
            File jobFileDirectory  = new File(path, job.getJobName());
            if(jobFileDirectory.isDirectory()) {
                return jobFileDirectory;
            }
        }
        catch(Exception e) {
            throw new GobiiException(GobiiStatusLevel.ERROR, 
                                     GobiiValidationStatusType.UNKNOWN, 
                                     "Error in GDM file system configuration. " +
                                     "Contact administrator");
        }

        return null;
    }

    @Override
    public PagedResult<JobDTO> getJobs(Integer page, Integer pageSizeToUse, 
                                       String username, boolean getLoadJobs,
                                       boolean getExtractJobs, String cropType) {

            List<JobType> jobTypes = new ArrayList<>();
            
            if(getLoadJobs) {
                jobTypes.add(JobType.CV_JOBTYPE_LOAD);
            }

            if(getExtractJobs) {
                jobTypes.add(JobType.CV_JOBTYPE_EXTRACT);
            }

            
            List<Job> jobs = jobDao.getJobs(
                page, pageSizeToUse, 
                null, username,
                jobTypes);

            List<JobDTO> jobDTOs = new ArrayList<>();

            jobs.forEach(job -> {
                jobDTOs.add(mapJobDTO(job, cropType));
            });
            return PagedResult.createFrom(page, jobDTOs);
    }

}
