package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.brapi.CallSetDTO;
import org.gobiiproject.gobiimodel.dto.brapi.CallSetsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface CallSetService {

    PagedResult<CallSetDTO> getCallSets(Integer pageSize,
                                        Integer pageNum,
                                        Integer variantSetDbId,
                                        CallSetDTO callSetsFilter);

    CallSetDTO getCallSetById(Integer callSetDbId);

    PagedResult<CallSetDTO> getCallSetsBySearchQuery(CallSetsSearchQueryDTO callSetsSearchQuery,
                                                     Integer pageSize, Integer pageNum);

    PagedResult<CallSetDTO> getCallSetsByGenotypesExtractQuery(
        GenotypeCallsSearchQueryDTO genotypesSearchQuery,
        Integer pageSize,
        String pageToken) throws GobiiDomainException;


}
