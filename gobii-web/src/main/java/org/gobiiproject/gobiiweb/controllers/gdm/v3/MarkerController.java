package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.MarkerService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerUploadRequestDTO;
import org.gobiiproject.gobiiweb.security.CropAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

@Scope(value = "request")
@Controller
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@CrossOrigin
@Api
@Slf4j
public class MarkerController {


    private final MarkerService markerService;

    /**
     * Constructor
     *
     * @param markerService {@link MarkerService}
     */
    @Autowired
    public MarkerController(final MarkerService markerService) {
        this.markerService = markerService;
    }


    @CropAuth(CURATOR)
    @PostMapping(value = "/marker/load",
        consumes = "application/json",
        produces = "application/json")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<JobDTO>> uploadMarkers(
        @PathVariable final String cropType,
        @RequestBody final MarkerUploadRequestDTO markerUploadRequest
    ) throws Exception {
        JobDTO job = markerService.loadMarkerData(
            markerUploadRequest,
            cropType);
        BrApiMasterPayload<JobDTO> payload = ControllerUtils.getMasterPayload(job);
        return ResponseEntity.accepted().body(payload);
    }

}
