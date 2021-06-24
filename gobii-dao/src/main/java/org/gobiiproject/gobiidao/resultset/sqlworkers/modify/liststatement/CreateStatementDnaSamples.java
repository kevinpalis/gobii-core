package org.gobiiproject.gobiidao.resultset.sqlworkers.modify.liststatement;

import static org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId.CREATE_ID_DNASAMPLES;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListStatement;

/**
 * Created by VCalaminos on 5/2/2019.
 */
public class CreateStatementDnaSamples implements ListStatement {

    @Override
    public ListSqlId getListSqlId() { return CREATE_ID_DNASAMPLES; }

    @Override
    public PreparedStatement makePreparedStatement(Connection dbConnection, Map<String, Object> jdbcParamVals, Map<String, Object> sqlParamVals) throws SQLException {

        //List<DnaSampleDTO> sampleArray = (ArrayList) sqlParamVals.get("sampleList");

        //ParameterizedSql parameterizedSql =
                //new ParameterizedSql("");

        PreparedStatement returnVal = null;

        return returnVal;


    }

}
