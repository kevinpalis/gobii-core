package org.gobiiproject.gobiidao.resultset.core;


import org.gobiiproject.gobiidao.GobiiDaoException;
import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.jdbc.Work;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Type;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.gobiiproject.gobiidao.util.async.Promise;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Phil on 4/18/2016.
 */
public class SpRunnerCallable {

    Logger LOGGER = LoggerFactory.getLogger(SpRunnerCallable.class);

    public SpRunnerCallable() {}

    @PersistenceContext
    private EntityManager em;

    public Integer run(SpDef spDef, Map<String, Object> paramVals) throws SQLGrammarException {

        Promise<Integer> result = new Promise<>();

        Session session = null;

        try {

            // first do validation checking
            List<SpParamDef> paramDefs = spDef.getSpParamDefs();
            for (int idx = 0; idx < paramDefs.size(); idx++) {

                SpParamDef currentParamDef = paramDefs.get(idx);
                String currentParamName = currentParamDef.getParamName();

                if (paramVals.containsKey(currentParamName)) {

                    Object currentParamVal = paramVals.get(currentParamName);
                    if (null == currentParamVal && !currentParamDef.isNullable()) {
                        throw new GobiiDaoException("Parameter is not allowed to be null : " + currentParamName);
                    } else if (null != currentParamVal && !currentParamVal.getClass().equals(currentParamDef.getParamType())) {
                        throw new GobiiDaoException("Parameter " + currentParamName + " should be of type " + currentParamDef.getParamType() + "; ");
                    } else {
                        if (null != currentParamVal) {
                            currentParamDef.setCurrentValue(currentParamVal);
                        } else {
                            currentParamDef.setCurrentValue(currentParamDef.getDefaultValue());
                        }
                    }

                } else {

                    String message = "There is no value param entry for parameter " + currentParamName + ";";
                    LOGGER.error(message);
                    throw new GobiiDaoException(message);
                }

            } // iterate param defs

            session = em.getEntityManagerFactory().unwrap(SessionFactory.class).openSession();
            session.doWork(createWorkFunction(spDef, result));
        } finally {

            if (session != null && session.isConnected()) {
                session.close();
            }
        }

        return result.get();

    } // run()

    @SuppressWarnings("unchecked")
    private Work createWorkFunction(final SpDef spDef, final Promise<Integer> promise)  {

        return connection -> {
            CallableStatement callableStatement = connection.prepareCall(spDef.getCallString());

            List<SpParamDef> paramDefs = spDef.getSpParamDefs();

            for (SpParamDef currentParamDef : paramDefs) {

                Integer currentParamIndex = currentParamDef.getOrderIdx();
                String currentParamName = currentParamDef.getParamName();
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
                    } else {
                        throw new SQLException("Unsupported param type: " + Type.class.toString());
                    }

                } catch (Exception e) {
                    String message = "Error executing stored procedure " + spDef.getCallString() + " with " +
                            "Param Name: " + currentParamName + "; " +
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
                promise.set(callableStatement.getInt(resultOutParamIdx));
            } else {
                promise.set(null);
            }
        };

    } // execute
}
