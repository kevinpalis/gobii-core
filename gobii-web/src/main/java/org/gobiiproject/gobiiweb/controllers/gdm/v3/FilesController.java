package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.FileService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.FileDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.FileManifestDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiiweb.security.CropAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

@Scope(value = "request")
@RestController
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@Api()
@CrossOrigin
@Slf4j
public class FilesController {

    private final FileService fileService;

    /**
     * Constructor
     * @param fileService    {@link FileService}
     */
    @Autowired
    public FilesController(final FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Initiate file upload request.
     * 
     * Creates a directory to which file would be uploaded to.
     * 
     * @return {@link FileDTO}
     * @throws Exception
     */
    @CropAuth(CURATOR)
    @PostMapping("/file/upload")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<FileManifestDTO>>
    initiateFileUploadRequest(@PathVariable final String cropType) throws GobiiException {
        FileManifestDTO fileDTO = fileService.initiateFileUpload(cropType);
        BrApiMasterPayload<FileManifestDTO> result = ControllerUtils.getMasterPayload(fileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @CropAuth(CURATOR)
    @PostMapping(value = "/files", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<FileDTO>>
    updateFileChunk(@PathVariable final String cropType,
                    @RequestParam final String fileUploadId,
                    @RequestPart("file") MultipartFile file) throws GobiiException {


        InputStream fileChunkInputStream;
        try {
            fileChunkInputStream = file.getInputStream();
        }
        catch (IOException e) {
            log.error(e.getMessage());
            throw new GobiiException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Error in rading file chunk");
        }

        FileDTO updatedFile = fileService.updateFileChunk(
            file.getOriginalFilename(),
            file.getContentType(),
            cropType,
            fileChunkInputStream,
            fileUploadId);

        BrApiMasterPayload<FileDTO> result = ControllerUtils.getMasterPayload(updatedFile);

        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/files/{filePath}")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<FileDTO>>
    listFiles(@PathVariable final String cropType,
              @PathVariable final String filePath,
              @RequestParam(defaultValue = "1000") final Integer pageSize,
              @RequestParam(defaultValue = "0") final Integer page) throws GobiiException {
        PagedResult<FileDTO> files = 
            fileService.listFilesByFilePath(filePath, cropType, pageSize, page);
        BrApiMasterListPayload<FileDTO> payload = ControllerUtils.getMasterListPayload(files);
        return ResponseEntity.ok(payload);
    }
}
