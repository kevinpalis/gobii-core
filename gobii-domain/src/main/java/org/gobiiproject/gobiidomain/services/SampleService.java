package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.DnaSampleDTO;

/**
 * Created by Phil on 4/21/2016.
 */
public interface SampleService {

    DnaSampleDTO getSampleDetailsByExternalCode(String externalCode) throws GobiiDomainException;

    List<DataSetDTO> getDatasetForLoadedSamplesOfDataType(String externalCode, String datasetType) throws GobiiDomainException;

}
