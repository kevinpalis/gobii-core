package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.auditable.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedList;

/**
 * Created by Phil on 4/21/2016.
 */
public interface DataSetService {

    DataSetDTO createDataSet(DataSetDTO dataSetDTO) throws GobiiDomainException;
    DataSetDTO replaceDataSet(Integer dataSetId, DataSetDTO dataSetDTO) throws GobiiDomainException;
    List<DataSetDTO> getDataSets() throws GobiiDomainException;
    PagedList<DataSetDTO> getDatasetsPaged(Integer pageSize, Integer pageNo, String pgQueryId) throws GobiiDtoMappingException;
    List<DataSetDTO> getDataSetsByTypeId(Integer typeId) throws GobiiDomainException;
    DataSetDTO getDataSetById(Integer dataSetId) throws GobiiDomainException;
    JobDTO getJobDetailsByDatasetId(Integer datasetId) throws GobiiDomainException;
    List<AnalysisDTO> getAnalysesByDatasetId(Integer datasetId) throws GobiiDomainException;

}
