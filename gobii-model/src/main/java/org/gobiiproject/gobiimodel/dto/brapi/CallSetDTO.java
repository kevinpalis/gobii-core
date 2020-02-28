package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.DnaRun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by VCalaminos on 6/25/2019.
 * Modified By VishnuG
 */

@JsonIgnoreProperties(ignoreUnknown = true, value={
        "id", "allowedProcessTypes", "entityNameType",
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallSetBrapiDTO extends DTOBase {

    @GobiiEntityMap(paramName = "dnaRunId", entity = DnaRun.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer callSetDbId;

    @GobiiEntityMap(paramName = "dnaRunName", entity = DnaRun.class)
    private String callSetName;

    @GobiiEntityMap(paramName = "experiment.experimentId", entity = DnaRun.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer studyDbId;

    @GobiiEntityMap(paramName = "dnaSample.dnaSampleId", entity = DnaRun.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer sampleDbId;

    @GobiiEntityMap(paramName = "dnaSample.dnaSampleName", entity = DnaRun.class, deep = true)
    private String sampleName;

    private List<Integer> variantSetIds = new ArrayList<>();

    @GobiiEntityMap(paramName = "dnaSample.germplasm.germplasmId", entity = DnaRun.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer germplasmDbId;

    @GobiiEntityMap(paramName = "dnaSample.germplasm.germplasmName", entity = DnaRun.class, deep = true)
    private String germplasmName;

    private Map<String, String> additionalInfo = new HashMap<>();

    @Override
    public Integer getId() {
        return this.callSetDbId;
    }

    @Override
    public void setId(Integer id) {
        this.callSetDbId = id;
    }

    public Integer getCallSetDbId() {
        return callSetDbId;
    }

    public void setCallSetDbId(Integer callSetDbId) {
        this.callSetDbId = callSetDbId;
    }

    public Integer getStudyDbId() {
        return studyDbId;
    }

    public void setStudyDbId(Integer studyDbId) {
        this.studyDbId = studyDbId;
    }

    public Integer getSampleDbId() {
        return sampleDbId;
    }

    public void setSampleDbId(Integer sampleDbId) {
        this.sampleDbId = sampleDbId;
    }

    public String getCallSetName() {
        return callSetName;
    }

    public void setCallSetName(String callSetName) {
        this.callSetName = callSetName;
    }

    public List<Integer> getVariantSetIds() {
        return variantSetIds;
    }

    public void setVariantSetIds(List<Integer> variantSetIds) {
        this.variantSetIds = variantSetIds;
    }

    public Integer getGermplasmDbId() {
        return germplasmDbId;
    }

    public void setGermplasmDbId(Integer germplasmDbId) {
        this.germplasmDbId = germplasmDbId;
    }

    public String getGermplasmName() {
        return germplasmName;
    }

    public void setGermplasmName(String germplasmName) {
        this.germplasmName = germplasmName;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
