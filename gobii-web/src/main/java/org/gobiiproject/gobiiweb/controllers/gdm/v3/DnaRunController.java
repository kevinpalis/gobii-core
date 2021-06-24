package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.DnaRunService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.DnaRunUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;
import org.gobiiproject.gobiiweb.security.CropAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

@Scope(value = "request")
@Controller
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@CrossOrigin
@Api
@Slf4j
public class DnaRunController {


    private final DnaRunService dnaRunService;

    /**
     * Constructor
     *
     * @param dnaRunService {@link DnaRunService}
     */
    @Autowired
    public DnaRunController(final DnaRunService dnaRunService) {
        this.dnaRunService = dnaRunService;
    }


    /**
     * Submits a upload job for dna runs
     * @param cropType              crop to load dna runs into
     * @param dnaRunUploadRequest   {@link DnaRunUploadRequest}
     * @return  {@link JobDTO} when successfully submitted the job. 
     * @throws Exception
     */
    @CropAuth(CURATOR)
    @PostMapping(value = "/dnarun/load", produces = "application/json")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<JobDTO>> uploadDnaRuns(
        @PathVariable final String cropType,
        @RequestBody final DnaRunUploadRequestDTO dnaRunUploadRequest
    ) throws Exception {

        JobDTO job = dnaRunService.loadDnaRuns(
            dnaRunUploadRequest,
            cropType);
        BrApiMasterPayload<JobDTO> payload = ControllerUtils.getMasterPayload(job);
        return ResponseEntity.accepted().body(payload);
    }
}
