package org.gobiiproject.gobiidtomapping.entity.auditable;

import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.auditable.OrganizationDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ProtocolDTO;
import org.gobiiproject.gobiimodel.dto.children.VendorProtocolDTO;

/**
 * Created by VCalaminos on 2016-12-14.
 */
public interface DtoMapProtocol extends DtoMap<ProtocolDTO> {

    ProtocolDTO create(ProtocolDTO protocolDTO) throws GobiiDtoMappingException;
    ProtocolDTO replace(Integer protocolId, ProtocolDTO protocolDTO) throws GobiiDtoMappingException;
    ProtocolDTO get(Integer protocolId) throws GobiiDtoMappingException;
    List<ProtocolDTO> getList() throws GobiiDtoMappingException;

    OrganizationDTO addVendotrToProtocol(Integer protocolId, OrganizationDTO organizationDTO) throws GobiiDtoMappingException;
    OrganizationDTO getVendorForProtocolByName(String vendorProtocolName) throws GobiiDtoMappingException;    List<OrganizationDTO> getVendorsForProtocolByProtocolId(Integer protocolId) throws GobiiDtoMappingException;
    void addVendorProtocolsToOrganization(OrganizationDTO organizationDTO)  throws GobiiException;
    void addVendorProtocolsToProtocol(ProtocolDTO protocolDTO)  throws GobiiException;
    OrganizationDTO updateOrReplaceVendotrByProtocolId(Integer protocolId, OrganizationDTO organizationDTO) throws GobiiDtoMappingException;
    ProtocolDTO getProtocolsByExperimentId(Integer experimentId)  throws GobiiDtoMappingException;
    VendorProtocolDTO getVendorProtocolByVendorProtocolId(Integer vendorProtocolId) throws GobiiDtoMappingException;
}
