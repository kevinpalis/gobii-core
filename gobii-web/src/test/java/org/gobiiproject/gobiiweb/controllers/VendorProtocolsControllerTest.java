package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.gobiiproject.gobiidomain.services.gdmv3.VendorProtocolService;
import org.gobiiproject.gobiimodel.dto.gdmv3.VendorProtocolDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.VendorProtocolsController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
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
public class VendorProtocolsControllerTest {
    @Mock
    private VendorProtocolService vendorProtocolService;

    private VendorProtocolsController vendorProtocolsController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 VendorProtocol Controller test");
        MockitoAnnotations.initMocks(this);

        this.vendorProtocolsController = new VendorProtocolsController( vendorProtocolService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(vendorProtocolsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

        // assert this.projectsController.getProjectService() != null.

    }

    @Test
    public void testGetVendorProtocols() throws Exception {
        when(vendorProtocolService.getVendorProtocols(0, 1000)).thenReturn(new PagedResult<VendorProtocolDTO>());
        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/vendorprotocols").contextPath("/gdm"))
            .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(vendorProtocolService, times(1)).getVendorProtocols(0, 1000);

    }

}

