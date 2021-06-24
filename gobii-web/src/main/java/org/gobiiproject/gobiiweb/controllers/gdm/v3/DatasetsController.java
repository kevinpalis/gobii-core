/**
 * DatasetsController.java
 * Gobii API endpoint controllers for Datasets
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

import javax.validation.Valid;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.DatasetService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetRequestDTO;
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
public class DatasetsController {

    private DatasetService datasetService;

    /**
     * Create Dataset
     * @return
     */
    @CropAuth(CURATOR)
    @PostMapping("/datasets")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<DatasetDTO>> createDataset(
        @RequestBody @Validated(DatasetRequestDTO.Create.class) final DatasetRequestDTO request,
        BindingResult bindingResult
    ) throws Exception {
       ControllerUtils.checkBindingErrors(bindingResult);
       String user = AuthUtils.getCurrentUser();
       DatasetDTO result = datasetService.createDataset(request, user);
       BrApiMasterPayload<DatasetDTO> payload = ControllerUtils.getMasterPayload(result);
       return ResponseEntity.created(null).body(payload);    
    }

    /**
     * Datasets listing
     * @return
     */
    @GetMapping("/datasets")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<DatasetDTO>> getDatasets(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize,
        @RequestParam(required=false) Integer experimentId,
        @RequestParam(required=false) Integer datasetTypeId
    ) throws Exception {
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<DatasetDTO> pagedResult = datasetService.getDatasets(
            page, pageSizeToUse, experimentId, datasetTypeId
        );

        BrApiMasterListPayload<DatasetDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);
    }

    /**
     * Get datasets by Id
     * 
     */
    @GetMapping("/datasets/{datasetId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<DatasetDTO>> getDataset(
        @PathVariable Integer datasetId
    ) throws Exception {
        DatasetDTO datasetDTO = datasetService.getDataset(datasetId);
        BrApiMasterPayload<DatasetDTO> payload = ControllerUtils.getMasterPayload(datasetDTO);
        return ResponseEntity.ok(payload);
    }

    /**
     * Update dataset by Id
     * @return
     */
    @CropAuth(CURATOR)
    @PatchMapping("/datasets/{datasetId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<DatasetDTO>> updateDataset(
        @PathVariable Integer datasetId,
        @RequestBody @Valid final DatasetRequestDTO request,
        BindingResult bindingResult
    ) throws Exception {
        String user = AuthUtils.getCurrentUser();
        DatasetDTO datasetDTO = datasetService.updateDataset(datasetId, request, user);
        BrApiMasterPayload<DatasetDTO> payload = ControllerUtils.getMasterPayload(datasetDTO);
        return ResponseEntity.ok(payload);
    }

    /**
     * Delete dataset
     * @return
     */
    @CropAuth(CURATOR)
    @DeleteMapping("/datasets/{datasetId}")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteDataset(
        @PathVariable Integer datasetId
    ) throws Exception {
        datasetService.deleteDataset(datasetId);
        return ResponseEntity.noContent().build();
    }

    //------ DatasetTypes
    /**
     * List dataset types
     * @return
     */
    @GetMapping("/dataset/types")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<CvTypeDTO>> getDatasetTypes(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);

        PagedResult<CvTypeDTO> pagedResult = datasetService.getDatasetTypes(page, pageSizeToUse);
        
        BrApiMasterListPayload<CvTypeDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);
    }

    /**
     * Create Analysis Type
     * @return
     */
    @CropAuth(CURATOR)
    @PostMapping("/dataset/types")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<CvTypeDTO>> createDatasetType(
        @RequestBody @Validated(CvTypeDTO.Create.class) final CvTypeDTO datasetTypeRequest,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String user = AuthUtils.getCurrentUser();
        CvTypeDTO result = datasetService.createDatasetType(
            datasetTypeRequest.getTypeName(),
            datasetTypeRequest.getTypeDescription(),
            user
        );
        BrApiMasterPayload<CvTypeDTO> payload = ControllerUtils.getMasterPayload(result);
        return ResponseEntity.created(null).body(payload);
    }
}
