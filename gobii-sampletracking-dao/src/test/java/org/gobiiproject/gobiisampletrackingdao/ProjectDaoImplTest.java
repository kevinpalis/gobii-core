/**
 * ProjectDaoImplTest.java
 * 
 * This test requires a test db.
 */
package org.gobiiproject.gobiisampletrackingdao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/test-config.xml" })
@Slf4j
@Transactional
public class ProjectDaoImplTest {

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private CvDao cvDao;

    @Autowired
    private ProjectDao v3ProjectDao;

    DaoTestSetUp daoTestSetup;

    Random random = new Random();

    final Integer testPageSize = 10;

    @Before
    public void createTestData() {
        daoTestSetup = new DaoTestSetUp(em, cvDao);
        daoTestSetup.createTestProjects(testPageSize);
        em.flush();
    }

    private Contact getRandomContact() {
        //get contact
        List<Contact> contacts = daoTestSetup.getCreatedContacts();
        assert contacts.size() > 0;

        return contacts.get(random.nextInt(contacts.size()));
    }

    private Project createProject(
        String name,
        String description,
        Map<String, String> props
    ) {
        Project toBeCreated = new Project();
        toBeCreated.setProjectName(name);
        toBeCreated.setProjectDescription(description);
        
        //get contact

        Contact contact = this.getRandomContact();
        toBeCreated.setContact(contact);
        toBeCreated.setCreatedBy(contact.getContactId());
        toBeCreated.setCreatedDate(new java.util.Date());

        if (props != null && props.size() > 0) toBeCreated.setProperties(props);
        toBeCreated.setStatus(cvDao.getNewStatus());

        return toBeCreated;
    }

    @Test
    public void testSimpleQuery() {
        assert v3ProjectDao != null;
        List<Project> projects = v3ProjectDao.getProjects(0, 1000, null);
        assertTrue("Projects list is null", projects != null);
        assertTrue(projects.size() >= daoTestSetup.getCreatedProjects().size());
       
    }

    @Test
    public void testWithContactIdQuery() {
        List<Project> createdProjects = daoTestSetup.getCreatedProjects();

        //get the first element and find out who it's contact is and use that for this test
        String target = createdProjects.get(0).getContact().getUsername();
        Integer targetCount = 0;
        for (int i = 0; i < createdProjects.size(); i++) {
            if (createdProjects.get(i).getContact().getUsername() == target) targetCount++;
        }
        
        //call with target contact
        List<Project> projectsByTarget =  v3ProjectDao.getProjects(0, 1000, target);
        assertTrue( projectsByTarget.size() >=  targetCount); //since there maybe existing projects in db
    }

    @Test
    public void testCreateProject() throws Exception {

        Project toBeCreated = createProject("test-project", "", null);

        Project project = v3ProjectDao.createProject(toBeCreated);

        assertTrue(project.getProjectId() != null && project.getProjectId() > 0); //assigned id

    }

    @Test
    @Transactional
    public void testCreateProjectWithProperties() throws Exception {
        //Properties
        Map<String, String> props = new HashMap<>();
        props.put("test-prop", "test-value");

        Project toBeCreated = createProject("test-project-2", "test-desc", props);

        Project project = v3ProjectDao.createProject(toBeCreated);
        assertTrue(project.getProjectId() != null && project.getProjectId() > 0);
        assertTrue("Without properties", project.getProperties().size() == 1);

    }


    @Test
    public void testGetProjectProperties() {
        List<Cv> cvs = v3ProjectDao.getCvProperties(0, 1000);
        assert cvs.size() > 0;
    }

    @Test
    public void testPatchProject() throws Exception {
        Project project = daoTestSetup.getCreatedProjects().get(0);
       
        
        //patch contact test
        //create patch
        Contact testContact =  daoTestSetup.getCreatedContacts().get(0);

        project.setContact(testContact);
        project.setProjectDescription("test");
        
        project = v3ProjectDao.patchProject(project);
        log.debug("Project description: " + project.getProjectDescription());

        assertEquals("description not patched", project.getProjectDescription(), "test");
        assertEquals("contact incorrect", project.getPiContactId(), testContact.getContactId());

        //edit-test
        //project.getProperties() is null 
        Map<String, String> newProps = new HashMap<>();
        newProps.put("4", "new-test-value");
        project.setProperties(newProps);
        project = v3ProjectDao.patchProject(project); 
        assert project.getProperties().size() > 0;
        log.info(project.getProperties().get("4"));
        assert project.getProperties().get("4").equals("new-test-value");

        //delete props test
        project.getProperties().remove("4");
        project = v3ProjectDao.patchProject(project); 
        assert project.getProperties().size() == 0; //property shoud be deleted

    }

}



