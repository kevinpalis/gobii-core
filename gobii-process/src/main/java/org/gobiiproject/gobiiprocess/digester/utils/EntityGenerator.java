package org.gobiiproject.gobiiprocess.digester.utils;

import lombok.Builder;
import org.gobii.masticator.aspects.ConstantAspect;
import org.gobii.masticator.aspects.ElementAspect;
import org.gobii.masticator.aspects.FileAspect;
import org.gobii.masticator.aspects.TableAspect;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiiprocess.SimplePostgresConnector;
import org.gobiiproject.gobiiprocess.digester.EBSLoader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Standardized class for creating new database entity shells for needed Gobii data entities.
 */


public class EntityGenerator {
    // Map of Entity -> 'name' of that entity in the database
    Map<InputEntity, String> inputEntityMap;
    Connection dbConnection;
    EntityConnection entityConnection;


    public EntityGenerator(Map<InputEntity, String> inputEntityMap, Connection dbConnection) {
        this.inputEntityMap = inputEntityMap;
        this.dbConnection = dbConnection;
        this.entityConnection = new EntityConnection(dbConnection);
    }

    /**
     * Modifies baseAspect using Connection to create a) entities in baseAspect that relate to IDs in the system, and
     * b) entities in the database that correspond to entities in the aspect
     *
     * @param baseAspect
     */

    public void updateAspect(FileAspect baseAspect) throws SQLException {

        //For <inputEntity> we have a list of table+id that entity could exist in.
        //For each table+id, if that is in the input entities list, we get the ID in the database if it exists
        //If it does not exist, we create the entity in the database
        //Either way, we update the aspect's ID column to match that ID.

        for(InputEntity entity:InputEntity.values()){
            DefaultInputEntity defaultEntity = InputEntityDefaults.get(entity);
            String entityValue;
            if(inputEntityMap.containsKey(entity)){
                entityValue = inputEntityMap.get(entity);
            }
            else{
                entityValue = defaultEntity.getDefaultValue();
            }

            Integer id = entityConnection.getEntityIdbyName(entity,entityValue);
            if(id==null){
                id = entityConnection.createEntitybyName(entity,entityValue,InputEntityDefaults);
            }
            defaultEntity.setValue = id;

            //Set ID in all constant fields referenced
            if(id==null){
                continue;
            }
            Map<String, TableAspect> tables = baseAspect.getAspects();
            for(TableEntry te:defaultEntity.tableEntryList){
                if(tables.containsKey(te.table)){
                    TableAspect aspect = tables.get(te.table);

                    ElementAspect element = aspect.getAspects().getOrDefault(te.element,null);
                    if(element instanceof ConstantAspect){ // != null is implicit here
                        ConstantAspect cAspect = (ConstantAspect)element;
                        cAspect.setConstant(Integer.toString(id));
                    }
                }
            }
        }


    }


    public enum InputEntity {
        Project, Platform, Experiment, Dataset/*, Germplasm_Species, Germplasm_Type -- No uses of these for now */
    }

    static HashMap<InputEntity, DefaultInputEntity> InputEntityDefaults = generateEntityDefaults();

    private static HashMap<InputEntity, DefaultInputEntity> generateEntityDefaults() {
        HashMap<InputEntity,DefaultInputEntity> ret = new HashMap<InputEntity,DefaultInputEntity>();
        ret.put(InputEntity.Project,new DefaultInputEntity(InputEntity.Project,
                Arrays.asList(new TableEntry("dnasample","project_id"),
                        new TableEntry("dnarun","project_id"))));

        ret.put(InputEntity.Platform,new DefaultInputEntity(InputEntity.Platform,
                Arrays.asList(new TableEntry("dataset_dnarun","platform_id"))));

        ret.put(InputEntity.Experiment,new DefaultInputEntity(InputEntity.Experiment,
                Arrays.asList(new TableEntry("dataset_dnarun","experiment_id"),
                        new TableEntry("dnarun","experiment_id"))));

        ret.put(InputEntity.Dataset,new DefaultInputEntity(InputEntity.Dataset, Arrays.asList(
                new TableEntry("dataset_dnarun","dataset_id"),
                new TableEntry("dataset_marker","dataset_id"))));
/*
        ret.put(InputEntity.Germplasm_Species,new DefaultInputEntity(InputEntity.Germplasm_Species, Arrays.asList(
               )));

        ret.put(InputEntity.Germplasm_Type,new DefaultInputEntity(InputEntity.Germplasm_Type, Arrays.asList(
                )));*/

        return ret;
    }

    private static class DefaultInputEntity {
        InputEntity entity;
        public List<TableEntry> tableEntryList;
        public Integer setValue;

        DefaultInputEntity(InputEntity entity, List<TableEntry> tableEntryList){
            this.entity=entity;
            this.tableEntryList = tableEntryList;
        }

        String getDefaultValue(){
            return entity.name() + " " + Math.round(Math.random()*100000);//Random 5 digit integer
        }
    }


    private static class TableEntry {
        String table,element;
        TableEntry(String table, String element){
            this.table=table;
            this.element=element;
    }
}


    private static class EntityConnection {
        Connection dbConn;
        EntityConnection(Connection dbConn){
            this.dbConn = dbConn;
        }
        Integer getEntityIdbyName(InputEntity entity, String name) throws SQLException {
            SimplePostgresConnector connector = new SimplePostgresConnector(dbConn);
            Integer ret = null;
            switch(entity){
                case Project:
                    ret = connector.getProjectId(name);
                    break;
                case Platform:
                    ret = connector.getPlatformId(name);
                    break;
                case Experiment:
                    ret = connector.getExperimentId(name);
                    break;
                case Dataset:
                    ret = connector.getDatasetId(name);
                default:
                    break;
            }


            return ret;
        }
        Integer createEntitybyName(InputEntity entity, String name, HashMap<InputEntity,DefaultInputEntity> defaultEntities) throws SQLException {
            Integer ret = null;
            String sqlCommand;
            SimplePostgresConnector connector = new SimplePostgresConnector(dbConn);
            switch(entity){
                case Project:
                    sqlCommand = "INSERT INTO project (name,pi_contact,status) VALUES ('" + name + "',1,57)";
                    connector.boolQuery(sqlCommand);
                    break;
                case Platform:
                    sqlCommand = "INSERT INTO platform (name,code,status) VALUES ('" + name + "','"+name+"',57)";
                    connector.boolQuery(sqlCommand);
                    break;
                case Experiment:
                    int project_id=1; //An experiment is tied to a project. Find out what project we're working with from the project entity request
                    if(defaultEntities.containsKey(InputEntity.Project) && defaultEntities.get(InputEntity.Project).setValue!=null){
                        project_id=defaultEntities.get(InputEntity.Project).setValue;
                    }
                    sqlCommand = "INSERT INTO experiment (name,code,project_id,status) VALUES ('" + name + "','"+name+"',"+project_id+",57)";
                    connector.boolQuery(sqlCommand);
                    break;
                case Dataset:
                    int experiment_id=1; //A dataset is tied to an experiment, find the experiment we're working with from the previous request
                    if(defaultEntities.containsKey(InputEntity.Experiment) && defaultEntities.get(InputEntity.Experiment).setValue!=null){
                        experiment_id=defaultEntities.get(InputEntity.Experiment).setValue;
                    }
                    Integer analysisId=connector.getAnalysisId("undefined");
                    if(analysisId==null){
                        Logger.logError("EntityGenerator","No undefined calling analysis in DB. Does this DB have default data?");
                    }
                    sqlCommand = "INSERT INTO dataset (experiment_id,callinganalysis_id,name) VALUES ("+experiment_id+","+analysisId+",'"+name+"')";
                    connector.boolQuery(sqlCommand);
                default:
                    break;
            }
            ret = getEntityIdbyName(entity,name);
            return ret;
        }
    }

    public Integer getValue(InputEntity entity){
        DefaultInputEntity defEntity = InputEntityDefaults.getOrDefault(entity,null);
        if(defEntity==null) return null;

        return defEntity.setValue;
    }

}
