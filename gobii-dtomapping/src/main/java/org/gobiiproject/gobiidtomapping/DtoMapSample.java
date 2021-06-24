package org.gobiiproject.gobiidtomapping;

import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.DnaSampleDTO;

/**
 * Created by Phil on 3/29/2016.
 */
public interface DtoMapSample {

    DnaSampleDTO getSampleDetailsByExternalCode(String externalCode) throws GobiiDtoMappingException;

    List<DataSetDTO> getDatasetForLoadedSamplesOfDataType(String externalCode, String datasetType) throws GobiiDtoMappingException;

    JobDTO submitDnaSamplesByJobName(String jobName, List<DnaSampleDTO> dnaSampleDTOList) throws GobiiDtoMappingException;
}
