package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerUploadRequestDTO;

public interface MarkerService {

    JobDTO uploadMarkerFile(byte[] markerFile,
                            MarkerUploadRequestDTO markerUploadRequest,
                            String cropType) throws GobiiException;
}