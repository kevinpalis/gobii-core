package org.gobiiproject.gobidomain.services;

import org.gobiiproject.gobidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.entity.auditable.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.entity.auditable.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.entity.noaudit.JobDTO;

import java.util.List;

/**
 * Created by Phil on 4/21/2016.
 */
public interface DataSetService {

    DataSetDTO createDataSet(DataSetDTO dataSetDTO) throws GobiiDomainException;
    DataSetDTO replaceDataSet(Integer dataSetId, DataSetDTO dataSetDTO) throws GobiiDomainException;
    List<DataSetDTO> getDataSets() throws GobiiDomainException;
    List<DataSetDTO> getDataSetsByTypeId(Integer typeId) throws GobiiDomainException;
    DataSetDTO getDataSetById(Integer dataSetId) throws GobiiDomainException;
    JobDTO getJobDetailsByDatasetId(Integer datasetId) throws GobiiDomainException;
    List<AnalysisDTO> getAnalysesByDatasetId(Integer datasetId) throws GobiiDomainException;

}
