package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidMarkersException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.MarkerStatus;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerGroupDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.entity.MarkerGroup;
import org.gobiiproject.gobiimodel.entity.Platform;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.MarkerDao;
import org.gobiiproject.gobiisampletrackingdao.MarkerGroupDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

import lombok.extern.slf4j.Slf4j;

@WebAppConfiguration
@Slf4j
public class MarkerGroupServiceImplTest {

    @Mock
    private CvDao cvDao;

    @Mock
    private MarkerGroupDao markerGroupDao;

    @Mock
    private ContactDao contactDao;

    @Mock
    private MarkerDao markerDao;

    @InjectMocks
    private MarkerGroupServiceImpl markerGroupServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInvalidAlleles() {

        MarkerDTO markerDTO = new MarkerDTO();
        markerDTO.setMarkerName("test1");
        markerDTO.setPlatformName("KASP");
        markerDTO.setFavorableAlleles(new String[] { "B", "D", "E" });

        List<MarkerDTO> inputMarkers = new ArrayList<>();
        inputMarkers.add(markerDTO);

        MarkerGroup mockMarkerGroup = new MarkerGroup();
        mockMarkerGroup.setMarkerGroupId(123);
        mockMarkerGroup.setName("test-marker-group");
        mockMarkerGroup.setGermplasmGroup("test-germplasm-group");

        when(markerGroupDao.getMarkerGroup(123)).thenReturn(mockMarkerGroup);

        List<Marker> mockMarkers = new ArrayList<>();
        Marker mockMarker = new Marker();
        mockMarker.setMarkerName("test1");

        Platform mockPlatform = new Platform();
        mockPlatform.setPlatformId(1);
        mockPlatform.setPlatformName("KASP");
        mockMarker.setPlatform(mockPlatform);

        mockMarkers.add(mockMarker);

        when(markerDao.getMarkersByPlatformMarkerNameTuples(anyList())).thenReturn(mockMarkers);

        Exception exception = assertThrows(InvalidMarkersException.class, () -> {
            markerGroupServiceImpl.mapMarkers(123, inputMarkers, "test-user");
        });

        assertTrue("Exception is null", exception != null);

        InvalidMarkersException exc = (InvalidMarkersException) exception;

        List<MarkerStatus> statusList = exc.getStatusList();
        assertTrue("Status list empty", statusList != null && statusList.size() == 1);

        MarkerStatus status = statusList.get(0);
        log.info(status.getError());
        assertTrue(status.getError().equals("Bad Request. Invalid allele value(s) for KASP, test1: B, D, E"));

    }

    @Test
    public void testInvalidAlleles2() {

        MarkerDTO markerDTO = new MarkerDTO();
        markerDTO.setMarkerName("test1");
        markerDTO.setPlatformName("KASP");
        markerDTO.setFavorableAlleles(
                new String[] { "A", "G", "C", "T", "Z", "1000", "100", "0900", "+", "-", "_", "2000" });

        List<MarkerDTO> inputMarkers = new ArrayList<>();
        inputMarkers.add(markerDTO);

        MarkerGroup mockMarkerGroup = new MarkerGroup();
        mockMarkerGroup.setMarkerGroupId(123);
        mockMarkerGroup.setName("test-marker-group");
        mockMarkerGroup.setGermplasmGroup("test-germplasm-group");

        when(markerGroupDao.getMarkerGroup(123)).thenReturn(mockMarkerGroup);

        List<Marker> mockMarkers = new ArrayList<>();
        Marker mockMarker = new Marker();
        mockMarker.setMarkerName("test1");

        Platform mockPlatform = new Platform();
        mockPlatform.setPlatformId(1);
        mockPlatform.setPlatformName("KASP");
        mockMarker.setPlatform(mockPlatform);

        mockMarkers.add(mockMarker);

        when(markerDao.getMarkersByPlatformMarkerNameTuples(anyList())).thenReturn(mockMarkers);

        Exception exception = assertThrows(InvalidMarkersException.class, () -> {
            markerGroupServiceImpl.mapMarkers(123, inputMarkers, "test-user");
        });

        assertTrue("Exception is null", exception != null);

        InvalidMarkersException exc = (InvalidMarkersException) exception;

        List<MarkerStatus> statusList = exc.getStatusList();
        assertTrue("Status list empty", statusList != null && statusList.size() == 1);

        MarkerStatus status = statusList.get(0);
        log.info(status.getError());
        assertTrue(status.getError().equals("Bad Request. Invalid allele value(s) for KASP, test1: Z, 100, _, 2000"));

    }

    @Test
    public void testMissingMarker1() {
        MarkerDTO markerDTO = new MarkerDTO();
        markerDTO.setMarkerName("test1");
        markerDTO.setPlatformName("KASP");
        markerDTO.setFavorableAlleles(new String[] { "G" });

        List<MarkerDTO> inputMarkers = new ArrayList<>();
        inputMarkers.add(markerDTO);

        MarkerGroup mockMarkerGroup = new MarkerGroup();
        mockMarkerGroup.setMarkerGroupId(123);
        mockMarkerGroup.setName("test-marker-group");
        mockMarkerGroup.setGermplasmGroup("test-germplasm-group");

        when(markerGroupDao.getMarkerGroup(123)).thenReturn(mockMarkerGroup);

        List<Marker> mockMarkers = new ArrayList<>();
        // no matching marker

        when(markerDao.getMarkersByPlatformMarkerNameTuples(anyList())).thenReturn(mockMarkers);

        Exception exception = assertThrows(InvalidMarkersException.class, () -> {
            markerGroupServiceImpl.mapMarkers(123, inputMarkers, "test-user");
        });

        assertTrue("Exception is null", exception != null);

        InvalidMarkersException exc = (InvalidMarkersException) exception;

        List<MarkerStatus> statusList = exc.getStatusList();
        assertTrue("Status list empty", statusList != null && statusList.size() == 1);

        MarkerStatus status = statusList.get(0);
        log.info(status.getError());
        assertTrue(status.getError().equals("Bad Request. Marker: KASP, test1 is invalid"));

    }

    @Test
    public void testCreateMarkerGroupOk() throws Exception {
        MarkerGroupDTO request = new MarkerGroupDTO();
        request.setMarkerGroupName("test-group");
        request.setGermplasmGroup("germplasmGroup");

        Cv mockCv = new Cv();
        mockCv.setTerm("new");
        mockCv.setCvId(57);

        when(cvDao.getNewStatus()).thenReturn(new Cv());

        Contact mockContact = new Contact();
        mockContact.setUsername("test-editor");

        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);
        when(markerGroupDao.createMarkerGroup(any(MarkerGroup.class))).thenReturn(new MarkerGroup());

        ArgumentCaptor<MarkerGroup> arg = ArgumentCaptor.forClass(MarkerGroup.class);
        markerGroupServiceImpl.createMarkerGroup(request, "test-editor");

        verify(markerGroupDao).createMarkerGroup(arg.capture());

        assertTrue(arg.getValue().getName().equals("test-group"));
        assertTrue(arg.getValue().getGermplasmGroup().equals("germplasmGroup"));

    }

    @Test
    public void testGetMarkerGroupsOk() throws Exception {
        List<MarkerGroup> mockList = new ArrayList<>();
        mockList.add(new MarkerGroup());

        when(markerGroupDao.getMarkerGroups(0, 1000)).thenReturn(mockList);

        PagedResult<MarkerGroupDTO> result = markerGroupServiceImpl.getMarkerGroups(0, 1000);
        
        assertTrue ( result.getCurrentPageNum() == 0);
        assertTrue ( result.getCurrentPageSize() == 1);
        assertTrue ( result.getResult().size() == 1);
    }

    @Test
    public void testGetMarkerGroupOk() throws Exception {
        when(markerGroupDao.getMarkerGroup(123)).thenReturn(new MarkerGroup());

        markerGroupServiceImpl.getMarkerGroup(123);

        verify(markerGroupDao, times(1)).getMarkerGroup(123);
    }

    @Test(expected = GobiiException.class)
    public void testGetMarkerGroupNotFound() throws Exception {
        when(markerGroupDao.getMarkerGroup(123)).thenReturn(null);

        markerGroupServiceImpl.getMarkerGroup(123);

        verify(markerGroupDao, times(1)).getMarkerGroup(123);
    }

    @Test
    public void testDeleteMarkerGroup() throws Exception {
        when(markerGroupDao.getMarkerGroup(123)).thenReturn(new MarkerGroup());
        doNothing().when(markerGroupDao).deleteMarkerGroup(any(MarkerGroup.class));
        markerGroupServiceImpl.deleteMarkerGroup(123);

        verify(markerGroupDao, times(1)).getMarkerGroup(123);
        verify(markerGroupDao, times(1)).deleteMarkerGroup(any(MarkerGroup.class));
    }

    @Test
    public void testGetMarkerGroupMarkersOk() throws Exception {
        MarkerGroup mockMarkerGroup = new MarkerGroup();

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode markerNode = objectMapper.createObjectNode();
        
        ArrayNode alleles = markerNode.putArray("4");
        alleles.add("A");
        mockMarkerGroup.setMarkers(markerNode);

        when(markerGroupDao.getMarkerGroup(123)).thenReturn(mockMarkerGroup);

        Marker mockMarker = new Marker();
        mockMarker.setMarkerId(4);

        List<Marker> mockList = new ArrayList<>();
        mockList.add(mockMarker);

        when(markerDao.getMarkersByMarkerIds(anySet())).thenReturn(mockList);

        PagedResult<MarkerDTO> result = markerGroupServiceImpl.getMarkerGroupMarkers(123, 0, 1000);
        assertTrue(result.getCurrentPageNum() == 0);
        assertTrue(result.getCurrentPageSize() == 1);
    }

    @Test
    public void testUpdateMarkerGroupOk() throws Exception {
        MarkerGroupDTO request = new MarkerGroupDTO();
        request.setMarkerGroupName("new-name");
        request.setGermplasmGroup("new-group");

        MarkerGroup mockGroup = new MarkerGroup();
        mockGroup.setName("old-name");
        mockGroup.setGermplasmGroup("old-group");

        when(markerGroupDao.getMarkerGroup(123)).thenReturn(mockGroup);

        when(markerGroupDao.updateMarkerGroup(any(MarkerGroup.class))).thenReturn(new MarkerGroup());
        when(cvDao.getModifiedStatus()).thenReturn(new Cv());
        when(contactDao.getContactByUsername("test-editor")).thenReturn(new Contact());
        ArgumentCaptor<MarkerGroup> arg = ArgumentCaptor.forClass(MarkerGroup.class);

        markerGroupServiceImpl.updateMarkerGroup(123, request, "test-editor");
        verify(markerGroupDao).updateMarkerGroup(arg.capture());

        assertTrue(arg.getValue().getName().equals("new-name"));
        assertTrue(arg.getValue().getGermplasmGroup().equals("new-group"));
    }

    @Test
    public void testUpdateMarkerGroupNoUpdatesOk() throws Exception {
        MarkerGroupDTO request = new MarkerGroupDTO();


        MarkerGroup mockGroup = new MarkerGroup();
        mockGroup.setName("old-name");
        mockGroup.setGermplasmGroup("old-group");

        when(markerGroupDao.getMarkerGroup(123)).thenReturn(mockGroup);

        when(markerGroupDao.updateMarkerGroup(any(MarkerGroup.class))).thenReturn(new MarkerGroup());
       
        markerGroupServiceImpl.updateMarkerGroup(123, request, "test-editor");
        verify(markerGroupDao, times(0)).updateMarkerGroup(any(MarkerGroup.class));

       
    }

    @Test
    public void testMapMarkersOkAlleles() throws Exception {

        MarkerDTO markerDTO = new MarkerDTO();
        markerDTO.setMarkerName("test1");
        markerDTO.setPlatformName("KASP");
        markerDTO.setFavorableAlleles(new String[] { "G", "C", "A" });

        List<MarkerDTO> inputMarkers = new ArrayList<>();
        inputMarkers.add(markerDTO);
        ObjectMapper mapper = new ObjectMapper();
        MarkerGroup mockMarkerGroup = new MarkerGroup();
        mockMarkerGroup.setMarkerGroupId(123);
        mockMarkerGroup.setName("test-marker-group");
        mockMarkerGroup.setGermplasmGroup("test-germplasm-group");
        mockMarkerGroup.setMarkers(mapper.createObjectNode());

        when(markerGroupDao.getMarkerGroup(123)).thenReturn(mockMarkerGroup);

        List<Marker> mockMarkers = new ArrayList<>();
        Marker mockMarker = new Marker();
        mockMarker.setMarkerId(4);
        mockMarker.setMarkerName("test1");

        Platform mockPlatform = new Platform();
        mockPlatform.setPlatformId(1);
        mockPlatform.setPlatformName("KASP");
        mockMarker.setPlatform(mockPlatform);

        mockMarkers.add(mockMarker);

        when(markerDao.getMarkersByPlatformMarkerNameTuples(anyList())).thenReturn(mockMarkers);
        when(cvDao.getModifiedStatus()).thenReturn(new Cv());
        when(contactDao.getContactByUsername("test-editor")).thenReturn(new Contact());
        ArgumentCaptor<MarkerGroup> arg = ArgumentCaptor.forClass(MarkerGroup.class);

        when(markerGroupDao.updateMarkerGroup(any(MarkerGroup.class))).thenReturn(new MarkerGroup());

        markerGroupServiceImpl.mapMarkers(123, inputMarkers, "test-editor");
        verify(markerGroupDao).updateMarkerGroup(arg.capture());
       
        assertTrue( arg.getValue().getMarkers() != null);
        assertTrue( arg.getValue().getMarkers().get("4") != null);
    }

    @Test(expected = GobiiException.class)
    public void testMapMarkersTooManyMarkers() throws Exception {

        MarkerDTO markerDTO = new MarkerDTO();
        markerDTO.setMarkerName("test1");
        markerDTO.setPlatformName("KASP");
        markerDTO.setFavorableAlleles(new String[] { "G", "C", "A" });

        List<MarkerDTO> inputMarkers = new ArrayList<>();
        for (int i=0; i < 1001; i++) inputMarkers.add(markerDTO);

        markerGroupServiceImpl.mapMarkers(123, inputMarkers, "test-editor");


      
    }

}