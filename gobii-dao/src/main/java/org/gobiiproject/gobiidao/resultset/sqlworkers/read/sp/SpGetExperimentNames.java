package org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.jdbc.Work;

/**
 * Created by Angel on 4/29/2016.
 */
public class SpGetExperimentNames implements Work {

    //private Map<String, Object> parameters = null;

    public SpGetExperimentNames() {
    }


    private ResultSet resultSet = null;

    public ResultSet getResultSet() {
        return resultSet;
    }

    @Override
    public void execute(Connection dbConnection) throws SQLException {

        String sql = "\n" +
                "select e.experiment_id, \n" +
                "e.name,\n" +
                "p.project_id\n" +
                "from experiment e\n" +
                "left outer join project p on (e.project_id=p.project_id)\n" +
                "order by lower(e.name)";

        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);

        resultSet = preparedStatement.executeQuery();

    } // execute()
}
