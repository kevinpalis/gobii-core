package org.gobiiproject.gobiimodel.dto.instructions.loader;

import org.gobiiproject.gobiimodel.cvnames.JobPayloadType;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.dto.children.PropNameId;
import org.gobiiproject.gobiimodel.types.DatasetOrientationType;

public class GobiiLoaderMetadata {

	private PropNameId project = new PropNameId();
	private PropNameId platform = new PropNameId();
	private PropNameId experiment = new PropNameId();

	private DatasetOrientationType datasetOrientationType;
	private PropNameId dataset = new PropNameId();
	private PropNameId datasetType = new PropNameId();
	private PropNameId mapset = new PropNameId();

	private GobiiFile gobiiFile = new GobiiFile();

	private String gobiiCropType;

	private JobPayloadType jobPayloadType;
	private JobProgressStatusType gobiiJobStatus = JobProgressStatusType.CV_PROGRESSSTATUS_NOSTATUS;

	private Integer contactId;
	private String contactEmail;

	private boolean qcCheck;

	public PropNameId getProject() {
		return project;
	}

	public void setProject(PropNameId project) {
		this.project = project;
	}

	public PropNameId getPlatform() {
		return platform;
	}

	public void setPlatform(PropNameId platform) {
		this.platform = platform;
	}

	public PropNameId getExperiment() {
		return experiment;
	}

	public void setExperiment(PropNameId experiment) {
		this.experiment = experiment;
	}

	public DatasetOrientationType getDatasetOrientationType() {
		return datasetOrientationType;
	}

	public void setDatasetOrientationType(DatasetOrientationType datasetOrientationType) {
		this.datasetOrientationType = datasetOrientationType;
	}

	public PropNameId getDataset() {
		return dataset;
	}

	public void setDataset(PropNameId dataset) {
		this.dataset = dataset;
	}

	public PropNameId getDatasetType() {
		return datasetType;
	}

	public void setDatasetType(PropNameId datasetType) {
		this.datasetType = datasetType;
	}

	public PropNameId getMapset() {
		return mapset;
	}

	public void setMapset(PropNameId mapset) {
		this.mapset = mapset;
	}

	public GobiiFile getGobiiFile() {
		return gobiiFile;
	}

	public void setGobiiFile(GobiiFile gobiiFile) {
		this.gobiiFile = gobiiFile;
	}

	public String getGobiiCropType() {
		return gobiiCropType;
	}

	public void setGobiiCropType(String gobiiCropType) {
		this.gobiiCropType = gobiiCropType;
	}

	public JobPayloadType getJobPayloadType() {
		return jobPayloadType;
	}

	public void setJobPayloadType(JobPayloadType jobPayloadType) {
		this.jobPayloadType = jobPayloadType;
	}

	public JobProgressStatusType getGobiiJobStatus() {
		return gobiiJobStatus;
	}

	public void setGobiiJobStatus(JobProgressStatusType gobiiJobStatus) {
		this.gobiiJobStatus = gobiiJobStatus;
	}

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public boolean isQcCheck() {
		return qcCheck;
	}

	public void setQcCheck(boolean qcCheck) {
		this.qcCheck = qcCheck;
	}
}