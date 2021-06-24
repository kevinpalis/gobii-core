package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiimodel.dto.brapi.VariantSetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface VariantSetsService {

    PagedResult<VariantSetDTO> getVariantSets(Integer pageSize, Integer pageNum,
                               Integer variantSetDbId, String variantSetName,
                               Integer studyDbId, String studyName);

    VariantSetDTO getVariantSetById(Integer variantSetDbId);

    String getEtag(Integer datasetId);

}
