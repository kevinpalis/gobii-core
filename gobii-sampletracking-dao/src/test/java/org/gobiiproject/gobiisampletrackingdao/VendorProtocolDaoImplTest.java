package org.gobiiproject.gobiisampletrackingdao;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.gobiiproject.gobiimodel.entity.VendorProtocol;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test-config.xml"})
@Transactional
public class VendorProtocolDaoImplTest {
    
    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private CvDao cvDao;

    @Autowired
    private VendorProtocolDao vendorProtocolDao;

    DaoTestSetUp daoTestSetup;

    Random random = new Random();

    final Integer testPageSize = 10;

    @Before
    public void createTestData() {
        daoTestSetup = new DaoTestSetUp(em, cvDao);
        daoTestSetup.createTestVendorProtocols(10);
        em.flush();
    }

    @Test
    public void testGetVendorProtocols() {
        List<VendorProtocol> vps = vendorProtocolDao.getVendorProtocols(0, 1000);
        assertTrue(vps.size() >= testPageSize);
    }
}
