package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.brapi.MapsetService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.BrapiDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Brapi REST endpoint for genome maps.
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/maps")
@CrossOrigin
@Api
@Slf4j
public class MapsController {

    private final MapsetService mapsetService;

    /**
     * Constructor
     *
     * @param mapsetService The {@link MapsetService} instance.
     */
    @Autowired
    public MapsController(final MapsetService mapsetService) {
        this.mapsetService = mapsetService;
    }

    /**
     * Gets genome maps based on user filter parameters.
     *
     * @param page      Page to get
     * @param pageSize  Size of the page
     * @param studyDbId Id of experiment. Get maps in the given experiment.
     * @return
     * @throws GobiiException
     */
    @ApiOperation(
        value = "List Genome Maps", notes = "Lists Genome maps in GDM system.",
        tags = {"Genome Maps"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="List Genome Maps")
        })
    }
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                code = 200, message = "",
                response = SwaggerResponseModels.MapsListResponse.class),
            @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
        }
    )
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token", required = true,
            paramType = "header", dataType = "string"),
    })
    @GetMapping(produces = "application/json")
    public @ResponseBody ResponseEntity<BrApiMasterListPayload<MapsetDTO>>
    getMaps(
        @ApiParam(value = "Used to request a specific page of data to be returned. " +
            "The page indexing starts at 0 (the first page is 'page'= 0). Default is 0")
        @RequestParam(value = "page", required = false,
            defaultValue = BrapiDefaults.pageNum) Integer page,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        @ApiParam(value = "Filter by studyDbId.")
        @RequestParam(value = "studyDbId", required = false) Integer studyDbId
    ) throws GobiiException {

        log.info(
            "getting Genome Maps by [pageSize | page | studyDbId]\n {} | {} | {}",
            pageSize,
            page,
            studyDbId
        );

        PagedResult<MapsetDTO> pagedResult = mapsetService.getMapSets(
            pageSize,
            page,
            studyDbId);

        BrApiMasterListPayload<MapsetDTO> payload = new BrApiMasterListPayload<>(
            pagedResult.getResult(),
            pagedResult.getCurrentPageSize(),
            pagedResult.getCurrentPageNum());

        return ResponseEntity.ok(payload);

    }

    /**
     * Gets genome map by id
     *
     * @param mapDbId   Id the the genome map to be fetched.
     * @return a genome map
     * @throws GobiiException when it is a bad request or service error.
     */
    @ApiOperation(
        value = "Get Map by mapDbId", notes = "Gets a genome map by map id",
        tags = {"Genome Maps"}, extensions = {
        @Extension(properties = {@ExtensionProperty(name="summary", value="Get Map by Id")})
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required = true, paramType = "header", dataType = "string"),
    })
    @GetMapping(value="/{mapDbId}", produces = "application/json")
    public @ResponseBody ResponseEntity<BrApiMasterPayload<MapsetDTO>> getMapByMapId(
        @ApiParam(value = "ID of the Genome Map", required = true)
        @PathVariable(value = "mapDbId") Integer mapDbId
    ) throws GobiiException {

        log.info("getting Genome Map by id {}", mapDbId);

        MapsetDTO mapset = mapsetService.getMapSetById(mapDbId);
        BrApiMasterPayload<MapsetDTO> payload = new BrApiMasterPayload<>(mapset);
        return ResponseEntity.ok(payload);
    }


}
