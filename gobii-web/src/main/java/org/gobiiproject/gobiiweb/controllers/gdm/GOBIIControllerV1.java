// ************************************************************************
// (c) 2016 GOBii Projects
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiiweb.controllers.gdm;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.tika.Tika;
import org.gobiiproject.gobiidomain.services.gdmv3.KeycloakService;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.AnalysisService;
import org.gobiiproject.gobiidomain.services.ConfigSettingsService;
import org.gobiiproject.gobiidomain.services.ContactService;
import org.gobiiproject.gobiidomain.services.CvGroupService;
import org.gobiiproject.gobiidomain.services.CvService;
import org.gobiiproject.gobiidomain.services.DataSetService;
import org.gobiiproject.gobiidomain.services.DisplayService;
import org.gobiiproject.gobiidomain.services.EntityStatsService;
import org.gobiiproject.gobiidomain.services.ExperimentService;
import org.gobiiproject.gobiidomain.services.ExtractorInstructionFilesService;
import org.gobiiproject.gobiidomain.services.FilesService;
import org.gobiiproject.gobiidomain.services.JobService;
import org.gobiiproject.gobiidomain.services.LoaderFilesService;
import org.gobiiproject.gobiidomain.services.LoaderInstructionFilesService;
import org.gobiiproject.gobiidomain.services.ManifestService;
import org.gobiiproject.gobiidomain.services.MapsetService;
import org.gobiiproject.gobiidomain.services.MarkerGroupService;
import org.gobiiproject.gobiidomain.services.MarkerService;
import org.gobiiproject.gobiidomain.services.NameIdListService;
import org.gobiiproject.gobiidomain.services.OrganizationService;
import org.gobiiproject.gobiidomain.services.PingService;
import org.gobiiproject.gobiidomain.services.PlatformService;
import org.gobiiproject.gobiidomain.services.ProjectService;
import org.gobiiproject.gobiidomain.services.ProtocolService;
import org.gobiiproject.gobiidomain.services.ReferenceService;
import org.gobiiproject.gobiiapimodel.payload.HeaderAuth;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.gobii.GobiiEntityNameConverter;
import org.gobiiproject.gobiiapimodel.restresources.gobii.GobiiUriFactory;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiiapimodel.types.GobiiHttpHeaderNames;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds.DtoMapNameIdParams;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.dto.auditable.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ContactDTO;
import org.gobiiproject.gobiimodel.dto.auditable.DisplayDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ExperimentDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ManifestDTO;
import org.gobiiproject.gobiimodel.dto.auditable.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.auditable.MarkerGroupDTO;
import org.gobiiproject.gobiimodel.dto.auditable.OrganizationDTO;
import org.gobiiproject.gobiimodel.dto.auditable.PlatformDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ProjectDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ProtocolDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ReferenceDTO;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.ExtractorInstructionFilesDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.LoaderFilePreviewDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.LoaderInstructionFilesDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.CvDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.CvGroupDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.MarkerDTO;
import org.gobiiproject.gobiimodel.dto.rest.RestProfileDTO;
import org.gobiiproject.gobiimodel.dto.system.ConfigSettingsDTO;
import org.gobiiproject.gobiimodel.dto.system.EntityStatsDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedList;
import org.gobiiproject.gobiimodel.dto.system.PingDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.DnaSampleDTO;
import org.gobiiproject.gobiimodel.security.TokenInfo;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.types.GobiiFilterType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.types.RestMethodType;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.automation.ControllerUtils;
import org.gobiiproject.gobiiweb.automation.GobiiVersionInfo;
import org.gobiiproject.gobiiweb.automation.PayloadReader;
import org.gobiiproject.gobiiweb.automation.PayloadWriter;
import org.gobiiproject.gobiiweb.automation.RestResourceLimits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.ResponseHeader;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Created by MrPhil on 7/6/2015.
 */

@Scope(value = "request")
@RestController
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII)
@Api()
@CrossOrigin
public class GOBIIControllerV1 {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GOBIIControllerV1.class);

    @Autowired
    HttpServletRequest req;

    @Autowired
    private PingService pingService = null;

    @Autowired
    private ProjectService<ProjectDTO> projectService = null;

    @Autowired
    private ContactService contactService = null;

    @Autowired
    private ReferenceService referenceService = null;

    @Autowired
    private AnalysisService analysisService = null;

    @Autowired
    private ManifestService manifestService = null;

    @Autowired
    private MarkerGroupService markerGroupService = null;

    @Autowired
    private OrganizationService organizationService = null;

    @Autowired
    private ExperimentService<ExperimentDTO> experimentService = null;

    @Autowired
    private NameIdListService nameIdListService = null;

    @Autowired
    private LoaderInstructionFilesService loaderInstructionFilesService = null;

    @Autowired
    private ExtractorInstructionFilesService extractorInstructionFilesService = null;

    @Autowired
    private LoaderFilesService loaderFilesService = null;

    @Autowired
    private DisplayService displayService = null;

    @Autowired
    private CvService cvService = null;

    @Autowired
    private CvGroupService cvGroupService = null;

    @Autowired
    private MarkerService markerService = null;

    @Autowired
    private DataSetService dataSetService = null;

    @Autowired
    private PlatformService platformService = null;

    @Autowired
    private MapsetService mapsetService = null;

    @Autowired
    private ConfigSettingsService configSettingsService;

    @Autowired
    private ProtocolService protocolService = null;

    @Autowired
    private FilesService fileService = null;

    @Autowired
    private JobService jobService = null;

    @Autowired
    private EntityStatsService entityStatsService = null;

    @Autowired
    private KeycloakService keycloakService;

    private Tika tika = new Tika();

    /**
     * @return crop type from class level path variable
     */
    private String cropType() {
        Map<String, String> variables = (Map<String, String>)
            req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return variables.get("cropType");
    }

    @ApiOperation(value="ping",
            notes = "Pings the GDB Web server.",
            tags = {"Ping"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Ping")
            })
    }
    )
    @RequestMapping(value = "/ping", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<PingDTO> getPingResponse(@RequestBody PayloadEnvelope<PingDTO> pingDTOPayloadEnvelope) {

        PayloadEnvelope<PingDTO> returnVal = new PayloadEnvelope<PingDTO>();

        try {

            PayloadReader<PingDTO> payloadReader = new PayloadReader<>(PingDTO.class);
            PingDTO pingDTORequest = payloadReader.extractSingleItem(pingDTOPayloadEnvelope);

            PingDTO pingDTOResponse = pingService.getPings(pingDTORequest);
            String newResponseString = LineUtils.wrapLine("Loader controller responded");
            pingDTOResponse.getPingResponses().add(newResponseString);

            // add gobii version
            returnVal.getHeader().setGobiiVersion(GobiiVersionInfo.getVersion());
            returnVal.getHeader().setCropType(CropRequestAnalyzer.getGobiiCropType());


            returnVal.getPayload().getData().add(pingDTOResponse);
        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
            LOGGER.error(e.getMessage());
        }


        return (returnVal);

    }//getPingResponse()


    @ApiOperation(
            value = "authenticate",
            notes = "The user credentials are specified in the request headers X-Username and X-Password; " +
                    "the response and the response headers include the token in the X-Auth-Token header. " +
                    "X-Auth-Token header and value obtained from /auth call will be used as an API-key " +
                    "for the rest of the GDM calls.",
            tags = {"Authentication"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Authentication")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Username", value="User Identifier", required=true,
                    paramType = "header", dataType = "string"),
            @ApiImplicitParam(name="X-Password", value="User password", required=true,
                    paramType = "header", dataType = "string"),
    })
    @ApiResponses(value={
            @ApiResponse(code=200, message = "OK", responseHeaders=@ResponseHeader(
                    name="X-Auth-Token ", description = "API key to authenticate GDM api calls",
                    response = String.class
            ))
    })
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    @ResponseBody
    public String authenticate(@RequestBody(required = false) String noContentExpected,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        String returnVal = null;
        try {
            //returnVal = "Authenticated: " + (new Date()).toString();
            String username = request.getHeader(GobiiHttpHeaderNames.HEADER_NAME_USERNAME);
            String password = request.getHeader(GobiiHttpHeaderNames.HEADER_NAME_PASSWORD);
            TokenInfo tokenInfo = keycloakService.getToken(username, password);

            //     PayloadWriter<AuthDTO> payloadWriter = new PayloadWriter<>(request, response,
            //             AuthDTO.class);

            HeaderAuth dtoHeaderAuth = new HeaderAuth();
            //payloadWriter.setAuthHeader(dtoHeaderAuth, response);

            dtoHeaderAuth.setUserName(username);
            dtoHeaderAuth.setToken(tokenInfo.getAccessToken());
            dtoHeaderAuth.setGobiiCropType(
                    CropRequestAnalyzer.getGobiiCropType(request)
            );

            //manually set the values in the header since this endpoint handler is excluded
            //from the keycloak interceptor
            response.setHeader(GobiiHttpHeaderNames.HEADER_NAME_USERNAME, username);
            response.setHeader(GobiiHttpHeaderNames.HEADER_NAME_TOKEN, tokenInfo.getAccessToken());
            response.setHeader(GobiiHttpHeaderNames.HEADER_NAME_GOBII_CROP, dtoHeaderAuth.getGobiiCropType());


            ObjectMapper objectMapper = new ObjectMapper();
            String dtoHeaderAuthString = objectMapper.writeValueAsString(dtoHeaderAuth);
            returnVal = dtoHeaderAuthString;

        } catch (Exception e) {  //TODO: what is this?
            e.printStackTrace();

        }

        return (returnVal);

    }

    @ApiOperation(
            value = "List all configuration settings.",
            notes = "List all configuration settings.\n+" +
                    "Provides generic configuration information about the GOBii instances in " +
                    "a given deployment. This call does not require authentication",
            tags = {"ConfigSettings"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="ConfigSettings")
            })
    }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/configsettings", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ConfigSettingsDTO> getConfigSettings(
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ConfigSettingsDTO> returnVal = new PayloadEnvelope<>();
        try {

            ConfigSettingsDTO configSettingsDTO = configSettingsService.getConfigSettings();
            if (null != configSettingsDTO) {

                PayloadWriter<ConfigSettingsDTO> payloadWriter = new PayloadWriter<>(request, response,
                        ConfigSettingsDTO.class);

                payloadWriter.writeSingleItemForDefaultId(returnVal,
                        GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                                RestResourceId.GOBII_CONFIGSETTINGS),
                        configSettingsDTO, cropType());

            } else {
                returnVal.getHeader().getStatus().addStatusMessage(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.NONE,
                        "Unable to retrieve config settings");
            }

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }


    @ApiOperation(
            value = "/restprofiles/",
            notes = "Update REST profiles\n" +
                    "When the Header of the payload envelope for a resource contains " +
                    "maxGet, maxPost, and maxPut values, this resource provides a means " +
                    "to update the max for a given rest resource ID and for a given HTTP verb. " +
                    "The values are transient in the sense that they will be confined only to a " +
                    "specific web service deployment. They are stored in the web service configuration" +
                    "document",
            tags = {"RestProfiles"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="update")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/restprofiles", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<RestProfileDTO> updateRestProfile(
            @RequestBody PayloadEnvelope<RestProfileDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<RestProfileDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<RestProfileDTO> payloadReader = new PayloadReader<>(RestProfileDTO.class);
            RestProfileDTO restProfileDTOToUpdate = payloadReader.extractSingleItem(payloadEnvelope);

            RestResourceLimits.setResourceLimit(restProfileDTOToUpdate);

            PayloadWriter<RestProfileDTO> payloadWriter = new PayloadWriter<>(request, response,
                    RestProfileDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_REST_PROFILES),
                    restProfileDTOToUpdate, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
            LOGGER.error(e.getMessage());
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    // *********************************************
    // *************************** ANALYSIS METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new analyses.",
            notes = "Create analysis entity for GDM system.",
            tags = {"Analyses"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Analyses"),
                    @ExtensionProperty(
                            name="tag-description",
                            value="Analyses describe the different algorithms " +
                                    "that were applied to the genotyping or " +
                                    "sequence data to produce the final dataset being loaded. " +
                                    "Each analysis is grouped into an analysis type. " +
                                    "The analysis types are: calling (variant calling), " +
                                    "cleaning, and imputation. " +
                                    "Additional analysis types can be added in Controlled Vocabularies")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/analyses", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<AnalysisDTO> createAnalysis(
            @ApiParam(required = true) @RequestBody PayloadEnvelope<AnalysisDTO> analysisPostEnvelope,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        PayloadEnvelope<AnalysisDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<AnalysisDTO> payloadReader = new PayloadReader<>(AnalysisDTO.class);
            AnalysisDTO analysisDTOToCreate = payloadReader.extractSingleItem(analysisPostEnvelope);

            AnalysisDTO analysisDTONew = analysisService.createAnalysis(analysisDTOToCreate);

            PayloadWriter<AnalysisDTO> payloadWriter = new PayloadWriter<>(request, response,
                    AnalysisDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ANALYSIS),
                    analysisDTONew, cropType());
        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
            LOGGER.error(e.getMessage());
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }


    @ApiOperation(
            value = "Update the analyses by analysesId",
            notes = "Updates the Analysis entity having the specified analysisId.",
            tags = {"Analyses"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Analyses : analysesId")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/analyses/{analysisId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<AnalysisDTO> replaceAnalysis(
            @RequestBody PayloadEnvelope<AnalysisDTO> payloadEnvelope,
            @ApiParam(value = "ID of Analysis to be updated", required = true)
            @PathVariable("analysisId") Integer analysisId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<AnalysisDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<AnalysisDTO> payloadReader = new PayloadReader<>(AnalysisDTO.class);
            AnalysisDTO analysisDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            AnalysisDTO analysisDTOReplaced = analysisService.replaceAnalysis(analysisId, analysisDTOToReplace);

            PayloadWriter<AnalysisDTO> payloadWriter = new PayloadWriter<>(request, response,
                    AnalysisDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ANALYSIS),
                    analysisDTOReplaced, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "List all analyses",
            notes = "List of all Analysis entities.",
            tags = {"Analyses"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Analyses")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/analyses", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<AnalysisDTO> getAnalyses(HttpServletRequest request,
                                                    HttpServletResponse response) {

        PayloadEnvelope<AnalysisDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<AnalysisDTO> analysisDTOs = analysisService.getAnalyses();

            PayloadWriter<AnalysisDTO> payloadWriter = new PayloadWriter<>(request, response,
                    AnalysisDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ANALYSIS),
                    analysisDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Get an analyses by analysesId",
            notes = "Get analyses by analyses Id.",
            tags = {"Analyses"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Analyses : analysesId")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/analyses/{analysisId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<AnalysisDTO> getAnalysisById(
            @ApiParam(value = "ID of Analysis to be extracted.", required = true)
            @PathVariable("analysisId") Integer analysisId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<AnalysisDTO> returnVal = new PayloadEnvelope<>();

        try {

            AnalysisDTO analysisDTO = analysisService.getAnalysisById(analysisId);

            PayloadWriter<AnalysisDTO> payloadWriter = new PayloadWriter<>(request, response,
                    AnalysisDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ANALYSIS),
                    analysisDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }


    // *********************************************
    // *************************** CONTACT METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new contact",
            notes = "Create new contact.",
            tags = {"Contacts"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Contacts"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="A contact is a person who can be assigned different roles " +
                                            "according to their system access. " +
                                            "For example, curators can submit and extract all projects and update CV terms."
                            )
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/contacts", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<ContactDTO> createContact(@RequestBody PayloadEnvelope<ContactDTO> payloadEnvelope,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {

        PayloadEnvelope<ContactDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadReader<ContactDTO> payloadReader = new PayloadReader<>(ContactDTO.class);
            ContactDTO contactDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            ContactDTO contactDTONew = contactService.createContact(contactDTOToCreate);

            PayloadWriter<ContactDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ContactDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_CONTACTS),
                    contactDTONew, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Update a contact by contactId",
            notes = "Update contact by contact id.",
            tags = {"Contacts"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Contacts : contactId")
            })
    }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/contacts/{contactId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<ContactDTO> replaceContact(
            @RequestBody PayloadEnvelope<ContactDTO> payloadEnvelope,
            @ApiParam(value = "ID of contacts to be updated.", required = true)
            @PathVariable("contactId") Integer contactId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ContactDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<ContactDTO> payloadReader = new PayloadReader<>(ContactDTO.class);
            ContactDTO contactDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            ContactDTO contactDTOReplaced = contactService.replaceContact(contactId, contactDTOToReplace);

            PayloadWriter<ContactDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ContactDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_CONTACTS),
                    contactDTOReplaced, cropType());


        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }


    @ApiOperation(
            value = "Get a contact by contact id",
            notes = "Get contacts by Contact Id.",
            tags = {"Contacts"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Contacts : contactId")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/contacts/{contactId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ContactDTO> getContactsById(
            @ApiParam(value = "ID of contacts to be extracted.", required = true)
            @PathVariable("contactId") Integer contactId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ContactDTO> returnVal = new PayloadEnvelope<>();
        try {

            ContactDTO contactDTO = contactService.getContactById(contactId);
            if (null != contactDTO) {

                PayloadWriter<ContactDTO> payloadWriter = new PayloadWriter<>(request, response,
                        ContactDTO.class);

                payloadWriter.writeSingleItemForDefaultId(returnVal,
                        GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                                RestResourceId.GOBII_CONTACTS),
                        contactDTO, cropType());

            } else {
                returnVal.getHeader().getStatus().addStatusMessage(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.NONE,
                        "Unable to retrieve a contact with contactId " + contactId);
            }

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all contacts",
            notes = "List all contacts.",
            tags = {"Contacts"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Contacts")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ContactDTO> getContacts(HttpServletRequest request,
                                                   HttpServletResponse response) {

        PayloadEnvelope<ContactDTO> returnVal = new PayloadEnvelope<>();
        try {

            //PayloadReader<ContactDTO> payloadReader = new PayloadReader<>(ContactDTO.class);
            List<ContactDTO> platformDTOs = contactService.getContacts();

            PayloadWriter<ContactDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ContactDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_CONTACTS),
                    platformDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    //Technically, this regex specifies an email address format, and it actually works.
    //However, when you execute this, you get back an error "The resource identified by this request is only
    // capable of generating responses with characteristics not acceptable according to the request "accept" headers."
    // In other words, the email address is telling the server that you're asking for some other format
    // So for email based searches, you'll have to use the request parameter version
    @ApiOperation(
            value = "Get contact by email id",
            notes = "Get contact by email id.",
            tags = {"Contacts"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Contacts : emailId")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/contacts/{email:[a-zA-Z-]+@[a-zA-Z-]+.[a-zA-Z-]+}",
            method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ContactDTO> getContactsByEmail(
            @ApiParam(value = "email id of contacts to be extracted.", required = true)
            @PathVariable("email") String email,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ContactDTO> returnVal = new PayloadEnvelope<>();
        try {

            returnVal.getHeader().getStatus().addStatusMessage(GobiiStatusLevel.ERROR, "Method not implemented");

//            ContactDTO contactRequestDTO = new ContactDTO();
//            contactRequestDTO.setContactId(1);
            //contactRequestDTO.setEmail(email);
            //returnVal = contactService.processDml(new PayloadEnvelope<>(contactRequestDTO, GobiiProcessType.READ));

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // Example: http://localhost:8282/gobii-dev/gobii/v1/contact-search?email=foo&lastName=bar&firstName=snot
    // all parameters must be present, but they don't all neeed a value
    @ApiOperation(
            value = "List all contacts from Contacts search",
            notes = "List contacts for emailid, lastname, firstname, username.",
            tags = {"Contacts"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="ContactSearch")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(
            value = "/contact-search",
            params = {"email", "lastName", "firstName", "userName"},
            method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ContactDTO> getContactsBySearch(@RequestParam(value = "email", required = false) String email,
                                                           @RequestParam(value = "lastName", required = false) String lastName,
                                                           @RequestParam(value = "firstName", required = false) String firstName,
                                                           @RequestParam(value = "userName", required = false) String userName,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {

        PayloadEnvelope<ContactDTO> returnVal = new PayloadEnvelope<>();
        try {
            ContactDTO contactDTO = null;

            PayloadWriter<ContactDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ContactDTO.class);

            if (!LineUtils.isNullOrEmpty(email)) {
                contactDTO = contactService.getContactByEmail(email);
            } else if (!LineUtils.isNullOrEmpty(userName)) {
                contactDTO = contactService.getContactByUserName(userName);
            } else {
                returnVal.getHeader().getStatus().addException(new GobiiException(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "search request must provide email or userName"));
            }


            if (contactDTO != null) {
                payloadWriter.writeSingleItemForDefaultId(returnVal,
                        GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                                RestResourceId.GOBII_CONTACTS),
                        contactDTO, cropType());
            }

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** CV METHODS
    // *********************************************
    @ApiOperation(value = "Create a new Controlled Vocabulary",
            notes = "Creates new cv's.",
            tags = {"ControlledVocabularies"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Cvs"),
                    @ExtensionProperty(
                            name="tag-description",
                            value="Edit property fields and type fields in the Controlled Vocabulary tables. " +
                                    "Property fields are identified by the suffix '_prop.' " +
                                    "These are additional, user-defined fields associated with database tables. " +
                                    "Type fields are identified by the suffix '_type.' " +
                                    "These control the entries being loaded to a field and require an exact match. "
                    )
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/cvs", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<CvDTO> createCv(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<CvDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<CvDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<CvDTO> payloadReader = new PayloadReader<>(CvDTO.class);
            CvDTO cvDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            CvDTO cvDTONew = cvService.createCv(cvDTOToCreate);

            PayloadWriter<CvDTO> payloadWriter = new PayloadWriter<>(request, response,
                    CvDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_CV),
                    cvDTONew, cropType());
        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
            LOGGER.error(e.getMessage());
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }


    @ApiOperation(value = "Update a CV by cvId",
            notes = "Update cv by cvId.",
            tags = {"ControlledVocabularies"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Cvs : cvId")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/cvs/{cvId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<CvDTO> replaceCv(@RequestBody PayloadEnvelope<CvDTO> payloadEnvelope,
                                            @ApiParam(value="ID of the CV to be updated", required = true)
                                            @PathVariable("cvId") Integer cvId,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {

        PayloadEnvelope<CvDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<CvDTO> payloadReader = new PayloadReader<>(CvDTO.class);
            CvDTO cvDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            CvDTO cvDTOReplaced = cvService.replaceCv(cvId, cvDTOToReplace);

            PayloadWriter<CvDTO> payloadWriter = new PayloadWriter<>(request, response,
                    CvDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_CV),
                    cvDTOReplaced, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(value = "List all CVs",
            notes = "List all cvs in the GDM.",
            tags = {"ControlledVocabularies"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Cvs")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/cvs", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<CvDTO> getCvs(HttpServletRequest request,
                                         HttpServletResponse response) {

        PayloadEnvelope<CvDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<CvDTO> cvDTOs = cvService.getCvs();

            PayloadWriter<CvDTO> payloadWriter = new PayloadWriter<>(request, response,
                    CvDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_CV),
                    cvDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(value = "Get a CV by cvId",
            notes = "Get cv by the id.",
            tags = {"ControlledVocabularies"},
            extensions = {
                @Extension(properties = {
                       @ExtensionProperty(name="summary", value="Cvs : cvId")
                })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/cvs/{cvId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<CvDTO> getCvById(
            @ApiParam(value = "ID of the CV to be extracted") @PathVariable("cvId") Integer cvId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<CvDTO> returnVal = new PayloadEnvelope<>();

        try {

            CvDTO cvDTO = cvService.getCvById(cvId);

            PayloadWriter<CvDTO> payloadWriter = new PayloadWriter<>(request, response,
                    CvDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_CV),
                    cvDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Delete a CV by cvId",
            notes = "Deletes cv by id.",
            tags = {"ControlledVocabularies"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Cvs : cvId")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/cvs/{cvId:[\\d]+}", method = RequestMethod.DELETE)
    @ResponseBody
    public PayloadEnvelope<CvDTO> deleteCv(
            @ApiParam(value="ID of cv to be deleted", required = true) @PathVariable("cvId") Integer cvId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<CvDTO> returnVal = new PayloadEnvelope<>();

        try {

            //PayloadReader<CvDTO> payloadReader = new PayloadReader<>(CvDTO.class);
            CvDTO cvDTODeleted = cvService.deleteCv(cvId);

            PayloadWriter<CvDTO> payloadWriter = new PayloadWriter<>(request, response,
                    CvDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_CV),
                    cvDTODeleted, cropType());

        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {

            returnVal.getHeader().getStatus().addException(e);

        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all CVs in a given groupName",
            notes = "List cvs by the group name.",
            nickname = "getCvsByGroupName",
            tags = {"ControlledVocabularies"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Cvs : groupName")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/cvs/{groupName:[a-zA-Z_]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<CvDTO> getCvById(
            @ApiParam(value="name of cv group to be extracted") @PathVariable("groupName") String groupName,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<CvDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<CvDTO> cvDTOs = cvService.getCvsByGroupName(groupName);

            PayloadWriter<CvDTO> payloadWriter = new PayloadWriter<>(request, response,
                    CvDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_CV),
                    cvDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** CVGROUP METHODS
    // *********************************************
    @ApiOperation(
            value = "list",
            notes = "List CV terms in CV group with cvGroupId",
            tags = {"ControlledVocabularies"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="CvGroups.cvs")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/cvgroups/{cvGroupId}/cvs", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<CvDTO> getCvsForCvGroup(
            @ApiParam(value = "ID of the CV group.", required = true)
            @PathVariable("cvGroupId") Integer cvGroupId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<CvDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<CvDTO> cvDTOS = cvGroupService.getCvsForGroup(cvGroupId);

            PayloadWriter<CvDTO> payloadWriter = new PayloadWriter<>(request, response,
                    CvDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceColl(request.getContextPath(),
                            RestResourceId.GOBII_CV)
                            .addUriParam("id"),
                    cvDTOS, cropType());

        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {

            returnVal.getHeader().getStatus().addException(e);

        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Get a CV group by cvGroupTypeId",
            notes = "Get CV group by cv group type ID",
            tags = {"ControlledVocabularies"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="CvGroups : cvGroupTypeId")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/cvgroups/{cvGroupTypeId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<CvGroupDTO> getCvGroupsByType(
            @ApiParam(value = "ID of the cv group type", required = true)
            @PathVariable("cvGroupTypeId") Integer cvGroupTypeId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<CvGroupDTO> returnVal = new PayloadEnvelope<>();

        try {

            GobiiCvGroupType gobiiCvGroupType = GobiiCvGroupType.fromInt(cvGroupTypeId);

            if (gobiiCvGroupType != GobiiCvGroupType.GROUP_TYPE_UNKNOWN) {

                List<CvGroupDTO> cvGroupDTOS = cvGroupService.getCvsForType(gobiiCvGroupType);

                PayloadWriter<CvGroupDTO> payloadWriter = new PayloadWriter<>(request, response,
                        CvGroupDTO.class);

                // we don't have a GET for a single cvGrouop, and probably don't need one
                // so  our links will just be the same URL as we got
                payloadWriter.writeList(returnVal,
                        GobiiUriFactory.resourceColl(request.getContextPath(),
                                RestResourceId.GOBII_CVGROUP)
                                .addUriParam("id"),
                        cvGroupDTOS, cropType());
            } else {
                returnVal.getHeader().getStatus().addException(new Exception("Unknown group type: "
                        + cvGroupTypeId.toString()));
            }

        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {

            returnVal.getHeader().getStatus().addException(e);

        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Get a CV group by groupName",
            notes = "Get CV group by name",
            tags = {"ControlledVocabularies"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="CvGroups : groupName")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/cvgroups/{groupName:[a-zA-Z_]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<CvGroupDTO> getCvGroupDetails(
            @ApiParam(value = "name of the cv group to be extracted")
            @PathVariable("groupName") String groupName,
            @RequestParam(value = "cvGroupTypeId") Integer cvGroupTypeId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<CvGroupDTO> returnVal = new PayloadEnvelope<>();

        try {

            CvGroupDTO cvGroupDTO = cvGroupService.getCvGroupDetailsByGroupName(groupName, cvGroupTypeId);

            PayloadWriter<CvGroupDTO> payloadWriter = new PayloadWriter<>(request, response,
                    CvGroupDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_CVGROUP),
                    cvGroupDTO, cropType());

        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {

            returnVal.getHeader().getStatus().addException(e);

        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** DATASET METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new dataset",
            notes = "Creates a new dataset in the system.",
            tags = {"Datasets"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Datasets"),
                    @ExtensionProperty(
                            name="tag-description",
                            value="In a Dataset, you can define the suite of analyses" +
                                    " applied to the data to generate the dataset"
                    )
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/datasets", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<DataSetDTO> createDataSet(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<DataSetDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<DataSetDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadReader<DataSetDTO> payloadReader = new PayloadReader<>(DataSetDTO.class);
            DataSetDTO dataSetDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            DataSetDTO dataSetDTONew = dataSetService.createDataSet(dataSetDTOToCreate);


            PayloadWriter<DataSetDTO> payloadWriter = new PayloadWriter<>(request, response,
                    DataSetDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_DATASETS),
                    dataSetDTONew, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Update/Replace a dataset by datasetId",
            notes = "Updates the Dataset entity having the specified datasetId.",
            tags = {"Datasets"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Datasets : dataSetId")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/datasets/{dataSetId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<DataSetDTO> replaceDataSet(
            @RequestBody PayloadEnvelope<DataSetDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Dataset to be updated", required = true)
            @PathVariable("dataSetId") Integer dataSetId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<DataSetDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<DataSetDTO> payloadReader = new PayloadReader<>(DataSetDTO.class);
            DataSetDTO dataSetDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            DataSetDTO dataSetDTOReplaced = dataSetService.replaceDataSet(dataSetId, dataSetDTOToReplace);


            PayloadWriter<DataSetDTO> payloadWriter = new PayloadWriter<>(request, response,
                    DataSetDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_DATASETS),
                    dataSetDTOReplaced, cropType());
//

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all datasets",
            notes = "List all the existing datasets in the system. "+
                    "The list can be retrieved by page and specific page size.",
            tags = {"Datasets"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Datasets")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/datasets", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<DataSetDTO> getDataSets(
            HttpServletRequest request,
            HttpServletResponse response,
            @ApiParam(value = "Specify the custom page size")
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @ApiParam(value = "Retrieve the specified page by number", required = false)
            @RequestParam("pageNo") Optional<Integer> pageNo,
            @RequestParam("queryId") Optional<String> queryId) {

        PayloadEnvelope<DataSetDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadWriter<DataSetDTO> payloadWriter = new PayloadWriter<>(request, response,
                    DataSetDTO.class);

            if (pageSize.isPresent() && pageSize.get() != null &&
                    pageNo.isPresent() && pageNo.get() != null) {

                PagedList<DataSetDTO> pagedList = dataSetService.getDatasetsPaged(pageSize.get(),
                        pageNo.get(),
                        queryId.get());

                payloadWriter.writeListFromPagedQuery(returnVal,
                        GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                                RestResourceId.GOBII_DATASETS),
                        pagedList, cropType());

            } else {

                List<DataSetDTO> dataSetDTOs = dataSetService.getDataSets();

                payloadWriter.writeList(returnVal,
                        GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                                RestResourceId.GOBII_DATASETS),
                        dataSetDTOs, cropType());
            }

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get dataset by datasetId",
            notes = "Gets the Dataset entity having the specified ID.",
            tags = {"Datasets"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Datasets : dataSetId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/datasets/{dataSetId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<DataSetDTO> getDataSetsById(
            @ApiParam(value = "ID of the Dataset to be extracted", required = true)
            @PathVariable("dataSetId") Integer dataSetId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<DataSetDTO> returnVal = new PayloadEnvelope<>();
        try {

            DataSetDTO dataSetDTO = dataSetService.getDataSetById(dataSetId);

            PayloadWriter<DataSetDTO> payloadWriter = new PayloadWriter<>(request, response,
                    DataSetDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_DATASETS),
                    dataSetDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all analyses in a dataset with given datasetId",
            notes = "Lists all the analysis in a dataset identified by dataset id.",
            tags = {"Datasets"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Datasets.analyses")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/datasets/{dataSetId:[\\d]+}/analyses", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<AnalysisDTO> getAnalysesForDataset(
            @ApiParam(value = "ID of the dataset", required = true)
            @PathVariable("dataSetId") Integer dataSetId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<AnalysisDTO> returnVal = new PayloadEnvelope<>();
        try {

            List<AnalysisDTO> analysisDTOs = dataSetService.getAnalysesByDatasetId(dataSetId);

            PayloadWriter<AnalysisDTO> payloadWriter = new PayloadWriter<>(request, response,
                    AnalysisDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ANALYSIS),
                    analysisDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List of types of datasets",
            notes = "Lists dataset types in the system.",
            tags = {"Datasets"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Datasets.types")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/datasets/types", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<NameIdDTO> getDataSetsTypes(HttpServletRequest request,
                                                       HttpServletResponse response) {

        PayloadEnvelope<NameIdDTO> returnVal = new PayloadEnvelope<>();
        try {

            GobiiEntityNameType gobiiEntityNameType = GobiiEntityNameType.CV;
            GobiiFilterType gobiiFilterType = GobiiFilterType.NAMES_BY_TYPE_NAME;

            DtoMapNameIdParams dtoMapNameIdParams = new DtoMapNameIdParams(gobiiEntityNameType, gobiiFilterType, "dataset_type", null);

            List<NameIdDTO> nameIdDTOList = nameIdListService.getNameIdList(dtoMapNameIdParams);

            PayloadWriter<NameIdDTO> payloadWriter = new PayloadWriter<>(request, response,
                    NameIdDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_DATASETTYPES),
                    nameIdDTOList, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get datasets by type id",
            notes = "Gets the Dataset type by type ID.",
            tags = {"Datasets"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Datasets.types : id")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/datasets/types/{id}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<DataSetDTO> getDataSetsByTypeId(
            @ApiParam(value = "ID of the dataset type", required = true)
            @PathVariable("id") Integer id,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<DataSetDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<DataSetDTO> dataSetDTOS = dataSetService.getDataSetsByTypeId(id);

            PayloadWriter<DataSetDTO> payloadWriter = new PayloadWriter<>(request, response,
                    DataSetDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_DATASETS),
                    dataSetDTOS, cropType());


        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {

            returnVal.getHeader().getStatus().addException(e);

        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all jobs for a dataset with datasetId",
            notes = "List information for active job for a given datasetId",
            tags = {"Datasets"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Datasets.jobs")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/datasets/{datasetId}/jobs", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<JobDTO> getJobDetailsByDatasetId(
            @ApiParam(value = "ID of the dataset", required = true)
            @PathVariable("datasetId") String datasetId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<JobDTO> returnVal = new PayloadEnvelope<>();
        try {

            //String cropType = 
            CropRequestAnalyzer.getGobiiCropType(request);
            JobDTO jobDTO = dataSetService.getJobDetailsByDatasetId(Integer.parseInt(datasetId));

            PayloadWriter<JobDTO> payloadWriter = new PayloadWriter<>(request, response,
                    JobDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_JOB),
                    jobDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** DISPLAY METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new display",
            notes = "Creates displays in GDM.",
            tags = {"Displays"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Displays")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/displays", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<DisplayDTO> createDisplay(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<DisplayDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<DisplayDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<DisplayDTO> payloadReader = new PayloadReader<>(DisplayDTO.class);
            DisplayDTO displayDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            DisplayDTO displayDTONew = displayService.createDisplay(displayDTOToCreate);

            PayloadWriter<DisplayDTO> payloadWriter = new PayloadWriter<>(request, response,
                    DisplayDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_DISPLAY),
                    displayDTONew,cropType());
        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
            LOGGER.error(e.getMessage());
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }


    @ApiOperation(
            value = "Update a display with displayId",
            notes = "Updates the Display entity having the specified displayId.",
            tags = {"Displays"},
            extensions = {
                @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Displays : displayId")
                })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/displays/{displayId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<DisplayDTO> replaceDisplay(
            @RequestBody PayloadEnvelope<DisplayDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Display to be updated", required = true)
            @PathVariable("displayId") Integer displayId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<DisplayDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<DisplayDTO> payloadReader = new PayloadReader<>(DisplayDTO.class);
            DisplayDTO displayDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            DisplayDTO displayDTOReplaced = displayService.replaceDisplay(displayId, displayDTOToReplace);

            PayloadWriter<DisplayDTO> payloadWriter = new PayloadWriter<>(request, response,
                    DisplayDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_DISPLAY),
                    displayDTOReplaced,cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "List all displays",
            notes = "Lists all Displays in GDM",
            tags = {"Displays"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Displays")
            })
    }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/displays", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<DisplayDTO> getDisplays(HttpServletRequest request,
                                                   HttpServletResponse response) {

        PayloadEnvelope<DisplayDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<DisplayDTO> displayDTOS = displayService.getDisplays();

            PayloadWriter<DisplayDTO> payloadWriter = new PayloadWriter<>(request, response,
                    DisplayDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_DISPLAY),
                    displayDTOS,cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Get a display by displayId",
            notes = "Get the Display by display Id",
            tags = {"Displays"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Displays : displayId")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/displays/{displayId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<DisplayDTO> getDisplayById(
            @ApiParam(value = "ID of the Display to be extracted", required = true)
            @PathVariable("displayId") Integer displayId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<DisplayDTO> returnVal = new PayloadEnvelope<>();

        try {

            DisplayDTO displayDTO = displayService.getDisplayById(displayId);

            PayloadWriter<DisplayDTO> payloadWriter = new PayloadWriter<>(request, response,
                    DisplayDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_DISPLAY),
                    displayDTO,cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** LOADER INSTRUCTION METHODS
    // *********************************************

    @ApiOperation(
            value = "Create loader instruction file",
            notes = "Creates loader instruction file and then submits a new Job.",
            tags = {"Instructions"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Loader")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/instructions/loader", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<LoaderInstructionFilesDTO> createLoaderInstruction(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<LoaderInstructionFilesDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<LoaderInstructionFilesDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadReader<LoaderInstructionFilesDTO> payloadReader = new PayloadReader<>(LoaderInstructionFilesDTO.class);
            LoaderInstructionFilesDTO loaderInstructionFilesDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);
            LoaderInstructionFilesDTO loaderInstructionFilesDTONew = loaderInstructionFilesService.createInstruction(
                    cropType, loaderInstructionFilesDTOToCreate);

            PayloadWriter<LoaderInstructionFilesDTO> payloadWriter = new PayloadWriter<>(request, response,
                    LoaderInstructionFilesDTO.class);

            payloadWriter.writeSingleItemForId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_FILE_LOAD_INSTRUCTIONS),
                    loaderInstructionFilesDTONew,
                    loaderInstructionFilesDTONew.getInstructionFileName(),cropType);

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get a loader instruction file",
            notes = "Gets the loader instruction file entity having the specified instruction file name.",
            tags = {"Instructions"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Loader : instructionFileName")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/instructions/loader/{instructionFileName}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<LoaderInstructionFilesDTO> getLoaderInstruction(
            @ApiParam(value = "Name of the instruction file to be retrieved.", required = true)
            @PathVariable("instructionFileName") String instructionFileName,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<LoaderInstructionFilesDTO> returnVal = new PayloadEnvelope<>();
        try {

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);
            LoaderInstructionFilesDTO loaderInstructionFilesDTO = loaderInstructionFilesService.getStatus(cropType, instructionFileName);

            PayloadWriter<LoaderInstructionFilesDTO> payloadWriter = new PayloadWriter<>(request, response,
                    LoaderInstructionFilesDTO.class);

            payloadWriter.writeSingleItemForId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_FILE_LOAD_INSTRUCTIONS),
                    loaderInstructionFilesDTO,
                    loaderInstructionFilesDTO.getInstructionFileName(),cropType);

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get loader job status by job name",
            notes = "Gets the loading job status along with other job details having the specified Job Name.",
            tags = {"Instructions"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Loader.jobs : jobName")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/instructions/loader/jobs/{jobName}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<JobDTO> getLoaderInstructionStatus(
            @ApiParam(value = "Name of the job", required =  true)
            @PathVariable("jobName") String jobName,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<JobDTO> returnVal = new PayloadEnvelope<>();
        try {

            //String cropType = 
            CropRequestAnalyzer.getGobiiCropType(request);
            JobDTO jobDTO = jobService.getJobByJobName(jobName);

            PayloadWriter<JobDTO> payloadWriter = new PayloadWriter<>(request, response,
                    JobDTO.class);

            payloadWriter.writeSingleItemForId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_FILE_LOADER_JOBS),
                    jobDTO,
                    jobDTO.getJobName(),cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** EXTRACTOR INSTRUCTION METHODS
    // *********************************************

    @ApiOperation(
            value = "Create an extractor instruction file",
            notes = "Creates extractor instruction file and then submits a new Job.",
            tags = {"Instructions"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Extractor")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/instructions/extractor", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<ExtractorInstructionFilesDTO> createExtractorInstruction(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<ExtractorInstructionFilesDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ExtractorInstructionFilesDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadReader<ExtractorInstructionFilesDTO> payloadReader = new PayloadReader<>(ExtractorInstructionFilesDTO.class);
            ExtractorInstructionFilesDTO extractorInstructionFilesDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);
            String cropType = CropRequestAnalyzer.getGobiiCropType(request);

            ExtractorInstructionFilesDTO extractorInstructionFilesDTONew = extractorInstructionFilesService.createInstruction(cropType, extractorInstructionFilesDTOToCreate);


            PayloadWriter<ExtractorInstructionFilesDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ExtractorInstructionFilesDTO.class);

            payloadWriter.writeSingleItemForId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_FILE_EXTRACTOR_INSTRUCTIONS),
                    extractorInstructionFilesDTONew,
                    extractorInstructionFilesDTONew.getJobId(),cropType);

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Download an extractor instructor file",
            notes = "Retrieves the extractor instruction file entity having the specified instruction file name.",
            tags = {"Instructions"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Extractor : instructionFileName")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/instructions/extractor/{instructionFileName}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ExtractorInstructionFilesDTO> getExtractorInstruction(
            @ApiParam(value = "Name of the instruction file to be retrieved", required = true)
            @PathVariable("instructionFileName") String instructionFileName,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ExtractorInstructionFilesDTO> returnVal = new PayloadEnvelope<>();
        try {

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);
            ExtractorInstructionFilesDTO extractorInstructionFilesDTO = extractorInstructionFilesService.getStatus(cropType, instructionFileName);

            PayloadWriter<ExtractorInstructionFilesDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ExtractorInstructionFilesDTO.class);

            payloadWriter.writeSingleItemForId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_FILE_EXTRACTOR_INSTRUCTIONS),
                    extractorInstructionFilesDTO,
                    extractorInstructionFilesDTO.getInstructionFileName(),cropType);

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get extractor job status by jobName",
            notes = "Retrieves the extract job status along with other job details having the specified Job Name.",
            tags = {"Instructions"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Extractor.jobs : jobName")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/instructions/extractor/jobs/{jobName}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<JobDTO> getExtractorInstructionStatus(
            @ApiParam(value = "Name of the job", required = true)
            @PathVariable("jobName") String jobName,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<JobDTO> returnVal = new PayloadEnvelope<>();
        try {

            //String cropType = 
            CropRequestAnalyzer.getGobiiCropType(request);
            JobDTO jobDTO = jobService.getJobByJobName(jobName);

            PayloadWriter<JobDTO> payloadWriter = new PayloadWriter<>(request, response,
                    JobDTO.class);

            payloadWriter.writeSingleItemForId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_FILE_EXTRACTOR_JOBS),
                    jobDTO,
                    jobDTO.getJobName(),cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** MANIFEST METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a manifest",
            notes = "Creates a Manifest entity for GOBii system.",
            tags = {"Manifests"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Manifests"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="A fixed set of markers that run in a single reaction. " +
                                            "Use the manifest to extract data by the manifest name, " +
                                            "rather than listing the markers contained in the manifest."
                            )
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/manifests", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<ManifestDTO> createManifest(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<ManifestDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ManifestDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<ManifestDTO> payloadReader = new PayloadReader<>(ManifestDTO.class);
            ManifestDTO manifestDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            ManifestDTO manifestDTONew = manifestService.createManifest(manifestDTOToCreate);

            PayloadWriter<ManifestDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ManifestDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MANIFEST),
                    manifestDTONew, cropType());
        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
            LOGGER.error(e.getMessage());
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Update a manifest",
            notes = "Updates the Manifest entity having the specified manifestId.",
            tags = {"Manifests"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Manifests : manifestId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/manifests/{manifestId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<ManifestDTO> replaceManifest(
            @RequestBody PayloadEnvelope<ManifestDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Manifest to be updated", required = true)
            @PathVariable("manifestId") Integer manifestId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ManifestDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<ManifestDTO> payloadReader = new PayloadReader<>(ManifestDTO.class);
            ManifestDTO manifestDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            ManifestDTO manifestDTOReplaced = manifestService.replaceManifest(manifestId, manifestDTOToReplace);

            PayloadWriter<ManifestDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ManifestDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MANIFEST),
                    manifestDTOReplaced, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "List all manifests",
            notes = "Lists an unfiltered list of all Manifest entities.",
            tags = {"Manifests"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Manifests")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/manifests", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ManifestDTO> getManifests(HttpServletRequest request,
                                                     HttpServletResponse response) {

        PayloadEnvelope<ManifestDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<ManifestDTO> manifestDTOS = manifestService.getManifests();

            PayloadWriter<ManifestDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ManifestDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MANIFEST),
                    manifestDTOS,cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Get manifests by manifestId",
            notes = "Gets the Manifest entity having the specified ID.",
            tags = {"Manifests"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Manifests : manifestId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/manifests/{manifestId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ManifestDTO> getManifestById(
            @ApiParam(value = "ID of the Manifest to be retrieved", required = true)
            @PathVariable("manifestId") Integer manifestId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ManifestDTO> returnVal = new PayloadEnvelope<>();

        try {

            ManifestDTO manifestDTO = manifestService.getManifestById(manifestId);

            PayloadWriter<ManifestDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ManifestDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MANIFEST),
                    manifestDTO,cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** MARKER METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new marker",
            notes = "Creates a new marker in the system.",
            tags = {"Markers"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Markers"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="A Marker Group defines a group of markers, " +
                                            "the marker platform, and any optional favorable alleles for each marker."
                            )
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/markers", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<MarkerDTO> createMarker(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<MarkerDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<MarkerDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadReader<MarkerDTO> payloadReader = new PayloadReader<>(MarkerDTO.class);
            MarkerDTO markerDtoToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            MarkerDTO dataSetDTONew = markerService.createMarker(markerDtoToCreate);


            PayloadWriter<MarkerDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MarkerDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MARKERS),
                    dataSetDTONew,cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Update a marker by markerId",
            notes = "Updates the Marker entity having the specified markerId.",
            tags = {"Markers"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Markers : markerId")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/markers/{markerId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<MarkerDTO> replaceMarker(
            @RequestBody PayloadEnvelope<MarkerDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Marker to be updated", required = true)
            @PathVariable("markerId") Integer markerId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<MarkerDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<MarkerDTO> payloadReader = new PayloadReader<>(MarkerDTO.class);
            MarkerDTO markerDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            MarkerDTO dataSetDTOReplaced = markerService.replaceMarker(markerId, markerDTOToReplace);


            PayloadWriter<MarkerDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MarkerDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MARKERS),
                    dataSetDTOReplaced,cropType());
//

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all markers",
            notes = "Lists all the existing markers in the system.",
            tags = {"Markers"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Markers")                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/markers", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<MarkerDTO> getMarkers(HttpServletRequest request,
                                                 HttpServletResponse response) {

        PayloadEnvelope<MarkerDTO> returnVal = new PayloadEnvelope<>();
        try {

            //PayloadReader<MarkerDTO> payloadReader = new PayloadReader<>(MarkerDTO.class);
            List<MarkerDTO> markerDTOs = markerService.getMarkers();

            PayloadWriter<MarkerDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MarkerDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MARKERS),
                    markerDTOs,cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get a marker by markerId",
            notes = "Retrieves the Marker entity having the specified ID.",
            tags = {"Markers"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Markers : markerId")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/markers/{markerId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<MarkerDTO> getMarkerById(
            @ApiParam(value = "ID of the Marker to be extracted", required = true)
            @PathVariable("markerId") Integer markerId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<MarkerDTO> returnVal = new PayloadEnvelope<>();
        try {

            MarkerDTO dataSetDTO = markerService.getMarkerById(markerId);

            PayloadWriter<MarkerDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MarkerDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MARKERS),
                    dataSetDTO,cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all markers from markers search",
            notes = "List Marker search results.",
            tags = {"Markers"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="MarkerSearch")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/marker-search",
            params = {"name"},
            method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<MarkerDTO> getMarkerByName(
            @ApiParam(value = "Name of the marker", required = true)
            @RequestParam("name") String name,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<MarkerDTO> returnVal = new PayloadEnvelope<>();
        try {

            List<MarkerDTO> markersByName = markerService.getMarkersByName(name);

            PayloadWriter<MarkerDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MarkerDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MARKERS),
                    markersByName, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** NameIDList
    // *********************************************

    @ApiOperation(
            value = "List all NameIds",
            notes = "List of name/ID combination for a given entity. " +
                    "For the list of entities supported see class GobiiEntityNameType." +
                    "List can further be filtered out by specifying the filter type and value." +
                    "For the list of filter types supported see class GobiiFilterType." +
                    "Example use case: entity = CV; filterType = NAMES_BY_TYPE_NAME; filterValue = status" +
                    "Result will be a list of CV terms having status as the cv group",
            tags = {"Names"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Names : entity")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/names/{entity}",
            method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<NameIdDTO> getNames(
            @ApiParam(value = "The entity to be retrieved", required = true)
            @PathVariable("entity") String entity,
            @ApiParam(value = "The filter type for the name list")
            @RequestParam(value = "filterType", required = false) String filterType,
            @ApiParam(value = "The value for the filter type")
            @RequestParam(value = "filterValue", required = false) String filterValue,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<NameIdDTO> returnVal = new PayloadEnvelope<>();
        try {

            // We are getting raw string parameters from the uri and query parameters;
            // here is the place to validate the types before sending the parameters on the service layer,
            // which should only be dealing with GOBII native natives.
            //
            // **************** Get entity type
            GobiiEntityNameType gobiiEntityNameType;
            try {
                gobiiEntityNameType = GobiiEntityNameType.valueOf(entity.toUpperCase());
            } catch (IllegalArgumentException e) {

                throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.NONE,
                        "Unsupported entity for list request: " + entity);
            }


            // **************** If a filter was specified, convert it as well
            GobiiFilterType gobiiFilterType = GobiiFilterType.NONE;
            Object typedFilterValue = filterValue;
            if (!LineUtils.isNullOrEmpty(filterType)) {
                try {
                    gobiiFilterType = GobiiFilterType.valueOf(filterType.toUpperCase());
                } catch (IllegalArgumentException e) {

                    throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.NONE,
                            "Unsupported filter for list request: "
                                    + filterType
                                    + " for entity "
                                    + gobiiEntityNameType);
                }

                if (!LineUtils.isNullOrEmpty(filterValue)) {

                    if (GobiiFilterType.NAMES_BY_TYPEID == gobiiFilterType) {
                        if (NumberUtils.isNumber(filterValue)) {
                            typedFilterValue = Integer.valueOf(filterValue);
                        } else {
                            throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                                    GobiiValidationStatusType.NONE,
                                    "Value for "
                                            + filterType
                                            + " value is not a number: "
                                            + filterValue
                                            + " for entity "
                                            + gobiiEntityNameType);
                        }

                    } else if (GobiiFilterType.NAMES_BY_TYPE_NAME == gobiiFilterType) {
                        // there is nothing to test here -- the string could be anything
                        // add additional validation tests for other filter types

                    } else {
                        throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                                GobiiValidationStatusType.NONE,
                                "Unable to do type checking on filter value for filter type "
                                        + filterType
                                        + " with value "
                                        + filterValue
                                        + " for entity "
                                        + gobiiEntityNameType);
                    }

                } else {
                    throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.NONE,
                            "A value was not supplied for filter: "
                                    + filterType
                                    + " for entity "
                                    + gobiiEntityNameType);
                }
            }


            Integer callLimit = RestResourceLimits.getResourceLimit(RestResourceId.GOBII_NAMES, RestMethodType.GET, entity.toUpperCase());
            DtoMapNameIdParams dtoMapNameIdParams = new DtoMapNameIdParams(gobiiEntityNameType, gobiiFilterType, typedFilterValue, callLimit);

            List<NameIdDTO> nameIdList = nameIdListService.getNameIdList(dtoMapNameIdParams);

            PayloadWriter<NameIdDTO> payloadWriter = new PayloadWriter<>(request, response, NameIdDTO.class);
            payloadWriter.writeList(returnVal,
                    GobiiEntityNameConverter.toServiceRequestId(request.getContextPath(),
                            gobiiEntityNameType),
                    nameIdList, cropType());


            // for call limit, the case of /names, we need to add the entity type
            // so that the limit can be looked up by entity type
            payloadWriter.setCallLimitToHeader(returnVal,
                    GobiiUriFactory.resourceColl(request.getContextPath(),
                            RestResourceId.GOBII_NAMES)
                            .addUriParam("entity", entity));

            //String cropType = 
            returnVal.getHeader().getCropType();

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Create a new NameID",
            notes = "Retrieves a list of name/ID combination for a given entity and name list. " +
                    "For the list of entities supported see class GobiiEntityNameType." +
                    "This is service is specifically implemented for these filter types: " +
                    "NAMES_BY_NAME_LIST - given a list of names, return the same list with the corresponding ID in the database. If name doesn't exist, ID will be 0." +
                    "NAMES_BY_NAME_LIST_RETURN_EXISTS - given a list of names, return the list of names with ID that exists in the database." +
                    "NAMES_BY_NAME_LIST_RETURN_ABSENT - given a list of names, return the list of names that doesn't exist in the database with 0 as the ID" +
                    "Filter value varies per entity. This can be cv group name, project ID, platform ID, etc." +
                    "Example use case: entity = CV; filterType = NAMES_BY_NAME_LIST; filterValue = germplasm_type" +
                    "Result will be a list of CV terms with ID having germplasm_type as the cv group",
            tags = {"Names"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Names : entity")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/names/{entity}",
            method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<NameIdDTO> getNamesByNameList(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<NameIdDTO> payloadEnvelope,
            @ApiParam(value = "The entity to be retrieved", required = true)
            @PathVariable("entity") String entity,
            @ApiParam(value = "The filter type for the name list")
            @RequestParam(value = "filterType", required = false) String filterType,
            @ApiParam(value = "The value for the filter type")
            @RequestParam(value = "filterValue", required = false) String filterValue,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<NameIdDTO> returnVal = new PayloadEnvelope<>();

        try {

            GobiiEntityNameType gobiiEntityNameType;
            try {
                gobiiEntityNameType = GobiiEntityNameType.valueOf(entity.toUpperCase());
            } catch (IllegalArgumentException e) {

                throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.NONE,
                        "Unsupported entity for list request: " + entity);
            }

            GobiiFilterType gobiiFilterType = GobiiFilterType.NONE;
            Object typedFilterValue = filterValue;
            List<NameIdDTO> nameIdDTOList = new ArrayList<>();

            if (!LineUtils.isNullOrEmpty(filterType)) {

                try {
                    gobiiFilterType = GobiiFilterType.valueOf(filterType.toUpperCase());
                } catch (IllegalArgumentException e) {

                    throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.NONE,
                            "Unsupported filter for list request: "
                                    + filterType
                                    + " for entity "
                                    + gobiiEntityNameType);
                }

                if (!LineUtils.isNullOrEmpty(filterValue)) {

                    if (GobiiFilterType.NAMES_BY_TYPEID == gobiiFilterType) {
                        if (NumberUtils.isNumber(filterValue)) {
                            typedFilterValue = Integer.valueOf(filterValue);
                        } else {
                            throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                                    GobiiValidationStatusType.NONE,
                                    "Value for "
                                            + filterType
                                            + " value is not a number: "
                                            + filterValue
                                            + " for entity "
                                            + gobiiEntityNameType);
                        }
                    } else if (GobiiFilterType.NAMES_BY_TYPE_NAME == gobiiFilterType) {
                        // there is nothing to test here -- the string could be anything
                        // add additional validation tests for other filter types
                    } else if (GobiiFilterType.NAMES_BY_NAME_LIST == gobiiFilterType || GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_ABSENT == gobiiFilterType || GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS == gobiiFilterType) {

                        PayloadReader<NameIdDTO> payloadReader = new PayloadReader<>(NameIdDTO.class);
                        nameIdDTOList = payloadReader.extractListOfItems(payloadEnvelope);

                    } else {
                        throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                                GobiiValidationStatusType.NONE,
                                "Unable to do type checking on filter value for filter type "
                                        + filterType
                                        + " with value "
                                        + filterValue
                                        + " for entity "
                                        + gobiiEntityNameType);
                    }

                } else {
                    throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.NONE,
                            "A value was not supplied for filter: "
                                    + filterType
                                    + " for entity "
                                    + gobiiEntityNameType);
                }
            }


            Integer callLimit = RestResourceLimits.getResourceLimit(RestResourceId.GOBII_NAMES, RestMethodType.POST, entity.toUpperCase());

            if (callLimit == null || new Integer(nameIdDTOList.size()) <= callLimit) {
                DtoMapNameIdParams dtoMapNameIdParams = new DtoMapNameIdParams(gobiiEntityNameType, gobiiFilterType, typedFilterValue, nameIdDTOList, callLimit);

                List<NameIdDTO> nameIdList = nameIdListService.getNameIdList(dtoMapNameIdParams);

                PayloadWriter<NameIdDTO> payloadWriter = new PayloadWriter<>(request, response, NameIdDTO.class);
                payloadWriter.writeList(returnVal,
                        GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                                RestResourceId.GOBII_NAMES),
                        nameIdList, cropType());

                // return the nameIdDTOs with null IDs
                for (NameIdDTO currentNameIdDTO : nameIdList) {

                    if (currentNameIdDTO.getId().equals(0)) {

                        returnVal.getPayload().getData().add(currentNameIdDTO);

                    }

                }
            } else {

                returnVal.getHeader().getStatus().addException(
                        new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                                GobiiValidationStatusType.RESOURCE_LIMIT,
                                "The POST to resource " +
                                        RestResourceId.GOBII_NAMES.getResourcePath()
                                        + "/"
                                        + entity
                                        + " exceeds the max POST limit of "
                                        + callLimit.toString()));

            } // if-else the call exceeds the limit


        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);


        return (returnVal);
    }


    // *********************************************
    // *************************** ORGANIZATION METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new organization",
            notes = "Creates a new organization in the system.",
            tags = {"Organizations"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Organizations"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="Organization is a company or institute. " +
                                            "Organization may be associated with a contact " +
                                            "in the Define Contacts page. An organization can " +
                                            "also describe a vendor and be associated with a " +
                                            "protocol to create a vendor-protocol in the Define Protocols page."
                            )
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/organizations", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<OrganizationDTO> createOrganization(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<OrganizationDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<OrganizationDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadReader<OrganizationDTO> payloadReader = new PayloadReader<>(OrganizationDTO.class);
            OrganizationDTO organizationDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            OrganizationDTO OrganizationDTONew = organizationService.createOrganization(organizationDTOToCreate);

            PayloadWriter<OrganizationDTO> payloadWriter = new PayloadWriter<>(request, response,
                    OrganizationDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ORGANIZATION),
                    OrganizationDTONew, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Update an organization by organizationId",
            notes = "Updates the Organization entity having the specified organizationId.",
            tags = {"Organizations"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Organizations : organizationId")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/organizations/{organizationId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<OrganizationDTO> replaceOrganization(
            @RequestBody PayloadEnvelope<OrganizationDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Organization to be updated", required = true)
            @PathVariable("organizationId") Integer organizationId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<OrganizationDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<OrganizationDTO> payloadReader = new PayloadReader<>(OrganizationDTO.class);
            OrganizationDTO organizationDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            OrganizationDTO organizationDTOReplaced = organizationService.replaceOrganization(organizationId, organizationDTOToReplace);

            PayloadWriter<OrganizationDTO> payloadWriter = new PayloadWriter<>(request, response,
                    OrganizationDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ORGANIZATION),
                    organizationDTOReplaced, cropType());


        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all organizations",
            notes = "List all the existing organizations in the system.",
            tags = {"Organizations"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Organizations")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<OrganizationDTO> getOrganizations(HttpServletRequest request,
                                                             HttpServletResponse response) {

        PayloadEnvelope<OrganizationDTO> returnVal = new PayloadEnvelope<>();
        try {

            //PayloadReader<OrganizationDTO> payloadReader = new PayloadReader<>(OrganizationDTO.class);
            List<OrganizationDTO> organizationDTOs = organizationService.getOrganizations();

            PayloadWriter<OrganizationDTO> payloadWriter = new PayloadWriter<>(request, response,
                    OrganizationDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ORGANIZATION),
                    organizationDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get organization by organizationId",
            notes = "Retrieves the Organization entity having the specified ID.",
            tags = {"Organizations"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Organizations : organizationId")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/organizations/{organizationId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<OrganizationDTO> getOrganizationsById(
            @ApiParam(value = "ID of the Organization to be extracted", required = true)
            @PathVariable("organizationId") Integer organizationId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<OrganizationDTO> returnVal = new PayloadEnvelope<>();
        try {

            //PayloadReader<OrganizationDTO> payloadReader = new PayloadReader<>(OrganizationDTO.class);
            OrganizationDTO organizationDTO = organizationService.getOrganizationById(organizationId);

            PayloadWriter<OrganizationDTO> payloadWriter = new PayloadWriter<>(request, response,
                    OrganizationDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ORGANIZATION),
                    organizationDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** MAPSET METHODS
    // *********************************************
    /*
     * NOTE: this implementation is incorrect: it is using getAllmapsetNames;
     * There needs to be a getAllMapset() method added. For now, the funcitonality
     * Provided by the LoadControlle remains in place and the client side tets have
     * not been modified. This funcitonality will have to be built out later.
     * Also note that the resource name /maps is correct but does not match
     * what is being used in ResourceBuilder on the client side*/
    @ApiOperation(
            value = "List all mapsets",
            notes = "Lists all the existing Mapsets in the system.",
            tags = {"Maps"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Maps"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="A mapset describes distances between " +
                                            "markers and their association to linkage " +
                                            "groups or chromosomes. A marker can belong to, " +
                                            "or be mapped to, one or multiple mapsets."
                            )
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/maps", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<MapsetDTO> getMaps(HttpServletRequest request,
                                              HttpServletResponse response) {

        PayloadEnvelope<MapsetDTO> returnVal = new PayloadEnvelope<>();
        try {

            List<MapsetDTO> mapsetDTOs = mapsetService.getAllMapsetNames();

            PayloadWriter<MapsetDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MapsetDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MAPSET),
                    mapsetDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** PLATFORM METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new platform",
            notes = "Creates a new Platform in the system.",
            tags = {"Platforms"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Platforms"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="The platform describes the general chemistry used to generate marker genotyping data. " +
                                            "Examples of platforms are KASP, Illumina, etc. All markers must be associated with a platform.\n" +
                                            "There are several considerations for defining platforms.\n" +
                                            "1. Consistency of data generated: the expectation is that within each platform, the genotyping data " +
                                            "generated by a marker name should give consistent results to allow for future composite scores by marker. " +
                                            "A marker that generates data using different chemistry or separation methods would likely give different results " +
                                            "and should be placed in separate platforms (or given a different marker name).\n" +
                                            "2. Avoiding unnecessary duplication of marker names:  if five different sequencing methods each " +
                                            "produce the same or overlapping marker names that give equivalent genotyping scores, they should " +
                                            "all be placed under a single platform. For example, \"Sequencing,\" and the five methods described using protocols. \n" +
                                            "3. Extract requirements: one of the primary extracts will be by   platform, and so consider how the " +
                                            "user will want to extract data."
                            )
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/platforms", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<PlatformDTO> createPlatform(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<PlatformDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<PlatformDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadReader<PlatformDTO> payloadReader = new PayloadReader<>(PlatformDTO.class);
            PlatformDTO platformDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            PlatformDTO platformDTONew = platformService.createPlatform(platformDTOToCreate);

            PayloadWriter<PlatformDTO> payloadWriter = new PayloadWriter<>(request, response,
                    PlatformDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PLATFORM),
                    platformDTONew, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Update a platform by platformId",
            notes = "Updates the Platform entity having the specified platformId.",
            tags = {"Platforms"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Platforms : platformId")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/platforms/{platformId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<PlatformDTO> replacePlatform(
            @RequestBody PayloadEnvelope<PlatformDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Platform to be updated", required = true)
            @PathVariable("platformId") Integer platformId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<PlatformDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<PlatformDTO> payloadReader = new PayloadReader<>(PlatformDTO.class);
            PlatformDTO platformDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            PlatformDTO platformDTOReplaced = platformService.replacePlatform(platformId, platformDTOToReplace);

            PayloadWriter<PlatformDTO> payloadWriter = new PayloadWriter<>(request, response,
                    PlatformDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PLATFORM),
                    platformDTOReplaced, cropType());


        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all platforms",
            notes = "List all the existing platforms in the system.",
            tags = {"Platforms"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Platforms")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/platforms", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<PlatformDTO> getPlatforms(HttpServletRequest request,
                                                     HttpServletResponse response) {

        PayloadEnvelope<PlatformDTO> returnVal = new PayloadEnvelope<>();
        try {

            //PayloadReader<PlatformDTO> payloadReader = new PayloadReader<>(PlatformDTO.class);
            List<PlatformDTO> platformDTOs = platformService.getPlatforms();

            PayloadWriter<PlatformDTO> payloadWriter = new PayloadWriter<>(request, response,
                    PlatformDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PLATFORM),
                    platformDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get a platform by platformId",
            notes = "Retrieves the Platform entity having the specified ID.",
            tags = {"Platforms"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Platforms : platformId")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/platforms/{platformId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<PlatformDTO> getPlatformsById(
            @ApiParam(value = "ID of the Platform to be extracted", required = true)
            @PathVariable("platformId") Integer platformId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<PlatformDTO> returnVal = new PayloadEnvelope<>();
        try {

            //PayloadReader<PlatformDTO> payloadReader = new PayloadReader<>(PlatformDTO.class);
            PlatformDTO platformDTO = platformService.getPlatformById(platformId);

            PayloadWriter<PlatformDTO> payloadWriter = new PayloadWriter<>(request, response,
                    PlatformDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PLATFORM),
                    platformDTO, cropType());

        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

            /*
               Almost all the v1 controllers methods have the same issue.
               Since, GDM-509 concerns only for platforms, fixing only that.
               TODO: this is being corrected in v3. But, in case,
                v1 is continued to be used irrespective of v3,
                this have to be corrected for all v1 controller methods with
                a new GSD ticket.
             */
            if(e.getGobiiValidationStatusType()
                    == GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST) {

                ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                        response,
                        HttpStatus.CREATED,
                        HttpStatus.NOT_FOUND);
                return  returnVal;
            }

        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "GET /platforms/protocols/{vendorProtocolId}",
            notes = "Gets the Platform entity having the specified Vendor Protocol ID.",
            tags = {"Platforms"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Protocols : vendorProtocolId")
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/platforms/protocols/{vendorProtocolId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<PlatformDTO> getPlatformDetailsByVendorProtocolId(
            @ApiParam(value = "ID of the vendor protocol", required = true)
            @PathVariable("vendorProtocolId") Integer vendorProtocolId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<PlatformDTO> returnVal = new PayloadEnvelope<>();
        try {

            //PayloadReader<PlatformDTO> payloadReader = new PayloadReader<>(PlatformDTO.class);
            PlatformDTO platformDTO = platformService.getPlatformDetailsByVendorProtocolId(vendorProtocolId);

            PayloadWriter<PlatformDTO> payloadWriter = new PayloadWriter<>(request, response,
                    PlatformDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PLATFORM),
                    platformDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }


    // *********************************************
    // *************************** PROJECT METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new project",
            notes = "Creates a new project in the system.",
            tags = {"Projects"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Projects"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="A project consists of a group of samples that are, " +
                                            "or will be, genotyped. A project belongs to a Principal Investigator (PI), " +
                                            "also called a PI contact. "
                            )
                    })
            }
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/projects", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<ProjectDTO> createProject(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<ProjectDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ProjectDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<ProjectDTO> payloadReader = new PayloadReader<>(ProjectDTO.class);

            ProjectDTO projectDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            ProjectDTO projectDTONew = projectService.createProject(projectDTOToCreate);


            PayloadWriter<ProjectDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ProjectDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PROJECTS),
                    projectDTONew, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Update a project by projectId",
            notes = "Updates the Project entity having the specified projectId.",
            tags = {"Projects"},
            extensions = {
            @Extension(properties = {
                    @ExtensionProperty(name="summary", value="Projects : projectId")
            })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/projects/{projectId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<ProjectDTO> replaceProject(
            @RequestBody PayloadEnvelope<ProjectDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Project to be updated", required = true)
            @PathVariable("projectId") Integer projectId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ProjectDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<ProjectDTO> payloadReader = new PayloadReader<>(ProjectDTO.class);
            ProjectDTO projectDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            ProjectDTO projectDTOReplaced = projectService.replaceProject(projectId, projectDTOToReplace);


            PayloadWriter<ProjectDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ProjectDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PROJECTS),
                    projectDTOReplaced, cropType());
//

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all projects",
            notes = "List all the existing projects in the system.",
            tags = {"Projects"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Projects")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ProjectDTO> getProjects(HttpServletRequest request,
                                                   HttpServletResponse response) {

        PayloadEnvelope<ProjectDTO> returnVal = new PayloadEnvelope<>();
        try {

            //PayloadReader<ProjectDTO> payloadReader = new PayloadReader<>(ProjectDTO.class);
            List<ProjectDTO> projectDTOs = projectService.getProjects();

            PayloadWriter<ProjectDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ProjectDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PROJECTS),
                    projectDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get a project by projectId",
            notes = "Retrieves the Project entity having the specified ID.",
            tags = {"Projects"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Projects : projectId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/projects/{projectId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ProjectDTO> getProjectsById(
            @ApiParam(value = "ID of the Project to be extracted", required = true)
            @PathVariable("projectId") Integer projectId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ProjectDTO> returnVal = new PayloadEnvelope<>();
        try {

            ProjectDTO projectDTO = projectService.getProjectById(projectId);

            PayloadWriter<ProjectDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ProjectDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PROJECTS),
                    projectDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }


    // *********************************************
    // *************************** EXPERIMENT METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new experiment",
            notes = "Creates a new experiment in the system.",
            tags = {"Experiments"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Experiments"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="An experiment defines the protocol and vendor" +
                                            " used to generate the genotyping data. More than one experiment can belong to a project."
                            )
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/experiments", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<ExperimentDTO> createExperiment(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<ExperimentDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ExperimentDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadReader<ExperimentDTO> payloadReader = new PayloadReader<>(ExperimentDTO.class);
            ExperimentDTO exprimentDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            ExperimentDTO exprimentDTONew = experimentService.createExperiment(exprimentDTOToCreate);

            PayloadWriter<ExperimentDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ExperimentDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_EXPERIMENTS),
                    exprimentDTONew, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Update an experiment by experimentId",
            notes = "Updates the Experiment entity having the specified experimentId.",
            tags = {"Experiments"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Experiments : experimentId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/experiments/{experimentId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<ExperimentDTO> replaceExperiment(
            @RequestBody PayloadEnvelope<ExperimentDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Experiment to be updated", required = true)
            @PathVariable("experimentId") Integer experimentId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ExperimentDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<ExperimentDTO> payloadReader = new PayloadReader<>(ExperimentDTO.class);
            ExperimentDTO exprimentDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            ExperimentDTO exprimentDTOReplaced = experimentService.replaceExperiment(experimentId, exprimentDTOToReplace);


            PayloadWriter<ExperimentDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ExperimentDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_EXPERIMENTS),
                    exprimentDTOReplaced, cropType());


        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all experiments",
            notes = "List all the existing experiments in the system.",
            tags = {"Experiments"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Experiments")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/experiments", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ExperimentDTO> getExperiments(HttpServletRequest request,
                                                         HttpServletResponse response) {

        PayloadEnvelope<ExperimentDTO> returnVal = new PayloadEnvelope<>();
        try {

            List<ExperimentDTO> experimentDTOs = experimentService.getExperiments();


            PayloadWriter<ExperimentDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ExperimentDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_EXPERIMENTS),
                    experimentDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get a experiment by experimentId",
            notes = "Gets the Experiment entity having the specified ID.",
            tags = {"Experiments"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Experiments : experimentId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/experiments/{experimentId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ExperimentDTO> getExperimentsById(
            @ApiParam(value = "ID of the Experiment to be updated", required = true)
            @PathVariable("experimentId") Integer experimentId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ExperimentDTO> returnVal = new PayloadEnvelope<>();
        try {

            ExperimentDTO experimentDTO = experimentService.getExperimentById(experimentId);

            PayloadWriter<ExperimentDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ExperimentDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_EXPERIMENTS),
                    experimentDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** PROTOCOL METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new protocol",
            notes = "Creates a new Protocol in the system.",
            tags = {"Protocols"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Protocols"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="Protocols are grouped within platforms. " +
                                            "Protocol defines the specific method that " +
                                            "is used to generate the genotyping data for a " +
                                            "set of markers within a platform. There could be" +
                                            " minor differences in the genotyping data generated " +
                                            "by different protocols within the same platform, but the " +
                                            "data should not be substantially different for the " +
                                            "same marker for the same marker. Examples of protocols " +
                                            "are different enzymes used to generate GbS data within a sequencing platform."
                            )
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/protocols", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<ProtocolDTO> createProtocol(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<ProtocolDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ProtocolDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadReader<ProtocolDTO> payloadReader = new PayloadReader<>(ProtocolDTO.class);
            ProtocolDTO protocolDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            ProtocolDTO protocolDTONew = protocolService.createProtocol(protocolDTOToCreate);

            PayloadWriter<ProtocolDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ProtocolDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PROTOCOL),
                    protocolDTONew, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Update a protocol by protocolId",
            notes = "Updates the Protocol entity having the specified protocolId.",
            tags = {"Protocols"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Protocols : protocolId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/protocols/{protocolId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<ProtocolDTO> replaceProtocol(
            @RequestBody PayloadEnvelope<ProtocolDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Protocol to be updated", required = true)
            @PathVariable("protocolId") Integer protocolId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ProtocolDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<ProtocolDTO> payloadReader = new PayloadReader<>(ProtocolDTO.class);
            ProtocolDTO protocolDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            ProtocolDTO protocolDTOReplaced = protocolService.replaceProtocol(protocolId, protocolDTOToReplace);

            PayloadWriter<ProtocolDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ProtocolDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PROTOCOL),
                    protocolDTOReplaced, cropType());
        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get a protocol by protocolId",
            notes = "Retrieves the Protocol entity having the specified ID.",
            nickname = "getProtocol",
            tags = {"Protocols"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Protocols : protocolId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/protocols/{protocolId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ProtocolDTO> replaceProtocol(
            @ApiParam(value = "ID of the Protocol to be extracted", required = true)
            @PathVariable("protocolId") Integer protocolId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ProtocolDTO> returnVal = new PayloadEnvelope<>();

        try {


            ProtocolDTO protocolDTO = protocolService.getProtocolById(protocolId);

            PayloadWriter<ProtocolDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ProtocolDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PROTOCOL),
                    protocolDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all protocols",
            notes = "List all the existing protocols in the system.",
            tags = {"Protocols"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Protocols")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/protocols", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ProtocolDTO> getProtocols(HttpServletRequest request,
                                                     HttpServletResponse response) {

        PayloadEnvelope<ProtocolDTO> returnVal = new PayloadEnvelope<>();
        try {

            //PayloadReader<ProtocolDTO> payloadReader = new PayloadReader<>(ProtocolDTO.class);
            List<ProtocolDTO> ProtocolDTOs = protocolService.getProtocols();

            PayloadWriter<ProtocolDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ProtocolDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PROTOCOL),
                    ProtocolDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Create new vendors for a given protocolId",
            notes = "Creates a new Vendor Protocol in the system for specified Protocol ID.",
            tags = {"Protocols.vendor"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Protocols.vendors"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="Selection of a vendor allows for a unique combination of the vendor" +
                                            " and protocol to be defined. Vendor names are generated in Define | Organization. " +
                                            "For additional information, refer to Organizations."
                            )
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/protocols/{protocolId:[\\d]+}/vendors", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<OrganizationDTO> addVendorToProtocol(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<OrganizationDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Protocol")
            @PathVariable("protocolId") Integer protocolId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<OrganizationDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<OrganizationDTO> payloadReader = new PayloadReader<>(OrganizationDTO.class);
            OrganizationDTO organizationDTO = payloadReader.extractSingleItem(payloadEnvelope);

            OrganizationDTO protocolDTOAssociated = protocolService.addVendotrToProtocol(protocolId, organizationDTO);

            PayloadWriter<OrganizationDTO> payloadWriter = new PayloadWriter<>(request, response,
                    OrganizationDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ORGANIZATION),
                    protocolDTOAssociated, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Update vendors for a given protocolId",
            notes = "Updates the Vendor Protocol entity having the specified protocolId.",
            tags = {"Protocols"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Protocols.vendors")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/protocols/{protocolId:[\\d]+}/vendors", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<OrganizationDTO> updateOrReplaceVendorProtocol(
            @RequestBody PayloadEnvelope<OrganizationDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Protocol", required = true)
            @PathVariable("protocolId") Integer protocolId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<OrganizationDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<OrganizationDTO> payloadReader = new PayloadReader<>(OrganizationDTO.class);
            OrganizationDTO organizationDTO = payloadReader.extractSingleItem(payloadEnvelope);

            OrganizationDTO protocolDTOAssociated = protocolService.updateOrReplaceVendotrToProtocol(protocolId, organizationDTO);

            PayloadWriter<OrganizationDTO> payloadWriter = new PayloadWriter<>(request, response,
                    OrganizationDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_ORGANIZATION),
                    protocolDTOAssociated, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all vendors for a given protocolId",
            notes = "List all the vendor protocols given protocolId in the system.",
            tags = {"Protocols"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Protocols.vendors")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/protocols/{protocolId:[\\d]+}/vendors", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<OrganizationDTO> getVendorsForProtocol(
            @ApiParam(value = "ID of the Protocol ID", required = true)
            @PathVariable("protocolId") Integer protocolId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<OrganizationDTO> returnVal = new PayloadEnvelope<>();

        try {


            List<OrganizationDTO> organizationDTOs = this.protocolService.getVendorsForProtocolByProtocolId(protocolId);

            PayloadWriter<OrganizationDTO> payloadWriter = new PayloadWriter<>(request, response,
                    OrganizationDTO.class);


            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceColl(request.getContextPath(),
                            RestResourceId.GOBII_PROTOCOL)
                            .addUriParam("protocolId")
                            .setParamValue("protocolId", protocolId.toString())
                            .appendSegment(RestResourceId.GOBII_VENDORS)
                            .addUriParam("id"), // <-- this is the one that PayloadWriter will set based on the list
                    organizationDTOs, cropType());

        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all protocols for a given experimentId",
            notes = "Retrieves all the protocols having the specified experimentId in the system.",
            tags = {"Experiments"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Experiments.protocols")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/experiments/{experimentId:[\\d]+}/protocols", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ProtocolDTO> getProtocolByExperimentId(
            @ApiParam(value = "ID of the Experiment", required = true)
            @PathVariable("experimentId") Integer experimentId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ProtocolDTO> returnVal = new PayloadEnvelope<>();
        try {

            ProtocolDTO protocolDTO = protocolService.getProtocolsByExperimentId(experimentId);

            PayloadWriter<ProtocolDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ProtocolDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_PROTOCOL),
                    protocolDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** FILE PREVIEW METHODS
    // *********************************************

    @ApiOperation(
            value = "Add a loader file to given directory",
            notes = "Updates a directory in the system that will be used for storing the data files for loading",
            tags = {"Files"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Loader : directoryName")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/files/loader/{directoryName}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<LoaderFilePreviewDTO> createLoaderFileDirectory(
            @ApiParam(value = "Name of the directory/folder", required = true)
            @PathVariable("directoryName") String directoryName,
            @RequestBody PayloadEnvelope<LoaderFilePreviewDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<LoaderFilePreviewDTO> returnVal = new PayloadEnvelope<>();
        try {

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);

            LoaderFilePreviewDTO loaderFilePreviewDTO = loaderFilesService.makeDirectory(cropType, directoryName);
            PayloadWriter<LoaderFilePreviewDTO> payloadWriter = new PayloadWriter<>(request, response,
                    LoaderFilePreviewDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_FILE_LOAD),
                    loaderFilePreviewDTO, cropType
            );

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List of loader files in a given directory",
            notes = "Gets file preview for the specified directory name",
            tags = {"Files"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Loader : directoryName")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/files/loader/{directoryName}",
            params = {"fileFormat"},
            method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<LoaderFilePreviewDTO> getFilePreviewBySearch(
            @ApiParam(value = "Name of the directory", required = true)
            @PathVariable("directoryName") String directoryName,
            @ApiParam(value = "Format/Extension of the file")
            @RequestParam(value = "fileFormat", required = false) String fileFormat,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<LoaderFilePreviewDTO> returnVal = new PayloadEnvelope<>();
        try {

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);
            LoaderFilePreviewDTO loaderFilePreviewDTO = loaderFilesService.getPreview(cropType, directoryName, fileFormat);
            PayloadWriter<LoaderFilePreviewDTO> payloadWriter = new PayloadWriter<>(request, response,
                    LoaderFilePreviewDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_FILE_LOAD),
                    loaderFilePreviewDTO, cropType
            );

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    // *********************************************
    // *************************** MAPSET METHODS
    // *********************************************

    @ApiOperation(
            value = "Create a new Mapset",
            notes = "Creates a new mapset in the system.",
            tags = {"Maps"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Mapsets"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="A mapset describes distances between " +
                                            "markers and their association to linkage " +
                                            "groups or chromosomes. A marker can belong to, " +
                                            "or be mapped to, one or multiple mapsets."
                            )
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/mapsets", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<MapsetDTO> createMapset(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<MapsetDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<MapsetDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<MapsetDTO> payloadReader = new PayloadReader<>(MapsetDTO.class);
            MapsetDTO mapsetDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            MapsetDTO mapsetDTONew = mapsetService.createMapset(mapsetDTOToCreate);

            PayloadWriter<MapsetDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MapsetDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MAPSET),
                    mapsetDTONew, cropType());
        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
            LOGGER.error(e.getMessage());
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Update a mapset by mapsetId",
            notes = "Updates the Mapset entity having the specified mapsetId.",
            tags = {"Maps"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Mapsets : mapsetId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/mapsets/{mapsetId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<MapsetDTO> replaceMapset(
            @RequestBody PayloadEnvelope<MapsetDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Mapset to be updated", required = true)
            @PathVariable("mapsetId") Integer mapsetId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<MapsetDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<MapsetDTO> payloadReader = new PayloadReader<>(MapsetDTO.class);
            MapsetDTO mapsetDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            MapsetDTO mapsetDTOReplaced = mapsetService.replaceMapset(mapsetId, mapsetDTOToReplace);

            PayloadWriter<MapsetDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MapsetDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MAPSET),
                    mapsetDTOReplaced, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "List all mapsets",
            notes = "List all the existing mapsets in the system.",
            tags = {"Maps"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Mapsets")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/mapsets", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<MapsetDTO> getMapsets(HttpServletRequest request,
                                                 HttpServletResponse response) {

        PayloadEnvelope<MapsetDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<MapsetDTO> mapsetDTOs = mapsetService.getMapsets();

            PayloadWriter<MapsetDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MapsetDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MAPSET),
                    mapsetDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Get mapsets by mapsetId",
            notes = "Gets the Mapset entity having the specified ID.",
            tags = {"Maps"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Mapsets : mapsetId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/mapsets/{mapsetId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<MapsetDTO> getMapsetById(
            @ApiParam(value = "ID of the Mapset to be extracted", required = true)
            @PathVariable("mapsetId") Integer mapsetId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<MapsetDTO> returnVal = new PayloadEnvelope<>();

        try {

            MapsetDTO mapsetDTO = mapsetService.getMapsetById(mapsetId);

            PayloadWriter<MapsetDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MapsetDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MAPSET),
                    mapsetDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** MARKERGROUP METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new Marker group",
            notes = "Creates a new marker group in the system.",
            tags = {"Markers"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="MarkerGroups")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/markergroups", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<MarkerGroupDTO> createMarkerGroup(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<MarkerGroupDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<MarkerGroupDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<MarkerGroupDTO> payloadReader = new PayloadReader<>(MarkerGroupDTO.class);
            MarkerGroupDTO markerGroupDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            MarkerGroupDTO markerGroupDTONew = markerGroupService.createMarkerGroup(markerGroupDTOToCreate);

            PayloadWriter<MarkerGroupDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MarkerGroupDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MARKERGROUP),
                    markerGroupDTONew, cropType());
        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
            LOGGER.error(e.getMessage());
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Update a marker by markergroupId",
            notes = "Updates the Marker Group entity having the specified markerGroupId.",
            tags = {"Markers"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="MarkerGroups : markerGroupId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/markergroups/{markerGroupId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<MarkerGroupDTO> replaceMarkerGroup(
            @RequestBody PayloadEnvelope<MarkerGroupDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Marker Group to be updated", required = true)
            @PathVariable("markerGroupId") Integer markerGroupId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<MarkerGroupDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<MarkerGroupDTO> payloadReader = new PayloadReader<>(MarkerGroupDTO.class);
            MarkerGroupDTO markerGroupDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            MarkerGroupDTO markerGroupDTOReplaced = markerGroupService.replaceMarkerGroup(markerGroupId, markerGroupDTOToReplace);

            PayloadWriter<MarkerGroupDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MarkerGroupDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MARKERGROUP),
                    markerGroupDTOReplaced,cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "List all markergroups",
            notes = "Lists all the existing marker groups in the system.",
            tags = {"Markers"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="MarkerGroups")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/markergroups", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<MarkerGroupDTO> getMarkerGroups(HttpServletRequest request,
                                                           HttpServletResponse response) {

        PayloadEnvelope<MarkerGroupDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<MarkerGroupDTO> markerGroupDTOs = markerGroupService.getMarkerGroups();

            PayloadWriter<MarkerGroupDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MarkerGroupDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MARKERGROUP),
                    markerGroupDTOs, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Gets all markers in a given markergroup",
            notes = "Retrieves the Marker Group entity having the specified ID.",
            tags = {"Markers"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="MarkerGroups : markerGroupId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/markergroups/{markerGroupId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<MarkerGroupDTO> getMarkerGroupById(
            @ApiParam(value = "ID of the marker group to be updated", required = true)
            @PathVariable("markerGroupId") Integer markerGroupId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<MarkerGroupDTO> returnVal = new PayloadEnvelope<>();

        try {

            MarkerGroupDTO markerGroupDTO = markerGroupService.getMarkerGroupById(markerGroupId);

            PayloadWriter<MarkerGroupDTO> payloadWriter = new PayloadWriter<>(request, response,
                    MarkerGroupDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_MARKERGROUP),
                    markerGroupDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** REFERENCE METHODS
    // *********************************************
    @ApiOperation(
            value = "Create a new reference",
            notes = "Creates a new reference in the system.",
            tags = {"References"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="References"),
                            @ExtensionProperty(
                                    name="tag-description",
                                    value="The authoritative genome reference and " +
                                            "associated linked files for a physical mapset."
                            )
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/references", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<ReferenceDTO> createReference(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<ReferenceDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ReferenceDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<ReferenceDTO> payloadReader = new PayloadReader<>(ReferenceDTO.class);
            ReferenceDTO referenceDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            ReferenceDTO referenceDTONew = referenceService.createReference(referenceDTOToCreate);

            PayloadWriter<ReferenceDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ReferenceDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_REFERENCE),
                    referenceDTONew, cropType());
        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
            LOGGER.error(e.getMessage());
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Update a reference by referenceId",
            notes = "Updates the Reference entity having the specified referenceId.",
            tags = {"References"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="References : referenceId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/references/{referenceId:[\\d]+}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<ReferenceDTO> replaceReference(
            @RequestBody PayloadEnvelope<ReferenceDTO> payloadEnvelope,
            @ApiParam(value = "ID of the Reference to be updated", required = true)
            @PathVariable("referenceId") Integer referenceId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ReferenceDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<ReferenceDTO> payloadReader = new PayloadReader<>(ReferenceDTO.class);
            ReferenceDTO referenceDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            ReferenceDTO referenceDTOReplaced = referenceService.replaceReference(referenceId, referenceDTOToReplace);

            PayloadWriter<ReferenceDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ReferenceDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_REFERENCE),
                    referenceDTOReplaced, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "List all references",
            notes = "Lists all the existing references in the system.",
            tags = {"References"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="References")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/references", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ReferenceDTO> getReferences(HttpServletRequest request,
                                                       HttpServletResponse response) {

        PayloadEnvelope<ReferenceDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<ReferenceDTO> referenceDTOS = referenceService.getReferences();

            PayloadWriter<ReferenceDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ReferenceDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_REFERENCE),
                    referenceDTOS, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "GET /references/{referenceId}",
            notes = "Gets the Reference entity having the specified ID.",
            tags = {"References"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="References : referenceId")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/references/{referenceId:[\\d]+}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<ReferenceDTO> getReferenceById(
            @ApiParam(value = "ID of the Reference to be extracted", required = true)
            @PathVariable("referenceId") Integer referenceId,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<ReferenceDTO> returnVal = new PayloadEnvelope<>();

        try {

            ReferenceDTO referenceDTO = referenceService.getReferenceById(referenceId);

            PayloadWriter<ReferenceDTO> payloadWriter = new PayloadWriter<>(request, response,
                    ReferenceDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_REFERENCE),
                    referenceDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    // *********************************************
    // *************************** FILE UPLOAD/DOWNLOAD
    // *********************************************

    /***
     * Uplaod an arbitary file to the specified destination
     * @param destinationType
     * @param fileName
     * @param file
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    @ApiOperation(
            value = "Upload a file to given destination",
            notes = "Uploads an arbitrary file to the specified destination",
            tags = {"Files"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Files : destinationType")
                    })}
    )
    //OpenAPI specification uses "string" as datatype for file, but the swagger automatically
    //adds "ref" as datatype for file parameter. So, an Implicit parameter is added and the original
    //parameter is made hidden.
    @ApiImplicitParams({
            @ApiImplicitParam(name="file", value="The file to be uploaded",
                    required = true, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/files/{destinationType}",
            method = RequestMethod.POST)
    public
    @ResponseBody
    String uploadFile(
            @ApiParam(value = "Destination type where the file will be uploaded to", required = true)
            @PathVariable("destinationType") String destinationType,
            @ApiParam(value = "Name of the file to be uploaded", required = true)
            @RequestParam("fileName") String fileName,
            @ApiParam(value = "dummy", required = false, hidden = true)
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        //String fileName= file.getName();


        //we aren't using jobId here yet. For some destination types it will be required
        //for example, if we wanted to put files into the extractor/output directory, we would need
        //to use the jobid. But we don't suppor that use case yet.

        //Enumeration<String> headers = request.getHeaders("Content-Disposition");

        if (!file.isEmpty()) {
            try {

                String cropType = CropRequestAnalyzer.getGobiiCropType(request);

                byte[] byteArray = file.getBytes();
                GobiiFileProcessDir gobiiFileProcessDir = GobiiFileProcessDir.valueOf(destinationType);

                this.fileService
                        .writeFileToProcessDir(
                                cropType,
                                fileName,
                                gobiiFileProcessDir,
                                byteArray);

            } catch (Exception e) {
                ControllerUtils.writeRawResponse(response,
                        HttpServletResponse.SC_NOT_ACCEPTABLE,
                        e.getMessage());
                LOGGER.error("Error uploading file", e);
            }

        } else {

            String message = "You failed to upload because the file was empty.";
            ControllerUtils.writeRawResponse(response,
                    HttpServletResponse.SC_NOT_ACCEPTABLE,
                    message);
            LOGGER.error("Error uploading file", message);

        }

        // this method has to return _something_ in order for a content-type to be set in the response (this makes
        // our client framework happy)
        return "";
    }

    /***
     * Delete an arbitary file to the specified destination
     * @param destinationType
     * @param fileName
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    @ApiOperation(
            value = "Detele a file by destination type",
            notes = "Deletes an arbitrary file from the specified destination",
            tags = {"Files"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Files : destinationType")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/files/{destinationType}",
            method = RequestMethod.DELETE
            , produces = MediaType.TEXT_PLAIN_VALUE
    )
    public
    @ResponseBody
    String deleteFile(
            @ApiParam(value = "Destination type where the file will be delete from", required = true)
            @PathVariable("destinationType") String destinationType,
            @ApiParam(value = "Name of the file to be deleted", required = true)
            @RequestParam("fileName") String fileName,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        //String fileName= file.getName();


        //we aren't using jobId here yet. For some destination types it will be required
        //for example, if we wanted to put files into the extractor/output directory, we would need
        //to use the jobid. But we don't suppor that use case yet.
        String cropType = CropRequestAnalyzer.getGobiiCropType(request);
        GobiiFileProcessDir gobiiFileProcessDir = GobiiFileProcessDir.valueOf(destinationType);

        try {
            this.fileService
                    .deleteFileFromProcessDir(
                            cropType,
                            fileName,
                            gobiiFileProcessDir);

        } catch (Exception e) {
            ControllerUtils.writeRawResponse(response,
                    HttpServletResponse.SC_NOT_ACCEPTABLE,
                    e.getMessage());
            LOGGER.error("Error deleting file", e);
        }


        // this method has to return _something_ in order for a content-type to be set in the response (this makes
        // our client framework happy)
        return "";
    }

    /***
     * Upload the specified file for a specific job to the specified directory
     * @param gobiiJobId
     * @param destinationType
     * @param fileName
     * @param file
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    @ApiOperation(
            value = "Upload a file for a given job and destination type",
            notes = "Uploads the specified file for a specific job to the specified directory",
            tags = {"Files"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Jobs : destinationType")
                    })}
    )
    @RequestMapping(value = "/files/{gobiiJobId}/{destinationType}",
            params = {"fileName"},
            method = RequestMethod.POST)
    //OpenAPI specification uses "string" as datatype for file, but the swagger automatically
    //adds "ref" as datatype for file parameter. So, an Implicit parameter is added and the original
    //parameter is made hidden.
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
            @ApiImplicitParam(name="file", value="The file to be uploaded",
                    required = true, dataType = "string",
                    paramType = "query")
    })
    public
    @ResponseBody
    String uploadJobFile(
            @ApiParam(value = "ID of the Job that the file will be associated to", required = true)
            @PathVariable("gobiiJobId") String gobiiJobId,
            @ApiParam(value = "Destination type where the file will be uploaded to", required = true)
            @PathVariable("destinationType") String destinationType,
            @ApiParam(value = "Name of the file", required = true)
            @RequestParam("fileName") String fileName,
            @ApiParam(value="dummy", hidden = true, required = false)
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        //String name = file.getName();

        String fileMimeType = null;

        //we aren't using jobId here yet. For some destination types it will be required
        //for example, if we wanted to put files into the extractor/output directory, we would need
        //to use the jobid. But we don't suppor that use case yet.

        //Enumeration<String> headers = request.getHeaders("Content-Disposition");

        if (!file.isEmpty()) {
            try {

                byte[] byteArray = file.getBytes();

                // GDM-266 Need to validate file type only for
                // text files. But, this endpoint allows different type of files from different sources.
                // So, adding below condition to avoid validating for other kind of instruction files.
                if(destinationType.equals("EXTRACTOR_INSTRUCTIONS") && fileName.endsWith(".txt")) {
                    fileMimeType = this.tika.detect(byteArray);
                }

                if(fileMimeType == null || fileMimeType.equals("text/plain") ) {

                    String cropType = CropRequestAnalyzer.getGobiiCropType(request);
                    GobiiFileProcessDir gobiiFileProcessDir = GobiiFileProcessDir.valueOf(destinationType);

                    this.fileService
                            .writeJobFileForCrop(cropType,
                                    gobiiJobId,
                                    fileName,
                                    gobiiFileProcessDir,
                                    byteArray);
                }
                else {

                    String message = "Invalid Input file. " + "File is identified as "+ fileMimeType + ".";
                    ControllerUtils.writeRawResponse(response,
                            HttpServletResponse.SC_NOT_ACCEPTABLE,
                            message);
                    LOGGER.error("Error uploading file", message);
                }

            } catch (Exception e) {
                ControllerUtils.writeRawResponse(response,
                        HttpServletResponse.SC_NOT_ACCEPTABLE,
                        e.getMessage());
                LOGGER.error("Error uploading file", e);
            }

        } else {

            String message = "You failed to upload because the file was empty.";
            ControllerUtils.writeRawResponse(response,
                    HttpServletResponse.SC_NOT_ACCEPTABLE,
                    message);
            LOGGER.error("Error uploading file", message);

        }

        // this method has to return _something_ in order for a content-type to be set in the response (this makes
        // our client framework happy)
        return "";
    }

    @ApiOperation(
            value = "Download a job file from a given destination type",
            notes = "Downloads the specified file for a specific job from the specified directory",
            tags = {"Files"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Jobs : destinationType")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/files/{gobiiJobId}/{destinationType}",
            method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadJobFile(
            @ApiParam(value = "ID of the Job", required = true)
            @PathVariable("gobiiJobId") String gobiiJobId,
            @ApiParam(value = "Destination type where the file will be downloaded from", required = true)
            @PathVariable("destinationType") String destinationType,
            @ApiParam(value = "Name of the file to be downloaded", required = true)
            @RequestParam("fileName") String fileName,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ResponseEntity<InputStreamResource> returnVal = null;
        try {

            String cropType = CropRequestAnalyzer.getGobiiCropType(request);
            GobiiFileProcessDir gobiiFileProcessDir = GobiiFileProcessDir.valueOf(destinationType.toUpperCase());
            File file = this.fileService.readCropFileForJob(cropType, gobiiJobId, fileName, gobiiFileProcessDir);

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            respHeaders.setContentLength(file.length());
            respHeaders.setContentDispositionFormData("attachment", fileName);

            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
            returnVal = new ResponseEntity<>(inputStreamResource, respHeaders, HttpStatus.OK);

        } catch (Exception e) {

            ControllerUtils.writeRawResponse(response,
                    HttpServletResponse.SC_NOT_ACCEPTABLE,
                    e.getMessage());
            LOGGER.error("Error downloading file", e);

        }

        return returnVal;

    }


    /*** JOB METHODS ***/
    @ApiOperation(
            value = "Create a new job",
            notes = "Creates a new job in the system.",
            tags = {"Jobs"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Jobs")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/jobs", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<JobDTO> createJob(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<JobDTO> payloadEnvelope,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<JobDTO> returnVal = new PayloadEnvelope<>();
        try {

            PayloadReader<JobDTO> payloadReader = new PayloadReader<>(JobDTO.class);
            JobDTO jobDTOToCreate = payloadReader.extractSingleItem(payloadEnvelope);

            JobDTO jobDTONew = jobService.createJob(jobDTOToCreate);


            PayloadWriter<JobDTO> payloadWriter = new PayloadWriter<>(request, response,
                    JobDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_JOB),
                    jobDTONew, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "List all jobs",
            notes = "Lists all the existing jobs in the system.",
            tags = {"Jobs"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Jobs")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<JobDTO> getStatus(HttpServletRequest request,
                                             HttpServletResponse response) {

        PayloadEnvelope<JobDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<JobDTO> jobDTOS = jobService.getJobs();

            PayloadWriter<JobDTO> payloadWriter = new PayloadWriter<>(request, response,
                    JobDTO.class);

            payloadWriter.writeList(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_JOB),
                    jobDTOS, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Update a job by jobName",
            notes = "Updates the Job entity having the specified jobName.",
            tags = {"Jobs"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Jobs : jobName")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/jobs/{jobName}", method = RequestMethod.PUT)
    @ResponseBody
    public PayloadEnvelope<JobDTO> replaceStatus(
            @RequestBody PayloadEnvelope<JobDTO> payloadEnvelope,
            @ApiParam(value = "Name of the Job to be updated", required = true)
            @PathVariable("jobName") String jobName,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<JobDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<JobDTO> payloadReader = new PayloadReader<>(JobDTO.class);
            JobDTO jobDTOToReplace = payloadReader.extractSingleItem(payloadEnvelope);

            JobDTO jobDTOReplaced = jobService.replaceJob(jobName, jobDTOToReplace);

            PayloadWriter<JobDTO> payloadWriter = new PayloadWriter<>(request, response,
                    JobDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_JOB),
                    jobDTOReplaced, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Get job details by job name",
            notes = "Gets the Job entity having the specified name.",
            tags = {"Jobs"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Jobs : jobName")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/jobs/{jobName}", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<JobDTO> getStatusById(
            @ApiParam(value = "Name of the Job to be extracted", required = true)
            @PathVariable("jobName") String jobName,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<JobDTO> returnVal = new PayloadEnvelope<>();
        try {

            JobDTO jobDTO = jobService.getJobByJobName(jobName);

            PayloadWriter<JobDTO> payloadWriter = new PayloadWriter<>(request, response,
                    JobDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_JOB),
                    jobDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);

    }

    @ApiOperation(
            value = "Create DNA samples for a given job",
            notes = "Creates DNA samples for a given Job having the specified name in the system.",
            tags = {"Jobs"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="DnaSamples : jobName")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/jobs/dnasamples/{jobName}", method = RequestMethod.POST)
    @ResponseBody
    public PayloadEnvelope<JobDTO> submitDnaSamplesByJobName(
            @ApiParam(required = true)
            @RequestBody PayloadEnvelope<DnaSampleDTO> payloadEnvelope,
            @ApiParam(value = "Name of the Job that the DNA samples will be added to", required = true)
            @PathVariable("jobName") String jobName,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<JobDTO> returnVal = new PayloadEnvelope<>();

        try {

            PayloadReader<DnaSampleDTO> payloadReader = new PayloadReader<>(DnaSampleDTO.class);
            List<DnaSampleDTO> dnaSampleDTOList = payloadReader.extractListOfItems(payloadEnvelope);

            JobDTO dnaSampleJobDTO = jobService.submitDnaSamplesByJobName(jobName, dnaSampleDTOList);

            PayloadWriter<JobDTO> payloadWriter = new PayloadWriter<>(request, response, JobDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    GobiiUriFactory.resourceByUriIdParam(request.getContextPath(),
                            RestResourceId.GOBII_JOB),
                    dnaSampleJobDTO, cropType());

        } catch (GobiiException e) {
            returnVal.getHeader().getStatus().addException(e);
        } catch (Exception e) {
            returnVal.getHeader().getStatus().addException(e);
        }


        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.CREATED,
                HttpStatus.INTERNAL_SERVER_ERROR);


        return (returnVal);

    }


    // *********************************************
    // *************************** ENTITY STATS METHODS
    // *********************************************
    @ApiOperation(
            value = "List all entities",
            notes = "Lists all the existing entities in the system.",
            tags = {"Entities"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Entities")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/entities", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<EntityStatsDTO> getAllEntityStats(HttpServletRequest request,
                                                             HttpServletResponse response) {

        PayloadEnvelope<EntityStatsDTO> returnVal = new PayloadEnvelope<>();

        try {

            List<EntityStatsDTO> allEntityStats = entityStatsService.getAll();

            PayloadWriter<EntityStatsDTO> payloadWriter = new PayloadWriter<>(request, response,
                    EntityStatsDTO.class);

            payloadWriter.writeList(returnVal,
                    null,
                    allEntityStats, cropType());

        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {

            returnVal.getHeader().getStatus().addException(e);

        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Get last modified date of an entity",
            notes = "Gets last modified Entity for the given entityName",
            tags = {"Entities"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Entities.lastModified")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/entities/{entityName}/lastmodified", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<EntityStatsDTO> getEntityLastModified(
            @ApiParam(value = "Name of the Entity", required = true)
            @PathVariable("entityName") String entityName,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<EntityStatsDTO> returnVal = new PayloadEnvelope<>();

        try {

            GobiiEntityNameType gobiiEntityNameType = GobiiEntityNameType.valueOf(entityName.toUpperCase());

            EntityStatsDTO entityStatsDTO = entityStatsService.getEntityLastModified(gobiiEntityNameType);

            PayloadWriter<EntityStatsDTO> payloadWriter = new PayloadWriter<>(request, response,
                    EntityStatsDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    null,
                    entityStatsDTO, cropType());

        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {

            returnVal.getHeader().getStatus().addException(e);

        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }

    @ApiOperation(
            value = "Get count for a given entity",
            notes = "Gets the total Entity count for the given entityName",
            tags = {"Entities"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Entities.count")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/entities/{entityName}/count", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<EntityStatsDTO> getEntityCount(
            @ApiParam(value = "Name of the Entity", required = true)
            @PathVariable("entityName") String entityName,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<EntityStatsDTO> returnVal = new PayloadEnvelope<>();

        try {

            GobiiEntityNameType gobiiEntityNameType = GobiiEntityNameType.valueOf(entityName.toUpperCase());

            EntityStatsDTO entityStatsDTO = entityStatsService.getEntityCount(gobiiEntityNameType);

            PayloadWriter<EntityStatsDTO> payloadWriter = new PayloadWriter<>(request, response,
                    EntityStatsDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    null,
                    entityStatsDTO, cropType());

        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {

            returnVal.getHeader().getStatus().addException(e);

        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }


    @ApiOperation(
            value = "Get entity count for a given entity child",
            notes = "Retrieves the total count of the children for the given entity",
            tags = {"Entities"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name="summary", value="Child.count")
                    })}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-Auth-Token", value="Authentication Token", required=true,
                    paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "/entities/{entityNameParent}/{parentId}/{entityNameChild}/count", method = RequestMethod.GET)
    @ResponseBody
    public PayloadEnvelope<EntityStatsDTO> getEntityCountOfChildren(
            @ApiParam(value = "Name of the parent entity", required = true)
            @PathVariable("entityNameParent") String entityNameParent,
            @ApiParam(value = "ID of the parent entity", required = true)
            @PathVariable("parentId") Integer parentId,
            @ApiParam(value = "Name of the child entity", required = true)
            @PathVariable("entityNameChild") String entityNameChild,
            HttpServletRequest request,
            HttpServletResponse response) {

        PayloadEnvelope<EntityStatsDTO> returnVal = new PayloadEnvelope<>();

        try {

            GobiiEntityNameType parentEntityNameType = GobiiEntityNameType.valueOf(entityNameParent.toUpperCase());
            GobiiEntityNameType childEntityNameType = GobiiEntityNameType.valueOf(entityNameChild.toUpperCase());

            EntityStatsDTO entityStatsDTO = entityStatsService.getEntityCountOfChildren(parentEntityNameType,
                    parentId,
                    childEntityNameType);

            PayloadWriter<EntityStatsDTO> payloadWriter = new PayloadWriter<>(request, response,
                    EntityStatsDTO.class);

            payloadWriter.writeSingleItemForDefaultId(returnVal,
                    null,
                    entityStatsDTO, cropType());

        } catch (GobiiException e) {

            returnVal.getHeader().getStatus().addException(e);

        } catch (Exception e) {

            returnVal.getHeader().getStatus().addException(e);

        }

        ControllerUtils.setHeaderResponse(returnVal.getHeader(),
                response,
                HttpStatus.OK,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return (returnVal);
    }
}// GOBIIController
