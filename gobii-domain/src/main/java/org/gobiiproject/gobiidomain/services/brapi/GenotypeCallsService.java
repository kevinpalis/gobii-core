package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.brapi.*;
import org.gobiiproject.gobiimodel.dto.system.PagedResultTyped;

import java.math.BigDecimal;

public interface GenotypeCallsService {

    PagedResultTyped<GenotypeCallsResult> getGenotypeCallsByCallSetId(
        Integer callSetDbId,
        Integer pageSize,
        String pageToken) throws GobiiDomainException;

    PagedResultTyped<GenotypeCallsResult> getGenotypeCallsByVariantDbId(
        Integer markerId,
        Integer pageSize,
        String pageToken) throws GobiiDomainException;

    PagedResultTyped<GenotypeCallsResult> getGenotypeCallsByVariantSetDbId(
        Integer datasetId,
        String mapsetName,
        String linkageGroupName,
        BigDecimal minPosition,
        BigDecimal maxPosition,
        Integer pageSize,
        String pageToken) throws GobiiDomainException;

    PagedResultTyped<GenotypeCallsResult> getGenotypeCallsByExtractQuery(
        GenotypeCallsSearchQueryDTO genotypesSearchQuery,
        Integer pageSize,
        String pageToken) throws GobiiDomainException;



    String getGenotypeCallsAsString(Integer datasetId, Integer pageNum);

}
