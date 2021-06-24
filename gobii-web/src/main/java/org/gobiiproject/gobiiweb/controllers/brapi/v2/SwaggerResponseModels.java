package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import org.gobiiproject.gobiimodel.dto.brapi.*;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiResult;
import org.gobiiproject.gobiimodel.dto.noaudit.SearchResultDTO;

/**
 * Response models for swagger api to generate api document.
 */
public class SwaggerResponseModels {
    public static class MapsListResponse extends BrApiMasterListPayload<BrApiResult<MapsetDTO>> {}
    public static class MapsResponse extends BrApiMasterPayload<MapsetDTO> {}
    public static class CallSetResponse extends BrApiMasterPayload<CallSetDTO>{}
    public static class CallSetListResponse extends BrApiMasterPayload<BrApiResult<CallSetDTO>>{}
    public static class GenotypeCallsListResponse
        extends BrApiMasterPayload<BrApiResult<GenotypeCallsDTO>>{}
    public static class SamplesListResponse extends BrApiMasterPayload<BrApiResult<SamplesDTO>>{}
    public static class StudiesListResponse extends BrApiMasterPayload<BrApiResult<StudiesDTO>> {}

    public static class GenotypeCallsResponse extends BrApiMasterPayload<GenotypeCallsDTO>{}
    public static class VariantResponse extends BrApiMasterPayload<VariantDTO>{}
    public static class VariantListResponse extends BrApiMasterPayload<BrApiResult<VariantDTO>>{}
    public static class SearchResultResponse extends BrApiMasterPayload<SearchResultDTO>{}

    public static class MarkerPositionsResponse extends BrApiMasterPayload<MarkerPositions>{}
    public static class MarkerPositionsListResponse extends
        BrApiMasterPayload<BrApiResult<MarkerPositions>>{}
    public static class VariantSetResponse extends BrApiMasterPayload<VariantSetDTO>{}
    public static class VariantSetListResponse extends BrApiMasterPayload<BrApiResult<VariantSetDTO>>{}

}
