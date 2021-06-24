package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.discrete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;
import org.gobiiproject.gobiidao.resultset.core.listquery.ParameterizedSql;

/**
 * Created by VCalaminos on 11/5/2018.
 */
@SuppressWarnings("serial")
public class ListStatementMarkerNamesAll implements ListStatement {

    private final String PARAM_NAME_CALL_LIMIT = "callLimit";

    @Override
    public ListSqlId getListSqlId() { return ListSqlId.QUERY_ID_MARKER_NAMES_ALL; }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection, Map<String, Object> jdbcParamVals, Map<String, Object> sqlParamVals) throws SQLException {

        ParameterizedSql parameterizedSql =
                new ParameterizedSql("select marker_id, name " +
                        "from marker " +
                        "limit ?",
                        new HashMap<String, String>(){}
                );

        String sql = parameterizedSql.getSql();

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        Integer callLimit = (Integer) jdbcParamVals.get(PARAM_NAME_CALL_LIMIT);
        returnVal.setInt(1, callLimit);

        return returnVal;

    }


}
