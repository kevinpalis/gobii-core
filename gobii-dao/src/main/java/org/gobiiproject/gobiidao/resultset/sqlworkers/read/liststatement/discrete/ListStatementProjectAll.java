package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.discrete;

import static org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId.QUERY_ID_PROJECT_ALL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;

/**
 */
public class ListStatementProjectAll implements ListStatement {

    @Override
    public ListSqlId getListSqlId() {
        return QUERY_ID_PROJECT_ALL;
    }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection, Map<String, Object> jdbcParamVals, Map<String, Object> sqlParamVals) throws SQLException {

        String sql = "select * from project order by lower(name)";

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        return returnVal;
    }
}
