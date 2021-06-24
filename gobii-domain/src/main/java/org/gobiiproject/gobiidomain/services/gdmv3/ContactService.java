/**
 * ContactService.java
 * 
 * Interface for GDM V3 Contact service
 */

package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.ContactDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public interface ContactService {

    PagedResult<ContactDTO> getContacts(Integer page, Integer pageSize, Integer organizationId) throws Exception;

    //this is to differentiate from getContacts, getUsers should get from keycloak
    PagedResult<ContactDTO> getUsers(String cropType, 
                                     String role, 
                                     Integer page, 
                                     Integer pageSize,
                                     String userName) throws Exception;
    
    ContactDTO addContact(String preferredUsername, String givenName, String familyName, String email, String organization, String createdBy) throws Exception;

    ContactDTO getCurrentUser() throws GobiiException;

    static String getCurrentUserName() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(auth -> auth.getName())
            .orElse(null);
    }
 }