// ************************************************************************
// (c) 2016 GOBii Projects
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiiweb.controllers.brapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import org.gobiiproject.gobiidomain.services.PingService;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiibrapi.calls.germplasm.BrapiResponseGermplasmByDbId;
import org.gobiiproject.gobiibrapi.calls.germplasm.BrapiResponseMapGermplasmByDbId;
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
import org.gobiiproject.gobiimodel.dto.noaudit.*;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


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
@SuppressWarnings("unused")
public class BRAPIIControllerV1 {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BRAPIIControllerV1.class); //u

    private final Integer brapiDefaultPageSize = 1000; //TODO: remove

    @Autowired
    private PingService pingService = null; //TODO: remove


    @Autowired
    private BrapiResponseMapStudiesSearch brapiResponseMapStudiesSearch = null;


    @Autowired
    private BrapiResponseMapAlleleMatrixSearch brapiResponseMapAlleleMatrixSearch = null;

    @Autowired
    private BrapiResponseMapMarkerProfiles brapiResponseMapMarkerProfiles = null;


    @Autowired
    private BrapiResponseMapAlleleMatrices brapiResponseMapAlleleMatrices = null;

    private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);


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

            //String cropType = CropRequestAnalyzer.getGobiiCropType(request);
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

}
