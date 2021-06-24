package org.gobiiproject.gobiidtomapping.entity.auditable;

import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.auditable.PlatformDTO;

/**
 * Created by Phil on 4/27/2016.
 */
public interface DtoMapPlatform extends DtoMap<PlatformDTO> {

    PlatformDTO create(PlatformDTO platformDTO) throws GobiiDtoMappingException;
    PlatformDTO replace(Integer platformId, PlatformDTO platformDTO) throws GobiiDtoMappingException;
    PlatformDTO get(Integer platformId) throws GobiiDtoMappingException;
    PlatformDTO getPlatformDetailsByVendorProtocolId(Integer vendorProtocolId) throws GobiiDtoMappingException;
    List<PlatformDTO> getList() throws GobiiDtoMappingException;

}
