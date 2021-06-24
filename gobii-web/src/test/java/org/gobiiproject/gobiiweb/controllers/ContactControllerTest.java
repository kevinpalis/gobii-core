package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import org.gobiiproject.gobiidomain.services.gdmv3.ContactService;
import org.gobiiproject.gobiimodel.config.Roles;
import org.gobiiproject.gobiimodel.dto.gdmv3.ContactDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.ContactsController;
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
@PrepareForTest(CropRequestAnalyzer.class)
@ContextConfiguration(classes = GOBIIControllerV3TestConfiguration.class
// locations = { "classpath:/spring/application-config.xml" }
)
@WebAppConfiguration
@Slf4j
public class ContactControllerTest {

    @Mock
    private ContactService contactService;

    private ContactsController contactsController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Controller test");
        MockitoAnnotations.initMocks(this);

        this.contactsController = new ContactsController( contactService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(contactsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

        // assert this.projectsController.getProjectService() != null.

    }

    @Test
    public void testGetContacts() throws Exception {
        assert contactService != null;
        PowerMockito.mockStatic(CropRequestAnalyzer.class);

        // CropRequestAnalyzer.getGobiiCropType();

        when(CropRequestAnalyzer.getGobiiCropType()).thenReturn("dev");

        List<ContactDTO> mockList = new ArrayList<ContactDTO>();
        ContactDTO mockItem = new ContactDTO();
        mockItem.setPiContactId("111");
        mockItem.setPiContactFirstName("test");
        mockList.add(mockItem);
        PagedResult<ContactDTO> mockPayload = new PagedResult<>();
        mockPayload.setResult(mockList);
        mockPayload.setCurrentPageNum(0);
        mockPayload.setCurrentPageSize(1);

        // use the spy
        // doReturn("dev").when(gobiiControllerV3).getCropType();

        when(contactService.getUsers("dev", Roles.PI, 0, 1000, null)).thenReturn(mockPayload);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/contacts").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
                .andExpect(jsonPath("$.result.data[0].piContactId").value(mockItem.getPiContactId()))
                .andExpect(jsonPath("$.result.data[0].piContactFirstName").value(mockItem.getPiContactFirstName()));
        verify(contactService, times(1)).getUsers("dev", Roles.PI, 0, 1000, null);
    }
}
