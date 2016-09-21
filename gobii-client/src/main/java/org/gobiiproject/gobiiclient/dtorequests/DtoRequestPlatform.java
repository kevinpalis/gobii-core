// ************************************************************************
// (c) 2016 GOBii DataSet
// Initial Version: Phil Glaser
// Create Date:   2016-03-25
// ************************************************************************
package org.gobiiproject.gobiiclient.dtorequests;


import org.gobiiproject.gobiiclient.core.restmethods.dtopost.DtoRequestProcessor;
import org.gobiiproject.gobiimodel.dto.container.PlatformDTO;
import org.gobiiproject.gobiimodel.dto.types.ControllerType;
import org.gobiiproject.gobiimodel.dto.types.ServiceRequestId;

public class DtoRequestPlatform {


    public PlatformDTO process(PlatformDTO platformDTO) throws Exception {

        return new DtoRequestProcessor<PlatformDTO>().process(platformDTO,
                PlatformDTO.class,
                ControllerType.LOADER,
                ServiceRequestId.URL_PLATFORM);

    } // getPing()


}
