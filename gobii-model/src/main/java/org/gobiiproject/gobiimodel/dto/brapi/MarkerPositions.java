package org.gobiiproject.gobiimodel.dto.brapi;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.MarkerLinkageGroup;

public class MarkerPositions {

    @GobiiEntityMap(paramName = "linkageGroup.linkageGroupName", entity = MarkerLinkageGroup.class, deep = true)
    private String linkageGroupName;

    @GobiiEntityMap(paramName = "linkageGroup.mapset.mapsetId", entity = MarkerLinkageGroup.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer mapDbId;

    @GobiiEntityMap(paramName = "linkageGroup.mapset.mapsetName", entity = MarkerLinkageGroup.class, deep = true)
    private String mapName;

    @GobiiEntityMap(paramName = "start", entity = MarkerLinkageGroup.class, deep = true)
    private BigDecimal position;

    @GobiiEntityMap(paramName = "marker.markerId", entity = MarkerLinkageGroup.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer variantDbId;

    @GobiiEntityMap(paramName = "marker.markerName", entity = MarkerLinkageGroup.class, deep = true)
    private String variantName;

    public String getLinkageGroupName() {
        return linkageGroupName;
    }

    public void setLinkageGroupName(String linkageGroupName) {
        this.linkageGroupName = linkageGroupName;
    }

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

    public BigDecimal getPosition() {
        return position;
    }

    public void setPosition(BigDecimal position) {
        this.position = position;
    }

    public Integer getVariantDbId() {
        return variantDbId;
    }

    public void setVariantDbId(Integer variantDbId) {
        this.variantDbId = variantDbId;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }
}
