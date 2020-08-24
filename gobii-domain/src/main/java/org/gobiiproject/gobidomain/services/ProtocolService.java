package org.gobiiproject.gobidomain.services;

import java.util.List;
import org.gobiiproject.gobidomain.GobiiDomainException;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiimodel.dto.auditable.OrganizationDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ProtocolDTO;

/**
 * Created by VCalaminos on 2016-12-14.
 */
public interface ProtocolService {


    ProtocolDTO createProtocol(ProtocolDTO protocolDTO) throws GobiiDomainException;

    ProtocolDTO replaceProtocol(Integer protocolId, ProtocolDTO protocolDTO) throws GobiiDomainException;

    ProtocolDTO getProtocolById(Integer protocolId) throws GobiiDomainException;

    List<ProtocolDTO> getProtocols() throws GobiiDomainException;

    OrganizationDTO addVendotrToProtocol(Integer protocolId, OrganizationDTO organizationDTO) throws GobiiDomainException;

    OrganizationDTO updateOrReplaceVendotrToProtocol(Integer protocolId, OrganizationDTO organizationDTO) throws GobiiDomainException;

    List<OrganizationDTO> getVendorsForProtocolByProtocolId(Integer protocolId) throws GobiiDaoException;

    ProtocolDTO getProtocolsByExperimentId(Integer experimentId) throws GobiiDomainException;
}
