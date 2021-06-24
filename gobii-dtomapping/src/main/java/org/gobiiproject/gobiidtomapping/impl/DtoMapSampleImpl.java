package org.gobiiproject.gobiidtomapping.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsSampleDao;
import org.gobiiproject.gobiidao.resultset.core.ResultColumnApplicator;
import org.gobiiproject.gobiidao.resultset.core.listquery.DtoListQueryColl;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidtomapping.DtoMapSample;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.noaudit.DtoMapJob;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.DnaSampleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Phil on 3/29/2016.
 */
@SuppressWarnings("serial")
public class DtoMapSampleImpl implements DtoMapSample {

    Logger LOGGER = LoggerFactory.getLogger(DtoMapSampleImpl.class);

    @Autowired
    RsSampleDao rsSampleDao = null;

    @Autowired
    private DtoMapJob dtoMapJob = null;

    @Autowired
    private DtoListQueryColl dtoListQueryColl;



    @Transactional
    @Override
    public DnaSampleDTO getSampleDetailsByExternalCode(String externalCode) throws GobiiDtoMappingException {

        DnaSampleDTO returnVal = new DnaSampleDTO();


        try {

            ResultSet resultSet = rsSampleDao.getSampleDetailsByExternalCode(externalCode);

            if (resultSet.next()) {

                ResultColumnApplicator.applyColumnValues(resultSet, returnVal);
            }

        } catch(SQLException e) {
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;

    }

    @Transactional
    @Override
    public List<DataSetDTO> getDatasetForLoadedSamplesOfDataType(String externalCode, String datasetType) throws GobiiDtoMappingException {

        List<DataSetDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = dtoListQueryColl.getResultSet(ListSqlId.QUERY_ID_DATASET_FOR_SAMPLES_BY_DATATYPE,
                    new HashMap<String, Object>(){{
                        put("externalCode", externalCode);
                    }}, new HashMap<String, Object>(){{
                        put("datasetType", datasetType);
                    }});

            while (resultSet.next()) {

                DataSetDTO currentDatasetDTO = new DataSetDTO();

                ResultColumnApplicator.applyColumnValues(resultSet, currentDatasetDTO);

                returnVal.add(currentDatasetDTO);
            }

        } catch (Exception e) {

            throw new GobiiDaoException(e);

        }

        return returnVal;

    }

    @Override
    public JobDTO submitDnaSamplesByJobName(String jobName, List<DnaSampleDTO> dnaSampleDTOList) throws GobiiDtoMappingException {

        JobDTO returnVal = new JobDTO();

        try {

            returnVal = dtoMapJob.getJobDetailsByJobName(jobName);


        } catch (Exception e) {


        }

        return returnVal;
    }

} // DtoMapMarkerImpl
