package org.gobiiproject.gobiiprocess.digester.utils;

import lombok.Builder;
import org.gobii.masticator.aspects.FileAspect;
import org.gobiiproject.gobiiprocess.SimplePostgresConnector;
import org.gobiiproject.gobiiprocess.digester.EBSLoader;

import java.sql.Connection;
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


    public void updateAspect(FileAspect baseAspect) {
        for(InputEntity entity:InputEntity.values()){
            String entityValue;
            if(inputEntityMap.containsKey(entity)){
                entityValue = inputEntityMap.get(entity);
            }
            else{
                entityValue = InputEntityDefaults.get(entity).getDefaultValue();
            }

            Integer id = entityConnection.getEntityIdbyName(entity,entityValue);
            if(id==null){
                id = entityConnection.createEntitybyName(entity,entityValue);
            }

        }


        //For <inputEntity> we have a list of table+id that entity could exist in.
        //For each table+id, if that is in the input entities list, we get the ID in the database if it exists
        //If it does not exist, we create the entity in the database
        //Either way, we update the aspect's ID column to match that ID.
    }

    /**
     * Modifies baseAspect using Connection to create a) entities in baseAspect that relate to IDs in the system, and
     * b) entities in the database that correspond to entities in the aspect
     *
     * @param baseAspect
     */
    public void generateEntities(FileAspect baseAspect) {





    }

    public enum InputEntity {
        Project, Platform, Experiment, Dataset, Germplasm_Species, Germplasm_Type
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
                new TableEntry("dataset_dnarun","dataset_id"))));

        ret.put(InputEntity.Germplasm_Species,new DefaultInputEntity(InputEntity.Germplasm_Species, Arrays.asList(
                new TableEntry("dna_sample","project_id"))));

        ret.put(InputEntity.Germplasm_Type,new DefaultInputEntity(InputEntity.Germplasm_Type, Arrays.asList(
                new TableEntry("dna_sample","project_id"))));

        return ret;
    }

    private static class DefaultInputEntity {
        InputEntity entity;
        List<TableEntry> tableEntryList;

        DefaultInputEntity(InputEntity entity, List<TableEntry> tableEntryList){
            this.entity=entity;
            this.tableEntryList = tableEntryList;
        }

        String getDefaultValue(){
            return entity.name() + " " + Math.random();
        }
    }


    private static class TableEntry {
        String table,element;
        TableEntry(String table, String element){
            this.table=table;
            this.element=element;
    }
}


    private class EntityConnection {
        Connection dbConn;
        EntityConnection(Connection dbConn){
            this.dbConn = dbConn;
        }
        Integer getEntityIdbyName(InputEntity entity, String name){
            SimplePostgresConnector connector = new SimplePostgresConnector(dbConn);
            Integer ret = null;
            switch(entity){
                case Project:
                    connector.hasProject(name);
                    break;
                default:
                    break;
            }


            return null;
        }
        Integer createEntitybyName(InputEntity entity, String name){
            Integer ret = null;
            switch(entity){
                case Project:
                    String sqlCommand = "INSERT INTO PROJECT (name) VALUES (" + name + ");";
                    break;
                default:
                    break;
            }
            ret = getEntityIdbyName(entity,name);
            return ret;
        }
    }

}
