package org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.jdbc.Work;

/**
 * Created by Phil on 4/7/2016.
 */
public class SpGetManifestNames implements Work {

    //private Map<String, Object> parameters = null;

    public SpGetManifestNames() {
    }


    private ResultSet resultSet = null;

    public ResultSet getResultSet() {
        return resultSet;
    }

    @Override
    public void execute(Connection dbConnection) throws SQLException {

        String sql = "select manifest_id, name from manifest order by lower(name)";

        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);

        resultSet = preparedStatement.executeQuery();

    } // execute()
}
