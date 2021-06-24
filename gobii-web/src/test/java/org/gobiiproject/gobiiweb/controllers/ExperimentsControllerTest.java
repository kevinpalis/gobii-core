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

import org.gobiiproject.gobiidomain.services.gdmv3.ExperimentService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.ExperimentDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.ExperimentsController;
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
public class ExperimentsControllerTest {
    
    @Mock
    private ExperimentService experimentService;

    private ExperimentsController experimentsController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Experiments Controller test");
        MockitoAnnotations.initMocks(this);

        this.experimentsController = new ExperimentsController( experimentService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(experimentsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

        // assert this.projectsController.getProjectService() != null.

    }

    @Test
    public void testGetExperimentsSimple() throws Exception {
        List<ExperimentDTO> mockList = new ArrayList<>();
        ExperimentDTO mockItem = new ExperimentDTO();

        mockList.add(mockItem);
        PagedResult<ExperimentDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);
        when(experimentService.getExperiments(0, 1000, null)).thenReturn(mockPayload);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/experiments").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
                .andExpect(jsonPath("$.result.data[0].experimentId").value(mockItem.getExperimentId()));
        verify(experimentService, times(1)).getExperiments(0, 1000, null);
    }

    @Test
    public void testGetExperimentById() throws Exception {
        ExperimentDTO mockItem = new ExperimentDTO();
        when(experimentService.getExperiment(123)).thenReturn(mockItem);
        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/experiments/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.experimentName").hasJsonPath());
        verify(experimentService, times(1)).getExperiment(123);

    }

    @Test
    public void testGetExperimentById404() throws Exception {
        when(experimentService.getExperiment(123)).thenThrow(new NullPointerException());
        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/experiments/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNotFound());
        verify(experimentService, times(1)).getExperiment(123);

    }

    @Test
    public void testCreateExperimentsSimple() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        String jsonRequest = "{\"projectId\" : \"7\", \"experimentName\" : \"fooExperiment\", \"vendorProtocolId\" : \"4\"}";
        when(experimentService.createExperiment(any(ExperimentDTO.class), eq("test-user")))
                .thenReturn(new ExperimentDTO());
        when(AuthUtils.getCurrentUser()) 
            .thenReturn("test-user");

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/experiments")
                .contentType(MediaType.APPLICATION_JSON).content(jsonRequest).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.metadata").doesNotExist());
        verify(experimentService, times(1)).createExperiment(any(ExperimentDTO.class), eq("test-user"));
    }

    @Test
    public void testUpdateExperimentSimple() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        String jsonRequest = "{\"projectId\" : \"7\", \"experimentName\" : \"fooExperiment\", \"vendorProtocolId\" : \"4\"}";
        when(experimentService.updateExperiment(eq(123), any(ExperimentDTO.class), eq("test-user")))
                .thenReturn(new ExperimentDTO());
        when(
            AuthUtils.getCurrentUser() 
        ).thenReturn("test-user");

        mockMvc.perform(MockMvcRequestBuilders.patch("/gdm/crops/dev/gobii/v3/experiments/123")
                .contentType(MediaType.APPLICATION_JSON).content(jsonRequest).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(experimentService, times(1)).updateExperiment(eq(123), any(ExperimentDTO.class), eq("test-user"));
    }

    @Test
    public void testDeleteExperiment() throws Exception {

        doNothing().when(experimentService).deleteExperiment(eq(123));

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/experiments/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(experimentService, times(1)).deleteExperiment(eq(123));
    }

    @Test
    public void testDeleteExperimentException409() throws Exception {
        Exception exc = new GobiiException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.FOREIGN_KEY_VIOLATION,
                "test");
        doThrow(exc).when(experimentService).deleteExperiment(eq(123));

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/experiments/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().is(409))
                .andExpect(jsonPath("$.error").value(StringContains.containsString("test")));
        verify(experimentService, times(1)).deleteExperiment(eq(123));
    }

}
