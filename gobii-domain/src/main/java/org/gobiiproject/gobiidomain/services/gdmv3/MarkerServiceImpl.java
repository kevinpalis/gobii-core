package org.gobiiproject.gobiidomain.services.gdmv3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.*;
import org.gobiiproject.gobiimodel.entity.*;
import org.gobiiproject.gobiimodel.types.*;
import org.gobiiproject.gobiimodel.utils.IntegerUtils;
import org.gobiiproject.gobiisampletrackingdao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SuppressWarnings("unchecked")
@Transactional
public class MarkerServiceImpl implements MarkerService {

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private PlatformDao platformDao;

    @Autowired
    private MapsetDao mapsetDao;

    @Autowired
    private JobService jobService;

    final ObjectMapper mapper = new ObjectMapper();

    final String loadType = "MARKER";


    /**
     * Uploads markers in the file to the database.
     * Also, loads marker positions and linkage groups when provided in the same file.
     *
     * @param inputFileStream      marker file input stream
     * @param markerUploadRequest   Request object with meta data and template
     * @param cropType              Crop type to which the markers need to uploaded
     * @return  {@link JobDTO}
     * @throws GobiiException   Gobii Exception for bad request or if any run time system error
     */
    @Override
    public JobDTO loadMarkerData(MarkerUploadRequestDTO markerUploadRequest,
                                 String cropType) throws GobiiException {

        LoaderInstruction3 loaderInstruction = new LoaderInstruction3();
        loaderInstruction.setLoadType(loadType);

        loaderInstruction.setCropType(cropType);

        // Verify platform Id
        if(!IntegerUtils.isNullOrZero(markerUploadRequest.getPlatformId())) {
            Platform platform = platformDao.getPlatform(markerUploadRequest.getPlatformId());
            if (platform == null) {
                throw new InvalidException("platform");
            }
        }

        // Verify Mapset
        if(!IntegerUtils.isNullOrZero(markerUploadRequest.getMapId())) {
            Mapset mapset = mapsetDao.getMapset(markerUploadRequest.getMapId());
            if(mapset == null) {
                throw new InvalidException("mapset");
            }
        }
        
        // Check if input files are found
        if(markerUploadRequest.getInputFiles().size() == 0) {
            throw new InvalidException("request: no input files");
        }

        // Check whether input file paths are valid
        Utils.checkIfInputFilesAreValid(markerUploadRequest.getInputFiles());

        // Get user submitting the load
        String userName = ContactService.getCurrentUserName();
        Contact createdBy = contactDao.getContactByUsername(userName);

        // Set contact email in loader instruction
        loaderInstruction.setContactEmail(createdBy.getEmail());

        // Create loader job after validating user input.
        JobDTO jobDTO = new JobDTO();
        jobDTO.setPayload(GobiiLoaderPayloadTypes.MARKERS.getTerm());
        jobDTO.setJobMessage("Submitted marker load job.");
        JobDTO job = jobService.createLoaderJob(jobDTO);

        String jobName = job.getJobName();
        
        //Set output dir
        String outputFilesDir = Utils.getOutputDir(jobName, cropType);
        loaderInstruction.setOutputDir(outputFilesDir);

        loaderInstruction.setUserRequest(markerUploadRequest);

        // Write instruction file
        Utils.writeInstructionFile(loaderInstruction, jobName, cropType);

        return jobDTO;
    }

}
