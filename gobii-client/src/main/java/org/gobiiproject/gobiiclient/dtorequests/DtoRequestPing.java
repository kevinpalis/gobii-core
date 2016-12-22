// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-25
// ************************************************************************
package org.gobiiproject.gobiiclient.dtorequests;

import org.gobiiproject.gobiiclient.core.gobii.dtopost.DtoRequestProcessor;
import org.gobiiproject.gobiimodel.dto.container.PingDTO;
import org.gobiiproject.gobiiapimodel.types.ControllerType;
import org.gobiiproject.gobiiapimodel.types.ServiceRequestId;

public class DtoRequestPing {

    public PingDTO process(PingDTO pingDTO) throws Exception {

        return new DtoRequestProcessor<PingDTO>().process(pingDTO,
                PingDTO.class,
                ControllerType.LOADER,
                ServiceRequestId.URL_PING);

    } // getPingFromExtractController()

}
