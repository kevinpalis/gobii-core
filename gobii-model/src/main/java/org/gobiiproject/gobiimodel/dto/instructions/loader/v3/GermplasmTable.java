package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@GobiiAspectTable(name = "germplasm")
public class GermplasmTable implements Table {

    @JsonProperty("name")
    private CoordinatesAspect germplasmName;

    @JsonProperty("external_code")
    private CoordinatesAspect germplasmExternalCode;

    @JsonProperty("type_name")
    private CoordinatesAspect germplasmType;

    @JsonProperty("species_name")
    private CoordinatesAspect germplasmSpeciesName;

    private String status;

    @JsonProperty("props")
    private JsonAspect germplasmProperties;

    public CoordinatesAspect getGermplasmName() {
        return germplasmName;
    }

    public void setGermplasmName(CoordinatesAspect germplasmName) {
        this.germplasmName = germplasmName;
    }

    public CoordinatesAspect getGermplasmExternalCode() {
        return germplasmExternalCode;
    }

    public void setGermplasmExternalCode(CoordinatesAspect germplasmExternalCode) {
        this.germplasmExternalCode = germplasmExternalCode;
    }

    public CoordinatesAspect getGermplasmType() {
        return germplasmType;
    }

    public void setGermplasmType(CoordinatesAspect germplasmType) {
        this.germplasmType = germplasmType;
    }

    public CoordinatesAspect getGermplasmSpeciesName() {
        return germplasmSpeciesName;
    }

    public void setGermplasmSpeciesName(CoordinatesAspect germplasmSpeciesName) {
        this.germplasmSpeciesName = germplasmSpeciesName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JsonAspect getGermplasmProperties() {
        return germplasmProperties;
    }

    public void setGermplasmProperties(JsonAspect germplasmProperties) {
        this.germplasmProperties = germplasmProperties;
    }
}
