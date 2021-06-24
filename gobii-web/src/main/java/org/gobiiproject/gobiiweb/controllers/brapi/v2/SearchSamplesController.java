package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.brapi.SamplesService;
import org.gobiiproject.gobiidomain.services.brapi.SearchService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.SamplesDTO;
import org.gobiiproject.gobiimodel.dto.brapi.SamplesSearchQueryDTO;
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
 * Brapi REST endpoint for search samples.
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/search/samples")
@CrossOrigin
@Api
@Slf4j
public class SearchSamplesController extends SearchController {

    private final SamplesService sampleService;

    /**
     * Constructor.
     *
     * @param searchService The {@link SearchService} instance.
     * @param sampleService The {@link SamplesService} instance.
     */
    @Autowired
    public SearchSamplesController(final SearchService searchService,
                                   final SamplesService sampleService) {
        super(searchService);
        this.sampleService = sampleService;
    }

    /**
     * Creates samples search query resource and returns the id for the same.
     *
     * @param samplesSearchQuery    samples search query object
     * @param request               http request object to fetch croptype.
     * @return {@link SearchResultDTO} Brapi Result object with search query resource id
     * @throws GobiiException when it is a bad request or due to any other service error.
     */
    @ApiOperation(
        value = "Search Samples", notes = "Creates a search query for samples",
        tags = {"Samples"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="Search Samples")
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
    public ResponseEntity<BrApiMasterPayload<SearchResultDTO>> searchSamples(
        @Valid @RequestBody SamplesSearchQueryDTO samplesSearchQuery,
        HttpServletRequest request
    ) throws GobiiException {
        return submitSearchQuery(samplesSearchQuery, request);
    }

    /**
     * Gets Samples result for search query result id.
     *
     * @param searchResultDbId  samples search result id.
     * @param pageSize          size of the page
     * @param page              page number to fetch
     * @return  Brapi list payload with samples.
     * @throws GobiiException   when is it a bad request or due to any other service error.
     */
    @ApiOperation(
        value = "List Samples for SearchQuery",
        notes = "List of all the samples for given search query",
        tags = {"Samples"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="List Samples for SearchQuery")
            })
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "",
            response = SwaggerResponseModels.SamplesListResponse.class),
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
    public ResponseEntity<BrApiMasterListPayload<SamplesDTO>>
    getSamplesBySearchQuery(
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
                "Internal Server Error " + e.getMessage()
            );
        }

        SamplesSearchQueryDTO samplesSearchQuery = (SamplesSearchQueryDTO)
            searchService.getSearchQuery(
                searchResultDbId,
                cropType,
                SamplesSearchQueryDTO.class.getName());

        PagedResult<SamplesDTO> pagedResult = sampleService.getSamplesBySamplesSearchQuery(
            samplesSearchQuery,
            pageSize,
            page);

        BrApiMasterListPayload<SamplesDTO> payload = new BrApiMasterListPayload<>(
            pagedResult.getResult(),
            pagedResult.getCurrentPageSize(),
            pagedResult.getCurrentPageNum());

        return ResponseEntity.ok(payload);
    }


}
