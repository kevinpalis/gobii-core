/**
 * MapsetsController.java
 * Gobii API endpoint controllers for Mapsets
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.MapsetService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MapsetDTO;
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
public class MapsetsController {

    private MapsetService mapsetService;

    /**
     * Get Mapsets list
     * @return
     */
    @GetMapping("/mapsets")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<MapsetDTO>> getMapsets(
        @RequestParam(required=false, defaultValue="0") Integer page,
        @RequestParam(required=false, defaultValue="1000") Integer pageSize,
        @RequestParam(required=false) Integer mapsetTypeId
    ) throws Exception {
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<MapsetDTO> pagedResult = mapsetService.getMapsets(page, pageSizeToUse, mapsetTypeId);

        BrApiMasterListPayload<MapsetDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);
    }

    /**
     * Create mapset entry
     * @return
     */
    @CropAuth(CURATOR)
    @PostMapping("/mapsets")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<MapsetDTO>> createMapset(
        @RequestBody @Validated(MapsetDTO.Create.class) final MapsetDTO mapset,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String user = AuthUtils.getCurrentUser();
        MapsetDTO mapsetDTO = mapsetService.createMapset(mapset, user);
        BrApiMasterPayload<MapsetDTO> payload = ControllerUtils.getMasterPayload(mapsetDTO);
        return ResponseEntity.created(null).body(payload);
    }

    /**
     * Get mapset entry by id
     * @return
     */
    @GetMapping("/mapsets/{mapsetId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<MapsetDTO>> getMapset(
        @PathVariable Integer mapsetId
    ) throws Exception {
        MapsetDTO result = mapsetService.getMapset(mapsetId);
        BrApiMasterPayload<MapsetDTO> payload = ControllerUtils.getMasterPayload(result);
        return ResponseEntity.ok(payload);
    }

    /**
     * Update mapset
     * @return
     */
    @CropAuth(CURATOR)
    @PatchMapping("/mapsets/{mapsetId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<MapsetDTO>> updateMapset(
        @PathVariable Integer mapsetId,
        @RequestBody @Validated(MapsetDTO.Update.class) final MapsetDTO patchData,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String editedBy = AuthUtils.getCurrentUser();

        MapsetDTO result = mapsetService.updateMapset(mapsetId, patchData, editedBy);
        BrApiMasterPayload<MapsetDTO> payload = ControllerUtils.getMasterPayload(result);
        return ResponseEntity.ok(payload);
    }

    /**
     * Delete mapset
     */
    @CropAuth(CURATOR)
    @DeleteMapping("/mapsets/{mapsetId}")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteMapset(
        @PathVariable Integer mapsetId
    ) throws Exception {
        mapsetService.deleteMapset(mapsetId);
        return ResponseEntity.noContent().build();
    }

    //----Mapset Type
    /**
     * Create Mapset Type
     * @param mapsetTypeRequest
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @CropAuth(CURATOR)
    @PostMapping("/mapset/types")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<CvTypeDTO>> createMapsetType(
        @RequestBody @Validated(CvTypeDTO.Create.class) final CvTypeDTO mapsetTypeRequest,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String user = AuthUtils.getCurrentUser();
        CvTypeDTO result = mapsetService.createMapsetType(
            mapsetTypeRequest.getTypeName(),
            mapsetTypeRequest.getTypeDescription(),
            user
        );

        BrApiMasterPayload<CvTypeDTO> payload = ControllerUtils.getMasterPayload(result);
        return ResponseEntity.created(null).body(payload);
    }

    /**
     * Get Mapset Types
     * @return
     */
    @GetMapping("/mapset/types")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<CvTypeDTO>> getMapsetTypes(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) throws Exception {
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<CvTypeDTO> result = mapsetService.getMapsetTypes(page, pageSizeToUse);
        BrApiMasterListPayload<CvTypeDTO> payload = ControllerUtils.getMasterListPayload(result);
        return ResponseEntity.ok(payload);
    }

}
