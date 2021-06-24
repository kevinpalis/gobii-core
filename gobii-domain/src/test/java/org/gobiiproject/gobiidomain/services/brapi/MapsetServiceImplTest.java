package org.gobiiproject.gobiidomain.services.brapi;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Mapset;
import org.gobiiproject.gobiisampletrackingdao.GobiiDaoException;
import org.gobiiproject.gobiisampletrackingdao.MapsetDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class MapsetServiceImplTest {

    @InjectMocks
    private MapsetServiceImpl mapsetBrapiService;

    @Mock
    private MapsetDaoImpl mapsetDao;

    MockSetup mockSetup;

    Integer pageSize = 10;
    Integer pageNum = 0;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockSetup = new MockSetup();
    }



    @Test
    public void getMapsTest() throws Exception {

        mockSetup.createMockMapSets(pageSize);

        Integer testPageSize = pageSize - 2;

        when(
            mapsetDao.getMapsetsWithCounts(pageSize, 0,
                null, null)
        ).thenReturn(mockSetup.mockMapSets.subList(0, testPageSize));

        PagedResult<MapsetDTO>  mapsPageResult = mapsetBrapiService.getMapSets(pageSize, 0, null);

        assertEquals("Page size Test failed", testPageSize, mapsPageResult.getCurrentPageSize());

        assertEquals("Page number Test failed", pageNum, mapsPageResult.getCurrentPageNum());

        for(int i = 0; i < testPageSize; i++) {
            testMainFieldMapping(mapsPageResult.getResult().get(i), mockSetup.mockMapSets.get(i));
        }

        GobiiDomainException ge = assertThrows(GobiiDomainException.class, () ->
            mapsetBrapiService.getMapSets(null, 0, null));

        assertEquals("PageSize exception not thrown", ge.getCause().getLocalizedMessage(),
            "pageSize : Required non null");

        when(
            mapsetDao.getMapsetsWithCounts(any(Integer.TYPE), any(Integer.TYPE),
                any(Integer.TYPE), any(Integer.TYPE))
        ).thenThrow(new GobiiDaoException(""));

        assertThrows(GobiiException.class, () -> {
            mapsetBrapiService.getMapSets(null, null, null);
        });



    }



    @Test
    public void getMapByIdTest() throws Exception {

        mockSetup.createMockMapSets(1);

        when(
            mapsetDao.getMapsetWithCountsById(any(Integer.TYPE))
        ).thenReturn(mockSetup.mockMapSets.get(0));

        MapsetDTO mapsetDTO = mapsetBrapiService.getMapSetById(
            mockSetup.mockMapSets.get(0).getMapsetId());

        testMainFieldMapping(mapsetDTO, mockSetup.mockMapSets.get(0));

        GobiiDomainException ge = assertThrows(GobiiDomainException.class, () ->
            mapsetBrapiService.getMapSetById(null));

        assertEquals("MapDbId null exception not thrown", ge.getCause().getLocalizedMessage(),
            "mapDbId : Required non null");
        when(
            mapsetDao.getMapsetWithCountsById(any(Integer.TYPE))
        ).thenThrow(new GobiiDomainException(""));

        assertThrows(GobiiDomainException.class, () ->
            mapsetBrapiService.getMapSetById(any(Integer.TYPE)));
    }

    private void testMainFieldMapping(MapsetDTO mapsetDTO, Mapset mapset) {

        assertEquals("MapSetId : MapDbId mapping failed",
            mapsetDTO.getMapDbId(), mapset.getMapsetId());
        assertEquals("MapSetName : MapName mapping failed",
            mapsetDTO.getMapName(), mapset.getMapsetName());
        assertEquals("MapSetDescription : MapComments mapping failed",
            mapsetDTO.getComments(), mapset.getMapsetDescription());
        assertEquals("MapSetTypeTerm : MapType mapping failed",
            mapsetDTO.getType(), mapset.getType().getTerm());
        assertEquals("MapSetLinkageGroupCount : MapLinkageGroupCount mapping failed",
            mapsetDTO.getLinkageGroupCount(), mapset.getLinkageGroupCount());
        assertEquals("MapSetMarkerCount : MapMarkerCount mapping failed",
            mapsetDTO.getMarkerCount(), mapset.getMarkerCount());

    }
}
