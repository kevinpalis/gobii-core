package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyListOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.gobiiproject.gobiidomain.services.gdmv3.MarkerGroupService;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerGroupDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.MarkerGroupsController;
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
public class MarkerGroupsControllerTest {

    @Mock
    private MarkerGroupService markerGroupService;

    private MarkerGroupsController markerGroupsController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 MarkerGroups Controller test");
        MockitoAnnotations.initMocks(this);

        this.markerGroupsController = new MarkerGroupsController( markerGroupService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(markerGroupsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

        // assert this.projectsController.getProjectService() != null.

    }
    @Test
    public void testCreateMarkerSet() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        when(AuthUtils.getCurrentUser()).thenReturn("test-user");
        final String requestJson = "{\"markerGroupName\": \"test-marker-group\", \"germplasmGroup\": \"test-germplasm-group\"}";

        when(markerGroupService.createMarkerGroup(any(MarkerGroupDTO.class), eq("test-user")))
                .thenReturn(new MarkerGroupDTO());
        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/markergroups")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(markerGroupService, times(1)).createMarkerGroup(any(MarkerGroupDTO.class), eq("test-user"));
    }

    @Test
    public void testListMarkerGroups() throws Exception {

        when(markerGroupService.getMarkerGroups(0, 1000)).thenReturn(new PagedResult<MarkerGroupDTO>());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/markergroups").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(markerGroupService, times(1)).getMarkerGroups(0, 1000);
    }

    @Test
    public void testGetMarkerGroupById() throws Exception {
        when(markerGroupService.getMarkerGroup(123)).thenReturn(new MarkerGroupDTO());
        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/markergroups/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(markerGroupService, times(1)).getMarkerGroup(123);
    }

    @Test
    public void testEditMarkerGroup() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        when(AuthUtils.getCurrentUser()).thenReturn("test-user");
        final String requestJson = "{\"markerGroupName\": \"test-marker-group-updated\", \"germplasmGroup\": \"test-germplasm-group\"}";
        when(markerGroupService.updateMarkerGroup(eq(123), any(MarkerGroupDTO.class), eq("test-user")))
                .thenReturn(new MarkerGroupDTO());

        mockMvc.perform(MockMvcRequestBuilders.patch("/gdm/crops/dev/gobii/v3/markergroups/123").content(requestJson)
                .contentType(MediaType.APPLICATION_JSON).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(markerGroupService, times(1)).updateMarkerGroup(eq(123), any(MarkerGroupDTO.class), eq("test-user"));

    }

    @Test
    public void testDeleteMarkerGroup() throws Exception {
        doNothing().when(markerGroupService).deleteMarkerGroup(123);

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/markergroups/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(markerGroupService, times(1)).deleteMarkerGroup(123);
    }

    @Test
    public void testAssignMarkersToMarkerGroup() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        when(AuthUtils.getCurrentUser()).thenReturn("test-user");
        final String requestJson = "[{\"markerName\": \"foo marker\", \"platformName\": \"foo platform\", \"favorableAlleles\": [\"A\"]},{\"markerName\": \"bar marker\",\"platformName\": \"bar platform\", \"favorableAlleles\": [\"G\"]}]";

        when(markerGroupService.mapMarkers(eq(123), anyListOf(MarkerDTO.class), eq("test-user")))
                .thenReturn(new PagedResult<MarkerDTO>());
        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/markergroups/123/markerscollection")
                .content(requestJson).contentType(MediaType.APPLICATION_JSON).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(markerGroupService, times(1)).mapMarkers(eq(123), anyListOf(MarkerDTO.class), eq("test-user"));
    }

    @Test
    public void testListMarkersInMarkerGroups() throws Exception {
        when(markerGroupService.getMarkerGroupMarkers(123, 0, 1000)).thenReturn(new PagedResult<>());
        mockMvc.perform(
                MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/markergroups/123/markers").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());
        verify(markerGroupService, times(1)).getMarkerGroupMarkers(123, 0, 1000);
    }
}
