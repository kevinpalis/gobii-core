package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.gobiiproject.gobiidomain.services.gdmv3.DatasetService;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetRequestDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.DatasetsController;
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
public class DatasetsControllerTest {

    @Mock
    private DatasetService datasetService;

    private DatasetsController datasetsController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Datasets Controller test");
        MockitoAnnotations.initMocks(this);

        this.datasetsController = new DatasetsController( datasetService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(datasetsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

    }
    @Test
    public void createDatasetSimpleTest() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetName("test-name");
        request.setExperimentId(1);
        request.setCallingAnalysisId(1);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);

        when(AuthUtils.getCurrentUser()).thenReturn("test-user");
        when(datasetService.createDataset(any(DatasetRequestDTO.class), eq("test-user"))).thenReturn(new DatasetDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/datasets").contextPath("/gdm")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
        verify(datasetService, times(1)).createDataset(any(DatasetRequestDTO.class), eq("test-user"));

    }

    @Test
    public void testCreateDatasetWithAnalysisIds() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        String requestJson = "{\"datasetName\" : \"test-name\", \"experimentId\" : \"1\", \"callingAnalysisId\" : \"1\", \"analysisIds\" : [ \"2\", \"3\", \"4\" ]}";

        when(AuthUtils.getCurrentUser()).thenReturn("test-user");

        when(datasetService.createDataset(any(DatasetRequestDTO.class), eq("test-user"))).thenReturn(new DatasetDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/datasets").contextPath("/gdm")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).characterEncoding("UTF-8")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(datasetService, times(1)).createDataset(any(DatasetRequestDTO.class), eq("test-user"));
    }

    @Test
    public void testDatasetListing() throws Exception {
        when(datasetService.getDatasets(0, 1000, null, null)).thenReturn(new PagedResult<DatasetDTO>());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/datasets").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(datasetService, times(1)).getDatasets(0, 1000, null, null);

    }

    @Test
    public void testDatasetListingWithQuery() throws Exception {
        when(datasetService.getDatasets(5, 100, 1, 2)).thenReturn(new PagedResult<DatasetDTO>());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/gdm/crops/dev/gobii/v3/datasets?page=5&pageSize=100&experimentId=1&datasetTypeId=2")
                .contextPath("/gdm")).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(datasetService, times(1)).getDatasets(5, 100, 1, 2);

    }

    @Test
    public void testDatasetGetById() throws Exception {
        Integer target = 112;
        when(datasetService.getDataset(target)).thenReturn(new DatasetDTO());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/datasets/112").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(datasetService, times(1)).getDataset(target);
    }

    @Test
    public void testDatasetByIdUpdate() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        Integer target = 112;
        when(datasetService.updateDataset(eq(target), any(DatasetRequestDTO.class), eq("test-user")))
                .thenReturn(new DatasetDTO());

        when(AuthUtils.getCurrentUser()).thenReturn("test-user");
        String requestJson = "{\"datasetName\" : \"test-name-edited\", \"experimentId\" : \"1\", \"callingAnalysisId\" : \"1\", \"analysisIds\" : [ \"2\", \"3\", \"4\" ]}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/gdm/crops/dev/gobii/v3/datasets/112")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDatasetDeleteById() throws Exception {
        Integer datasetId = 112;
        doNothing().when(datasetService).deleteDataset(datasetId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/datasets/112").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(datasetService, times(1)).deleteDataset(datasetId);
    }

    @Test
    public void testListDatasetTypes() throws Exception {

        when(datasetService.getDatasetTypes(0, 1000)).thenReturn(new PagedResult<CvTypeDTO>());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/datasets/types").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testAddDatasetType() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        when(datasetService.createDatasetType("test-name", "datasetTypeDescription", "user"))
                .thenReturn(new CvTypeDTO());

        when(AuthUtils.getCurrentUser()).thenReturn("test-user");

        String requestJson = "{\"typeName\" : \"test-name\", \"typeDescription\": \"datasetTypeDescription\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/datasets/types").contextPath("/gdm")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
 
}
