package org.gobiiproject.gobiidomain.services.gdmv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.EntityDoesNotExistException;
import org.gobiiproject.gobiimodel.dto.gdmv3.OrganizationDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Organization;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.OrganizationDao;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    OrganizationDao organizationDao;
    

    @Autowired
    CvDao cvDao;

    @Autowired
    ContactDao contactDao;
    

    @Transactional
    @Override
    public PagedResult<OrganizationDTO> getOrganizations(Integer page, Integer pageSizetoUse) throws Exception {
        Integer offset = page * pageSizetoUse;
        List<Organization> orgList = organizationDao.getOrganizations(offset, pageSizetoUse);
        List<OrganizationDTO> orgDTOs = new ArrayList<>();

        orgList.forEach(organization -> {
            OrganizationDTO organizationDTO = new OrganizationDTO();
            ModelMapper.mapEntityToDto(organization, organizationDTO);
            orgDTOs.add(organizationDTO);
        });

        return PagedResult.createFrom(page, orgDTOs);   
    }

    @Transactional
    @Override
    public OrganizationDTO getOrganization(Integer organizationId) throws Exception {
        Organization organization = this.loadOrganization(organizationId);
        OrganizationDTO organizationDTO = new OrganizationDTO();
        ModelMapper.mapEntityToDto(organization, organizationDTO);
        return organizationDTO;
    }

    @Transactional
    @Override
    public OrganizationDTO createOrganization(OrganizationDTO request, String createdBy) throws Exception {
        log.debug("Creating new organization entry: %s", request.getOrganizationName());
        Organization organization = new Organization();
        organization.setName(request.getOrganizationName());
        organization.setAddress(request.getOrganizationAddress());
        organization.setWebsite(request.getOrganizationWebsite());

         // Get the Cv for status, new row
        Cv cv = cvDao.getNewStatus();
        organization.setStatus(cv);

        //audit
        Contact creator = contactDao.getContactByUsername(createdBy);
        organization.setCreatedBy(Optional.ofNullable(creator).map( c -> c.getContactId()).orElse(null));
        organization.setCreatedDate(new java.util.Date());
        organization = organizationDao.createOrganization(organization);

        OrganizationDTO createdOrganization = new OrganizationDTO();
        ModelMapper.mapEntityToDto(organization, createdOrganization);
        return createdOrganization;
    }

    @Transactional
    @Override
    public OrganizationDTO updateOrganization(Integer organizationId, OrganizationDTO request, String updatedBy) throws Exception {
        Organization organization = this.loadOrganization(organizationId);
        if (request.getOrganizationName() != null) {
            organization.setName(request.getOrganizationName());
        }

        if (request.getOrganizationAddress() != null) {
            organization.setAddress(request.getOrganizationAddress());
        }

        if (request.getOrganizationWebsite() != null) {
            organization.setWebsite(request.getOrganizationWebsite());
        }
        //audit items
        //audit
        Contact creator = contactDao.getContactByUsername(updatedBy);
        organization.setModifiedBy(Optional.ofNullable(creator).map( value -> value.getContactId()).orElse(null));
        organization.setModifiedDate(new java.util.Date());

        organization = organizationDao.updateOrganization(organization);
        OrganizationDTO organizationDTO = new OrganizationDTO();
        ModelMapper.mapEntityToDto(organization, organizationDTO);
        return organizationDTO;

    }

    @Transactional
    @Override
    public void deleteOrganization(Integer organizationId) throws Exception {
        Organization organization = this.loadOrganization(organizationId);
        organizationDao.deleteOrganization(organization);
    }

    private Organization loadOrganization(Integer organizationId) throws Exception {
        Organization organization = organizationDao.getOrganization(organizationId);
        if (organization == null) {
            throw new EntityDoesNotExistException("organization");
        }
        return organization;
    }
    
}