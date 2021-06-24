package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.auditable.MarkerGroupDTO;

/**
 * Created by Phil on 4/21/2016.
 */
public interface MarkerGroupService {

    List<MarkerGroupDTO> getMarkerGroups() throws GobiiDomainException;
    MarkerGroupDTO createMarkerGroup(MarkerGroupDTO markerGroupDTO) throws GobiiDomainException;
    MarkerGroupDTO replaceMarkerGroup(Integer markerGroupId, MarkerGroupDTO markerGroupDTO) throws GobiiDomainException;
    MarkerGroupDTO getMarkerGroupById(Integer markerGroupId) throws GobiiDomainException;

}
