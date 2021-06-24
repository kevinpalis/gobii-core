/**
 * ReferencesController.java
 * Gobii API endpoint controllers for references
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.ReferenceService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.ReferenceDTO;
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
public class ReferencesController {

    private ReferenceService referenceService;
    
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
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<ReferenceDTO> pagedResult = referenceService.getReferences(page, pageSizeToUse);
        BrApiMasterListPayload<ReferenceDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);
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
        ControllerUtils.checkBindingErrors(bindingResult);
        String createdBy = AuthUtils.getCurrentUser();
        ReferenceDTO referenceDTO = referenceService.createReference(request, createdBy);
        BrApiMasterPayload<ReferenceDTO> payload =  ControllerUtils.getMasterPayload(referenceDTO);
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
        BrApiMasterPayload<ReferenceDTO> payload = ControllerUtils.getMasterPayload(referenceDTO);
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
        ControllerUtils.checkBindingErrors(bindingResult);
        String updatedBy = AuthUtils.getCurrentUser();
        ReferenceDTO referenceDTO = referenceService.updateReference(referenceId, request, updatedBy);
        BrApiMasterPayload<ReferenceDTO> payload = ControllerUtils.getMasterPayload(referenceDTO);
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
}

