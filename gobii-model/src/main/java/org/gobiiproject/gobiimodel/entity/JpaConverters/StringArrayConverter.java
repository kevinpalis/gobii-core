package org.gobiiproject.gobiimodel.entity.JpaConverters;

import java.sql.Array;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.postgresql.jdbc.PgArray;

/**
 * JPA convertor class to convert Postgresql Database text array to string array and vice versa.
 */
@Converter
public class StringArrayConverter implements AttributeConverter<String[], Object> {

    /**
     * Converts json node to string.
     * @param stringArray - String array which needs to be persisited
     * @return
     */
    @Override
    public Object convertToDatabaseColumn(String[] stringArray) {
        return stringArray;
    }

    /**
     * Converts json string to string array.
     * @param  - Postgres text array object.
     * @return
     */
    @Override
    public String[] convertToEntityAttribute(Object pgObject) {

        String[] stringArray = null;

        if(pgObject instanceof String[])
            return (String[])pgObject;

        try {

            Array databaseArray = ((PgArray) pgObject);

            if(pgObject != null) {

                stringArray = (String[]) databaseArray.getArray();

            }

            return stringArray;
        }
        catch(Exception e) {
            throw new GobiiException("Conversion of text[] database column to string array in failed");
        }

    }

}
