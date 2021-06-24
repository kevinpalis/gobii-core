package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.brapi.MarkerPositionsService;
import org.gobiiproject.gobiidomain.services.brapi.SamplesService;
import org.gobiiproject.gobiidomain.services.brapi.SearchService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.MarkerPositions;
import org.gobiiproject.gobiimodel.dto.brapi.MarkerPositionsSearchQueryDTO;
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
 * Brapi REST endpoint for search marker positions.
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/search/markerpositions")
@CrossOrigin
@Api
@Slf4j
public class SearchMarkerPositionsController extends SearchController {

    private final MarkerPositionsService markerPositionsService;

    /**
     * Constructor.
     *
     * @param searchService             The {@link SearchService} instance.
     * @param markerPositionsService    The {@link SamplesService} instance.
     */
    @Autowired
    public SearchMarkerPositionsController(final SearchService searchService,
                                           final MarkerPositionsService markerPositionsService) {
        super(searchService);
        this.markerPositionsService = markerPositionsService;
    }

    /**
     * Creates marker positions search query resource and returns the id for the same.
     * resource id is used to fetch marker positions which matches the search query.
     *
     * @param markerPositionsSearchQuery    marker positions search query object
     * @param request                       http request object to fetch crop type.
     * @return {@link SearchResultDTO} Brapi Result object with search query resource id
     * @throws GobiiException when the invalid query is submitted or due to any other service error
     */
    @ApiOperation(
        value = "Search MarkerPositions", notes = "Creates a search query for marker positions",
        tags = {"Genome Maps"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="Search MarkerPositions")
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
    public ResponseEntity<BrApiMasterPayload<SearchResultDTO>> searchMarkerPositions(
        @Valid @RequestBody MarkerPositionsSearchQueryDTO markerPositionsSearchQuery,
        HttpServletRequest request) {
        return submitSearchQuery(markerPositionsSearchQuery, request);
    }

    /**
     * Gets MarkerPositions for given search query result id.
     *
     * @param searchResultDbId  markerpositions search result id.
     * @param pageSize          size of the page
     * @param page              page number to fetch
     * @return  Brapi list payload with markerpositions.
     * @throws GobiiException when the invalid query is submitted or due to any other service error
     */
    @ApiOperation(
        value = "List Marker positions for SearchQuery",
        notes = "List of all the marker positions for given search query",
        tags = {"Genome Maps"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="List MarkerPositions for SearchQuery")
            })
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "",
            response = SwaggerResponseModels.MarkerPositionsListResponse.class ),
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
    public ResponseEntity<BrApiMasterListPayload<MarkerPositions>>
    getMarkerPositionsBySearchQuery(
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

        MarkerPositionsSearchQueryDTO markerPositionsSearchQuery =
            (MarkerPositionsSearchQueryDTO) searchService.getSearchQuery(
                searchResultDbId,
                cropType,
                MarkerPositionsSearchQueryDTO.class.getName());

        PagedResult<MarkerPositions> pagedResult =
            markerPositionsService.getMarkerPositionsBySearchQuery(
                markerPositionsSearchQuery,
                pageSize,
                page);

        BrApiMasterListPayload<MarkerPositions> payload = new BrApiMasterListPayload<>(
            pagedResult.getResult(),
            pagedResult.getCurrentPageSize(),
            pagedResult.getCurrentPageNum());

        return ResponseEntity.ok(payload);
    }


}
