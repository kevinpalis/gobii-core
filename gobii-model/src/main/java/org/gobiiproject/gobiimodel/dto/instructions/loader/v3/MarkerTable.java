package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

@JsonInclude(JsonInclude.Include.NON_NULL)
@GobiiAspectTable(name = "marker")
public class MarkerTable implements Table {

    @JsonProperty("platform_id")
    private String platformId;

    private String status;

    @JsonProperty("name")
    @NotNull(message = "marker name not mapped")
    private CoordinatesAspect markerName;

    @JsonProperty("platform_name")
    private CoordinatesAspect platformName;

    @JsonProperty("ref")
    private CoordinatesAspect markerRef;

    @JsonProperty("alts")
    private CoordinatesAspect markerAlt;

    @JsonProperty("sequence")
    private CoordinatesAspect markerSequence;

    @JsonProperty("strand_name")
    private CoordinatesAspect markerStrandName;

    @JsonProperty("props")
    private JsonAspect markerProperties;

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CoordinatesAspect getMarkerName() {
        return markerName;
    }

    public void setMarkerName(CoordinatesAspect markerName) {
        this.markerName = markerName;
    }

    public CoordinatesAspect getPlatformName() {
        return platformName;
    }

    public void setPlatformName(CoordinatesAspect platformName) {
        this.platformName = platformName;
    }

    public CoordinatesAspect getMarkerRef() {
        return markerRef;
    }

    public void setMarkerRef(CoordinatesAspect markerRef) {
        this.markerRef = markerRef;
    }

    public CoordinatesAspect getMarkerAlt() {
        return markerAlt;
    }

    public void setMarkerAlt(CoordinatesAspect markerAlt) {
        this.markerAlt = markerAlt;
    }

    public CoordinatesAspect getMarkerSequence() {
        return markerSequence;
    }

    public void setMarkerSequence(CoordinatesAspect markerSequence) {
        this.markerSequence = markerSequence;
    }

    public CoordinatesAspect getMarkerStrandName() {
        return markerStrandName;
    }

    public void setMarkerStrandName(CoordinatesAspect markerStrandName) {
        this.markerStrandName = markerStrandName;
    }

    public JsonAspect getMarkerProperties() {
        return markerProperties;
    }

    public void setMarkerProperties(JsonAspect markerProperties) {
        this.markerProperties = markerProperties;
    }
}
