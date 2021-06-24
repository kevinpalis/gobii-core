package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.discrete.sampletracking;

import static org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId.QUERY_ID_PROJECT_ALL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

public class ListStatementProjectAll implements ListStatement {

    @Override
    public ListSqlId getListSqlId() {
        return QUERY_ID_PROJECT_ALL;
    }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection,
                                                   Map<String, Object> jdbcParamVals,
                                                   Map<String, Object> sqlParamVals)
            throws SQLException, GobiiException {

        String pageCondition = "";
        String pageSizeCondition = "";
        Integer pageSize = 0;

        if(sqlParamVals != null) {
            if (sqlParamVals.containsKey("pageSize")
                    && sqlParamVals.get("pageSize") instanceof Integer) {
                pageSize = (Integer) sqlParamVals.getOrDefault("pageSize", 0);
                if (pageSize > 0) {
                    pageSizeCondition = "LIMIT ?";
                }
                else {
                    throw new GobiiException(
                            GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.BAD_REQUEST,
                            "Invalid Page Size");
                }
            } else if(sqlParamVals.containsKey("pageSize")) {
                    throw new GobiiException(
                            GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.BAD_REQUEST,
                            "Invalid Page Size");
            }

            if (sqlParamVals.containsKey("pageToken")
                    && sqlParamVals.get("pageToken") instanceof Integer) {
                if ((Integer) sqlParamVals.getOrDefault("pageToken", 0) > 0) {
                    pageCondition = " WHERE project_id > ?";
                } else {
                    pageCondition = "";
                }
            } else if(sqlParamVals.containsKey("pageToken")) {
                throw new GobiiException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.BAD_REQUEST,
                        "Invalid Page Token");
            }
        }

        String sql = "SELECT project.*, project_prop.properties AS system_properties " +
                "FROM project LEFT OUTER JOIN (" +
                "SELECT p.project_id AS project_id, " +
                "json_object(array_agg(cv.term::text), array_agg(p.props->>p.props_keys::text)) AS properties " +
                "FROM(SELECT project.project_id, project.props, jsonb_object_keys(props)::int as props_keys " +
                "FROM project " + pageCondition +
                "ORDER BY project_id "+ pageSizeCondition +") AS p " +
                "INNER JOIN cv AS cv ON (cv.cv_id = p.props_keys) GROUP BY 1) AS project_prop " +
                "USING(project_id) " + pageCondition + " ORDER BY project.project_id " + pageSizeCondition;


        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        if(!pageCondition.isEmpty() && !pageSizeCondition.isEmpty()) {

            returnVal.setInt(1, (Integer) sqlParamVals.get("pageToken"));
            returnVal.setInt(2, pageSize);
            returnVal.setInt(3, (Integer) sqlParamVals.get("pageToken"));
            returnVal.setInt(4, pageSize);
        }
        else if(!pageCondition.isEmpty()) {
            returnVal.setInt(1, (Integer) sqlParamVals.get("pageToken"));
            returnVal.setInt(2, (Integer) sqlParamVals.get("pageToken"));
        }
        else if(!pageSizeCondition.isEmpty()) {
            returnVal.setInt(1, pageSize);
            returnVal.setInt(2, pageSize);
        }

        return returnVal;
    }

}
