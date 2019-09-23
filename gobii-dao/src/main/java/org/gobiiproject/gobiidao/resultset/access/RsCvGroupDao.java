package org.gobiiproject.gobiidao.resultset.access;

import java.sql.ResultSet;
import org.gobiiproject.gobiidao.GobiiDaoException;

/**
 * Created by VCalaminos on 2/1/2017.
 */
public interface RsCvGroupDao {

    ResultSet getCvGroupById(Integer groupId) throws GobiiDaoException;
    ResultSet getCvsByGroupId(Integer groupId) throws GobiiDaoException;
    ResultSet getGroupTypeForGroupId(Integer groupId) throws GobiiDaoException;
    ResultSet getUserCvGroupByName(String groupName) throws GobiiDaoException;
    ResultSet getCvGroupsForType(Integer groupType) throws GobiiDaoException;
    ResultSet getCvGroupDetailsByGroupName(String groupName, Integer cvGroupTypeId) throws GobiiDaoException;
}
