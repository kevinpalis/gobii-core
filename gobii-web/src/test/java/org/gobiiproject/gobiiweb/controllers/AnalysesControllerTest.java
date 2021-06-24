package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.gobiiproject.gobiidomain.services.gdmv3.AnalysisService;
import org.gobiiproject.gobiimodel.dto.gdmv3.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AnalysesController;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
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
public class AnalysesControllerTest {
    
    @Mock
    private AnalysisService analysisService;

    private AnalysesController analysesController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Analysis Controller test");
        MockitoAnnotations.initMocks(this);

        this.analysesController = new AnalysesController( analysisService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(analysesController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

    }

    @Test
    public void testListAnalysis() throws Exception {
        AnalysisDTO mockAnalysisDTO = new AnalysisDTO();
        List<AnalysisDTO> mockList = new ArrayList<>();
        mockList.add(mockAnalysisDTO);
        PagedResult<AnalysisDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);

        when(analysisService.getAnalyses(eq(0), eq(1000))).thenReturn(mockPayload);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/analyses").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());
        verify(analysisService, times(1)).getAnalyses(eq(0), eq(1000));
    }

    @Test
    public void testCreateAnalysisSimple() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        String jsonRequest = "{\"analysisName\": \"test-name\", \"analysisTypeId\": \"93\" }";
        when(analysisService.createAnalysis(any(AnalysisDTO.class), any(String.class))).thenReturn(new AnalysisDTO());

        when( AuthUtils.getCurrentUser()
        ).thenReturn("test-user");

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/analyses")
                .contentType(MediaType.APPLICATION_JSON).content(jsonRequest).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
        verify(analysisService, times(1)).createAnalysis(any(AnalysisDTO.class), eq("test-user"));
    }

    @Test
    public void testCreateAnalysisType() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        String jsonRequest = "{\"analysisTypeName\" : \"testType\"}";

        when(analysisService.createAnalysisType(any(CvTypeDTO.class), any(String.class))).thenReturn(new CvTypeDTO());

        when( AuthUtils.getCurrentUser()
        ).thenReturn("test-user");

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/analyses/types")
                .contentType(MediaType.APPLICATION_JSON).content(jsonRequest).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    public void testListAnalysisTypes() throws Exception {
        List<CvTypeDTO> analysisTypes = new ArrayList<>();
        Integer page = 0;
        Integer pageSize = 1000;
        PagedResult<CvTypeDTO> result = new PagedResult<>();
        result.setResult(analysisTypes);

        when(analysisService.getAnalysisTypes(page, pageSize)).thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/analyses/types").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(analysisService, times(1)).getAnalysisTypes(0, 1000);
    }

    @Test
    public void testPatchAnalysisById() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        AnalysisDTO patchData = new AnalysisDTO();
        patchData.setAnalysisName("New Name");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(patchData);
        Integer testId = 123;

        AnalysisDTO mockDTO = new AnalysisDTO();

        when(analysisService.updateAnalysis(eq(testId), any(AnalysisDTO.class), eq("test-user"))).thenReturn(mockDTO);

        when( AuthUtils.getCurrentUser()
        ).thenReturn("test-user");

        mockMvc.perform(MockMvcRequestBuilders.patch("/gdm/crops/dev/gobii/v3/analyses/123")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(analysisService, times(1)).updateAnalysis(eq(testId), any(AnalysisDTO.class), eq("test-user"));
    }

    @Test
    public void getAnalysisByIdTest() throws Exception {
        AnalysisDTO mockDTO = new AnalysisDTO();
        Integer mockId = 123;
        when(analysisService.getAnalysis(mockId)).thenReturn(mockDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/analyses/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(analysisService, times(1)).getAnalysis(eq(123));

    }

    @Test
    public void deleteAnalysisByIdTest() throws Exception {
        Integer analysisId = 123;
        doNothing().when(analysisService).deleteAnalysis(analysisId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/analyses/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNoContent());
        ;
        verify(analysisService, times(1)).deleteAnalysis(123);

    }

}
