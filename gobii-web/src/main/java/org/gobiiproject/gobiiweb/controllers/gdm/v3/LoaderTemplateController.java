package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.services.gdmv3.LoaderTemplateService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.LoaderTemplateDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.templates.DnaRunTemplateDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.templates.MarkerTemplateDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.LoaderTemplate;
import org.gobiiproject.gobiiweb.security.CropAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/gobii/v3/loader-templates/")
@CrossOrigin
@Api
@Slf4j
public class LoaderTemplateController {


    private final LoaderTemplateService loaderTemplateService;

    /**
     * Constructor.
     *
     * @param loaderTemplateService {@link LoaderTemplateService}
     */
    @Autowired
    public LoaderTemplateController(final LoaderTemplateService loaderTemplateService) {
        this.loaderTemplateService = loaderTemplateService;
    }

    /**
     * List marker loader templates in the system.
     * @return list of {@link LoaderTemplate}
     */
    @GetMapping(value = "/marker", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BrApiMasterListPayload<LoaderTemplateDTO>>
    getMarkerTemplates(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize
    ) {
        PagedResult<LoaderTemplateDTO> loaderTemplateList =
            loaderTemplateService.getMarkerTemplates(pageSize, page);
        BrApiMasterListPayload<LoaderTemplateDTO> payload =
            ControllerUtils.getMasterListPayload(loaderTemplateList);
        return ResponseEntity.ok(payload);
    }

    /**
     * @return empty loader template {@link MarkerTemplateDTO} for marker upload.
     */
    @GetMapping(value = "/empty-marker", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MarkerTemplateDTO> getEmptyMarkerTemplate() {
        MarkerTemplateDTO markerTemplateDTO = loaderTemplateService.getEmptyMarkerTemplate();
        return ResponseEntity.ok(markerTemplateDTO);
    }

    /**
     * List dna run loader templates in the system
     * @return list of {@link org.gobiiproject.gobiimodel.entity.LoaderTemplate}
     *
     * @throws Exception
     */
    @GetMapping(value = "/dnarun", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BrApiMasterListPayload<LoaderTemplateDTO>>
    getDnaRunTemplates(
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize) {

        PagedResult<LoaderTemplateDTO> loaderTemplateList =
            loaderTemplateService.getDnaRunTemplates(pageSize, page);
        BrApiMasterListPayload<LoaderTemplateDTO> payload =
            ControllerUtils.getMasterListPayload(loaderTemplateList);
        return ResponseEntity.ok(payload);
    }

    /**
     * @return empty loader template {@link DnaRunTemplateDTO} for dnarun upload.
     */
    @GetMapping(value = "/empty-dnarun", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DnaRunTemplateDTO> getEmptyDnaRunTemplate() {
        DnaRunTemplateDTO dnaRunTemplateDTO = loaderTemplateService.getEmptyDnaRunTemplate();
        return ResponseEntity.ok(dnaRunTemplateDTO);
    }

    /**
     * Adds the marker template to the GDM system
     * CropAuth annotation allows only CURATOR to access the endpoint
     * @param loaderTemplateDTO    marker file upload template to be saved
     * @return {@link LoaderTemplateDTO} created loader template
     * @throws Exception
     */
    @CropAuth(CURATOR)
    @PostMapping(value = "/marker", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<LoaderTemplateDTO>> addMarkerTemplate(
        @RequestBody final LoaderTemplateDTO loaderTemplateDTO) throws Exception {
        LoaderTemplateDTO loaderTemplate = loaderTemplateService.addMarkerTemplate(
            loaderTemplateDTO);
        BrApiMasterPayload<LoaderTemplateDTO> payload = ControllerUtils.getMasterPayload(
            loaderTemplate);
        return ResponseEntity.created(null).body(payload);
    }

    /**
     * Adds the dnarun template to the GDM system
     * CropAuth annotation allows only CURATOR to access the endpoint
     * @param loaderTemplateDTO    dnarun file upload template to be saved
     * @return {@link LoaderTemplateDTO} created loader template
     * @throws Exception
     */
    @CropAuth(CURATOR)
    @PostMapping(value = "/dnarun", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<LoaderTemplateDTO>> addDnaRunTemplate(
        @RequestBody final LoaderTemplateDTO loaderTemplateDTO
    ) throws Exception {
        LoaderTemplateDTO loaderTemplate = loaderTemplateService.addDnaRunTemplate(
            loaderTemplateDTO);
        BrApiMasterPayload<LoaderTemplateDTO> payload = ControllerUtils.getMasterPayload(
            loaderTemplate);
        return ResponseEntity.created(null).body(payload);
    }

}
