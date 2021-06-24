package org.gobiiproject.gobiisampletrackingdao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.entity.VendorProtocol;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ExperimentDaoImpl implements ExperimentDao {

    @PersistenceContext
    protected EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<Experiment> getExperiments(Integer pageSize, Integer rowOffset,
                                           Integer projectId) {

        List<Experiment> experiments;

        try {

            Objects.requireNonNull(pageSize, "pageSize : Required non null");
            Objects.requireNonNull(pageSize, "rowOffset : Required non null");

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

            CriteriaQuery<Experiment> criteriaQuery = criteriaBuilder.createQuery(Experiment.class);

            Root<Experiment> experimentRoot = criteriaQuery.from(Experiment.class);
            criteriaQuery.select(experimentRoot);

            Join<Object, Object> project = (Join<Object, Object>) experimentRoot.fetch("project");

            project.fetch("contact", JoinType.LEFT).fetch("organization", JoinType.LEFT);

            Join<Object, Object>  vendorProtocol =
                    (Join<Object, Object>) experimentRoot
                            .fetch("vendorProtocol");
            vendorProtocol.fetch("protocol", JoinType.LEFT).fetch("platform", JoinType.LEFT);
            vendorProtocol.fetch("vendor", JoinType.LEFT);

            if(projectId != null) {
                criteriaQuery.where(criteriaBuilder.equal(project.get("projectId"), projectId));
            }

            experiments = em.createQuery(criteriaQuery)
                .setFirstResult(rowOffset)
                .setMaxResults(pageSize)
                .getResultList();

            return experiments;

        } catch (Exception e) {

            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    public Experiment getExperiment(Integer i) throws GobiiDaoException {
        try {
            return em.find(Experiment.class, i, getHints());
        }
        catch (IllegalArgumentException e) {
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    public VendorProtocol getVendorProtocol(Integer vendorProtocolId) {
        return em.find(VendorProtocol.class, vendorProtocolId);
    }

    @Override
    public Experiment createExperiment(Experiment experiment) throws Exception {
        em.persist(experiment);
        em.flush();
        em.refresh(experiment, getHints());
        return experiment;
    }

    @Override
    public Experiment updateExperiment(Experiment target) throws Exception {
        Experiment experiment = em.merge(target);
        em.flush();
        em.refresh(experiment, getHints());
        return experiment;
    }
    
    private Map<String, Object> getHints() {
        EntityGraph<?> graph = em.getEntityGraph("graph.experiment");
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);
        return hints;    
    }

    @Override
    public void deleteExperiment(Experiment experiment) {
        try {
                em.remove(experiment);
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
