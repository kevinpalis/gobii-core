package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.*;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.utils.customserializers.UtcDateSerializer;

import java.util.*;

/**
 * Model for the Brapi Samples endpoint
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value={
        "id", "allowedProcessTypes", "entityNameType",
        "createdDate", "modifiedDate", "createdBy", "modifiedBy"
})
public class VariantSetDTO extends DTOBaseAuditable {

    public VariantSetDTO() {
        super(GobiiEntityNameType.DATASET);
    }

    @GobiiEntityMap(paramName="datasetId", entity = Dataset.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer variantSetDbId;

    @GobiiEntityMap(paramName="datasetName", entity = Dataset.class)
    private String variantSetName;

    @GobiiEntityMap(paramName="experiment.experimentId",
            entity = Dataset.class, deep=true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer studyDbId;

    @GobiiEntityMap(paramName="experiment.experimentName",
            entity = Dataset.class, deep=true)
    private String studyName;

    @GobiiEntityMap(paramName="callingAnalysis.reference.referenceId",
            entity = Dataset.class, deep=true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer referenceSetDbId;

    @GobiiEntityMap(paramName="type.term", entity = Dataset.class, deep=true)
    private String variantSetType;

    private List<FileFormatDTO> availableFormats;

    private Set<AnalysisDTO> analyses = new HashSet<>();

    @GobiiEntityMap(
        paramName="datasetStats.dnarunCount",
        entity = Dataset.class,
        deep=true)
    private Integer callSetCount = 0;

    @GobiiEntityMap(
        paramName="datasetStats.markerCount",
        entity = Dataset.class,
        deep=true)
    private Integer variantCount = 0;

    private Map<String, Object> additionalInfo;

    public Integer getId() {
        return 0;
    }

    public void setId(Integer i) {
    }

    @JsonSerialize(using=UtcDateSerializer.class)
    public Date getUpdated() {
        return this.getModifiedDate();
    }

    @JsonSerialize(using= UtcDateSerializer.class)
    public Date getCreated() {
        return this.getCreatedDate();
    }

    public Integer getVariantSetDbId() {
        return variantSetDbId;
    }

    public void setVariantSetDbId(Integer variantSetDbId) {
        this.variantSetDbId = variantSetDbId;
    }

    public String getVariantSetName() {
        return variantSetName;
    }

    public void setVariantSetName(String variantSetName) {
        this.variantSetName = variantSetName;
    }

    public Integer getStudyDbId() {
        return studyDbId;
    }

    public void setStudyDbId(Integer studyDbId) {
        this.studyDbId = studyDbId;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public Integer getReferenceSetDbId() {
        return referenceSetDbId;
    }

    public void setReferenceSetDbId(Integer referenceSetDbId) {
        this.referenceSetDbId = referenceSetDbId;
    }

    public List<FileFormatDTO> getAvailableFormats() {
        return availableFormats;
    }

    public void setAvailableFormats(List<FileFormatDTO> availableFormats) {
        this.availableFormats = availableFormats;
    }

    public Set<AnalysisDTO> getAnalyses() {
        return analyses;
    }

    public void setAnalyses(Set<AnalysisDTO> analyses) {
        this.analyses = analyses;
    }

    public Integer getCallSetCount() {
        return callSetCount;
    }

    public void setCallSetCount(Integer callSetCount) {
        this.callSetCount = callSetCount;
    }

    public Integer getVariantCount() {
        return variantCount;
    }

    public void setVariantCount(Integer variantCount) {
        this.variantCount = variantCount;
    }

    public String getVariantSetType() {
        return variantSetType;
    }

    public void setVariantSetType(String variantSetType) {
        this.variantSetType = variantSetType;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
