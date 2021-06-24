package org.gobiiproject.gobiidomain.services.gdmv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.ContactDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Organization;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.OrganizationDao;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private KeycloakService keycloakService;

    @Transactional
    @Override
    public PagedResult<ContactDTO> getContacts(Integer page, Integer pageSize, Integer organizationId)
            throws Exception {
        try {
            Objects.requireNonNull(page);
            Objects.requireNonNull(pageSize);
            List<ContactDTO> contactDTOs = new ArrayList<>();

            List<Contact> contacts = contactDao.getContacts(page, pageSize, organizationId);
            contacts.forEach(contact -> {
                ContactDTO contactDTO = getContactDTO(contact);
                contactDTOs.add(contactDTO);
            });
            return PagedResult.createFrom(page, contactDTOs);
        } catch (GobiiException gE) {
            throw gE;
        } catch (Exception e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }

    @Transactional
    @Override
    public ContactDTO addContact(String preferredUsername, String givenName, String familyName, String email,
            String orgName, String createdBy) throws Exception {
        Contact contact = contactDao.getContactByUsername(preferredUsername);
       
        if (contact == null) {
            // create a new one
            contact = new Contact();
            contact.setLastName(familyName);
            contact.setFirstName(givenName);
            contact.setEmail(email);
            contact.setUsername(preferredUsername);
            contact.setCode(String.format("contact_keycloak_%s", preferredUsername));
            contactDao.stampCreated(contact, createdBy);

            // organization
            if (orgName != null) {
                Organization organization = Optional.ofNullable(organizationDao.getOrganizationByName(orgName))
                        .orElseGet(() -> this.createOrganization(orgName));
                contact.setOrganization(organization);
            }


            contactDao.addContact(contact);

        }

        //detect email is still the same
        else  {
            if (!contact.getEmail().equals(email)) {
                //update email
                contact.setEmail(email);
                contactDao.updateContact(contact);
            }
        }

        return getContactDTO(contact);
    } 

    private Organization createOrganization(String orgName)  {
        try {
            Organization org = new Organization();
            org.setName(orgName);
            organizationDao.createOrganization(org);
            return org;
        } catch (Exception e) {
            log.error("Cannot create organization", e);
            return null;
        }

    }

    private ContactDTO getContactDTO(Contact contact) {
        ContactDTO contactDTO = new ContactDTO();
        ModelMapper.mapEntityToDto(contact, contactDTO);
        return contactDTO;
    }

    public ContactDTO getCurrentUser() throws GobiiException {
        ContactDTO contactDTO = new ContactDTO();
        try {
            String userName = ContactService.getCurrentUserName();
            Contact contact = contactDao.getContactByUsername(userName);
            ModelMapper.mapEntityToDto(contact, contactDTO);
            return contactDTO;
        } catch (Exception e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
}

    @Override
    public PagedResult<ContactDTO> getUsers(String cropType, 
                                            String role, 
                                            Integer page, 
                                            Integer pageSize,
                                            String userName) throws Exception {
            List<ContactDTO> contactDTOs = new ArrayList<>();

            // If user name filter is provided dont have to fetch all
            if(userName != null && !userName.isBlank()) {
                ContactDTO contactDTO = keycloakService.getUserByUserName(userName);
                contactDTOs.add(contactDTO);
            }
            else {
                contactDTOs = keycloakService.getKeycloakUsers(cropType, role, page, pageSize); 
            }
            return PagedResult.createFrom(page, contactDTOs);
    }


    
  

}