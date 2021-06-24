package org.gobiiproject.gobiimodel.modelmapper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectMap;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectMaps;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.*;

public class AspectMapper {

    private static List<Field> getAllDeclaredFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        return getAllDeclaredFields(fields, type);
    }

    /**
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

    public static boolean mapTemplateToAspects(Object templateInstance,
                                               Object aspectTableInstance,
                                               Map<String, Object> valuesToSet
    ) throws GobiiException {

        boolean mapped = false;

        try {
            // Get all declared fields in template object
            List<Field> allFields = getAllDeclaredFields(templateInstance.getClass());

            for (Field templateField : allFields) {
                if (templateField.isAnnotationPresent(GobiiAspectMaps.class)) {

                    GobiiAspectMaps aspectMaps = templateField.getAnnotation(GobiiAspectMaps.class);
                    for (GobiiAspectMap aspectMap : aspectMaps.maps()) {

                        // Ignore Aspect maps whose aspect table not same as aspectTableInstance
                        if(!aspectMap.aspectTable().equals(aspectTableInstance.getClass())) {
                            continue;
                        }

                        String templateFieldName = templateField.getName();

                        // If value is not defined, ignore the field
                        if(!valuesToSet.containsKey(templateFieldName) ||
                            ObjectUtils.isEmpty(valuesToSet.get(templateFieldName))) {
                            continue;
                        }

                        String aspectFieldName = aspectMap.paramName();
                        if(StringUtils.isEmpty(aspectFieldName)) {
                            aspectFieldName = templateFieldName;
                        }
                        Field aspectField =
                            getDeclaredField(aspectFieldName, aspectTableInstance.getClass());
                        if (aspectField == null) {
                            throw new GobiiException(String.format(
                                "Invalid Aspect field mapping templateField->aspectField(%s->%s)",
                                templateFieldName,
                                aspectFieldName));
                        }
                        aspectField.setAccessible(true);
                        templateField.setAccessible(true);
                        if (templateField.get(templateInstance) != null) {
                            Class<?> templateValueClass =
                                valuesToSet.get(templateFieldName).getClass();
                            Class<?> aspectFieldClass = aspectField.getType();
                            if (aspectFieldClass.isAssignableFrom(templateValueClass)) {
                                aspectField.set(
                                    aspectTableInstance,
                                    valuesToSet.get(templateFieldName));
                                mapped = true;
                            } else {
                                throw new GobiiException(String.format(
                                    "Invalid Aspect field mapping: type mismatch, " +
                                        "templateField->aspectField(%s->%s)",
                                    templateFieldName,
                                    aspectFieldName));
                            }
                        }
                    }
                }
            }
        }
        catch(NullPointerException | IllegalAccessException e) {
            e.printStackTrace();
            LoggerFactory.getLogger(ModelMapper.class).error(e.getMessage());
            throw new GobiiException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                "Unable to map DTO to Entity");
        }
        return mapped;
    }

}
