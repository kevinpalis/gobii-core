package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.utils.customserializers.UtcDateSerializer;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true, value={
        "id", "allowedProcessTypes", "entityNameType", "createdBy",
        "modifiedBy", "modifiedDate", "createdDate"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalysisDTO extends DTOBaseAuditable {


    @GobiiEntityMap(paramName="analysisId", entity = Analysis.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer analysisDbId;

    @GobiiEntityMap(paramName="analysisName", entity = Analysis.class)
    private String analysisName;

    @GobiiEntityMap(paramName="type.term", entity = Analysis.class, deep = true)
    private String type;

    @GobiiEntityMap(paramName="description", entity = Analysis.class)
    private String description;

    @GobiiEntityMap(paramName="software", entity = Analysis.class)
    private String software;

    public AnalysisDTO() {
        super(GobiiEntityNameType.ANALYSIS);
    }

    @Override
    public Integer getId() { return this.analysisDbId; }

    @Override
    public void setId(Integer analysisDbId) { this.analysisDbId = analysisDbId; }

    public Integer getAnalysisDbId() { return this.analysisDbId; }

    public void setAnalysisDbId(Integer analysisDbId) { this.analysisDbId = analysisDbId; }

    public String getAnalysisName() { return this.analysisName; }

    public void setAnalysisName(String analysisName) { this.analysisName = analysisName; }

    public String getType() { return this.type; }

    public void setType(String type) { this.type = type; }

    public String getDescription() { return this.description; }

    public void setDescription(String description) { this.description = description; }

    public String getSoftware() { return this.software; }

    public void setSoftware(String software) { this.software = software; }

    @JsonSerialize(using= UtcDateSerializer.class)
    public Date getCreated() {
        return this.getCreatedDate();
    }

}
