package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.brapi.GenotypeCallsService;
import org.gobiiproject.gobiidomain.services.brapi.VariantService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsDTO;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsResult;
import org.gobiiproject.gobiimodel.dto.brapi.VariantDTO;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiResult;
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

/**
 * Brapi REST endpoint for variants (markers and snps)
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/variants")
@CrossOrigin
@Api
@Slf4j
public class VariantsController {



    private final VariantService variantService;
    private final GenotypeCallsService genotypeCallsService;

    /**
     * Constructor
     *
     * @param variantService        {@link VariantService} instance
     * @param genotypeCallsService  {@link GenotypeCallsService} instance
     */
    @Autowired
    public VariantsController(final VariantService variantService,
                              final GenotypeCallsService genotypeCallsService) {
        this.variantService = variantService;
        this.genotypeCallsService = genotypeCallsService;
    }

    /**
     * Gets variants by given filter parameters.
     *
     * @param pageSize          Size of the page
     * @param pageToken         token to fetch the page. can be obtained
     *                          from nextPageToken field in previous response.
     * @param variantDbId       id of the variant (marker/snp)
     * @param variantSetDbId    id of the dataset
     * @return  Brapi list of markers
     * @throws GobiiException when it is a bad request or service error.
     */
    @ApiOperation(
        value = "List Variants",
        notes = "Lists Variants in GDM System.",
        tags = {"Variants"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="List Variants")
            })
        }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "",
            response = SwaggerResponseModels.VariantListResponse.class),
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
    public @ResponseBody
    ResponseEntity<BrApiMasterListPayload<VariantDTO>> getVariants(
        @ApiParam(value = "Size of the page")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize,
        @ApiParam(value = "Page Token to fetch a page. " +
            "Value is $metadata.pagination.nextPageToken form previous page.")
        @RequestParam(value = "pageToken", required = false) String pageToken,
        @ApiParam(value = "ID of the variant to be extracted")
        @RequestParam(value = "variantDbId", required = false) Integer variantDbId,
        @ApiParam(value = "ID of the variantSet to be extracted")
        @RequestParam(value = "variantSetDbId", required = false) Integer variantSetDbId
    ) throws GobiiException {
        try {

            PagedResult<VariantDTO> pagedResult = variantService.getVariants(
                pageSize,
                pageToken,
                variantDbId,
                variantSetDbId);

            BrApiMasterListPayload<VariantDTO>  payload =
                new BrApiMasterListPayload<>(
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
                GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                "Internal Server Error" + e.getMessage()
            );
        }
    }

    /**
     * Get variant by id.
     *
     * @param variantDbId id of the variant to fetch
     * @return Brapi result object with requested variant
     * @throws GobiiException when it is a bad request or service error
     */
    @ApiOperation(
        value = "Get Variant by variantDbId",
        notes = "Retrieves the Variant with variantDbId",
        tags = {"Variants"},
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name="summary", value="Get Variant by Id")
            })
        }
    )
    @ApiResponses(
        value = {
            @ApiResponse(code = 200, message = "",
                response = SwaggerResponseModels.VariantResponse.class),
            @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
        }
    )
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required = true, paramType = "header", dataType = "string"
        )
    })
    @GetMapping(value="/{variantDbId:[\\d]+}", produces = "application/json")
    public @ResponseBody ResponseEntity<BrApiMasterPayload<VariantDTO>>
    getVariantsByVariantDbId(
        @ApiParam(value = "ID of the Variant to be extracted", required = true)
        @PathVariable("variantDbId") Integer variantDbId
    ) {
        try {
            VariantDTO variantDTO = variantService.getVariantByVariantDbId(variantDbId);
            BrApiMasterPayload<VariantDTO> payload = new BrApiMasterPayload<>(variantDTO);
            return ResponseEntity.ok(payload);
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
     * Gets list of genotype calls for given variant id.
     *
     * @param variantDbId - Marker run Id.
     * @param pageSize - Size of the page to fetched.
     * @param pageToken         token to fetch the page. can be obtained
     *                          from nextPageToken field in previous response.
     * @return BrApi list of genotype calls.
     * @throws GobiiException when it is a brapi request or service error.
     */
    @ApiOperation(
        value = "List Genotypes by Variant", notes = "List of all the genotype calls " +
        "in the marker with variantDbId",
        tags = {"Variants"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="Get Genotypes by Variant")
        })
    }
    )
    @ApiResponses(
        value = {
            @ApiResponse(code = 200, message = "",
                response = SwaggerResponseModels.GenotypeCallsListResponse.class),
            @ApiResponse(code = 400, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 401, message = "", response = ErrorPayload.class),
            @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
        }
    )
    @ApiImplicitParams({
        @ApiImplicitParam(
            name="Authorization", value="Authentication Token",
            required=true, paramType = "header", dataType = "string"
        )
    })
    @RequestMapping(value="/{variantDbId}/calls", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<BrApiMasterPayload<GenotypeCallsResult>>
    getCallsByVariant(
        @ApiParam(value = "Id for marker to be fetched")
        @PathVariable(value="variantDbId") Integer variantDbId,
        @ApiParam(value = "Page Token to fetch a page. " +
            "Value is $metadata.pagination.nextPageToken from previous page.")
        @RequestParam(value = "pageToken", required = false) String pageToken,
        @ApiParam(value = "Size of the page to be fetched. Default is 1000.")
        @RequestParam(value = "pageSize", required = false,
            defaultValue = BrapiDefaults.pageSize) Integer pageSize
    ) throws GobiiException {

        try {

            PagedResultTyped<GenotypeCallsResult> genotypeCallsPaged =
                genotypeCallsService.getGenotypeCallsByVariantDbId(
                    variantDbId,
                    pageSize,
                    pageToken);

            BrApiMasterPayload<GenotypeCallsResult> payload =
                new BrApiMasterPayload<>(
                    genotypeCallsPaged.getResult(),
                    genotypeCallsPaged.getCurrentPageSize(),
                    genotypeCallsPaged.getNextPageToken());

            return ResponseEntity.ok(payload);
        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            throw new GobiiException(
                GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                "Internal Server Error" + e.getMessage());
        }
    }


}
