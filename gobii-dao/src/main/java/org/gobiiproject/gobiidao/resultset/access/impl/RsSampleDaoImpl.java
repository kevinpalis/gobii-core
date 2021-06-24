package org.gobiiproject.gobiidao.resultset.access.impl;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsSampleDao;
import org.gobiiproject.gobiidao.resultset.core.SpRunnerCallable;
import org.gobiiproject.gobiidao.resultset.core.StoredProcExec;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.SpGetSampleDetailsByExternalCode;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Phil on 4/8/2016.
 */
public class RsSampleDaoImpl implements RsSampleDao {

    Logger LOGGER = LoggerFactory.getLogger(RsSampleDaoImpl.class);


    @Autowired
    private StoredProcExec storedProcExec = null;

    @Autowired
    @SuppressWarnings("unused")
    private SpRunnerCallable spRunnerCallable;


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getSampleDetailsByExternalCode(String externalCode) throws GobiiDaoException {


        ResultSet returnVal;

        try {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("externalCode", externalCode);
            SpGetSampleDetailsByExternalCode spGetSampleDetailsByExternalCode = new SpGetSampleDetailsByExternalCode(parameters);
            storedProcExec.doWithConnection(spGetSampleDetailsByExternalCode);
            returnVal = spGetSampleDetailsByExternalCode.getResultSet();

        } catch (SQLGrammarException e) {
            LOGGER.error("Error retrieving marker detail with SQL " + e.getSQL(), e.getSQLException());
            throw new GobiiDaoException(e.getSQLException());
        }

        return returnVal;
    }

} //
