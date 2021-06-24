package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.entity.Mapset;
import org.gobiiproject.gobiimodel.entity.Reference;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.MapsetDao;
import org.gobiiproject.gobiisampletrackingdao.ReferenceDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class MapsetServiceImplTest {

    @Mock
    private MapsetDao mapsetDao;

    @Mock
    private CvDao cvDao;

    @Mock
    private ReferenceDao referenceDao;

    @Mock
    private ContactDao contactDao;

    @InjectMocks
    private MapsetServiceImpl mapsetServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetMapsetsOk() throws Exception {
        List<Mapset> mockList = new ArrayList<>();
        mockList.add(new Mapset());
        when(mapsetDao.getMapsets(1000, 0, null)).thenReturn(mockList);

        PagedResult<MapsetDTO> results = mapsetServiceImpl.getMapsets(0, 1000, null);
        assertTrue(results.getCurrentPageNum() == 0);
        assertTrue(results.getCurrentPageSize() == 1);
        assertTrue(results.getResult().size() == 1);

    }

    @Test
    public void testCreateMapsetOk() throws Exception {
        Cv mockType = new Cv();
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName());
        mockType.setTerm("test-term");
        mockType.setCvGroup(mockGroup);
        mockType.setCvId(789);

        when(cvDao.getCvByCvId(789)).thenReturn(mockType);

        Reference mockRef = new Reference();
        mockRef.setReferenceId(456);

        when(referenceDao.getReference(456)).thenReturn(mockRef);

        MapsetDTO request = getMockRequest();

        Contact mockContact = new Contact();
        mockContact.setUsername("test-editor");

        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);

        Cv mockNewStat = new Cv();
        when(cvDao.getNewStatus()).thenReturn(mockNewStat);

        ArgumentCaptor<Mapset> arg = ArgumentCaptor.forClass(Mapset.class);
        when(mapsetDao.createMapset(any(Mapset.class))).thenReturn(new Mapset());

        mapsetServiceImpl.createMapset(request, "test-editor");
        verify(cvDao, times(1)).getCvByCvId(789);
        verify(referenceDao, times(1)).getReference(456);

        verify(mapsetDao).createMapset(arg.capture());

        assertTrue(arg.getValue().getMapsetName().equals("test-mapset"));
        assertTrue(arg.getValue().getMapsetDescription().equals("test-desc"));

        assertTrue(arg.getValue().getMapsetCode().equals("test-term_test-mapset"));
        assertTrue(arg.getValue().getType().getCvId() == 789);
        assertTrue(arg.getValue().getReference().getReferenceId() == 456);
    }

    @Test
    public void testCreateMapsetNoReferenceNoDescriptionOk() throws Exception {
        Cv mockType = new Cv();
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName());
        mockType.setTerm("test-term");
        mockType.setCvGroup(mockGroup);
        mockType.setCvId(789);

        when(cvDao.getCvByCvId(789)).thenReturn(mockType);

        MapsetDTO request = getMockRequest();
        request.setReferenceId(null);
        request.setMapsetDescription(null);

        Contact mockContact = new Contact();
        mockContact.setUsername("test-editor");

        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);

        Cv mockNewStat = new Cv();
        when(cvDao.getNewStatus()).thenReturn(mockNewStat);

        ArgumentCaptor<Mapset> arg = ArgumentCaptor.forClass(Mapset.class);
        when(mapsetDao.createMapset(any(Mapset.class))).thenReturn(new Mapset());

        mapsetServiceImpl.createMapset(request, "test-editor");
        verify(cvDao, times(1)).getCvByCvId(789);
        verify(mapsetDao).createMapset(arg.capture());

        assertTrue(arg.getValue().getMapsetName().equals("test-mapset"));
        assertNull(arg.getValue().getMapsetDescription());

        assertTrue(arg.getValue().getMapsetCode().equals("test-term_test-mapset"));
        assertTrue(arg.getValue().getType().getCvId() == 789);
        assertNull(arg.getValue().getReference());
    }

    @Test
    public void testGetMapsetOk() throws Exception {
        when(mapsetDao.getMapset(111)).thenReturn(new Mapset());

        mapsetServiceImpl.getMapset(111);

        verify(mapsetDao, times(1)).getMapset(111);
    }

    @Test(expected = GobiiException.class)
    public void testGetMapsetNotOk() throws Exception {
        when(mapsetDao.getMapset(111)).thenReturn(null);

        mapsetServiceImpl.getMapset(111);

        verify(mapsetDao, times(1)).getMapset(111);
    }

    @Test
    public void testUpdateMapsetOk() throws Exception {

        Mapset mockMapset = new Mapset();
        mockMapset.setMapsetId(111);
        mockMapset.setMapsetName("test-old-mapset");

        when(mapsetDao.getMapset(111)).thenReturn(mockMapset);

        Cv mockType = new Cv();
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName());
        mockType.setTerm("test-term");
        mockType.setCvGroup(mockGroup);
        mockType.setCvId(789);

        when(cvDao.getCvByCvId(789)).thenReturn(mockType);

        Reference mockRef = new Reference();
        mockRef.setReferenceId(456);

        when(referenceDao.getReference(456)).thenReturn(mockRef);

        MapsetDTO request = getMockRequest();

        Contact mockContact = new Contact();
        mockContact.setUsername("test-editor");

        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);

        Cv mockNewStat = new Cv();
        when(cvDao.getModifiedStatus()).thenReturn(mockNewStat);

        ArgumentCaptor<Mapset> arg = ArgumentCaptor.forClass(Mapset.class);
        when(mapsetDao.updateMapset(any(Mapset.class))).thenReturn(new Mapset());

        mapsetServiceImpl.updateMapset(111, request, "test-editor");
        verify(cvDao, times(1)).getCvByCvId(789);
        verify(referenceDao, times(1)).getReference(456);

        verify(mapsetDao).updateMapset(arg.capture());

        assertTrue(arg.getValue().getMapsetName().equals("test-mapset"));
        assertTrue(arg.getValue().getMapsetDescription().equals("test-desc"));

        assertTrue(arg.getValue().getType().getCvId() == 789);
        assertTrue(arg.getValue().getReference().getReferenceId() == 456);
    }

    @Test
    public void testUpdateMapsetNoNameNoDescNoRefOk() throws Exception {

        Mapset mockMapset = new Mapset();
        mockMapset.setMapsetId(111);
        mockMapset.setMapsetName("test-old-mapset");
        mockMapset.setMapsetDescription("old-desc");

        when(mapsetDao.getMapset(111)).thenReturn(mockMapset);

        Cv mockType = new Cv();
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName());
        mockType.setTerm("test-term");
        mockType.setCvGroup(mockGroup);
        mockType.setCvId(789);

        when(cvDao.getCvByCvId(789)).thenReturn(mockType);


        MapsetDTO request = getMockRequest();
        request.setMapsetName(null);
        request.setMapsetDescription(null);
        request.setReferenceId(null);

        Contact mockContact = new Contact();
        mockContact.setUsername("test-editor");

        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);

        Cv mockNewStat = new Cv();
        when(cvDao.getModifiedStatus()).thenReturn(mockNewStat);

        ArgumentCaptor<Mapset> arg = ArgumentCaptor.forClass(Mapset.class);
        when(mapsetDao.updateMapset(any(Mapset.class))).thenReturn(new Mapset());

        mapsetServiceImpl.updateMapset(111, request, "test-editor");
        verify(cvDao, times(1)).getCvByCvId(789);
        verify(mapsetDao).updateMapset(arg.capture());

        assertTrue(arg.getValue().getMapsetName().equals("test-old-mapset"));
        assertTrue(arg.getValue().getMapsetDescription().equals("old-desc"));

        assertTrue(arg.getValue().getType().getCvId() == 789);
        assertNull(arg.getValue().getReference());
    }

    @Test(expected = GobiiException.class)
    public void testUpdateMapsetUnknownType() throws Exception {
        MapsetDTO request = getMockRequest();
        Mapset mockMapset = new Mapset();
        mockMapset.setMapsetId(111);
        mockMapset.setMapsetName("test-old-mapset");

        when(mapsetDao.getMapset(111)).thenReturn(mockMapset);

        when(cvDao.getCvByCvId(789)).thenReturn(null);

        mapsetServiceImpl.updateMapset(111, request, "test-editor");
        verify(cvDao, times(1)).getCvByCvId(789);

        verify(mapsetDao, times(0)).updateMapset(any(Mapset.class));
    }

    @Test(expected = GobiiException.class)
    public void testUpdateMapsetIncorrectType() throws Exception {
        MapsetDTO request = getMockRequest();
        Mapset mockMapset = new Mapset();
        mockMapset.setMapsetId(111);
        mockMapset.setMapsetName("test-old-mapset");

        when(mapsetDao.getMapset(111)).thenReturn(mockMapset);

        Cv mockType = new Cv();
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName());
        mockType.setTerm("test-term");
        mockType.setCvGroup(mockGroup);
        mockType.setCvId(789);
        when(cvDao.getCvByCvId(789)).thenReturn(mockType);

        mapsetServiceImpl.updateMapset(111, request, "test-editor");
        verify(cvDao, times(1)).getCvByCvId(789);

        verify(mapsetDao, times(0)).updateMapset(any(Mapset.class));

    }

    @Test(expected = GobiiException.class)
    public void testUpdateMapsetUnknownReference() throws Exception {
        MapsetDTO request = getMockRequest();
        request.setMapsetTypeId(null);

        Mapset mockMapset = new Mapset();
        mockMapset.setMapsetId(111);
        mockMapset.setMapsetName("test-old-mapset");

        when(mapsetDao.getMapset(111)).thenReturn(mockMapset);

        when(referenceDao.getReference(456)).thenReturn(null);

        mapsetServiceImpl.updateMapset(111, request, "test-editor");
        verify(referenceDao, times(1)).getReference(456);

        verify(mapsetDao, times(0)).updateMapset(any(Mapset.class));
    }

    @Test
    public void testDeleteOk() throws Exception {
        Mapset mockMapset = new Mapset();
        mockMapset.setMapsetId(111);
        mockMapset.setMapsetName("test-old-mapset");

        when(mapsetDao.getMapset(111)).thenReturn(mockMapset);

        mapsetServiceImpl.deleteMapset(111);

        verify(mapsetDao, times(1)).deleteMapset(any(Mapset.class));
    }

    @Test
    public void testCreateMapsetTypeOk() throws Exception {
        String cvGroupName = CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName();
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(cvGroupName);
        mockGroup.setCvGroupId(123);
        mockGroup.setCvGroupType(2);

        when(cvDao.getCvGroupByNameAndType(cvGroupName, 2)).thenReturn(mockGroup);

        when(cvDao.getNewStatus()).thenReturn(new Cv());
        when(cvDao.createCv(any(Cv.class))).thenReturn(new Cv());
        ArgumentCaptor<Cv> arg = ArgumentCaptor.forClass(Cv.class);

        mapsetServiceImpl.createMapsetType("mapsetTypeName", "mapsetTypeDescription", "user");

        verify(cvDao, times(1)).createCv(any(Cv.class));
        verify(cvDao).createCv(arg.capture());

        assertTrue(arg.getValue().getTerm().equals("mapsetTypeName"));
        assertTrue(arg.getValue().getDefinition().equals("mapsetTypeDescription"));
        assertTrue(arg.getValue().getCvGroup().getCvGroupName().equals(cvGroupName));

    }

    @Test
    public void testCreateMapsetTypeNoDescOk() throws Exception {
        String cvGroupName = CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName();
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(cvGroupName);
        mockGroup.setCvGroupId(123);
        mockGroup.setCvGroupType(2);

        when(cvDao.getCvGroupByNameAndType(cvGroupName, 2)).thenReturn(mockGroup);

        when(cvDao.getNewStatus()).thenReturn(new Cv());
        when(cvDao.createCv(any(Cv.class))).thenReturn(new Cv());
        ArgumentCaptor<Cv> arg = ArgumentCaptor.forClass(Cv.class);

        mapsetServiceImpl.createMapsetType("mapsetTypeName", null, "user");

        verify(cvDao, times(1)).createCv(any(Cv.class));
        verify(cvDao).createCv(arg.capture());

        assertTrue(arg.getValue().getTerm().equals("mapsetTypeName"));
        assertNull(arg.getValue().getDefinition());
        assertTrue(arg.getValue().getCvGroup().getCvGroupName().equals(cvGroupName));

    }

    @Test(expected =  GobiiDaoException.class)
    public void testCreateMapsetTypeMissingCorrectCvGroupType() throws Exception {
        when(cvDao.getCvGroupByNameAndType(CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName(), 2)).thenReturn(null);

        mapsetServiceImpl.createMapsetType("test-mapset-type", "desc", "user");

        verify(cvDao, times(1)).getCvGroupByNameAndType(CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName(), 2);
    }

    @Test
    public void testGetMapsetTypes() throws Exception {
        String cvGroupName = CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName();
        List<Cv> mockCvs = new ArrayList<>();
        Cv mockType = new Cv();
        mockCvs.add(mockType);

        when(cvDao.getCvs(null, cvGroupName, null, 0, 1000)).thenReturn(mockCvs);

        PagedResult<CvTypeDTO> results = mapsetServiceImpl.getMapsetTypes(0, 1000);
        assertTrue(results.getCurrentPageNum() == 0);
        assertTrue(results.getCurrentPageSize() == 1);
        assertTrue(results.getResult().size() == 1);

    }

    private MapsetDTO getMockRequest() {
        MapsetDTO request = new MapsetDTO();
        request.setMapsetName("test-mapset");
        request.setMapsetDescription("test-desc");
        request.setMapsetTypeId(789);
        request.setReferenceId(456);
        return request;
    }

}