package org.gobiiproject.gobiidomain.services.gdmv3;

import java.util.List;

import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerGroupDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;


public interface MarkerGroupService {

	MarkerGroupDTO createMarkerGroup(MarkerGroupDTO request, String creator) throws Exception;

	PagedResult<MarkerGroupDTO> getMarkerGroups(Integer page, Integer pageSize);

	MarkerGroupDTO getMarkerGroup(Integer markerGroupId) throws Exception;

	MarkerGroupDTO updateMarkerGroup(Integer markerGroupId, MarkerGroupDTO request, String updatedBy) throws Exception;

	void deleteMarkerGroup(Integer markerGroupId) throws Exception;

    PagedResult<MarkerDTO> mapMarkers(Integer markerGroupId, List<MarkerDTO> markers, String editedBy) throws Exception;

	PagedResult<MarkerDTO> getMarkerGroupMarkers(Integer markerGroupId, Integer page, Integer pageSize) throws Exception;
    
}