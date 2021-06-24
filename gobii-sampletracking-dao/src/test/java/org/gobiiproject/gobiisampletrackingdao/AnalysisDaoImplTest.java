package org.gobiiproject.gobiisampletrackingdao;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.gobiiproject.gobiimodel.entity.Analysis;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/test-config.xml" })
@Slf4j
@Transactional
public class AnalysisDaoImplTest {

    @Autowired
    private AnalysisDao analysisDao;

    @Autowired
    private CvDao cvDao;

    @PersistenceContext
    protected EntityManager em;
    
    Random random = new Random();

    final int testPageSize = 10;

    private DaoTestSetUp daoTestSetUp;

    @Before
    public void createTestData() {
        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestAnalyses(testPageSize);
        em.flush();
    }

    @Test
    public void testListAnalysis() {
        log.info("Test listing");
        List<Analysis> results = analysisDao.getAnalyses(0, 10);
        assertTrue("getAnalyses results is null", results != null);
        log.debug("Size: " + results.size());
        assertTrue("results size is 0",  results.size() > 0);
    }

    @Test
    public void testListAnalysesByIds() {
        List<Analysis> analyses = daoTestSetUp.getCreatedAnalyses();
        Set<Integer> analysisIds = new HashSet<>();

        //let's get at most three random ids 
        for (int i = 0; i < 3; i++) {
            analysisIds.add( analyses.get(random.nextInt(analyses.size())).getAnalysisId());
        }

        List<Analysis> analysesByIds = analysisDao.getAnalysesByAnalysisIds(analysisIds);
        assertTrue("Result is null", analysesByIds != null);
        assertTrue("Query did not get same analyses size", analysesByIds.size() == analysisIds.size());  
    }

}