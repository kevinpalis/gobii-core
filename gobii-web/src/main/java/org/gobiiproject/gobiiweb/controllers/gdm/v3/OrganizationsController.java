/**
 * OrganizationsController.java
 * Gobii API endpoint controllers for organizations
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.OrganizationService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.OrganizationDTO;
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
import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;

@Scope(value = "request")
@RestController
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@Api()
@CrossOrigin
//@Slf4j
@AllArgsConstructor
public class OrganizationsController {

    private OrganizationService organizationService;
    
    /**
     * Get Organizationsi
     */
    @GetMapping("/organizations")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<OrganizationDTO>> getOrganizations(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {
        Integer pageSizetoUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<OrganizationDTO> result = organizationService.getOrganizations(page, pageSizetoUse);
        BrApiMasterListPayload<OrganizationDTO> payload = ControllerUtils.getMasterListPayload(result);
        return ResponseEntity.ok(payload);
    }

    /**
     * Get Organization by Id
     */
    @GetMapping("/organizations/{organizationId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<OrganizationDTO>> getOrganizationById(
        @PathVariable Integer organizationId
    ) throws Exception {
        OrganizationDTO organizationDTO = organizationService.getOrganization(organizationId);
        BrApiMasterPayload<OrganizationDTO> payload = ControllerUtils.getMasterPayload(organizationDTO);
        return ResponseEntity.ok(payload);
    }

    /**
     * Create Organization
     * @return
     */
    @CropAuth(CURATOR)
    @PostMapping("/organizations")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<OrganizationDTO>> createOrganization(
        @RequestBody @Validated(OrganizationDTO.Create.class) final OrganizationDTO request,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String user  = AuthUtils.getCurrentUser();

        OrganizationDTO createdOrganization = organizationService.createOrganization(request, user);
        BrApiMasterPayload<OrganizationDTO> payload = ControllerUtils.getMasterPayload(createdOrganization);
        return ResponseEntity.created(null).body(payload);
    }

    /**
     * Patch organization
     */
    @CropAuth(CURATOR)
    @PatchMapping("/organizations/{organizationId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<OrganizationDTO>> updateOrganization(
        @PathVariable Integer organizationId,
        @RequestBody @Validated final OrganizationDTO request,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String user = AuthUtils.getCurrentUser();
        OrganizationDTO updatedOrganization = organizationService.updateOrganization(organizationId, request, user);
        BrApiMasterPayload<OrganizationDTO> payload = ControllerUtils.getMasterPayload(updatedOrganization);
        return ResponseEntity.ok(payload);
    }

    /**
     * Delete organization
     */
    @CropAuth(CURATOR)
    @DeleteMapping("/organizations/{organizationId}")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteOrganiztion(
        @PathVariable Integer organizationId
    ) throws Exception {
        organizationService.deleteOrganization(organizationId);
        return ResponseEntity.noContent().build();
    }

}
