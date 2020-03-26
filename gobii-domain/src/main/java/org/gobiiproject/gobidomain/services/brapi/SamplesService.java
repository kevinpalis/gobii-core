package org.gobiiproject.gobidomain.services.brapi;

import org.gobiiproject.gobiimodel.dto.brapi.SamplesDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface SamplesService {

    PagedResult<SamplesDTO> getSamples(Integer pageSize, Integer pageNum,
                                       Integer sampleDbId, Integer germplasmDbId,
                                       String observationUnitDbId);

}
