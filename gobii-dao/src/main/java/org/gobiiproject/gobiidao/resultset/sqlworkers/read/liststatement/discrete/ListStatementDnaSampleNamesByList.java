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
import static org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId.QUERY_ID_DNASAMPLE_NAMES_BYLIST;

/**
 * Created by VCalaminos on 9/18/2018.
 */
@SuppressWarnings("serial")
public class ListStatementDnaSampleNamesByList implements ListStatement {

    private final String PARAM_NAME_NAME_LIST = "nameArray";
    private final String PARAM_NAME_PROJECT_ID = "projectId";

    @Override
    public ListSqlId getListSqlId() { return QUERY_ID_DNASAMPLE_NAMES_BYLIST; }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public PreparedStatement makePreparedStatement(Connection dbConnection, Map<String, Object> jdbcParamVals, Map<String, Object> sqlParamVals) throws SQLException {

        List<NameIdDTO> nameArray = (ArrayList) sqlParamVals.get(PARAM_NAME_NAME_LIST);

        // parse array into csv

        String parsedNameList = ListStatementUtil.generateParsedNameListForDnaSamples(nameArray);

        ParameterizedSql parameterizedSql =
                new ParameterizedSql("select dnasample_id, name, num " +
                        "from dnasample " +
                        "where project_id::varchar = ?" +
                        "and ("+ PARAM_NAME_NAME_LIST+ ")",
                        new HashMap<String, String>(){
                            {
                                put(PARAM_NAME_NAME_LIST, null);
                            }
                        });

        String sql = parameterizedSql
                .setParamValue(PARAM_NAME_NAME_LIST, parsedNameList)
                .getSql();

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        String projectId = (String) jdbcParamVals.get(PARAM_NAME_PROJECT_ID);
        returnVal.setString(1, projectId);

        return returnVal;
    }

}
