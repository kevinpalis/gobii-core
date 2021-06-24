package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@GobiiAspectTable(name = "dnarun")
@Data
public class DnaRunTable implements Table {

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("experiment_id")
    private String experimentId;

    @JsonProperty("experiment_name")
    private CoordinatesAspect experimentName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("dnasample_name")
    private CoordinatesAspect dnaSampleName;

    @JsonProperty("num")
    private Aspect dnaSampleNum;

    @JsonProperty("name")
    private CoordinatesAspect dnaRunName;

    @JsonProperty("props")
    private JsonAspect dnaRunProperties;
}
