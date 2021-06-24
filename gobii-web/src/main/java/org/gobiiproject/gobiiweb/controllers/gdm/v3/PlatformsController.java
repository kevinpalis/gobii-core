/**
 * PlatformsController.java
 * Gobii API endpoint controllers for platforms
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.PlatformService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.PlatformDTO;
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
public class PlatformsController {
    
    private PlatformService platformService;

    @CropAuth(CURATOR)
    @PostMapping("/platforms")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<PlatformDTO>> addPlatform(
        @RequestBody @Validated(PlatformDTO.Create.class) final PlatformDTO request,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String user = AuthUtils.getCurrentUser();

        PlatformDTO platformDTO = platformService.createPlatform(request, user);
        BrApiMasterPayload<PlatformDTO> payload = ControllerUtils.getMasterPayload(platformDTO);
        return ResponseEntity.created(null).body(payload);
    }

    @GetMapping("/platforms")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<PlatformDTO>> getPlatforms(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize,
        @RequestParam(required=false) Integer platformTypeId
    ) throws Exception {
        PagedResult<PlatformDTO> results = platformService.getPlatforms(page, pageSize, platformTypeId);
        BrApiMasterListPayload<PlatformDTO> payload = ControllerUtils.getMasterListPayload(results);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/platforms/{platformId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<PlatformDTO>> getPlatform(
        @PathVariable Integer platformId
    ) throws Exception {
        PlatformDTO platformDTO = platformService.getPlatform(platformId);
        BrApiMasterPayload<PlatformDTO> payload = ControllerUtils.getMasterPayload(platformDTO);
        return ResponseEntity.ok(payload);
    }

    @CropAuth(CURATOR)
    @PatchMapping("/platforms/{platformId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<PlatformDTO>> updatePlatform(
        @PathVariable Integer platformId,
        @RequestBody @Validated(PlatformDTO.Update.class) final PlatformDTO request,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String updatedBy = AuthUtils.getCurrentUser();

        PlatformDTO platformDTO = platformService.updatePlatform(platformId, request, updatedBy);
        BrApiMasterPayload<PlatformDTO> payload = ControllerUtils.getMasterPayload(platformDTO);
        return ResponseEntity.ok(payload);
    }

    @CropAuth(CURATOR)
    @DeleteMapping("/platforms/{platformId}")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity deletePlatform(
        @PathVariable Integer platformId
    ) throws Exception {
        platformService.deletePlatform(platformId);
        return ResponseEntity.noContent().build();
    }

    @CropAuth(CURATOR)
    @PostMapping("/platform/types")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<CvTypeDTO>> createPlatformType(
        @RequestBody @Validated(CvTypeDTO.Create.class) final CvTypeDTO request 
    ) throws Exception {
        CvTypeDTO cvTypeDTO = platformService.createPlatformType(request);
        BrApiMasterPayload<CvTypeDTO> payload = ControllerUtils.getMasterPayload(cvTypeDTO);
        return ResponseEntity.created(null).body(payload);
    }

    @GetMapping("/platform/types")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<CvTypeDTO>> getPlatformTypes(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {
        PagedResult<CvTypeDTO> results = platformService.getPlatformTypes(page, pageSize);
        BrApiMasterListPayload<CvTypeDTO> payload = ControllerUtils.getMasterListPayload(results);
        return ResponseEntity.ok(payload);
    }

}
