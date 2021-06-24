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
 * Created by VCalaminos on 9/19/2018.
 */
@SuppressWarnings({"serial", "unchecked", "rawtypes"})
public class ListStatementPlatformByList implements ListStatement {

    private final String PARAM_NAME_NAME_LIST = "nameArray";

    @Override
    public ListSqlId getListSqlId() { return ListSqlId.QUERY_ID_PLATFORM_NAMES_BYLIST; }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection, Map<String, Object> jdbcParamVals, Map<String, Object> sqlParamVals) throws SQLException {

        List<NameIdDTO> nameArray = (ArrayList) sqlParamVals.get(PARAM_NAME_NAME_LIST);

        // parse array into csv

        String parsedNameList = ListStatementUtil.generateParsedNameList(nameArray);

        ParameterizedSql parameterizedSql =
                new ParameterizedSql("select platform_id, name " +
                        "from platform " +
                        "where name in (" + PARAM_NAME_NAME_LIST + ") ",
                        new HashMap<String, String>(){
                            {
                                put(PARAM_NAME_NAME_LIST, null);
                            }
                        });

        String sql = parameterizedSql
                .setParamValue(PARAM_NAME_NAME_LIST, parsedNameList)
                .getSql();

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        return returnVal;

    }

}
