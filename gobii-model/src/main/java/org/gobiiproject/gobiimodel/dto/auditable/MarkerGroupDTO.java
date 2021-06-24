// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiimodel.dto.auditable;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;
import org.gobiiproject.gobiimodel.dto.children.MarkerGroupMarkerDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

public class MarkerGroupDTO extends DTOBaseAuditable {


    public MarkerGroupDTO() {
        super(GobiiEntityNameType.MARKER_GROUP);
    }

    private Integer markerGroupId;
    private String name;
    private String code;
    private List<MarkerGroupMarkerDTO> markers = new ArrayList<>();
    private String germplasmGroup;
    private Integer statusId;


    @Override
    public Integer getId() {
        return this.markerGroupId;
    }

    @Override
    public void setId(Integer id) {
        this.markerGroupId = id;
    }

    @GobiiEntityParam(paramName = "markerGroupId")
    public Integer getMarkerGroupId() {
        return markerGroupId;
    }

    @GobiiEntityColumn(columnName = "marker_group_id")
    public void setMarkerGroupId(Integer markerGroupId) {
        this.markerGroupId = markerGroupId;
    }


    @GobiiEntityParam(paramName = "status")
    public Integer getStatusId() {
        return statusId;
    }

    @GobiiEntityColumn(columnName = "status")
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    @GobiiEntityParam(paramName = "germplasmGroup")
    public String getGermplasmGroup() {
        return germplasmGroup;
    }

    @GobiiEntityColumn(columnName = "germplasm_group")
    public void setGermplasmGroup(String germplasmGroup) {
        this.germplasmGroup = germplasmGroup;
    }

    public List<MarkerGroupMarkerDTO> getMarkers() {
        return markers;
    }

    public void setMarkers(List<MarkerGroupMarkerDTO> markers) {
        this.markers = markers;
    }

    @GobiiEntityParam(paramName = "code")
    public String getCode() {
        return code;
    }

    @GobiiEntityColumn(columnName = "code")
    public void setCode(String code) {
        this.code = code;
    }

    @GobiiEntityParam(paramName = "name")
    public String getName() {
        return name;
    }

    @GobiiEntityColumn(columnName = "name")
    public void setName(String name) {
        this.name = name;
    }


    // ******************** below this line is garbage and can probably be removed
    private Map<String, List<String>> markerMap = new HashMap<>();

    public Map<String, List<String>> getMarkerMap() {
        return markerMap;
    }

    public void setMarkerMap(Map<String, List<String>> markerMap) {
        this.markerMap = markerMap;
    }

    @JsonIgnore
    public Set<String> getMarkerGroups() {
        return markerMap.keySet();
    }

}//ResourceDTO
