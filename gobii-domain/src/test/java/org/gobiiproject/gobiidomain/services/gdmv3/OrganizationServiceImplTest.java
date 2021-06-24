package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.OrganizationDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Organization;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.OrganizationDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class OrganizationServiceImplTest {
    
    @Mock
    private OrganizationDao organizationDao;

    @Mock
    private CvDao cvDao;

    @Mock
    private ContactDao contactDao;

    @InjectMocks
    private OrganizationServiceImpl organizationServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetOrganizationsOk() throws Exception {
        List<Organization> mockList = new ArrayList<>();
        mockList.add(new Organization());

        when(organizationDao.getOrganizations(0, 1000)).thenReturn(mockList);
        PagedResult<OrganizationDTO> result = organizationServiceImpl.getOrganizations(0, 1000);
        assertTrue( result.getCurrentPageNum() == 0);
        assertTrue( result.getCurrentPageSize() == 1);
        assertTrue( result.getResult().size() == 1);
    }

    @Test
    public void testGetOrganizationOk() throws Exception {
        when(organizationDao.getOrganization(200)).thenReturn(new Organization());
        organizationServiceImpl.getOrganization(200);
        verify(organizationDao, times(1)).getOrganization(200);
    }

    @Test
    public void testCreateOrganizationOk() throws Exception {
        OrganizationDTO request = new OrganizationDTO();
        request.setOrganizationName("test-org");
        request.setOrganizationAddress("test-address");
        request.setOrganizationWebsite("http://test-website.com");

        when(cvDao.getNewStatus()).thenReturn(new Cv());

        Contact mockContact = new Contact();
        mockContact.setContactId(333);

        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);

        when(organizationDao.createOrganization(any(Organization.class))).thenReturn(new Organization());
        ArgumentCaptor<Organization> arg = ArgumentCaptor.forClass(Organization.class);
        
        organizationServiceImpl.createOrganization(request, "test-editor");

        verify(organizationDao).createOrganization(arg.capture());

        assertTrue(arg.getValue().getName().equals("test-org"));
        assertTrue(arg.getValue().getAddress().equals("test-address"));
        assertTrue(arg.getValue().getWebsite().equals("http://test-website.com"));
        assertTrue(arg.getValue().getCreatedBy() == 333 );

    }

    @Test
    public void testUpdateOrganizationOk() throws Exception {
        Organization mockOrg = new Organization();
        mockOrg.setOrganizationId(12);
        mockOrg.setName("old-name");
        mockOrg.setAddress("old-addr");
        mockOrg.setWebsite("old-website");
        
        when(organizationDao.getOrganization(12)).thenReturn(mockOrg);

        OrganizationDTO request = new OrganizationDTO();
        request.setOrganizationName("test-org");
        request.setOrganizationAddress("test-address");
        request.setOrganizationWebsite("http://test-website.com");

        when(cvDao.getModifiedStatus()).thenReturn(new Cv());

        when(contactDao.getContactByUsername("test-editor")).thenReturn(new Contact());

        when(organizationDao.updateOrganization(any(Organization.class))).thenReturn(new Organization());
        ArgumentCaptor<Organization> arg = ArgumentCaptor.forClass(Organization.class);
        
        organizationServiceImpl.updateOrganization(12, request, "test-editor");

        verify(organizationDao).updateOrganization(arg.capture());

        assertTrue(arg.getValue().getName().equals("test-org"));
        assertTrue(arg.getValue().getAddress().equals("test-address"));
        assertTrue(arg.getValue().getWebsite().equals("http://test-website.com"));
    }

    @Test
    public void testUpdateOrganizationOkSomeFieldsNull() throws Exception {
        Organization mockOrg = new Organization();
        mockOrg.setOrganizationId(12);
        mockOrg.setName("old-name");
        mockOrg.setAddress("old-addr");
        mockOrg.setWebsite("old-website");
        
        when(organizationDao.getOrganization(12)).thenReturn(mockOrg);

        OrganizationDTO request = new OrganizationDTO();

        when(cvDao.getModifiedStatus()).thenReturn(new Cv());

        when(contactDao.getContactByUsername("test-editor")).thenReturn(new Contact());

        when(organizationDao.updateOrganization(any(Organization.class))).thenReturn(new Organization());
        ArgumentCaptor<Organization> arg = ArgumentCaptor.forClass(Organization.class);
        
        organizationServiceImpl.updateOrganization(12, request, "test-editor");

        verify(organizationDao).updateOrganization(arg.capture());

        assertTrue(arg.getValue().getName().equals("old-name"));
        assertTrue(arg.getValue().getAddress().equals("old-addr"));
        assertTrue(arg.getValue().getWebsite().equals("old-website"));
    }


    @Test(expected = GobiiException.class)
    public void testUpdateOrganizationNotFound() throws Exception {
        when(organizationDao.getOrganization(12)).thenReturn(null);

        OrganizationDTO request = new OrganizationDTO();
        request.setOrganizationName("test-org");
        request.setOrganizationAddress("test-address");
        request.setOrganizationWebsite("http://test-website.com");

        organizationServiceImpl.updateOrganization(12, request, "test-editor");

        verify(organizationDao, times(0)).updateOrganization(any(Organization.class));
        
    }

    @Test
    public void testDeleteOrganizationOk() throws Exception {
        when(organizationDao.getOrganization(123)).thenReturn(new Organization());
        doNothing().when(organizationDao).deleteOrganization(any(Organization.class));

        organizationServiceImpl.deleteOrganization(123);

        verify(organizationDao, times(1)).deleteOrganization(any(Organization.class));
    }
}