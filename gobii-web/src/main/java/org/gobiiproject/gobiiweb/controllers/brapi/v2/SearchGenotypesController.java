package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.brapi.CallSetService;
import org.gobiiproject.gobiidomain.services.brapi.GenotypeCallsService;
import org.gobiiproject.gobiidomain.services.brapi.SearchService;
import org.gobiiproject.gobiidomain.services.brapi.VariantService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.CallSetDTO;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsResult;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.brapi.VariantDTO;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiimodel.dto.noaudit.SearchResultDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.dto.system.PagedResultTyped;
import org.gobiiproject.gobiimodel.types.BrapiDefaults;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Brapi REST endpoint for search genotype calls.
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/search/calls")
@CrossOrigin
@Api
@Slf4j
public class SearchGenotypesController extends SearchController {

    private final GenotypeCallsService genotypeCallsService;
    private final VariantService variantService;
    private final CallSetService callSetService;

    /**
     * Constructor.
     *
     * @param searchService         The {@link SearchService} instance.
     * @param genotypeCallsService  The {@link GenotypeCallsService} instance.
     * @param variantService        The {@link VariantService} instance.
     *
     */
    @Autowired
    public SearchGenotypesController(final SearchService searchService,
                                     final GenotypeCallsService genotypeCallsService,
                                     final VariantService variantService,
                                     final CallSetService callSetService) {
        super(searchService);
        this.genotypeCallsService = genotypeCallsService;
        this.variantService = variantService;
        this.callSetService = callSetService;
    }

    /**
     * Creates callsets search query resource and returns the id for the same.
     * resource id is used to fetch genotypes which matches the search query.
     *
     * @param genotypeCallsSearchQuery  genotypes search query object
     * @param request                   http request object to fetch croptype.
     * @return {@link SearchResultDTO} Result object with search query resource id
     * @throws GobiiException when it is a bad request or due to any other service error.
     */
    @ApiOperation(
        value = "Search Genotypes", notes = "Creates a search query for genotypes search",
        tags = {"Genotype Calls"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="Search Genotypes")
        })
    }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "",
            response = SwaggerResponseModels.SearchResultResponse.class),
        @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 404, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header", dataType = "string")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BrApiMasterPayload<SearchResultDTO>>
    searchGenotypeCalls(
        @Valid @RequestBody GenotypeCallsSearchQueryDTO genotypeCallsSearchQuery,
        HttpServletRequest request
    ) throws GobiiException {

        log.info("Submitting genotype calls search query\n {}",
            genotypeCallsSearchQuery.toString());

        return submitSearchQuery(genotypeCallsSearchQuery, request);
    }


    /**
     * Gets Genotype calls for given search query result id.
     *
     * @param searchResultDbId  genotype calls search result id.
     * @param pageSize          size of the page,
     * @param pageToken         token to fetch the page. can be obtained
     *                          from nextPageToken field in previous response.
     * @return  Brapi list payload with genotype calls.
     * @throws GobiiException when it is a bad request or due to any other service error.
     */
    @ApiOperation(
        value = "List Genotypes for SearchQuery",
        notes = "List of all the genotype calls for given search query",
        tags = {"Genotype Calls"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="List Genotypes for SearchQuery")
            })
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "",
            response = SwaggerResponseModels.GenotypeCallsListResponse.class),
        @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 404, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header", dataType = "string")
    })
    @GetMapping(value = "/{searchResultDbId}", produces = "application/json")
    public ResponseEntity<BrApiMasterPayload<GenotypeCallsResult>>
    getGenotypeCallsBySearchQuery(
        @ApiParam(value = "Search Query Id for which genotypes need to be fetched.")
        @PathVariable String searchResultDbId,
        @ApiParam(value = "Page Token to fetch a page. " +
            "Value is $metadata.pagination.nextPageToken form previous page.")
        @RequestParam(value = "pageToken", required = false) String pageToken,
        @ApiParam(value = "Size of the page to be fetched. Default is 100000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.genotypesPageSize) Integer pageSize,
        HttpServletRequest request) throws GobiiException {


        PagedResultTyped<GenotypeCallsResult> pagedResult =
            genotypeCallsService.getGenotypeCallsByExtractQuery(
                getGenotypeCallsSearchQuery(request, searchResultDbId),
                pageSize,
                pageToken);

        BrApiMasterPayload<GenotypeCallsResult> payload = new BrApiMasterPayload<>(
            pagedResult.getResult(),
            pagedResult.getCurrentPageSize(),
            pagedResult.getNextPageToken());

        return ResponseEntity.ok(payload);
    }

    /**
     * Gets Variants associated with genotypes search result.
     *
     * @param searchResultDbId  genotypes search result id.
     * @param pageSize          size of the page
     * @param pageToken         token to fetch the page. can be obtained
     *                          from nextPageToken field in previous response.
     * @return  Brapi list payload with variants.
     * @throws GobiiException when it is a bad request or due to any other service error.
     */
    @ApiOperation(
        value = "List Variants for Genotypes SearchQuery",
        notes = "List of all the variants for given genotypes search query",
        tags = {"Genotype Calls"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="List Variants for Genotypes SearchQuery")
            })
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "",
            response = SwaggerResponseModels.VariantListResponse.class),
        @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 404, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header", dataType = "string")
    })
    @GetMapping(value = "/{searchResultDbId}/variants", produces = "application/json")
    public ResponseEntity<BrApiMasterListPayload<VariantDTO>>
    getVariantsForGenotypesSearchQuery(
        @ApiParam(value = "Search Query Id for which genotypes need to be fetched.")
        @PathVariable String searchResultDbId,
        @ApiParam(value = "Page Token to fetch a page. " +
            "Value is $metadata.pagination.nextPageToken form previous page.")
        @RequestParam(value = "pageToken", required = false) String pageToken,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        HttpServletRequest request
    ) throws GobiiException {

        PagedResult<VariantDTO> pagedResult = variantService.getVariantsByGenotypesExtractQuery(
            getGenotypeCallsSearchQuery(request, searchResultDbId),
            pageSize,
            pageToken);

        BrApiMasterListPayload<VariantDTO> payload = new BrApiMasterListPayload<>(
            pagedResult.getResult(),
            pagedResult.getCurrentPageSize(),
            pagedResult.getNextPageToken());

        return ResponseEntity.ok(payload);
    }

    /**
     * Gets CallSets associated with genotypes search result.
     *
     * @param searchResultDbId  genotypes search result id.
     * @param pageSize          size of the page
     * @param pageToken         token to fetch the page. can be obtained
     *                          from nextPageToken field in previous response.
     * @return  Brapi list payload with variants.
     * @throws GobiiException when it is a bad request or due to any other service error.
     */
    @ApiOperation(
        value = "List CallSets for Genotypes SearchQuery",
        notes = "List of all the callsets for given genotypes search query",
        tags = {"Genotype Calls"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="List CallSets for Genotypes SearchQuery")
            })
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "",
            response = SwaggerResponseModels.CallSetListResponse.class),
        @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 404, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header", dataType = "string")
    })
    @GetMapping(value = "/{searchResultDbId}/callsets", produces = "application/json")
    public ResponseEntity<BrApiMasterListPayload<CallSetDTO>>
    getCallSetsForGenotypesSearchQuery(
        @ApiParam(value = "Search Query Id for which genotypes need to be fetched.")
        @PathVariable String searchResultDbId,
        @ApiParam(value = "Page Token to fetch a page. " +
            "Value is $metadata.pagination.nextPageToken form previous page.")
        @RequestParam(value = "pageToken", required = false) String pageToken,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        HttpServletRequest request
    ) throws GobiiException {

        PagedResult<CallSetDTO> pagedResult = callSetService.getCallSetsByGenotypesExtractQuery(
            getGenotypeCallsSearchQuery(request, searchResultDbId),
            pageSize,
            pageToken);

        BrApiMasterListPayload<CallSetDTO> payload = new BrApiMasterListPayload<>(
            pagedResult.getResult(),
            pagedResult.getCurrentPageSize(),
            pagedResult.getNextPageToken());

        return ResponseEntity.ok(payload);
    }


    private GenotypeCallsSearchQueryDTO getGenotypeCallsSearchQuery(HttpServletRequest request,
                                                                    String searchResultDbId
    ) throws GobiiException {

        String cropType;

        try {
            cropType = CropRequestAnalyzer.getGobiiCropType(request);
        }
        catch (Exception e) {
            throw new GobiiException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Internal Server Error " + e.getMessage()
            );
        }

        GenotypeCallsSearchQueryDTO genotypeCallsSearchQueryDTO =
            (GenotypeCallsSearchQueryDTO) searchService.getSearchQuery(
                searchResultDbId,
                cropType,
                GenotypeCallsSearchQueryDTO.class.getName());

        return genotypeCallsSearchQueryDTO;

    }


}
