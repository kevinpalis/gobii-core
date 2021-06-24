package org.gobiiproject.gobiidtomapping.entity.auditable;

import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.auditable.MapsetDTO;

/**
 * Created by Phil on 4/28/2016.
 * Modified by AVB on 9/30/2016.
 */
public interface DtoMapMapset extends DtoMap<MapsetDTO> {

    MapsetDTO create(MapsetDTO mapsetDTO) throws GobiiDtoMappingException;
    MapsetDTO replace(Integer mapsetId, MapsetDTO mapsetDTO) throws GobiiDtoMappingException;
    MapsetDTO get(Integer mapsetId) throws GobiiDtoMappingException;
    List<MapsetDTO> getList() throws GobiiDtoMappingException;
    List<MapsetDTO> getMapsets() throws GobiiDtoMappingException;
}
