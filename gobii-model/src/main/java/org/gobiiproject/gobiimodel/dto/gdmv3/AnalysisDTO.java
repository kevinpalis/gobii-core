/**
 * AnalysisDTO.java
 * 
 * DTO class for analysis.
 * @author Rodolfo N. Duldulao, Jr.
 */
package org.gobiiproject.gobiimodel.dto.gdmv3;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIgnoreProperties(ignoreUnknown = false, value={
    "id", "allowedProcessTypes", "entityNameType", "status"
})
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
@EqualsAndHashCode(callSuper=false)
public class 
AnalysisDTO extends DTOBaseAuditable {

    //validation groups
    public static interface Create {}
    public static interface Update {}

    public AnalysisDTO() {
        super(GobiiEntityNameType.ANALYSIS);
    }

    @Override
    public Integer getId() {
        return analysisId;
    }

    @Override
    public void setId(Integer id) {
        this.analysisId = id;

    }

    @GobiiEntityMap(paramName = "analysisId", entity = Analysis.class, ignoreOnDtoToEntity = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer analysisId;
    

    @GobiiEntityMap(paramName = "analysisName", entity = Analysis.class)
    @NotBlank(groups = AnalysisDTO.Create.class )
    private String analysisName;

    @GobiiEntityMap(paramName = "type.cvId", entity = Analysis.class, deep =  true, ignoreOnDtoToEntity = true)
    @JsonSerialize(using = ToStringSerializer.class)
    @Positive(groups = AnalysisDTO.Create.class)
    private Integer analysisTypeId;

    @GobiiEntityMap(paramName = "description", entity = Analysis.class)
    private String description;
    

    @GobiiEntityMap(paramName = "type.term", entity = Analysis.class, deep =  true, ignoreOnDtoToEntity = true)
    private String analysisTypeName;

    @GobiiEntityMap(paramName = "program", entity = Analysis.class)
    private String program;

    @GobiiEntityMap(paramName = "programVersion", entity = Analysis.class)
    private String programVersion;

    @GobiiEntityMap(paramName = "algorithm", entity = Analysis.class)
    private String algorithm;

    @GobiiEntityMap(paramName = "sourceName", entity = Analysis.class)
    private String sourceName;

    @GobiiEntityMap(paramName = "sourceVersion", entity = Analysis.class)
    private String sourceVersion;

    @GobiiEntityMap(paramName = "sourceUri", entity = Analysis.class)
    private String sourceUri;

    @GobiiEntityMap(paramName = "reference.referenceId", entity = Analysis.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer referenceId;

    @GobiiEntityMap(paramName = "reference.referenceName", entity = Analysis.class, deep = true, ignoreOnDtoToEntity = true)
    private String referenceName;

    @GobiiEntityMap(paramName = "parameters", entity = Analysis.class)
    private Map<String, String> parameters;

}