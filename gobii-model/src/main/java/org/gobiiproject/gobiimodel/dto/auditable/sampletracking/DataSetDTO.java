package org.gobiiproject.gobiimodel.dto.auditable.sampletracking;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true, value={
        "id", "allowedProcessTypes", "entityNameType"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSetDTO extends DTOBase {

    
    private Integer datasetId;
    private String datasetName;
    private String datatypeName;
    private Integer experimentId;
    private String callingAnalysisId;
    private Integer createdBy = null;
    private Date createdDate = null;
    private Integer modifiedBy = null;
    private Date modifiedDate = null;
    private List<Integer> analysisIds = new ArrayList<>();

    @Override
    public Integer getId() {
        return this.datasetId;
    }

    @Override
    public void setId(Integer id) {
        this.datasetId = id;
    }

    public Integer getDatasetId() {
        return this.datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    @GobiiEntityParam(paramName = "createdBy")
    public Integer getCreatedBy() {
        return createdBy;
    }

    @GobiiEntityColumn(columnName = "created_by")
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    @GobiiEntityParam(paramName = "createdDate")
    public Date getCreatedDate() {
        return createdDate;
    }

    @GobiiEntityColumn(columnName = "created_date")
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @GobiiEntityParam(paramName = "modifiedBy")
    public Integer getModifiedBy() {
        return modifiedBy;
    }

    @GobiiEntityColumn(columnName = "modified_by")
    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @GobiiEntityParam(paramName = "modifiedDate")
    public Date getModifiedDate() {
        return modifiedDate;
    }

    @GobiiEntityColumn(columnName = "modified_date")
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @GobiiEntityParam(paramName = "datasetName")
    public String getDatasetName() {
        return datasetName;
    }

    @GobiiEntityColumn(columnName = "datasetname")
    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    @GobiiEntityParam(paramName = "experimentId")
    public Integer getExperimentId() {
        return experimentId;
    }

    @GobiiEntityColumn(columnName = "experiment_id")
    public void setExperimentId(Integer experimentId) {
        this.experimentId = experimentId;
    }

    @GobiiEntityParam(paramName = "callingAnalysisId")
    public String getCallingAnalysisName() {
        return callingAnalysisId;
    }

    @GobiiEntityColumn(columnName = "callinganalysisname")
    public void setCallingAnalysisName(String callingAnalysisName) {
        this.callingAnalysisId = callingAnalysisName;
    }

    @GobiiEntityParam(paramName = "datasetAnalysIds")
    public List<Integer> getAnalysisIds() {
        return analysisIds;
    }

    @GobiiEntityColumn(columnName = "analyses")
    public void setAnalysesIds(List<Integer> analysisIds) {
        this.analysisIds = analysisIds;
    }

    @GobiiEntityParam(paramName = "datatypeName")
    public String getDatatypeName() {
        return datatypeName;
    }

    @GobiiEntityColumn(columnName = "datatypename")
    public void setDatatypeName(String datatypeName) {
        this.datatypeName = datatypeName;
    }

}
