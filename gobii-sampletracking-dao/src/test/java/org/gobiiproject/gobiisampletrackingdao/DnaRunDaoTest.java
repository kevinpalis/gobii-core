package org.gobiiproject.gobiisampletrackingdao;

import static junit.framework.TestCase.assertTrue;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test-config.xml"})
@Transactional
public class DnaRunDaoTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DnaRunDao dnaRunDao;

    @Autowired
    private CvDao cvDao;

    final int testPageSize = 10;

    Random random = new Random();

    DaoTestSetUp daoTestSetUp;

    @Before
    public void testCreateData() {
        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestDnaRuns(testPageSize);
        em.flush();
    }

    @Test
    public void testGetDnaRuns() {


        Integer rowOffset = 0;

        List<DnaRun> dnaruns = dnaRunDao.getDnaRuns(
                testPageSize, rowOffset,
                null, null,
                null, null,
                null, null,
                null, null);

        assertTrue("Empty dnaRun list: ",dnaruns.size() > 0);

        if(dnaruns.size() > 0) {

            int pageSize = dnaruns.size() - 1;

            List<DnaRun> dnaRunsPaged = dnaRunDao.getDnaRuns(
                    pageSize, rowOffset,
                    null, null,
                    null, null,
                    null, null,
                    null, null);


            assertTrue("dnarun result list size not equal to the page size",
                    dnaRunsPaged.size() == pageSize);
        }
    }

    @Test
    public void testGetDnaRunsByDatasetIdWithNoAssociations() {

        Integer rowOffset = 0;

        Integer dnaRunWithDatasetIndex = 0;

        String testDatasetId = daoTestSetUp
            .getCreatedDnaRuns()
            .get(random.nextInt(daoTestSetUp.getCreatedDnaRuns().size()))
            .getDatasetDnaRunIdx().fieldNames().next();

        for(DnaRun dnaRun : daoTestSetUp.getCreatedDnaRuns()) {
            if(dnaRun.getDatasetDnaRunIdx().has(testDatasetId)) {
                dnaRunWithDatasetIndex++;
            }
        }

        Integer datasetId = Integer.parseInt(testDatasetId);

        List<DnaRun> dnaRuns = dnaRunDao.getDnaRunsByDatasetId(
                datasetId, testPageSize,
                rowOffset, false);

        assertTrue("Empty dnaRun list: ",
            dnaRuns.size() == dnaRunWithDatasetIndex);

        for(DnaRun dnaRun : dnaRuns) {
            assertTrue("Filter by datasetId failed",
                dnaRun.getDatasetDnaRunIdx().has(datasetId.toString()));
        }
    }

    @Test
    public void testGetDnaRunsFilterByGermplasmFields() {

        Integer rowOffset = 0;

        String germplasmName = daoTestSetUp.getCreatedGermplasms()
            .get(random.nextInt(daoTestSetUp.getCreatedGermplasms().size()))
            .getGermplasmName();

        int numOfDnaRunsInGermaplasmName = 0;

        for(DnaRun dnaRun : daoTestSetUp.getCreatedDnaRuns()) {
            if(dnaRun.getDnaSample().getGermplasm().getGermplasmName()
                == germplasmName) {
                numOfDnaRunsInGermaplasmName++;
            }
        }

        List<DnaRun> dnaRuns = dnaRunDao.getDnaRuns(testPageSize, rowOffset,
                null, null,
                null, null,
                null, null,
                null, germplasmName);

        //there is only one dnarun for this germplasm
        assertTrue("DnaRunDao test setup for germplasm filter not working",
            dnaRuns.size() <= testPageSize &&
                dnaRuns.size() == numOfDnaRunsInGermaplasmName);

        for(DnaRun dnaRun : dnaRuns) {
            assertTrue("Filter By Germplasm Name Failed",
                germplasmName ==
                    dnaRun.getDnaSample().getGermplasm().getGermplasmName());
        }


        Integer germplasmId = daoTestSetUp.getCreatedGermplasms()
            .get(random.nextInt(daoTestSetUp.getCreatedGermplasms().size()))
            .getGermplasmId();

        int numOfDnaRunsInGermplasmId = 0;

        for(DnaRun dnaRun : daoTestSetUp.getCreatedDnaRuns()) {
            if(dnaRun.getDnaSample().getGermplasm().getGermplasmId()
                == germplasmId) {
                numOfDnaRunsInGermplasmId++;
            }
        }

        List<DnaRun> dnaRunsByGermplasmId =
            dnaRunDao.getDnaRuns(testPageSize, rowOffset,
                null, null,
                null, null,
                null, null,
                germplasmId, null);

        //there is only one dnarun for this germplasm
        assertTrue("DnaRunDao test setup for germplasm filter not working",
            dnaRuns.size() <= testPageSize &&
                dnaRunsByGermplasmId.size() == numOfDnaRunsInGermplasmId);


        for(DnaRun dnaRun : dnaRunsByGermplasmId) {
            assertTrue("Filter By Germplasm Id Failed",
                germplasmId ==
                    dnaRun.getDnaSample().getGermplasm().getGermplasmId());
        }
    }

    @Test
    public void testGetDnaRunsFilterByExperimentFields() {

        Integer rowOffset = 0;

        Integer experimentId = daoTestSetUp.getCreatedExperiments()
            .get(random.nextInt(daoTestSetUp.getCreatedExperiments().size()))
            .getExperimentId();

        int numOfDnaRunsInExperiments = 0;

        for(DnaRun dnaRun : daoTestSetUp.getCreatedDnaRuns()) {
            if(dnaRun.getExperiment().getExperimentId() == experimentId) {
                numOfDnaRunsInExperiments++;
            }
        }

        List<DnaRun> dnaRuns = dnaRunDao.getDnaRuns(testPageSize, rowOffset,
            null, null,
            null, experimentId,
            null, null,
            null, null);

        //there is only one dnarun for this germplasm
        assertTrue("DnaRunDao test setup for germplasm filter not working",
            dnaRuns.size() <= testPageSize &&
                dnaRuns.size() == numOfDnaRunsInExperiments);

        for(DnaRun dnaRun : dnaRuns) {
            assertTrue("Filter By Experiment Failed",
                experimentId ==
                    dnaRun.getExperiment().getExperimentId());
        }

    }

    @Test
    public void testGetDnaRunsByDatasetId() {

        Integer rowOffset = 0;

        Integer dnaRunWithDatasetIndex = 0;

        String testDatasetId = daoTestSetUp
            .getCreatedDnaRuns()
            .get(random.nextInt(daoTestSetUp.getCreatedDnaRuns().size()))
            .getDatasetDnaRunIdx().fieldNames().next();

        for(DnaRun dnaRun : daoTestSetUp.getCreatedDnaRuns()) {
            if(dnaRun.getDatasetDnaRunIdx().has(testDatasetId)) {
                dnaRunWithDatasetIndex++;
            }
        }


        Integer datasetId = Integer.parseInt(testDatasetId);

        List<DnaRun> dnarunsByDatasetId = dnaRunDao.getDnaRunsByDatasetId(
                datasetId, testPageSize, rowOffset);

        assertTrue("DnaRunDao test setup failed.",
            dnarunsByDatasetId.size() == dnaRunWithDatasetIndex);

        for(DnaRun dnaRun : dnarunsByDatasetId) {
            assertTrue("Filter by datasetId failed.",
                    dnaRun.getDatasetDnaRunIdx().has(datasetId.toString()));
        }

    }

    @Test
    public void getDnaRunsByDnaRunIds() {

        HashSet<Integer> dnarunIdSet = new HashSet<>();

        for(DnaRun dnaRun : daoTestSetUp.getCreatedDnaRuns()) {
            dnarunIdSet.add(dnaRun.getDnaRunId());
        }

        List<DnaRun> dnaruns = dnaRunDao.getDnaRunsByDanRunIds(dnarunIdSet);

        assertTrue("Failed DnaRuns Test Setup",
            dnaruns.size() <= dnarunIdSet.size()
            && dnaruns.size() > 0);

        for(DnaRun dnaRun : dnaruns) {
            assertTrue("Failed DnaRun filter by Ids",
                dnarunIdSet.contains(dnaRun.getDnaRunId()));
        }

    }


    @Test
    public void getDnaRunsByDnaRunNames() {

        Set<String> dnarunNameSet = new HashSet<>();
        Map<Integer, List<DnaRun>> experimentDnaRunMap = new HashMap<>();

        for(DnaRun dnaRun : daoTestSetUp.getCreatedDnaRuns()) {
            dnarunNameSet.add(dnaRun.getDnaRunName());
            if(!experimentDnaRunMap.containsKey(dnaRun.getExperiment().getExperimentId())) {
                experimentDnaRunMap.put(dnaRun.getExperiment().getExperimentId(),
                    new ArrayList<>());
            }
            experimentDnaRunMap.get(dnaRun.getExperiment().getExperimentId()).add(dnaRun);
        }

        assertTrue("Invalid test data for experiment filter",
            experimentDnaRunMap.size() > 0);

        Integer testExperimentId = new ArrayList<>(experimentDnaRunMap.keySet()).get(0);
        List<DnaRun> dnaruns = dnaRunDao.getDnaRunsByDnaRunNames(
            dnarunNameSet,
            testExperimentId,
            dnarunNameSet.size(),
            0);

        assertTrue("Failed Experiment Id filter",
            dnaruns.size() == experimentDnaRunMap.get(testExperimentId).size());

        for(DnaRun dnaRun : dnaruns) {
            assertTrue("Failed DanRun filter by dnarun names",
                dnarunNameSet.contains(dnaRun.getDnaRunName()));
            assertTrue("Failed Experiment Id filter",
                dnaRun.getExperiment().getExperimentId().equals(testExperimentId));
        }


    }


}
