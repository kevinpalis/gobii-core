package org.gobiiproject.gobiidao.resultset.access.impl;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsMapsetBrapiDao;
import org.gobiiproject.gobiidao.resultset.core.StoredProcExec;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp.SpGetMapsetByMapsetIdBrapi;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public class RsMapsetBrapiDaoImpl implements RsMapsetBrapiDao {

    Logger LOGGER = LoggerFactory.getLogger(RsMapsetBrapiDaoImpl.class);

    @Autowired
    private StoredProcExec storedProcExec = null;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getMapsetByMapsetId(Integer mapsetId) throws GobiiDaoException {

        ResultSet returnVal;

        try {

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("mapsetId", mapsetId);

            SpGetMapsetByMapsetIdBrapi spGetMapsetByMapsetIdBrapi = new SpGetMapsetByMapsetIdBrapi(parameters);

            storedProcExec.doWithConnection(spGetMapsetByMapsetIdBrapi);

            returnVal = spGetMapsetByMapsetIdBrapi.getResultSet();

        } catch (SQLGrammarException e) {

            LOGGER.error("Error retrieving marker", e.getSQL(), e.getSQLException());
            throw new GobiiDaoException(e.getSQLException());
        }

        return returnVal;
    }

}
