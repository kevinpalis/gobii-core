package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LoaderInstruction3 {


    private final String instructionType = "v3";

    @NotNull(message = "Required Crop Type")
    private String cropType;

    @NotNull(message = "Required Load Type")
    private String loadType;

    @NotNull(message = "Output direcotory not defined")
    private String outputDir;

    private String contactEmail;

    private Map<String, Object> aspects;

    private Object userRequest;

    private String jobName;

}
