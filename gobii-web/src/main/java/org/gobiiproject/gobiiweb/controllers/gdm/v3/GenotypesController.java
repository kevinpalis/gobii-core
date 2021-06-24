package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.GenotypeService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.GenotypeUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;
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
public class GenotypesController {


    private final GenotypeService genotypeService;

    /**
     * Constructor
     *
     * @param genotypeService {@link GenotypeService}
     */
    @Autowired
    public GenotypesController(final GenotypeService genotypeService) {
        this.genotypeService = genotypeService;
    }


    @CropAuth(CURATOR)
    @PostMapping(value = "/genotype/load",
        consumes = "application/json",
        produces = "application/json")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<JobDTO>> uploadGenotypes(
        @PathVariable final String cropType,
        @RequestBody final GenotypeUploadRequestDTO genotypesUploadRequest
        ) throws Exception {

        JobDTO job = genotypeService.loadGenotypes(
            genotypesUploadRequest,
            cropType);

        BrApiMasterPayload<JobDTO> payload = ControllerUtils.getMasterPayload(job);

        return ResponseEntity.accepted().body(payload);

    }





}
