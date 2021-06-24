package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.gdmv3.ProtocolDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface ProtocolService {
    ProtocolDTO getProtocolById(Integer protocolId) throws GobiiDomainException;
    PagedResult<ProtocolDTO> getProtocols(Integer pageSize, Integer pageNum,
                                          Integer platformId);
    ProtocolDTO createProtocol(ProtocolDTO protocolDTO);
    ProtocolDTO patchProtocol(Integer protocolId, ProtocolDTO protocolDTO);
    void deleteProtocol(Integer protocolId);
}
