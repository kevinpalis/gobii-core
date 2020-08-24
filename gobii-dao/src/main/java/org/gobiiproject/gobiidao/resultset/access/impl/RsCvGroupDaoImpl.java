package org.gobiiproject.gobiidao.resultset.access.impl;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsCvGroupDao;
import org.gobiiproject.gobiidao.resultset.core.SpRunnerCallable;
import org.gobiiproject.gobiidao.resultset.core.StoredProcExec;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp.SpCvGroupById;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp.SpGetCvGroupDetailsForGroupName;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp.SpGetCvGroupsByTypeId;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp.SpGetCvItemsByGroupId;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp.SpGetGroupTypeByGroupId;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp.SpUserCvGroupByName;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by VCalaminos on 2/1/2017.
 */
public class RsCvGroupDaoImpl implements RsCvGroupDao {

    Logger LOGGER = LoggerFactory.getLogger(RsCvGroupDao.class);

    @Autowired
    private StoredProcExec storedProcExec = null;

    @Autowired
    @SuppressWarnings("unused")
    private SpRunnerCallable spRunnerCallable;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getCvsByGroupId(Integer groupId) throws GobiiDaoException {

        ResultSet returnVal;

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("groupId", groupId);
            SpGetCvItemsByGroupId spGetCvItemsByGroupId = new SpGetCvItemsByGroupId(parameters);

            storedProcExec.doWithConnection(spGetCvItemsByGroupId);

            returnVal = spGetCvItemsByGroupId.getResultSet();

        } catch (SQLGrammarException e) {

            LOGGER.error("Error retrieving Cvs by group id " + e.getSQL(), e.getSQLException());
            throw (new GobiiDaoException(e.getSQLException()));

        }

        return returnVal;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getCvGroupsForType(Integer groupType) throws GobiiDaoException {

        ResultSet returnVal;

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("groupType", groupType);
            SpGetCvGroupsByTypeId spSpGetCvGroupsByTypeId = new SpGetCvGroupsByTypeId(parameters);

            storedProcExec.doWithConnection(spSpGetCvGroupsByTypeId);

            returnVal = spSpGetCvGroupsByTypeId.getResultSet();

        } catch (SQLGrammarException e) {

            LOGGER.error("Error retrieving Cvs by group id " + e.getSQL(), e.getSQLException());
            throw (new GobiiDaoException(e.getSQLException()));

        }

        return returnVal;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getGroupTypeForGroupId(Integer groupId) throws GobiiDaoException {

        ResultSet returnVal;

        try {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("groupId", groupId);
            SpGetGroupTypeByGroupId spGetGroupTypeByGroupId = new SpGetGroupTypeByGroupId(parameters);

            storedProcExec.doWithConnection(spGetGroupTypeByGroupId);

            returnVal = spGetGroupTypeByGroupId.getResultSet();


        } catch (SQLGrammarException e) {

            LOGGER.error("Error retrieving group type " + e.getSQL(), e.getSQLException());
            throw (new GobiiDaoException(e.getSQLException()));

        }

        return returnVal;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getUserCvGroupByName(String groupName) throws GobiiDaoException {

        ResultSet returnVal;

        try {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("groupName", groupName);
            SpUserCvGroupByName spUserCvGroupByName = new SpUserCvGroupByName(parameters);

            storedProcExec.doWithConnection(spUserCvGroupByName);

            returnVal = spUserCvGroupByName.getResultSet();


        } catch (SQLGrammarException e) {

            LOGGER.error("Error retrieving group type " + e.getSQL(), e.getSQLException());
            throw (new GobiiDaoException(e.getSQLException()));

        }

        return returnVal;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getCvGroupById(Integer groupId) throws GobiiDaoException {
        ResultSet returnVal;

        try {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("groupId", groupId);
            SpCvGroupById spCvGroupById = new SpCvGroupById(parameters);

            storedProcExec.doWithConnection(spCvGroupById);

            returnVal = spCvGroupById.getResultSet();


        } catch (SQLGrammarException e) {

            LOGGER.error("Error retrieving group type " + e.getSQL(), e.getSQLException());
            throw (new GobiiDaoException(e.getSQLException()));

        }

        return returnVal;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultSet getCvGroupDetailsByGroupName(String groupName, Integer cvGroupTypeId) throws GobiiDaoException {

        ResultSet returnVal;

        try {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("groupName", groupName);
            parameters.put("cvGroupTypeId", cvGroupTypeId);

            SpGetCvGroupDetailsForGroupName spGetCvGroupDetailsForGroupName = new SpGetCvGroupDetailsForGroupName(parameters);
            storedProcExec.doWithConnection(spGetCvGroupDetailsForGroupName);
            returnVal = spGetCvGroupDetailsForGroupName.getResultSet();


        } catch (SQLGrammarException e) {

            LOGGER.error("Error retrieving cv group details " + e.getSQL(), e.getSQLException());
            throw (new GobiiDaoException(e.getSQLException()));

        }

        return returnVal;

    }

}
