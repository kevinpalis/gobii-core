package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;

import org.gobiiproject.gobiimodel.entity.Organization;

public interface OrganizationDao {

    public List<Organization> getOrganizations(Integer offset, Integer pageSize) throws Exception;

	public Organization getOrganization(Integer organizationId) throws Exception;

	public Organization createOrganization(Organization organization) throws Exception;

	public Organization updateOrganization(Organization organization) throws Exception;

	public void deleteOrganization(Organization organization) throws Exception;

	public Organization getOrganizationByName(String name);
    
}