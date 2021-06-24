package org.gobiiproject.gobiidao.resultset.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Phil on 4/18/2016.
 */
public class ResultColumnApplicator {

    private static Logger LOGGER = LoggerFactory.getLogger(ResultColumnApplicator.class);

    @SuppressWarnings("unchecked")
    public static void applyColumnValues(ResultSet resultSet, Object dtoInstance) throws GobiiDaoException {

        //Map<String, Object> returnVal = new HashMap<>();
        String currentColumnName = null;
        String currentParameterName = null;
        Type currentColumnType = null;
        try {
            for (Method currentMethod : dtoInstance.getClass().getMethods()) {

                GobiiEntityColumn gobiiEntityColumn = currentMethod.getAnnotation(GobiiEntityColumn.class);

                if (null != gobiiEntityColumn) {

                    currentColumnName = gobiiEntityColumn.columnName();
                    Type[] methodParameterTypes = currentMethod.getParameterTypes();
                    if (methodParameterTypes.length == 1) {

                        currentColumnType = methodParameterTypes[0];

                        currentParameterName = currentMethod.getParameters()[0].getName();

                        Object currentColumnValue = null;

                        try {

                            currentColumnValue = resultSet.getObject(currentColumnName);
                        }
                        catch(SQLException sqlE) {
                            String message = sqlE.getMessage();

                            if (null != sqlE.getCause()) {
                                message += " caused by: " + sqlE.getCause();
                            }

                            LOGGER.error(message);

                           continue;
                        }

                        if (currentColumnType.equals(String.class)) {

                            String currentStringValue = (String) currentColumnValue;
                            currentMethod.invoke(dtoInstance, currentStringValue);

                        } else if (currentColumnType.equals(Integer.class)) {

                            Integer currentIntegerValue = (Integer) currentColumnValue;
                            currentMethod.invoke(dtoInstance, currentIntegerValue);

                        } else if (currentColumnType.equals(Long.class)) {

                            Long currentLongValue = (Long) currentColumnValue;
                            currentMethod.invoke(dtoInstance, currentLongValue);

                        } else if (currentColumnType.equals(BigDecimal.class)) {

                            BigDecimal currentBigDecimalValue = (BigDecimal) currentColumnValue;
                            currentMethod.invoke(dtoInstance, currentBigDecimalValue);
                        }
                        else if (currentColumnType.equals(Date.class)) {

                            //Date currentDateValue = (Date) currentColumnValue;
                            Timestamp timestamp = resultSet.getTimestamp(currentColumnName);
                            java.util.Date currentDateValue = null;
                            if( timestamp != null ) {
                              currentDateValue = new java.util.Date(timestamp.getTime());
                            }
                            currentMethod.invoke(dtoInstance, currentDateValue);

                        } else if (currentColumnType.equals(List.class)) {

                            List<Integer> intList = new ArrayList<>();
                            Array sqlArray = resultSet.getArray(currentColumnName);

                            if (null != sqlArray) {

                                Integer[] integerList = (Integer[]) sqlArray.getArray();
                                for (int idx = 0; idx < integerList.length; idx++) {
                                    Integer currentIntValue = integerList[idx];
                                    intList.add(currentIntValue);

                                } // iterate values
                            }

                            currentMethod.invoke(dtoInstance, intList);

                        } else if(currentColumnType.equals(Map.class)) {
                            ObjectMapper mapper = new ObjectMapper();
                            Map<String, Object> map = new HashMap<>();
                            if(resultSet.getString(currentColumnName) != null) {
                                map = mapper.readValue(resultSet.getString(currentColumnName), HashMap.class);
                            }
                            currentMethod.invoke(dtoInstance, map);
                        } else {
                            throw new SQLException("Unsupported param type for method " + currentMethod.getName() + ", parameter " + currentParameterName + ": " + currentColumnType);
                        }


                    } else {
                        throw new GobiiDaoException("Annotated setter method " + currentMethod.getName() + " does not have exactly one parameter");
                    }

                }  // if the field is annotated as a column

            } // iterate all fields

        } catch (Exception e) {

            String message = "error applying value of column "
                    + currentColumnName
                    + " to setter labeleled as "
                    + currentParameterName
                    + " in class  "
                    + dtoInstance.getClass()
                    + ": "
                    + e.getMessage();

            if (null != e.getCause()) {
                message += " caused by: " + e.getCause();
            }

            LOGGER.error(message, e);

            throw new GobiiDaoException(message);

        }

    } // applyColumnValues()

} // ParamExtractor

