package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.DnaSample;

import java.util.Map;

/**
 * Model for the Brapi Samples endpoint
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SamplesDTO {

    @GobiiEntityMap(paramName="germplasm.germplasmId", entity = DnaSample.class, deep=true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer germplasmDbId;

    @GobiiEntityMap(paramName="germplasm.externalCode", entity = DnaSample.class, deep=true)
    private String germplasmPUI;

    /** Sample Type is DNA for all GDM data **/
    private String sampleType = "DNA";

    private String tissueType;

    @GobiiEntityMap(paramName="dnaSampleId", entity = DnaSample.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer sampleDbId;

    @GobiiEntityMap(paramName="dnaSampleName", entity = DnaSample.class)
    private String sampleName;

    @GobiiEntityMap(paramName="dnaSampleUuid", entity = DnaSample.class)
    private String samplePUI;

    @GobiiEntityMap(paramName="plateName", entity = DnaSample.class)
    private String plateName;

    @GobiiEntityMap(paramName="wellRow", entity = DnaSample.class)
    private String row;

    @GobiiEntityMap(paramName="wellCol", entity = DnaSample.class)
    private String column;

    @GobiiEntityMap(paramName="dnaSampleNum", entity = DnaSample.class)
    private String well;

    @GobiiEntityMap(paramName="project.projectId", entity = DnaSample.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer sampleGroupDbId;

    @GobiiEntityMap(paramName="project.projectName", entity = DnaSample.class, deep = true)
    private String sampleGroupName;


    private Map<String, String> additionalInfo;

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public String getTissueType() {
        return tissueType;
    }

    public void setTissueType(String tissueType) {
        this.tissueType = tissueType;
    }


    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSamplePUI() {
        return samplePUI;
    }

    public void setSamplePUI(String samplePUI) {
        this.samplePUI = samplePUI;
    }

    public String getPlateName() {
        return plateName;
    }

    public void setPlateName(String plateName) {
        this.plateName = plateName;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
    }


    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }



    public Integer getGermplasmDbId() {
        return germplasmDbId;
    }

    public void setGermplasmDbId(Integer germplasmDbId) {
        this.germplasmDbId = germplasmDbId;
    }


    public Integer getSampleDbId() {
        return sampleDbId;
    }

    public void setSampleDbId(Integer sampleDbId) {
        this.sampleDbId = sampleDbId;
    }

    public Integer getSampleGroupDbId() {
        return sampleGroupDbId;
    }

    public void setSampleGroupDbId(Integer sampleGroupDbId) {
        this.sampleGroupDbId = sampleGroupDbId;
    }

    public String getSampleGroupName() {
        return sampleGroupName;
    }

    public void setSampleGroupName(String sampleGroupName) {
        this.sampleGroupName = sampleGroupName;
    }

    public String getGermplasmPUI() {
        return germplasmPUI;
    }

    public void setGermplasmPUI(String germplasmPUI) {
        this.germplasmPUI = germplasmPUI;
    }
}
