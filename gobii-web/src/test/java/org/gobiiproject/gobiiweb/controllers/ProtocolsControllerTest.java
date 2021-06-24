package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.gobiiproject.gobiidomain.services.gdmv3.ProtocolService;
import org.gobiiproject.gobiimodel.dto.gdmv3.ProtocolDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.ProtocolsController;
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
public class ProtocolsControllerTest {
    @Mock
    private ProtocolService protocolService;

    private ProtocolsController protocolsController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Protocols Controller test");
        MockitoAnnotations.initMocks(this);

        this.protocolsController = new ProtocolsController( protocolService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(protocolsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

        // assert this.projectsController.getProjectService() != null.

    }

    @Test
    public void testGetProtocols() throws Exception {
        when(protocolService.getProtocols(1000, 0, null)).thenReturn(new PagedResult<ProtocolDTO>());
        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/protocols").contextPath("/gdm"))
            .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        verify(protocolService, times(1)).getProtocols(1000, 0, null);

    }
}
