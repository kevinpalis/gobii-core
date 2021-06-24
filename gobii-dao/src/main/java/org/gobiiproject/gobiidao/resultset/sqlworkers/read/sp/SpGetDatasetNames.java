package org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.jdbc.Work;

/**
 * Created by Angel on 4/29/2016.
 */
public class SpGetDatasetNames implements Work {

    @SuppressWarnings("unused")
    private Map<String, Object> parameters = null;

    public SpGetDatasetNames() {
    }

    private ResultSet resultSet = null;

    public ResultSet getResultSet() {
        return resultSet;
    }

    @Override
    public void execute(Connection dbConnection) throws SQLException {

        String sql = "select dataset_id, name from dataset order by lower(name)";

        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);

        resultSet = preparedStatement.executeQuery();

    } // execute()
}
