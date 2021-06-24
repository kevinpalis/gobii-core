package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.noaudit.MarkerDTO;

/**
 * Created by Phil on 4/21/2016.
 */
public interface MarkerService {

    MarkerDTO createMarker(MarkerDTO MarkerDTO) throws GobiiDomainException;
    MarkerDTO replaceMarker(Integer MarkerId, MarkerDTO MarkerDTO) throws GobiiDomainException;
    List<MarkerDTO> getMarkers() throws GobiiDomainException;
    MarkerDTO getMarkerById(Integer MarkerId) throws GobiiDomainException;
    List<MarkerDTO> getMarkersByName(String markerName) throws GobiiDomainException;

}
