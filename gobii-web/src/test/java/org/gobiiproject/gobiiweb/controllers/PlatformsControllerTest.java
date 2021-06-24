package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.gobiiproject.gobiidomain.services.gdmv3.PlatformService;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.PlatformDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.PlatformsController;
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
public class PlatformsControllerTest {
    @Mock
    private PlatformService platformService;

    private PlatformsController platformsController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Platforms Controller test");
        MockitoAnnotations.initMocks(this);

        this.platformsController = new PlatformsController( platformService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(platformsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

        // assert this.projectsController.getProjectService() != null.

    }

    @Test
    public void testCreatePlatform() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        final String requestJson = "{\"platformName\": \"test-platform-name\", \"platformTypeId\": \"7\"}";

        when(AuthUtils.getCurrentUser()).thenReturn("test-user");

        when(platformService.createPlatform(any(PlatformDTO.class), eq("test-user"))).thenReturn(new PlatformDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/platforms")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(platformService, times(1)).createPlatform(any(PlatformDTO.class), eq("test-user"));

    }

    @Test
    public void testListPlatforms() throws Exception {
        when(platformService.getPlatforms(0, 1000, null)).thenReturn(new PagedResult<PlatformDTO>());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/platforms").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(platformService, times(1)).getPlatforms(0, 1000, null);
    }

    @Test
    public void testGetPlatform() throws Exception {
        when(platformService.getPlatform(123)).thenReturn(new PlatformDTO());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/platforms/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(platformService, times(1)).getPlatform(123);

    }

    @Test
    public void testUpdatePlatform() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        when(AuthUtils.getCurrentUser()).thenReturn("test-user");

        when(platformService.updatePlatform(eq(123), any(PlatformDTO.class), eq("test-user")))
                .thenReturn(new PlatformDTO());

        final String requestJson = "{\"platformName\": \"updated-platform-name\", \"platformTypeId\": \"12\"}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/gdm/crops/dev/gobii/v3/platforms/123").contextPath("/gdm")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(platformService, times(1)).updatePlatform(eq(123), any(PlatformDTO.class), eq("test-user"));

    }

    @Test
    public void testDeletePlatform() throws Exception {
        doNothing().when(platformService).deletePlatform(123);

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/platforms/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(platformService, times(1)).deletePlatform(123);
    }

    @Test
    public void testCreatePlatformType() throws Exception {
        final String requestJson = "{\"typeName\": \"new-platform-type\", \"typeDescription\": \"12\"}";

        when(platformService.createPlatformType(any(CvTypeDTO.class))).thenReturn(new CvTypeDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/platforms/types")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(platformService, times(1)).createPlatformType(any(CvTypeDTO.class));
    }

    @Test
    public void testListPlatformTypes() throws Exception {
        when(platformService.getPlatformTypes(0, 1000)).thenReturn(new PagedResult<CvTypeDTO>());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/platforms/types").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(platformService, times(1)).getPlatformTypes(0, 1000);
    }

}
