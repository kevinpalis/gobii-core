package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.PlatformDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface PlatformService {

	PlatformDTO createPlatform(PlatformDTO request, String createdBy) throws Exception;

	PagedResult<PlatformDTO> getPlatforms(Integer page, Integer pageSize, Integer platformTypeId) throws Exception;

	PlatformDTO getPlatform(Integer platformId) throws Exception;

	PlatformDTO updatePlatform(Integer platformId, PlatformDTO request, String updatedBy) throws Exception;

	void deletePlatform(Integer platformId) throws Exception;

	CvTypeDTO createPlatformType(CvTypeDTO request);

	PagedResult<CvTypeDTO> getPlatformTypes(Integer page, Integer pageSize);
    
}