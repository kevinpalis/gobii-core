package org.gobiiproject.gobiiprocess.services;

import org.gobiiproject.gobiimodel.entity.Dataset;

public interface DatasetService {
    Dataset update(Integer datasetId, String dataTableName, String hdf5FileName) throws Exception;
}
