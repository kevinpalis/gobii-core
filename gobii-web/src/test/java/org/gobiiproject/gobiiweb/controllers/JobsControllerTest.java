package org.gobiiproject.gobiiweb.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.File;

import com.google.common.io.Files;

import org.gobiiproject.gobiidomain.services.gdmv3.JobService;
import org.gobiiproject.gobiiweb.CropRequestAnalyzer;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.AuthUtils;
import org.gobiiproject.gobiiweb.controllers.gdm.v3.JobsController;
import org.junit.After;
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
public class JobsControllerTest {
    
    @Mock
    private JobService jobService;

    private JobsController jobsController;

    private MockMvc mockMvc;

    private File tempDir;
    private File tempFile;

    @Before
    public void setup() throws Exception {
        log.info("Setting up Gobii V3 Mapsets Controller test");
        MockitoAnnotations.initMocks(this);

        this.jobsController = new JobsController( jobService );

        this.mockMvc = MockMvcBuilders.standaloneSetup(jobsController)
                .setControllerAdvice(new GlobalControllerExceptionHandler()).build();

        //set up temp files
        tempDir = Files.createTempDir();

        tempFile = new File(tempDir, "test.txt");
        Files.touch(tempFile);
    }

    @After
    public void tearDown() throws Exception {
        tempFile.delete();
        tempDir.delete();
    }

    @Test
    public void testDownloadZip() throws Exception {

        PowerMockito.mockStatic(CropRequestAnalyzer.class);
        when(CropRequestAnalyzer.getGobiiCropType()).thenReturn("dev");

        when(jobService.getJobStatusDirectory("dev", 4)).thenReturn(tempDir);

        mockMvc.perform(MockMvcRequestBuilders.get("/gdm/crops/dev/gobii/v3/jobs/4/files").contextPath("/gdm"))
                .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        
    }



}