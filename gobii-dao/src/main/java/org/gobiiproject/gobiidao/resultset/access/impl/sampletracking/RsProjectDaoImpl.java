package org.gobiiproject.gobiidao.resultset.access.impl.sampletracking;

import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.core.SpRunnerCallable;
import org.gobiiproject.gobiidao.resultset.core.StoredProcExec;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp.sampletracking.SpGetProjectDetailsByProjectId;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class RsProjectDaoImpl extends org.gobiiproject.gobiidao.resultset.access.impl.RsProjectDaoImpl {

    Logger LOGGER = LoggerFactory.getLogger(RsProjectDaoImpl.class);


    @Autowired
    private StoredProcExec storedProcExec = null;

    @Autowired
    @SuppressWarnings("unused")
    private SpRunnerCallable spRunnerCallable;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getProjectDetailsForProjectId(Integer projectId) throws GobiiDaoException {

        ResultSet returnVal;

        try {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("projectId", projectId);
            SpGetProjectDetailsByProjectId spGetProjectDetailsByProjectId = (
                    new SpGetProjectDetailsByProjectId(parameters));
            storedProcExec.doWithConnection(spGetProjectDetailsByProjectId);
            returnVal = spGetProjectDetailsByProjectId.getResultSet();

        } catch (SQLGrammarException e) {

            LOGGER.error("Error retrieving project details", e.getSQL(), e.getSQLException());
            throw (new GobiiDaoException(e.getSQLException()));

        }
        return returnVal;
    } // getProjectDetailsForProjectId()

}
