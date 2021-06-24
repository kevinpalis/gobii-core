/**
 * JsonNodeType.java
 * 
 * UserType for JsonNode (generic Json data)
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-13
 */
package org.gobiiproject.gobiimodel.entity.pgsql;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonNodeType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[] { Types.JAVA_OBJECT };
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class returnedClass() {
        return java.util.Map.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return false;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return 0;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        final String cellContent = rs.getString(names[0]);
        if (cellContent == null) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode jsonNode = objectMapper.readTree(cellContent);
            return jsonNode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        String jsonString = "{}";
        if (value != null) {
            try {
                JsonNode props = (JsonNode) value;
                ObjectMapper objectMapper = new ObjectMapper();
                StringWriter stringWriter = new StringWriter();
                objectMapper.writeValue(stringWriter, props);
                stringWriter.flush();
                jsonString = stringWriter.toString();
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  
        }
        
        log.debug("Setting " + jsonString + " to index " + index);
        st.setObject(index, jsonString, Types.OTHER);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (value instanceof ObjectNode) {
            ObjectNode objectNode = (ObjectNode) value;
            return objectNode.deepCopy();
        }
        try {
            // use serialization to create a deep copy
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(value);
            oos.flush();
            oos.close();
            bos.close();
             
            ByteArrayInputStream bais = new ByteArrayInputStream(bos.toByteArray());
            Object obj = new ObjectInputStream(bais).readObject();
            bais.close();
            return obj;
        } catch (ClassNotFoundException | IOException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return null;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return null;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
    
}