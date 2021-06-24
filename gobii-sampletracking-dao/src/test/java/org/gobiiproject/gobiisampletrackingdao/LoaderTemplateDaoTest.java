package org.gobiiproject.gobiisampletrackingdao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.LoaderTemplate;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiimodel.types.GobiiLoaderPayloadTypes;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/test-config.xml" })
@Transactional
@Slf4j
@Ignore
public class LoaderTemplateDaoTest {
    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private LoaderTemplateDao loaderTemplateDao;

    @Autowired
    private CvDao cvDao;

    DaoTestSetUp daoTestSetUp;

    @Before
    public void createTestData() {
        daoTestSetUp = new DaoTestSetUp(em, cvDao);
        daoTestSetUp.createTestLoaderTemplates(10);
        em.flush();
    }

    @Test
    public void createLoaderTemplateTest() throws Exception {
        Cv templateType = cvDao.getCvs(
            "markers",
            CvGroupTerm.CVGROUP_PAYLOADTYPE.getCvGroupName(),
            null).get(0);

        String templateString = "{\n" +
            "        \"markerName\": [\"marker_name\"],\n" +
            "        \"linkageGroupName\": [\"chrom\"],\n" +
            "        \"markerStart\": [\"pos\"],\n" +
            "        \"markerStop\": [\"pos\"]\n" +
            "    }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode template = mapper.readTree(templateString);

        LoaderTemplate loaderTemplate = new LoaderTemplate();

        loaderTemplate.setTemplateName(RandomStringUtils.random(7, true, false));

        loaderTemplate.setCreatedBy(daoTestSetUp.getCreatedContacts().get(0));
        loaderTemplate.setModifiedBy(daoTestSetUp.getCreatedContacts().get(0));

        loaderTemplate.setCreatedDate(new Date());
        loaderTemplate.setModifiedDate(new Date());
        loaderTemplate.setTemplate(template);
        loaderTemplate.setTemplateType(templateType);

        LoaderTemplate result = loaderTemplateDao.create(loaderTemplate);

        assertTrue(result.getTemplateId() > 0);

    }

    @Test
    public void listLoaderTemplateTest() {
        List<LoaderTemplate> filteredLoaderTemplates = loaderTemplateDao.list(
            10, 0, GobiiLoaderPayloadTypes.MARKERS);
        for(LoaderTemplate loaderTemplate : filteredLoaderTemplates) {
            assertTrue("Filter by payload type failed",
                loaderTemplate.getTemplateType().getTerm().equals(
                    GobiiLoaderPayloadTypes.MARKERS.getTerm()));
        }

    }

}
