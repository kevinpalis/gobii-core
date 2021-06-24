/**
 * V3ProjectServiceImplTest.java
 * 
 * Unit test for V3ProjectServiceImpl
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-07
 */
package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.PropertiesService;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.UnknownEntityException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.ContactDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.ProjectDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Project;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.ProjectDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class ProjectServiceImplTest {

    @Mock
    private ProjectDao projectDao;

    @Mock
    private CvDao cvDao;

    @Mock
    private ContactDao contactDao;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private PropertiesService propertiesService;

    @InjectMocks
    private ProjectServiceImpl v3ProjectServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSimple() throws Exception {

        assert projectDao != null;

        // Mock Cvs
        List<Cv> mockCvList = new java.util.ArrayList<>();
        when(cvDao.getCvListByCvGroup(CvGroupTerm.CVGROUP_PROJECT_PROP.getCvGroupName(), null)).thenReturn(mockCvList);

        List<Project> daoReturn = new java.util.ArrayList<>();
        Project mockEntity = new Project();
        mockEntity.setProjectName("PName");
        mockEntity.setContact(new Contact());
        mockEntity.getContact().setUsername("test");
        daoReturn.add(mockEntity);
        when(projectDao.getProjects(0, 1000, null)).thenReturn(daoReturn);

        //Mock Keycloak contact
        ContactDTO mockContactDto = new ContactDTO();
        mockContactDto.setUsername("test");
        mockContactDto.setPiContactId("pIConTActId");
        List<ContactDTO> mockPis = new ArrayList<>();
        mockPis.add(mockContactDto);
        when(keycloakService.getKeycloakUsers("test-crop", "pi", 0, 1000))
            .thenReturn(mockPis);

        PagedResult<ProjectDTO> payload = v3ProjectServiceImpl.getProjects(0, 1000, null, "test-crop");
        assert payload.getResult().size() == 1;
        assert payload.getCurrentPageNum() == 0;
        assert payload.getCurrentPageSize() == 1;

        verify(cvDao, times(1)).getCvListByCvGroup(CvGroupTerm.CVGROUP_PROJECT_PROP.getCvGroupName(), null);
    }

    @Test(expected = GobiiException.class)
    public void testGetProjectsException() throws Exception {
        when(cvDao.getCvListByCvGroup(CvGroupTerm.CVGROUP_PROJECT_PROP.getCvGroupName(), null))
                .thenThrow(new GobiiException("test-exc"));
        v3ProjectServiceImpl.getProjects(0, 1000, null, "test-crop");
    }

    @Test(expected = GobiiDomainException.class)
    public void testGetProjectsNotOk2() throws Exception {
        v3ProjectServiceImpl.getProjects(null, null, null, "test-crop");
    }

    @Test
    public void testCreateWithNullProperties() throws Exception {
        // Setup test GobiiProject
        ProjectDTO request = new ProjectDTO();
        String projectName = RandomStringUtils.random(10);
        request.setProjectName(projectName);
        request.setProjectDescription(RandomStringUtils.random(20));
        request.setPiContactUserName("piContactUserName");

        List<CvPropertyDTO> props = new ArrayList<>();
        CvPropertyDTO nullValued = new CvPropertyDTO();
        nullValued.setPropertyId(10);
        nullValued.setPropertyValue(null);
        props.add(nullValued);
        request.setProperties(props);

        Contact mockContact = new Contact();
        mockContact.setContactId(111);

        ContactDTO mockKeycloakUser = new ContactDTO();
        mockKeycloakUser.setUsername("pIConTactId");
        when(keycloakService.getUser("pIConTactId")).thenReturn(mockKeycloakUser);
        when(contactDao.getContactByUsername("pIConTactId")).thenReturn(mockContact);

        Cv mockNewStat = new Cv();

        when(cvDao.getNewStatus()).thenReturn(mockNewStat);

        Contact mockCreator = new Contact();
        mockCreator.setContactId(123);

        when(contactDao.getContactByUsername("test-user")).thenReturn(mockCreator);

        ArgumentCaptor<Project> arg = ArgumentCaptor.forClass(Project.class);

        v3ProjectServiceImpl.createProject(request, "test-user");
        verify(projectDao).createProject(arg.capture());

        assertTrue(arg.getValue().getProperties().size() == 0);
        assertTrue(arg.getValue().getProjectName().equals(projectName));
    }

    @Test
    public void testCreateWithNonNullProperties() throws Exception {
        // Setup test GobiiProject
        ProjectDTO request = new ProjectDTO();
        String projectName = RandomStringUtils.random(10);
        request.setProjectName(projectName);
        request.setProjectDescription(RandomStringUtils.random(20));
        request.setPiContactUserName("pIConTactName");

        List<CvPropertyDTO> props = new ArrayList<>();
        CvPropertyDTO valued = new CvPropertyDTO();
        valued.setPropertyId(10);
        valued.setPropertyValue("test");
        props.add(valued);
        request.setProperties(props);

        Contact mockContact = new Contact();
        mockContact.setContactId(111);

        ContactDTO mockKeycloakUser = new ContactDTO();
        mockKeycloakUser.setUsername("pIConTactId");
        when(keycloakService.getUser("pIConTactId")).thenReturn(mockKeycloakUser);
        when(contactDao.getContactByUsername("pIConTactId")).thenReturn(mockContact);

        Cv mockNewStat = new Cv();

        when(cvDao.getNewStatus()).thenReturn(mockNewStat);

        Contact mockCreator = new Contact();
        mockCreator.setContactId(123);

        when(contactDao.getContactByUsername("test-user")).thenReturn(mockCreator);
        when(cvDao.getCvListByCvGroup(CvGroupTerm.CVGROUP_PROJECT_PROP.getCvGroupName(), null))
                .thenReturn(new ArrayList<Cv>()); // this is just to avoid errors since we are only interested in the
                                                  // createProject args
        ArgumentCaptor<Project> arg = ArgumentCaptor.forClass(Project.class);

        v3ProjectServiceImpl.createProject(request, "test-user");
        verify(projectDao).createProject(arg.capture());

        assertTrue(arg.getValue().getProperties().size() == 1);

    }

    // Will not be loading contacts by db contact id anymore
    //@Test(expected = UnknownEntityException.class)
    //public void testCreateContactNotFound() throws Exception {
    //    when(contactDao.getContact(13)).thenThrow(new UnknownEntityException.Contact());

    //    ProjectDTO request = new ProjectDTO();
    //    String projectName = RandomStringUtils.random(10);
    //    request.setProjectName(projectName);
    //    request.setProjectDescription(RandomStringUtils.random(20));
    //    request.setPiContactId("pIConTactId");

    //    v3ProjectServiceImpl.createProject(request, "test-user");
    //}

    @Test
    public void testPatchProjectOk() throws Exception {
        Project mockProject = getMockProject();

        Contact mockContact = new Contact();
        mockContact.setContactId(333);
        mockContact.setUsername("test-user");

        mockProject.setContact(mockContact);

        // Contact mockNewContact = new Contact();
        // mockNewContact.setContactId(222);
        // mockNewContact.setUsername("new-user");

        Contact mockEditor = new Contact();
        mockEditor.setContactId(444);
        mockEditor.setUsername("test-editor");

        Cv mockModifiedStatus = new Cv();
        mockModifiedStatus.setCvId(12);
        mockModifiedStatus.setTerm("modified");

        ProjectDTO request = new ProjectDTO();
        //request.setPiContactId(222);
        request.setProjectName("new-project-name");
        request.setProjectDescription("new-project-description");

        when(projectDao.getProject(123)).thenReturn(mockProject);
        //when(contactDao.getContact(222)).thenReturn(mockNewContact);
        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockEditor);
        when(cvDao.getModifiedStatus()).thenReturn(mockModifiedStatus);
        when(projectDao.patchProject(any(Project.class))).thenReturn(new Project());
        ArgumentCaptor<Project> arg = ArgumentCaptor.forClass(Project.class);

        v3ProjectServiceImpl.patchProject(123, request, "test-editor");
        verify(projectDao).patchProject(arg.capture());

        Project param = arg.getValue();
        assertTrue(param.getProjectName().equals("new-project-name"));
        assertTrue(param.getProjectDescription().equals("new-project-description"));
        //assertTrue(param.getContact().getContactId() == 222);

    }

    @Test
    public void testUpdateProperties() throws Exception {
        Project mockProject = getMockProject();
        Contact mockContact = new Contact();
        mockContact.setContactId(333);
        mockContact.setUsername("test-user");

        mockProject.setContact(mockContact);

        ProjectDTO request = new ProjectDTO();
        List<CvPropertyDTO> testProps = new ArrayList<>();
        CvPropertyDTO mockProp = new CvPropertyDTO();
        mockProp.setPropertyId(444);
        mockProp.setPropertyValue("test-value");
        testProps.add(mockProp);
        request.setProperties(testProps);

        Contact mockEditor = new Contact();
        mockEditor.setContactId(444);
        mockEditor.setUsername("test-editor");

        Cv mockModifiedStatus = new Cv();
        mockModifiedStatus.setCvId(12);
        mockModifiedStatus.setTerm("modified");

        when(projectDao.getProject(123)).thenReturn(mockProject);
        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockEditor);
        when(cvDao.getModifiedStatus()).thenReturn(mockModifiedStatus);
        when(projectDao.patchProject(any(Project.class))).thenReturn(new Project());

        ArgumentCaptor<Project> arg = ArgumentCaptor.forClass(Project.class);

        v3ProjectServiceImpl.patchProject(123, request, "test-editor");
        verify(projectDao).patchProject(arg.capture());

        Project param = arg.getValue();
        assertTrue(param.getProperties().size() == 1);
    }

    @Test
    public void testDeleteProject() throws Exception {
        Project mockProject = getMockProject();

        when(projectDao.getProject(123)).thenReturn(mockProject);
        ArgumentCaptor<Project> arg = ArgumentCaptor.forClass(Project.class);
        v3ProjectServiceImpl.deleteProject(123);

        verify(projectDao).deleteProject(arg.capture());
        assertTrue(arg.getValue().getProjectId() == 123);
        verify(projectDao, times(1)).deleteProject(any(Project.class));
    }

    @Test
    public void testGetEditor() {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("test-editor");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertTrue(v3ProjectServiceImpl.getDefaultProjectEditor() == "test-editor");

    }

    @Test
    public void testGetProjectPropertiesOk() throws Exception {
        when(propertiesService.getProperties(0, 1000, CvGroupTerm.CVGROUP_PROJECT_PROP))
                .thenReturn(new PagedResult<CvPropertyDTO>());
        v3ProjectServiceImpl.getProjectProperties(0, 1000);

        verify(propertiesService, times(1)).getProperties(0, 1000, CvGroupTerm.CVGROUP_PROJECT_PROP);
    }

    @Test
    public void testGetProjectOk() throws Exception {
        when(projectDao.getProject(123)).thenReturn(new Project());
        v3ProjectServiceImpl.getProject(123);
        verify(projectDao, times(1)).getProject(123);
    }

    @Test(expected = GobiiException.class)
    public void testUpdateProjectNotFound() throws Exception {
        when(projectDao.getProject(123)).thenReturn(null);
        ProjectDTO request = new ProjectDTO();
        v3ProjectServiceImpl.patchProject(123, request, "test-editor");
        verify(projectDao, times(0)).patchProject(any(Project.class));
    }

    @Test
    public void testPatchProjectSameContactOk() throws Exception {
        Project mockProject = getMockProject();

        Contact mockContact = new Contact();
        mockContact.setContactId(333);
        mockContact.setUsername("test-user");

        mockProject.setContact(mockContact);

        Contact mockEditor = new Contact();
        mockEditor.setContactId(444);
        mockEditor.setUsername("test-editor");

        Cv mockModifiedStatus = new Cv();
        mockModifiedStatus.setCvId(12);
        mockModifiedStatus.setTerm("modified");

        ProjectDTO request = new ProjectDTO();
        request.setPiContactUserName("pIConTactName");

        when(projectDao.getProject(123)).thenReturn(mockProject);
        ContactDTO mockKeycloakUser = new ContactDTO();
        mockKeycloakUser.setUsername("pIConTactId");
        when(keycloakService.getUser("pIConTactId")).thenReturn(mockKeycloakUser);
        when(contactDao.getContactByUsername("pIConTactId")).thenReturn(mockContact);
        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockEditor);
        when(cvDao.getModifiedStatus()).thenReturn(mockModifiedStatus);
        when(projectDao.patchProject(any(Project.class))).thenReturn(new Project());
        ArgumentCaptor<Project> arg = ArgumentCaptor.forClass(Project.class);

        v3ProjectServiceImpl.patchProject(123, request, "test-editor");
        verify(projectDao).patchProject(arg.capture());

        Project param = arg.getValue();
        // assert no changes made
        assertTrue(param.getProjectName().equals("test-project"));
        assertTrue(param.getProjectDescription().equals("test-description"));
        assertTrue(param.getContact().getContactId() == 333);
        

    }

    @Test(expected = GobiiException.class)
    public void testPatchProjectUpdateContactNotFound() throws Exception {
        Project mockProject = getMockProject();

        Contact mockContact = new Contact();
        mockContact.setContactId(333);
        mockContact.setUsername("test-user");

        mockProject.setContact(mockContact);

        ProjectDTO request = new ProjectDTO();
        request.setPiContactUserName("pIConTactName");

        when(projectDao.getProject(123)).thenReturn(mockProject);
        ContactDTO mockKeycloakUser = new ContactDTO();
        mockKeycloakUser.setUsername("pIConTactId");
        when(keycloakService.getUser("pIConTactId")).thenReturn(mockKeycloakUser);
        when(contactDao.getContactByUsername("pIConTactId")).thenReturn(mockContact);

        v3ProjectServiceImpl.patchProject(123, request, "test-editor");
        verify(projectDao, times(0)).patchProject(any(Project.class));

    }

    @Test
    public void testPatchProjectEmptyUpdateOk() throws Exception {
        Project mockProject = getMockProject();

        Contact mockContact = new Contact();
        mockContact.setContactId(333);
        mockContact.setUsername("test-user");

        mockProject.setContact(mockContact);

        Contact mockEditor = new Contact();
        mockEditor.setContactId(444);
        mockEditor.setUsername("test-editor");

        Cv mockModifiedStatus = new Cv();
        mockModifiedStatus.setCvId(12);
        mockModifiedStatus.setTerm("modified");

        ProjectDTO request = new ProjectDTO();

        when(projectDao.getProject(123)).thenReturn(mockProject);
        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockEditor);
        when(cvDao.getModifiedStatus()).thenReturn(mockModifiedStatus);
        when(projectDao.patchProject(any(Project.class))).thenReturn(new Project());
        ArgumentCaptor<Project> arg = ArgumentCaptor.forClass(Project.class);

        v3ProjectServiceImpl.patchProject(123, request, "test-editor");
        verify(projectDao).patchProject(arg.capture());

        Project param = arg.getValue();
        // assert no changes made
        assertTrue(param.getProjectName().equals("test-project"));
        assertTrue(param.getProjectDescription().equals("test-description"));
        assertTrue(param.getContact().getContactId() == 333);

    }

    private Project getMockProject() {
        Project mockProject = new Project();
        mockProject.setProjectId(123);
        mockProject.setProjectName("test-project");
        mockProject.setProjectDescription("test-description");
        mockProject.setProperties(new HashMap<String, String>());
        return mockProject;
    }
}