package org.gobiiproject.gobiisampletrackingdao.spworkers;


import com.fasterxml.jackson.databind.JsonNode;
import org.gobiiproject.gobiisampletrackingdao.GobiiDaoException;
import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Phil on 4/18/2016.
 */
@SuppressWarnings("unused")
public class SpWorker implements Work {

    Logger LOGGER = LoggerFactory.getLogger(SpWorker.class);

    public SpWorker() {}

    public SpWorker(EntityManager entityManager) {
        this.em = entityManager;
    }

    @PersistenceContext
    protected EntityManager em;

    private SpDef spDef = null;
    private Map<String, Object> paramVals = null; //TODO: remove this?

    Integer result = null;

    public Integer getResult() {
        return result;
    }

    public void run(SpDef spDef) throws SQLGrammarException {

        try {

            this.spDef = spDef;

            Session session = (Session) em.getDelegate();

            session.doWork(this);

        }
        finally {
            this.spDef = null;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(Connection connection) throws SQLException, GobiiDaoException {

        CallableStatement callableStatement = connection.prepareCall(spDef.getCallString());

        List<SpParamDef> paramDefs = spDef.getSpParamDefs();

        for (SpParamDef currentParamDef : paramDefs) {

            Integer currentParamIndex = currentParamDef.getOrderIdx();

            Type currentParamType = currentParamDef.getParamType();

            Object currentParamValue = currentParamDef.getCurrentValue();

            try {
                if (currentParamType.equals(String.class)) {
                    if (null != currentParamValue) {
                        callableStatement.setString(currentParamIndex, (String) currentParamValue);
                    } else {
                        callableStatement.setNull(currentParamIndex, Types.VARCHAR);
                    }
                } else if (currentParamType.equals(Integer.class)) {
                    if (null != currentParamValue) {
                        callableStatement.setInt(currentParamIndex, (Integer) currentParamValue);
                    } else {
                        callableStatement.setNull(currentParamIndex, Types.INTEGER);
                    }
                } else if (currentParamType.equals(Date.class)) {
                    if (null != currentParamValue) {

                        Date javaDateValue = (Date) currentParamValue;
                        LocalDate localDate = javaDateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
                        callableStatement.setDate(currentParamIndex, sqlDate);
                    } else {
                        callableStatement.setNull(currentParamIndex, Types.DATE);
                    }
                } else if (currentParamType.equals(ArrayList.class)) {
                    if (null != currentParamValue) {
                        List<Integer> list = (List<Integer>) currentParamValue;
                        Integer[] intArray = new Integer[list.size()];
                        intArray = list.toArray(intArray);
                        Array sqlArray = connection.createArrayOf("integer", intArray);
                        callableStatement.setArray(currentParamIndex, sqlArray);
                    } else {
                        callableStatement.setNull(currentParamIndex, Types.ARRAY);
                    }
                }
                else if (currentParamType.equals(JsonNode.class)) {

                    if(null != currentParamValue) {

                        String currentParamJsonString = currentParamValue.toString();

                        callableStatement.setString(currentParamIndex, (String) currentParamJsonString);

                    } else {
                        callableStatement.setNull(currentParamIndex, Types.VARCHAR);
                    }
                }
                else {
                    throw new SQLException("Unsupported param type: " + Type.class.toString());
                }

            }
            catch (Exception e) {
                String message = "Error executing stored procedure " + spDef.getCallString() + " with " +
                        "Param Value: " + currentParamValue + "; " +
                        "Param Type: " + currentParamType.toString() + ": " +
                        "Reported Exception: " +e.getMessage();
                throw new GobiiDaoException(message);
            }
        }


        Integer resultOutParamIdx = paramDefs.size();

        if (spDef.isReturnsKey()) {
            callableStatement.registerOutParameter(resultOutParamIdx, Types.INTEGER);
        }

        callableStatement.executeUpdate();

        if (spDef.isReturnsKey()) {
            result = callableStatement.getInt(resultOutParamIdx);
        }


    } // execute
}
