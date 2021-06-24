package org.gobiiproject.gobiimodel.modelmapper;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.*;

public class ModelMapper {

    private static List<Field> getAllDeclaredFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        return getAllDeclaredFields(fields, type);
    }

    /**
     * stackoverflow.com/questions/1042798
     * @param fields - Fields of a given model
     * @param type - type of the fileds
     * @return - List of mapped fields.
     */
    private static List<Field> getAllDeclaredFields(List<Field> fields, Class<?> type) {

        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if(type.getSuperclass() != null) {
            getAllDeclaredFields(fields, type.getSuperclass());
        }

        return fields;
    }

    private static Field getDeclaredField(String fieldName, Class<?> type) {
        try {
            return type.getDeclaredField(fieldName);
        }
        catch(NoSuchFieldException noFiEx) {
            if(type.getSuperclass() != null) {
                return getDeclaredField(fieldName, type.getSuperclass());
            }
            return null;
        }
    }

    private static void mapper(Object entityInstance,
                               Object dtoInstance,
                               boolean dtoToEntity,
                               boolean ignoreNull) {

        try {

            List<Field> allFields = getAllDeclaredFields(dtoInstance.getClass());

            for (Field dtoField : allFields) {
                if (dtoField.isAnnotationPresent(GobiiEntityMap.class)) {

                    GobiiEntityMap[] entityMaps =
                        dtoField.getAnnotationsByType(GobiiEntityMap.class);
                    dtoField.setAccessible(true);

                    for (GobiiEntityMap entityMap : entityMaps) {
                        if (dtoToEntity && entityMap.ignoreOnDtoToEntity()) continue;
                        if (entityMap.entity().equals(entityInstance.getClass()) ||
                            entityMap.base() == true) {

                            String dtoFieldName = dtoField.getName();

                            Object entityToSetOrGet = entityInstance;

                            String entityFieldName = entityMap.paramName();

                            Field entityField = getDeclaredField(
                                entityFieldName, entityInstance.getClass());

                            if(entityMap.deep()) {
                                //escape regular expression dot
                                String[] deepParams = entityFieldName.split("\\.");

                                entityField = getDeclaredField(
                                    deepParams[0],
                                    entityToSetOrGet.getClass());

                                for(int i = 1; i < deepParams.length; i++) {

                                    if(entityField == null) {
                                        break;
                                    }

                                    entityField.setAccessible(true);
                                    Object entityFieldValue =
                                        entityField.get(entityToSetOrGet);

                                    // Instantiate entity value when
                                    // dto value is not null
                                    if(entityFieldValue == null &&
                                        dtoToEntity &&
                                        dtoField.get(dtoInstance) != null) {

                                        Object emptyFieldValue =
                                            Class
                                                .forName(entityField.getType()
                                                .getName())
                                                .getConstructor()
                                                .newInstance();

                                        entityField.set(
                                            entityToSetOrGet,
                                            emptyFieldValue);

                                        entityToSetOrGet = emptyFieldValue;
                                    }
                                    else {
                                        entityToSetOrGet = entityFieldValue;
                                        if(entityToSetOrGet == null) {
                                            break;
                                        }
                                    }
                                    entityField = getDeclaredField(deepParams[i],
                                        entityToSetOrGet.getClass());

                                }
                            }


                            if(entityField == null || entityToSetOrGet == null) {
                                continue;
                            }

                            entityField.setAccessible(true);

                            if(!entityField.getType().equals(dtoField.getType())) {
                                LoggerFactory.getLogger(ModelMapper.class).error(
                                    "Unable to map DTO to Entity: DTO field " + dtoFieldName +
                                        " of type " + dtoField.getType().toString() +
                                        " is not mappable to Entity field" + entityFieldName +
                                        " of type " + entityField.getType().toString());

                                throw new GobiiException(
                                    GobiiStatusLevel.ERROR,
                                    GobiiValidationStatusType.UNKNOWN,
                                    "Unable to map DTO to Entity");

                            }
                            if(dtoToEntity) {
                                Object value = dtoField.get(dtoInstance);
                                if (ignoreNull && value == null) continue;
                                entityField.set(entityToSetOrGet, value);
                            }
                            else {
                                dtoField.set(dtoInstance, entityField.get(entityToSetOrGet));
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            LoggerFactory.getLogger(ModelMapper.class).error(e.getMessage());
            throw new GobiiException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                "Unable to map DTO to Entity");
        }
    }

    public static void mapDtoToEntity(Object dtoInstance,
                                      Object entityInstance,
                                      boolean ignoreNull) throws  GobiiException {
        ModelMapper.mapper(entityInstance, dtoInstance, true, ignoreNull);
    }

    public static void mapDtoToEntity(Object dtoInstance,
                                      Object entityInstance) throws  GobiiException {
        ModelMapper.mapper(entityInstance, dtoInstance, true, false);
    }

    public static void mapEntityToDto(Object entityInstance,
                                      Object dtoInstance) throws GobiiException {
        ModelMapper.mapper(entityInstance, dtoInstance, false, false);
    }

    @SuppressWarnings("all")
    private static void getDtoEntityMap(Class dtoClassName,
                                        Map<String, EntityFieldBean> returnVal,
                                        String prefix) {

        try {

            for(Field field : dtoClassName.getDeclaredFields()) {
                if(field.isAnnotationPresent(GobiiEntityMap.class)) {
                    GobiiEntityMap gobiiEntityMap = field.getAnnotation(GobiiEntityMap.class);
                    String entityParamName = gobiiEntityMap.paramName();
                    Class entityClass = gobiiEntityMap.entity();

                    if(entityParamName != null & entityClass != void.class) {

                        Field entityField;
                        String tableName = null;

                        if(entityClass.isAnnotationPresent(Table.class)) {
                            Table table = (Table) entityClass.getAnnotation(Table.class);
                            tableName = table.name();
                        }

                        try {
                            entityField = entityClass.getDeclaredField(entityParamName);
                        }
                        catch(NoSuchFieldException noFiEx) {
                            continue;
                        }

                        if(entityField.isAnnotationPresent(Column.class)) {

                            Column jpaEntity = entityField.getAnnotation(Column.class);
                            String dbColumnName = jpaEntity.name();
                            EntityFieldBean entityFieldBean = new EntityFieldBean();
                            entityFieldBean.setColumnName(dbColumnName);
                            entityFieldBean.setTableName(tableName);

                            if(prefix.isEmpty()) {
                                returnVal.put(field.getName(), entityFieldBean);
                            }
                            else {
                                returnVal.put(prefix + "." + field.getName(), entityFieldBean);
                            }
                        }
                    }
                }
                else if(
                    field
                        .getType()
                        .getName()
                        .startsWith("org.gobiiproject.gobiimodel.dto")) {
                   getDtoEntityMap(field.getType(), returnVal, field.getName());
                }
                else {
                    if(prefix.isEmpty()) {
                        returnVal.put(field.getName(), null);
                    }
                    else {
                        returnVal.put(prefix + "." + field.getName(), null);
                    }
                }
            }
        }
        catch(Exception e) {

            LoggerFactory.getLogger(ModelMapper.class).error(e.getMessage());

            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    "Unable to map DTO to Entity");
        }



    }

    @SuppressWarnings("rawtypes")
    public static Map<String, EntityFieldBean> getDtoEntityMap(Class dtoClassName) {

        Map<String, EntityFieldBean> returnVal = new HashMap<>();

        getDtoEntityMap(dtoClassName, returnVal, "");

        return returnVal;

    }

}
