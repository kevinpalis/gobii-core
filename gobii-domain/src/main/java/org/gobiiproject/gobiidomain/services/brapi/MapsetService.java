package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiimodel.dto.brapi.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface MapsetService {

    PagedResult<MapsetDTO> getMapSets(Integer pageSize, Integer pageNum, Integer studyDbId);
    MapsetDTO getMapSetById(Integer mapDbId);

}
