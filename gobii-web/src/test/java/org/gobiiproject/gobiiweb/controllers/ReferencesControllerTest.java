package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.gobiiproject.gobiidomain.services.gdmv3.ReferenceService;
import org.gobiiproject.gobiimodel.dto.gdmv3.ReferenceDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.ReferencesController;
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
public class ReferencesControllerTest {

    @Mock
    private ReferenceService referenceService;

    private ReferencesController referencesController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 References Controller test");
        MockitoAnnotations.initMocks(this);

        this.referencesController = new ReferencesController( referenceService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(referencesController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

        // assert this.projectsController.getProjectService() != null.

    }

    @Test
    public void testGetReferences() throws Exception {
        when(referenceService.getReferences(0, 1000)).thenReturn(new PagedResult<ReferenceDTO>());
        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/references").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(referenceService, times(1)).getReferences(0, 1000);

    }

    @Test
    public void testCreateGenomeReference() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        when(AuthUtils.getCurrentUser()).thenReturn("test-user");
        when(referenceService.createReference(any(ReferenceDTO.class), eq("test-user"))).thenReturn(new ReferenceDTO());

        final String requestJson = "{\"referenceName\": \"test-ref\", \"version\": \"vtest\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/references")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(referenceService, times(1)).createReference(any(ReferenceDTO.class), eq("test-user"));
    }

    @Test
    public void testGetReference() throws Exception {
        when(referenceService.getReference(123)).thenReturn(new ReferenceDTO());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/references/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(referenceService, times(1)).getReference(123);
    }

    @Test
    public void testUpdateReference() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        when(AuthUtils.getCurrentUser()).thenReturn("test-user");

        when(referenceService.updateReference(eq(123), any(ReferenceDTO.class), eq("test-user")))
                .thenReturn(new ReferenceDTO());

        final String requestJson = "{\"referenceName\": \"test-ref-update\", \"version\": \"vtest1\"}";
        mockMvc.perform(MockMvcRequestBuilders.patch("/gdm/crops/dev/gobii/v3/references/123")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(referenceService, times(1)).updateReference(eq(123), any(ReferenceDTO.class), eq("test-user"));
    }

    @Test
    public void testDeleteReference() throws Exception {
        doNothing().when(referenceService).deleteReference(123);

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/references/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(referenceService, times(1)).deleteReference(123);
    }
}
