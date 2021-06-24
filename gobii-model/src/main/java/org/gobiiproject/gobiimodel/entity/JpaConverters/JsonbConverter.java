package org.gobiiproject.gobiimodel.entity.JpaConverters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.postgresql.util.PGobject;

/**
 * JPA convertor class to convert Postgresql Database json string to jackson Json Node and vice versa.
 */
@Converter
public class JsonbConverter implements AttributeConverter<JsonNode, Object> {

    /**
     * Converts json node to string.
     * @param jsonNode - Jackson Json Node
     * @return
     */
    @Override
    public Object convertToDatabaseColumn(JsonNode jsonNode) {

        String jsonString = "{}";

        if(jsonNode != null) {
            jsonString = jsonNode.toString();
        }

        try {

            PGobject jsonObject = new PGobject();

            jsonObject.setType("jsonb");

            jsonObject.setValue(jsonString);

            return jsonObject;
        }
        catch(Exception e) {
            throw new GobiiException("Conversion of jsonb database column failed");
        }

    }

    /**
     * Converts json string to jackson json node.
     * @param jsonObject - Postgres jsonb object.
     * @return
     */
    @Override
    public JsonNode convertToEntityAttribute(Object jsonObject) {

        try {


            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.createObjectNode();


            if(jsonObject != null) {

                String jsonString = ((PGobject) jsonObject).getValue();

                jsonNode = objectMapper.readTree(jsonString);
            }

            return  jsonNode;
        }
        catch(Exception e) {
            throw new GobiiException("Conversion of jsonb database column failed");
        }

    }

}
