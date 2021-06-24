package org.gobiiproject.gobiimodel.dto.request;

import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Analysis;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnalysisRequest {

    @NotEmpty
    @GobiiEntityMap(paramName = "analysisName", entity = Analysis.class)
    private String analysisName;

    @JsonSerialize(using = ToStringSerializer.class)
    @Positive
    private Integer analysisTypeId;

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

    //TODO: url validation?
    @GobiiEntityMap(paramName = "sourceUrl", entity = Analysis.class)
    private String sourceUrl;

    @JsonSerialize(using = ToStringSerializer.class)
    private Integer referenceId;

    @GobiiEntityMap(paramName = "analysisName", entity = Analysis.class)
    private Map<String, String> parameters;

}