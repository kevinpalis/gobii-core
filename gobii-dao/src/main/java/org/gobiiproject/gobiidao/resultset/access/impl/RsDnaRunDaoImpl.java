package org.gobiiproject.gobiidao.resultset.access.impl;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsDnaRunDao;
import org.gobiiproject.gobiidao.resultset.core.StoredProcExec;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp.SpGetDnaRunByDnaRunId;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by VCalaminos on 6/25/2019.
 */
public class RsDnaRunDaoImpl implements RsDnaRunDao {

    Logger LOGGER = LoggerFactory.getLogger(RsDnaRunDaoImpl.class);

    @Autowired
    private StoredProcExec storedProcExec = null;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getDnaRunForDnaRunId(Integer dnaRunId) throws GobiiDaoException {

        ResultSet returnVal;

        try {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("dnaRunId", dnaRunId);
            SpGetDnaRunByDnaRunId spGetDnaRunByDnaRunId = new SpGetDnaRunByDnaRunId(parameters);
            storedProcExec.doWithConnection(spGetDnaRunByDnaRunId);
            returnVal = spGetDnaRunByDnaRunId.getResultSet();

        } catch (SQLGrammarException e) {

            LOGGER.error("Error retrieving dnarun", e.getSQL(), e.getSQLException());
            throw (new GobiiDaoException(e.getSQLException()));
        }

        return returnVal;

    }
}
