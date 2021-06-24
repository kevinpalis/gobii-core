package org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.jdbc.Work;

/**
 * Created by Phil on 4/7/2016.
 */
public class SpGetProjectNames implements Work {

    //private Map<String, Object> parameters = null;

    public SpGetProjectNames() {
    }


    private ResultSet resultSet = null;

    public ResultSet getResultSet() {
        return resultSet;
    }

    @Override
    public void execute(Connection dbConnection) throws SQLException {

        String sql = "select p.project_id, \n" +
                "p.name, \n" +
                "c.contact_id\n" +
                "from project p \n" +
                "left outer join contact c on (p.pi_contact=c.contact_id)\n" +
                "order by lower(name)";

        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);

        resultSet = preparedStatement.executeQuery();

    } // execute()
}
