package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.dto.gdmv3.DnaRunUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.JobDTO;

public interface DnaRunService {
    JobDTO loadDnaRuns(DnaRunUploadRequestDTO dnaRunUploadRequest,
                       String cropType);
}
