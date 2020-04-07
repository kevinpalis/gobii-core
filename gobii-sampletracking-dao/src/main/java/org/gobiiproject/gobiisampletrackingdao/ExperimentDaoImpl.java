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
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.entity.VendorProtocol;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional
public class ExperimentDaoImpl implements ExperimentDao {

    Logger LOGGER = LoggerFactory.getLogger(ExperimentDaoImpl.class);

    @PersistenceContext
    protected EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<Experiment> getExperiments(Integer pageSize, Integer rowOffset,
                                           Integer projectId) {

        List<Experiment> experiments;

        try {

            Objects.requireNonNull(pageSize,
                    "pageSize : Required non null");
            Objects.requireNonNull(pageSize,
                    "rowOffset : Required non null");

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

            CriteriaQuery<Experiment> criteriaQuery =
                    criteriaBuilder.createQuery(Experiment.class);

            Root<Experiment> experimentRoot =
                    criteriaQuery
                            .from(Experiment.class);

            Join<Object, Object> project =
                    (Join<Object, Object>) experimentRoot
                            .fetch("project");

            project.fetch("contact");

            Join<Object, Object>  vendorProtocol =
                    (Join<Object, Object>) experimentRoot
                            .fetch("vendorProtocol");
            vendorProtocol.fetch("protocol").fetch("platform");

            criteriaQuery.select(experimentRoot);

            if(projectId != null) {
                criteriaQuery.where(
                        criteriaBuilder.equal(
                                project.get("projectId"), projectId));
            }

            experiments = em
                    .createQuery(criteriaQuery)
                    .setFirstResult(rowOffset)
                    .setMaxResults(pageSize)
                    .getResultList();

            return experiments;

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    public Experiment getExperiment(Integer i) throws Exception {
        EntityGraph<?> graph = em.getEntityGraph("graph.experiment");
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);
        return em.find(Experiment.class, i, hints);
    }

    @Override
    public VendorProtocol getVendorProtocol(Integer vendorProtocolId) {
        return em.find(VendorProtocol.class, vendorProtocolId);
    }

    @Override
    public Experiment createExperiment(Experiment experiment) {
        em.persist(experiment);
        em.flush();
        return experiment;
    }

    @Override
    public Experiment updateExperiment(Experiment target) {
        Experiment experiment = em.merge(target);
        em.flush();
        return experiment;
    }
}
