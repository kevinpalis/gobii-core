/**
 * ProjetDaoImpl.java
 * 
 * ProjectDao Default Implementation.  DAO for Project and Project Properties (CV)
 * @author Rodolfo N. Duldulao, Jr.
 */
package org.gobiiproject.gobiisampletrackingdao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Project;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectDaoImpl implements ProjectDao {

    @Autowired
    private CvDao cvDao;

    @PersistenceContext
    protected EntityManager em;

    final int defaultPageSize = 1000;

    @Override
    public List<Project> getProjects(Integer pageNum, Integer pageSize, String piContactUserName) {
        log.debug("DAO getting projects");
        List<Project> projects = new ArrayList<>();
        piContactUserName = (Optional.ofNullable(piContactUserName)).orElse("");

        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Project> criteriaQuery = criteriaBuilder.createQuery(Project.class);

            Root<Project> projectRoot = criteriaQuery.from(Project.class);
            projectRoot.fetch("contact");
            criteriaQuery.select(projectRoot);
            if (!piContactUserName.isBlank()) {
                criteriaQuery.where(
                    criteriaBuilder.equal(
                        projectRoot.get("contact").get("username"),
                        piContactUserName
                    )
                );
            }
            criteriaQuery.orderBy(criteriaBuilder.asc(projectRoot.get("projectId")));

            projects = em.createQuery(criteriaQuery).setFirstResult(pageNum * pageSize).setMaxResults(pageSize)
                    .getResultList();
            log.debug("Projects List " + projects.size());
            return projects;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }

    }

    @Override
    public Project createProject(Project projectToBeCreated) throws Exception {
        em.persist(projectToBeCreated);
        em.flush();
        return projectToBeCreated;
    }

    public Cv getCv(Integer id) throws Exception {
        return em.find(Cv.class, id);
    }

    @Override
    public Project patchProject(Project projectToBePatched) throws Exception {   
        Project project = em.merge(projectToBePatched);
        em.flush();
        em.refresh(project, this.getContactHints());
        return project;
    }


    @Override
    public List<Cv> getCvProperties(Integer page, Integer pageSize) {
        return cvDao.getCvs(null, CvGroupTerm.CVGROUP_PROJECT_PROP.getCvGroupName(), null, page, pageSize);
    }

    @Override
    public Project getProject(Integer projectId) {
        log.info("Getting project %d", projectId );
        try {
            Project project = em.find(Project.class, projectId, getContactHints());
            return project;
        }
        catch (IllegalArgumentException e) {
            throw new GobiiException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                "Unable tp fetch project");
        }
    }

    private Map<String, Object> getContactHints() {
        EntityGraph<?> graph = this.em.getEntityGraph("project.contact");
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);
        return hints;
    }

    @Override
    public void deleteProject(Project project) throws Exception {
        try {
            em.remove(project);
            em.flush();
        } catch (javax.persistence.PersistenceException pe) {
            throw new GobiiException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.FOREIGN_KEY_VIOLATION,
                "Associated resources found. Cannot complete the action unless they are deleted.");
        } catch (Exception e) {
            throw e;
        }
    }

}