package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Germplasm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test-config.xml"})
@Transactional
public class GermplasmDaoTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private GermplasmDao germplasmDao;

    @Autowired
    private CvDao cvDao;

    final int testPageSize = 10;

    Random random = new Random();

    DaoTestSetUp daoTestSetUp;

    @Before
    public void testCreateData() {
        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestGermplasms(testPageSize);
        em.flush();
    }


    @Test
    public void getGermplasmByExternalCodes() {

        Set<String> germplasmExternalCodes = new HashSet<>();

        for(Germplasm germplasm : daoTestSetUp.getCreatedGermplasms()) {
            germplasmExternalCodes.add(germplasm.getExternalCode());
        }


        List<Germplasm> germplasms = germplasmDao.getGermplamsByExternalCodes(
            germplasmExternalCodes,
            germplasmExternalCodes.size(),
            0);

        for(Germplasm germplasm : germplasms) {
            assertTrue("Failed to get germplasms by external codes",
                germplasmExternalCodes.contains(germplasm.getExternalCode()));
        }


    }


}
