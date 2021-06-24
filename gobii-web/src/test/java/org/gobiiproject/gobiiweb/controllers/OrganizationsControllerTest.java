package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.gobiiproject.gobiidomain.services.gdmv3.OrganizationService;
import org.gobiiproject.gobiimodel.dto.gdmv3.OrganizationDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.OrganizationsController;
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
public class OrganizationsControllerTest {

    @Mock
    private OrganizationService organizationService;

    private OrganizationsController organizationsController;
    
    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Organizations Controller test");
        MockitoAnnotations.initMocks(this);

        this.organizationsController = new OrganizationsController( organizationService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(organizationsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

    }
    @Test
    public void getOrganizationsList() throws Exception {
        when(organizationService.getOrganizations(0, 1000)).thenReturn(new PagedResult<OrganizationDTO>());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/organizations").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());
        verify(organizationService, times(1)).getOrganizations(0, 1000);
    }

    @Test
    public void getOrganizationById() throws Exception {
        final Integer organizationId = 122;
        when(organizationService.getOrganization(organizationId)).thenReturn(new OrganizationDTO());

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/organizations/122").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(organizationService, times(1)).getOrganization(122);
    }

    @Test
    public void testCreateOrganization() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        final String requestJson = "{\"organizationName\": \"test-org\", \"organizationAddress\": \"organization-address\", \"organizationWebsite\": \"https://website.com\"}";

        when(organizationService.createOrganization(any(OrganizationDTO.class), eq("test-user")))
                .thenReturn(new OrganizationDTO());

        when(AuthUtils.getCurrentUser()).thenReturn("test-user");

        mockMvc.perform(MockMvcRequestBuilders.post("/gdm/crops/dev/gobii/v3/organizations")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(organizationService, times(1)).createOrganization(any(OrganizationDTO.class), eq("test-user"));
    }

    @Test
    public void testUpdateOrganization() throws Exception {
        PowerMockito.mockStatic(AuthUtils.class);
        final String requestJson = "{\"organizationName\": \"test-org\", \"organizationAddress\": \"organization-address\", \"organizationWebsite\": \"https://website.com\"}";

        when(organizationService.updateOrganization(eq(123), any(OrganizationDTO.class), eq("test-user")))
                .thenReturn(new OrganizationDTO());

        when(AuthUtils.getCurrentUser()).thenReturn("test-user");

        mockMvc.perform(MockMvcRequestBuilders.patch("/gdm/crops/dev/gobii/v3/organizations/123")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson).contextPath("/gdm")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(organizationService, times(1)).updateOrganization(eq(123), any(OrganizationDTO.class), eq("test-user"));
    }

    @Test
    public void testDeleteOrganization() throws Exception {
        doNothing().when(organizationService).deleteOrganization(123);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/gdm/crops/dev/gobii/v3/organizations/123").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(organizationService, times(1)).deleteOrganization(123);
    }
}
