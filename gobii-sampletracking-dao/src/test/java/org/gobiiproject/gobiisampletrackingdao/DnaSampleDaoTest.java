package org.gobiiproject.gobiisampletrackingdao;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiimodel.entity.DnaSample;
import org.gobiiproject.gobiimodel.entity.Germplasm;
import org.gobiiproject.gobiimodel.entity.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test-config.xml"})
@Transactional
public class DnaSampleDaoTest {

    @Autowired
    private DnaSampleDao dnaSampleDao;

    @Autowired
    private CvDao cvDao;

    @PersistenceContext
    protected EntityManager em;

    Integer testPageSize = 10;

    private DaoTestSetUp daoTestSetUp;

    Random random = new Random();

    @Before
    public void createTestData() {
        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestDnaSamples(testPageSize);
        em.flush();
    }


    @Test
    public void testGetDnaSamples() {

        List<DnaSample> dnaSamples =
            dnaSampleDao.getDnaSamples(testPageSize, 0);

        assertTrue("Empty dnasample list",dnaSamples.size() > 0);
        assertTrue("dnaSamples result list size not equal to the page size",
            dnaSamples.size() == testPageSize);

    }

    @Test
    public void testGetDnaSampleByDnaSampleId() {

        Integer dnaSampleQueryId =
            daoTestSetUp
                .getCreatedDnaSamples()
                .get(random.nextInt(daoTestSetUp.getCreatedDnaSamples().size()))
                .getDnaSampleId();

        assertNotNull("DnaSample Test Setup with null dnaSampleId", dnaSampleQueryId);

        DnaSample dnaSamplesByDnaSampleId =
            dnaSampleDao.getDnaSampleByDnaSampleId(dnaSampleQueryId);

        assertEquals("Get DanSample by dnaSampleId failed",
            dnaSamplesByDnaSampleId.getDnaSampleId(), dnaSampleQueryId);


    }

    @Test
    public void testGetDnaSamplesByGermplasmId() {


        Integer germplasmQueryId =
            daoTestSetUp
                .getCreatedGermplasms()
                .get(random.nextInt(daoTestSetUp.getCreatedGermplasms().size()))
                .getGermplasmId();

        assertNotNull("DnaSampleDao test setup failed. Null germplasmId", germplasmQueryId);

        int numOfDnaSamplesWithGermplasmIds = 0;

        for(DnaSample dnaSample : daoTestSetUp.getCreatedDnaSamples()) {
            if(dnaSample.getGermplasm().getGermplasmId() == germplasmQueryId) {
                numOfDnaSamplesWithGermplasmIds += 1;
            }
        }

        List<DnaSample> dnaSamplesByGermplasmId =
            dnaSampleDao.getDnaSamplesByGermplasmId(germplasmQueryId, testPageSize, 0);

        assertTrue("Filter by germplasmDbId failed",
            dnaSamplesByGermplasmId.size() == numOfDnaSamplesWithGermplasmIds);

        for(DnaSample dnaSampleResult : dnaSamplesByGermplasmId) {

            assertEquals("Filter by germplasmDbId failed.",
                dnaSampleResult.getGermplasm().getGermplasmId(), germplasmQueryId);
        }
    }

    @Test
    public void testGetDnaSamplesByGermplasmExternalCode() {

        String germplasmExternalCode =
            daoTestSetUp.getCreatedGermplasms()
                .get(random.nextInt(daoTestSetUp.getCreatedGermplasms().size()))
                .getExternalCode();

        assertNotNull("DnaSampleDao test setup failed. Null germplasmExternalCode.",
            germplasmExternalCode);

        int numOfDnaSamplesWithGermplasmExternalCodes = 0;

        for(DnaSample dnaSample : daoTestSetUp.getCreatedDnaSamples()) {
            if(dnaSample.getGermplasm().getExternalCode() == germplasmExternalCode) {
                numOfDnaSamplesWithGermplasmExternalCodes += 1;
            }
        }

        List<DnaSample> dnaSamplesByGermplasmCode =
            dnaSampleDao.getDnaSamplesByGermplasmExternalCode(
                germplasmExternalCode, testPageSize, 0);

        assertTrue("Filter by germplasmExternalCode failed",
            dnaSamplesByGermplasmCode.size() == numOfDnaSamplesWithGermplasmExternalCodes);

        for(DnaSample dnaSampleResult : dnaSamplesByGermplasmCode) {

            assertEquals("Filter by germplasmExternalCode.",
                dnaSampleResult.getGermplasm().getExternalCode(), germplasmExternalCode);
        }
    }

    @Test
    public void testGetDnaSamplesByProjectIds() {

        List<DnaSample> dnaSamplesMockSubList = daoTestSetUp.getCreatedDnaSamples()
            .subList(0, random.nextInt(daoTestSetUp.getCreatedDnaSamples().size() - 4) + 3);

        Set<Integer> projectIds = dnaSamplesMockSubList
            .stream()
            .map(DnaSample::getProject)
            .map(Project::getProjectId)
            .collect(Collectors.toSet());

        long expectedDnaSamplesSize =
            daoTestSetUp.getCreatedDnaSamples()
            .stream()
                .filter(dnaSample -> projectIds.contains(dnaSample.getProject().getProjectId()))
            .count();

        List<DnaSample> dnaSamples = dnaSampleDao.getDnaSamples(null, null, null,
            null, null, null, projectIds, testPageSize, 0);

        assertTrue("Get dna samples filter by project ids failed. Count mismatch.",
            dnaSamples.size() == expectedDnaSamplesSize);

        dnaSamples.forEach(dnaSample -> {
            assertTrue("Filter by ProjectIds failed",
                projectIds.contains(dnaSample.getProject().getProjectId()));
        });
    }

    @Test
    public void testQueryByNameAndNum() {

        List<DnaSample> dnaSamplesMockSubList = daoTestSetUp.getCreatedDnaSamples()
            .subList(0, random.nextInt(daoTestSetUp.getCreatedDnaSamples().size() - 4) + 3);

        List<DnaSample> dnaSamples = dnaSampleDao.queryByNameAndNum(dnaSamplesMockSubList, null);

        assertTrue("Get dna samples filter by project ids failed. Count mismatch.",
            dnaSamples.size() == dnaSamplesMockSubList.size());

        Set<String> sampleNameNumsSet =
            dnaSamplesMockSubList
                .stream()
                .map(dnaSample -> {
                    if(StringUtils.isNotEmpty(dnaSample.getDnaSampleNum())) {
                        return dnaSample.getDnaSampleName() + dnaSample.getDnaSampleNum();
                    }
                    return dnaSample.getDnaSampleName();
                }).collect(Collectors.toSet());

        dnaSamples.forEach(dnaSample -> {
            String dnaSampleNameNum = dnaSample.getDnaSampleName();
            if(StringUtils.isNotEmpty(dnaSample.getDnaSampleNum())) {
                dnaSampleNameNum += dnaSample.getDnaSampleNum();
            }
            assertTrue(
                "Query by Name and num failed",
                sampleNameNumsSet.contains(dnaSampleNameNum));
        });
    }

    @Test
    public void testFilterDnaSamplesByProjectIdsAndGermplasmNames() {

        List<DnaSample> dnaSamplesByProject = daoTestSetUp.getCreatedDnaSamples()
            .subList(0, random.nextInt(daoTestSetUp.getCreatedDnaSamples().size()-4) + 3);

        List<DnaSample> dnaSamplesByGermplsm = dnaSamplesByProject
            .subList(0, random.nextInt(dnaSamplesByProject.size()-2)+1);

        Set<Integer> projectIds = dnaSamplesByProject
            .stream()
            .map(DnaSample::getProject)
            .map(Project::getProjectId)
            .collect(Collectors.toSet());

        Set<String> germplasmNames = dnaSamplesByGermplsm
            .stream()
            .map(DnaSample::getGermplasm)
            .map(Germplasm::getGermplasmName)
            .collect(Collectors.toSet());

        long expectedDnaSamplesSize =
            daoTestSetUp.getCreatedDnaSamples()
                .stream()
                .filter(dnaSample -> projectIds.contains(dnaSample.getProject().getProjectId()) &&
                    germplasmNames.contains(dnaSample.getGermplasm().getGermplasmName()))
                .count();

        List<DnaSample> dnaSamples = dnaSampleDao.getDnaSamples(null, null, null,
            null,germplasmNames, null, projectIds, testPageSize, 0);

        assertTrue("Get dna samples filter by project ids and germplasm names failed. " +
                "Count mismatch.",
            dnaSamples.size() == expectedDnaSamplesSize);

        dnaSamples.forEach(dnaSample -> {
            assertTrue("Filter by ProjectIds failed",
                projectIds.contains(dnaSample.getProject().getProjectId()));
            assertTrue("Filter by Germplasm Names failed",
                germplasmNames.contains(dnaSample.getGermplasm().getGermplasmName()));
        });
    }
}
