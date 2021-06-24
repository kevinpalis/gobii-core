package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.discrete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;
import org.gobiiproject.gobiidao.resultset.core.listquery.ParameterizedSql;
import static org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId.QUERY_ID_COUNT_OF_CHILDREN;

/**

 */
@SuppressWarnings("serial")
public class ListStatementCountOfChildren implements ListStatement {

    private final String PARAM_NAME_TABLE_PARENT = "tableNameParent";
    private final String PARAM_NAME_PARENT_ID = "parentId";
    private final String PARAM_NAME_TABLE_CHILD = "tableNameChild";
    private final String PARAM_NAME_TABLE_PARENT_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_TABLE_PARENT);
    //private final String PARAM_NAME_PARENT_ID_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_PARENT_ID);
    private final String PARAM_NAME_TABLE_CHILD_DELIMITED = ParameterizedSql.makeDelimitedParamName(PARAM_NAME_TABLE_CHILD);


    @Override
    public ListSqlId getListSqlId() {
        return QUERY_ID_COUNT_OF_CHILDREN;
    }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection, Map<String, Object> jdbcParamVals, Map<String, Object> sqlParamVals) throws SQLException {

        ParameterizedSql parameterizedSql =
                new ParameterizedSql("select count(*) "
                        + "from " + PARAM_NAME_TABLE_PARENT_DELIMITED + " pa "
                        + "join " + PARAM_NAME_TABLE_CHILD_DELIMITED +  " ch on (pa." + PARAM_NAME_TABLE_PARENT_DELIMITED + "_id = ch." + PARAM_NAME_TABLE_PARENT_DELIMITED + "_id) "
                        + "where pa." + PARAM_NAME_TABLE_PARENT_DELIMITED + "_id =?",
                        new HashMap<String, String>() {
                            {
                                put(PARAM_NAME_TABLE_PARENT_DELIMITED, null);
                                put(PARAM_NAME_TABLE_CHILD_DELIMITED, null);

                            }
                        });

        String sql = parameterizedSql
                .setParamValue(PARAM_NAME_TABLE_PARENT_DELIMITED, sqlParamVals.get(PARAM_NAME_TABLE_PARENT).toString())
                .setParamValue(PARAM_NAME_TABLE_CHILD_DELIMITED, sqlParamVals.get(PARAM_NAME_TABLE_CHILD).toString())
                .getSql();

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);
        Integer parentTableId = (Integer) jdbcParamVals.get(PARAM_NAME_PARENT_ID);
        returnVal.setInt(1, parentTableId);

        return returnVal;
    }
}
