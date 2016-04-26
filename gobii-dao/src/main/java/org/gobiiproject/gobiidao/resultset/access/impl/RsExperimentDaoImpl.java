package org.gobiiproject.gobiidao.resultset.access.impl;

import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsExperimentDao;
import org.gobiiproject.gobiidao.resultset.core.StoredProcExec;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.SpGetExperimentDetailsByExperimentId;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.SpGetExperimentNamesByProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Angel on 4/19/2016.
 */
public class RsExperimentDaoImpl implements RsExperimentDao {


    Logger LOGGER = LoggerFactory.getLogger(RsExperimentDao.class);

    @Autowired
    private StoredProcExec storedProcExec = null;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getExperimentNamesByProjectId(Integer projectId) throws GobiiDaoException {

        ResultSet returnVal = null;

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("projectId", projectId);
            SpGetExperimentNamesByProjectId spGetExperimentNamesByProjectId = new SpGetExperimentNamesByProjectId(parameters);

            storedProcExec.doWithConnection(spGetExperimentNamesByProjectId);

            returnVal = spGetExperimentNamesByProjectId.getResultSet();
        } catch (Exception e) {

            LOGGER.error("Error retrieving experiment names", e);
            throw (new GobiiDaoException(e));

        }


        return returnVal;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getExperimentDetailsForExperimentId(int experimentId) throws GobiiDaoException {

        ResultSet returnVal = null;

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("experimentId", experimentId);
            SpGetExperimentDetailsByExperimentId spGetExperimentDetailsByExperimentId = new SpGetExperimentDetailsByExperimentId(parameters);
            storedProcExec.doWithConnection(spGetExperimentDetailsByExperimentId);
            returnVal = spGetExperimentDetailsByExperimentId.getResultSet();
        } catch (Exception e) {

            LOGGER.error("Error retrieving experiment details", e);
            throw (new GobiiDaoException(e));

        }

        return returnVal;
    }


} // RsProjectDaoImpl