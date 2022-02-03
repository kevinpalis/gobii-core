package org.gobiiproject.gobiiprocess;

import java.sql.*;

import org.gobiiproject.gobiimodel.config.ServerConfig;
import org.gobiiproject.gobiimodel.utils.HelperFunctions;
import org.gobiiproject.gobiimodel.utils.error.Logger;

/**
 * Created by jdl232 on 6/21/2017.
 */
public class SimplePostgresConnector {

    public SimplePostgresConnector(ServerConfig config){
        this.dbConn=getDataSource(config);
    }

    public SimplePostgresConnector(Connection dbConn){
        this.dbConn = dbConn;
    }

    private Connection dbConn=null;
    public static Connection getDataSource(ServerConfig config){
        Connection conn = null;
        try{
            String jdbcUrl = HelperFunctions.getJdbcConnectionString(config);
            conn=DriverManager.getConnection(jdbcUrl,config.getUserName(),config.getPassword());
        }catch(SQLException e){
            Logger.logError("SimplePostgresConnector","Failed creating postgres connection",e);
        }
        return conn;
    }

    /**
     * Use if you want a query that returns true on any result, or false on empty result.
     * @param query Query to execute, as a string
     * @return True or False
     * @throws SQLException Often
     */
    public boolean boolQuery(String query) throws SQLException{
        Statement s = dbConn.createStatement();
        boolean ret = s.execute(query);
        s.close();
        return ret;
    }

    /**
     *
     * @param query Query to execute, as a string
     * @return null if failed, first integer otherwise
     * @throws SQLException Often
     */
    public Integer intQuery(String query) throws SQLException{
        Statement s = dbConn.createStatement();
        if(!s.execute(query)) return null;
        ResultSet rs = s.getResultSet();
        if(rs.isAfterLast()) return null;
        int i = rs.getInt(1);//1 based for some reason
        s.close();
        return i;
    }

    /**
     * Calls boolQuery on 'Select 1 from {TABLE} where {ENTITY} = {NAME} LIMIT 1
     * returns true if there were any results
     * @param table table name
     * @param entity column name in the table
     * @param name object's identifier in the column named in entity
     * @return True or False
     * @throws SQLException Often
     */
    public boolean hasEntry(String table, String entity, String name) throws SQLException{
        String statement="SELECT 1 from "+table+" WHERE "+entity+" = '"+ name + "' LIMIT 1";
        return boolQuery(statement);
    }

    public Integer getProjectId(String name) throws SQLException{
        String statement="SELECT project_id from project WHERE name = '"+ name + "' LIMIT 1";
        return intQuery(statement);
    }
    public Integer getPlatformId(String name) throws SQLException{
        String statement="SELECT platform_id from platform WHERE name = '"+ name + "' LIMIT 1";
        return intQuery(statement);
    }
    public Integer getExperimentId(String name) throws SQLException{
        String statement="SELECT experiment_id from experiment WHERE name = '"+ name + "' LIMIT 1";
        return intQuery(statement);
    }

    public boolean hasMarkerInPlatform(String name, int platform) {
        String statement="SELECT 1 from marker WHERE name = '"+ name + "' and platform_id = "+platform+" LIMIT 1";
        try {
            return boolQuery(statement);
        }catch(SQLException e){
			Logger.logError("Postgres Connector",e);}
        return false;
    }

    public boolean hasMarker(String markerName){
        try {
            return hasEntry("marker", "name", markerName);
        }catch(SQLException e){
            Logger.logError("Postgres Connector",e);
        }
        return false;
    }




    public boolean hasDNARuninExperiment(String name, int experiment) {
        String statement="SELECT 1 from dnarun WHERE name = '"+ name + "' and experiment_id = "+experiment+" LIMIT 1";
        try {
            return boolQuery(statement);
        }catch(SQLException e){
			Logger.logError("Postgres Connector",e);}
        return false;
    }

    public boolean hasCVEntry(String cvGroupName, String cvName) throws SQLException{
        String statement="SELECT 1 from cv join cvgroup b on cv.cvgroup_id = b.cvgroup_id where b.name = '"+cvGroupName+"' and cv.term = '"+cvName+"' LIMIT 1";
        return boolQuery(statement);
    }
    public boolean hasGermplasmType(String germplasmType) {
        try{
            return hasCVEntry("germplasm_type",germplasmType);
        }catch(SQLException e){
            Logger.logError("Postgres Connector",e);
        }
        return false;
    }
    public boolean hasGermplasmSpecies(String germplasmSpecies) {
        try{
            return hasCVEntry("germplasm_species",germplasmSpecies);
        }catch(SQLException e){
            Logger.logError("Postgres Connector",e);
        }
        return false;
    }

    public boolean close(){
        try {
            dbConn.close();
        } catch (SQLException e) {
            Logger.logError("SimplePostgresConnector","Error closing",e);
            return false;
        }
        dbConn=null;
        return true;
    }

}
