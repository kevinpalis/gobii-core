package org.gobiiproject.gobidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetRequestDTO;

public interface DatasetService {

	DatasetDTO createDataset(DatasetRequestDTO request, String user) throws Exception;

}