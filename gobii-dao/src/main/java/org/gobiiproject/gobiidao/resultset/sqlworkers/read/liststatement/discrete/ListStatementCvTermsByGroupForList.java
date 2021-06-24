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
import static org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId.QUERY_ID_CV_BY_GROUP_AND_LIST;

/**
 * Created by VCalaminos on 1/10/2018.
 */
@SuppressWarnings("serial")
public class ListStatementCvTermsByGroupForList  implements ListStatement {

    private final String PARAM_NAME_NAME_LIST = "nameArray";
    private final String PARAM_NAME_CV_GROUP_NAME = "cvGroupName";

    @Override
    public ListSqlId getListSqlId() { return QUERY_ID_CV_BY_GROUP_AND_LIST; }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public PreparedStatement makePreparedStatement(Connection dbConnection, Map<String, Object> jdbcParamVals, Map<String, Object> sqlParamVals) throws SQLException {

        List<NameIdDTO> nameArray = (ArrayList) sqlParamVals.get(PARAM_NAME_NAME_LIST);

        // parse array into CSV

        String parsedNameList = ListStatementUtil.generateParsedNameList(nameArray);

        ParameterizedSql parameterizedSql =
                new ParameterizedSql("select c.cv_id, c.term "
                        + "from cv c, cvgroup cg "
                        + "where c.cvgroup_id = cg.cvgroup_id "
                        + "and c.term in (" + PARAM_NAME_NAME_LIST + ") "
                        + "and cg.name = ?",
                        new HashMap<String, String>(){
                            {
                                put(PARAM_NAME_NAME_LIST, null);
                            }
                        });

        String sql = parameterizedSql
                .setParamValue(PARAM_NAME_NAME_LIST, parsedNameList)
                .getSql();

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);
        String cvGroupName = (String) jdbcParamVals.get(PARAM_NAME_CV_GROUP_NAME);
        returnVal.setString(1, cvGroupName);

        return returnVal;
    }


}
