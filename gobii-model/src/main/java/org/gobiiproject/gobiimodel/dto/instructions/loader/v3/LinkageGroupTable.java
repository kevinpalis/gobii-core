package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

@JsonInclude(JsonInclude.Include.NON_NULL)
@GobiiAspectTable(name = "linkage_group")
public class LinkageGroupTable implements Table {

    @JsonProperty("map_id")
    private String mapId;

    @JsonProperty("map_name")
    private CoordinatesAspect mapName;

    @NotNull(message = "linkage group name is required")
    @JsonProperty("name")
    private CoordinatesAspect linkageGroupName;

    @JsonProperty("start")
    private CoordinatesAspect linkageGroupStart;

    @JsonProperty("stop")
    private CoordinatesAspect linkageGroupStop;

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public CoordinatesAspect getLinkageGroupName() {
        return linkageGroupName;
    }

    public void setLinkageGroupName(ColumnAspect linkageGroupName) {
        this.linkageGroupName = linkageGroupName;
    }

    public CoordinatesAspect getMapName() {
        return mapName;
    }

    public void setMapName(CoordinatesAspect mapName) {
        this.mapName = mapName;
    }

    public void setLinkageGroupName(CoordinatesAspect linkageGroupName) {
        this.linkageGroupName = linkageGroupName;
    }

    public CoordinatesAspect getLinkageGroupStart() {
        return linkageGroupStart;
    }

    public void setLinkageGroupStart(CoordinatesAspect linkageGroupStart) {
        this.linkageGroupStart = linkageGroupStart;
    }

    public CoordinatesAspect getLinkageGroupStop() {
        return linkageGroupStop;
    }

    public void setLinkageGroupStop(CoordinatesAspect linkageGroupStop) {
        this.linkageGroupStop = linkageGroupStop;
    }
}
