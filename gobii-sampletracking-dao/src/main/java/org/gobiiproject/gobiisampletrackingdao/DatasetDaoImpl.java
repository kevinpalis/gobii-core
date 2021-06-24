package org.gobiiproject.gobiisampletrackingdao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Data access object Implementation for dataset Entity in the database
 *
 */
@Slf4j
public class DatasetDaoImpl implements DatasetDao {

    @PersistenceContext
    protected EntityManager em;
    Logger LOGGER = LoggerFactory.getLogger(Dataset.class);


    /**
     * Get datasets by given parameters.
     *
     * @param pageSize - size of the page
     * @param rowOffset - Row offset after which the pages need to be fetched
     * @param datasetId - Id for dataset. Unique identifier.
     * @return List of DatsetEntity
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Dataset> getDatasets(
        Integer pageSize,
        Integer rowOffset,
        Integer datasetId,
        String datasetName,
        Integer datasetTypeId,
        Integer experimentId,
        String experimentName
    ) throws GobiiException {

        List<Dataset> datasets;

        List<Predicate> predicates = new ArrayList<>();


        try {

            Objects.requireNonNull(pageSize, "pageSize : Required non null");
            Objects.requireNonNull(pageSize, "rowOffset : Required non null");

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Dataset> criteriaQuery = criteriaBuilder.createQuery(Dataset.class);

            Root<Dataset> datasetRoot = criteriaQuery.from(Dataset.class);
            criteriaQuery.select(datasetRoot);

            Join<Object, Object> experiment =
                (Join<Object, Object>) datasetRoot.fetch("experiment");

            if(datasetId != null) {
                predicates.add(
                    criteriaBuilder.equal(datasetRoot.get("datasetId"), datasetId));
            }

            if(datasetName != null) {
                predicates.add(
                    criteriaBuilder.equal(datasetRoot.get("datasetName"), datasetName));
            }

            if(datasetTypeId != null) {
                predicates.add(
                    criteriaBuilder.equal(datasetRoot.get("type").get("cvId"), datasetTypeId));
            }

            if(experimentId != null) {
                predicates.add(
                    criteriaBuilder.equal(experiment.get("experimentId"), experimentId));
            }

            if(experimentName != null) {
                predicates.add(
                    criteriaBuilder.equal(experiment.get("experimentName"), experimentName));
            }

            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            EntityGraph<?> graph = this.em.getEntityGraph("graph.dataset");
            datasets = em.createQuery(criteriaQuery)
                    .setHint("javax.persistence.fetchgraph", graph)
                    .setFirstResult(rowOffset)
                    .setMaxResults(pageSize)
                    .getResultList();

            return datasets;

        }
        catch(PersistenceException e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getDatasetWithAnalysesAndStats(
        Integer pageSize,
        Integer rowOffset,
        Integer datasetId,
        String datasetName,
        Integer datasetTypeId,
        Integer experimentId,
        String experimentName
    ) throws GobiiException {

        String queryString = 
            "WITH ds AS ("                                                         +
	        "   SELECT * "                                                         + 
            "   FROM dataset "                                                     +
	        "   WHERE (:datasetId IS NULL OR dataset_id = :datasetId) "            +
            "   AND (:datasetName IS NULL OR dataset.name = :datasetName) "        +
            "   AND (:datasetTypeId IS NULL or dataset.type_id = :datasetTypeId) " +
            "   ORDER BY dataset_id ASC "                                          +
            "   LIMIT :pageSize OFFSET :rowOffset "                                +
            ") "                                                                   + 
            "SELECT {ds.*} , {anas.*}, {experiment.*}, {callinganalysis.*}, "      +
            "       {typeCv.*}, {statusCv.*}, {stats.*}, {project.*}, "            +
            "       {contact.*}, {anaTypeCv.*}, {reference.*} "                    +
            "FROM ds "                                                             +
            "INNER JOIN experiment AS experiment ON( "                             +
            "    (ds.experiment_id = experiment.experiment_id) "                   +
            "    AND (:experimentId IS NULL OR "                                   +
            "        experiment.experiment_id = :experimentId) "                   +
            "    AND (:experimentName IS NULL OR "                                 +
            "        experiment.name = :experimentName) "                          +
            ") "                                                                   + 
            "LEFT JOIN analysis AS anas ON(anas.analysis_id = ANY(ds.analyses)) "  +
            "LEFT JOIN analysis AS callinganalysis "                               +
            "     ON(callinganalysis.analysis_id = ds.callinganalysis_id) "        +
            "LEFT JOIN cv typeCv ON(ds.type_id = typeCv.cv_id) "                   +
            "LEFT JOIN cv statusCv ON(ds.status = statusCv.cv_id) "                +
            "LEFT JOIN cv anaTypeCv ON(anas.type_id = anaTypeCv.cv_id) "           +
            "LEFT JOIN dataset_stats stats ON (ds.dataset_id = stats.dataset_id) " +
            "LEFT JOIN project ON (experiment.project_id = project.project_id) "   +
            "LEFT JOIN contact ON (project.pi_contact = contact.contact_id) "      +
            "LEFT JOIN reference ON (anas.reference_id = reference.reference_id) ";
            
            try {

                Objects.requireNonNull(pageSize, "pageSize: Required non null");
                Objects.requireNonNull(rowOffset, "rowOffset: Required non null");
    
                Session session = em.unwrap(Session.class);
    
                List<Object[]> resultTupleList = session.createNativeQuery(queryString)
                        .addEntity("ds", Dataset.class)
                        .addEntity("anas", Analysis.class)
                        .addJoin("experiment", "ds.experiment")
                        .addJoin("callinganalysis", "ds.callingAnalysis")
                        .addJoin("stats", "ds.datasetStats")
                        .addJoin("typeCv", "ds.type")
                        .addJoin("statusCv", "ds.status")
                        .addJoin("project", "experiment.project")
                        .addJoin("contact", "project.contact")
                        .addJoin("anaTypeCv", "anas.type")
                        .addJoin("reference", "anas.reference")
                        .setParameter("pageSize", pageSize, IntegerType.INSTANCE)
                        .setParameter("rowOffset", rowOffset, IntegerType.INSTANCE)
                        .setParameter("datasetId", datasetId, IntegerType.INSTANCE)
                        .setParameter("experimentId", experimentId, IntegerType.INSTANCE)
                        .setParameter("datasetTypeId", datasetTypeId, IntegerType.INSTANCE)
                        .setParameter("datasetName", datasetName, StringType.INSTANCE)
                        .setParameter("experimentName", experimentName, StringType.INSTANCE)
                        .list();
                
                return resultTupleList;
    
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    /**
     * Returns a list of object tuple with Dataset entity left joined with analysis entities
     * and their respective marker and dnarun count.
     *
     * @param pageSize - number of dataset entities to be fetched
     * @param rowOffset - row offset for database list
     * @param datasetId - filter by dataset id
     * @param datasetName - filter by dataset name
     * @param experimentId - filter by experiment id
     * @param experimentName - filter by experiment name
     * @return List<Object[]> list of object tuple with,
     * Object[0] - Dataset Entity
     * Object[1] - Analysis Entity related to database. Joined using analyses column
     * Object[2] - markers count for each database entity
     * Object[3] - dnaruns count for each database entity
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getDatasetsWithAnalysesAndCounts(
        Integer pageSize,
        Integer rowOffset,
        Integer datasetId,
        String datasetName,
        Integer experimentId,
        String experimentName) {


        String queryString = "WITH ds AS (" +
            "SELECT * " +
            "FROM dataset " +
            "WHERE (:datasetId IS NULL OR dataset_id = :datasetId) " +
            "AND (:datasetName IS NULL OR dataset.name = :datasetName) " +
            "LIMIT :pageSize OFFSET :rowOffset) " +
            "SELECT {ds.*}, " +
            "{anas.*}, " +
            "{ds_stats.*}, " +
            "{experiment.*}, " +
            "{callinganalysis.*}, " +
            "{job.*}, " +
            "{typeCv.*}, " +
            "{statusCv.*} " +
            "FROM ds " +
            "INNER JOIN dataset_stats as ds_stats USING(dataset_id) " +
            "INNER JOIN experiment AS experiment ON(" +
            "   (ds.experiment_id = experiment.experiment_id) " +
            "   AND (:experimentId IS NULL OR experiment.experiment_id = :experimentId) " +
            "   AND (:experimentName IS NULL OR experiment.name = :experimentName) " +
            ") " +
            "LEFT JOIN analysis AS anas ON(anas.analysis_id = ANY(ds.analyses)) " +
            "LEFT JOIN analysis AS callinganalysis ON(" +
                "callinganalysis.analysis_id = ds.callinganalysis_id" +
            ") " +
            "LEFT JOIN job USING(job_id) " +
            "LEFT JOIN cv typeCv ON(job.type_id = typeCv.cv_id) " +
            "LEFT JOIN cv statusCv ON(job.status = statusCv.cv_id) ";


        try {

            Objects.requireNonNull(pageSize, "pageSize: Required non null");
            Objects.requireNonNull(rowOffset, "rowOffset: Required non null");

            Session session = em.unwrap(Session.class);

            List<Object[]> resultTupleList = session.createNativeQuery(queryString)
                    .addEntity("ds", Dataset.class)
                    .addEntity("anas", Analysis.class)
                    .addJoin("ds_stats", "ds.datasetStats")
                    .addJoin("experiment", "ds.experiment")
                    .addJoin("job", "ds.job")
                    .addJoin("callinganalysis", "ds.callingAnalysis")
                    .addJoin("typeCv", "job.type")
                    .addJoin("statusCv", "job.status")
                    .setParameter("pageSize", pageSize, IntegerType.INSTANCE)
                    .setParameter("rowOffset", rowOffset, IntegerType.INSTANCE)
                    .setParameter("datasetId", datasetId, IntegerType.INSTANCE)
                    .setParameter("experimentId", experimentId, IntegerType.INSTANCE)
                    .setParameter("datasetName", datasetName, StringType.INSTANCE)
                    .setParameter("experimentName", experimentName, StringType.INSTANCE)
                    .list();

            return resultTupleList;

        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }

    }

    @Override
    public int getDatasetCountByAnalysisId(Integer id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Dataset> dataset = criteriaQuery.from(Dataset.class);
        Join<Object, Object> callingAnalysis = dataset.join("callingAnalysis");
        criteriaQuery.select(
            criteriaBuilder.count(
                dataset
            )
        ).where(
            criteriaBuilder.equal(
                callingAnalysis.get("analysisId"),
                id
            )
        );
        return em.createQuery(criteriaQuery).getSingleResult().intValue();
    }

	@Override
	public int getDatasetCountWithAnalysesContaining(Integer id) {
		Query query = em.createNativeQuery(
            "SELECT COUNT(*) FROM dataset WHERE ? = ANY(analyses)"
        );
        query.setParameter(1, id); 
        int count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	public Dataset saveDataset(Dataset dataset) throws Exception {
        em.persist(dataset);
        em.flush();
        em.refresh(dataset, getDatasetHints());
        return dataset;
    }
    

    private Map<String, Object> getDatasetHints() {
        EntityGraph<?> graph = this.em.getEntityGraph("graph.dataset");
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);
        return hints;
    }

	@Override
	public Dataset getDataset(Integer datasetId) {
		return em.find(Dataset.class, datasetId, getDatasetHints());
	}

	@Override
	public Dataset updateDataset(Dataset dataset) throws Exception {
        em.merge(dataset);
        em.flush();
        em.refresh(dataset, this.getDatasetHints());
		return dataset;
	}

	@Override
	public void deleteDataset(Dataset dataset) {
        em.remove(dataset);
        em.flush();
	}

}
