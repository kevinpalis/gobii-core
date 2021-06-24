package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement.discrete;

import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;


public class ListStatementMarkerMetaDataByMarkerList implements ListStatement {

    @Override
    public ListSqlId getListSqlId() {
        return ListSqlId.QUERY_ID_MARKER_METADATA_BY_DATASET;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection,
                                                   Map<String, Object> jdbcParamVals,
                                                   Map<String, Object> sqlParamVals)
            throws SQLException, GobiiException {

        //Integer pageOffset = 0;
        //Integer pageSize = 0;

        if(sqlParamVals != null) {

            if((!sqlParamVals.containsKey("markerIdList")) || sqlParamVals.get("markerIdList") == null) {
                throw new GobiiException(
                        GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "Invalid Marker Id List");
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
                "FROM marker WHERE marker.marker_id = ANY(?) ORDER BY marker_id";

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        ArrayList<String> markerIdList = (ArrayList<String>) sqlParamVals.get("markerIdList");

        Array markerIdArray = dbConnection.createArrayOf("INTEGER", markerIdList.toArray());

        returnVal.setArray(1, markerIdArray);


        return returnVal;

    }

}
