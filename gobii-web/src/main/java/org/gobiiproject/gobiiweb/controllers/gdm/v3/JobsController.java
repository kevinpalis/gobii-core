package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import io.swagger.annotations.Api;

import java.io.File;
import java.util.zip.ZipOutputStream;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.JobService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.GobiiFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Scope(value = "request")
@RestController
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@Api()
@CrossOrigin
public class JobsController {

    private final JobService jobService;

    /**
     * Constructor
     * @param jobService    {@link JobService}
     */
    @Autowired
    public JobsController(final JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Get Job by Id. to check job status
     * @param jobId
     * @return
     * @throws Exception
     */
    @GetMapping("/jobs/{jobId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<JobDTO>> getJObById(
        @PathVariable Integer jobId,
        @PathVariable String cropType
    ) throws GobiiException {
        JobDTO jobDTO = jobService.getJobById(jobId, cropType);
        BrApiMasterPayload<JobDTO> result = ControllerUtils.getMasterPayload(jobDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/jobs")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<JobDTO>> getJobs(
        @RequestParam(required=false, defaultValue="0") Integer page,
        @RequestParam(required=false, defaultValue="1000") Integer pageSize,
        @RequestParam(required=false, defaultValue="") String username,
        @RequestParam(required=false, defaultValue="false") boolean getLoadJobs,
        @RequestParam(required=false, defaultValue="false") boolean getExtractJobs,
        @PathVariable String cropType
    ) throws GobiiException {
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<JobDTO> pagedResult = jobService.getJobs(
            page, pageSizeToUse, 
            username, getLoadJobs, 
            getExtractJobs, cropType
        );

        BrApiMasterListPayload<JobDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);
    }



    @GetMapping(
        value="/jobs/{jobId}/files/{jobFilesDirectoryName:[\\w]+}.zip", 
        produces="application/zip")
    public ResponseEntity<StreamingResponseBody> zipFiles(
        @PathVariable Integer jobId,
        @PathVariable String jobFilesDirectoryName,
        @PathVariable String cropType) throws Exception {

        File instructionFileDirectory = jobService.getJobStatusDirectory(cropType, jobId);
        
        if (instructionFileDirectory == null || 
            !jobFilesDirectoryName.equals(instructionFileDirectory.getName())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
            .header(
                "Content-Disposition",
                String.format("attachment; filename=\"%s\"", 
                    instructionFileDirectory.getName() + ".zip")
            )
            .body(out -> {
                ZipOutputStream zipOut = new ZipOutputStream(out);
                GobiiFileUtils.streamZipFile(instructionFileDirectory, 
                    instructionFileDirectory.getName(), 
                    zipOut);
                zipOut.close();
            });

    }

}
