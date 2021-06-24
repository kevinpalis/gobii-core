/**
 * CvsController.java
 * Gobii API endpoint controllers for Cv 
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.CvService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvGroupDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
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
public class CvsController {

    private CvService cvService;

    @CropAuth(CURATOR)
    @PostMapping("/cvs")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<CvDTO>> createCv(
        @RequestBody @Validated(CvDTO.Create.class) final CvDTO requestCvDTO,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        CvDTO createdCvDTO = cvService.createCv(requestCvDTO);
        BrApiMasterPayload<CvDTO> payload = ControllerUtils.getMasterPayload(createdCvDTO);
        return ResponseEntity.created(null).body(payload);
    }

    @CropAuth(CURATOR)
    @PatchMapping("/cvs/{cvId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<CvDTO>> updateCv(
        @PathVariable Integer cvId,
        @RequestBody @Validated(CvDTO.Update.class) final CvDTO requestCvDTO,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        CvDTO updatedCvDTO = cvService.updateCv(cvId, requestCvDTO);
        BrApiMasterPayload<CvDTO> payload = ControllerUtils.getMasterPayload(updatedCvDTO);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/cvs")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<CvDTO>> getCvs(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize,
        @RequestParam(required=false) String cvGroupName,
        @RequestParam(required=false) String cvGroupType
    ) throws Exception {
        PagedResult<CvDTO> pagedResult = cvService.getCvs(page, pageSize, cvGroupName, cvGroupType);
        BrApiMasterListPayload<CvDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/cvs/{cvId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<CvDTO>> getCv(
        @PathVariable Integer cvId
    ) throws Exception {
        CvDTO cvDTO = cvService.getCv(cvId);
        BrApiMasterPayload<CvDTO> payload = ControllerUtils.getMasterPayload(cvDTO);
        return ResponseEntity.ok(payload);

    }

    @CropAuth(CURATOR)
    @DeleteMapping("/cvs/{cvId}")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteCv(
        @PathVariable Integer cvId
    ) throws Exception {
        cvService.deleteCv(cvId);
        return ResponseEntity.noContent().build();
    } 

    //-- cv group`
    @GetMapping("/cv/groups")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<CvGroupDTO>> getCvGroups(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize,
        @RequestParam(required=false) String cvGroupType
    ) throws Exception {
        PagedResult<CvGroupDTO> results = cvService.getCvGroups(page, pageSize, cvGroupType);
        BrApiMasterListPayload<CvGroupDTO> payload = ControllerUtils.getMasterListPayload(results);
        return ResponseEntity.ok(payload);
    }


    
}
