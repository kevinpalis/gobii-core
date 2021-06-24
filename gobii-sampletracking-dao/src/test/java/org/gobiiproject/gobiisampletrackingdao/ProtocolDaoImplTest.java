package org.gobiiproject.gobiisampletrackingdao;

import org.apache.commons.lang.RandomStringUtils;
import org.gobiiproject.gobiimodel.entity.Protocol;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test-config.xml"})
@Transactional
public class ProtocolDaoImplTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProtocolDao protocolDao;

    @Autowired
    private CvDao cvDao;

    final int testPageSize = 10;

    Random random = new Random();

    DaoTestSetUp daoTestSetUp;

    @Before
    public void testCreateData() {
        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestProtocols(testPageSize);
        em.flush();
    }

    @Test(expected = GobiiDaoException.class)
    public void getProtocolByIdTest()  {

        Protocol testProtocol =
            daoTestSetUp
                .getCreatedProtocols()
                .get(random.nextInt(daoTestSetUp.getCreatedProtocols().size() - 1));

        Protocol protocol = protocolDao.getProtocolById(testProtocol.getProtocolId());

        assertTrue("Get Protocol by Id failed",
            protocol.getProtocolId() == testProtocol.getProtocolId());

        assertTrue("Get Protocol by Id failed: protocol name mismatch",
            protocol.getName() == testProtocol.getName());

        assertTrue("Get Protocol by Id failed: protocol description failed",
            protocol.getDescription() == testProtocol.getDescription());

        // Assuming primary key gets incremented by 1
        Integer invalidId = daoTestSetUp
            .getCreatedProtocols()
            .get(daoTestSetUp.getCreatedProtocols().size() - 1)
            .getProtocolId() + 10;

        protocolDao.getProtocolById(invalidId);

    }

    @Test
    public void getProtocolsTest() {

        int pageSize = 5;

        List<Protocol> protocols = protocolDao.getProtocols(pageSize, 0, null);

        assertTrue("Get Protocols failed: pageSize not correct",
            protocols.size() <= pageSize && protocols.size() > 0);

        Integer platformId =
            daoTestSetUp
                .getCreatedProtocols()
                .get(random.nextInt(daoTestSetUp.getCreatedProtocols().size() - 1))
                .getPlatform()
                .getPlatformId();

        long numOfProtocolsFWithPlatformId =
            daoTestSetUp
                .getCreatedProtocols()
                .stream()
                .filter(protocol -> protocol.getPlatform().getPlatformId() == platformId)
                .count();

        List<Protocol> protocolsFiltered = protocolDao.getProtocols(pageSize, 0, platformId);

        assertTrue("Filter by platformId failed",
            protocolsFiltered.size() == numOfProtocolsFWithPlatformId);

        protocolsFiltered.forEach(protocol -> {
            assertTrue("Filter by platformId failed",
                protocol.getPlatform().getPlatformId() == platformId);
        });

    }

    @Test(expected = GobiiDaoException.class)
    public void createProtocolTest() {

        Protocol protocolToCreate = new Protocol();
        protocolToCreate.setName(RandomStringUtils.random(7, true, true));
        protocolToCreate.setDescription(RandomStringUtils.random(7, true, true));
        Protocol protocol = protocolDao.createProtocol(protocolToCreate);

        assertTrue("Create Protocol failed", protocol.getProtocolId() != null);

        assertTrue("Create protocol failed: protocolName mismatch",
            protocol.getName() == protocolToCreate.getName());

        assertTrue("Create protocol failed: protocolDescription mismatch",
            protocol.getDescription().equals(protocolToCreate.getDescription()));

        // Expected to throw exception
        Protocol protocolToCreate2 = new Protocol();
        protocolToCreate.setDescription(RandomStringUtils.random(7, true, true));
        protocolDao.createProtocol(protocolToCreate2);
    }

    @Test(expected = GobiiDaoException.class)
    public void patchProtocolTest() {

        Protocol testProtocol =
            daoTestSetUp
                .getCreatedProtocols()
                .get(random.nextInt(daoTestSetUp.getCreatedProtocols().size() - 1));

        testProtocol.setName(RandomStringUtils.random(7, true, true));

        Protocol updatedProtocol = protocolDao.patchProtocol(testProtocol);

        assertTrue("Updated protocol failed",
            testProtocol.getProtocolId().equals(updatedProtocol.getProtocolId()));

        assertTrue("Updated protocol failed",
            testProtocol.getName().equals(updatedProtocol.getName()));

        // Expected to throw exception
        String existingName = testProtocol.getName();
        testProtocol.setName(null);
        updatedProtocol = protocolDao.createProtocol(testProtocol);

        assertTrue("Updated protocol failed: name gets updated for null value",
            updatedProtocol.getName().equals(existingName));

    }

    @Test(expected = GobiiDaoException.class)
    public void deleteProtocolTest() {

        Protocol testProtocol =
            daoTestSetUp
                .getCreatedProtocols()
                .get(random.nextInt(daoTestSetUp.getCreatedProtocols().size() - 1));

        protocolDao.deleteProtocol(testProtocol);

        protocolDao.deleteProtocol(testProtocol);

    }


}
