package org.gobiiproject.gobiidtomapping.entity.auditable;

import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedList;

/**
 * Created by Phil on 4/21/2016.
 */
//When GP1-1539 is implemented, this interface should again extend
// DatasetDTO because then DatasetDTO will derive from auditable rather than base
// See notes on DatasetDTO for more info
//public interface DtoMapDataSet extends DtoMap<DataSetDTO>{
 public interface DtoMapDataSet {


    DataSetDTO create(DataSetDTO dataSetDTO) throws GobiiDtoMappingException;
    DataSetDTO replace(Integer projectId, DataSetDTO dataSetDTO) throws GobiiDtoMappingException;
    DataSetDTO get(Integer projectId) throws GobiiDtoMappingException;
    List<DataSetDTO> getList() throws GobiiDtoMappingException;
    PagedList<DataSetDTO> getListPaged(Integer pageSize, Integer pageNo, String pgQueryId) throws GobiiDtoMappingException;

    List<DataSetDTO> getByTypeId(Integer typeId) throws GobiiDtoMappingException;

    void updateDatasetForJobInfo(JobDTO jobDTO, DataSetDTO dataSetDTO) throws GobiiDtoMappingException;

}
