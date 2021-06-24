package org.gobiiproject.gobiimodel.dto.gdmv3;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Marker;

import lombok.Data;

@Data
public class MarkerDTO {
    
    @GobiiEntityMap(paramName = "markerId", entity = Marker.class)
    private Integer markerId;

    @GobiiEntityMap(paramName = "markerName", entity = Marker.class)
    private String markerName;

    @GobiiEntityMap(paramName = "platform.platformId", entity = Marker.class, deep=true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer platformId;

    @GobiiEntityMap(paramName = "platform.platformName", entity = Marker.class, deep = true)
    private String platformName;

    //from marker_group data
    private String[] favorableAlleles;
}