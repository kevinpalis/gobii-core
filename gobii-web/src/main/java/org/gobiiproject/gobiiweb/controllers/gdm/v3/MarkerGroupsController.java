/**
 * MarkerGroupsController.java
 * Gobii API endpoint controllers for marker groups
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

import java.util.List;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.MarkerGroupService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerGroupDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.security.CropAuth;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;

@Scope(value = "request")
@RestController
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@Api()
@CrossOrigin
//@Slf4j
@AllArgsConstructor
public class MarkerGroupsController {

    private MarkerGroupService markerGroupService;
    
    @CropAuth(CURATOR)
    @PostMapping("/markergroups")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<MarkerGroupDTO>> createMarkerGroup(
        @RequestBody @Validated(MarkerGroupDTO.Create.class) final MarkerGroupDTO request,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String creator = AuthUtils.getCurrentUser();
        MarkerGroupDTO markerGroupDTO = markerGroupService.createMarkerGroup(request, creator);
        BrApiMasterPayload<MarkerGroupDTO> payload = ControllerUtils.getMasterPayload(markerGroupDTO);
        return ResponseEntity.created(null).body(payload);
    }

    @GetMapping("/markergroups")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<MarkerGroupDTO>> getMarkerGroups(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<MarkerGroupDTO> results = markerGroupService.getMarkerGroups(page, pageSizeToUse);
        BrApiMasterListPayload<MarkerGroupDTO> payload = ControllerUtils.getMasterListPayload(results);
        return ResponseEntity.ok(payload);

    }

    @GetMapping("/markergroups/{markerGroupId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<MarkerGroupDTO>> getMarkerGroupById(
        @PathVariable Integer markerGroupId
    ) throws Exception {
        MarkerGroupDTO markerGroupDTO = markerGroupService.getMarkerGroup(markerGroupId);
        BrApiMasterPayload<MarkerGroupDTO> payload = ControllerUtils.getMasterPayload(markerGroupDTO);
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
        ControllerUtils.checkBindingErrors(bindingResult);
        String updatedBy = AuthUtils.getCurrentUser();
        MarkerGroupDTO markerGroupDTO = markerGroupService.updateMarkerGroup(markerGroupId, request, updatedBy);
        BrApiMasterPayload<MarkerGroupDTO> payload = ControllerUtils.getMasterPayload(markerGroupDTO);
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
        ControllerUtils.checkBindingErrors(bindingResult);
        String editedBy = AuthUtils.getCurrentUser();
        PagedResult<MarkerDTO> results = markerGroupService.mapMarkers(markerGroupId, markers, editedBy);
        BrApiMasterListPayload<MarkerDTO> payload = ControllerUtils.getMasterListPayload(results);
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
        BrApiMasterListPayload<MarkerDTO> payload = ControllerUtils.getMasterListPayload(results);
        return ResponseEntity.ok(payload);
    }
}