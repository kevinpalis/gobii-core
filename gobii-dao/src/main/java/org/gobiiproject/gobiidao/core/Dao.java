package org.gobiiproject.gobiidao.core;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Phil on 3/29/2016.
 */
public interface Dao <T> {
    /**
     * Method that returns the number of entries from a table that meet some
     * criteria (where clause params)
     *
     * @param params
     *            sql parameters
     * @return the number of records meeting the criteria
     */

//    long countAll(Map<String, Object> params);

    T create(T t);

    void delete(Object id);

    T find(Object id);

    T update(T t);
}