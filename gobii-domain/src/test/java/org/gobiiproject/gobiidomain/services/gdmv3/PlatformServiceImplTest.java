package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.PlatformDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.entity.Platform;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.PlatformDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class PlatformServiceImplTest {
    
    @Mock
    private PlatformDao platformDao;

    @Mock
    private ContactDao contactDao;

    @Mock
    private CvDao cvDao;

    @InjectMocks
    private PlatformServiceImpl platformServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatePlatformOk() throws Exception {
        Cv mockCv = new Cv();
        mockCv.setCvId(111);

        CvGroup mockCvGroup = new CvGroup();
        mockCvGroup.setCvGroupName(CvGroupTerm.CVGROUP_PLATFORM_TYPE.getCvGroupName());
        mockCv.setCvGroup(mockCvGroup);
        when(cvDao.getCvByCvId(111)).thenReturn(mockCv);
        
        PlatformDTO request = new PlatformDTO();
        request.setPlatformName("test-platform");
        request.setPlatformTypeId(111);

        Contact mockContact = new Contact();
        mockContact.setUsername("test-editor");
        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);

        Cv mockNewStat = new Cv();
        when(cvDao.getNewStatus()).thenReturn(mockNewStat);

        when(platformDao.createPlatform(any(Platform.class))).thenReturn(new Platform());
        
        platformServiceImpl.createPlatform(request, "test-editor");
        ArgumentCaptor<Platform> arg = ArgumentCaptor.forClass(Platform.class);
        verify(platformDao).createPlatform(arg.capture());
        
        assertTrue ( arg.getValue().getPlatformName().equals("test-platform"));
        assertTrue ( arg.getValue().getType().getCvId() == 111);
        
    }

    @Test(expected = GobiiException.class)
    public void testCreatePlatformInvalidPlatformType1() throws Exception {
        Cv mockCv = new Cv();
        mockCv.setCvId(111);

        CvGroup mockCvGroup = new CvGroup();
        mockCvGroup.setCvGroupName(CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName()); //the wrong time
        mockCv.setCvGroup(mockCvGroup);
        when(cvDao.getCvByCvId(111)).thenReturn(mockCv);
        
        PlatformDTO request = new PlatformDTO();
        request.setPlatformName("test-platform");
        request.setPlatformTypeId(111);

        
        platformServiceImpl.createPlatform(request, "test-editor");
        verify(platformDao, times(0)).createPlatform(any(Platform.class));
        
    }

    @Test(expected = GobiiException.class)
    public void testCreatePlatformInvalidPlatformType2() throws Exception {
        when(cvDao.getCvByCvId(111)).thenReturn(null);
        
        PlatformDTO request = new PlatformDTO();
        request.setPlatformName("test-platform");
        request.setPlatformTypeId(111);
  
        platformServiceImpl.createPlatform(request, "test-editor");
        verify(platformDao, times(0)).createPlatform(any(Platform.class));
        
    }

    @Test
    public void testGetPlatformsOk() throws Exception {
        List<Platform> mockList = new ArrayList<>();
        
        mockList.add(new Platform());

        when(platformDao.getPlatforms(0, 1000, null)).thenReturn(mockList);

        PagedResult<PlatformDTO> result = platformServiceImpl.getPlatforms(0, 1000, null);
        assertTrue( result.getCurrentPageNum() == 0);
        assertTrue( result.getCurrentPageSize() == 1);
        assertTrue( result.getResult().size() == 1);
    }

    @Test
    public void testGetPlatformOk() throws Exception {
        when(platformDao.getPlatform(100)).thenReturn(new Platform());
        platformServiceImpl.getPlatform(100);
        verify(platformDao, times(1)).getPlatform(100);
    }

    @Test
    public void testUpdatePlatformOk() throws Exception {
        Platform mockPlatform = new Platform();
        mockPlatform.setPlatformName("platform-name");

        Cv mockCv = new Cv();
        mockCv.setCvId(111);

        CvGroup mockCvGroup = new CvGroup();
        mockCvGroup.setCvGroupName(CvGroupTerm.CVGROUP_PLATFORM_TYPE.getCvGroupName());
        mockCv.setCvGroup(mockCvGroup);
        mockPlatform.setType(mockCv);

        when(cvDao.getCvByCvId(111)).thenReturn(mockCv);
        when(platformDao.getPlatform(100)).thenReturn(mockPlatform);

        Cv mockCv2 = new Cv();
        mockCv.setCvId(112);
        mockCv2.setCvGroup(mockCvGroup);
        when(cvDao.getCvByCvId(112)).thenReturn(mockCv);

        Contact mockContact = new Contact();
        mockContact.setUsername("test-editor");
        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);

        Cv mockModStat = new Cv();
        when(cvDao.getModifiedStatus()).thenReturn(mockModStat);

        when(platformDao.updatePlatform(any(Platform.class))).thenReturn(new Platform());

        ArgumentCaptor<Platform> arg = ArgumentCaptor.forClass(Platform.class);

        PlatformDTO request = new PlatformDTO();
        request.setPlatformName("modified-name");
        request.setPlatformTypeId(112);

        platformServiceImpl.updatePlatform(100, request, "test-editor");

        verify(platformDao).updatePlatform(arg.capture());

        assertTrue( arg.getValue().getPlatformName().equals("modified-name"));
        assertTrue( arg.getValue().getType().getCvId() == 112);
    }

    @Test
    public void testUpdatePlatformNoUpdatesOk() throws Exception {
        Platform mockPlatform = new Platform();
        mockPlatform.setPlatformName("platform-name");

        Cv mockCv = new Cv();
        mockCv.setCvId(111);

        CvGroup mockCvGroup = new CvGroup();
        mockCvGroup.setCvGroupName(CvGroupTerm.CVGROUP_PLATFORM_TYPE.getCvGroupName());
        mockCv.setCvGroup(mockCvGroup);
        mockPlatform.setType(mockCv);

        when(cvDao.getCvByCvId(111)).thenReturn(mockCv);
        when(platformDao.getPlatform(100)).thenReturn(mockPlatform);


        PlatformDTO request = new PlatformDTO();


        platformServiceImpl.updatePlatform(100, request, "test-editor");

        verify(platformDao, times(0)).updatePlatform(any(Platform.class));
    }

    @Test(expected = GobiiException.class)
    public void testUpdatePlatformNotFound() throws Exception {

        when(platformDao.getPlatform(100)).thenReturn(null);


        PlatformDTO request = new PlatformDTO();
        platformServiceImpl.updatePlatform(100, request, "test-editor");

        verify(platformDao, times(0)).updatePlatform(any(Platform.class));
    }  

    @Test
    public void tesetDeleteOk() throws Exception {
        Platform mockPlatform = new Platform();
        when(platformDao.getPlatform(100)).thenReturn(mockPlatform);

        platformServiceImpl.deletePlatform(100);

        verify(platformDao, times(1)).deletePlatform(any(Platform.class));
    }

    @Test
    public void testCreatePlatformTypeOk() throws Exception {

        when(cvDao.getCvGroupByNameAndType(CvGroupTerm.CVGROUP_PLATFORM_TYPE.getCvGroupName(), 2)).thenReturn(new CvGroup());
        when(cvDao.getNewStatus()).thenReturn(new Cv());

        CvTypeDTO request = new CvTypeDTO();
        request.setTypeName("test-type");
        request.setTypeDescription("test-desc");

        when(cvDao.createCv(any(Cv.class))).thenReturn(new Cv());
        ArgumentCaptor<Cv> arg = ArgumentCaptor.forClass(Cv.class);

        platformServiceImpl.createPlatformType(request);
        verify(cvDao).createCv(arg.capture());

        assertTrue( arg.getValue().getTerm().equals("test-type"));
        assertTrue( arg.getValue().getDefinition().equals("test-desc"));
    }

    @Test
    public void testGetPlatformTypes() throws Exception {
        List<Cv> mockList = new ArrayList<>();
        mockList.add(new Cv());

        when(cvDao.getCvs(null, CvGroupTerm.CVGROUP_PLATFORM_TYPE.getCvGroupName(), null, 0, 1000)).thenReturn(mockList);

        PagedResult<CvTypeDTO> result = platformServiceImpl.getPlatformTypes(0, 1000);
        assertTrue( result.getCurrentPageNum() == 0);
        assertTrue( result.getCurrentPageSize() == 1);
        assertTrue( result.getResult().size() == 1);

    }
}