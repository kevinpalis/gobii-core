package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@GobiiAspectTable(name = "marker_group")
@Data
public class DatasetMarkerTable implements Table {

    @JsonProperty("platform_id")
    private String platformId;

    @NotNull(message = "marker name cannot be null")
    @JsonProperty("marker_name")
    private Aspect markerName;

    @JsonProperty("platform_name")
    private Aspect platformName;

    @NotNull(message = "dataset id cannot be null")
    @JsonProperty("dataset_id")
    private String datasetId;

    @NotNull(message = "datset marker index cannot be null")
    @JsonProperty("marker_idx")
    private Aspect datasetMarkerIdx;

}