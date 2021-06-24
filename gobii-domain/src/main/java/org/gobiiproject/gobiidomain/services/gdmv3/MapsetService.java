package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface MapsetService {
    PagedResult<MapsetDTO> getMapsets(Integer page, Integer pageSize, Integer mapsetTypeId) throws Exception;

	MapsetDTO createMapset(MapsetDTO mapset, String user) throws Exception;

	MapsetDTO getMapset(Integer mapsetId) throws Exception;

	MapsetDTO updateMapset(Integer mapsetId, MapsetDTO any, String editedBy) throws Exception;

	void deleteMapset(Integer mapsetId) throws Exception;

	CvTypeDTO createMapsetType(String mapsetTypeName, String mapsetTypeDescription, String user);

	PagedResult<CvTypeDTO> getMapsetTypes(Integer page, Integer pageSizeToUse);
}