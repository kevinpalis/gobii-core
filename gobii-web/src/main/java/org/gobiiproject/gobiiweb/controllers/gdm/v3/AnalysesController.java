/**
 * AnalysesController.java
 * Gobii API endpoint controllers for analysis
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.AnalysisService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
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

@Scope(value = "request")
@RestController
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@Api()
@CrossOrigin
@AllArgsConstructor
public class AnalysesController {

    private AnalysisService analysisService;

    /**
     * List Analyses
     * 
     * @return
     */
    @GetMapping("/analyses")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<AnalysisDTO>> getAnalyses(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<AnalysisDTO> pagedResult = analysisService.getAnalyses(page, pageSizeToUse);

        BrApiMasterListPayload<AnalysisDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);
    }

    /**
     * Create Analyses
     * 
     * @return
     */
    @CropAuth(CURATOR)
    @PostMapping("/analyses")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<AnalysisDTO>> createAnalysis(
        @RequestBody @Validated(AnalysisDTO.Create.class) final AnalysisDTO analysisRequest,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String user = AuthUtils.getCurrentUser();
        AnalysisDTO result = analysisService.createAnalysis(analysisRequest, user);

        BrApiMasterPayload<AnalysisDTO> payload = ControllerUtils.getMasterPayload(result);
        return ResponseEntity.created(null).body(payload);
    }

    /**
     * Update Analysis  By Id
     */
    @CropAuth(CURATOR)
    @PatchMapping("/analyses/{analysisId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<AnalysisDTO>> patchAnalysis(
        @PathVariable Integer analysisId,
        @RequestBody final AnalysisDTO analysisRequest,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String user = AuthUtils.getCurrentUser();

        AnalysisDTO result = analysisService.updateAnalysis(analysisId, analysisRequest, user);

        BrApiMasterPayload<AnalysisDTO> payload = ControllerUtils.getMasterPayload(result);
        return ResponseEntity.ok(payload);
    }

    /**
     * Get Analysis By Id
     */
    @GetMapping("/analyses/{analysisId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<AnalysisDTO>> getAnalysis(
        @PathVariable Integer analysisId
    ) throws Exception {
        AnalysisDTO analysisDTO = analysisService.getAnalysis(analysisId);
        BrApiMasterPayload<AnalysisDTO> payload = ControllerUtils.getMasterPayload(analysisDTO);
        return ResponseEntity.ok(payload);
    }

    /**
     * Get Analysis By Id
     */
    @CropAuth(CURATOR)
    @DeleteMapping("/analyses/{analysisId}")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteAnalysis(
        @PathVariable Integer analysisId
    ) throws Exception {
        analysisService.deleteAnalysis(analysisId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Create Analysis Type
     * @return
     */
    @CropAuth(CURATOR)
    @PostMapping("/analysis/types")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<CvTypeDTO>> createAnalysisType(
        @RequestBody @Validated(CvTypeDTO.Create.class) final CvTypeDTO analysisTypeRequest,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String user = AuthUtils.getCurrentUser();
        CvTypeDTO result = analysisService.createAnalysisType(analysisTypeRequest, user);
        BrApiMasterPayload<CvTypeDTO> payload = ControllerUtils.getMasterPayload(result);
        return ResponseEntity.created(null).body(payload);
    }

    /**
     * List Analysis Types
     * @return
     */
    @GetMapping("/analysis/types")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<CvTypeDTO>> getAnalysisTypes(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<CvTypeDTO> result = analysisService.getAnalysisTypes(page, pageSizeToUse);
        BrApiMasterListPayload<CvTypeDTO> payload = ControllerUtils.getMasterListPayload(result);
        return ResponseEntity.ok(payload);
    }

}
