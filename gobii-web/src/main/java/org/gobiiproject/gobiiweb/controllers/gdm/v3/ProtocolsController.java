/**
 * ProtocolsController.java
 * Gobii API endpoint controllers for protocols.
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.ProtocolService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.ProtocolDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
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
public class ProtocolsController {

    private ProtocolService protocolService;
    
    /**
     * Get Protocol
     * @return
     */
    @GetMapping("/protocols")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<ProtocolDTO>> getProtocols(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize,
        @RequestParam(required=false) Integer platformId
    ) throws Exception {
        
        pageSize = ControllerUtils.getPageSize(pageSize);
        PagedResult<ProtocolDTO> pagedResult = protocolService.getProtocols(
            pageSize, page, platformId);
        BrApiMasterListPayload<ProtocolDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);
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
        ControllerUtils.checkBindingErrors(bindingResult);
        ProtocolDTO protocolDTO = protocolService.createProtocol(request);
        BrApiMasterPayload<ProtocolDTO> payload =  ControllerUtils.getMasterPayload(protocolDTO);
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
        BrApiMasterPayload<ProtocolDTO> payload = ControllerUtils.getMasterPayload(protocolDTO);
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
        ControllerUtils.checkBindingErrors(bindingResult);
        ProtocolDTO protocolDTO = protocolService.patchProtocol(protocolId, request);
        BrApiMasterPayload<ProtocolDTO> payload = ControllerUtils.getMasterPayload(protocolDTO);
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
}
