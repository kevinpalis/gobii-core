package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.config.Roles;
import org.gobiiproject.gobiimodel.dto.gdmv3.ContactDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Organization;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.OrganizationDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class ContactServiceImplTest {

    @Mock
    private ContactDao contactDao;

    @Mock
    private OrganizationDao organizationDao;
    
    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private ContactServiceImpl contactServiceImpl;



    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetContactsOk() throws Exception {
        List<Contact> mockList = new ArrayList<>();
        mockList.add(new Contact());
        when(contactDao.getContacts(0, 1000, null)).thenReturn(mockList);

        PagedResult<ContactDTO> result = contactServiceImpl.getContacts(0, 1000, null);
        assertTrue( result.getCurrentPageNum() == 0);
        assertTrue( result.getCurrentPageSize() == 1);
        assertTrue( result.getResult().size() == 1);
    }

    @Test( expected = GobiiException.class)
    public void testGetContactsNotOk1() throws Exception {
        when(contactDao.getContacts(0, 1000, null)).thenThrow(new GobiiException("test"));

        contactServiceImpl.getContacts(0, 1000, null);
    }

    @Test( expected = GobiiDomainException.class)
    public void testGetContactsNotOk2() throws Exception {
        contactServiceImpl.getContacts(null, null, null);
    }

    @Test
    public void testAddContactOk() throws Exception {
        when(contactDao.getContactByUsername("test")).thenReturn(null);
        when(contactDao.addContact(any(Contact.class))).thenReturn(new Contact());
        when(organizationDao.getOrganizationByName("test-org")).thenReturn(new Organization());
        ArgumentCaptor<Contact> arg = ArgumentCaptor.forClass(Contact.class);

        contactServiceImpl.addContact("test", "test1", "testlastname", "test@email", "test-org", null);
        verify(contactDao).addContact(arg.capture());
        assertTrue( arg.getValue().getFirstName().equals("test1"));
        assertTrue( arg.getValue().getLastName().equals("testlastname"));
        assertTrue( arg.getValue().getUsername().equals("test"));
        assertTrue( arg.getValue().getEmail().equals("test@email"));
        
    }

    @Test
    public void testAddContactNewOrgOk() throws Exception {
        when(contactDao.getContactByUsername("test")).thenReturn(null);
        when(contactDao.addContact(any(Contact.class))).thenReturn(new Contact());
        when(organizationDao.getOrganizationByName("test-org")).thenReturn(null);
        when(organizationDao.createOrganization(any(Organization.class))).thenReturn(new Organization());
        ArgumentCaptor<Contact> arg = ArgumentCaptor.forClass(Contact.class);

        contactServiceImpl.addContact("test", "test1", "testlastname", "test@email", "test-org", null);
        verify(contactDao).addContact(arg.capture());
        assertTrue( arg.getValue().getFirstName().equals("test1"));
        assertTrue( arg.getValue().getLastName().equals("testlastname"));
        assertTrue( arg.getValue().getUsername().equals("test"));
        assertTrue( arg.getValue().getEmail().equals("test@email"));
        verify(organizationDao, times(1)).createOrganization(any(Organization.class));
    }


    @Test
    public void testAddContactNullOrgOk() throws Exception {
        when(contactDao.getContactByUsername("test")).thenReturn(null);
        when(contactDao.addContact(any(Contact.class))).thenReturn(new Contact());

        ArgumentCaptor<Contact> arg = ArgumentCaptor.forClass(Contact.class);

        contactServiceImpl.addContact("test", "test1", "testlastname", "test@email", null, null);
        verify(contactDao).addContact(arg.capture());
        assertTrue( arg.getValue().getFirstName().equals("test1"));
        assertTrue( arg.getValue().getLastName().equals("testlastname"));
        assertTrue( arg.getValue().getUsername().equals("test"));
        assertTrue( arg.getValue().getEmail().equals("test@email"));
        verify(organizationDao, times(0)).getOrganizationByName(any(String.class));
        verify(organizationDao, times(0)).createOrganization(any(Organization.class));
    }

    @Test
    public void testAddContactAndContactAlreadyExistsOk() throws Exception {
        Contact mockContact = new Contact();
        when(contactDao.getContactByUsername("test")).thenReturn(mockContact);
        contactServiceImpl.addContact("test", "test1", "testlastname", "test@email", null, null);
        verify(contactDao, times(0)).addContact(any(Contact.class));  
    }

    @Test
    public void testAddContactNewOrgThrowsExceptionOk() throws Exception {
        when(contactDao.getContactByUsername("test")).thenReturn(null);
        when(contactDao.addContact(any(Contact.class))).thenReturn(new Contact());
        when(organizationDao.getOrganizationByName("test-org")).thenReturn(null);
        when(organizationDao.createOrganization(any(Organization.class))).thenThrow(new Exception());
        ArgumentCaptor<Contact> arg = ArgumentCaptor.forClass(Contact.class);

        contactServiceImpl.addContact("test", "test1", "testlastname", "test@email", "test-org", null);
        verify(contactDao).addContact(arg.capture());
        assertTrue( arg.getValue().getFirstName().equals("test1"));
        assertTrue( arg.getValue().getLastName().equals("testlastname"));
        assertTrue( arg.getValue().getUsername().equals("test"));
        assertTrue( arg.getValue().getEmail().equals("test@email"));
        assertNull( arg.getValue().getOrganization());
        verify(organizationDao, times(1)).createOrganization(any(Organization.class));
    }


    @Test
    public void testGetKeycloakUsers() throws Exception {
        when(keycloakService.getKeycloakUsers("rice", Roles.PI, 0, 1000)).thenReturn(new ArrayList<ContactDTO>());
        
        contactServiceImpl.getUsers("rice", Roles.PI, 0, 1000, null);

        verify(keycloakService, times(1)).getKeycloakUsers("rice", Roles.PI, 0, 1000);

    }

    @Test(expected = GobiiDomainException.class)
    public void testGetKeycloakUsersThrowException() throws Exception {
        when(keycloakService.getKeycloakUsers("rice", Roles.PI, 0, 1000)).thenThrow(new Exception("foo"));
        
        contactServiceImpl.getUsers("rice", Roles.PI, 0, 1000, null);

        verify(keycloakService, times(1)).getKeycloakUsers("rice", Roles.PI, 0, 1000);

    }


}