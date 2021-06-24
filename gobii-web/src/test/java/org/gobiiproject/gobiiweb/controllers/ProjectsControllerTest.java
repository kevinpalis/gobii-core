package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.gobiiproject.gobiidomain.services.gdmv3.ProjectService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.ProjectDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.ProjectsController;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import lombok.extern.slf4j.Slf4j;

@ActiveProfiles("projectsController-test")
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*"})
@PrepareForTest({CropRequestAnalyzer.class, AuthUtils.class})
@ContextConfiguration(classes = GOBIIControllerV3TestConfiguration.class
// locations = { "classpath:/spring/application-config.xml" }
)
@WebAppConfiguration
@Slf4j
public class ProjectsControllerTest {

    @Mock
    private ProjectService projectService;

    private ProjectsController projectsController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Projects Controller test");
        MockitoAnnotations.initMocks(this);

        this.projectsController = new ProjectsController( projectService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(projectsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

        // assert this.projectsController.getProjectService() != null.

    }
    
    private ProjectDTO createMockProjectDTO() {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(1);
        dto.setModifiedBy(1);
        dto.setProjectName("test-project");

        List<CvPropertyDTO> propDtoList = createMockPropDTOList();
        dto.setProperties(propDtoList);
        return dto;
    }

    private CvPropertyDTO createMockPropDTO() {
        // mock DTO
        CvPropertyDTO propDto = new CvPropertyDTO();
        propDto.setPropertyId(1);
        propDto.setPropertyName("test-prop");
        propDto.setPropertyGroupId(1);
        propDto.setPropertyGroupName("test-group");
        propDto.setPropertyGroupType(1);
        return propDto;
    }

    private List<CvPropertyDTO> createMockPropDTOList() {
        List<CvPropertyDTO> propDtoList = new java.util.ArrayList<>();
        propDtoList.add(createMockPropDTO());
        return propDtoList;
    }

    @Test
    public void simpleListTest() throws Exception {
        assert projectService != null;
        List<ProjectDTO> mockList = new ArrayList<ProjectDTO>();
        ProjectDTO mockItem = createMockProjectDTO();
        mockList.add(mockItem);
        PagedResult<ProjectDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);
        when(projectService.getProjects(0, 1000, null, "dev")).thenReturn(mockPayload);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/projects").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
                .andExpect(jsonPath("$.result.data[0].projectId").value(mockItem.getProjectId()))
                .andExpect(jsonPath("$.result.data[0].projectName").value(mockItem.getProjectName()))
                .andExpect(jsonPath("$.result.data[0].properties[0].propertyType").value("system defined"));
        verify(projectService, times(1)).getProjects(0, 1000, null, "dev");
    }

    @Test
    public void listWithQueryTest() throws Exception {
        assert projectService != null;
        List<ProjectDTO> mockList = new ArrayList<ProjectDTO>();
        ProjectDTO mockItem = createMockProjectDTO();
        mockList.add(mockItem);
        PagedResult<ProjectDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);
        when(projectService.getProjects(1, 100, "contact-uuid", "dev")).thenReturn(mockPayload);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/projects?page=1&pageSize=100&piContactId=2")
                .contextPath("/gdm")).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
                .andExpect(jsonPath("$.result.data[0].projectId").value(mockItem.getProjectId()))
                .andExpect(jsonPath("$.result.data[0].projectName").value(mockItem.getProjectName()))
                .andExpect(jsonPath("$.result.data[0].properties[0].propertyType").value("system defined"));
        verify(projectService, times(1)).getProjects(1, 100, "contact-uuid", "dev");
    }

    @Test
    public void testCreateSimple() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        ProjectDTO mockRequest = new ProjectDTO();
        mockRequest.setPiContactUserName("test-picontact-name"); // need to mock contact here
        mockRequest.setProjectName("Test project");
        mockRequest.setProjectDescription("Test description");
        // this test does not include properties
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(mockRequest);

        ProjectDTO mockGobiiProject = new ProjectDTO();
        // let's leave it empty since it's a mock anyways
        //doReturn("gadm").when(projectService).getDefaultProjectEditor();
        when(AuthUtils.getCurrentUser()).thenReturn("gadm");
        when(projectService.createProject(any(ProjectDTO.class), eq("gadm"))).thenReturn(mockGobiiProject);
       
        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/projects")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.metadata").doesNotExist());
    }

    @Test
    public void testCreateWithProperties() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        String requestJson = "{\"piContactId\" : \"4\",\"projectName\" : \"test\", \"projectDescription\" : \"Test description\","
                + "\"properties\" : [ {\"propertyId\" : 4,  \"propertyValue\" : \"test-value\"} ]}";

        ProjectDTO mockGobiiProject = new ProjectDTO();
        // let's leave it empty since it's a mock anyways
        when(AuthUtils.getCurrentUser()).thenReturn("gadm");
        when(projectService.createProject(any(ProjectDTO.class), eq("gadm"))).thenReturn(mockGobiiProject);

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/projects")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.metadata").doesNotExist());
    }

    @Test
    public void testProjectCreateWithPropertiesValidationError() throws Exception {
        String requestJson = "{\"piContactId\" : null,\"projectName\" : null, \"projectDescription\" : \"Test description\","
                + "\"properties\" : [ {\"propertyId\" : 4, \"propertyName\" : null, \"propertyValue\" : \"test-value\"} ]}";

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/projects")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(StringContains.containsString("piContactId must not be null")))
                .andExpect(jsonPath("$.error").value(StringContains.containsString("projectName must not be empty")));
    }

    @Test
    public void testPatchWithProperties() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        String requestJson = "{\"piContactId\" : \"4\",\"projectName\" : \"test\", \"projectDescription\" : \"Test description\","
                + "\"properties\" : [ {\"propertyId\" : \"4\",  \"propertyValue\" : \"test-value\"} ]}";

        ProjectDTO mockGobiiProject = new ProjectDTO();
        // let's leave it empty since it's a mock anyways
        when(AuthUtils.getCurrentUser()).thenReturn("gadm");
        when(projectService.patchProject(eq(84), any(ProjectDTO.class), eq("gadm"))).thenReturn(mockGobiiProject);

        mockMvc.perform(MockMvcRequestBuilders.patch("/gdm/crops/dev/gobii/v3/projects/84")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.metadata").doesNotExist());
    }

    // TEsts for Project Properties
    @Test
    public void testListProjectProperties() throws Exception {

        List<CvPropertyDTO> mockList = createMockPropDTOList();
        PagedResult<CvPropertyDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);
        when(projectService.getProjectProperties(0, 1000)).thenReturn(mockPayload);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/projects/properties").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
                .andExpect(jsonPath("$.result.data[0].propertyId").value(1))
                .andExpect(jsonPath("$.result.data[0].propertyName").value("test-prop"))
                .andExpect(jsonPath("$.result.data[0].propertyType").value("system defined"));
        verify(projectService, times(1)).getProjectProperties(0, 1000);
    }

    @Test
    public void testGetProject() throws Exception {

        ProjectDTO mockGobiiProject = new ProjectDTO(); // let's leave it empty since it's a mock anyways
        when(projectService.getProject(eq(123))).thenReturn(mockGobiiProject);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/projects/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.metadata").doesNotExist());
        verify(projectService, times(1)).getProject(123);
    }

    @Test
    public void testGetProjectNotFound() throws Exception {

        when(projectService.getProject(eq(123))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/projects/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNotFound());
        verify(projectService, times(1)).getProject(123);
    }

    @Test
    public void testDelete() throws Exception {

        doNothing().when(projectService).deleteProject(eq(84));

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/projects/84").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().is(204));

        verify(projectService, times(1)).deleteProject(eq(84));
    }

    @Test
    public void testDelete404() throws Exception {
        Exception exc = new NullPointerException("test");
        doThrow(exc).when(projectService).deleteProject(eq(84));

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/projects/84").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(jsonPath("$.error").value(StringContains.containsString("Resource not found")));

        verify(projectService, times(1)).deleteProject(eq(84));
    }

    @Test
    public void testDelete409() throws Exception {
        Exception exc = new GobiiException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.FOREIGN_KEY_VIOLATION,
                "test");
        doThrow(exc).when(projectService).deleteProject(eq(84));

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/projects/84").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().is(409))
                .andExpect(jsonPath("$.error").value(StringContains.containsString("test")));

        verify(projectService, times(1)).deleteProject(eq(84));
    }

   
}
