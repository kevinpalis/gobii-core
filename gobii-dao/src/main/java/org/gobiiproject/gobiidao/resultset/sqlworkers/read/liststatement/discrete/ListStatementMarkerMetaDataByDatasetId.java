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



public class ListStatementMarkerMetaDataByDatasetId implements ListStatement {

    @Override
    public ListSqlId getListSqlId() {
        return ListSqlId.QUERY_ID_MARKER_METADATA_BY_DATASET;
    }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection,
                                                   Map<String, Object> jdbcParamVals,
                                                   Map<String, Object> sqlParamVals)
            throws SQLException, GobiiException {

        Integer pageOffset = 0;
        //Integer pageSize = 0;

        if(sqlParamVals != null) {

            if((!sqlParamVals.containsKey("datasetId")) || sqlParamVals.get("datasetId") == null) {
                throw new GobiiException(
                        GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "Invalid Dataset Id");
            }

            if((!sqlParamVals.containsKey("pageSize")) || sqlParamVals.get("pageSize") == null ||
                    !(sqlParamVals.get("pageSize") instanceof Integer)) {
                throw new GobiiException(
                        GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "Invalid Page Size in query.");
            }

            if(sqlParamVals.containsKey("pageOffset") && sqlParamVals.get("pageOffset") != null &&
                    (sqlParamVals.get("pageSize") instanceof Integer)) {
                pageOffset = (Integer) sqlParamVals.get("pageOffset");
            }
        }
        else {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "Invalid SQL Parameters");
        }

        String sql = "SELECT marker.marker_id AS marker_id, " +
                "marker.name AS marker_name, " +
                "marker.dataset_marker_idx as dataset_marker_idx " +
                "FROM marker WHERE marker.dataset_marker_idx ?? ?::text " +
                "LIMIT ? ";


        if(pageOffset > 0) {
            sql += "OFFSET ? ";

        }

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        returnVal.setInt(1, (Integer) sqlParamVals.get("datasetId"));
        returnVal.setInt(2, (Integer) sqlParamVals.get("pageSize"));

        if(pageOffset > 0) {
            returnVal.setInt(3, (Integer) sqlParamVals.get("pageOffset"));
        }

        return returnVal;

    }

}
