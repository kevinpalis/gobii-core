package org.gobiiproject.gobiisampletrackingdao;

import static junit.framework.TestCase.assertTrue;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
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
 * Tests are developer tests against local database with existing data.
 * TODO: Before classes need to be created for setup data for tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test-config.xml"})
@Transactional
public class MarkerLinkageGroupDaoTest {

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private MarkerLinkageGroupDao markerLinkageGroupDao;

    @Autowired
    private CvDao cvDao;

    DaoTestSetUp daoTestSetUp;

    final int testPageSize = 10;

    Random random = new Random();

    @Before
    public void createTestData() {
        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestMarkerLinkageGroups(testPageSize);
        em.flush();
    }

    @Test
    public void testGetMarkerLinkageGroupsByMapsetId() {


        Integer testMapsetId =
            daoTestSetUp
                .getCreatedMarkerLinkageGroups()
                .get(random.nextInt(daoTestSetUp.getCreatedMarkerLinkageGroups().size()))
                .getLinkageGroup()
                .getMapset()
                .getMapsetId();

        List<MarkerLinkageGroup> markerLinkageGroups = markerLinkageGroupDao.getMarkerLinkageGroups(
            testPageSize, 0, testMapsetId, null,
            null, null, null, null, null, null, null);


        assertTrue("Empty MarkerLinkageGroup list",markerLinkageGroups.size() > 0);
        assertTrue("MarkerLinkageGroups size not equal to the page size",
                markerLinkageGroups.size() <= testPageSize);

        for(MarkerLinkageGroup markerLinkageGroup : markerLinkageGroups) {
            assertTrue("Mapset Filter not working",
                    markerLinkageGroup.getLinkageGroup().getMapset().getMapsetId() == testMapsetId);
        }
    }

    @Test
    public void testGetMarkerLinkageGroupsByMarkerId() {

        Integer testMarkerId =
            daoTestSetUp
                .getCreatedMarkerLinkageGroups()
                .get(random.nextInt(daoTestSetUp.getCreatedMarkerLinkageGroups().size()))
                .getMarker()
                .getMarkerId();

        List<MarkerLinkageGroup> markerLinkageGroups = markerLinkageGroupDao.getMarkerLinkageGroups(
            testPageSize, 0, null, null, null, null,
            testMarkerId, null, null, null, null);

        assertTrue("Empty MarkerLinkageGroup list",markerLinkageGroups.size() > 0);
        assertTrue("MarkerLinkageGroups size not equal to the page size",
            markerLinkageGroups.size() <= testPageSize);

        for(MarkerLinkageGroup markerLinkageGroup : markerLinkageGroups) {
            assertTrue("Marker Id filter not working",
                markerLinkageGroup.getMarker().getMarkerId() == testMarkerId);
        }

    }

    @Test
    public void testGetMarkerLinkageGroupsByMarkerIds() {

        Set<Integer> testMarkerIds = new HashSet<>();

        for(MarkerLinkageGroup testMarkerLinkageGroup :
            daoTestSetUp.getCreatedMarkerLinkageGroups().subList(0, 4)) {
            testMarkerIds.add(testMarkerLinkageGroup.getMarker().getMarkerId());
        }


        List<MarkerLinkageGroup> markerLinkageGroups = markerLinkageGroupDao.getMarkerLinkageGroups(
            testPageSize, 0, null, null, null, null,
            testMarkerIds, null, null, null, null);

        assertTrue("Empty MarkerLinkageGroup list",markerLinkageGroups.size() > 0);
        assertTrue("MarkerLinkageGroups size not equal to the page size",
            markerLinkageGroups.size() <= testPageSize);

        for(MarkerLinkageGroup markerLinkageGroup : markerLinkageGroups) {
            assertTrue("Marker Id filter not working",
                testMarkerIds.contains(markerLinkageGroup.getMarker().getMarkerId()));
        }

    }

    @Test
    public void testGetMarkerLinkageGroupsByMinAndMaxPosition() {


        BigDecimal testMinPosition =
            daoTestSetUp
                .getCreatedMarkerLinkageGroups()
                .get(random.nextInt(daoTestSetUp.getCreatedMarkerLinkageGroups().size()))
                .getStart();

        BigDecimal testMaxPosition = testMinPosition.add(new BigDecimal("500"));

        List<MarkerLinkageGroup> markerLinkageGroups = markerLinkageGroupDao.getMarkerLinkageGroups(
            testPageSize, 0, (Integer) null, null, null, null,
            null, null, testMinPosition, testMaxPosition, null);

        assertTrue("Empty MarkerLinkageGroup list",markerLinkageGroups.size() > 0);
        assertTrue("MarkerLinkageGroups size not equal to the page size",
            markerLinkageGroups.size() <= testPageSize);

        for(MarkerLinkageGroup markerLinkageGroup : markerLinkageGroups) {

            int minPositionComparision = testMinPosition.compareTo(markerLinkageGroup.getStart());
            int maxPositionComparision = testMaxPosition.compareTo(markerLinkageGroup.getStop());

            assertTrue("Min position filter not working",
                minPositionComparision == -1 || minPositionComparision == 0);
            assertTrue("Max position filter not working",
                maxPositionComparision == 1 || maxPositionComparision == 0);

        }


    }


}
