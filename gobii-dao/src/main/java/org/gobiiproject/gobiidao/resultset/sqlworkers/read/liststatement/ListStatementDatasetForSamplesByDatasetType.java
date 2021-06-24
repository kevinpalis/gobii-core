package org.gobiiproject.gobiidao.resultset.sqlworkers.read.liststatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;
import org.gobiiproject.gobiidao.resultset.core.listquery.ParameterizedSql;

/**
 * Created by VCalaminos on 2/20/2018.
 */
@SuppressWarnings("serial")
public class ListStatementDatasetForSamplesByDatasetType implements ListStatement {

    private final String PARAM_NAME_DATASET_TYPE = "datasetType";
    private final String PARAM_NAME_EXTERNAL_CODE = "externalCode";

    @Override
    public ListSqlId getListSqlId() { return ListSqlId.QUERY_ID_DATASET_FOR_SAMPLES_BY_DATATYPE; }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection, Map<String, Object> jdbcParamVals, Map<String, Object> sqlParamVals) throws SQLException {


        String datasetType = sqlParamVals.get(PARAM_NAME_DATASET_TYPE).toString();
        String quotedDatasetType = "'" + datasetType + "'";

        ParameterizedSql parameterizedSql =
                new ParameterizedSql("select * \n" +
                        "from dataset ds\n" +
                        "where ds.dataset_id::text IN (select jsonb_object_keys(dnr.dataset_dnarun_idx)\n" +
                        "from germplasm g\n" +
                        "join dnasample dns\n" +
                        "on (dns.germplasm_id = g.germplasm_id)\n" +
                        "join dnarun dnr\n" +
                        "on (dnr.dnasample_id = dns.sample_id)\n" +
                        "where g.external_code = ?)\n" +
                        "and ds.job_id is not null\n" +
                        "and ds.data_file is not null\n" +
                        "and ds.type_id = (select cvid::text from getcvid("+ PARAM_NAME_DATASET_TYPE +", 'dataset_type', 1))",
                        new HashMap<String, String>(){
                            {
                                put(PARAM_NAME_DATASET_TYPE, null);
                            }
                        });

        String sql = parameterizedSql
                .setParamValue(PARAM_NAME_DATASET_TYPE, quotedDatasetType)
                .getSql();

        PreparedStatement returnVal = dbConnection.prepareStatement(sql);
        String externalCode = (String) jdbcParamVals.get(PARAM_NAME_EXTERNAL_CODE);
        returnVal.setString(1, externalCode);

        return returnVal;
    }

}
