package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@GobiiAspectTable(name = "marker_group")
@Data
public class MarkerGroupTable implements Table {

    @JsonProperty("platform_id")
    private String platformId;

    @NotNull(message = "marker name cannot be null")
    @JsonProperty("marker_name")
    private CoordinatesAspect markerName;

    @JsonProperty("platform_name")
    private CoordinatesAspect platformName;

    @NotNull(message = "marker group name cannot be null")
    @JsonProperty("marker_group_name")
    private CoordinatesAspect markerGroupName;

    private CoordinatesAspect germplasmGroup;

    @JsonProperty("fav_alleles")
    private CoordinatesAspect favorableAlleles;

}
