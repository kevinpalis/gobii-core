package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

@JsonInclude(JsonInclude.Include.NON_NULL)
@GobiiAspectTable(name="marker_linkage_group")
public class MarkerLinkageGroupTable implements Table {

    @JsonProperty("platform_id")
    private String platformId;

    @JsonProperty("marker_name")
    @NotNull(message = "marker name is required")
    private CoordinatesAspect markerName;

    @JsonProperty("platform_name")
    private CoordinatesAspect platformName;

    @JsonProperty("linkage_group_name")
    @NotNull(message = "linkage group name is required")
    private CoordinatesAspect linkageGroupName;

    @JsonProperty("map_id")
    private String mapId;

    @JsonProperty("map_name")
    private CoordinatesAspect mapName;

    @JsonProperty("start")
    private CoordinatesAspect markerStart;

    @JsonProperty("stop")
    private CoordinatesAspect markerEnd;

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
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

    public CoordinatesAspect getLinkageGroupName() {
        return linkageGroupName;
    }

    public void setLinkageGroupName(CoordinatesAspect linkageGroupName) {
        this.linkageGroupName = linkageGroupName;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public CoordinatesAspect getMapName() {
        return mapName;
    }

    public void setMapName(CoordinatesAspect mapName) {
        this.mapName = mapName;
    }

    public CoordinatesAspect getMarkerStart() {
        return markerStart;
    }

    public void setMarkerStart(CoordinatesAspect markerStart) {
        this.markerStart = markerStart;
    }

    public CoordinatesAspect getMarkerEnd() {
        return markerEnd;
    }

    public void setMarkerEnd(CoordinatesAspect markerEnd) {
        this.markerEnd = markerEnd;
    }
}
