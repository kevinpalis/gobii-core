/**
 * ExperimentServiceImplTest.java
 * 
 * @author Rodolfo N. Duldulao, Jr.
 */

package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.UnknownEntityException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.ExperimentDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.*;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiisampletrackingdao.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class ExperimentServiceImplTest {

    @Mock
    private ExperimentDao experimentDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private ContactDao contactDao;

    @Mock
    private CvDao cvDao;

    @Mock
    private VendorProtocolDao vendorProtocolDao;

    @Mock
    private OrganizationDao organizationDao;

    @Mock
    private ProtocolDao protocolDao;

    @InjectMocks
    private ExperimentServiceImpl experimentServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testGetExperiment() throws Exception {
        assert experimentDao != null;
        Experiment experiment = new Experiment();
        experiment.setExperimentName("test-experiment");

        
        when(
            experimentDao.getExperiment(123)
        ).thenReturn(
            experiment
        );

        ExperimentDTO target = experimentServiceImpl.getExperiment(123);
        assert target.getExperimentName() == experiment.getExperimentName();
        
    }

    @Test
    public void testCreateExperiment() throws Exception {
        assert experimentDao != null;

        ExperimentDTO request = new ExperimentDTO();
        request.setExperimentName("test-experiment");
        request.setProjectId(7);
        request.setVendorId(4);
        request.setProtocolId(8);

        Project dummyProject = new Project();
        dummyProject.setProjectId(7);
        dummyProject.setProjectName("test-project");
        
        when(
            projectDao.getProject(7)
        ).thenReturn(
            dummyProject
        );

        List<Cv> dummyCvList = new java.util.ArrayList<>();
        Cv dummyCv = new Cv();
        dummyCvList.add(dummyCv);

        when(
            cvDao.getCvs("new", CvGroupTerm.CVGROUP_STATUS.getCvGroupName(), GobiiCvGroupType.GROUP_TYPE_SYSTEM)
        ).thenReturn(
            dummyCvList
        );

        VendorProtocol dummyVp = new VendorProtocol();
        dummyVp.setName("test-vendor-protocol");
        dummyVp.setVendorProtocolId(4);

        Organization dummyVendor = new Organization();
        dummyVendor.setOrganizationId(7);
        dummyVendor.setName(RandomStringUtils.random(7, true, true));


        Platform platform = new Platform();
        platform.setPlatformId(1);
        platform.setPlatformName("test-platform");

        Protocol dummyProtocol = new Protocol();
        dummyProtocol.setProtocolId(1);
        dummyProtocol.setName("test-protocol");
        dummyProtocol.setPlatform(platform);

        dummyVp.setProtocol(dummyProtocol);

        when(
            experimentDao.getVendorProtocol(4)
        ).thenReturn(
            dummyVp
        );

        when(organizationDao.getOrganization(any())).thenReturn(dummyVendor);
        when(protocolDao.getProtocolById(any())).thenReturn(dummyProtocol);
        when(vendorProtocolDao.createVendorProtocol(any())).thenReturn(dummyVp);

        Contact dummyContact = new Contact();
        dummyContact.setContactId(1);

        when(
            contactDao.getContactByUsername("test-user")
        ).thenReturn(dummyContact);

        Experiment mockExp = new Experiment();
        mockExp.setVendorProtocol(dummyVp);
        when(
            experimentDao.createExperiment(Mockito.any(Experiment.class))
        ).thenReturn(
            mockExp
        );
        ArgumentCaptor<Experiment> arg = ArgumentCaptor.forClass(Experiment.class);
        experimentServiceImpl.createExperiment(request, "test-user");
        verify(experimentDao).createExperiment(arg.capture());
        verify(projectDao, times(1)).getProject(7);
        verify(experimentDao, times(1)).createExperiment( Mockito.any(Experiment.class));
        verify(contactDao, times(1)).getContactByUsername("test-user");
        
        assertTrue(arg.getValue().getExperimentName().equals("test-experiment"));
        assertTrue(arg.getValue().getProject().getProjectId() == 7);
    }

    @Test
    public void testUpdateExperiment() throws Exception {
        assert experimentDao != null;

        Experiment dummyExperiment = new Experiment();
        dummyExperiment.setExperimentId(123);
        


        ExperimentDTO request = new ExperimentDTO();
        request.setExperimentName("test-experiment");
        // request.setProjectId(7); -- no longer allowed
        request.setVendorId(4);
        request.setProtocolId(8);

        // Project dummyProject = new Project();
        // dummyProject.setProjectId(7);
        // dummyProject.setProjectName("test-project");
        
        // when(
        //     projectDao.getProject(7)
        // ).thenReturn(
        //     dummyProject
        // );


        VendorProtocol dummyVp = new VendorProtocol();
        dummyVp.setName("test-vendor-protocol");
        dummyVp.setVendorProtocolId(4);

        Organization dummyVendor = new Organization();
        dummyVendor.setOrganizationId(7);
        dummyVendor.setName(RandomStringUtils.random(7, true, true));

        Platform platform = new Platform();
        platform.setPlatformId(1);
        platform.setPlatformName("test-platform");

        Protocol dummyProtocol = new Protocol();
        dummyProtocol.setProtocolId(1);
        dummyProtocol.setName("test-protocol");
        dummyProtocol.setPlatform(platform);

        dummyVp.setProtocol(dummyProtocol);
        dummyVp.setVendor(dummyVendor);

        dummyExperiment.setVendorProtocol(dummyVp);

        when(
            experimentDao.getVendorProtocol(4)
        ).thenReturn(
            dummyVp
        );

        when(organizationDao.getOrganization(any())).thenReturn(dummyVendor);
        when(protocolDao.getProtocolById(any())).thenReturn(dummyProtocol);
        when(vendorProtocolDao.createVendorProtocol(any())).thenReturn(dummyVp);
        when(
            experimentDao.getExperiment(123)
        ).thenReturn(dummyExperiment);

        Contact dummyContact = new Contact();
        dummyContact.setContactId(1);

        when(
            contactDao.getContactByUsername("test-user")
        ).thenReturn(dummyContact);

       
        
        when(
            experimentDao.updateExperiment(Mockito.any(Experiment.class))
        ).thenReturn(
            new Experiment() //does not matter that it's empty as we are testing for the
                             //param instead,
        );
        ArgumentCaptor<Experiment> arg = ArgumentCaptor.forClass(Experiment.class);
        experimentServiceImpl.updateExperiment(123, request, "test-user");
        
        verify(experimentDao).updateExperiment(arg.capture());
        //verify(projectDao, times(1)).getProject(7);
        verify(experimentDao, times(2)).getExperiment(123);
        verify(experimentDao, times(1)).updateExperiment( Mockito.any(Experiment.class));
        verify(contactDao, times(1)).getContactByUsername("test-user");

        assertTrue(arg.getValue().getExperimentName().equals("test-experiment"));
        //assertTrue(arg.getValue().getProject().getProjectId() == 7);
    }

    @Test
    public void testUpdateExperimentNoVendorProtocolOk() throws Exception {
        assert experimentDao != null;

        Experiment dummyExperiment = new Experiment();
        dummyExperiment.setExperimentId(123);
        

        when(
            experimentDao.getExperiment(123)
        ).thenReturn(dummyExperiment);

        ExperimentDTO request = new ExperimentDTO();
        request.setExperimentName("test-experiment");
        //request.setProjectId(7);

        Project dummyProject = new Project();
        dummyProject.setProjectId(7);
        dummyProject.setProjectName("test-project");
        
        // when(
        //     projectDao.getProject(7)
        // ).thenReturn(
        //     dummyProject
        // );


        Contact dummyContact = new Contact();
        dummyContact.setContactId(1);

        when(
            contactDao.getContactByUsername("test-user")
        ).thenReturn(dummyContact);

       
        
        when(
            experimentDao.updateExperiment(Mockito.any(Experiment.class))
        ).thenReturn(
            new Experiment() //does not matter that it's empty as we are testing for the
                             //param instead,
        );
        ArgumentCaptor<Experiment> arg = ArgumentCaptor.forClass(Experiment.class);
        experimentServiceImpl.updateExperiment(123, request, "test-user");
        
        verify(experimentDao).updateExperiment(arg.capture());
        //verify(projectDao, times(1)).getProject(7);
        verify(experimentDao, times(2)).getExperiment(123);
        verify(experimentDao, times(1)).updateExperiment( Mockito.any(Experiment.class));
        verify(contactDao, times(1)).getContactByUsername("test-user");

        assertTrue(arg.getValue().getExperimentName().equals("test-experiment"));
        //assertTrue(arg.getValue().getProject().getProjectId() == 7);
        assertNull(arg.getValue().getVendorProtocol());
    }


    // @Test(expected = UnknownEntityException.class)
    // public void testUpdateExperimentUnknownProject() throws Exception {
    //     ExperimentDTO request = new ExperimentDTO();
    //     request.setProjectId(7);
    //     when(experimentDao.getExperiment(123)).thenReturn(new Experiment());
    //     when(projectDao.getProject(7)).thenReturn(null);

    //     experimentServiceImpl.updateExperiment(123, request, "test-updater");
    //     verify(experimentDao, times(0)).updateExperiment(any(Experiment.class));

    // } -- no longer allowed

    @Test(expected = UnknownEntityException.class)
    public void testUpdateExperimentUnknownVendorProtocol() throws Exception {

        ExperimentDTO request = new ExperimentDTO();
        request.setVendorId(4);
        request.setProtocolId(8);

        Experiment target = new Experiment();
        VendorProtocol targetVendorProtocol = new VendorProtocol();
        Organization targetVendor = new Organization();
        Protocol targetProtocol = new Protocol();
        targetVendor.setOrganizationId(7);
        targetProtocol.setProtocolId(9);
        targetVendorProtocol.setProtocol(targetProtocol);
        targetVendorProtocol.setVendor(targetVendor);
        target.setVendorProtocol(targetVendorProtocol);

        when(experimentDao.getExperiment(123)).thenReturn(target);
        when(vendorProtocolDao.getVendorProtocol(any(), any())).thenReturn(null);
        when(organizationDao.getOrganization(any())).thenReturn(null);
        when(protocolDao.getProtocolById(any())).thenReturn(targetProtocol);

        experimentServiceImpl.updateExperiment(123, request, "test-updater");
        verify(experimentDao, times(0)).updateExperiment(any(Experiment.class));
        
    }

    @Test
    public void testGetExperimentsOk() throws Exception {
        List<Experiment> mockList = new ArrayList<>();
        mockList.add(new Experiment());

        when(experimentDao.getExperiments(1000, 0, null)).thenReturn(mockList);

        PagedResult<ExperimentDTO> result = experimentServiceImpl.getExperiments(0, 1000, null);
        assertTrue(result.getCurrentPageNum() == 0);
        assertTrue(result.getCurrentPageSize() == 1);
        assertTrue(result.getResult().size() == 1);
    }

    @Test
    public void testDeleteExperimentOk() throws Exception {
        when(experimentDao.getExperiment(123)).thenReturn(new Experiment());
        experimentServiceImpl.deleteExperiment(123);

        verify(experimentDao, times(1)).deleteExperiment(any(Experiment.class));
    }

    @Test(expected = GobiiException.class)
    public void testDeleteExperimentNotOk1() throws Exception {
        when(experimentDao.getExperiment(123)).thenReturn(null);
        experimentServiceImpl.deleteExperiment(123);

        verify(experimentDao, times(0)).deleteExperiment(any(Experiment.class));
    }

}