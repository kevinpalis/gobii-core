/**
 * ProjectsControllerTest.java
 * Unit test for Projects endpoints
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-03-06
 */
package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyListOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.gobiiproject.gobidomain.services.gdmv3.AnalysisService;
import org.gobiiproject.gobidomain.services.gdmv3.ContactService;
import org.gobiiproject.gobidomain.services.gdmv3.CvService;
import org.gobiiproject.gobidomain.services.gdmv3.DatasetService;
import org.gobiiproject.gobidomain.services.gdmv3.ExperimentService;
import org.gobiiproject.gobidomain.services.gdmv3.MapsetService;
import org.gobiiproject.gobidomain.services.gdmv3.MarkerGroupService;
import org.gobiiproject.gobidomain.services.gdmv3.OrganizationService;
import org.gobiiproject.gobidomain.services.gdmv3.PlatformService;
import org.gobiiproject.gobidomain.services.gdmv3.ProjectService;
import org.gobiiproject.gobidomain.services.gdmv3.ReferenceService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.auditable.GobiiProjectDTO;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.*;

import org.gobiiproject.gobiimodel.dto.request.ExperimentPatchRequest;
import org.gobiiproject.gobiimodel.dto.request.ExperimentRequest;
import org.gobiiproject.gobiimodel.dto.request.GobiiProjectPatchDTO;
import org.gobiiproject.gobiimodel.dto.request.GobiiProjectRequestDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = GOBIIControllerV3TestConfiguration.class
  //locations = { "classpath:/spring/application-config.xml" }
)
@WebAppConfiguration
@Slf4j
public class GOBIIControllerV3Test {
    @Mock
    private ProjectService projectService;

    @Mock
    private ContactService contactService;

    @Mock
    private ExperimentService experimentService;

    @Mock
    private AnalysisService analysisService;

    @Mock
    private DatasetService datasetService;

    @Mock
    private ReferenceService referenceService;

    @Mock
    private MapsetService mapsetService;

    @Mock
    private MarkerGroupService markerGroupService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private CvService cvService;

    @Mock
    private PlatformService platformService;

    @InjectMocks
    private GOBIIControllerV3 gobiiControllerV3;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Controller test");
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(gobiiControllerV3)
            .setControllerAdvice(new GlobalControllerExceptionHandler())
            .build();

        //assert this.projectsController.getProjectService() != null.

    }

    private GobiiProjectDTO createMockProjectDTO() {
        GobiiProjectDTO dto = new GobiiProjectDTO();
        dto.setId(1);
        dto.setModifiedBy(1);
        dto.setProjectName("test-project");
        

        List<CvPropertyDTO> propDtoList = createMockPropDTOList();
        dto.setProperties(propDtoList);
        return dto;
    }

    private CvPropertyDTO createMockPropDTO() {
        //mock DTO
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
        propDtoList.add( createMockPropDTO() );
        return propDtoList;
    }

    @Test
    public void simpleListTest() throws Exception {
        assert projectService != null;
        List<GobiiProjectDTO> mockList = new ArrayList<GobiiProjectDTO>();
        GobiiProjectDTO mockItem = createMockProjectDTO();
        mockList.add(mockItem);
        PagedResult<GobiiProjectDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);
        when(
            projectService.getProjects(0, 1000, null)
        ).thenReturn(
            mockPayload
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/projects")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
        .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
        .andExpect(jsonPath("$.result.data[0].projectId").value(mockItem.getProjectId()))
        .andExpect(jsonPath("$.result.data[0].projectName").value(mockItem.getProjectName()))
        .andExpect(jsonPath("$.result.data[0].properties[0].propertyType").value("system defined"))
        ;
        verify(projectService, times(1)).getProjects(0, 1000, null);
    }

    @Test
    public void listWithQueryTest() throws Exception {
        assert projectService != null;
        List<GobiiProjectDTO> mockList = new ArrayList<GobiiProjectDTO>();
        GobiiProjectDTO mockItem = createMockProjectDTO();
        mockList.add(mockItem);
        PagedResult<GobiiProjectDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);
        when(
            projectService.getProjects(1, 100, 2)
        ).thenReturn(
            mockPayload
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/projects?page=1&pageSize=100&piContactId=2")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
        .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
        .andExpect(jsonPath("$.result.data[0].projectId").value(mockItem.getProjectId()))
        .andExpect(jsonPath("$.result.data[0].projectName").value(mockItem.getProjectName()))
        .andExpect(jsonPath("$.result.data[0].properties[0].propertyType").value("system defined"))
        ;
        verify(projectService, times(1)).getProjects(1, 100, 2);
    }
    
    @Test
    public void testCreateSimple() throws Exception {
        GobiiProjectRequestDTO mockRequest = new GobiiProjectRequestDTO();
        mockRequest.setPiContactId("1"); //need to mock contact here
        mockRequest.setProjectName("Test project");
        mockRequest.setProjectDescription("Test description");
        //this test does not include properties
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(mockRequest);

        GobiiProjectDTO mockGobiiProject = new GobiiProjectDTO();
        //let's leave it empty since it's a mock anyways
        doReturn("gadm").when(projectService).getDefaultProjectEditor();
        
		when(
            projectService.createProject( any(GobiiProjectRequestDTO.class), eq("gadm"))
        ).thenReturn(
            mockGobiiProject
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.metadata").doesNotExist())
        ;
    }

    @Test
    public void testCreateWithProperties() throws Exception {
        String requestJson = "{\"piContactId\" : 4,\"projectName\" : \"test\", \"projectDescription\" : \"Test description\"," +
            "\"properties\" : [ {\"propertyId\" : 4,  \"propertyValue\" : \"test-value\"} ]}";

        GobiiProjectDTO mockGobiiProject = new GobiiProjectDTO();
        //let's leave it empty since it's a mock anyways
        doReturn("gadm").when(projectService).getDefaultProjectEditor();
        when(
            projectService.createProject( any(GobiiProjectRequestDTO.class), eq("gadm") )
        ).thenReturn(
            mockGobiiProject
        );

        

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.metadata").doesNotExist())
        ;
    }

    @Test
    public void testCreateWithPropertiesValidationError() throws Exception {
        String requestJson = "{\"piContactId\" : null,\"projectName\" : null, \"projectDescription\" : \"Test description\"," +
            "\"properties\" : [ {\"propertyId\" : 4, \"propertyName\" : null, \"propertyValue\" : \"test-value\"} ]}";

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value(StringContains.containsString("piContactId must not be empty")))
        .andExpect(jsonPath("$.error").value(StringContains.containsString("projectName must not be empty")))
        ;
    }

    @Test
    public void testPatchWithProperties() throws Exception {
        String requestJson = "{\"piContactId\" : \"4\",\"projectName\" : \"test\", \"projectDescription\" : \"Test description\"," +
            "\"properties\" : [ {\"propertyId\" : \"4\",  \"propertyValue\" : \"test-value\"} ]}";

        GobiiProjectDTO mockGobiiProject = new GobiiProjectDTO();
        //let's leave it empty since it's a mock anyways
        doReturn("gadm").when(projectService).getDefaultProjectEditor();
        when(
            projectService.patchProject( eq(84), any(GobiiProjectPatchDTO.class), eq("gadm") )
        ).thenReturn(
            mockGobiiProject
        );

        
        mockMvc.perform(
            MockMvcRequestBuilders
            .patch("/gobii-dev/gobii/v3/projects/84")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.metadata").doesNotExist())
        ;
    }

    //TEsts for Project Properties
    @Test
    public void testListProjectProperties() throws Exception {

        List<CvPropertyDTO> mockList = createMockPropDTOList();
        PagedResult<CvPropertyDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);
        when(
            projectService.getProjectProperties(0, 1000)
        ).thenReturn(
            mockPayload
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/projects/properties")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
        .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
        .andExpect(jsonPath("$.result.data[0].propertyId").value(1))
        .andExpect(jsonPath("$.result.data[0].propertyName").value("test-prop"))
        .andExpect(jsonPath("$.result.data[0].propertyType").value("system defined"))
        ;
        verify(projectService, times(1)).getProjectProperties(0, 1000);
    }

    @Test
    public void testGetProject() throws Exception {

        GobiiProjectDTO mockGobiiProject = new GobiiProjectDTO(); //let's leave it empty since it's a mock anyways
        when(
            projectService.getProject( eq(123) )
        ).thenReturn(
            mockGobiiProject
        );

        
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/projects/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.metadata").doesNotExist())
        ;
        verify(projectService, times(1)).getProject(123);
    }

    @Test
    public void testGetProjectNotFound() throws Exception {

        when(
            projectService.getProject( eq(123) )
        ).thenReturn(
            null
        );
     
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/projects/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        ;
        verify(projectService, times(1)).getProject(123);
    }

    @Test
    public void testDelete() throws Exception {
        
        doNothing().when(
            projectService
        ).deleteProject(eq(84));
       
        
        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/projects/84")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().is(204))
        ;

        verify(projectService, times(1)).deleteProject(eq(84));
    }

    @Test
    public void testDelete404() throws Exception {
        Exception exc = new NullPointerException("test");
        doThrow(
            exc
        ).when(
            projectService
        ).deleteProject(eq(84));
       
        
        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/projects/84")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().is(404))
        .andExpect(jsonPath("$.error").value(StringContains.containsString("Resource not found")))
        ;

        verify(projectService, times(1)).deleteProject(eq(84));
    }

    @Test
    public void testDelete409() throws Exception {
        Exception exc = new GobiiException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.FOREIGN_KEY_VIOLATION, "test");
        doThrow(
            exc
        ).when(
            projectService
        ).deleteProject(eq(84));
       
        
        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/projects/84")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().is(409))
        .andExpect(jsonPath("$.error").value(StringContains.containsString("test")))
        ;

        verify(projectService, times(1)).deleteProject(eq(84));
    }

    @Test
    public void testGetContacts() throws Exception {
        assert contactService != null;
        List<ContactDTO> mockList = new ArrayList<ContactDTO>();
        ContactDTO mockItem = new ContactDTO();
        mockItem.setPiContactId(111);
        mockItem.setPiContactFirstName("test");
        mockList.add(mockItem);
        PagedResult<ContactDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);
        when(
            contactService.getContacts(0, 1000, null)
        ).thenReturn(
            mockPayload
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/contacts")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
        .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
        .andExpect(jsonPath("$.result.data[0].piContactId").value(mockItem.getPiContactId()))
        .andExpect(jsonPath("$.result.data[0].piContactFirstName").value(mockItem.getPiContactFirstName()))
        ;
        verify(contactService, times(1)).getContacts(0, 1000, null);
    }

    @Test
    public void testGetExperimentsSimple() throws Exception {
        assert experimentService != null;
        List<ExperimentDTO> mockList = new ArrayList<>();
        ExperimentDTO mockItem = new ExperimentDTO();

        mockList.add(mockItem);
        PagedResult<ExperimentDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);
        when(
            experimentService.getExperiments(0, 1000, null)
        ).thenReturn(
            mockPayload
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/experiments")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
        .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
        .andExpect(jsonPath("$.result.data[0].experimentId").value(mockItem.getExperimentId()))
        ;
        verify(experimentService, times(1)).getExperiments(0, 1000, null);
    }

    @Test
    public void testGetExperimentById() throws Exception {
        ExperimentDTO mockItem = new ExperimentDTO();
        when(
            experimentService.getExperiment(123)
        ).thenReturn(
            mockItem
        );
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/experiments/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.result.experimentName").hasJsonPath())
        ;
        verify(experimentService, times(1)).getExperiment(123);

    }

    @Test
    public void testGetExperimentById404() throws Exception {
        when(
            experimentService.getExperiment(123)
        ).thenThrow(
            new NullPointerException()
        );
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/experiments/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        ;
        verify(experimentService, times(1)).getExperiment(123);

    }

    @Test
    public void testCreateExperimentsSimple() throws Exception {
        String jsonRequest = "{\"projectId\" : \"7\", \"experimentName\" : \"fooExperiment\", \"vendorProtocolId\" : \"4\"}";
        when(
            experimentService.createExperiment( any( ExperimentRequest.class), eq("test-user" ))
        ).thenReturn(
            new ExperimentDTO()
        );
        when(
            projectService.getDefaultProjectEditor() //TODO: Refactor where this editor info is called
        ).thenReturn("test-user");

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/experiments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.metadata").doesNotExist())
        ;
        verify(experimentService, times(1)).createExperiment( any( ExperimentRequest.class ), eq("test-user"));
    }

    @Test
    public void testUpdateExperimentSimple() throws Exception {
        String jsonRequest = "{\"projectId\" : \"7\", \"experimentName\" : \"fooExperiment\", \"vendorProtocolId\" : \"4\"}";
        when(
            experimentService.updateExperiment(eq(123),  any( ExperimentPatchRequest.class), eq("test-user"))
        ).thenReturn(
            new ExperimentDTO()
        );
        when(
            projectService.getDefaultProjectEditor() //TODO: replace this with AuthenticationService
        ).thenReturn("test-user");

        mockMvc.perform(
            MockMvcRequestBuilders
            .patch("/gobii-dev/gobii/v3/experiments/123")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        ;
        verify(experimentService, times(1)).updateExperiment(eq(123),  any( ExperimentPatchRequest.class ), eq("test-user"));
    }

    @Test
    public void testDeleteExperiment() throws Exception {
        
        doNothing().when(experimentService).deleteExperiment(eq(123));

        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/experiments/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        ;
        verify(experimentService, times(1)).deleteExperiment(eq(123));
    }

    @Test
    public void testDeleteExperimentException409() throws Exception {
        Exception exc = new GobiiException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.FOREIGN_KEY_VIOLATION, "test");
        doThrow(exc).when(experimentService).deleteExperiment(eq(123));

        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/experiments/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().is(409))
        .andExpect(jsonPath("$.error").value(StringContains.containsString("test")))
        ;
        verify(experimentService, times(1)).deleteExperiment(eq(123));
    }

    @Test
    public void testListAnalysis() throws Exception {
        AnalysisDTO mockAnalysisDTO  = new AnalysisDTO();
        List<AnalysisDTO> mockList = new ArrayList<>();
        mockList.add(mockAnalysisDTO);
        PagedResult<AnalysisDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);

        when(
            analysisService.getAnalyses(eq(0), eq(1000))
        ).thenReturn(
            mockPayload
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/analyses")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;
        verify(analysisService, times(1)).getAnalyses(eq(0), eq(1000));
    }
    
    @Test
    public void testCreateAnalysisSimple() throws Exception {
        
        String jsonRequest = "{\"analysisName\": \"test-name\", \"analysisTypeId\": \"93\" }";
        when(
            analysisService.createAnalysis(any(AnalysisDTO.class), any(String.class))
        ).thenReturn(
            new AnalysisDTO()
        );

        when(
            projectService.getDefaultProjectEditor() //TODO: Refactor where this editor info is called
        ).thenReturn("test-user");

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/analyses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;
        verify(analysisService, times(1)).createAnalysis(any(AnalysisDTO.class), eq("test-user"));
    }

    @Test
    public void testCreateAnalysisType() throws Exception {
        String jsonRequest = "{\"analysisTypeName\" : \"testType\"}";

        when(
            analysisService.createAnalysisType(any(CvTypeDTO.class), any(String.class))
        ).thenReturn(
            new CvTypeDTO()
        );

        when(
            projectService.getDefaultProjectEditor() //TODO: Refactor where this editor info is called
        ).thenReturn("test-user");

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/analyses/types")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;
        
    }

    @Test
    public void testListAnalysisTypes() throws Exception {
        List<CvTypeDTO> analysisTypes = new ArrayList<>();
        Integer page = 0;
        Integer pageSize = 1000;
        PagedResult<CvTypeDTO> result = new PagedResult<>();
        result.setResult(analysisTypes);

        when(
            analysisService.getAnalysisTypes(page, pageSize)
        ).thenReturn(
            result
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/analyses/types")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(analysisService, times(1)).getAnalysisTypes(0, 1000);
    }


    @Test
    public void testPatchAnalysisById() throws Exception {

        AnalysisDTO patchData = new AnalysisDTO();
        patchData.setAnalysisName("New Name");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(patchData);
        Integer testId = 123;

        AnalysisDTO mockDTO = new AnalysisDTO();

        when(
            analysisService.updateAnalysis(eq(testId), any(AnalysisDTO.class), eq("test-user"))
        ).thenReturn(
            mockDTO
        );

        when(
            projectService.getDefaultProjectEditor() //TODO: Refactor where this editor info is called
        ).thenReturn("test-user");

        mockMvc.perform(
            MockMvcRequestBuilders
            .patch("/gobii-dev/gobii/v3/analyses/123")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(analysisService, times(1)).updateAnalysis(eq(testId), any(AnalysisDTO.class), eq("test-user"));
        verify(projectService, times(1)).getDefaultProjectEditor();
    }

    @Test
    public void getAnalysisByIdTest() throws Exception {
        AnalysisDTO mockDTO = new AnalysisDTO();
        Integer mockId = 123;
        when(
            analysisService.getAnalysis(mockId)
        ).thenReturn(
            mockDTO
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/analyses/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(analysisService, times(1)).getAnalysis(eq(123));

    }

    @Test
    public void deleteAnalysisByIdTest() throws Exception {
        Integer analysisId = 123;
        doNothing().when(analysisService).deleteAnalysis(analysisId);

        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/analyses/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());
        ;
        verify(analysisService, times(1)).deleteAnalysis(123);

      
    }

    @Test 
    public void createDatasetSimpleTest() throws Exception {
        assert datasetService != null;
        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetName("test-name");
        request.setExperimentId(1);
        request.setCallingAnalysisId(1);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(request);

        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn(
            "test-user"
        );
        when(
            datasetService.createDataset(any(DatasetRequestDTO.class), eq("test-user"))
        ).thenReturn(
            new DatasetDTO()
        );
        
        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/datasets")
            .contextPath("/gobii-dev")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;
        verify( datasetService, times(1)).createDataset(any(DatasetRequestDTO.class), eq("test-user"));


    }

    @Test
    public void testCreateDatasetWithAnalysisIds() throws Exception {
        assert datasetService != null;
        String requestJson = "{\"datasetName\" : \"test-name\", \"experimentId\" : \"1\", \"callingAnalysisId\" : \"1\", \"analysisIds\" : [ \"2\", \"3\", \"4\" ]}";

        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn(
            "test-user"
        );

        when(
            datasetService.createDataset(any(DatasetRequestDTO.class), eq("test-user"))
        ).thenReturn(
            new DatasetDTO()
        );
        
        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/datasets")
            .contextPath("/gobii-dev")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .characterEncoding("UTF-8")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;

        verify( datasetService, times(1)).createDataset(any(DatasetRequestDTO.class), eq("test-user"));
    }


    @Test
    public void testDatasetListing() throws Exception {
        when(
            datasetService.getDatasets(0, 1000, null, null)
        ).thenReturn(
            new PagedResult<DatasetDTO>()
        );


        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/datasets")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(datasetService, times(1)).getDatasets(0, 1000, null, null);

    }


    @Test
    public void testDatasetListingWithQuery() throws Exception {
        when(
            datasetService.getDatasets(5, 100, 1, 2)
        ).thenReturn(
            new PagedResult<DatasetDTO>()
        );


        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/datasets?page=5&pageSize=100&experimentId=1&datasetTypeId=2")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(datasetService, times(1)).getDatasets(5, 100, 1, 2);

    }


    @Test
    public void testDatasetGetById() throws Exception {
        Integer target = 112;
        when(
            datasetService.getDataset(target)
        ).thenReturn(
            new DatasetDTO()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/datasets/112")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(datasetService, times(1)).getDataset(target);
    }

    @Test
    public void testDatasetByIdUpdate() throws Exception {
        Integer target = 112;
        when(
            datasetService.updateDataset(
                eq(target),
                any(DatasetRequestDTO.class),
                eq("test-user")
            )
        ).thenReturn(
            new DatasetDTO()
        );

        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn("test-user");

        String requestJson = "{\"datasetName\" : \"test-name-edited\", \"experimentId\" : \"1\", \"callingAnalysisId\" : \"1\", \"analysisIds\" : [ \"2\", \"3\", \"4\" ]}";

        mockMvc.perform(
            MockMvcRequestBuilders
            .patch("/gobii-dev/gobii/v3/datasets/112")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;
    }

    @Test
    public void testDatasetDeleteById() throws Exception {
        Integer datasetId = 112;
        doNothing().when(datasetService).deleteDataset(datasetId);

        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/datasets/112")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(datasetService, times(1)).deleteDataset(datasetId);
    }

    @Test
    public void testListDatasetTypes() throws Exception {

        when(
            datasetService.getDatasetTypes(0, 1000)
        ).thenReturn(
            new PagedResult<CvTypeDTO>()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/datasets/types")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testAddDatasetType() throws Exception {
        when(
            datasetService.createDatasetType("test-name", "datasetTypeDescription", "user")
        ).thenReturn(
            new CvTypeDTO()
        );

        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn("user");


        String requestJson = "{\"typeName\" : \"test-name\", \"typeDescription\": \"datasetTypeDescription\"}";
        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/datasets/types")
            .contextPath("/gobii-dev")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;
    }


    @Test
    public void testGetReferences() throws Exception {
        when(
            referenceService.getReferences(0, 1000)
        ).thenReturn(
            new PagedResult<ReferenceDTO>()
        );
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/references")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());

        verify(referenceService, times(1)).getReferences(0, 1000);

    }

    @Test
    public void testGetMapsets() throws Exception {
        when(
            mapsetService.getMapsets(0, 1000, null)
        ).thenReturn(
            new PagedResult<MapsetDTO>()
        );
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/mapsets")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(mapsetService, times(1)).getMapsets(0, 1000, null);
    }

    @Test
    public void testCreateSimpleMapset() throws Exception {
        String requestJson = "{\"mapsetName\": \"test-mapset\", \"mapsetDescription\": \"test-description\", \"mapsetTypeId\": \"10\", \"referenceId\": \"12\"}";

        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn("test-user");

        when(
            mapsetService.createMapset(any(MapsetDTO.class), eq("test-user"))
        ).thenReturn(
            new MapsetDTO()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/mapsets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;

        verify(mapsetService, times(1)).createMapset(any(MapsetDTO.class), eq("test-user"));
    }

    @Test
    public void testCreateSimpleMapsetMissingMapsetTypeId() throws Exception {
        String requestJson = "{\"mapsetName\": \"test-mapset\", \"mapsetDescription\": \"test-description\", \"referenceId\": \"12\"}";

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/mapsets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        ;
    }

    @Test
    public void testGetMapsetById() throws Exception {
        when(
            mapsetService.getMapset(122)
        ).thenReturn(
            new MapsetDTO()
        );
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/mapsets/122")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;
        verify(mapsetService, times(1)).getMapset(122);
    }

    @Test
    public void testGetMapsetByIdNotFound() throws Exception {
        when(
            mapsetService.getMapset(122)
        ).thenThrow(
            new GobiiException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST, "Mapset not found")
        );
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/mapsets/122")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        ;
        verify(mapsetService, times(1)).getMapset(122);
    }

    @Test
    public void testPatchSimpleMapset() throws Exception {
        String requestJson = "{\"mapsetName\": \"test-mapset\", \"mapsetDescription\": \"test-description\"}";

        when(
            projectService.getDefaultProjectEditor()   
        ).thenReturn("test-user");

        when(
            mapsetService.updateMapset(eq(122), any(MapsetDTO.class), eq("test-user"))
        ).thenReturn(
            new MapsetDTO()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .patch("/gobii-dev/gobii/v3/mapsets/122")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(mapsetService, times(1)).updateMapset(eq(122), any(MapsetDTO.class), eq("test-user"));
    }


    @Test
    public void testDeleteMapset() throws Exception {
        Integer mapsetId = 122;
        doNothing().when(mapsetService).deleteMapset(mapsetId);

        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/mapsets/122")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        ;
        verify(mapsetService, times(1)).deleteMapset(122);
    }

    @Test
    public void createMapsetTypeTest() throws Exception {
        String requestJson = "{\"typeName\": \"test-name\", \"typeDescription\": \"test-desc\"}";
        when(
            mapsetService.createMapsetType("test-name", "test-desc", "user")
        ).thenReturn(
            new CvTypeDTO()
        );

        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn("user");

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/mapsets/types")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;

        verify(mapsetService, times(1)).createMapsetType("test-name", "test-desc", "user");
    }

    @Test
    public void getMapsetTypes() throws Exception {
        when(
            mapsetService.getMapsetTypes(0, 1000)
        ).thenReturn(
            new PagedResult<CvTypeDTO>()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/mapsets/types")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;
        verify(mapsetService, times(1)).getMapsetTypes(0, 1000);
    }

    @Test
    public void getOrganizationsList() throws Exception {
        when(
            organizationService.getOrganizations(0, 1000)
        ).thenReturn(
            new PagedResult<OrganizationDTO>()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/organizations")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;
        verify(organizationService, times(1)).getOrganizations(0, 1000);
    }

    @Test
    public void getOrganizationById() throws Exception {
        Integer organizationId = 122;
        when(
            organizationService.getOrganization(organizationId)
        ).thenReturn(
            new OrganizationDTO()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/organizations/122")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(organizationService, times(1)).getOrganization(122);
    }

    @Test
    public void testCreateOrganization() throws Exception {
        String requestJson = "{\"organizationName\": \"test-org\", \"organizationAddress\": \"organization-address\", \"organizationWebsite\": \"https://website.com\"}";

        when(
            organizationService.createOrganization(any(OrganizationDTO.class), eq("test-user"))
        ).thenReturn(
            new OrganizationDTO()
        );

        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn("test-user");

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/organizations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;

        verify(organizationService, times(1)).createOrganization(any(OrganizationDTO.class), eq("test-user"));
    }

    @Test
    public void testUpdateOrganization() throws Exception {
        String requestJson = "{\"organizationName\": \"test-org\", \"organizationAddress\": \"organization-address\", \"organizationWebsite\": \"https://website.com\"}";

        when(
            organizationService.updateOrganization(eq(123), any(OrganizationDTO.class), eq("test-user"))
        ).thenReturn(
            new OrganizationDTO()
        );

        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn("test-user");

        mockMvc.perform(
            MockMvcRequestBuilders
            .patch("/gobii-dev/gobii/v3/organizations/123")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(organizationService, times(1)).updateOrganization(eq(123), any(OrganizationDTO.class), eq("test-user"));
    }

    @Test
    public void testDeleteOrganization() throws Exception {
        doNothing().when(organizationService).deleteOrganization(123);
        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/organizations/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        ;
        verify(organizationService, times(1)).deleteOrganization(123);
    }

    //--- Cv tests

    @Test
    public void testCreateCv() throws Exception {
        String requestJson = "{\"cvName\": \"test-cv\", \"cvDescription\": \"test-desc\", \"cvGroupId\" : \"16\"}";

        when(
            cvService.createCv(any(CvDTO.class))
        ).thenReturn(
            new CvDTO()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/cvs")
            .contextPath("/gobii-dev")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;
    }

    @Test
    public void testUpdateCv() throws Exception {
        String requestJson = "{\"cvName\": \"updated-cv-name\"}";

        when(
            cvService.updateCv(eq(123), any(CvDTO.class))
        ).thenReturn(
            new CvDTO()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .patch("/gobii-dev/gobii/v3/cvs/123")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());

        verify(cvService, times(1)).updateCv(eq(123), any(CvDTO.class));
    }

    @Test
    public void testListCvsDefault()  throws Exception {

        when(
            cvService.getCvs(0, 1000, null, null)
        ).thenReturn(
            new PagedResult<CvDTO>()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/cvs")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());

        verify(cvService, times(1)).getCvs(0, 1000, null, null);

    }

    @Test
    public void testGetCvById() throws Exception {
        when(cvService.getCv(123)).thenReturn(new CvDTO());

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/cvs/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());

        verify(cvService, times(1)).getCv(123);
    }

    @Test
    public void testGetCvProps() throws Exception {

        when(cvService.getCvProperties(0, 1000)).thenReturn(new PagedResult<CvPropertyDTO>());
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/cvs/properties")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());

        verify(cvService, times(1)).getCvProperties(0, 1000);
    }

    @Test
    public void testAddCvProps() throws Exception {
        String requestJson = "{\"propertyName\": \"test-prop\", \"propertyDescription\": \"test-desc\"}";
        CvPropertyDTO mockDTO = new CvPropertyDTO();
        mockDTO.setPropertyGroupType(1);
        when(cvService.addCvProperty(any(CvPropertyDTO.class))).thenReturn(new CvPropertyDTO());

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/cvs/properties")
            .contextPath("/gobii-dev")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(cvService, times(1)).addCvProperty(any(CvPropertyDTO.class));
    }


    @Test
    public void testDeleteCv() throws Exception {
        doNothing().when(cvService).deleteCv(123);

        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/cvs/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(cvService, times(1)).deleteCv(123);
    }

    //--Platforms
    @Test
    public void testCreatePlatform() throws Exception {
        String requestJson = "{\"platformName\": \"test-platform-name\", \"platformTypeId\": \"7\"}";

        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn("test-user");

        when(
            platformService.createPlatform(any(PlatformDTO.class), eq("test-user"))
        ).thenReturn(
            new PlatformDTO()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/platforms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;

        verify(platformService, times(1)).createPlatform(any(PlatformDTO.class), eq("test-user"));

    }

    @Test
    public void testListPlatforms() throws Exception {
        when(
            platformService.getPlatforms(0, 1000, null)
        ).thenReturn( new PagedResult<PlatformDTO>());

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/platforms")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(platformService, times(1)).getPlatforms(0, 1000, null);
    }

    @Test
    public void testGetPlatform() throws Exception {
        when(
            platformService.getPlatform(123)
        ).thenReturn( new PlatformDTO());

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/platforms/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(platformService, times(1)).getPlatform(123);

    }

    @Test
    public void testUpdatePlatform() throws Exception {
        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn("test-user");

        when(
            platformService.updatePlatform(eq(123), any(PlatformDTO.class), eq("test-user"))
        ).thenReturn(new PlatformDTO());

        String requestJson = "{\"platformName\": \"updated-platform-name\", \"platformTypeId\": \"12\"}";

        mockMvc.perform(
            MockMvcRequestBuilders
            .patch("/gobii-dev/gobii/v3/platforms/123")
            .contextPath("/gobii-dev")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(platformService, times(1)).updatePlatform(eq(123), any(PlatformDTO.class), eq("test-user"));

    }

    @Test
    public void testDeletePlatform() throws Exception {
        doNothing().when(platformService).deletePlatform(123);

        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/platforms/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(platformService, times(1)).deletePlatform(123);
    }

    @Test
    public void testCreatePlatformType() throws Exception {
        String requestJson = "{\"typeName\": \"new-platform-type\", \"typeDescription\": \"12\"}";

        when(platformService.createPlatformType(any(CvTypeDTO.class))).thenReturn(new CvTypeDTO());

        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/platforms/types")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated());
            
        verify(platformService, times(1)).createPlatformType(any(CvTypeDTO.class));
    }

    @Test
    public void testListPlatformTypes() throws Exception {
        when(platformService.getPlatformTypes(0,1000)).thenReturn(new PagedResult<CvTypeDTO>());

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/platforms/types")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());

        verify(platformService, times(1)).getPlatformTypes(0, 1000);
    }

    // -- References
    @Test
    public void testCreateGenomeReference() throws Exception {
        when(
            referenceService.createReference(any(ReferenceDTO.class), eq("test-user"))
        ).thenReturn( new ReferenceDTO());

        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn("test-user");
        
        String requestJson = "{\"referenceName\": \"test-ref\", \"version\": \"vtest\"}";
        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/references")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;

        verify(referenceService, times(1)).createReference(any(ReferenceDTO.class), eq("test-user"));
    }

    @Test
    public void testGetReference() throws Exception {
        when(
            referenceService.getReference(123)
        ).thenReturn(new ReferenceDTO());

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/references/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(referenceService, times(1)).getReference(123);
    }

    @Test
    public void testUpdateReference() throws Exception {
        when(
            projectService.getDefaultProjectEditor()
        ).thenReturn("test-user");

        when(
            referenceService.updateReference(eq(123), any(ReferenceDTO.class), eq("test-user"))
        ).thenReturn(new ReferenceDTO());

        String requestJson = "{\"referenceName\": \"test-ref-update\", \"version\": \"vtest1\"}";
        mockMvc.perform(
            MockMvcRequestBuilders
            .patch("/gobii-dev/gobii/v3/references/123")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(referenceService, times(1)).updateReference(eq(123), any(ReferenceDTO.class), eq("test-user"));
    }

    @Test
    public void testDeleteReference() throws Exception {
        doNothing().when(referenceService).deleteReference(123);

        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/references/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        ;

        verify(referenceService, times(1)).deleteReference(123);
    }

    @Test
    public void testCreateMarkerSet() throws Exception {
        String requestJson = "{\"markerGroupName\": \"test-marker-group\", \"germplasmGroup\": \"test-germplasm-group\"}";

        when( projectService.getDefaultProjectEditor() ).thenReturn("test-user");
        when( markerGroupService.createMarkerGroup( any(MarkerGroupDTO.class), eq("test-user"))).thenReturn(new MarkerGroupDTO());
        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/markergroups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        ;

        verify(markerGroupService, times(1)).createMarkerGroup(any(MarkerGroupDTO.class), eq("test-user"));
    }

    @Test
    public void testListMarkerGroups() throws Exception {

        when( markerGroupService.getMarkerGroups(0, 1000)).thenReturn(
            new PagedResult<MarkerGroupDTO>()
        );

        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/markergroups")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify( markerGroupService, times(1)).getMarkerGroups(0, 1000);
    }

    @Test
    public void testGetMarkerGroupById() throws Exception {
        when( markerGroupService.getMarkerGroup(123)).thenReturn(new MarkerGroupDTO());
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/markergroups/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(markerGroupService, times(1)).getMarkerGroup(123);
    }

    @Test
    public void testEditMarkerGroup()  throws Exception {
        String requestJson = "{\"markerGroupName\": \"test-marker-group-updated\", \"germplasmGroup\": \"test-germplasm-group\"}";
        when( projectService.getDefaultProjectEditor()).thenReturn("test-user");
        when( markerGroupService.updateMarkerGroup(eq(123), any(MarkerGroupDTO.class), eq("test-user"))).thenReturn(new MarkerGroupDTO());

        mockMvc.perform(
            MockMvcRequestBuilders
            .patch("/gobii-dev/gobii/v3/markergroups/123")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        ;
        verify(markerGroupService, times(1)).updateMarkerGroup(eq(123), any(MarkerGroupDTO.class), eq("test-user"));

    }

    @Test
    public void testDeleteMarkerGroup() throws Exception {
        doNothing().when(markerGroupService).deleteMarkerGroup(123);

        mockMvc.perform(
            MockMvcRequestBuilders
            .delete("/gobii-dev/gobii/v3/markergroups/123")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(markerGroupService, times(1)).deleteMarkerGroup(123);
    }

    @Test
    public void testAssignMarkersToMarkerGroup() throws Exception {
        String requestJson = "[{\"markerName\": \"foo marker\", \"platformName\": \"foo platform\", \"favorableAlleles\": [\"A\"]},{\"markerName\": \"bar marker\",\"platformName\": \"bar platform\", \"favorableAlleles\": [\"G\"]}]";
        when (projectService.getDefaultProjectEditor()).thenReturn("test-user");
        when (markerGroupService.mapMarkers(eq(123), anyListOf(MarkerDTO.class), eq("test-user"))).thenReturn(new PagedResult<MarkerDTO>());
        mockMvc.perform(
            MockMvcRequestBuilders
            .post("/gobii-dev/gobii/v3/markergroups/123/markerscollection")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON)
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());

        verify(markerGroupService, times(1)).mapMarkers(eq(123), anyListOf(MarkerDTO.class), eq("test-user"));
    }

    @Test
    public void testListMarkersInMarkerGroups() throws Exception {
        when (markerGroupService.getMarkerGroupMarkers(123, 0, 1000)).thenReturn(new PagedResult<>());
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/markergroups/123/markers")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());
        verify(markerGroupService, times(1)).getMarkerGroupMarkers(123, 0,1000);
    }

    @Test
    public void listCvGroups() throws Exception {
        when(cvService.getCvGroups(0, 1000)).thenReturn(new PagedResult<>());
        mockMvc.perform(
            MockMvcRequestBuilders
            .get("/gobii-dev/gobii/v3/cvs/groups")
            .contextPath("/gobii-dev")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());
        verify(cvService, times(1)).getCvGroups(0, 1000);
    }
}