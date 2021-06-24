package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.gobiiproject.gobiimodel.entity.Mapset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test-config.xml"})
@Transactional
public class MapsetDaoTest {

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private MapsetDao mapsetDao;

    @Autowired
    private CvDao cvDao;

    Random random = new Random();

    final int testPageSize = 10;

    private DaoTestSetUp daoTestSetUp;

    @Before
    public void createTestData() {
        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestMapsets(testPageSize);
        em.flush();
    }

    @Test
    public void getMapsetsTest() {


        List<Mapset> mapsets = mapsetDao.getMapsetsWithCounts(testPageSize, 0,
                null, null);

        assertTrue("getMapset with pageSize failed",
                mapsets.size() > 0 && mapsets.size() <= testPageSize);

    }

    @Test
    public void getMapsetByIdTest() {

        Integer testMapsetId =
            daoTestSetUp
                .getCreatedMapsets()
                .get(random.nextInt(daoTestSetUp.getCreatedMapsets().size()))
                .getMapsetId();

        Mapset mapset = mapsetDao.getMapsetWithCountsById(testMapsetId);

        assertTrue(
            "Get Mapset By Id failed",
            mapset.getMapsetId() == testMapsetId);

    }

    @Test
    public void getMapsetsSimpleTest() throws Exception {
        List<Mapset> mapsets = mapsetDao.getMapsets(1000, 0, null);
        assertTrue("getMapsets fails",mapsets != null);
    }
}
