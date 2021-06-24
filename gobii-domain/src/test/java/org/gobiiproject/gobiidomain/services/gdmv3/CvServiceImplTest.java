package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvGroupDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class CvServiceImplTest {
    @Mock
    private CvDao cvDao;

    @InjectMocks
    private CvServiceImpl cvServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateCvNoProperties() throws Exception {
        CvDTO cvDTORequest = new CvDTO();
        cvDTORequest.setCvName("test-name");
        cvDTORequest.setCvDescription("test-desc");
        cvDTORequest.setCvGroupId(12);

        CvGroup mockCvGroup = new CvGroup();
        mockCvGroup.setCvGroupId(12);
        mockCvGroup.setCvGroupType(2);
        mockCvGroup.setCvGroupName("test-group");

        when(cvDao.getCvGroupById(12)).thenReturn(mockCvGroup);

        Cv mockNewStatus = new Cv();
        mockNewStatus.setTerm("new");
        mockNewStatus.setCvId(57);

        when(cvDao.getNewStatus()).thenReturn(mockNewStatus);

        when(cvDao.getCvListByCvGroup("cv_prop", null)).thenReturn(new ArrayList<Cv>());

        when(cvDao.createCv(any(Cv.class))).thenReturn(new Cv());

        ArgumentCaptor<Cv> arg = ArgumentCaptor.forClass(Cv.class);

        cvServiceImpl.createCv(cvDTORequest);

        verify(cvDao).createCv(arg.capture());

        Cv result = arg.getValue();

        assertTrue("Result cv name incorrect", result.getTerm().equals("test-name"));
        assertTrue("Result cv description incorrect", result.getDefinition().equals("test-desc"));
        assertTrue("Result cv status incorrect", result.getStatus() == 57);
        assertTrue("Result cvGroup name incorrect", result.getCvGroup().getCvGroupName().equals("test-group"));
        assertTrue("Result cvGroup Type incorrect", result.getCvGroup().getCvGroupType() == 2);
        assertTrue("Result cvGroup Id incorrect", result.getCvGroup().getCvGroupId() == 12);
        assertTrue("Result properties not empty", result.getProperties() == null || result.getProperties().size() == 0);
    }

    @Test
    public void testCreateWithProperties() throws Exception {
        CvDTO cvDTORequest = new CvDTO();
        cvDTORequest.setCvName("test-name");
        cvDTORequest.setCvDescription("test-desc");
        cvDTORequest.setCvGroupId(12);

        List<CvPropertyDTO> properties = new ArrayList<>();
        CvPropertyDTO mockProp = new CvPropertyDTO();
        mockProp.setPropertyId(888);
        mockProp.setPropertyValue("SomeValue");
        properties.add(mockProp);

        CvGroup mockCvGroup = new CvGroup();
        mockCvGroup.setCvGroupId(12);
        mockCvGroup.setCvGroupType(2);
        mockCvGroup.setCvGroupName("test-group");

        Cv mockPropCv = new Cv();
        mockPropCv.setCvId(888);
        CvGroup mockPropCvGroup = new CvGroup();
        mockPropCv.setCvGroup(mockPropCvGroup);

        when(cvDao.getCvByCvId(888)).thenReturn(mockPropCv);

        when(cvDao.getCvGroupById(12)).thenReturn(mockCvGroup);

        Cv mockNewStatus = new Cv();
        mockNewStatus.setTerm("new");
        mockNewStatus.setCvId(57);

        when(cvDao.getNewStatus()).thenReturn(mockNewStatus);

        when(cvDao.getCvListByCvGroup("cv_prop", null)).thenReturn(new ArrayList<Cv>());

        when(cvDao.createCv(any(Cv.class))).thenReturn(new Cv());

        ArgumentCaptor<Cv> arg = ArgumentCaptor.forClass(Cv.class);

        cvServiceImpl.createCv(cvDTORequest);

        verify(cvDao).createCv(arg.capture());

        Cv result = arg.getValue();

        assertTrue("Result cv name incorrect", result.getTerm().equals("test-name"));
        assertTrue("Result cv description incorrect", result.getDefinition().equals("test-desc"));
        assertTrue("Result cv status incorrect", result.getStatus() == 57);
        assertTrue("Result cvGroup name incorrect", result.getCvGroup().getCvGroupName().equals("test-group"));
        assertTrue("Result cvGroup Type incorrect", result.getCvGroup().getCvGroupType() == 2);
        assertTrue("Result cvGroup Id incorrect", result.getCvGroup().getCvGroupId() == 12);
    }


    @Test
    public void testUpdateSimple() throws Exception {
        CvDTO cvDTORequest = new CvDTO();
        cvDTORequest.setCvName("test-name");

        Cv mockCv = new Cv();
        mockCv.setCvId(123);
        mockCv.setTerm("before-test");
        mockCv.setDefinition("before-test-definition");

        CvGroup mockCvGroup = new CvGroup();
        mockCvGroup.setCvGroupId(12);
        mockCvGroup.setCvGroupName("test-group");
        mockCvGroup.setCvGroupType(1);

        mockCv.setCvGroup(mockCvGroup);

        // Cv mockCv2 = new Cv();
        // mockCv2.setCvId(123);
        // mockCv2.setTerm("test-name");
        // mockCv2.setDefinition("before-test-definition");
        // mockCv2.setCvGroup(mockCvGroup);

        when(cvDao.getCvByCvId(123)).thenReturn(mockCv);

        when(cvDao.updateCv(any(Cv.class))).thenReturn(new Cv());

        Cv mockModStatus = new Cv();
        mockModStatus.setTerm("modified");
        mockModStatus.setCvId(57);

        when(cvDao.getModifiedStatus()).thenReturn(mockModStatus);

        ArgumentCaptor<Cv> arg = ArgumentCaptor.forClass(Cv.class);

        cvServiceImpl.updateCv(123, cvDTORequest);

        verify(cvDao).updateCv(arg.capture());

        Cv cvToUpdate = arg.getValue();

        assertTrue("test update failed", cvToUpdate.getTerm().equals("test-name"));
    }


    @Test
    public void testUpdateSimpleNoUpdatesMadeOk() throws Exception {
        CvDTO cvDTORequest = new CvDTO();

        Cv mockCv = new Cv();
        mockCv.setCvId(123);
        mockCv.setTerm("before-test");
        mockCv.setDefinition("before-test-definition");

        CvGroup mockCvGroup = new CvGroup();
        mockCvGroup.setCvGroupId(12);
        mockCvGroup.setCvGroupName("test-group");
        mockCvGroup.setCvGroupType(1);

        mockCv.setCvGroup(mockCvGroup);
        when(cvDao.getCvByCvId(123)).thenReturn(mockCv);

        cvServiceImpl.updateCv(123, cvDTORequest);

        verify(cvDao, times(0)).updateCv(any(Cv.class));

    }


    @Test
    public void testGetCvs() throws Exception {
        List<Cv> mockList = new ArrayList<>();
        Cv newStatus = new Cv();
        newStatus.setCvId(57);
        newStatus.setTerm("new");

        Cv modStatus = new Cv();
        newStatus.setCvId(58);
        newStatus.setTerm("modified");

        Cv cv1 = new Cv();
        cv1.setStatus(57);

        mockList.add(cv1);

        Cv cv2 = new Cv();
        cv2.setStatus(58);
        mockList.add(cv2);

        when(cvDao.getCvs(null, null, null, 0, 1000)).thenReturn(mockList);
        when(cvDao.getNewStatus()).thenReturn(newStatus);
        when(cvDao.getModifiedStatus()).thenReturn(modStatus);
        PagedResult<CvDTO> result = cvServiceImpl.getCvs(0, 1000, null, null);
        verify(cvDao, times(1)).getCvs(null, null, null, 0, 1000);

        assertTrue(result.getCurrentPageNum() == 0);
        assertTrue(result.getCurrentPageSize() == 2);
        assertTrue(result.getResult().size() == 2);
    }

    @Test
    public void testGetSystemDefinedCvs() throws Exception {
        List<Cv> mockList = new ArrayList<>();
        mockList.add(new Cv());

        when(cvDao.getCvs(null, null, GobiiCvGroupType.GROUP_TYPE_SYSTEM, 0, 1000)).thenReturn(mockList);
        when(cvDao.getNewStatus()).thenReturn(new Cv());
        when(cvDao.getModifiedStatus()).thenReturn(new Cv());
        PagedResult<CvDTO> result = cvServiceImpl.getCvs(0, 1000, null, "system_defined");
        verify(cvDao, times(1)).getCvs(null, null, GobiiCvGroupType.GROUP_TYPE_SYSTEM, 0, 1000);

        assertTrue(result.getCurrentPageNum() == 0);
        assertTrue(result.getCurrentPageSize() == 1);
        assertTrue(result.getResult().size() == 1);

        // check other user type inputs
        result = cvServiceImpl.getCvs(0, 1000, null, "1");
        verify(cvDao, times(2)).getCvs(null, null, GobiiCvGroupType.GROUP_TYPE_SYSTEM, 0, 1000);

        when(cvDao.getCvs(null, null, GobiiCvGroupType.GROUP_TYPE_USER, 0, 1000)).thenReturn(mockList);
        result = cvServiceImpl.getCvs(0, 1000, null, "user_defined");
        verify(cvDao, times(1)).getCvs(null, null, GobiiCvGroupType.GROUP_TYPE_USER, 0, 1000);

        result = cvServiceImpl.getCvs(0, 1000, null, "2");
        verify(cvDao, times(2)).getCvs(null, null, GobiiCvGroupType.GROUP_TYPE_USER, 0, 1000);

        when(cvDao.getCvs(null, null, GobiiCvGroupType.GROUP_TYPE_UNKNOWN, 0, 1000)).thenReturn(mockList);
        result = cvServiceImpl.getCvs(0, 1000, null, "foo");
        verify(cvDao, times(1)).getCvs(null, null, GobiiCvGroupType.GROUP_TYPE_UNKNOWN, 0, 1000);
    }

    @Test
    public void testGetCvOk() throws Exception {

        Cv mockCv = new Cv();
        mockCv.setTerm("test");
        mockCv.setCvGroup(new CvGroup());
        mockCv.setStatus(58);

        when(cvDao.getCvByCvId(123)).thenReturn(mockCv);
        when(cvDao.getCvByCvId(58)).thenReturn(new Cv()); // this one for the status cv

        cvServiceImpl.getCv(123);

        verify(cvDao, times(1)).getCvByCvId(123);
        verify(cvDao, times(1)).getCvByCvId(58);
    }



    @Test
    public void testDeleteOk() throws Exception {
        Cv mockCv = new Cv();
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupType(2); // user defined
        mockCv.setCvGroup(mockGroup);

        when(cvDao.getCvByCvId(123)).thenReturn(mockCv);

        cvServiceImpl.deleteCv(123);

        verify(cvDao, times(1)).deleteCv(any(Cv.class));

    }

    @Test(expected = GobiiException.class)
    public void testDeleteNotOk() throws Exception {
        Cv mockCv = new Cv();
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupType(1); // system defined
        mockCv.setCvGroup(mockGroup);

        when(cvDao.getCvByCvId(123)).thenReturn(mockCv);

        cvServiceImpl.deleteCv(123);

        verify(cvDao, times(0)).deleteCv(any(Cv.class));
    }

    @Test
    public void testGetCvGroupsOk() throws Exception {
        List<CvGroup> mockList = new ArrayList<>();
        mockList.add(new CvGroup());

        when(cvDao.getCvGroups(1000, 0, GobiiCvGroupType.GROUP_TYPE_SYSTEM)).thenReturn(mockList);
        PagedResult<CvGroupDTO> result =
            cvServiceImpl.getCvGroups(0, 1000, "system_defined");
        verify(cvDao, times(1)).getCvGroups(1000, 0, GobiiCvGroupType.GROUP_TYPE_SYSTEM);

        assertTrue(result.getCurrentPageNum() == 0);
        assertTrue(result.getCurrentPageSize() == 1);
        assertTrue(result.getResult().size() == 1);
    }

    @Test(expected = GobiiException.class)
    public void testDeleteCvNotFound() throws Exception {
        when(cvDao.getCvByCvId(123)).thenReturn(null);

        cvServiceImpl.deleteCv(123);
        verify(cvDao, times(1)).deleteCv(any(Cv.class));
    }

    @Test(expected = InvalidException.class)
    public void testUpdateCvInvalidGroupType() throws Exception {
        CvDTO request = new CvDTO();

        request.setCvGroupId(123);

        Cv mockCv = new Cv();
        mockCv.setCvId(100);

        when(cvDao.getCvByCvId(100)).thenReturn(mockCv);

        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupId(123);
        mockGroup.setCvGroupName(CvGroupTerm.CVGROUP_PLATFORM_TYPE.getCvGroupName());// the wrong group

        when(cvDao.getCvGroupById(123)).thenReturn(mockGroup);

        cvServiceImpl.updateCv(100, request);
        verify(cvDao, times(0)).updateCv(any(Cv.class));
    }

}