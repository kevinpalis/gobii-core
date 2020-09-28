/**
 * ProjectController.java
 * Gobii API endpoint controllers for projects
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-03-06
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.gobiiproject.gobiimodel.config.Roles.*;

import org.gobiiproject.gobiidomain.services.gdmv3.*;
import org.gobiiproject.gobiiapimodel.payload.HeaderAuth;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.*;
import org.gobiiproject.gobiimodel.dto.system.AuthDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.automation.PayloadWriter;
import org.gobiiproject.gobiiweb.exceptions.ValidationException;
import org.gobiiproject.gobiiweb.security.CropAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Scope(value = "request")
@RestController
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@Api()
@CrossOrigin
@Slf4j
public class GOBIIControllerV3  {
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private VendorProtocolService vendorProtocolService;

    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private MapsetService mapsetService;

    @Autowired
    private MarkerGroupService markerGroupService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private CvService cvService;

    @Autowired
    private PlatformService platformService;

    /**
     * Authentication Endpoint
     * Mimicking same logic used in v1
     * @param request - Request from the client
     * @param response - Response with Headers values filled in TokenFilter
     * @return
     */
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<HeaderAuth> authenticate(HttpServletRequest request,
                                       HttpServletResponse response) {

        try {

            HeaderAuth dtoHeaderAuth = new HeaderAuth();

            PayloadWriter<AuthDTO> payloadWriter = new PayloadWriter<>(
                    request, response, AuthDTO.class);

            payloadWriter.setAuthHeader(dtoHeaderAuth, response);

            return ResponseEntity.ok(dtoHeaderAuth);

        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "Entity does not exist"
            );
        }


    }


    /**
     * List Vendor Protocols
     * @return
     */
    @GetMapping("/vendorprotocols")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<VendorProtocolDTO>> getVendorProtocols(
            @RequestParam(required=false, defaultValue = "0") Integer page,
            @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {

        Integer pageSizeToUse = getPageSize(pageSize);

        PagedResult<VendorProtocolDTO> pagedResult =
                vendorProtocolService.getVendorProtocols(
                        Math.max(0, page),
                        pageSizeToUse
        );
        BrApiMasterListPayload<VendorProtocolDTO> payload = this.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);
    }


    //-----Dataset API Handlers section

 

    //-------- Mapsets ------------
 
    //-------Organizations----------

    //--- Cv 

    // --- Platforms

    // -- References
    /**
     * Get References
     * @return
     */
    @GetMapping("/references")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<ReferenceDTO>> getReferences(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {
        Integer pageSizeToUse = this.getPageSize(pageSize);
        PagedResult<ReferenceDTO> pagedResult = referenceService.getReferences(page, pageSizeToUse);
        BrApiMasterListPayload<ReferenceDTO> payload = this.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);  
    }

    /**
     * Create reference
     * @return
     */
    @CropAuth(CURATOR)
    @PostMapping("/references")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ReferenceDTO>> createReference(
        @RequestBody @Validated(ReferenceDTO.Create.class) final ReferenceDTO request,
        BindingResult bindingResult
    ) throws Exception {
        this.checkBindingErrors(bindingResult);
        String createdBy = this.getCurrentUser();
        ReferenceDTO referenceDTO = referenceService.createReference(request, createdBy);
        BrApiMasterPayload<ReferenceDTO> payload =  this.getMasterPayload(referenceDTO);
        return ResponseEntity.created(null).body(payload);
    }

    /**
     * Get Reference by Id
     * @return
     */
    @GetMapping("/references/{referenceId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ReferenceDTO>> getReference(
        @PathVariable Integer referenceId
    ) throws Exception {
        ReferenceDTO referenceDTO = referenceService.getReference(referenceId);
        BrApiMasterPayload<ReferenceDTO> payload = this.getMasterPayload(referenceDTO);
        return ResponseEntity.ok(payload);
    }

    /**
     * Update
     */
    @CropAuth(CURATOR)
    @PatchMapping("/references/{referenceId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ReferenceDTO>> updateReference(
        @PathVariable Integer referenceId,
        @RequestBody @Validated(ReferenceDTO.Update.class) final ReferenceDTO request,
        BindingResult bindingResult
    ) throws Exception {
        this.checkBindingErrors(bindingResult);
        String updatedBy = this.getCurrentUser();
        ReferenceDTO referenceDTO = referenceService.updateReference(referenceId, request, updatedBy);
        BrApiMasterPayload<ReferenceDTO> payload = this.getMasterPayload(referenceDTO);
        return ResponseEntity.ok(payload);
    }

    /**
     * Delete
     * @return
     */
    @CropAuth(CURATOR)
    @DeleteMapping("/references/{referenceId}")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteReference(
        @PathVariable Integer referenceId
    ) throws Exception {
        referenceService.deleteReference(referenceId);
        return ResponseEntity.noContent().build();
    }

    // -- Protocols

    /**
     * Get Protocol
     * @return
     */
    @GetMapping("/protocols")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<ProtocolDTO>> getProtocols(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize,
        @RequestParam(required = false) Integer platformId
    ) throws Exception {
        pageSize = this.getPageSize(pageSize);
        PagedResult<ProtocolDTO> pagedResult = protocolService.getProtocols(
            pageSize, page, platformId);
        BrApiMasterListPayload<ProtocolDTO> payload = this.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);
    }

    /**
     * Create protocol
     * @return
     */
    @PostMapping("/protocols")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ProtocolDTO>> createProtocol(
        @RequestBody @Validated(ProtocolDTO.Create.class) final ProtocolDTO request,
        BindingResult bindingResult
    ) throws Exception {
        this.checkBindingErrors(bindingResult);
        ProtocolDTO protocolDTO = protocolService.createProtocol(request);
        BrApiMasterPayload<ProtocolDTO> payload =  this.getMasterPayload(protocolDTO);
        return ResponseEntity.created(null).body(payload);
    }

    /**
     * Get Protocol by Id
     * @return
     */
    @GetMapping("/protocols/{protocolId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ProtocolDTO>> getProtocol(
        @PathVariable Integer protocolId
    ) throws Exception {
        ProtocolDTO protocolDTO = protocolService.getProtocolById(protocolId);
        BrApiMasterPayload<ProtocolDTO> payload = this.getMasterPayload(protocolDTO);
        return ResponseEntity.ok(payload);
    }

    /**
     * Update Protocol by Id
     */
    @PatchMapping("/protocols/{protocolId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ProtocolDTO>> updateProtocol(
        @PathVariable Integer protocolId,
        @RequestBody @Validated(ProtocolDTO.Update.class) final ProtocolDTO request,
        BindingResult bindingResult
    ) throws Exception {
        this.checkBindingErrors(bindingResult);
        ProtocolDTO protocolDTO = protocolService.patchProtocol(protocolId, request);
        BrApiMasterPayload<ProtocolDTO> payload = this.getMasterPayload(protocolDTO);
        return ResponseEntity.ok(payload);
    }

    /**
     * Delete Protocol by Id
     * @return
     */
    @DeleteMapping("/protocols/{protocolId}")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteProtocol(
        @PathVariable Integer protocolId
    ) throws Exception {
        protocolService.deleteProtocol(protocolId);
        return ResponseEntity.noContent().build();
    }

    //---- Marker Group
    @CropAuth(CURATOR)
    @PostMapping("/markergroups")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<MarkerGroupDTO>> createMarkerGroup(
        @RequestBody @Validated(MarkerGroupDTO.Create.class) final MarkerGroupDTO request,
        BindingResult bindingResult
    ) throws Exception {
        this.checkBindingErrors(bindingResult);
        String creator = this.getCurrentUser();
        MarkerGroupDTO markerGroupDTO = markerGroupService.createMarkerGroup(request, creator);
        BrApiMasterPayload<MarkerGroupDTO> payload = this.getMasterPayload(markerGroupDTO);
        return ResponseEntity.created(null).body(payload);
    }

    @GetMapping("/markergroups")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<MarkerGroupDTO>> getMarkerGroups(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {
        Integer pageSizeToUse = this.getPageSize(pageSize);
        PagedResult<MarkerGroupDTO> results = markerGroupService.getMarkerGroups(page, pageSizeToUse);
        BrApiMasterListPayload<MarkerGroupDTO> payload = this.getMasterListPayload(results);
        return ResponseEntity.ok(payload);

    }

    @GetMapping("/markergroups/{markerGroupId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<MarkerGroupDTO>> getMarkerGroupById(
        @PathVariable Integer markerGroupId
    ) throws Exception {
        MarkerGroupDTO markerGroupDTO = markerGroupService.getMarkerGroup(markerGroupId);
        BrApiMasterPayload<MarkerGroupDTO> payload = this.getMasterPayload(markerGroupDTO);
        return ResponseEntity.ok(payload);
    }

    @CropAuth(CURATOR)
    @PatchMapping("/markergroups/{markerGroupId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<MarkerGroupDTO>> updateMarkerGroup(
        @PathVariable Integer markerGroupId,
        @RequestBody final MarkerGroupDTO request,
        BindingResult bindingResult
    ) throws Exception {
        this.checkBindingErrors(bindingResult);
        String updatedBy = this.getCurrentUser();
        MarkerGroupDTO markerGroupDTO = markerGroupService.updateMarkerGroup(markerGroupId, request, updatedBy);
        BrApiMasterPayload<MarkerGroupDTO> payload = this.getMasterPayload(markerGroupDTO);
        return ResponseEntity.ok(payload);
    }

    @CropAuth(CURATOR)
    @DeleteMapping("/markergroups/{markerGroupId}")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteMarkerGroup(
        @PathVariable Integer markerGroupId
    ) throws Exception {
        markerGroupService.deleteMarkerGroup(markerGroupId);
        return ResponseEntity.noContent().build();
    }

    @CropAuth(CURATOR)
    @PostMapping("/markergroups/{markerGroupId}/markerscollection")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<MarkerDTO>> mapMarkers(
        @PathVariable Integer markerGroupId,
        @RequestBody final List<MarkerDTO> markers,
        BindingResult bindingResult
    ) throws Exception {
        this.checkBindingErrors(bindingResult);
        String editedBy = this.getCurrentUser();
        PagedResult<MarkerDTO> results = markerGroupService.mapMarkers(markerGroupId, markers, editedBy);
        BrApiMasterListPayload<MarkerDTO> payload = this.getMasterListPayload(results);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/markergroups/{markerGroupId}/markers")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<MarkerDTO>> getMarkerGroupMarkers(
        @PathVariable Integer markerGroupId,
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {
        PagedResult<MarkerDTO> results = markerGroupService.getMarkerGroupMarkers(markerGroupId, page, pageSize);
        BrApiMasterListPayload<MarkerDTO> payload = this.getMasterListPayload(results);
        return ResponseEntity.ok(payload);
    }

    
    // --test
    @GetMapping("/test")
    @CropAuth("pi")
    @ResponseBody
    public ResponseEntity<String> testMe() {
        return ResponseEntity.ok("test");
    }

    @GetMapping("/test2")
    @PreAuthorize("hasPermission('test2', 'read')")
    @ResponseBody
    public ResponseEntity<String> testMe2() {
        return ResponseEntity.ok("test2");
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    private String getCurrentUser() {
        return projectService.getDefaultProjectEditor();
    }

    private Integer getPageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) return 1000;
        return pageSize;
    }

    //This needs to be public
    public String getCropType() throws Exception {
        return CropRequestAnalyzer.getGobiiCropType();
    }

    private void checkBindingErrors(BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            
            List<String> info = new ArrayList<String>();
        
            bindingResult.getFieldErrors().forEach(
                objErr -> {
                    info.add(objErr.getField() + " " + objErr.getDefaultMessage());
                }
            );
            throw new ValidationException("Bad Request. "
                + String.join(", ", info.toArray(new String[info.size()])));
        } 
    }

    private <T> BrApiMasterPayload<T> getMasterPayload(T dtoObject) {
        BrApiMasterPayload<T> masterPayload = new BrApiMasterPayload<>();
        masterPayload.setMetadata(null);
        masterPayload.setResult(dtoObject);
        return masterPayload;
    }

    private <T> BrApiMasterListPayload<T> getMasterListPayload(PagedResult<T> objectList) {
        return new BrApiMasterListPayload<T>(
            objectList.getResult(),
            objectList.getCurrentPageSize(),
            objectList.getCurrentPageNum()
        );
    }
}