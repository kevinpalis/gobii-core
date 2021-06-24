package org.gobiiproject.gobiisampletrackingdao;

import static junit.framework.TestCase.assertTrue;

import java.math.BigDecimal;
import java.util.*;

import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.entity.MarkerLinkageGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * Test cases for MarkerDaoImpl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test-config.xml"})
@Transactional
public class MarkerDaoTest {

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private MarkerDao markerDao;

    @Autowired
    private CvDao cvDao;

    DaoTestSetUp daoTestSetUp;

    final int testPageSize = 10;

    Random random = new Random();

    @Before
    public void createTestData() {
        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestMarkers(testPageSize);
        em.flush();
    }

    @Test
    public void testGetMarkers() {

        Integer rowOffset = 0;

        List<Marker> markers = markerDao.getMarkers(testPageSize, rowOffset, null, null);

        assertTrue("Empty marker list",markers.size() > 0);

        if(markers.size() > 0) {

            int pageSize = markers.size() - 1;

            List<Marker> markersPaged = markerDao.getMarkers(pageSize, rowOffset, null, null);

            assertTrue("marker result list size not equal to the page size",
                markersPaged.size() == pageSize);
        }
    }

    @Test
    public void testGetMarkersByDatasetId() {

        Integer testDatasetId = daoTestSetUp.getCreatedDatasets()
            .get(random.nextInt(daoTestSetUp.getCreatedDatasets().size())).getDatasetId();

        List<Marker> markers = markerDao.getMarkersByDatasetId(testDatasetId, testPageSize, 0);

        assertTrue("marker result list size not equal to the page size",
            markers.size() <= testPageSize);

        for(Marker marker : markers) {
            assertTrue(marker.getDatasetMarkerIdx().has(testDatasetId.toString()));
        }


    }

    @Test
    public void testGetMarkersByMap() {

        daoTestSetUp.createTestMarkerLinkageGroups(10);
        em.flush();

        MarkerLinkageGroup testMarkerLinkageGroup = daoTestSetUp
            .getCreatedMarkerLinkageGroups()
            .get(random.nextInt(daoTestSetUp.getCreatedMarkerLinkageGroups().size()));

        Integer testDatasetId = Integer.parseInt(testMarkerLinkageGroup
            .getMarker().getDatasetMarkerIdx().fieldNames().next());

        Integer testMapsetId = testMarkerLinkageGroup.getLinkageGroup().getMapset().getMapsetId();


        List<Marker> markers = markerDao.getMarkersByMap(
            testPageSize, 0, testMapsetId, null,
            null, null, null, null,
            testDatasetId);

        assertTrue("marker result list size not equal to the page size",
            markers.size() <= testPageSize);

        for(Marker marker : markers) {
            assertTrue(marker.getDatasetMarkerIdx().has(testDatasetId.toString()));
        }

        // Test minPosition
        BigDecimal testMinPos = testMarkerLinkageGroup.getStart();

        Set<Integer> markersHigherThanMinPos = new HashSet<>();

        for(MarkerLinkageGroup markerLinkageGroup : daoTestSetUp.getCreatedMarkerLinkageGroups()) {
            if(markerLinkageGroup.getStart().compareTo(testMinPos) >= 0 &&
                markerLinkageGroup.getLinkageGroup().getMapset().getMapsetId() == testMapsetId) {
                markersHigherThanMinPos.add(markerLinkageGroup.getMarker().getMarkerId());
            }
        }

        markers = markerDao.getMarkersByMap(
            testPageSize, 0, testMapsetId, null,
            null, null, testMinPos, null,
            null);


        for(Marker marker : markers) {
            markersHigherThanMinPos.remove(marker.getMarkerId());
        }

        assertTrue(markersHigherThanMinPos.size() == 0);


    }

    @Test
    public void testGetMarkersByMarkerIdCursor() {


        Integer rowOffset = 0;

        List<Marker> markers = markerDao.getMarkers(testPageSize, rowOffset, null, null);

        assertTrue("Empty markers list", markers.size() > 0 && markers.size() <= testPageSize);

        Integer markerIdCursor = markers.get(random.nextInt(markers.size())).getMarkerId();

        List<Marker> markersByMarkerIdCursor =
            markerDao.getMarkersByMarkerIdCursor(testPageSize, markerIdCursor, null, null);

        for(Marker marker : markersByMarkerIdCursor) {
            assertTrue("Get Markers By MarkerId cursor failed",
                marker.getMarkerId() > markerIdCursor);
        }


    }

    @Test
    public void testGetMarkersByMarkerIds() {

        Set<Integer> markerIds = new HashSet<>();

        for(Marker mockMarker :
            daoTestSetUp.getCreatedMarkers()
                .subList(0, (int) Math.ceil((double) testPageSize/2))) {
            markerIds.add(mockMarker.getMarkerId());
        }

        List<Marker> markers = markerDao.getMarkersByMarkerIds(markerIds);

        assertTrue("Get Markers by MarkerIds failed", markers.size() <= markerIds.size());

        for(Marker marker : markers) {
            assertTrue("Get Markers by MarkerIds failed",
                markerIds.contains(marker.getMarkerId()));
        }
    }

    @Test
    public void testGetMarkersByMarkerNames() {

        Set<String> markerNames = new HashSet<>();

        for(Marker mockMarker :
            daoTestSetUp.getCreatedMarkers()
                .subList(0, (int) Math.ceil((double) testPageSize/2))) {
            markerNames.add(mockMarker.getMarkerName());
        }

        List<Marker> markers = markerDao.getMarkersByMarkerNames(markerNames);

        assertTrue("Get Markers by MarkerIds failed",
            markers.size() <= markerNames.size());

        for(Marker marker : markers) {
            assertTrue("Get Markers by MarkerIds failed",
                markerNames.contains(marker.getMarkerName()));
        }

    }

    @Test
    public void testQueryByNamesAndPlatformId() {

        Integer testPlatformId =
            daoTestSetUp
                .getCreatedMarkers()
                .get(random.nextInt(daoTestSetUp.getCreatedMarkers().size() -  1))
                .getPlatform()
                .getPlatformId();

        Set<String> testMarkerNames = new HashSet<>();

        Set<Integer> expectedMarkerIds = new HashSet<>();

        for(Marker marker : daoTestSetUp.getCreatedMarkers()) {
            if(marker.getPlatform().getPlatformId() == testPlatformId) {
                expectedMarkerIds.add(marker.getMarkerId());
            }
            testMarkerNames.add(marker.getMarkerName());
        }


        List<Marker> markers = markerDao.queryMarkersByNamesAndPlatformId(
            testMarkerNames,
            testPlatformId,
            testPageSize,
            0);

        assertTrue("query markers by names and platform id failed",
            markers.size() == expectedMarkerIds.size());

        Set<Integer> resultMarkerIds = new HashSet<>();
        for(Marker marker : markers) {
            resultMarkerIds.add(marker.getMarkerId());
        }

        resultMarkerIds.removeAll(expectedMarkerIds);

        assertTrue("query markers by names and platform id failed",
            resultMarkerIds.size() == 0);


    }

    @Test
    public void testGetMarkersByMarkerNamesAndDatasetIds() {


        Set<String> datasetIds = new HashSet<>();

        Set<String> markerNames = new HashSet<>();

        int i = 0;

        for(Dataset mockDataset : daoTestSetUp.getCreatedDatasets()) {
            if(i%2 == 0) {
                datasetIds.add(mockDataset.getDatasetId().toString());
            }
            i++;
        }

        int numFilteredMarkers = 0;

        for(Marker mockMarker :
            daoTestSetUp.getCreatedMarkers()
                .subList(0, (int) Math.ceil((double) testPageSize/2))) {

            markerNames.add(mockMarker.getMarkerName());

            for(String datasetId : datasetIds) {
                if(mockMarker.getDatasetMarkerIdx().has(datasetId)) {
                    numFilteredMarkers++;
                    break;
                }
            }

        }

        List<Marker> markers = markerDao.getMarkers(
                null, markerNames,
                datasetIds, null, null);

        assertTrue("Get Markers by names and dataset ids failes",
            markers.size() <= numFilteredMarkers);

        for(Marker marker : markers) {

            assertTrue(markerNames.contains(marker.getMarkerName()));
            boolean hasDatsetId = false;
            for(String datasetId : datasetIds) {
                if(marker.getDatasetMarkerIdx().has(datasetId)) {
                    hasDatsetId = true;
                    break;
                }
            }
            assertTrue("Get Markers by names and dataset ids failes", hasDatsetId);

        }

    }

    @Test
    public void testGetMarkersByPlatformMarkerName() {
        List<List<String>> markerTuples = new ArrayList<>();

        List<String> tuple1 = new ArrayList<>();
        tuple1.add(daoTestSetUp.getCreatedMarkers().get(0).getPlatform().getPlatformName());
        tuple1.add(daoTestSetUp.getCreatedMarkers().get(0).getMarkerName());

        markerTuples.add(tuple1);

        List<Marker> markers = markerDao.getMarkersByPlatformMarkerNameTuples(markerTuples);

        assertTrue("Get Markers by marker name and platform name failed",
            markers.size() == 1);
    }

}
