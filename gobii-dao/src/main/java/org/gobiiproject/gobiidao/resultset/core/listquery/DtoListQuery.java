package org.gobiiproject.gobiidao.resultset.core.listquery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.cache.PageFrameState;
import org.gobiiproject.gobiidao.cache.PageFramesTrackingCache;
import org.gobiiproject.gobiidao.resultset.core.ResultColumnApplicator;
import org.gobiiproject.gobiidao.resultset.core.StoredProcExec;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.system.PagedList;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Phil on 10/25/2016.
 */
public class DtoListQuery<T> {

    Logger LOGGER = LoggerFactory.getLogger(DtoListQuery.class);

    protected ListStatement listStatement;
    protected ListStatementPaged listStatementPaged;
    protected Class<T> dtoType;
    protected StoredProcExec storedProcExec;
    protected PageFramesTrackingCache pageFramesTrackingCache;


    public DtoListQuery(StoredProcExec storedProcExec,
                        Class<T> dtoType,
                        PageFramesTrackingCache pageFramesTrackingCache,
                        ListStatement listStatement,
                        ListStatementPaged listStatementPaged) {

        this.storedProcExec = storedProcExec;
        this.dtoType = dtoType;
        this.pageFramesTrackingCache = pageFramesTrackingCache;
        this.listStatement = listStatement;
        this.listStatementPaged = listStatementPaged;
    }


    protected List<T> makeDtoListFromResultSet(ResultSet resultSet) throws IllegalArgumentException,
            InstantiationException,
            SQLException {

        List<T> returnVal = new ArrayList<>();

        while (resultSet.next()) {
            try {
                T dto = dtoType.newInstance();
                ResultColumnApplicator.applyColumnValues(resultSet, dto);
                returnVal.add(dto);
            } catch (IllegalAccessException e) {
                throw new SQLException(e);
            } catch (InstantiationException e) {
                throw new SQLException(e);
            }
        }

        return returnVal;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<T> getDtoList(Map<String, Object> jdbcParameters, Map<String, Object> sqlParameters) throws GobiiException {

        List<T> returnVal;

        try {

            ResultSetFromSql dtoListFromSql = new ResultSetFromSql(listStatement, jdbcParameters, sqlParameters);
            this.storedProcExec.doWithConnection(dtoListFromSql);
            ResultSet resultSet = dtoListFromSql.getResultSet();
            returnVal = this.makeDtoListFromResultSet(resultSet);

        }
        catch (SQLGrammarException e) {
            String message = "Error retrieving dto list " + this.listStatement.getListSqlId().name() + " with SQL " + e.getSQL();
            LOGGER.error(message, e.getSQLException());
            throw (new GobiiDaoException(message, e.getSQLException()));

        }
        catch (GobiiException gE) {
            LOGGER.error(gE.getMessage(), gE);
            throw gE;
        }
        catch (Exception e) {
            String message = "Error retrieving dto list " + this.listStatement.getListSqlId().name();
            LOGGER.error(message, e);
            throw (new GobiiDaoException(message, e));

        }

        return returnVal;

    } // getDtoList()

    /***
     * This method does two queries. The first, when the there is no existing query ID, will populate the page
     * frame table that enables the client to know how many pages there are given a specific page size. In other words,
     * if there are N total records in the target query, those records will be divided by the page size, and the
     * page frames table will provide the record boundaries with which each page can be retrieved from the target query
     * result. Once the page frame query has been run, the result is cached so that the page frame query only has to be
     * run once per execution cycle.
     * @param pageSize the number of records per page
     * @param pageNo The number of the page from with the page frame table
     * @param pgQueryIdFromUser The ID with which to identify the page frames from a previous queury
     * @return
     * @throws GobiiException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @SuppressWarnings("rawtypes")
    public PagedList<T> getDtoListPaged(Integer pageSize, Integer pageNo,
                                        String pgQueryIdFromUser) throws GobiiException {

        PagedList<T> returnVal;

        try {

            // ideally, all query types will have a paged implementation
            if (listStatementPaged == null) {
                throw new GobiiException(
                        "There is no paged query support for query " + listStatement.getListSqlId().name());
            }

            String pgQueryId;
            if (StringUtils.isNotEmpty(pgQueryIdFromUser)) {
                pgQueryId = pgQueryIdFromUser;
            } else {
                pgQueryId = UUID.randomUUID().toString();
            }

            // in theory an existing id might have gotten nuked if the server were restarted
            PageFrameState pageFrameState = this.pageFramesTrackingCache.getPageFrames(pgQueryId);

            ResultSetFromSqlPaged resultSetFromSqlPaged = new ResultSetFromSqlPaged(
                    listStatementPaged, pageSize, pageNo, pageFrameState);
            this.storedProcExec.doWithConnection(resultSetFromSqlPaged);
            ResultSet resultSet = resultSetFromSqlPaged.getResultSet();
            List<T> dtoList = this.makeDtoListFromResultSet(resultSet);

            if (pageFrameState == null) {
                pgQueryId = UUID.randomUUID().toString(); //force client to refresh query id
                pageFrameState = resultSetFromSqlPaged.getPageFrameState();
                this.pageFramesTrackingCache.setPageFrames(pgQueryId, pageFrameState);
            }


            returnVal = new PagedList<>(
                    resultSetFromSqlPaged.getPageFrameState().getCreated(),
                    dtoList,
                    pageSize,
                    resultSetFromSqlPaged.getPageNo(),
                    resultSetFromSqlPaged.getPageFrameState().getPages().size(),
                    pgQueryId
            );

        } catch (SQLGrammarException e) {
            String message = "Error retrieving dto list " +
                    this.listStatementPaged.getListSqlId().name() + " with SQL " + e.getSQL();
            LOGGER.error(message, e.getSQLException());
            throw (new GobiiDaoException(message, e.getSQLException()));

        } catch (Exception e) {
            //The message in e is important - it's probably the _only_ part of this thing that is!
            String message = "Error retrieving dto list " + this.listStatementPaged.getListSqlId().name() + " " + e.getMessage();
            LOGGER.error(message, e);
            throw (new GobiiDaoException(message, e));

        }

        return returnVal;

    } // getDtoList()

    @Transactional(propagation = Propagation.REQUIRED)
    public ResultSet getResultSet(Map<String, Object> jdbcParameters, Map<String, Object> sqlParameters) throws GobiiException {

        ResultSet returnVal;

        try {

            ResultSetFromSql resultSetFromSql = new ResultSetFromSql(
                    listStatement, jdbcParameters, sqlParameters);
            this.storedProcExec.doWithConnection(resultSetFromSql);
            returnVal = resultSetFromSql.getResultSet();

        } catch (SQLGrammarException e) {
            String message = "Error retrieving dto list " + listStatement.getListSqlId().name() + "with SQL " + e.getSQL();
            LOGGER.error(message, e.getSQLException());
            throw (new GobiiDaoException(message, e.getSQLException()));

        } catch (Exception e) {

            //The message in e is important - it's probably the _only_ part of this thing that is!
            String message = "Error retrieving dto list " + this.listStatementPaged.getListSqlId().name() + " " + e.getMessage();
            LOGGER.error(message, e);
            throw (new GobiiDaoException(message, e));

        }


        return returnVal;

    } // getDtoList()


}
