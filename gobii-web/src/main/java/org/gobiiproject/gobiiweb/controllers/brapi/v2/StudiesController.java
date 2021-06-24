package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.brapi.StudiesService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.StudiesDTO;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.BrapiDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;

/**
 * Brapi REST endpoint for Studies(experiment)
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping(GobiiControllerType.SERVICE_PATH_BRAPI_V2)
@CrossOrigin
@Api
public class StudiesController {


    private final StudiesService studiesService;

    /**
     * Constructor
     *
     * @param studiesService {@link StudiesService} instance
     */
    @Autowired
    public StudiesController(final StudiesService studiesService) {
        this.studiesService = studiesService;
    }

    /**
     *
     * @param pageSize  Size of the page
     * @param page      page number
     * @param projectId id of the project. to filter by project experiments belong to.
     * @return Brapi list of experiments.
     * @throws GobiiException when it is a bad request or service error.
     */
    @ApiOperation(
        value = "List Studies", notes = "Lists Studies in GDM system",
        tags = {"Studies"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="List Studies")
        })
    })
    @ApiResponses(
        value = {
            @ApiResponse(code = 200, message = "",
                response = SwaggerResponseModels.StudiesListResponse.class),
            @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
        })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header", dataType = "string")
    })
    @GetMapping(value="/studies", produces = "application/json")
    public @ResponseBody
    ResponseEntity<BrApiMasterListPayload<StudiesDTO>> getStudies(
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        @ApiParam(value = "Used to request a specific page of data to be returned. " +
            "The page indexing starts at 0 (the first page is 'page'= 0). Default is 0")
        @RequestParam(value  = "page", required = false,
            defaultValue = BrapiDefaults.pageNum) Integer page,
        @ApiParam(value = "Filter by Project Id")
        @RequestParam(value = "projectId", required = false) Integer projectId
    ) throws GobiiException {

        PagedResult<StudiesDTO> studies = studiesService.getStudies(
            pageSize,
            page,
            projectId);

        BrApiMasterListPayload<StudiesDTO> payload = new BrApiMasterListPayload<>(
            studies.getResult(),
            studies.getCurrentPageSize(),
            studies.getCurrentPageNum());

        return ResponseEntity.ok(payload);
    }

}
