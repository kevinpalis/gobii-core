package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.GenotypeUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;


public interface GenotypeService {

    JobDTO loadGenotypes(GenotypeUploadRequestDTO genotypesUploadRequest,
                         String cropType) throws GobiiException;
}
