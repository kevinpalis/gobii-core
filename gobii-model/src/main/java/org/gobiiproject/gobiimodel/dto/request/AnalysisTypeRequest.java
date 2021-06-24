package org.gobiiproject.gobiimodel.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnalysisTypeRequest {

    @NotBlank
    private String analysisTypeName;
    
    private String analysisTypeDescription;

}