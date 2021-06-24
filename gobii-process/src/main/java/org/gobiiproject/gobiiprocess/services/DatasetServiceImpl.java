package org.gobiiproject.gobiiprocess.services;

import javax.transaction.Transactional;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;
import org.gobiiproject.gobiisampletrackingdao.DatasetDao;

@Transactional
public class DatasetServiceImpl implements DatasetService{
    
    public Dataset update(Integer dataSetId,
                          String dataTableName,
                          String hdf5FileName) throws Exception {
        
        DatasetDao datasetDao = 
            SpringContextLoaderSingleton.getInstance().getBean(DatasetDao.class);
        Dataset dataset = datasetDao.getDataset(dataSetId);
        dataset.setDataTable(dataTableName);
        dataset.setDataFile(hdf5FileName);
        return datasetDao.updateDataset(dataset);
    }
}
