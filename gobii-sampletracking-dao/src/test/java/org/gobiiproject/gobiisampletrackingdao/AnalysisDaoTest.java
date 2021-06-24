package org.gobiiproject.gobiisampletrackingdao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang.RandomStringUtils;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *  @Transactional mean if you annotate your test suite with it? Well it means
 *  that every test method in your suite is surrounded by an overarching
 *  Spring transaction. This transaction will be rolled back at the end of the
 *  test method regardless of it's outcome.
 *  https://www.marcobehler.com/2014/06/25/should-my-tests-be-transactional
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test-config.xml"})
@Transactional
public class AnalysisDaoTest {

    @Autowired
    private  CvDao cvDao;

    @Autowired
    private AnalysisDao analysisDao;

    @PersistenceContext
    protected EntityManager em;

    static final Integer testPageSize = 10;

    DaoTestSetUp daoTestSetUp;

    @Before
    public void createTestData() {

        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestAnalyses(testPageSize);
        em.flush();
    }


    @Test
    public void getAnalysesByAnalysisIdsTest() {

        Set<Integer> createdAnalysisIds = new HashSet<>();

        for(Analysis analysis : daoTestSetUp.getCreatedAnalyses()) {
            createdAnalysisIds.add(analysis.getAnalysisId());
        }

        List<Analysis> analyses = analysisDao.getAnalysesByAnalysisIds(
                createdAnalysisIds);

        for(Analysis analysis : analyses) {
            assertTrue("Failed Get Analysis by Ids Dao. " +
                            "Not fetching a valid id",
                    createdAnalysisIds.contains(analysis.getAnalysisId()));
        }

    }

    @Test
    public void testCreateUpdatedAndDeleteAnalysis() throws Exception {
        java.util.Random r = new java.util.Random();
        Analysis analysis = new Analysis();
        analysis.setAnalysisName(RandomStringUtils.random(10, true, true));
        analysis.setAlgorithm(RandomStringUtils.random(10, true, true));
        analysis.setDescription(RandomStringUtils.random(20, true, true));
        List<Cv> cvs = cvDao.getCvListByCvGroup(CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName(), null);
        analysis.setType(cvs.get(r.nextInt(cvs.size())));
        analysis.setStatus(cvDao.getNewStatus());
        analysis = analysisDao.createAnalysis(analysis);
        
        assertTrue("Analysis not created", analysis != null && analysis.getAnalysisId() > 0);

        assertTrue("Program is already set", LineUtils.isNullOrEmpty(analysis.getProgram()));
        String dummyProgram = RandomStringUtils.random(10, true, true);
        analysis.setProgram(dummyProgram);
        analysis = analysisDao.updateAnalysis(analysis);
        assertEquals("Incorrect program name", analysis.getProgram(), dummyProgram);

        //delete
        Integer id = analysis.getAnalysisId();
        analysisDao.deleteAnalysis(analysis);

        analysis = analysisDao.getAnalysis(id);
        assertNull("analysis should be null after delete", analysis);

    }

   


}
