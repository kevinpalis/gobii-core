package org.gobiiproject.gobiimodel.headerlesscontainer;
// Generated Mar 31, 2016 1:44:38 PM by Hibernate Tools 3.2.2.GA


import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;

public class DnaSampleDTO extends DTOBase {


    Integer dnaRunId;
    Integer dnaSampleId;
    String externalCode;
    Integer germplasmId;
    String germplasmName;
    String analysisMethodName;
    Integer markerCount;
    String dnaSampleName;
    String germplasmExternalCode;
    Integer projectId;
    Integer dnaSampleNum;
    String dnarunName;
    Integer experimentId;

    @Override
    public Integer getId() {
        return this.dnaSampleId;
    }

    @Override
    public void setId(Integer id) {
        this.dnaSampleId = id;
    }


    @GobiiEntityParam(paramName = "dnaRunId")
    public Integer getDnaRunId() {
        return dnaRunId;
    }

    @GobiiEntityColumn(columnName = "dnarun_id")
    public void setDnaRunId(Integer dnaRunId) {
        this.dnaRunId = dnaRunId;
    }

    @GobiiEntityParam(paramName = "dnaSampleId")
    public Integer getDnaSampleId() {
        return dnaSampleId;
    }

    @GobiiEntityColumn(columnName = "dnasample_id")
    public void setDnaSampleId(Integer dnaSampleId) {
        this.dnaSampleId = dnaSampleId;
    }

    @GobiiEntityParam(paramName = "externalCode")
    public String getExternalCode() {
        return externalCode;
    }

    @GobiiEntityColumn(columnName = "external_code")
    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    @GobiiEntityParam(paramName = "germplasmId")
    public Integer getGermplasmId() {
        return germplasmId;
    }

    @GobiiEntityColumn(columnName = "germplasm_id")
    public void setGermplasmId(Integer germplasmId) {
        this.germplasmId = germplasmId;
    }

    @GobiiEntityParam(paramName = "germplasmName")
    public String getGermplasmName() {
        return germplasmName;
    }

    @GobiiEntityColumn(columnName = "germplasmname")
    public void setGermplasmName(String germplasmName) {
        this.germplasmName = germplasmName;
    }

    @GobiiEntityParam(paramName = "analysisMethodName")
    public String getAnalysisMethodName() {
        return analysisMethodName;
    }

    @GobiiEntityColumn(columnName = "analysismethodname")
    public void setAnalysisMethodName(String analysisMethodName) {
        this.analysisMethodName = analysisMethodName;
    }

    @GobiiEntityParam(paramName = "markerCount")
    public Integer getMarkerCount() {
        return markerCount;
    }

    @GobiiEntityColumn(columnName = "markercount")
    public void setMarkerCount(Integer markerCount) {
        this.markerCount = markerCount;
    }

    @GobiiEntityParam(paramName = "dnaSampleName")
    public String getDnaSampleName() { return dnaSampleName;}

    @GobiiEntityColumn(columnName = "name")
    public void setDnaSampleName(String dnaSampleName) { this.dnaSampleName = dnaSampleName; }

    @GobiiEntityParam(paramName = "germplasmExternalCode")
    public String getGermplasmExternalCode() { return germplasmExternalCode; }

    @GobiiEntityColumn(columnName = "code")
    public void setGermplasmExternalCode(String germplasmExternalCode) { this.germplasmExternalCode = germplasmExternalCode; }

    @GobiiEntityParam(paramName = "projectId")
    public Integer getProjectId() { return projectId; }

    @GobiiEntityColumn(columnName = "project_id")
    public void setProjectId(Integer projectId) { this.projectId = projectId; }

    @GobiiEntityParam(paramName = "dnaSampleNum")
    public Integer getDnaSampleNum() { return dnaSampleNum; }

    @GobiiEntityColumn(columnName = "num")
    public void setDnaSampleNum(Integer dnaSampleNum) { this.dnaSampleNum = dnaSampleNum; }

    @GobiiEntityParam(paramName = "dnarunName")
    public String getDnarunName() { return dnarunName; }

    @GobiiEntityColumn(columnName = "dnarunname")
    public void setDnarunName(String dnarunName) { this.dnarunName = dnarunName; }

    @GobiiEntityParam(paramName = "experimentId")
    public Integer getExperimentId() { return experimentId; }

    @GobiiEntityColumn(columnName = "experiment_id")
    public void setExperimentId(Integer experimentId) { this.experimentId = experimentId; }
}


