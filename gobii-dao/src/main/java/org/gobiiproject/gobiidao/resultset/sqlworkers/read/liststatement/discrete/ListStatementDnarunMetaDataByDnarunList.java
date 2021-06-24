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

/**
 * Gets the Query for DNA run meta data required to pull genotype calls.
 */
public class ListStatementDnarunMetaDataByDnarunList implements ListStatement {

    @Override
    public ListSqlId getListSqlId() {
        return ListSqlId.QUERY_ID_DNARUN_METADATA_BY_DATASET;
    }

    /**
     *
     * @param dbConnection database connection
     * @param jdbcParamVals No use as of now
     * @param sqlParamVals For this specific query, sqlParamVals will have following keys,
     *                     datasetId - Dataset for which dnarun metadata needs to be fetched.
     *                     pageSize - Number of records to fetch.
     *                     pageOffset - Offset to fetch records.
     * @return SQL PreparedStatement with query string and its variables.
     * @throws SQLException
     * @throws GobiiException
     */
    @Override
    @SuppressWarnings("unchecked")
    public PreparedStatement makePreparedStatement(Connection dbConnection,
                                                   Map<String, Object> jdbcParamVals,
                                                   Map<String, Object> sqlParamVals)
            throws SQLException, GobiiException {


        if(sqlParamVals != null) {

            if((!sqlParamVals.containsKey("dnarunIdList")) || sqlParamVals.get("dnarunIdList") == null) {
                throw new GobiiException(
                        GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "Invalid Dnarun Id List");
            }
        }
        else {
            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "Invalid SQL Parameters");
        }

        String sql = "SELECT dnarun.dnarun_id AS dnarun_id, " +
                "dnarun.name AS dnarun_name, " +
                "dnarun.dataset_dnarun_idx as dataset_dnarun_idx " +
                "FROM dnarun WHERE dnarun.dnarun_id = ANY(?) ORDER BY dnarun_id";



        PreparedStatement returnVal = dbConnection.prepareStatement(sql);

        ArrayList<String> dnarunIdList = (ArrayList<String>) sqlParamVals.get("dnarunIdList");

        Array dnarunIdArray = dbConnection.createArrayOf("INTEGER", dnarunIdList.toArray());

        returnVal.setArray(1, dnarunIdArray);


        return returnVal;

    }

}
