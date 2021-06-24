package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.discrete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;
import org.gobiiproject.gobiimodel.config.GobiiException;

public class ListStatementLinkageGroupBrapi implements ListStatement {

    @Override
    public ListSqlId getListSqlId() { return ListSqlId.QUERY_ID_LINKAGE_GROUP_BRAPI_BYLIST; }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection,
                                                   Map<String, Object> jdbcParamVals,
                                                   Map<String, Object> sqlParamVals)
        throws SQLException, GobiiException {

        String pageSizeCondition = "";

        Integer pageSize = 0;
        Integer pageNumber = 0;

        if (sqlParamVals != null) {
            if (sqlParamVals.containsKey("pageSize")
                    && sqlParamVals.get("pageSize") instanceof Integer) {

                pageSize = (Integer) sqlParamVals.getOrDefault("pageSize", 0);

                if (pageSize > 0) {

                    pageSizeCondition = "LIMIT ? ";

                    if (sqlParamVals.containsKey("pageNum")
                            && sqlParamVals.get("pageNum") instanceof Integer) {

                        pageNumber = (Integer) sqlParamVals.getOrDefault("pageNum", 0);

                        if (pageNumber > 0) {

                            pageSizeCondition += "OFFSET ?";

                        }
                    }
                }
                else {
                    pageSizeCondition = "";
                }
            }
        }

        String sql = "WITH linkage_groups_paged AS " +
                "(SELECT * FROM linkage_group "+ pageSizeCondition+ ") " +
                "SELECT linkage_groups_paged.name AS linkage_group_name, " +
                "COUNT(marker_id) AS marker_count, linkage_groups_paged.stop AS max_position " +
                "FROM linkage_groups_paged " +
                "INNER JOIN marker_linkage_group USING(linkage_group_id) " +
                "GROUP BY linkage_groups_paged.name, linkage_groups_paged.stop;";

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        if(pageSize > 0) {

            returnVal.setInt(1, pageSize);

            if(pageNumber > 0) {
                returnVal.setInt(2, (pageNumber-1)*pageSize);
            }

        }

        return returnVal;
    }
}
