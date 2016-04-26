package org.gobiiproject.gobiidao.resultset.access;

import org.gobiiproject.gobiidao.GobiiDaoException;

import java.sql.ResultSet;

/**
 * Created by Phil on 4/7/2016.
 */
public interface RsMapSetDao {


    ResultSet getMapNames() throws GobiiDaoException;
//    ResultSet getContactsForRoleName(String roleName) throws GobiiDaoException;

	ResultSet getMapNamesByTypeId(int typeId) throws GobiiDaoException;
}