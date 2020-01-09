// ************************************************************************
// (c) 2016 GOBii Projects
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiiweb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.annotations.*;
import org.gobiiproject.gobidomain.async.SearchExtract;
import org.gobiiproject.gobidomain.services.*;
import org.gobiiproject.gobiiapimodel.payload.sampletracking.BrApiMasterListPayload;
import org.gobiiproject.gobiiapimodel.payload.sampletracking.BrApiMasterPayload;
import org.gobiiproject.gobiiapimodel.payload.sampletracking.BrApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gobiiproject.gobidomain.services.PingService;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiibrapi.calls.calls.BrapiResponseCalls;
import org.gobiiproject.gobiibrapi.calls.calls.BrapiResponseMapCalls;
import org.gobiiproject.gobiibrapi.calls.germplasm.BrapiResponseGermplasmByDbId;
import org.gobiiproject.gobiibrapi.calls.germplasm.BrapiResponseMapGermplasmByDbId;
import org.gobiiproject.gobiibrapi.calls.login.BrapiRequestLogin;
import org.gobiiproject.gobiibrapi.calls.login.BrapiResponseLogin;
import org.gobiiproject.gobiibrapi.calls.login.BrapiResponseMapLogin;
import org.gobiiproject.gobiibrapi.calls.markerprofiles.allelematrices.BrapiResponseAlleleMatrices;
import org.gobiiproject.gobiibrapi.calls.markerprofiles.allelematrices.BrapiResponseMapAlleleMatrices;
import org.gobiiproject.gobiibrapi.calls.markerprofiles.allelematrixsearch.BrapiResponseMapAlleleMatrixSearch;
import org.gobiiproject.gobiibrapi.calls.markerprofiles.markerprofiles.BrapiResponseMapMarkerProfiles;
import org.gobiiproject.gobiibrapi.calls.markerprofiles.markerprofiles.BrapiResponseMarkerProfilesMaster;
import org.gobiiproject.gobiibrapi.calls.studies.observationvariables.BrapiResponseMapObservationVariables;
import org.gobiiproject.gobiibrapi.calls.studies.observationvariables.BrapiResponseObservationVariablesMaster;
import org.gobiiproject.gobiibrapi.calls.studies.search.BrapiRequestStudiesSearch;
import org.gobiiproject.gobiibrapi.calls.studies.search.BrapiResponseMapStudiesSearch;
import org.gobiiproject.gobiibrapi.calls.studies.search.BrapiResponseStudiesSearch;
import org.gobiiproject.gobiibrapi.core.common.BrapiAsynchStatus;
import org.gobiiproject.gobiibrapi.core.common.BrapiMetaData;
import org.gobiiproject.gobiibrapi.core.common.BrapiPagination;
import org.gobiiproject.gobiibrapi.core.common.BrapiRequestReader;
import org.gobiiproject.gobiibrapi.core.responsemodel.BrapiResponseEnvelope;
import org.gobiiproject.gobiibrapi.core.responsemodel.BrapiResponseEnvelopeMaster;
import org.gobiiproject.gobiibrapi.core.responsemodel.BrapiResponseEnvelopeMasterDetail;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.dto.entity.auditable.VariantSetDTO;
import org.gobiiproject.gobiimodel.dto.entity.noaudit.*;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.GobiiExtractorInstruction;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.types.RestMethodType;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.automation.RestResourceLimits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * This controller is only for BRAPI v1. compliant calls. It consumes the gobii-brapi module.
 * <p>
 * BRAPI responses all contain a "metadata" and a "result" key. The "result" key's value can take
 * three forms:
 * 1) A "data" property that is an array of items of the same type (e.g., /studies-search)
 * 2) A set of arbitrary properties (i.e., a specific type in Java) (e.g., /germplasm/{id})
 * 3) A set of arbitrary properties and a "data" property (e.g., /studies/{studyDbId}/observationVariables)
 * <p>
 * Type 3) is for a master-detail scenario: you have a master record that is related to one or more detail
 * records. Type 2) is just the master. Type 1) is just the detail records.
 * <p>
 * The classes that support this API are as follows:
 * <p>
 * BrapiResponseEnvelope: This is the base class for all response envelopes: it is responsible for the metadata
 * key of the response
 * <p>
 * BrapiResponseEnvelopeMasterDetail and BrapiResponseEnvelopeMaster derive from BrapiResponseEnvelope.
 * They are responsible for the result key of the response.
 * <p>
 * BrapiResponseEnvelopeMasterDetail: This class is used for types 1) and 3). It is
 * type-parmaeterized for the pojo that will be the value of the result key; the pojo must extend  BrapiResponseDataList;
 * this way, the pojo will always have a setData() method for specifying the list content of the data key.
 * <p>
 * In the case of (1):
 * The result pojo will be a class with no properties that derives from BrapiResponseDataList. The maping
 * class will use the pojo's setData() method to specify the list that will constitutes the data key
 * of the result key.
 * <p>
 * In the case of (3):
 * Thee result pojo will have its own properties; the pojo's own properties will constitute the values
 * of the result key; the pojos setData() method will be used to specify the list that constitutes the
 * data key;
 * <p>
 * BrapiResponseEnvelopeMaster: This class is used for type 2). It is type-parameterized
 * for an arbitrary pojo. Because type 2 responses do not have a data key, the pojo does
 * _not_ extend BrapiResponseDataList. Its properties will be the values of the response's result key.
 * <p>
 * The calls namespace of gobii-brapi is organized as the brapi API is organized. In the descriptions
 * below, <CallName> refers to the BRAPI call name.
 * Each call contains several sorts of classes:
 * ---- POJOs named BrapiResponse<CallName>: these are the arbitrary pojos that
 * type-parameterize BrapiResponseElvelopeMasterDetail and BrapiResponseElvelopeMaster
 * ---- POJOs named BrapiRequest<CallName>: these are POST/PUT bodies for which the
 * relevant methods in here the controller have @RequestBody parameters (e.g., BrapiRequestStudiesSearch)
 * ---- POJOs named BrapiResponseMap<CallName>: Right now these clases create dummy responses; the real
 * implementations of these classes will consume classes from the gobii-domain project (i.e., the Service
 * classes): they will get data from gobii in terms of gobii DTOs and convert the DTOs in to the
 * BRAPI POJOs
 * <p>
 * The BrapiController does the following:
 * 0.   If there is a post body, deserializes it to the appropriate post pojo;
 * i.   Instantiates a BrapiResponseEnvelopeMaster or BrapiResponseEnvelopeMasterDetail;
 * ii.  Uses the map classes for each call to get the pojo that the call will use for its payload;
 * iii. Assigns the pojo to the respective respone envelope;
 * iv.  Serialies the content of the response envelope;
 * v.   Sets the reponse of the method to the serialized content.
 * <p>
 * Note that this controller receives and sends plain String json data. This approach is different
 * from the gobii api. The BrapiResponseEnvelopeList and BrapiResponseEnvelopeMaster are serialzied to
 * json and sent over the wire in this way rather than letting the Jackson embedded through Spring do
 * the job automatically. This approach is more traditionally the web service way of doing things.
 */
@Scope(value = "request")
@Controller
@Api()
@EnableAsync
@RequestMapping(GobiiControllerType.SERVICE_PATH_BRAPI)
@CrossOrigin
public class BRAPIIControllerV1 {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BRAPIIControllerV1.class);

    private final Integer brapiDefaultPageSize = 1000;

    @Autowired
    private PingService pingService = null;


    @Autowired
    private BrapiResponseMapStudiesSearch brapiResponseMapStudiesSearch = null;


    @Autowired
    private BrapiResponseMapAlleleMatrixSearch brapiResponseMapAlleleMatrixSearch = null;

    @Autowired
    private BrapiResponseMapMarkerProfiles brapiResponseMapMarkerProfiles = null;


    @Autowired
    private BrapiResponseMapAlleleMatrices brapiResponseMapAlleleMatrices = null;

    @Autowired
    private DnaRunService dnaRunService = null;

    @Autowired
    private MarkerBrapiService markerBrapiService = null;

    @Autowired
    private GenotypeCallsService genotypeCallsService = null;

    @Autowired
    private DatasetBrapiService dataSetBrapiService = null;

    @Autowired
    private SearchExtract searchExtract = null;

    @Autowired
    private ConfigSettingsService configSettingsService;

    @Autowired
    private MapsetBrapiService mapsetBrapiService;

    @Autowired
    private SamplesBrapiService samplesBrapiService;

    @Autowired
    private VariantSetsService variantSetsService;

    private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);


    private class CallSetResponse extends BrApiMasterPayload<DnaRunDTO>{}
    private class CallSetListResponse extends BrApiMasterPayload<BrApiResult<DnaRunDTO>>{}
    private class GenotypeCallsResponse extends BrApiMasterPayload<GenotypeCallsDTO>{}
    private class GenotypeCallsListResponse extends BrApiMasterPayload<BrApiResult<GenotypeCallsDTO>>{}
    private class VariantResponse extends BrApiMasterPayload<MarkerBrapiDTO>{}
    private class VariantListResponse extends BrApiMasterPayload<BrApiResult<MarkerBrapiDTO>>{}
    private class VariantSetResponse extends  BrApiMasterPayload<DataSetBrapiDTO>{}
    private class VariantSetListResponse extends BrApiMasterPayload<BrApiResult<DataSetBrapiDTO>>{}


    /**
     * List all BrApi compliant web services in GDM system
     *
     * @param request - request object
     * @return Json object with list of brapi calls in GDM
     * @throws Exception
     */
    @RequestMapping(value = "/serverinfo",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(
            value = "Get ServerInfo",
            notes = "List of all calls",
            tags = {"ServerInfo"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="ServerInfo"),
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful",
                            response = BrapiResponseMapCalls.class
                    )
            }
    )
    @ResponseBody
    public ResponseEntity getCalls(
            HttpServletRequest request) throws Exception {

        BrapiResponseEnvelopeMasterDetail<BrapiResponseCalls> brapiResponseEnvelopeMasterDetail =
                new BrapiResponseEnvelopeMasterDetail<>();
        try {

            BrapiResponseMapCalls brapiResponseMapCalls = new BrapiResponseMapCalls(request);

            BrapiResponseCalls brapiResponseCalls = brapiResponseMapCalls.getBrapiResponseCalls();

            brapiResponseEnvelopeMasterDetail.setResult(brapiResponseCalls);

        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseEnvelopeMasterDetail.getBrapiMetaData().addStatusMessage("exception", message);

        } catch (Exception e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseEnvelopeMasterDetail.getBrapiMetaData().addStatusMessage("exception", message);

        }

        return ResponseEntity.ok(brapiResponseEnvelopeMasterDetail);

    }


    /**
     * Endpoint for authenticating users against GDM system
     * @param loginRequestBody - BrAPI defined login request body
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/token",
            method = RequestMethod.POST,
            produces = "application/json")
    @ApiOperation(
            value = "Authentication",
            notes = "Returns a API Key if authentication is successful",
            tags = {"Authentication"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Authentication"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String postLogin(@RequestBody String loginRequestBody,
                            HttpServletResponse response) throws Exception {

        String returnVal;

        BrapiResponseLogin brapiResponseLogin = null;

        try {

            BrapiRequestReader<BrapiRequestLogin> brapiRequestReader = new BrapiRequestReader<>(
                    BrapiRequestLogin.class);

            BrapiRequestLogin brapiRequestLogin = brapiRequestReader.makeRequestObj(loginRequestBody);

            BrapiResponseMapLogin brapiResponseMapLogin = new BrapiResponseMapLogin();

            brapiResponseLogin = brapiResponseMapLogin.getLoginInfo(brapiRequestLogin, response);


            brapiResponseLogin.getBrapiMetaData().setPagination(new BrapiPagination(
                    1,
                    1,
                    1,
                    0
            ));

        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseLogin.getBrapiMetaData().addStatusMessage("exception", message);

        } catch (Exception e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseLogin.getBrapiMetaData().addStatusMessage("exception", message);
        }

        returnVal = objectMapper.writeValueAsString(brapiResponseLogin);

        return returnVal;

    }


    /**
     * BrAPI v1.1 endpoint for searching studies
     * @param studiesRequestBody
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/studies-search",
            method = RequestMethod.POST,
            produces = "application/json")
    @ApiOperation(
            value = "Search studies **deprecated in v1.3",
            notes = "Search for studies ",
            tags = {"Studies"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Search Studies**"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String getStudies(@RequestBody String studiesRequestBody) throws Exception {

        String returnVal;

        BrapiResponseEnvelopeMasterDetail<BrapiResponseStudiesSearch> BrapiResponseEnvelopeMasterDetail =
                new BrapiResponseEnvelopeMasterDetail<>();

        try {

            BrapiRequestReader<BrapiRequestStudiesSearch> brapiRequestReader = new BrapiRequestReader<>(BrapiRequestStudiesSearch.class);
            BrapiRequestStudiesSearch brapiRequestStudiesSearch = brapiRequestReader.makeRequestObj(studiesRequestBody);

            Integer requestedPageSize = brapiRequestStudiesSearch.getPageSize();

            BrapiResponseStudiesSearch brapiResponseStudySearch = brapiResponseMapStudiesSearch.getBrapiResponseStudySearch(brapiRequestStudiesSearch);

            BrapiResponseEnvelopeMasterDetail.setResult(brapiResponseStudySearch);

            Integer numberOfHits = BrapiResponseEnvelopeMasterDetail.getResult().getData().size();

            Integer reportedPageSize;
            Integer totalPages;
            if (requestedPageSize > numberOfHits) {
                reportedPageSize = numberOfHits;
                totalPages = 1;
            } else {
                reportedPageSize = requestedPageSize;
                totalPages = numberOfHits / reportedPageSize; // get the whole part of the result
                if (numberOfHits % reportedPageSize > 0) {   // if there's a remainder, there's an additional page
                    totalPages += 1;
                }
            }


            BrapiResponseEnvelopeMasterDetail.getBrapiMetaData().setPagination(new BrapiPagination(
                    numberOfHits,
                    reportedPageSize,
                    totalPages,
                    0
            ));

        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            BrapiResponseEnvelopeMasterDetail.getBrapiMetaData().addStatusMessage("exception", message);

        } catch (Exception e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            BrapiResponseEnvelopeMasterDetail.getBrapiMetaData().addStatusMessage("exception", message);
        }

        returnVal = objectMapper.writeValueAsString(BrapiResponseEnvelopeMasterDetail);

        return returnVal;
    }

    @RequestMapping(value = "/germplasm/{studyDbId}",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(
            value = "Get germplasm by study db id.",
            notes = "Get germplasm by study db id.",
            tags = {"BrAPI"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Germplasm : studyDbId"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String getGermplasmByDbId(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @ApiParam(value = "Study DB Id", required = true)
                                     @PathVariable Integer studyDbId
    ) throws Exception {


        BrapiResponseEnvelopeMaster<BrapiResponseGermplasmByDbId> responseEnvelope
                = new BrapiResponseEnvelopeMaster<>();

        String returnVal;

        try {

            BrapiResponseMapGermplasmByDbId brapiResponseMapGermplasmByDbId = new BrapiResponseMapGermplasmByDbId();

            // extends BrapiMetaData, no list items
            BrapiResponseGermplasmByDbId brapiResponseGermplasmByDbId = brapiResponseMapGermplasmByDbId.getGermplasmByDbid(studyDbId);

            responseEnvelope.setResult(brapiResponseGermplasmByDbId);


        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            responseEnvelope.getBrapiMetaData().addStatusMessage("exception", message);

        } catch (Exception e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            responseEnvelope.getBrapiMetaData().addStatusMessage("exception", message);

        }

        returnVal = objectMapper.writeValueAsString(responseEnvelope);

        return returnVal;

    }

    @RequestMapping(value = "/studies/{studyDbId}/observationVariables",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(
            value = "List all observation variables by styudyDbId",
            notes = "List all observation variables for given study db id",
            tags = {"Studies"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="ObservationVariables"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String getObservationVariables(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @ApiParam(value = "Study DB Id", required = true)
                                          @PathVariable Integer studyDbId) throws Exception {

        BrapiResponseEnvelopeMasterDetail<BrapiResponseObservationVariablesMaster> responseEnvelope
                = new BrapiResponseEnvelopeMasterDetail<>();

        String returnVal;

        try {

            BrapiResponseMapObservationVariables brapiResponseMapObservationVariables = new BrapiResponseMapObservationVariables();

            BrapiResponseObservationVariablesMaster brapiResponseObservationVariablesMaster = brapiResponseMapObservationVariables.gerObservationVariablesByStudyId(studyDbId);

            responseEnvelope.setResult(brapiResponseObservationVariablesMaster);

        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            responseEnvelope.getBrapiMetaData().addStatusMessage("exception", message);

        } catch (Exception e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            responseEnvelope.getBrapiMetaData().addStatusMessage("exception", message);

        }

        returnVal = objectMapper.writeValueAsString(responseEnvelope);

        return returnVal;
    }


    @RequestMapping(value = "/allelematrices",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(
            value = "List Allele Matrices **deprecated in v2.0",
            notes = "Get allele matrices by given study db id",
            tags = {"AlleleMatrices"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="AlleleMatrices**"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String getAlleleMatrices(@ApiParam(value = "Study DB Id", required = false)
                                    @RequestParam("studyDbId") Optional<String> studyDbIdd,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {

        String returnVal;

        BrapiResponseEnvelopeMasterDetail<BrapiResponseAlleleMatrices> BrapiResponseEnvelopeMasterDetail =
                new BrapiResponseEnvelopeMasterDetail<>();

        try {

            BrapiResponseAlleleMatrices brapiResponseAlleleMatrices;

            if (studyDbIdd.isPresent()) {
                Integer studyDbIdAsInteger = Integer.parseInt(studyDbIdd.get());
                brapiResponseAlleleMatrices = brapiResponseMapAlleleMatrices.getBrapiResponseAlleleMatricesItemsByStudyDbId(studyDbIdAsInteger);
            } else {
                brapiResponseAlleleMatrices = brapiResponseMapAlleleMatrices.getBrapiResponseAlleleMatrices();
            }

            BrapiResponseEnvelopeMasterDetail.setResult(brapiResponseAlleleMatrices);

        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            BrapiResponseEnvelopeMasterDetail.getBrapiMetaData().addStatusMessage("exception", message);

        } catch (Exception e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            BrapiResponseEnvelopeMasterDetail.getBrapiMetaData().addStatusMessage("exception", message);
        }

        returnVal = objectMapper.writeValueAsString(BrapiResponseEnvelopeMasterDetail);

        return returnVal;
    }


    @RequestMapping(value = {"/allelematrices-search"},
            method = {RequestMethod.POST},
            produces = "application/json")
    @ApiOperation(
            value = "Search Allele Matrix **deprecated in v2.0",
            notes = "Search allele matrix using marker profiles",
            tags = {"AlleleMatrices"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="AlleleMatricesSearch**"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String getAlleleMatrices(
            @RequestBody AlleleMatricesSearchDTO alleleMatricesRequest ,
                                  HttpServletRequest request,
                                  HttpServletResponse response
    ) throws Exception {

        BrapiResponseEnvelopeMaster<Map<String, String>> brapiResponseEnvelope = new BrapiResponseEnvelopeMaster<>();

        List<String> matrixDbIdList = alleleMatricesRequest.getMatrixDbId();
        List<String> markerprofileDbIdList = alleleMatricesRequest.getMarkerProfileDbId();

        Optional<String> matrixDbId = Optional.empty();
        Optional<String> markerprofileDbId = Optional.empty();

        if(matrixDbIdList != null) {
            matrixDbId = Optional.of(String.join(",", matrixDbIdList));
        }

        if(markerprofileDbIdList != null) {
            markerprofileDbId = Optional.of(String.join(",", markerprofileDbIdList));
        }

        try {

            String jobId = this.alleleMatrix(matrixDbId, markerprofileDbId, request, response);
            if(jobId == null || jobId.isEmpty()) {
                brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("400", "failed to submit job");
            }
            else {

                BrapiAsynchStatus asynchStatus = new BrapiAsynchStatus();

                asynchStatus.setAsynchId(jobId);

                asynchStatus.setStatus("PENDING");

                brapiResponseEnvelope.getBrapiMetaData().setAsynchStatus(asynchStatus);

                brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("2002", "Asynchronous call in progress");
            }

        }
        catch (GobiiException gE) {

            brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("400", gE.getMessage());
        }

        brapiResponseEnvelope.setResult(new HashMap<>());

        return objectMapper.writeValueAsString(brapiResponseEnvelope);

    }

    @RequestMapping(value = {"/allelematrix-search"},
            method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    @ApiOperation(
            value = "Search Allele Matrix **deprecated in v1.3",
            notes = "Search allele matrix using marker profiles",
            tags = {"BrAPI"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="AlleleMatrixSearch"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String postAlleleMatrix(@ApiParam(value = "Matrix DB Id", required = false)
                                  @RequestParam("matrixDbId") Optional<String> matrixDbId,
                                  @ApiParam(value = "Marker Profile Id", required = false)
                                  @RequestParam("markerprofileDbId") Optional<String> markerprofileDbId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {



        BrapiResponseEnvelope brapiResponseEnvelope = new BrapiResponseEnvelope();

        try {

            String jobId = this.alleleMatrix(matrixDbId, markerprofileDbId, request, response);

            if(jobId == null || jobId.isEmpty()) {
                brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("exception", "failed to submit job");
            }
            else {
                brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("asynchid", jobId);
            }


        }
        catch (GobiiException gE) {

            brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("exception", gE.getMessage());
        }

        return objectMapper.writeValueAsString(brapiResponseEnvelope);

    }


    public String alleleMatrix(Optional<String> matrixDbId,
                               Optional<String> markerprofileDbId,
                               HttpServletRequest request,
                               HttpServletResponse response) throws Exception {

        String returnVal = "";

        try {

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);
            if (matrixDbId.isPresent() == markerprofileDbId.isPresent()) {
                String message = "Incorrect request format. At least one of matrixDbId or markerprofileDbId should be specified.";
                throw new GobiiException(message);
            } else if (matrixDbId.isPresent()) {

                List<String> matrixDbIdList = Arrays.asList(matrixDbId.get().split(","));

                if (matrixDbIdList.size() > 1) {

                    String message = "Incorrect request format. Only one matrixDbId is supported at the moment.";


                    throw new GobiiException(message);

                }

                returnVal = brapiResponseMapAlleleMatrixSearch.searchByMatrixDbId(cropType, matrixDbIdList.get(0));

            } else {

                List<String> externalCodes = Arrays.asList(markerprofileDbId.get().split(","));

                returnVal =  brapiResponseMapAlleleMatrixSearch.searchByExternalCode(cropType, externalCodes);
            }
        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            throw new GobiiException(message);


        } catch (Exception e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();
            throw new GobiiException(message);

        }
        return returnVal;
    }


    @RequestMapping(value = {"/allelematrix-search/status/{jobId}"},
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(
            value = "Get Allele Matrix Job status **deprecated in v1.3",
            notes = "Get allele matrix Job status",
            tags = {"BrAPI"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="AlleleMatrix.status : jobId**"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String getAlleleMatrixStatus(@ApiParam(value = "Job Id", required = true)
                                        @PathVariable("jobId") String jobId,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {

        String returnVal = null;

        BrapiResponseEnvelope brapiResponseEnvelope = new BrapiResponseEnvelope();
        try {

            BrapiMetaData metaData = new BrapiMetaData();

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);

            String jobStatus = brapiResponseMapAlleleMatrixSearch.getStatus(cropType, jobId, request);

            if(jobStatus == null) {
                throw new GobiiException("Job id not valid");
            }

            metaData.addStatusMessage("aynchstatus", jobStatus);

            if(jobStatus.equals("FINISHED")) {

                List<String> dataFiles = brapiResponseMapAlleleMatrixSearch.getDatFiles(cropType, jobId, request);

                metaData.setDatafiles(dataFiles);

            }


            brapiResponseEnvelope.setBrapiMetaData(metaData);


        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("error", message);

        } catch (Exception e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("error", message);
        }

        returnVal = objectMapper.writeValueAsString(brapiResponseEnvelope);

        return returnVal;
    }

    @RequestMapping(value = {"/allelematrices-search/{jobId}"},
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(
            value = "Get Allele Matrices Job status **deprecated in v2.0",
            notes = "Get allele matrix Job status",
            tags = {"BrAPI"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="AlleleMatrix.status : jobId**"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String getAlleleMatricesStatus(@ApiParam(value = "Job Id", required = true)
                                        @PathVariable("jobId") String jobId,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {

        String returnVal = null;

        BrapiResponseEnvelopeMaster<Map<String, String>> brapiResponseEnvelope = new BrapiResponseEnvelopeMaster<>();

        try {

            BrapiMetaData metaData = new BrapiMetaData();
            BrapiAsynchStatus asynchStatus = new BrapiAsynchStatus();

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);

            String jobStatus = brapiResponseMapAlleleMatrixSearch.getStatus(cropType, jobId, request);

            if(jobStatus == null) {
                throw new GobiiException("Job id not valid");
            }

            asynchStatus.setStatus(jobStatus);
            asynchStatus.setAsynchId(jobId);

            metaData.setAsynchStatus(asynchStatus);

            if(jobStatus.equals("FINISHED")) {

                metaData.addStatusMessage("200", jobStatus);

                List<String> dataFiles = brapiResponseMapAlleleMatrixSearch.getDatFiles(cropType, jobId, request);

                metaData.setDatafiles(dataFiles);

            }
            else {
                metaData.addStatusMessage("2002", jobStatus);
            }

            brapiResponseEnvelope.setBrapiMetaData(metaData);


        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("400", message);

        } catch (Exception e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("400", message);
        }

        brapiResponseEnvelope.setResult(new HashMap<>());

        returnVal = objectMapper.writeValueAsString(brapiResponseEnvelope);

        return returnVal;
    }

    /***
     * Returns a stream for the at at the path specified by the query parameter. This method is not
     * defined by the BRAPI spec, nor should it be: the spec only stipulates that entries in the files
     * section of the metadata object be accessible to the client.
     *
     * @param fqpn: fully qualified path of file to download
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/files",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiOperation(
            value = "List all files **deprectaed",
            notes = "List all the files in a given path.",
            tags = {"Files"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Files**"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public void getFile(@ApiParam(value = "Fully qualified path name", required = true)
                        @RequestParam("fqpn") String fqpn,
                        HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        try {

            response.setContentType("application/text");
            InputStream inputStream = new FileInputStream(fqpn);
            // copy it to response's OutputStream
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();

        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file " + fqpn + "to output stream: " + ex.getMessage());
        }
    }


    @RequestMapping(value = "/markerprofiles",
            method = {RequestMethod.GET},
            produces = "application/json")
    @ApiOperation(
            value = "List all Marker Profiles",
            notes = "List all Marker Profiles",
            tags = {"MarkerProfiles"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="MarkerProfiles"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String getMarkerProfile(@ApiParam(value = "Germplasm DB Id", required = true)
                                   @RequestParam("germplasmDbId") String germplasmDbId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        return this.markerProfile(germplasmDbId, request, response);
    }

    @RequestMapping(value = "/markerprofiles",
            method = {RequestMethod.POST},
            produces = "application/json")
    @ApiOperation(
            value = "Create Marker Profiles",
            notes = "Create Marker Profiles",
            tags = {"MarkerProfiles"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="MarkerProfiles"),
                    })
            }
            ,
            hidden = true
    )
    @ResponseBody
    public String postMarkerProfile(@ApiParam(value = "Germplasm DB Id", required = true)
                                    @RequestParam("germplasmDbId") String germplasmDbId,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        return this.markerProfile(germplasmDbId, request, response);
    }

    public String markerProfile(String germplasmDbId,
                                HttpServletRequest request,
                                HttpServletResponse response) throws Exception {

        BrapiResponseEnvelopeMasterDetail<BrapiResponseMarkerProfilesMaster> brapiResponseEnvelope
                = new BrapiResponseEnvelopeMasterDetail<>();

        String returnVal;
        try {

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);
            if (!LineUtils.isNullOrEmpty(germplasmDbId)) {

                brapiResponseEnvelope.setResult(brapiResponseMapMarkerProfiles.getBrapiResponseMarkerProfilesByGermplasmId(germplasmDbId));

            } else {
                String message = "Incorrect request format: germplasmDbId must be specified";
                brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("exception", message);
            }
        } catch (GobiiException e) {

            String message = e.getMessage() + ": " + e.getCause() + ": " + e.getStackTrace().toString();

            brapiResponseEnvelope.getBrapiMetaData().addStatusMessage("exception", message);

        }

        returnVal = objectMapper.writeValueAsString(brapiResponseEnvelope);

        return returnVal;
    }

    /**
     * Lists the dnaruns by page size and page token
     *
     * @param pageSize - Page size set by the user. If page size is more than maximum allowed
     *                 page size, then the response will have maximum page size
     * @return Brapi response with list of dna runs/call sets
     */
    @ApiOperation(
            value = "List all Callsets",
            notes = "List of all Callsets.",
            tags = {"Callsets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Callsets")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of CallSets",
                            response = CallSetListResponse.class
                    )
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required=true,
                paramType = "header", dataType = "string")
    })
    @RequestMapping(value="/callsets", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getCallSets(
            @ApiParam(value = "Page Token to fetch a page. " +
                    "nextPageToken form previous page's meta data should be used." +
                    "If pageNumber is specified pageToken will be ignored. " +
                    "pageToken can be used to sequentially get pages faster. " +
                    "When an invalid pageToken is given the page will start from beginning.")
            @RequestParam(value = "pageToken", required = false) Integer pageToken,
            @ApiParam(value = "Size of the page to be fetched. Default is 1000. Maximum page size is 1000")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "ID of the CallSet to be retrieved.")
            @RequestParam(value = "callSetDbId", required = false) Integer callSetDbId,
            @ApiParam(value = "The human readable name of the CallSet to be retrieved.")
            @RequestParam(value = "callSetName", required = false) String callSetName,
            @ApiParam(value = "The ID of the VariantSet to be retrieved.")
            @RequestParam(value = "variantSetDbId", required = false) String variantSetDbId,
            @ApiParam(value = "The ID of the Sample to be retrieved.")
            @RequestParam(value = "sampleDbId", required = false) String sampleDbId,
            @ApiParam(value = "The ID of the Germplasm to be retrieved.")
            @RequestParam(value = "germplasmDbId", required = false) String germplasmDbId,
            @ApiParam(value = "The ID of the study to be retrieved.")
            @RequestParam(value = "studyDbId", required = false) String studyDbId,
            @ApiParam(value = "The name of the sample to be retrieved.")
            @RequestParam(value = "sampleName", required = false) String sampleName
    ) {
        try {

            DnaRunDTO dnaRunDTOFilter = new DnaRunDTO();

            if (callSetDbId != null) {
                dnaRunDTOFilter.setCallSetDbId(callSetDbId);
            }

            if (callSetName != null) {
                dnaRunDTOFilter.setCallSetName(callSetName);
            }

            if (variantSetDbId != null) {
                List<Integer> variantDbArr = new ArrayList<>();
                variantDbArr.add(Integer.parseInt(variantSetDbId));
                dnaRunDTOFilter.setVariantSetIds(variantDbArr);
            }

            if (sampleDbId != null) {
                dnaRunDTOFilter.setSampleDbId(Integer.parseInt(sampleDbId));
            }

            if (germplasmDbId != null) {
                dnaRunDTOFilter.setGermplasmDbId(Integer.parseInt(germplasmDbId));
            }

            if (studyDbId != null) {
                dnaRunDTOFilter.setStudyDbId(Integer.parseInt(studyDbId));
            }

            if (sampleName != null) {
                dnaRunDTOFilter.setSampleName(sampleName);
            }

            Integer maxPageSize = RestResourceLimits.getResourceLimit(
                    RestResourceId.GOBII_DNARUN,
                    RestMethodType.GET
            );

            if(maxPageSize == null) {
                //As per brapi initial standards
                maxPageSize = 1000;
            }

            if (pageSize == null || pageSize > maxPageSize) {
                pageSize = maxPageSize;
            }

            List<DnaRunDTO> dnaRunList = dnaRunService.getDnaRuns(pageToken, pageSize, dnaRunDTOFilter);

            BrApiResult result = new BrApiResult();
            result.setData(dnaRunList);
            BrApiMasterPayload payload = new BrApiMasterPayload(result);

            if (dnaRunList.size() > 0) {
                payload.getMetadata().getPagination().setPageSize(dnaRunList.size());
                if(dnaRunList.size() >= pageSize) {
                    payload.getMetadata().getPagination().setNextPageToken(
                            dnaRunList.get(dnaRunList.size() - 1).getCallSetDbId().toString()
                    );
                }
            }

            return ResponseEntity.ok().contentType(
                    MediaType.APPLICATION_JSON).body(payload);

        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    "Internal Server Error" + e.getMessage());
        }
    }

    /**
     * Endpoint for getting a specific callset with a given callSetDbId
     *
     * @param callSetDbId ID of the requested callset
     * @return ResponseEntity with http status code specifying if retrieval of the callset is successful.
     * Response body contains the requested callset information
     */
    @ApiOperation(
            value = "Get callset by callsetId",
            notes = "Retrieves the Callset entity having the specified ID",
            tags = {"Callsets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Callsets : callSetDbId")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of CallSets", response = CallSetResponse.class)
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required = true,
            paramType = "header", dataType = "string"),
    })
    @RequestMapping(value="/callsets/{callSetDbId:[\\d]+}", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getCallSetsByCallSetDbId(
            @ApiParam(value = "ID of the Callset to be extracted", required = true)
            @PathVariable("callSetDbId") Integer callSetDbId) {

        Integer callSetDbIdInt;

        try {
            callSetDbIdInt = callSetDbId;
            DnaRunDTO dnaRunDTO = dnaRunService.getDnaRunById(callSetDbIdInt);
            BrApiMasterPayload<DnaRunDTO> payload = new BrApiMasterPayload<>(dnaRunDTO);

            return ResponseEntity.ok().contentType(
                    MediaType.APPLICATION_JSON).body(payload);
        }
        catch(Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "Entity does not exist");
        }

    }


    /**
     * Returns the list of genotypes calls in a given DNA run id.
     * It fetches calls in all the datasets where the dnarun_id is present.
     * The calls is paged.
     *
     * @param callSetDbId - DNA run Id.
     * @param pageSize - Size of the page to fetched.
     * @param pageToken - Page token to fetch the page. User will get the pageToken
     *                       from the nextPageToken parameter in the previous response.
     *
     * @return BrApi Response entity with list of genotypes calls for given dnarun id.
     * TODO: Add page number parameter to comply BrApi standards.
     */
    @ApiOperation(
            value = "List genotype calls",
            notes = "List of all the genotype calls in a given Dna run identified by Dna run Id",
            tags = {"Callsets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="GenotypeCalls")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of Genotype calls",
                            response = GenotypeCallsResponse.class
                    )
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name="Authorization", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string")
    })
    @RequestMapping(value="/callsets/{callSetDbId}/calls", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getCallsByCallset(
            @ApiParam(value = "Id for dna run to be fetched")
            @PathVariable(value="callSetDbId") String callSetDbId,
            @ApiParam(value = "Page Token to fetch a page. " +
                    "nextPageToken form previous page's meta data should be used." +
                    "If pageNumber is specified pageToken will be ignored. " +
                    "pageToken can be used to sequentially get pages faster. " +
                    "When an invalid pageToken is given the page will start from beginning.")
            @RequestParam(value = "pageToken", required = false) String pageToken,
            @ApiParam(value = "Size of the page to be fetched. Default is 1000. Maximum page size is 1000")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            HttpServletRequest request) throws Exception {

        Integer callSetDbIdInt;

        try {

            try {
                callSetDbIdInt = Integer.parseInt(callSetDbId);
            } catch (Exception e) {
                throw new GobiiException(
                        GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "Invalid dna run Id");
            }

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);

            Integer maxPageSize = RestResourceLimits.getResourceLimit(
                    RestResourceId.GOBII_DNARUN,
                    RestMethodType.GET);

            if(maxPageSize == null) {
                //As per brapi initial standards
                maxPageSize = 10000;
            }

            if (pageSize == null || pageSize > maxPageSize) {
                pageSize = maxPageSize;
            }

            List<GenotypeCallsDTO> genotypeCallsList = genotypeCallsService.getGenotypeCallsByDnarunId(
                    callSetDbIdInt, pageToken,
                    pageSize
            );

            BrApiMasterPayload<List<GenotypeCallsDTO>> payload = new BrApiMasterPayload<>(genotypeCallsList);

            if (genotypeCallsList.size() > 0) {
                payload.getMetadata().getPagination().setPageSize(genotypeCallsList.size());
                if (genotypeCallsList.size() >= pageSize) {
                    payload.getMetadata().getPagination().setNextPageToken(
                            genotypeCallsList.get(genotypeCallsList.size() - 1).getVariantDbId().toString()
                    );
                }
            }

            return ResponseEntity.ok(payload);
        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    "Internal Server Error" + e.getMessage());
        }
    }

    /**
     * Lists the variants by page size and page token
     *
     * @param pageSize - Page size set by the user. If page size is more than maximum allowed
     *                 page size, then the response will have maximum page sie
     * @return Brapi response with list of variants
     */
    @ApiOperation(
            value = "List all variants",
            notes = "List of all Variants",
            tags = {"Variants"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Variants")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of Variants",
                            response = VariantListResponse.class
                    )
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required=true,
            paramType = "header", dataType = "string")
    })
    @RequestMapping(value="/variants", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getVariants(
            @ApiParam(value = "Page Token to fetch a page. " +
                    "nextPageToken form previous page's meta data should be used." +
                    "If pageNumber is specified pageToken will be ignored. " +
                    "pageToken can be used to sequentially get pages faster. " +
                    "When an invalid pageToken is given the page will start from beginning.")
            @RequestParam(value = "pageToken", required = false) Integer pageToken,
            @ApiParam(value = "Size of the page to be fetched. Default is 1000. Maximum page size is 1000")
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "ID of the variant to be extracted")
            @RequestParam(value = "variantDbId", required = false) Integer variantDbId,
            @ApiParam(value = "ID of the variantSet to be extracted")
            @RequestParam(value = "variantSetDbId", required = false) Integer variantSetDbId,
            @ApiParam(value = "ID of the mapset to be retrieved")
            @RequestParam(value = "mapSetId", required = false) Integer mapSetId,
            @ApiParam(value = "Name of the mapset to be retrieved")
            @RequestParam(value = "mapSetName", required = false) String mapSetName
    ) {
        try {

            MarkerBrapiDTO markerBrapiDTOFilter = new MarkerBrapiDTO();

            if (variantDbId != null) {
                markerBrapiDTOFilter.setVariantDbId(variantDbId);
            }

            if (variantSetDbId != null) {
                List<Integer> variantDbArr = new ArrayList<>();
                variantDbArr.add(variantSetDbId);
                markerBrapiDTOFilter.setVariantSetDbId(variantDbArr);
            }

            if (mapSetId != null) {
                markerBrapiDTOFilter.setMapSetId(mapSetId);
            }

            if (mapSetName != null) {
                markerBrapiDTOFilter.setMapSetName(mapSetName);
            }

            Integer maxPageSize = RestResourceLimits.getResourceLimit(
                    RestResourceId.GOBII_MARKERS,
                    RestMethodType.GET
            );

            if (maxPageSize == null) {
                maxPageSize = 1000;
            }

            if (pageSize == null || pageSize >  maxPageSize) {
                pageSize = maxPageSize;
            }

            List<MarkerBrapiDTO> markerList = markerBrapiService.getMarkers(pageToken, pageNum,
                    pageSize, markerBrapiDTOFilter);

            BrApiResult result = new BrApiResult();
            result.setData(markerList);

            BrApiMasterPayload payload = new BrApiMasterPayload(result);

            if (markerList.size() > 0) {
                payload.getMetadata().getPagination().setPageSize(markerList.size());
                if (markerList.size() >= pageSize) {
                    payload.getMetadata().getPagination().setNextPageToken(
                            markerList.get(markerList.size() -1).getVariantDbId().toString()
                    );
                }
            }

            return ResponseEntity.ok().contentType(
                    MediaType.APPLICATION_JSON).body(payload);
        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    "Internal Server Error" + e.getMessage()
            );
        }
    }

    /**
     * Endpoint for getting a specific marker with a given markerDbId
     *
     * @param variantDbId ID of the requested marker
     * @return ResponseEntity with http status code specifying if retrieval of the marker is successful.
     * Response body contains the requested marker information
     */
    @ApiOperation(
            value = "Get a variant by variantDbId",
            notes = "Retrieves the Variant entity having the specified ID",
            tags = {"Variants"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Variants: variantDbId")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of Variant by Id",
                            response = VariantResponse.class
                    )
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required = true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value="/variants/{variantDbId:[\\d]+}", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getVariantsByVariantDbId(
            @ApiParam(value = "ID of the Variant to be extracted", required = true)
            @PathVariable("variantDbId") Integer variantDbId) {

        Integer variantDbIdInt;

        try {
            variantDbIdInt = variantDbId;

            MarkerBrapiDTO markerBrapiDTO = markerBrapiService.getMarkerById(variantDbIdInt);
            BrApiMasterPayload<MarkerBrapiDTO> payload = new BrApiMasterPayload<>(markerBrapiDTO);

            return ResponseEntity.ok().contentType(
                    MediaType.APPLICATION_JSON).body(payload);

        }
        catch (Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "Entity does not exist"
            );
        }
    }

    @RequestMapping(value="/samples", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getMaps(
            @RequestParam(value="sampleDbId", required=false) Integer sampleDbId,
            @RequestParam(value="observationUnitDbId", required=false) String observationUnitDbId,
            @RequestParam(value="germplasmDbId", required=false) Integer germplasmDbId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        try {

            if(page == null) {
                //First Page
                page = getDefaultBrapiPage();
            }

            if(pageSize == null) {
                //TODO: Using same resource limit as markers. But, Can be defined seperately
                pageSize = getDefaultPageSize(RestResourceId.GOBII_MARKERS);
            }

            List<SamplesBrapiDTO> samples = samplesBrapiService.getSamples(
                    page, pageSize,
                    sampleDbId, germplasmDbId,
                    observationUnitDbId);

            Map<String, Object> brapiResult = new HashMap<>();

            brapiResult.put("data", samples);

            BrApiMasterPayload<Map<String, Object>> payload = new BrApiMasterPayload(brapiResult);

            payload.getMetadata().getPagination().setCurrentPage(page);

            payload.getMetadata().getPagination().setPageSize(pageSize);


            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(payload);


        }
        catch(Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "Entity does not exist"
            );
        }
    }


    @ApiOperation(
            value = "List Maps",
            notes = "List Genome maps in the database",
            tags = {"Maps"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Maps")
                    })
            }
            ,
            hidden = true
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required = true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value="/maps", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getMaps(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "type", required = false) String mapType) throws GobiiException {

        try {

            if(page == null) {
                //First Page
                page = getDefaultBrapiPage();
            }

            if(pageSize == null) {
                //TODO: Using same resource limit as markers. But, Can be defined seperately
                pageSize = getDefaultPageSize(RestResourceId.GOBII_MARKERS);
            }


            List<MapsetBrapiDTO> mapsetList = mapsetBrapiService.getMapSets(page, pageSize);

            Map<String, Object> brapiResult = new HashMap<>();

            brapiResult.put("data", mapsetList);

            BrApiMasterPayload<Map<String, Object>> payload = new BrApiMasterPayload(brapiResult);

            payload.getMetadata().getPagination().setCurrentPage(page);

            payload.getMetadata().getPagination().setPageSize(pageSize);


            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(payload);
            
        }
        catch(Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "Entity does not exist"
            );
        }

    }

    @ApiOperation(
            value = "Get Maps by mapId",
            notes = "List Genome maps in the database",
            tags = {"Maps"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Maps : mapId")
                    })
            }
            ,
            hidden = true

    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required = true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value="/maps/{mapId}", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getMapByMapId(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @PathVariable(value = "mapId") Integer mapId) throws GobiiException {

        try {

            if(page == null) {
                //First Page
                page = getDefaultBrapiPage();
            }
            if(pageSize == null) {

                //TODO: Using same resource limit as markers. But, Can be defined seperately
                pageSize = getDefaultPageSize(RestResourceId.GOBII_MARKERS);

            }

            MapsetBrapiDTO mapset = mapsetBrapiService.getMapSet(mapId, page, pageSize);

            BrApiMasterPayload<Map<String, Object>> payload = new BrApiMasterPayload(mapset);


            payload.getMetadata().getPagination().setCurrentPage(page);

            payload.getMetadata().getPagination().setPageSize(pageSize);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(payload);

        }
        catch(Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "Entity does not exist"
            );
        }

    }

    @ApiOperation(
            value = "Get Markers by mapId",
            notes = "List Genome maps in the database",
            tags = {"Maps"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Markers")
                    })
            }
            ,
            hidden = true
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required = true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value="/maps/{mapId}/positions", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getMarkersByMapId(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "linkageGroupName", required = false) String linkageGroupName,
            @PathVariable(value = "mapId") Integer mapId) throws GobiiException {

        try {

            if(page == null) {
                //First Page
                page = getDefaultBrapiPage();
            }
            if(pageSize == null) {

                //TODO: Using same resource limit as markers. But, Can be defined seperately
                pageSize = getDefaultPageSize(RestResourceId.GOBII_MARKERS);

            }

            MapsetBrapiDTO mapset = mapsetBrapiService.getMapSet(mapId, page, pageSize);

            MarkerBrapiDTO markerFilter = new MarkerBrapiDTO();

            markerFilter.setMapSetId(mapset.getMapDbId());


            if(linkageGroupName != null) {
                markerFilter.setLinkageGroupName(linkageGroupName);
            }

            List<MarkerBrapiDTO> markers = markerBrapiService.getMarkers(
                    null, page, pageSize, markerFilter);

            Map<String, Object> brapiResult = new HashMap<>();

            brapiResult.put("data", markers);

            BrApiMasterPayload<Map<String, Object>> payload = new BrApiMasterPayload(brapiResult);


            payload.getMetadata().getPagination().setCurrentPage(page);
            payload.getMetadata().getPagination().setPageSize(pageSize);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(payload);

        }
        catch(GobiiException gE) {
            throw gE;
        }
        catch(Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "Entity does not exist"
            );
        }

    }

    /**
     * Returns the list of genotypes calls in a given Marker id.
     * It fetches calls in all the datasets where the marker_id is present.
     * The calls is paged.
     *
     * @param variantDbId - Marker run Id.
     * @param pageSize - Size of the page to fetched.
     * @param pageToken - Page token to fetch the page. User will get the pageToken
     *                       from the nextPageToken parameter in the previous response.
     *
     * @return BrApi Response entity with list of genotypes calls for given dnarun id.
     * TODO: Add page number parameter to comply BrApi standards.
     */
    @ApiOperation(
            value = "List Genotype Calls",
            notes = "List of all the genotype calls in a given Marker identified by Marker Id",
            tags = {"Variants"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="GenotypeCalls")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of Genotype Calls",
                            response = GenotypeCallsResponse.class
                    )
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name="Authorization", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string")
    })
    @RequestMapping(value="/variants/{variantDbId}/calls", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getCallsByVariant(
            @ApiParam(value = "Id for marker to be fetched")
            @PathVariable(value="variantDbId") String variantDbId,
            @ApiParam(value = "Page Token to fetch a page. " +
                    "nextPageToken form previous page's meta data should be used." +
                    "If pageNumber is specified pageToken will be ignored. " +
                    "pageToken can be used to sequentially get pages faster. " +
                    "When an invalid pageToken is given the page will start from beginning.")
            @RequestParam(value = "pageToken", required = false) String pageToken,
            @ApiParam(value = "Size of the page to be fetched. Default is 1000. Maximum page size is 1000")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            HttpServletRequest request) throws Exception {

        Integer variantDbIdInt;

        try {

            try {
                variantDbIdInt = Integer.parseInt(variantDbId);
            } catch (Exception e) {
                throw new GobiiException(
                        GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "Invalid marker Id");
            }


            Integer maxPageSize = RestResourceLimits.getResourceLimit(
                    RestResourceId.GOBII_DNARUN,
                    RestMethodType.GET);

            if(maxPageSize == null) {
                //As per initial standards
                maxPageSize = 10000;
            }

            if (pageSize == null || pageSize > maxPageSize) {
                pageSize = maxPageSize;
            }

            List<GenotypeCallsDTO> genotypeCallsList = genotypeCallsService.getGenotypeCallsByMarkerId(
                    variantDbIdInt, pageToken, pageSize);

            BrApiMasterPayload<List<GenotypeCallsDTO>> payload = new BrApiMasterPayload<>(genotypeCallsList);

            if (genotypeCallsList.size() > 0) {
                payload.getMetadata().getPagination().setPageSize(genotypeCallsList.size());
                if (genotypeCallsList.size() >= pageSize) {
                    payload.getMetadata().getPagination().setNextPageToken(
                            genotypeCallsList.get(genotypeCallsList.size() - 1).getVariantSetDbId().toString() +
                                    "-" +
                            genotypeCallsList.get(genotypeCallsList.size() - 1).getCallSetDbId().toString()
                    );
                }
            }

            return ResponseEntity.ok(payload);
        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    "Internal Server Error" + e.getMessage());
        }
    }

    /**
     * Lists the variantsets by page size and page token
     *
     * @param pageTokenParam - String page token
     * @param pageSize - Page size set by the user. If page size is more than maximum allowed
     *                 page size, then the response will have maximum page size
     * @return Brapi response with list of variantsets
     */
    @ApiOperation(
            value = "List Variantsets",
            notes = "List of all Variantsets",
            tags = {"VariantSets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="VariantSets")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of VariantSets",
                            response = VariantSetListResponse.class
                    )
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string")
    })
    @RequestMapping(value="/variantsets", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getVariantSets(
            @ApiParam(value = "Page Token to fetch a page. " +
                    "nextPageToken form previous page's meta data should be used." +
                    "If pageNumber is specified pageToken will be ignored. " +
                    "pageToken can be used to sequentially get pages faster. " +
                    "When an invalid pageToken is given the page will start from beginning.")
            @RequestParam(value = "pageToken", required = false) String pageTokenParam,
            @ApiParam(value = "Size of the page to be fetched. Default is 1000. Maximum page size is 1000")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "variantSetDbId", required = false) Integer variantSetDbId,
            @RequestParam(value = "variantSetName", required = false) String variantSetName,
            @RequestParam(value = "studyDbId", required = false) String studyDbId,
            HttpServletRequest request
    ) {
        try {

            Integer maxPageSize = RestResourceLimits.getResourceLimit(
                    RestResourceId.GOBII_DATASETS,
                    RestMethodType.GET
            );

            if (maxPageSize == null) {
                maxPageSize = this.brapiDefaultPageSize;
            }

            if (pageSize == null) {
                pageSize = this.brapiDefaultPageSize;
            }
            else if(pageSize > maxPageSize) {
                pageSize = maxPageSize;
            }

            variantSetsService.setCropType(CropRequestAnalyzer.getGobiiCropType(request));

            List<VariantSetDTO> variantSets = variantSetsService.listVariantSets(pageNum, pageSize, variantSetDbId);

            BrApiMasterListPayload<VariantSetDTO> payload = new BrApiMasterListPayload<>(variantSets);

            payload.getMetadata().getPagination().setPageSize(pageSize);

            payload.getMetadata().getPagination().setCurrentPage(pageNum);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(payload);

        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    "Internal Server Error" + e.getMessage()
            );
        }
    }

    /**
     * Endpoint for getting a specific variantset with a given variantSetDbId
     *
     * @param variantSetDbId ID of the requested variantset
     * @return ResponseEntity with http status code specifying if retrieval of the variantset is successful.
     * Response body contains the requested variantset information
     */
    @ApiOperation(
            value = "Get Variantset by variantSetDbId",
            notes = "Retrieves the VariantSet entity having the specified ID",
            tags = {"VariantSets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="VariantSets : variantSetDbId")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of VariantSet By ID",
                            response = VariantSetResponse.class
                    )
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required = true,
            paramType = "header", dataType = "string"),
    })
    @RequestMapping(value="/variantsets/{variantSetDbId:[\\d]+}", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getVariantSetById(
            @ApiParam(value = "ID of the VariantSet to be extracted", required = true)
            @PathVariable("variantSetDbId") Integer variantSetDbId,
            HttpServletRequest request) {

        try {

            variantSetsService.setCropType(CropRequestAnalyzer.getGobiiCropType(request));

            VariantSetDTO variantSetDTO = variantSetsService.getVariantSetById(variantSetDbId);

            BrApiMasterPayload<VariantSetDTO> payload = new BrApiMasterPayload<>(variantSetDTO);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(payload);

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
     * Lists the variants for a given VariantSetDbId by page size and page token
     *
     * @param variantSetDbId - Integer ID of the VariantSet to be fetched
     * @param pageTokenParam - String page token
     * @param pageSize - Page size set by the user. If page size is more than maximum allowed page size,
     *                 then the response will have maximum page size
     * @return Brapi response with list of CallSets
     */
    @ApiOperation(
            value = "List Variants by VariantSetDbId",
            notes = "List of all the Variants in a specific VariantSet",
            tags = {"VariantSets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Variants")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of Variants by VariantSetId",
                            response = VariantListResponse.class
                    )
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required = true,
                    paramType = "header", dataType = "string")
    })
    @RequestMapping(value="/variantsets/{variantSetDbId:[\\d]+}/variants", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getVariantsByVariantSetDbId(
            @ApiParam(value = "ID of the VariantSet of the Variants to be extracted", required = true)
            @PathVariable("variantSetDbId") Integer variantSetDbId,
            @ApiParam(value = "Page Token to fetch a page. " +
                    "nextPageToken form previous page's meta data should be used." +
                    "If pageNumber is specified pageToken will be ignored. " +
                    "pageToken can be used to sequentially get pages faster. " +
                    "When an invalid pageToken is given the page will start from beginning.")
            @RequestParam(value = "pageToken", required = false) String pageTokenParam,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @ApiParam(value = "Size of the page to be fetched. Default is 1000. Maximum page size is 1000")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "ID of the mapset to be retrieved")
            @RequestParam(value = "mapSetId", required = false) Integer mapSetId,
            @ApiParam(value = "Name of the mapset to be retrieved")
            @RequestParam(value = "mapSetName", required = false) String mapSetName
    ){

        try {

            Integer pageToken = null;

            if (pageTokenParam != null) {
                try {
                    pageToken = Integer.parseInt(pageTokenParam);
                } catch (Exception e) {
                    throw new GobiiException(
                            GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.BAD_REQUEST,
                            "Invalid Page Token"
                    );
                }
            }

            MarkerBrapiDTO markerBrapiDTOFilter = new MarkerBrapiDTO();

            List<Integer> variantSetDbIdArr = new ArrayList<>();
            variantSetDbIdArr.add(variantSetDbId);
            markerBrapiDTOFilter.setVariantSetDbId(variantSetDbIdArr);

            if (mapSetId != null) {
                markerBrapiDTOFilter.setMapSetId(mapSetId);
            }

            if (mapSetName != null) {
                markerBrapiDTOFilter.setMapSetName(mapSetName);
            }

            Integer maxPageSize = RestResourceLimits.getResourceLimit(
                    RestResourceId.GOBII_MARKERS,
                    RestMethodType.GET
            );

            if (maxPageSize == null){
                maxPageSize = 1000;
            }

            if (pageSize == null || pageSize > maxPageSize) {
                pageSize = maxPageSize;
            }

            List<MarkerBrapiDTO> markerList = markerBrapiService.getMarkers(pageToken, pageNum,
                    pageSize, markerBrapiDTOFilter);

            BrApiResult result = new BrApiResult();
            result.setData(markerList);
            BrApiMasterPayload payload = new BrApiMasterPayload(result);

            if (markerList.size() > 0) {
                payload.getMetadata().getPagination().setPageSize(markerList.size());
                if (markerList.size() >= pageSize) {
                    payload.getMetadata().getPagination().setNextPageToken(
                            markerList.get(markerList.size() -1).getVariantDbId().toString()
                    );
                }
            }

            return ResponseEntity.ok().contentType(
                    MediaType.APPLICATION_JSON).body(payload);

        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    "Internal Server Error" + e.getMessage()
            );
        }
    }

    /**
     * Lists the callsets for a given VariantSetDbId by page size and page token
     *
     * @param variantSetDbId - Integer ID of the VariantSet to be fetched
     * @param pageTokenParam - String page token
     * @param pageSize - Page size set by the user. If page size is more than maximum allowed page size, then the response will have maximum page size
     * @return Brapi response with list of CallSets
     */
    @ApiOperation(
            value = "List Callsets by VariantSetDbId",
            notes = "List of all the CallSets in a specific VariantSet",
            tags = {"VariantSets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Callsets")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of CallSets by VariantSetId",
                            response = CallSetListResponse.class
                    )
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="Authorization", value="Authentication Token", required = true,
                    paramType = "header", dataType = "string")
    })
    @RequestMapping(value="/variantsets/{variantSetDbId:[\\d]+}/callsets", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity getCallSetsByVariantSetDbId(
            @ApiParam(value = "ID of the VariantSet of the CallSets to be extracted", required = true)
            @PathVariable("variantSetDbId") Integer variantSetDbId,
            @ApiParam(value = "Page Token to fetch a page. " +
                    "nextPageToken form previous page's meta data should be used." +
                    "If pageNumber is specified pageToken will be ignored. " +
                    "pageToken can be used to sequentially get pages faster. " +
                    "When an invalid pageToken is given the page will start from beginning.")
            @RequestParam(value = "pageToken", required = false) String pageTokenParam,
            @ApiParam(value = "Size of the page to be fetched. Default is 1000. Maximum page size is 1000")
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ){

        try {

            Integer pageToken = null;

            if (pageTokenParam != null) {
                try {
                    pageToken = Integer.parseInt(pageTokenParam);
                } catch (Exception e) {
                    throw new GobiiException(
                            GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.BAD_REQUEST,
                            "Invalid Page Token"
                    );
                }
            }

            DnaRunDTO dnaRunDTOFilter = new DnaRunDTO();

            List<Integer> variantSetDbIdArr = new ArrayList<>();
            variantSetDbIdArr.add(variantSetDbId);
            dnaRunDTOFilter.setVariantSetIds(variantSetDbIdArr);

            Integer maxPageSize = RestResourceLimits.getResourceLimit(
                    RestResourceId.GOBII_MARKERS,
                    RestMethodType.GET
            );

            if (maxPageSize == null){
                maxPageSize = 1000;
            }

            if (pageSize == null || pageSize > maxPageSize) {
                pageSize = maxPageSize;
            }

            List<DnaRunDTO> dnaRunList = dnaRunService.getDnaRuns(pageToken, pageSize, dnaRunDTOFilter);

            BrApiResult result = new BrApiResult();
            result.setData(dnaRunList);
            BrApiMasterPayload payload = new BrApiMasterPayload(result);

            if (dnaRunList.size() > 0) {
                payload.getMetadata().getPagination().setPageSize(dnaRunList.size());
                if (dnaRunList.size() >= pageSize) {
                    payload.getMetadata().getPagination().setNextPageToken(
                            dnaRunList.get(dnaRunList.size() -1).getCallSetDbId().toString()
                    );
                }
            }

            return ResponseEntity.ok().contentType(
                    MediaType.APPLICATION_JSON).body(payload);

        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    "Internal Server Error" + e.getMessage()
            );
        }
    }





    @ApiOperation(
            value = "Create an extract ",
            notes = "Creates a variant set resource for given extract query",
            tags = {"VariantSets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="VariantSets")
                    })
            }
            ,
            hidden = true
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name="Authorization", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string")
    })
    @RequestMapping(value="/variantsets/extract", method=RequestMethod.POST)
    public ResponseEntity<String> VariantSetsExtract(
            HttpEntity<String> extractQuery,
            HttpServletRequest request) throws Exception {

        String cropType = CropRequestAnalyzer.getGobiiCropType(request);

        String processingId = UUID.randomUUID().toString();

        if(extractQuery.hasBody()) {

            String extractQueryPath = LineUtils.terminateDirectoryPath(
                    configSettingsService.getConfigSettings().getServerConfigs().get(
                            cropType).getFileLocations().get(GobiiFileProcessDir.RAW_USER_FILES)
            ) + processingId + LineUtils.PATH_TERMINATOR + "extractQuery.json";

            File extractQueryFile = new File(extractQueryPath);

            extractQueryFile.getParentFile().mkdirs();

            BufferedWriter bw = new BufferedWriter(new FileWriter(extractQueryFile));

            bw.write(extractQuery.getBody());

            bw.close();
        }

        searchExtract.asyncMethod();

        return ResponseEntity.ok(processingId);
    }

    @ApiOperation(
            value = "List Genotype Calls",
            notes = "List of all the genotype calls in a given Variantset",
            tags = {"VariantSets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="GenotypeCalls")
                    })
            }
            ,
            hidden = true
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful retrieval of Genotype Calls by VariantSetId",
                            response = GenotypeCallsListResponse.class
                    )
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name="Authorization", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string")
    })
    @RequestMapping(
            value="/variantsets/{variantSetDbId}/calls",
            method=RequestMethod.GET,
            produces = "application/json")
    public @ResponseBody ResponseEntity getCallsByVariantSetDbId(
            @ApiParam(value = "ID of the VariantSet of the CallSets to be extracted", required = true)
            @PathVariable("variantSetDbId") String variantSetDbIdVar,
            @ApiParam(value = "Page Token to fetch a page. " +
                    "nextPageToken form previous page's meta data should be used." +
                    "If pageNumber is specified pageToken will be ignored. " +
                    "pageToken can be used to sequentially get pages faster. " +
                    "When an invalid pageToken is given the page will start from beginning.")
            @RequestParam(value = "pageToken", required = false) String pageToken,
            @ApiParam(value = "Size of the page to be fetched. Default is 1000. Maximum page size is 1000")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            HttpServletRequest request
    ){

        try {
            List<GenotypeCallsDTO> genotypeCallsList = new ArrayList<>();

            Integer variantSetDbId;

            try {

                variantSetDbId = Integer.parseInt(variantSetDbIdVar);

                genotypeCallsList = genotypeCallsService.getGenotypeCallsByDatasetId(
                        variantSetDbId, pageToken, pageSize);

            }
            catch(NumberFormatException | NullPointerException ne) {

                String cropType = CropRequestAnalyzer.getGobiiCropType(request);

                String extractQueryPath = LineUtils.terminateDirectoryPath(
                        configSettingsService.getConfigSettings().getServerConfigs().get(
                                cropType).getFileLocations().get(GobiiFileProcessDir.RAW_USER_FILES)
                ) + variantSetDbIdVar + LineUtils.PATH_TERMINATOR + "extractQuery.json";

                genotypeCallsList =
                        genotypeCallsService.getGenotypeCallsByExtractQuery(
                                extractQueryPath, pageToken, pageSize);
            }


            BrApiResult result = new BrApiResult();
            result.setData(genotypeCallsList);
            BrApiMasterPayload payload = new BrApiMasterPayload(result);

            if (genotypeCallsList.size() > 0) {
                payload.getMetadata().getPagination().setPageSize(genotypeCallsList.size());
                if (genotypeCallsList.size() >= pageSize) {
                    payload.getMetadata().getPagination().setNextPageToken(
                            genotypeCallsService.getNextPageToken());
                }
            }

            return ResponseEntity.ok(payload);

        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    "Internal Server Error" + e.getMessage()
            );
        }
    }

    @ApiOperation(
            value = "Download Genotype Calls",
            notes = "Download of all the genotype calls in a given Variantset",
            tags = {"VariantSets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="GenotypeCalls")
                    })
            }
            ,
            hidden = true
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name="Authorization", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string")
    })
    @RequestMapping(
            value="/variantsets/{variantSetDbId:[\\d]+}/calls/download",
            method=RequestMethod.GET,
            produces = "text/csv")
    public ResponseEntity<ResponseBodyEmitter> handleRbe(
            @PathVariable("variantSetDbId") Integer variantSetDbId,
            HttpServletRequest request

    ) {

        //Giving Response emitter to finish the download within 30 mins.
        //The request thread will be terminated once 30 mins is done.
        ResponseBodyEmitter emitter = new ResponseBodyEmitter((long)1800000);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

           try {

               Map<String, String> genotypesResult = genotypeCallsService.getGenotypeCallsAsString(
                       variantSetDbId, null);

               String genotypes = genotypesResult.get("genotypes");

               if(genotypesResult.containsKey("genotypes") &&
                       genotypesResult.get("genotypes") != null &&
                       genotypesResult.get("genotypes").length() != 0) {

                   emitter.send(genotypes, MediaType.TEXT_PLAIN);

                   while(genotypesResult.get("nextPageOffset") != null) {

                       genotypesResult = genotypeCallsService.getGenotypeCallsAsString(
                               variantSetDbId, genotypesResult.get("nextPageOffset"));

                       genotypes = genotypesResult.get("genotypes");

                       emitter.send(genotypes, MediaType.TEXT_PLAIN);
                   }

                   emitter.complete();
               }
               else {
                   emitter.complete();
               }

               emitter.complete();

           } catch (Exception e) {

               e.printStackTrace();
               emitter.completeWithError(e);
               return;
           }

           emitter.complete();

        });

        return ResponseEntity.ok().header(
                "Content-Disposition", "attachment; filename=" + variantSetDbId.toString() + ".csv"
        ).contentType(MediaType.parseMediaType("text/csv")
        ).body(emitter);
    }

    /**
     * Brapi pages are 0 indexed and first page number is 0
     * @return First page number
     */
    public Integer getDefaultBrapiPage() {

        return 0;
    }

    /**
     * Gets the default page size
     * @param restResourceId Resource Id for which default page size needs to be fetched. Example: GOBII_MARKERS
     * @return
     */
    public Integer getDefaultPageSize(RestResourceId restResourceId) {

        Integer pageSize = 1000;

        try {

            //TODO: Using same resource limit as markers. define different resource limit if required
            pageSize = RestResourceLimits.getResourceLimit(
                    RestResourceId.GOBII_MARKERS,
                    RestMethodType.GET
            );

            if(pageSize == null) {
                pageSize = 1000;
            }
        }
        catch(Exception e) {
            //If resource limit is not defined
            pageSize = 1000;
        }

        return pageSize;
    }

}// BRAPIController
