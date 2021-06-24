package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.gobiiproject.gobiidomain.services.gdmv3.CvService;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.CvsController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
@PrepareForTest(CropRequestAnalyzer.class)
@ContextConfiguration(classes = GOBIIControllerV3TestConfiguration.class
// locations = { "classpath:/spring/application-config.xml" }
)
@WebAppConfiguration
@Slf4j
public class CvsControllerTest {
    
    @Mock
    private CvService cvService;

    private CvsController cvsController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Mapsets Controller test");
        MockitoAnnotations.initMocks(this);

        this.cvsController = new CvsController( cvService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(cvsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

    }
    @Test
    public void testCreateCv() throws Exception {
        final String requestJson = "{\"cvName\": \"test-cv\", \"cvDescription\": \"test-desc\", \"cvGroupId\" : \"16\"}";

        when(cvService.createCv(any(CvDTO.class))).thenReturn(new CvDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/cvs").contextPath("/gdm")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson)).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUpdateCv() throws Exception {
        final String requestJson = "{\"cvName\": \"updated-cv-name\"}";

        when(cvService.updateCv(eq(123), any(CvDTO.class))).thenReturn(new CvDTO());

        mockMvc.perform(MockMvcRequestBuilders.patch("/gdm/crops/dev/gobii/v3/cvs/123").content(requestJson)
                .contentType(MediaType.APPLICATION_JSON).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(cvService, times(1)).updateCv(eq(123), any(CvDTO.class));
    }

    @Test
    public void testListCvsDefault() throws Exception {

        when(cvService.getCvs(0, 1000, null, null)).thenReturn(new PagedResult<CvDTO>());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/cvs").contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(cvService, times(1)).getCvs(0, 1000, null, null);

    }

    @Test
    public void testGetCvById() throws Exception {
        when(cvService.getCv(123)).thenReturn(new CvDTO());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/cvs/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(cvService, times(1)).getCv(123);
    }


    @Test
    public void testDeleteCv() throws Exception {
        doNothing().when(cvService).deleteCv(123);

        mockMvc.perform(MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/cvs/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(cvService, times(1)).deleteCv(123);
    }


    @Test
    public void listCvGroups() throws Exception {
        when(cvService.getCvGroups(0, 1000, "system_defined")).thenReturn(new PagedResult<>());
        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/cvs/groups").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());
        verify(cvService, times(1)).getCvGroups(0, 1000, "system_defined");
    }


}
