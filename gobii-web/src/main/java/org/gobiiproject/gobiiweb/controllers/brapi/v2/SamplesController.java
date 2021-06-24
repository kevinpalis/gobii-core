package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.brapi.SamplesService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.SamplesDTO;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.BrapiDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Brapi REST endpoint for dna samples
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/samples")
@CrossOrigin
@Api
@Slf4j
public class SamplesController {


    private final SamplesService samplesService;

    /**
     * Constructor
     *
     * @param samplesBrapiService   The {@link SamplesService} instance.
     */
    @Autowired
    public SamplesController(final SamplesService samplesBrapiService) {
        this.samplesService = samplesBrapiService;
    }

    /**
     * Gets list of samples by filter parameters.
     *
     * @param sampleDbId            id of the sample to be fetched
     * @param observationUnitDbId   id of project to which samples belong to
     * @param germplasmDbId         id of the germplasm to which samples belong to
     * @param page                  page to be fetched
     * @param pageSize              size of the page
     * @return  list of samples filtered by given parameters
     * @throws GobiiException when request is bad or service error.
     */
    @ApiOperation(
        value = "List Samples", notes = "Lists Dna Samples in GDM System.",
        tags = {"Samples"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="List Samples")
        })
    }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "",
            response = SwaggerResponseModels.SamplesListResponse.class),
        @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header",
            dataType = "string")
    })
    @GetMapping(produces = "application/json")
    public @ResponseBody ResponseEntity<BrApiMasterListPayload<SamplesDTO>>
    getSamples(
        @ApiParam(value = "Id of the Sample to be fetched")
        @RequestParam(value="sampleDbId", required=false) Integer sampleDbId,
        @ApiParam(value = "Filter samples by observationUnitDbId.")
        @RequestParam(value="observationUnitDbId", required=false) String observationUnitDbId,
        @ApiParam(value = "Filter list of samples by germplasmDbId.")
        @RequestParam(value="germplasmDbId", required=false) Integer germplasmDbId,
        @ApiParam(value = "Used to request a specific page of data to be returned. " +
            "The page indexing starts at 0 (the first page is 'page'= 0). Default is 0")
        @RequestParam(value = "page", required = false,
            defaultValue = BrapiDefaults.pageNum) Integer page,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize
    ) throws GobiiException {

        log.info("getting Samples by [sampleDdbId | observationUnitDbId | germplasmDbId " +
                "| page | pageSize]\n {} | {} | {} | {} | {}",
            sampleDbId,
            observationUnitDbId,
            germplasmDbId,
            page,
            pageSize
        );

        PagedResult<SamplesDTO> samples = samplesService.getSamples(
            pageSize,
            page,
            sampleDbId,
            germplasmDbId,
            observationUnitDbId);

        BrApiMasterListPayload<SamplesDTO> payload = new BrApiMasterListPayload<>(
            samples.getResult(),
            samples.getCurrentPageSize(),
            samples.getCurrentPageNum());

        return ResponseEntity.ok(payload);

    }

}
