package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.paged;

import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatementPaged;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId.QUERY_ID_DATASET_ALL;


/**
 As an implementation of ListStatementPaged, this class will provide the table-specific
 configuration in order consume PagedSql so as to produce the page frame and single page
 sql statements.
 */
public class ListStatementProjectPaged implements ListStatementPaged {


    @Override
    public String getNameColName() {
        return "name";
    }

    @Override
    public String getIdColName() {
        return "project_id";
    }

    @SuppressWarnings("unused")
    private String getColTableAlias() {
        return "proj";
    }

    @Override
    public String getPageNumberColName() {
        return null;
    }


    @Override
    public PreparedStatement makePreparedStatementForPageFrames(
            Connection dbConnection,Integer pageSize ) throws SQLException {

        return null;
    }

    @Override
    public PreparedStatement makePreparedStatementForAPage(
            Connection dbConnection, Integer pageSize,
            String pgItemNameVal, Integer pgItemIdVal) throws SQLException {

        Map<String, Object> sqlParamVals = new HashMap<>();

        PagerSql pagerSql = new PagerSql(sqlParamVals);

        String pageFrameSql = pagerSql.makeSinglePageSql();

        PreparedStatement returnVal = dbConnection.prepareStatement(pageFrameSql);

        return returnVal;
    }

    public ListSqlId getListSqlId() {
        return QUERY_ID_DATASET_ALL;
    }

}
