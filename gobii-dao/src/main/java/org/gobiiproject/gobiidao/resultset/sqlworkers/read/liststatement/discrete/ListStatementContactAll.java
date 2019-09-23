package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.discrete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;
import static org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId.QUERY_ID_DATASET_ALL;

/**

 */
public class ListStatementContactAll implements ListStatement {


    @Override
    public ListSqlId getListSqlId() {
        return QUERY_ID_DATASET_ALL;
    }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection, Map<String, Object> jdbcParamVals, Map<String, Object> sqlParamVals) throws SQLException {

        String sql = "select * from contact order by lower(lastname),lower(firstname)";

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        return returnVal;
    }
}
