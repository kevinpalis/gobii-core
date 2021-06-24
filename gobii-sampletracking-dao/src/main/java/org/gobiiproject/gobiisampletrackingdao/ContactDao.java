/**
 * ContactDao.java
 * 
 * Contact DAO object for contact data.
 * @author RNDuldulao, Jr.
 * @since 2020-03-23
 */

package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;

import org.gobiiproject.gobiimodel.entity.BaseEntity;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Organization;

public interface ContactDao {

    List<Contact> getContacts(Integer page, Integer pageSize, Integer organizationId);
    Contact getContact(Integer contactId) throws Exception;
    Contact getContactByUsername(String username) throws GobiiDaoException;
    Contact addContact(Contact contact);
    Contact addContact(String username, String lastName, String firstName, String email, Organization organization, String createdBy) throws Exception;

    Contact updateContact(Contact contact) throws Exception;
    
    void stampCreated(BaseEntity auditable, String userid) throws Exception;

    void stampModified(BaseEntity auditable, String userid) throws Exception;

 }