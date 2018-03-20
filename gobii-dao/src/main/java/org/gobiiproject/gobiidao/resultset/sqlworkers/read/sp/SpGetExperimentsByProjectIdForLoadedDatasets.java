package org.gobiiproject.gobiidao.resultset.sqlworkers.read.sp;

import org.hibernate.jdbc.Work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by VCalaminos on 6/14/2017.
 */
public class SpGetExperimentsByProjectIdForLoadedDatasets implements Work {

    private Map<String, Object> parameters = null;

    public SpGetExperimentsByProjectIdForLoadedDatasets(Map<String,Object> parameters) { this.parameters = parameters; }

    private ResultSet resultSet = null;

    public ResultSet getResultSet() { return resultSet; }


    @Override
    public void execute(Connection dbConnection) throws SQLException {

        String sql = "select\n" +
                "e.project_id, \n" +
                "e.name || d.name as name, \n" +
                "d.dataset_id as matrixdbid, \n" +
                "e.experiment_id, \n" +
                "e.name as experiment_name, \n" +
                "d.name as dataset_name, \n" +
                "d.callinganalysis_id, \n" +
                "e.code, \n" +
                "e.data_file, \n" +
                "(select gettotaldnarunsindataset from gettotaldnarunsindataset(d.dataset_id::text)) as totalsamples,\n" +
                "(select gettotaldnarunsindataset from gettotaldnarunsindataset(d.dataset_id::text)) as totalmarkers\n" +
                "from \n" +
                "experiment e \n" +
                "join \n" +
                "dataset d \n" +
                "on \n" +
                "d.experiment_id = e.experiment_id \n" +
                "inner join\n" +
                "job j\n" +
                "on\n" +
                "j.job_id = d.job_id\n" +
                "where \n" +
                "e.project_id = ?\n" +
                "and (\n" +
                "(j.type_id::text = (select cvid::text from getcvid('load', 'job_type', 1)) and j.status::text = (select cvid::text from getcvid('completed', 'job_status', 1)))\n" +
                "or (j.type_id::text = (select cvid::text from getcvid('extract', 'job_type', 1)))\n" +
                ")\n" +
                "order by e.experiment_id, e.project_id";

        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        preparedStatement.setInt(1, (Integer) parameters.get("projectId"));
        resultSet = preparedStatement.executeQuery();


    }

}
