package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobidomain.services.brapi.CallSetService;
import org.gobiiproject.gobidomain.services.brapi.GenotypeCallsService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.*;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.dto.system.PagedResultTyped;
import org.gobiiproject.gobiimodel.types.BrapiDefaults;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Scope(value = "request")
@Controller
@RequestMapping("/brapi/v2/callsets")
@CrossOrigin
@Api
@Slf4j
public class CallSetsController {


    private final CallSetService callSetService;
    private final GenotypeCallsService genotypeCallsService;

    @Autowired
    public CallSetsController(final CallSetService callSetService,
                              final GenotypeCallsService genotypeCallsService) {
        this.callSetService = callSetService;
        this.genotypeCallsService  = genotypeCallsService;
    }

    /**
     * Lists the dnaruns by page size and page token
     * @param page - page number to be fetched for callsets
     * @param pageSize - Page size set by the user.
     *                 If page size is more than maximum allowed
     *                 page size, then the response will have maximum page size
     * @param variantSetDbId - Variant Set Db Id
     * @param callSetsFilter - CallsetBrapiDTO model to map the filters
     * @return Brapi response with list of dna runs/call sets
     */
    @ApiOperation(value = "List CallSets", notes = "Lists CallSets in GDM System.",
        tags = {"CallSets"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="List CallSets")
        })
    })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "",
            response = SwaggerResponseModels.CallSetListResponse.class),
        @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header", dataType = "string")
    })
    @GetMapping(produces = "application/json")
    public @ResponseBody ResponseEntity<BrApiMasterListPayload<CallSetDTO>>
    getCallSets(
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        @ApiParam(value = "Used to request a specific page of data to be returned. " +
            "The page indexing starts at 0 (the first page is 'page'= 0). Default is 0")
        @RequestParam(value  = "page", required = false,
            defaultValue = BrapiDefaults.pageNum) Integer page,
        @RequestParam(value = "variantSetDbId", required = false) Integer variantSetDbId,
        CallSetDTO callSetsFilter
    ) throws GobiiException {

        log.info(
            "getting CallSets by [pageSize | page | variantSetDbId]\n {} | {} | {}",
            pageSize,
            page,
            variantSetDbId
        );

        PagedResult<CallSetDTO> callSets = callSetService.getCallSets(
            pageSize,
            page,
            variantSetDbId,
            callSetsFilter);

        BrApiMasterListPayload<CallSetDTO> payload = new BrApiMasterListPayload<>(
            callSets.getResult(),
            callSets.getCurrentPageSize(),
            callSets.getCurrentPageNum());

        return ResponseEntity.ok(payload);

    }

    /**
     * Endpoint for getting a specific callset with a given callSetDbId
     *
     * @param callSetDbId ID of the requested callsets
     * @return ResponseEntity with http status code
     * specifying if retrieval of the callset is successful.
     *
     * Response body contains the requested callset information
     */
    @ApiOperation(
        value = "Get CallSet by callSetDbId",
        notes = "Retrieves the CallSet with given callSetDbId",
        tags = {"CallSets"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="Get CallSet By Id")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "",
            response = SwaggerResponseModels.CallSetResponse.class),
        @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token", required = true,
            paramType = "header", dataType = "string"),
    })
    @GetMapping(value="/{callSetDbId:[\\d]+}", produces = "application/json")
    public
    @ResponseBody ResponseEntity<BrApiMasterPayload<CallSetDTO>>
    getCallSetsByCallSetDbId(
        @ApiParam(value = "ID of the Callset to be extracted", required = true)
        @PathVariable("callSetDbId") Integer callSetDbId
    ) throws GobiiException {

        log.info("getting CallSets by id {}", callSetDbId);

        CallSetDTO callSet = callSetService.getCallSetById(callSetDbId);
        BrApiMasterPayload<CallSetDTO> payload = new BrApiMasterPayload<>(callSet);
        return ResponseEntity.ok(payload);
    }

    /**
     * Returns the list of genotypes calls in a given DNA run id.
     * It fetches calls in all the datasets where the dnarun_id is present.
     * The calls is paged.
     *
     * @param callSetDbId - DNA run Id.
     * @param pageSize - Size of the page to fetched.
     * @param pageToken - Page token to fetch the page.
     *                  User will get the pageToken from the nextPageToken
     *                  parameter in the previous response.
     *
     * @return BrApi Response entity with list
     * of genotypes calls for given dnarun id.
     */
    @ApiOperation(
        value = "List Genotypes by CallSet", notes = "List of all the genotype calls in a " +
        "CallSet with callSetDbId",
        tags = {"CallSets"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="List Genotypes by CallSet")
        })
    }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "", response = GenotypeCallsService.class),
        @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header", dataType = "string")
    })
    @RequestMapping(value="/{callSetDbId}/calls", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<BrApiMasterPayload<GenotypeCallsResult>>
    getCallsByCallSet(
        @ApiParam(value = "Id for dna run to be fetched")
        @PathVariable(value="callSetDbId") Integer callSetDbId,
        @ApiParam(value = "Page Token to fetch a page. " +
            "Value is $metadata.pagination.nextPageToken form previous page.")
        @RequestParam(value = "pageToken", required = false) String pageToken,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize
    ) throws GobiiException {

        log.info("getting Genotypes Calls by CallSet id {}", callSetDbId);

        PagedResultTyped<GenotypeCallsResult> genotypeCalls =
            genotypeCallsService.getGenotypeCallsByCallSetId(callSetDbId, pageSize, pageToken);

        BrApiMasterPayload<GenotypeCallsResult> payload = new BrApiMasterPayload<>(
            genotypeCalls.getResult(),
            genotypeCalls.getCurrentPageSize(),
            genotypeCalls.getNextPageToken());

        return ResponseEntity.ok(payload);
    }
}
