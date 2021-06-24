// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiidomain.services;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.system.PingDTO;

/**
 * Created by Phil on 3/24/2016.
 */
public interface PingService {

    PingDTO getPings(PingDTO pingDTO) throws GobiiException;
}
