package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Mapset;

/**
 * Brapi DTO for Genome Maps List
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapsetDTO {

    @GobiiEntityMap(paramName="mapsetId", entity = Mapset.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer mapDbId;

    @GobiiEntityMap(paramName="mapsetName", entity = Mapset.class)
    private String mapName;

    @GobiiEntityMap(paramName="mapsetDescription", entity = Mapset.class)
    private String comments;

    @GobiiEntityMap(paramName="type.term", entity = Mapset.class, deep = true)
    private String type;

    @GobiiEntityMap(paramName = "linkageGroupCount", entity = Mapset.class)
    private Integer linkageGroupCount;

    @GobiiEntityMap(paramName = "markerCount", entity = Mapset.class)
    private Integer markerCount;

    public Integer getMapDbId() {
        return mapDbId;
    }

    public void setMapDbId(Integer mapDbId) {
        this.mapDbId = mapDbId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLinkageGroupCount() {
        return linkageGroupCount;
    }

    public void setLinkageGroupCount(Integer linkageGroupCount) {
        this.linkageGroupCount = linkageGroupCount;
    }

    public Integer getMarkerCount() {
        return markerCount;
    }

    public void setMarkerCount(Integer markerCount) {
        this.markerCount = markerCount;
    }
}
