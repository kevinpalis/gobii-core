package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.discrete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;
import org.gobiiproject.gobiidao.resultset.core.listquery.ParameterizedSql;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;

/**
 * Created by VCalaminos on 9/24/2018.
 */
@SuppressWarnings("serial")
public class ListStatementDnaRunNamesByList implements ListStatement {

    private final String PARAM_NAME_NAME_LIST = "nameArray";
    private final String PARAM_NAME_EXPERIMENT_ID = "experimentId";

    @Override
    public ListSqlId getListSqlId() { return ListSqlId.QUERY_ID_DNARUN_NAMES_BYLIST; }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public PreparedStatement makePreparedStatement(Connection dbConnection, Map<String, Object> jdbcParamVals, Map<String, Object> sqlParamVals) throws SQLException {

        List<NameIdDTO> nameArray = (ArrayList) sqlParamVals.get(PARAM_NAME_NAME_LIST);

        // parse array into csv

        String parsedNameList = ListStatementUtil.generateParsedNameList(nameArray);

        ParameterizedSql parameterizedSql =
                new ParameterizedSql("select dnarun_id, name " +
                        "from dnarun " +
                        "where name in ("+ PARAM_NAME_NAME_LIST +") " +
                        "and experiment_id::varchar = ?",
                        new HashMap<String, String>(){
                            {
                                put(PARAM_NAME_NAME_LIST, null);
                            }
                        });

        String sql = parameterizedSql
                .setParamValue(PARAM_NAME_NAME_LIST, parsedNameList)
                .getSql();

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        String experimentId = (String) jdbcParamVals.get(PARAM_NAME_EXPERIMENT_ID);
        returnVal.setString(1, experimentId);

        return returnVal;
    }

}
