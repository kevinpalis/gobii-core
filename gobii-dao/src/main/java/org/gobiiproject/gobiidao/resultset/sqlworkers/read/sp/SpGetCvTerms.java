package org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.jdbc.Work;

/**
 * Created by Angel on 4/26/2016.
 */
public class SpGetCvTerms implements Work {
    /**
     * Created by Angel on 4/26/2016.
     */
    //private Map<String, Object> parameters = null;

    public SpGetCvTerms() {
        //this.parameters = parameters;
    }

    private ResultSet resultSet = null;

    public ResultSet getResultSet() {
        return resultSet;
    }

    @Override
    public void execute(Connection dbConnection) throws SQLException {

        String Sql = "select c.cv_id, c.term, g.type as group_type from cv c, cvgroup g where c.cvgroup_id = g.cvgroup_id order by lower(term)";
        
        PreparedStatement preparedStatement = dbConnection.prepareStatement(Sql);
        
        resultSet = preparedStatement.executeQuery();
    } // execute()

}
