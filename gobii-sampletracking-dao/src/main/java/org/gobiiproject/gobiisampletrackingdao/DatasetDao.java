package org.gobiiproject.gobiisampletrackingdao;


import java.util.List;

import org.gobiiproject.gobiimodel.entity.Dataset;

public interface DatasetDao {

    List<Dataset> getDatasets(Integer pageSize, Integer rowOffset,
                              Integer datasetId, String datasetName,
                              Integer datasetTypeId,
                              Integer experimentId, String experimentName);

    Dataset getDatasetById(Integer datasetId);
    Dataset getDataset(Integer datasetId); //this one uses the entitygraph

    List<Object[]> getDatasetsWithAnalysesAndCounts(Integer pageSize, Integer rowOffset,
                                                    Integer datasetId, String datasetName,
                                                    Integer experimentId, String experimentName);

    int getDatasetCountByAnalysisId(Integer id); //this is for callinganalyses
    
    int getDatasetCountWithAnalysesContaining(Integer id);

    Dataset saveDataset(Dataset dataset) throws Exception;
    Dataset updateDataset(Dataset dataset) throws Exception;
}
