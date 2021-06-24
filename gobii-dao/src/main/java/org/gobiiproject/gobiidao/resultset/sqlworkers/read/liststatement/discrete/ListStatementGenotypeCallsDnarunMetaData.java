package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.discrete;

import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId.QUERY_ID_GENOTYPE_CALLS_DNARUN_METADATA;


public class ListStatementGenotypeCallsDnarunMetaData implements ListStatement {

    @Override
    public ListSqlId getListSqlId() {
        return QUERY_ID_GENOTYPE_CALLS_DNARUN_METADATA ;
    }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection,
                                                   Map<String, Object> jdbcParamVals,
                                                   Map<String, Object> sqlParamVals)
            throws SQLException, GobiiException {

        //String pageCondition = "";
        String pageSizeCondition = "";

        Integer pageSize = 0;
        //Integer dnarunId;

        if(sqlParamVals != null) {

            if((!sqlParamVals.containsKey("datasetId")) || sqlParamVals.get("datasetId") == null) {
                throw new GobiiException(
                        GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "Required query parameter Dataset Id missing.");
            }

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

        }

        String sql = "SELECT dnarun.dnarun_id AS dnarun_id, " +
                "dnarun.name AS dnarun_name, " +
                "dnarun.dataset_dnarun_idx as dataset_dnarun_idx " +
                "FROM dnarun WHERE dnarun.dataset_dnarun_idx ?? ?::text " +
                "AND dnarun_id > ? ORDER BY dnarun.dnarun_id "+pageSizeCondition;

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        returnVal.setInt(1, (Integer) sqlParamVals.get("datasetId"));

        if (sqlParamVals.containsKey("dnarunId") &&
                (Integer) sqlParamVals.getOrDefault("dnarunId", 0) > 0) {
            returnVal.setInt(2, (Integer) sqlParamVals.get("dnarunId"));
        }
        else {
            returnVal.setInt(2, 0);
        }


        if(!pageSizeCondition.isEmpty()) {
            returnVal.setInt(3, pageSize);
        }

        return returnVal;

    }

}
