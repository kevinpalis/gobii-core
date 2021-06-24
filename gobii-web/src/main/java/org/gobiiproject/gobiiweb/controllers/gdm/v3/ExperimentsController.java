/**
 * ExperimentsController.java
 * Gobii API endpoint controllers for experiments
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import static org.gobiiproject.gobiimodel.config.Roles.*;

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
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.ExperimentService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.ExperimentDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.security.CropAuth;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;

@Scope(value = "request")
@RestController
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@Api()
@CrossOrigin
@AllArgsConstructor
public class ExperimentsController {
    
    private ExperimentService experimentService;

     /**
     * Lists Experiments
     * @return
     */
    @GetMapping("/experiments")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<ExperimentDTO>> getExperiments(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize,
        @RequestParam(required=false) Integer projectId
    ) throws Exception {
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<ExperimentDTO> pagedResult = experimentService.getExperiments(
            Math.max(0, page),
            pageSizeToUse,
            projectId
        );
        BrApiMasterListPayload<ExperimentDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);
    }

    /**
     * Get Experiment endpoint handler
     * 
     * @param experimentId
     * @return
     * @throws Exception
     */
    @GetMapping("/experiments/{experimentId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ExperimentDTO>> getExperiment(
        @PathVariable Integer experimentId
    ) throws Exception {
        ExperimentDTO experiment = experimentService.getExperiment(experimentId);
        BrApiMasterPayload<ExperimentDTO> result = ControllerUtils.getMasterPayload(experiment);
        return ResponseEntity.ok(result);
    }

    /**
     * createExperiment
     * 
     * Create new project
     * @since 2020-03-13
     */
    @CropAuth(CURATOR)
    @PostMapping("/experiments")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ExperimentDTO>> createProject(
            @RequestBody @Validated(ExperimentDTO.Create.class) final ExperimentDTO experiment,
            BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        //Get the current user
        String userName = AuthUtils.getCurrentUser();

        ExperimentDTO createdDTO = experimentService.createExperiment(experiment, userName);
        BrApiMasterPayload<ExperimentDTO> result = ControllerUtils.getMasterPayload(createdDTO);
        return ResponseEntity.created(null).body(result);
    }


    /**
     * Update Experiment endpoint handler
     * 
     * @param experimentId
     * @return
     * @throws Exception
     */
    @CropAuth(CURATOR)
    @PatchMapping("/experiments/{experimentId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ExperimentDTO>> updateExperiment(
        @PathVariable Integer experimentId,
        @RequestBody @Validated(ExperimentDTO.Update.class) final ExperimentDTO request,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String user = AuthUtils.getCurrentUser();

        ExperimentDTO experiment = experimentService.updateExperiment(experimentId, request, user);
        BrApiMasterPayload<ExperimentDTO> result = ControllerUtils.getMasterPayload(experiment);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete Experiment endpoint handler
     * 
     * @param experimentId
     * @return
     * @throws Exception
     */
    @CropAuth(CURATOR)
    @DeleteMapping("/experiments/{experimentId}")
    @ResponseBody
    public ResponseEntity<String> deleteExperiment(
        @PathVariable Integer experimentId
    ) throws Exception {
        experimentService.deleteExperiment(experimentId);
        return ResponseEntity.noContent().build();
    }

}
