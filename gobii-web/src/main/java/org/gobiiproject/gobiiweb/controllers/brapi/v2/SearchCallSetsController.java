package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.brapi.CallSetService;
import org.gobiiproject.gobiidomain.services.brapi.SearchService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.CallSetDTO;
import org.gobiiproject.gobiimodel.dto.brapi.CallSetsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiimodel.dto.noaudit.SearchResultDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
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
 * Brapi REST endpoint for search callsets (dna runs)
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/search/callsets")
@CrossOrigin
@Api
@Slf4j
public class SearchCallSetsController extends SearchController {

    private final CallSetService callSetService;

    /**
     * Constructor.
     *
     * @param searchService     The {@link SearchService} instance.
     * @param callSetService    The {@link CallSetService} instance.
     */
    @Autowired
    public SearchCallSetsController(final SearchService searchService,
                                    final CallSetService callSetService) {
        super(searchService);
        this.callSetService = callSetService;
    }

    /**
     * Creates callsets search query resource and returns the id for the same.
     * resource id is used to fetch callsets which match the search query
     *
     * @param callSetsSearchQuery   callSets search query object
     * @param request               http request object to fetch croptype.
     * @return {@link SearchResultDTO} Result object with search query resource id
     * @throws GobiiException when the request is bad or failure to create a search resource.
     */
    @ApiOperation(
        value = "Search CallSets", notes = "Creates a search query for callsets",
        tags = {"CallSets"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="Search CallSets")
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
    searchCallSets(
        @Valid @RequestBody CallSetsSearchQueryDTO callSetsSearchQuery,
        HttpServletRequest request
    ) throws GobiiException {

        log.info(
            "Submitting callset search query\n {}",
            callSetsSearchQuery.toString()
        );

        return submitSearchQuery(callSetsSearchQuery, request);
    }

    /**
     * Gets CallSets result for search query result id.
     *
     * @param searchResultDbId  callsets search result id.
     * @param pageSize          size of the page
     * @param page              page number to fetch
     * @return  Brapi list payload with callsets.
     * @throws GobiiException when it is a bad request or any other service error
     */
    @ApiOperation(
        value = "List CallSets for SearchQuery",
        notes = "List of all the callsets for given search query",
        tags = {"CallSets"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="List CallSets for SearchQuery")
            })
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "", response = CallSetService.class),
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
    public ResponseEntity<BrApiMasterListPayload<CallSetDTO>>
    getCallSetsBySearchQuery(
        @ApiParam(value = "Search Query Id for which genotypes need to be fetched.")
        @PathVariable String searchResultDbId,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        @ApiParam(value = "Page number", defaultValue = BrapiDefaults.pageNum)
        @RequestParam(value = "page", required = false,
            defaultValue = BrapiDefaults.pageNum) Integer page,
        HttpServletRequest request
    ) throws GobiiException {

        String cropType;

        try {
            cropType = CropRequestAnalyzer.getGobiiCropType(request);
        }
        catch (Exception e) {
            throw new GobiiException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Internal Server Error " + e.getMessage());
        }

        log.info("Getting search results for callSet search query. " +
                "[searchResultDbId | cropType]\n {} | {} ",
            searchResultDbId,
            cropType);

        CallSetsSearchQueryDTO callSetsSearchQuery =
            (CallSetsSearchQueryDTO) searchService.getSearchQuery(
                searchResultDbId,
                cropType,
                CallSetsSearchQueryDTO.class.getName());

        PagedResult<CallSetDTO> pagedResult = callSetService.getCallSetsBySearchQuery(
            callSetsSearchQuery,
            pageSize,
            page);

        BrApiMasterListPayload<CallSetDTO> payload = new BrApiMasterListPayload<>(
            pagedResult.getResult(),
            pagedResult.getCurrentPageSize(),
            pagedResult.getCurrentPageNum());

        return ResponseEntity.ok(payload);
    }

}
