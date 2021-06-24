package org.gobiiproject.gobiiprocess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Job;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiiprocess.services.JobService;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;
import org.gobiiproject.gobiisampletrackingdao.GobiiDaoException;

import static org.gobiiproject.gobiimodel.utils.error.Logger.logError;


public class JobStatus {

    Job job;

    JobService jobService;

	private static Set<String> acceptedStatuses=new HashSet<>(Arrays.asList(
			JobProgressStatusType.CV_PROGRESSSTATUS_ABORTED.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_COMPLETED.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_DIGEST.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_INPROGRESS.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_MATRIXLOAD.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_METADATALOAD.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_TRANSFORMATION.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_VALIDATION.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_FAILED.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_METADATAEXTRACT.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_FINALASSEMBLY.getCvName(),
			JobProgressStatusType.CV_PROGRESSSTATUS_QCPROCESSING.getCvName()
	));

    public JobStatus(String jobName) throws GobiiDaoException {
        this.jobService = SpringContextLoaderSingleton
            .getInstance()
            .getBean(JobService.class);
        this.job = jobService.getByName(jobName);
    }

    public void set(String status, String message) {
    	if(status==null || !acceptedStatuses.contains(status)) {
    		Logger.logError(
    		    "JobStatus","Invalid status passed to set: "+status+"\nMessage: "+message,
                new Exception());
    		return;
		}
    	try{
    	    this.job.setMessage(message);
    	    Cv newStatus = new Cv();
    	    newStatus.setTerm(status);
    	    job.setStatus(newStatus);
    	    this.jobService.update(job);
    	} catch (Exception e) {
    	    logError("Digester", "Exception while referencing Job table", e);
    	    return;
    	}
	}

	/**
     * Sets the status of the job underway. Adds a message based on the error automatically.
     *
     */
	public void setError(String message){
        String errorMessage="";
        if(this.job!=null){
            this.job = jobService.getByName(this.job.getJobName());
            errorMessage="Status: " + this.job.getStatus().getTerm()+" - "
                + this.job.getMessage() + " | \n";
        }
        errorMessage += message + " : " + Logger.getFirstErrorReason();
        set(JobProgressStatusType.CV_PROGRESSSTATUS_FAILED.getCvName(),errorMessage);
    }
}