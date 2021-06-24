package org.gobiiproject.gobiiprocess.digester;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.instructions.loader.DigesterResult;
import org.gobiiproject.gobiiprocess.JobStatus;

public interface Digest {
    DigesterResult digest() throws GobiiException;
    JobStatus getJobStatus();
}
