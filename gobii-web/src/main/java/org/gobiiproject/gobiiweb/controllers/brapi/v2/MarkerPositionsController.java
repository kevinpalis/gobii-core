package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.brapi.MarkerPositionsService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.MarkerPositions;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.BrapiDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Brapi REST endpoint for marker positions.
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/markerpositions")
@CrossOrigin
@Api
@Slf4j
public class MarkerPositionsController {

    private final MarkerPositionsService markerPositionsService;

    /**
     * Constructor
     *
     * @param markerPositionsService    {@link MarkerPositionsService} instance
     */
    @Autowired
    public MarkerPositionsController(final MarkerPositionsService markerPositionsService) {
        this.markerPositionsService = markerPositionsService;
    }

    /**
     * Gets markerpositions by user filter parameters.
     *
     * @param page                      Page to fetch
     * @param pageSize                  Size of the page
     * @param minPosition               Fetch marker positions with start position greater than
     *                                  the given value.
     * @param maxPosition               Fetch marker positions with stop position lesser than
     *                                  the given value.
     * @param variantSetDbId            Dataset Id marker positions belong to.
     * @param markerPositionsFilter     {@link MarkerPositions} filter by marker position fields.
     * @return  a list of marker position that satisfies filter parameters.
     * @throws GobiiException when the request is bad oe due to any other service error.
     */
    @ApiOperation(
        value = "List Marker positions", notes = "Lists Marker positions in GDM system.",
        tags = {"Genome Maps"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="List Marker positions")
        })
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required = true, paramType = "header", dataType = "string"),
    })
    @GetMapping(produces = "application/json")
    public @ResponseBody
    ResponseEntity<BrApiMasterListPayload<MarkerPositions>> getMarkersByMapId(
        @ApiParam(value = "Used to request a specific page of data to be returned. " +
            "The page indexing starts at 0 (the first page is 'page'= 0). Default is 0")
        @RequestParam(value = "page", required = false,
            defaultValue = BrapiDefaults.pageNum) Integer page,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        @ApiParam(value = "Filter positions greater than given value")
        @RequestParam(value = "minPosition", required = false) BigDecimal minPosition,
        @ApiParam(value = "Filter positions less than given value")
        @RequestParam(value = "maxPosition", required = false) BigDecimal maxPosition,
        @ApiParam(value = "Filter By VariantSet Db Id")
        @RequestParam(value = "variantSetDbId", required = false) Integer variantSetDbId,
        MarkerPositions markerPositionsFilter
    ) throws GobiiException {

        log.info("getting Marker Positions by [page | pageSize | minPosition | maxPosition |" +
            "variantSetDbId]\n {} | {} | {} | {} | {}",
            page,
            pageSize,
            minPosition,
            maxPosition,
            variantSetDbId);

        PagedResult<MarkerPositions> pagedResult =
            markerPositionsService.getMarkerPositions(
                pageSize,
                page,
                markerPositionsFilter,
                minPosition,
                maxPosition,
                variantSetDbId);

        BrApiMasterListPayload<MarkerPositions> payload =
            new BrApiMasterListPayload<>(
                pagedResult.getResult(),
                pagedResult.getCurrentPageSize(),
                pagedResult.getCurrentPageNum());

        return ResponseEntity.ok(payload);

    }

}
