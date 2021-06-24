package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.gobiiproject.gobiidomain.services.gdmv3.MapsetService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.MapsetsController;
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
public class MapsetsControllerTest {
    
    @Mock
    private MapsetService mapsetService;

    private MapsetsController mapsetsController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Mapsets Controller test");
        MockitoAnnotations.initMocks(this);

        this.mapsetsController = new MapsetsController( mapsetService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(mapsetsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

    }
    
    @Test
    public void testGetMapsets() throws Exception {
        when(mapsetService.getMapsets(0, 1000, null)).thenReturn(new PagedResult<MapsetDTO>());
        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/mapsets").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(mapsetService, times(1)).getMapsets(0, 1000, null);
    }

    @Test
    public void testCreateSimpleMapset() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        String requestJson = "{\"mapsetName\": \"test-mapset\", \"mapsetDescription\": \"test-description\", \"mapsetTypeId\": \"10\", \"referenceId\": \"12\"}";

        when(AuthUtils.getCurrentUser()).thenReturn("test-user");

        when(mapsetService.createMapset(any(MapsetDTO.class), eq("test-user"))).thenReturn(new MapsetDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/mapsets")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(mapsetService, times(1)).createMapset(any(MapsetDTO.class), eq("test-user"));
    }

    @Test
    public void testCreateSimpleMapsetMissingMapsetTypeId() throws Exception {
        String requestJson = "{\"mapsetName\": \"test-mapset\", \"mapsetDescription\": \"test-description\", \"referenceId\": \"12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/mapsets")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetMapsetById() throws Exception {
        when(mapsetService.getMapset(122)).thenReturn(new MapsetDTO());
        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/mapsets/122").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());
        verify(mapsetService, times(1)).getMapset(122);
    }

    @Test
    public void testGetMapsetByIdNotFound() throws Exception {
        when(mapsetService.getMapset(122)).thenThrow(new GobiiException(GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST, "Mapset not found"));
        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/mapsets/122").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNotFound());
        verify(mapsetService, times(1)).getMapset(122);
    }

    @Test
    public void testPatchSimpleMapset() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        String requestJson = "{\"mapsetName\": \"test-mapset\", \"mapsetDescription\": \"test-description\"}";

        when(AuthUtils.getCurrentUser()).thenReturn("test-user");

        when(mapsetService.updateMapset(eq(122), any(MapsetDTO.class), eq("test-user"))).thenReturn(new MapsetDTO());

        mockMvc.perform(MockMvcRequestBuilders.patch("/gdm/crops/dev/gobii/v3/mapsets/122")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(mapsetService, times(1)).updateMapset(eq(122), any(MapsetDTO.class), eq("test-user"));
    }

    @Test
    public void testDeleteMapset() throws Exception {
        Integer mapsetId = 122;
        doNothing().when(mapsetService).deleteMapset(mapsetId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/mapsets/122").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(mapsetService, times(1)).deleteMapset(122);
    }

    @Test
    public void createMapsetTypeTest() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        String requestJson = "{\"typeName\": \"test-name\", \"typeDescription\": \"test-desc\"}";
        when(mapsetService.createMapsetType("test-name", "test-desc", "user")).thenReturn(new CvTypeDTO());

        when(AuthUtils.getCurrentUser()).thenReturn("test-user");

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/mapsets/types")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(mapsetService, times(1)).createMapsetType("test-name", "test-desc", "test-user");
    }

    @Test
    public void getMapsetTypes() throws Exception {
        when(mapsetService.getMapsetTypes(0, 1000)).thenReturn(new PagedResult<CvTypeDTO>());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/mapsets/types").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());
        verify(mapsetService, times(1)).getMapsetTypes(0, 1000);
    }
}
