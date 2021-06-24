package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.dto.gdmv3.OrganizationDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface OrganizationService {

	public PagedResult<OrganizationDTO> getOrganizations(Integer page, Integer pageSizetoUse) throws Exception;

	public OrganizationDTO getOrganization(Integer organizationId) throws Exception;

	public OrganizationDTO createOrganization(OrganizationDTO request, String createdBy) throws Exception;

    public OrganizationDTO updateOrganization(Integer organizationId, OrganizationDTO request, String updatedBy) throws Exception;
    
    public void deleteOrganization(Integer organizationId) throws Exception;
    
}