package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.Protocol;

import java.util.List;

public interface ProtocolDao {

    Protocol getProtocolById(Integer protocolId) throws GobiiDaoException;
    List<Protocol> getProtocols(Integer pageSize,
                                Integer pageOffset,
                                Integer platformId) throws GobiiDaoException;
    Protocol createProtocol(Protocol protocolToBeCreated) throws GobiiDaoException;
    Protocol patchProtocol(Protocol protcolToBePatched) throws GobiiDaoException;
    void deleteProtocol(Protocol protocol) throws GobiiDaoException;

}
