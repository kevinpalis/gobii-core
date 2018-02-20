package org.gobiiproject.gobiidtomapping.impl;

import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsMarkerDao;
import org.gobiiproject.gobiidao.resultset.access.RsSampleDao;
import org.gobiiproject.gobiidao.resultset.core.ParamExtractor;
import org.gobiiproject.gobiidao.resultset.core.ResultColumnApplicator;
import org.gobiiproject.gobiidao.resultset.core.listquery.DtoListQueryColl;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidtomapping.DtoMapSample;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.entity.auditable.DataSetDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.SampleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Phil on 3/29/2016.
 */
public class DtoMapSampleImpl implements DtoMapSample {

    Logger LOGGER = LoggerFactory.getLogger(DtoMapSampleImpl.class);

    @Autowired
    RsSampleDao rsSampleDao = null;

    @Autowired
    private DtoListQueryColl dtoListQueryColl;



    @Transactional
    @Override
    public SampleDTO getSampleDetailsByExternalCode(String externalCode) throws GobiiDtoMappingException {

        SampleDTO returnVal = new SampleDTO();


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

} // DtoMapMarkerImpl
