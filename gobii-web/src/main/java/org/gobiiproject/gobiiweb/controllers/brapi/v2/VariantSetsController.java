package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.brapi.CallSetService;
import org.gobiiproject.gobiidomain.services.brapi.GenotypeCallsService;
import org.gobiiproject.gobiidomain.services.brapi.VariantService;
import org.gobiiproject.gobiidomain.services.brapi.VariantSetsService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.*;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiResult;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.dto.system.PagedResultTyped;
import org.gobiiproject.gobiimodel.types.BrapiDefaults;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Brapi REST endpoint for variantsets (datasets)
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/variantsets")
@CrossOrigin
@Api
@Slf4j
public class VariantSetsController {

    private final VariantSetsService variantSetsService;
    private final CallSetService callSetService;
    private final VariantService variantService;
    private final GenotypeCallsService genotypeCallsService;

    @Autowired
    public VariantSetsController(final VariantSetsService variantSetsService,
                                 final CallSetService callSetService,
                                 final VariantService variantService,
                                 final GenotypeCallsService genotypeCallsService) {
        this.variantSetsService = variantSetsService;
        this.callSetService = callSetService;
        this.variantService = variantService;
        this.genotypeCallsService = genotypeCallsService;
    }

    /**
     * Gets variantsets by filter parameters
     *
     * @param pageNum   page number to fetch
     * @param pageSize  size of the page
     * @return Brapi list of variantsets
     * @throws GobiiException when it is a bad request or service error
     */
    @ApiOperation(
        value = "List VariantSets", notes = "Lists VariantSets in GDM system.",
        tags = {"VariantSets"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="List VariantSets")
        })
    })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = SwaggerResponseModels.VariantListResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorPayload.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorPayload.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorPayload.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorPayload.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header", dataType = "string")
    })
    @GetMapping(produces = "application/json")
    public @ResponseBody
    ResponseEntity<BrApiMasterListPayload<VariantSetDTO>> getVariantSets(
        @ApiParam(value = "Id of the VariantSet to be fetched. Corresponds to dataset Id")
        @RequestParam(value = "variantSetDbId", required = false) Integer variantSetDbId,
        @ApiParam(value = "Study Id for which list of VariantSets need to be fetched. " +
            "Study corresponds to experiment")
        @RequestParam(value = "studyDbId", required = false) Integer studyDbId,
        @ApiParam(value = "Study Name for which list of VariantSets need to be fetched")
        @RequestParam(value = "studyName", required = false) String studyName,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        @ApiParam(value = "Page number to be fetched")
        @RequestParam(value = "page", required = false,
            defaultValue = BrapiDefaults.pageNum) Integer pageNum
    ) throws GobiiException {

        PagedResult<VariantSetDTO> pagedResult = variantSetsService.getVariantSets(
            pageSize,
            pageNum,
            variantSetDbId,
            null,
            studyDbId,
            studyName);

        BrApiMasterListPayload<VariantSetDTO> payload = new BrApiMasterListPayload<>(
            pagedResult.getResult(),
            pagedResult.getCurrentPageSize(),
            pagedResult.getCurrentPageNum());

        return ResponseEntity.ok(payload);
    }

    /**
     * Get variantset by id.
     *
     * @param variantSetDbId    id of the variantset to fetch
     * @return  Brapi result object with variantset
     * @throws GobiiException when it is a bad request or service error
     */
    @ApiOperation(
        value = "Get VariantSet by variantSetDbId",
        notes = "Retrieves the VariantSet entity having the specified ID",
        tags = {"VariantSets"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="Get VariantSet by Id")
            })
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "",
            response = SwaggerResponseModels.VariantSetResponse.class),
        @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 404, message = "", response = ErrorPayload.class),
        @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required = true, paramType = "header", dataType = "string"),
    })
    @GetMapping(value="/{variantSetDbId:[\\d]+}", produces = "application/json")
    public @ResponseBody ResponseEntity<BrApiMasterPayload<VariantSetDTO>> getVariantSetById(
        @ApiParam(value = "ID of the VariantSet to be extracted", required = true)
        @PathVariable("variantSetDbId") Integer variantSetDbId,
        HttpServletRequest request
    ) throws GobiiException {

        String ifNoneMatch = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        String etag = variantSetsService.getEtag(variantSetDbId);

        if(ifNoneMatch != null && ifNoneMatch.equals(etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).body(null);
        }

        VariantSetDTO variantSetDTO = variantSetsService.getVariantSetById(variantSetDbId);
        BrApiMasterPayload<VariantSetDTO> payload = new BrApiMasterPayload<>(variantSetDTO);

        if(etag != null) {
            return ResponseEntity
                .ok()
                .eTag(etag)
                .body(payload);
        }
        else {
            return ResponseEntity.ok(payload);
        }

    }

    /**
     * Get variants(markers) in variantset (dataset) by filter parameters
     *
     * @param variantSetDbId    id of the dataset
     * @param pageToken         token to fetch the page. can be obtained
     *                          from nextPageToken field in previous response.
     * @param pageSize          size of the page
     * @param variantDbId       id of the marker
     * @return Brapi list of variants in the dataset
     * @throws GobiiException when it is a bad request or service error
     */
    @ApiOperation(
        value = "List Variants in VariantSet",
        notes = "Lists all the Variants in VariantSet with variantSetDbId",
        tags = {"VariantSets"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="List Variants in VariantSet")
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
            required = true, paramType = "header", dataType = "string")
    })
    @GetMapping(value="/{variantSetDbId:[\\d]+}/variants", produces = "application/json")
    public @ResponseBody ResponseEntity<BrApiMasterListPayload<VariantDTO>>
    getVariantsByVariantSetDbId(
        @ApiParam(value = "ID of the VariantSet of the Variants to be extracted", required = true)
        @PathVariable("variantSetDbId") Integer variantSetDbId,
        @ApiParam(value = "Page Token to fetch a page. Value is " +
            "$metadata.pagination.nextPageToken form previous page.")
        @RequestParam(value = "pageToken", required = false) String pageToken,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        @ApiParam(value = "Get Variant with given variantDbId.")
        @RequestParam(value = "variantDbId", required = false) Integer variantDbId
    ) throws GobiiException {
        try {

            VariantSetDTO variantSet = variantSetsService.getVariantSetById(variantSetDbId);

            PagedResult<VariantDTO> pagedResult = variantService.getVariants(
                pageSize,
                pageToken,
                variantDbId,
                variantSet.getVariantSetDbId());

            BrApiMasterListPayload<VariantDTO>  payload = new BrApiMasterListPayload<>(
                pagedResult.getResult(),
                pagedResult.getCurrentPageSize(),
                pagedResult.getNextPageToken());

            return ResponseEntity.ok(payload);
        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            throw new GobiiException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                "Entity does not exist"
            );
        }
    }

    /**
     * Get list of genotypes in variantset.
     *
     * @param variantSetDbId    id of the variantset
     * @param mapName           filter parameter to genotypes calls list by genome map
     * @param linkageGroupName  filter parameter to genotype calls by linkage group name
     * @param minPosition       filter parameter to get gentypes with
     *                          marker positions greater than given minPosition
     * @param maxPosition       filter parameter to get genotypes with marker positions
     *                          lesser than maxPosition
     * @param pageToken         token to fetch the page. can be obtained
     *                          from nextPageToken field in previous response.
     * @param pageSize          size of the page
     * @return Brapi list of genotypes call
     * @throws GobiiException when it is a bad request or service error
     */
    @ApiOperation(
        value = "List Genotypes in VariantSet",
        notes = "List of all the genotype calls in a given VariantSet",
        tags = {"VariantSets"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="List Genotypes in VariantSet")
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
    @GetMapping(value="/{variantSetDbId:[\\d]+}/calls", produces = "application/json")
    public @ResponseBody ResponseEntity<BrApiMasterPayload<GenotypeCallsResult>>
    getCallsByVariantSetDbId(
        @ApiParam(value = "ID of the VariantSet", required = true)
            @PathVariable("variantSetDbId") Integer variantSetDbId,
        @ApiParam(value = "Name of the mapset")
            @RequestParam(value = "mapName", required = false) String mapName,
        @ApiParam(value = "Name of the linkagage group")
            @RequestParam(value = "linkageGroupName", required = false) String linkageGroupName,
        @ApiParam(value = "Minimum marker position")
            @RequestParam(value = "minPosition", required = false) BigDecimal minPosition,
        @ApiParam(value = "Maximum marker position")
            @RequestParam(value = "maxPosition", required = false) BigDecimal maxPosition,
        @ApiParam(value = "Page Token to fetch a page. " +
            "Value is $metadata.pagination.nextPageToken form previous page.")
            @RequestParam(value = "pageToken", required = false) String pageToken,
        @ApiParam(value = "Size of the page to be fetched. Default is 100000.")
            @RequestParam(value = "pageSize", required = false,
                defaultValue = BrapiDefaults.genotypesPageSize) Integer pageSize
    ) throws GobiiException {

        PagedResultTyped<GenotypeCallsResult> genotypeCallsPaged =
            genotypeCallsService.getGenotypeCallsByVariantSetDbId(
                variantSetDbId,
                mapName,
                linkageGroupName,
                minPosition,
                maxPosition,
                pageSize,
                pageToken);

        BrApiMasterPayload<GenotypeCallsResult> payload = new BrApiMasterPayload<>(
            genotypeCallsPaged.getResult(),
            genotypeCallsPaged.getCurrentPageSize(),
            genotypeCallsPaged.getNextPageToken());

        return ResponseEntity.ok(payload);
    }

    /**
     * Get list of callsets in given variantset.
     *
     * @param variantSetDbId    id of the variantset
     * @param page              page to fetch
     * @param pageSize          size of the page
     * @param callSetsFilter    {@link CallSetDTO} filter object
     * @return Brapi list of callsets
     *
     */
    @ApiOperation(
        value = "List CallSets in VariantSet", notes = "Lists CallSets in VariantSet",
        tags = {"VariantSets"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="List CallSets in VariantSet")
        })})
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
            required = true, paramType = "header", dataType = "string"
        )
    })
    @GetMapping(value="/{variantSetDbId:[\\d]+}/callsets", produces = "application/json")
    public @ResponseBody ResponseEntity<BrApiMasterListPayload<CallSetDTO>>
    getCallSetsByVariantSetDbId(
        @ApiParam(value = "ID of the VariantSet", required = true)
        @PathVariable("variantSetDbId") Integer variantSetDbId,
        @ApiParam(value = "Page number", defaultValue = BrapiDefaults.pageNum)
        @RequestParam(value = "page", required = false,
            defaultValue = BrapiDefaults.pageNum) Integer page,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        CallSetDTO callSetsFilter
    ) throws GobiiException {
        VariantSetDTO variantSet = variantSetsService.getVariantSetById(variantSetDbId);

        PagedResult<CallSetDTO> callSets = callSetService.getCallSets(
            pageSize,
            page,
            variantSet.getVariantSetDbId(),
            callSetsFilter);

        BrApiMasterListPayload<CallSetDTO> payload = new BrApiMasterListPayload<>(
            callSets.getResult(),
            callSets.getCurrentPageSize(),
            callSets.getCurrentPageNum());

        return ResponseEntity.ok(payload);
    }


    /**
     *  Download genotypes in varianset as file
     *
     * @param variantSetDbId    id of the variantset
     *
     * @return text/csv file
     * @throws GobiiException when it is a bad request or service error.
     */
    @ApiOperation(
        value = "Download Genotypes in VariantSet",
        notes = "Download of all the genotype calls in a given VariantSet",
        tags = {"VariantSets"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="Download Genotypes in VariantSet")
            })
        }
    )
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header", dataType = "string")
    })
    @ApiResponses(
        value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 404, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
        }
    )
    @GetMapping(
        value="/{variantSetDbId:[\\d]+}/calls/download",
        produces = "text/csv")
    public ResponseEntity<ResponseBodyEmitter> downloadGenotypes(
        @ApiParam(value = "Id of the variantset to download")
        @PathVariable("variantSetDbId") Integer variantSetDbId,
        HttpServletRequest request
    ) throws GobiiException {

        String ifNoneMatch = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        String etag = variantSetsService.getEtag(variantSetDbId);

        if(ifNoneMatch != null && ifNoneMatch.equals(etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).body(null);
        }

        //Giving Response emitter to finish the download within 30 mins.
        //The request thread will be terminated once 30 mins is done.
        ResponseBodyEmitter emitter = new ResponseBodyEmitter((long)1800000);

        String cropType;
        try {
            cropType =  CropRequestAnalyzer.getGobiiCropType(request);
        }
        catch (Exception e) {
            throw new GobiiException("Unable to read crop type from request");
        }

        String threadName = "gdm-executor-service-thread;cropType:"+cropType;
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat(threadName+";%d")
            .build();

        ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);

        executor.execute(() -> {
            try {
                int pageNum = 0;

                String name = Thread.currentThread().getName();

                String genotypesResult = genotypeCallsService.getGenotypeCallsAsString(
                    variantSetDbId,
                    pageNum);

                while(genotypesResult != null && genotypesResult.length() != 0) {

                    emitter.send(genotypesResult, MediaType.TEXT_PLAIN);

                    pageNum++;

                    genotypesResult = genotypeCallsService.getGenotypeCallsAsString(
                        variantSetDbId,
                        pageNum);

                }
                emitter.complete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.completeWithError(e);
                return;
            }
            emitter.complete();
        });

        return ResponseEntity
            .ok()
            .header(
                "Content-Disposition",
                "attachment; filename=" + variantSetDbId.toString() + ".csv"
            )
            .contentType(MediaType.parseMediaType("text/csv"))
            .eTag(etag)
            .body(emitter);
    }


}
