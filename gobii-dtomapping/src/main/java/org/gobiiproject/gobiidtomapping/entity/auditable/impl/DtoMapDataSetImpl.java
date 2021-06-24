package org.gobiiproject.gobiidtomapping.entity.auditable.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gobiiproject.gobiidao.resultset.access.RsDataSetDao;
import org.gobiiproject.gobiidao.resultset.core.ParamExtractor;
import org.gobiiproject.gobiidao.resultset.core.ResultColumnApplicator;
import org.gobiiproject.gobiidao.resultset.core.listquery.DtoListQueryColl;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapDataSet;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.cvnames.JobType;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Phil on 4/21/2016.
 */
public class DtoMapDataSetImpl implements DtoMapDataSet {

    Logger LOGGER = LoggerFactory.getLogger(DtoMapDataSetImpl.class);


    @Autowired
    private RsDataSetDao rsDataSetDao;

    @Autowired
    private DtoListQueryColl dtoListQueryColl;

    @SuppressWarnings("unchecked")
    @Override
    public List<DataSetDTO> getList() throws GobiiDtoMappingException {

        List<DataSetDTO> returnVal = new ArrayList<>();

        try {

            returnVal = (List<DataSetDTO>) dtoListQueryColl.getList(ListSqlId.QUERY_ID_DATASET_ALL);

        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PagedList<DataSetDTO> getListPaged(Integer pageSize, Integer pageNo, String pgQueryId) throws GobiiDtoMappingException {

        PagedList<DataSetDTO> returnVal;
        try {

            returnVal = (PagedList<DataSetDTO>) dtoListQueryColl.getListPaged(ListSqlId.QUERY_ID_DATASET_ALL,
                    pageSize,
                    pageNo,
                    pgQueryId);

        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    @Override
    public List<DataSetDTO> getByTypeId(Integer typeId) throws GobiiDtoMappingException {

        List<DataSetDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsDataSetDao.getDataSetsByTypeId(typeId);

            while(resultSet.next()) {

                DataSetDTO currentDataSetDTO = new DataSetDTO();

                currentDataSetDTO.setDatasetName(resultSet.getString("name"));
                currentDataSetDTO.setDataSetId(resultSet.getInt("dataset_id"));
                currentDataSetDTO.setDatatypeId(resultSet.getInt("type_id"));

                returnVal.add(currentDataSetDTO);

            }


        } catch (SQLException e) {

            throw new GobiiDtoMappingException(e);

        }

        return returnVal;


    }

    @Transactional
    @Override
    public DataSetDTO get(Integer dataSetId) throws GobiiDtoMappingException {

        DataSetDTO returnVal = new DataSetDTO();


        try {
            ResultSet resultSet = rsDataSetDao.getDataSetDetailsByDataSetId(dataSetId);

            if (resultSet.next()) {

                ResultColumnApplicator.applyColumnValues(resultSet, returnVal);
            }
        } catch(SQLException e) {
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;

    }

    @Override
    public DataSetDTO create(DataSetDTO dataSetDTO) throws GobiiDtoMappingException {

        DataSetDTO returnVal = dataSetDTO;


        dataSetDTO.setModifiedBy(null);
        dataSetDTO.setModifiedDate(null);
        Map<String, Object> parameters = ParamExtractor.makeParamVals(dataSetDTO);
        Integer datasetId = rsDataSetDao.createDataset(parameters);
        returnVal.setDataSetId(datasetId);

        return returnVal;
    }

    @Override
    public DataSetDTO replace(Integer projectId, DataSetDTO dataSetDTO) throws GobiiDtoMappingException {

        DataSetDTO returnVal = dataSetDTO;


        // Under the GP1-1534 dispensation, the modified_date and modified_by columns of dataset are
        // exclusively reserved for the date and submitter of a successful load job. This scenario
        // is handled by the updateDatasetForJobInfo() method. In the generic case, as we have here,
        // want these columns to be null. In other words, modified_date and modified_by should have
        // values only when the dataset has been successfully loaded.
        Map<String, Object> parameters = ParamExtractor.makeParamVals(returnVal);
        parameters.put("projectId", projectId);
        rsDataSetDao.updateDataSet(parameters);


        return returnVal;
    }


    @Override
    public void updateDatasetForJobInfo(JobDTO jobDTO, DataSetDTO dataSetDTO) throws GobiiDtoMappingException {

        // since this method is not defined at the base class auditable level, the date column will not be
        // overwritten by the aspect
        if( jobDTO.getType().equals(JobType.CV_JOBTYPE_LOAD.getCvName())
                && (jobDTO.getStatus().equals(JobProgressStatusType.CV_PROGRESSSTATUS_COMPLETED.getCvName())
                || jobDTO.getStatus().equals(JobProgressStatusType.CV_PROGRESSSTATUS_QCPROCESSING.getCvName()))
        ) {

            dataSetDTO.setModifiedBy(jobDTO.getSubmittedBy());
            dataSetDTO.setModifiedDate(jobDTO.getSubmittedDate());

        } // --if it's a load job and it's complete

        Map<String, Object> parameters = ParamExtractor.makeParamVals(dataSetDTO);
        rsDataSetDao.updateDataSet(parameters);

    }

}
