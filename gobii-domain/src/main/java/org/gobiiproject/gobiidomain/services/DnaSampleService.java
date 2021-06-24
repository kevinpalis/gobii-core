package org.gobiiproject.gobiidomain.services;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.ProjectSamplesDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.SampleMetadataDTO;

public interface DnaSampleService {


    ProjectSamplesDTO createSamples(ProjectSamplesDTO sampleListDTO) throws GobiiDomainException;

    JobDTO uploadSamples(
            byte[] inputFileBytes,
            SampleMetadataDTO sampleMetadataDTO,
            String cropType) throws GobiiDomainException;

}
