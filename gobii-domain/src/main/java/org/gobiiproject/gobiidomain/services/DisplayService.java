// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.auditable.DisplayDTO;

/**
 * Created by Phil on 3/24/2016.
 */
public interface DisplayService {

    List<DisplayDTO> getDisplays() throws GobiiDomainException;
    DisplayDTO createDisplay(DisplayDTO displayDTO) throws GobiiDomainException;
    DisplayDTO replaceDisplay(Integer displayId, DisplayDTO displayDTO) throws GobiiDomainException;
    DisplayDTO getDisplayById(Integer displayId) throws GobiiDomainException;
}
