package org.gobiiproject.gobiimodel.dto.instructions.loader;

import org.gobiiproject.gobiimodel.config.GobiiCropConfig;

import java.io.File;
import java.util.List;
import java.util.Map;

public class DigesterResult {

    private boolean success;

    private boolean sendQc;

    private String cropType;

    private GobiiCropConfig cropConfig;

    private String intermediateFilePath;

    private String loadType;

    private Map<String, File> loaderInstructionsMap;

    private List<String> loaderInstructionsList;

    private String datasetType;

    private Object jobStatusObject;

    private Integer datasetId;

    private String jobName;

    private String contactEmail;

    public DigesterResult(Builder builder) {
        this.success = builder.success;
        this.sendQc = builder.sendQc;
        this.cropType = builder.cropType;
        this.cropConfig = builder.cropConfig;
        this.intermediateFilePath = builder.intermediateFilePath;
        this.loadType = builder.loadType;
        this.loaderInstructionsMap = builder.loaderInstructionsMap;
        this.loaderInstructionsList = builder.loaderInstructionsList;
        this.datasetType = builder.datasetType;
        this.jobStatusObject = builder.jobStatusObject;
        this.datasetId = builder.datasetId;
        this.jobName = builder.jobName;
        this.contactEmail = builder.contactEmail;
    }

    public static class Builder {
        private boolean success;

        private boolean sendQc;

        private String cropType;

        private GobiiCropConfig cropConfig;

        private String intermediateFilePath;

        private String loadType;

        private Map<String, File> loaderInstructionsMap;

        private List<String> loaderInstructionsList;

        private String datasetType;

        private Object jobStatusObject;

        private Integer datasetId;

        private String jobName;

        private String contactEmail;

        public Builder setSuccess(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder setSendQc(boolean sendQc) {
            this.sendQc = sendQc;
            return this;
        }

        public Builder setIntermediateFilePath(String intermediateFilePath) {
            this.intermediateFilePath = intermediateFilePath;
            return this;
        }

        public Builder setCropType(String cropType) {
            this.cropType = cropType;
            return this;
        }

        public Builder setCropConfig(GobiiCropConfig cropConfig) {
            this.cropConfig = cropConfig;
            return this;
        }

        public Builder setLoadType(String loadType) {
            this.loadType = loadType;
            return this;
        }

        public Builder setLoaderInstructionsMap(Map<String, File> loaderInstructionsMap) {
            this.loaderInstructionsMap = loaderInstructionsMap;
            return this;
        }

        public Builder setLoaderInstructionsList(List<String> loaderInstructionsList) {
            this.loaderInstructionsList = loaderInstructionsList;
            return this;
        }
        
        public Builder setDatasetType(String datasetType) {
            this.datasetType = datasetType;
            return this;
        }

        public Builder setJobStatusObject(Object jobStatusObject) {
            this.jobStatusObject = jobStatusObject;
            return this;
        }
        
        public Builder setDatasetId(Integer datasetId) {
            this.datasetId = datasetId;
            return this;
        }

        public Builder setJobName(String jobName) {
            this.jobName = jobName;
            return this;
        }
        
        public Builder setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
            return this;
        }

        public DigesterResult build() {
            return new DigesterResult(this);
        }

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSendQc() {
        return sendQc;
    }

    public void setSendQc(boolean sendQc) {
        this.sendQc = sendQc;
    }

    public String getIntermediateFilePath() {
        return intermediateFilePath;
    }

    public void setIntermediateFilePath(String intermediateFilePath) {
        this.intermediateFilePath = intermediateFilePath;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public GobiiCropConfig getCropConfig() {
        return cropConfig;
    }

    public void setCropConfig(GobiiCropConfig cropConfig) {
        this.cropConfig = cropConfig;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public Map<String, File> getLoaderInstructionsMap() {
        return loaderInstructionsMap;
    }

    public void setLoaderInstructionsMap(Map<String, File> loaderInstructionsMap) {
        this.loaderInstructionsMap = loaderInstructionsMap;
    }

    public List<String> getLoaderInstructionsList() {
        return loaderInstructionsList;
    }

    public void setLoaderInstructionsList(List<String> loaderInstructionsList) {
        this.loaderInstructionsList = loaderInstructionsList;
    }

    public boolean isJobStatusSet() {
        if(jobStatusObject != null) {
            return true;
        }
        return false;
    }


    public String getDatasetType() {
        return datasetType;
    }

    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
    }

    public Object getJobStatusObject() {
        return jobStatusObject;
    }

    public void setJobStatusObject(Object jobStatusObject) {
        this.jobStatusObject = jobStatusObject;
    }

    public boolean hasGenotypeMatrix() {
        if(datasetId  != null) {
            return true;
        }
        return false;
    }

    public Integer getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
