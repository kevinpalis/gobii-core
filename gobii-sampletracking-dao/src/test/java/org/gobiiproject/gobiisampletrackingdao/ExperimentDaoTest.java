package org.gobiiproject.gobiisampletrackingdao;

import static junit.framework.TestCase.assertTrue;

import java.util.List;
import java.util.Random;

import org.gobiiproject.gobiimodel.entity.Experiment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/test-config.xml" })
@Transactional
@Slf4j
public class ExperimentDaoTest {

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private ExperimentDao experimentDao;

    @Autowired
    private CvDao cvDao;

    Random random = new Random();

    final int testPageSize = 10;

    DaoTestSetUp daoTestSetUp;

    @Before
    public void createTestData() {
        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestExperiments(testPageSize);
        em.flush();
    }

    @Test
    public void getExperimentsTest() {

        List<Experiment> experiments = experimentDao.getExperiments(testPageSize, 0, null);

        assertTrue("getExperiments by pageSize failed.", experiments.size() > 0 && experiments.size() == testPageSize);

    }

    @Test
    public void testFilterExperimentsByProjectId() {

        Integer testProjectId = daoTestSetUp.getCreatedProjects()
                .get(random.nextInt(daoTestSetUp.getCreatedProjects().size())).getProjectId();

        List<Experiment> experimentsByProjectId = experimentDao.getExperiments(testPageSize, 0, testProjectId);

        int numOfExperimentsInProject = 0;

        for (Experiment experiment : daoTestSetUp.getCreatedExperiments()) {
            if (experiment.getProject().getProjectId() == testProjectId) {
                numOfExperimentsInProject++;
            }
        }

        assertTrue("Filter Experiments by ProjectId failed.",
                numOfExperimentsInProject == experimentsByProjectId.size());

        for (Experiment experiment : experimentsByProjectId) {
            assertTrue("Filter Experiments by projectId failed",
                    experiment.getProject().getProjectId().equals(testProjectId));
        }
    }
}
