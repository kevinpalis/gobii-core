package org.gobiiproject.gobiiweb.controllers;

import io.swagger.annotations.Api;
import org.gobiiproject.gobiidomain.services.gdmv3.CropService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.CropsDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Scope(value = "request")
@Controller
@RequestMapping("/")
@CrossOrigin
@Api
public class CropsController {

    @Autowired
    private CropService cropService;

    @RequestMapping(value = "crops", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<CropsDTO>> getCrops() {
        PagedResult<CropsDTO> cropsPagedResult = cropService.getCrops();
        return ResponseEntity.ok(BrApiMasterListPayload.createPayload(cropsPagedResult));
    }

}
