package org.gobiiproject.gobiimodel.dto.auditable.sampletracking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.DnaSample;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by VCalaminos on 5/2/2019.
 */

@JsonIgnoreProperties(ignoreUnknown = true, value={
        "allowedProcessTypes", "entityNameType"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DnaSampleDTO extends DTOBaseAuditable{

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GobiiEntityMap(paramName = "dnaSampleId", entity = DnaSample.class)
    private int sampleId;

    @GobiiEntityMap(paramName = "projectId", entity = DnaSample.class)
    private Integer projectId;

    @GobiiEntityMap(paramName = "dnaSampleUuid", entity = DnaSample.class)
    private String sampleUuid;

    @GobiiEntityMap(paramName = "dnaSampleName", entity = DnaSample.class)
    private String sampleName;

    @GobiiEntityMap(paramName = "plateName", entity = DnaSample.class)
    private String plateName;

    @GobiiEntityMap(paramName = "dnaSampleNum", entity = DnaSample.class)
    private String sampleNum;

    @GobiiEntityMap(paramName = "wellRow", entity = DnaSample.class)
    private String wellRow;

    @GobiiEntityMap(paramName = "wellCol", entity = DnaSample.class)
    private String wellCol;

    private GermplasmDTO germplasm = new GermplasmDTO();

    private Map<String, Object> properties = new HashMap<>();


    public int getDnaSampleId() {
        return sampleId;
    }

    public void setDnaSampleId(int dnaSampleId) {
        this.sampleId = dnaSampleId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getSampleUuid() {
        return sampleUuid;
    }

    public void setSampleUuid(String sampleUuid) {
        this.sampleUuid = sampleUuid;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getPlateName() {
        return plateName;
    }

    public void setPlateName(String plateName) {
        this.plateName = plateName;
    }

    public String getSampleNum() {
        return sampleNum;
    }

    public void setSampleNum(String sampleNum) {
        this.sampleNum = sampleNum;
    }

    public String getWellRow() {
        return wellRow;
    }

    public void setWellRow(String wellRow) {
        this.wellRow = wellRow;
    }

    public String getWellCol() {
        return wellCol;
    }

    public void setWellCol(String wellCol) {
        this.wellCol = wellCol;
    }

    public GermplasmDTO getGermplasm() {
        return germplasm;
    }

    public void setGermplasm(GermplasmDTO germplasm) {
        this.germplasm = germplasm;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public DnaSampleDTO() { super(GobiiEntityNameType.DNASAMPLE); }

    @Override
    public Integer getId() { return this.sampleId; }

    @Override
    public void setId(Integer id) { this.sampleId = id; }



}
