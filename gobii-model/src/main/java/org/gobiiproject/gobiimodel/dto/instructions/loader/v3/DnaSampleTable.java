package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@GobiiAspectTable(name = "dnasample")
@Data
public class DnaSampleTable implements Table {

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("name")
    private CoordinatesAspect dnaSampleName;

    @JsonProperty("num")
    private Aspect dnaSampleNum;

    @JsonProperty("external_code")
    private CoordinatesAspect germplasmExternalCode;

    @JsonProperty("platename")
    private CoordinatesAspect dnaSamplePlateName;

    @JsonProperty("well_row")
    private CoordinatesAspect dnaSampleWellRow;

    @JsonProperty("well_col")
    private CoordinatesAspect dnaSampleWellCol;

    @JsonProperty("uuid")
    private CoordinatesAspect dnaSampleUuid;

    @JsonProperty("props")
    private JsonAspect dnaSampleProperties;

    private String status;

}
