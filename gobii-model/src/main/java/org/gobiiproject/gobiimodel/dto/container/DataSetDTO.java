package org.gobiiproject.gobiimodel.dto.container;

import org.gobiiproject.gobiimodel.dto.DtoMetaData;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Phil on 4/21/2016.
 */
public class DataSetDTO extends DtoMetaData {

    private Integer datasetId;
    private Integer experimentId;
    private Integer callingAnalysisId;
    private AnalysisDTO callingAnalysis;
    private String dataTable;
    private String dataFile;
    private String qualityTable;
    private String qualityFile;
    private String createdBy;
    private Date createdDate;
    private String modifiedBy;
    private Date modifiedDate;
    private Integer status;
    private List<Integer> analysesIds = new ArrayList<>();

    private List<AnalysisDTO> analyses = new ArrayList<>();
    private List<Integer> scores = new ArrayList<>();

    @GobiiEntityParam(paramName = "datasetId")
    public Integer getDatasetId() {
        return datasetId;
    }

    @GobiiEntityColumn(columnName = "dataset_id")
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    @GobiiEntityParam(paramName = "experimentId")
    public Integer getExperimentId() {
        return experimentId;
    }

    @GobiiEntityColumn(columnName = "experiment_id")
    public void setExperimentId(Integer experimentId) {
        this.experimentId = experimentId;
    }

    public AnalysisDTO getCallingAnalysis() {
        return callingAnalysis;
    }
    public void setCallingAnalysis(AnalysisDTO callingAnalysis) {
        this.callingAnalysis = callingAnalysis;
    }

    @GobiiEntityParam(paramName = "callinganalysis_id")
    public Integer getCallingAnalysisId() {
        return callingAnalysisId;
    }

    @GobiiEntityColumn(columnName = "callinganalysis_id")
    public void setCallingAnalysisId(Integer callingAnalysisId) {
        this.callingAnalysisId = callingAnalysisId;
    }


    @GobiiEntityParam(paramName = "dataTable")
    public String getDataTable() {
        return dataTable;
    }

    @GobiiEntityColumn(columnName = "data_table")
    public void setDataTable(String dataTable) {
        this.dataTable = dataTable;
    }

    @GobiiEntityParam(paramName = "dataFile")
    public String getDataFile() {
        return dataFile;
    }

    @GobiiEntityColumn(columnName = "data_file")
    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    @GobiiEntityParam(paramName = "qualityTable")
    public String getQualityTable() {
        return qualityTable;
    }

    @GobiiEntityColumn(columnName = "quality_table")
    public void setQualityTable(String qualityTable) {
        this.qualityTable = qualityTable;
    }

    @GobiiEntityParam(paramName = "qualityFile")
    public String getQualityFile() {
        return qualityFile;
    }

    @GobiiEntityColumn(columnName = "quality_file")
    public void setQualityFile(String qualityFile) {
        this.qualityFile = qualityFile;
    }

    @GobiiEntityParam(paramName = "createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @GobiiEntityColumn(columnName = "created_by")
    public void setCreatedBy(String createdBy) {
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
    public String getModifiedBy() {
        return modifiedBy;
    }

    @GobiiEntityColumn(columnName = "modified_by")
    public void setModifiedBy(String modifiedBy) {
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

    @GobiiEntityParam(paramName = "status")
    public Integer getStatus() {
        return status;
    }

    @GobiiEntityColumn(columnName = "status")
    public void setStatus(Integer status) {
        this.status = status;
    }


    @GobiiEntityParam(paramName = "datasetanalyses")
    public List<Integer> getAnalysesIds() {
        return analysesIds;
    }

    @GobiiEntityColumn(columnName = "analyses")
    public void setAnalysesIds(List<Integer> analysesIds) {
        this.analysesIds = analysesIds;
    }



    public List<AnalysisDTO> getAnalyses() {
        return analyses;
    }

    public void setAnalyses(List<AnalysisDTO> analyses) {
        this.analyses = analyses;
    }

    public List<Integer> getScores() {
        return scores;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }
}