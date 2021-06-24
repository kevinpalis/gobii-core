package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.brapi.SearchService;
import org.gobiiproject.gobiidomain.services.brapi.VariantService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.VariantDTO;
import org.gobiiproject.gobiimodel.dto.brapi.VariantsSearchQueryDTO;
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
 * Brapi REST endpoint for search variants.
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/search/variants")
@CrossOrigin
@Api
@Slf4j
public class SearchVariantsController extends SearchController {

    private final VariantService variantService;

    /**
     * Constructor.
     *
     * @param searchService     The {@link SearchService} instance.
     * @param variantService    The {@link VariantService} instance.
     */
    @Autowired
    public SearchVariantsController(final SearchService searchService,
                                    final VariantService variantService) {
        super(searchService);
        this.variantService = variantService;
    }

    /**
     * Creates variants search query resource and returns the id for the same.
     *
     * @param variantsSearchQuery   variants search query object
     * @param request               http request object to fetch croptype.
     *
     * @return {@link SearchResultDTO} Brapi result object with search query resource id
     * @throws GobiiException when it is a bad request or due to any other service error
     */
    @ApiOperation(
        value = "Search Variants", notes = "Creates a search query for variants",
        tags = {"Variants"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="Search Variants")
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
    public ResponseEntity<BrApiMasterPayload<SearchResultDTO>> searchVariants(
        @Valid @RequestBody VariantsSearchQueryDTO variantsSearchQuery,
        HttpServletRequest request
    ) throws GobiiException {
        return submitSearchQuery(variantsSearchQuery, request);
    }


    /**
     * Gets Variants for search query result id.
     *
     * @param searchResultDbId  variants search result id.
     * @param pageSize          size of the page
     * @param pageToken         token to fetch the page. can be obtained
     *                          from nextPageToken field in previous response.
     * @return  Brapi list payload with variants.
     * @throws GobiiException when it is a bad request or due to any other service error
     */
    @ApiOperation(
        value = "List Variants for SearchQuery",
        notes = "List of all the variants for given search query",
        tags = {"Variants"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="List Variants for SearchQuery")
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
    @RequestMapping(value = "/{searchResultDbId}", method = RequestMethod.GET,
        produces = "application/json")
    public ResponseEntity<BrApiMasterListPayload<VariantDTO>> getVariantsBySearchQuery(
        @ApiParam(value = "Search Query Id for which genotypes need to be fetched.")
        @PathVariable String searchResultDbId,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        @ApiParam(value = "Page Token to fetch a page. " +
            "Value is $metadata.pagination.nextPageToken form previous page.")
        @RequestParam(value = "pageToken", required = false) String pageToken,
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

        VariantsSearchQueryDTO variantsSearchQuery =
            (VariantsSearchQueryDTO) searchService.getSearchQuery(
                searchResultDbId,
                cropType,
                VariantsSearchQueryDTO.class.getName());

        PagedResult<VariantDTO> pagedResult = variantService
            .getVariantsByVariantSearchQuery(variantsSearchQuery, pageSize, pageToken);

        BrApiMasterListPayload<VariantDTO> payload = new BrApiMasterListPayload<>(
            pagedResult.getResult(), pagedResult.getCurrentPageSize(),
            pagedResult.getNextPageToken());

        return ResponseEntity.ok(payload);
    }


}
