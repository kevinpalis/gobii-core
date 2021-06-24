package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@GobiiAspectTable(name = "dataset_dnarun")
@Data
public class DatasetDnaRunTable implements Table {

    @JsonProperty("platform_id")
    private String platformId;

    @JsonProperty("platform_name")
    private CoordinatesAspect platformName;

    @NotNull(message = "dataset id cannot be null")
    @JsonProperty("dataset_id")
    private String datasetId;

    @JsonProperty("experiment_id")
    private String experimentId;

    @NotNull(message = "dnarun name cannot be null")
    @JsonProperty("dnarun_name")
    private CoordinatesAspect dnaRunName;

    @NotNull(message = "datset marker index cannot be null")
    @JsonProperty("dnarun_idx")
    private RangeAspect datasetDnaRunIdx;
}
