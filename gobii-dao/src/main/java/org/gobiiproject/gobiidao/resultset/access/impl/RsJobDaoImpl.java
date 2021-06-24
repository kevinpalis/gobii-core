package org.gobiiproject.gobiidao.resultset.access.impl;

import java.util.Map;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsJobDao;
import org.gobiiproject.gobiidao.resultset.core.SpRunnerCallable;
import org.gobiiproject.gobiidao.resultset.core.StoredProcExec;
import org.gobiiproject.gobiidao.resultset.sqlworkers.modify.SpInsJobByCvTerms;
import org.gobiiproject.gobiidao.resultset.sqlworkers.modify.SpUpdJobByCvTerms;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by VCalaminos on 9/4/2017.
 */
public class RsJobDaoImpl implements RsJobDao {

    Logger LOGGER = LoggerFactory.getLogger(RsJobDao.class);

    @Autowired
    @SuppressWarnings("unused")
    private StoredProcExec storedProcExec = null;

    @Autowired
    private SpRunnerCallable spRunnerCallable;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer createJobWithCvTerms(Map<String, Object> parameters) throws GobiiDaoException {

        try {

            return spRunnerCallable.run(new SpInsJobByCvTerms(), parameters);

        } catch (SQLGrammarException e) {

            LOGGER.error("Error creating job with SQL " + e.getSQL(), e.getSQLException());
            throw (new GobiiDaoException(e.getSQLException()));

        }

    }

    @Transactional(propagation = Propagation.REQUIRED
//    ,isolation = Isolation.SERIALIZABLE
    )
    @Override
    public void updateJobWithCvTerms(Map<String, Object> parameters) throws GobiiDaoException {

        try {

            spRunnerCallable.run(new SpUpdJobByCvTerms(), parameters);

        } catch (SQLGrammarException e) {

            LOGGER.error("Error updating job with SQL " + e.getSQL(), e.getSQLException());
            throw (new GobiiDaoException(e.getSQLException()));

        }


    }


}
