package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetRequestDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface DatasetService {

	DatasetDTO createDataset(DatasetRequestDTO request, String user) throws Exception;

	PagedResult<DatasetDTO> getDatasets(Integer page, Integer pageSize, Integer experimentId, Integer datasetTypeId) throws Exception;

	DatasetDTO getDataset(Integer target) throws Exception;

	DatasetDTO updateDataset(Integer datasetId, DatasetRequestDTO request, String user) throws Exception;

	void deleteDataset(Integer datasetId) throws Exception;

	PagedResult<CvTypeDTO> getDatasetTypes(Integer page, Integer pageSize) throws Exception;

	CvTypeDTO createDatasetType(String datasetTypeName, String datasetTypeDescription, String user);

}