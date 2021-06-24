package org.gobiiproject.gobiisampletrackingdao;

import static junit.framework.TestCase.assertTrue;

import java.util.List;

import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test-config.xml"})
@Transactional
public class CvDaoTest {

    @Autowired
    private CvDao cvDao;



    @Test
    public void getCvListByCvGroupTest() {

        String testCvGroupName = CvGroupTerm.CVGROUP_PROJECT_PROP.getCvGroupName();
        GobiiCvGroupType testCvType = GobiiCvGroupType.GROUP_TYPE_SYSTEM;

        List<Cv> cvList = cvDao.getCvListByCvGroup(
                CvGroupTerm.CVGROUP_PROJECT_PROP.getCvGroupName(),
                GobiiCvGroupType.GROUP_TYPE_SYSTEM);

        assertTrue(cvList.size() > 0);

        for(Cv cv : cvList) {
            assertTrue("CV group name and type condition failed",
                    cv.getCvGroup().getCvGroupName().equals(testCvGroupName) &&
                    cv.getCvGroup().getCvGroupType() == testCvType.getGroupTypeId());
        }
    }

    @Test
    public void getCvByCvId() throws Exception {

        Integer cvId = 4;

        Cv cv = cvDao.getCvByCvId(cvId);

        assertTrue("Failed getCv by Id", cv.getCvId() == cvId);

    }

    @Test
    public void getCvsByCvTermAndCvGroup() {

        List<Cv> cvList = cvDao.getCvs( "new",
                CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
                GobiiCvGroupType.GROUP_TYPE_SYSTEM);

        assertTrue(cvList.size() > 0);

    }

}
