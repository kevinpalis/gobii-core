package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.brapi.VariantDTO;
import org.gobiiproject.gobiimodel.dto.brapi.VariantsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface VariantService {

    PagedResult<VariantDTO> getVariants(Integer pageSize, String pageToken,
                                        Integer variantDbId, Integer variantSetDbId);

    VariantDTO getVariantByVariantDbId(Integer variantDbId);

    PagedResult<VariantDTO> getVariantsByGenotypesExtractQuery(
        GenotypeCallsSearchQueryDTO genotypesSearchQuery,
        Integer pageSize,
        String pageToken) throws GobiiDomainException;

    PagedResult<VariantDTO> getVariantsByVariantSearchQuery(
        VariantsSearchQueryDTO variantsSearchQuery,
        Integer pageSize, String pageToken) throws GobiiDomainException;

}
