package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class MarkerDaoImpl implements MarkerDao {

    Logger LOGGER = LoggerFactory.getLogger(MarkerDao.class);

    @PersistenceContext
    protected EntityManager em;

    final int defaultPageSize = 1000;

    @Override
    @Transactional
    public List<Marker> getMarkers(Integer pageSize, Integer rowOffset,
                                   Integer markerId, Integer datasetId) {

        List<Marker> markers = new ArrayList<>();

        List<Predicate> predicates = new ArrayList<>();

        try {

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

            CriteriaQuery<Marker> criteriaQuery = criteriaBuilder.createQuery(Marker.class);

            Root<Marker> markerRoot = criteriaQuery.from(Marker.class);
            markerRoot.fetch("platform", JoinType.LEFT);
            markerRoot.fetch("reference", JoinType.LEFT);

            if(markerId != null) {
                predicates.add(criteriaBuilder.equal(markerRoot.get("markerId"), markerId));
            }

            if(datasetId != null) {

                Expression<Boolean> datasetIdExists = criteriaBuilder.function(
                        "JSONB_EXISTS", Boolean.class,
                        markerRoot.get("datasetMarkerIdx"), criteriaBuilder.literal(datasetId.toString()));

                predicates.add(criteriaBuilder.isTrue(datasetIdExists));

            }

            criteriaQuery.select(markerRoot);
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            markers = em.createQuery(criteriaQuery)
                    .setFirstResult(rowOffset)
                    .setMaxResults(pageSize)
                    .getResultList();

            return markers;

        }
        catch(Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }



    }

    @Override
    @Transactional
    public Marker getMarkerById(Integer markerId) {
        try {

            List<Marker> markersById = this.getMarkers(null, null,
                    markerId, null);

            if (markersById.size() > 1) {

                LOGGER.error("More than one duplicate entries found.");

                throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.NONE,
                        "More than one marker entity exists for the same Id");

            } else if (markersById.size() == 0) {
                throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "Marker Entity for given id does not exist");
            }

            return markersById.get(0);

        }
        catch(GobiiException ge) {
            throw ge;
        }
        catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());

        }

    }

    @Override
    @Transactional
    public List<Marker> getMarkersByMarkerIdCursor(Integer markerIdCursor, Integer datasetId, Integer pageSize) {

        List<Marker> markers;

        String queryString = "SELECT {marker.*} " +
                " FROM marker AS marker " +
                " WHERE (marker.dataset_marker_idx->CAST(:datasetId AS TEXT) IS NOT NULL OR :datasetId IS NULL) " +
                " AND (:markerIdCursor IS NULL OR marker.marker_id > :markerIdCursor) " +
                " ORDER BY marker.marker_id " +
                " LIMIT :pageSize ";

        try {

            if(pageSize == null) {
                pageSize = defaultPageSize;
            }

            Session session = em.unwrap(Session.class);

            markers = session.createNativeQuery(queryString)
                    .addEntity("marker", Marker.class)
                    .setParameter("pageSize", pageSize, IntegerType.INSTANCE)
                    .setParameter("datasetId", datasetId, IntegerType.INSTANCE)
                    .setParameter("markerIdCursor", markerIdCursor, IntegerType.INSTANCE)
                    .list();

            return markers;

        }
        catch(Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    @Transactional
    public List<Marker> getMarkersByDatasetId(Integer datasetId,
                                   Integer pageSize,
                                   Integer rowOffset) {
        return getMarkers(pageSize, rowOffset, null, datasetId);
    }

    /**
     * Returns List of Marker Entities for given markerIds
     * @param markerIds - List of Marker Ids
     * @return Lis of Marker Entities
     */
    @Transactional
    @Override
    public List<Marker> getMarkersByMarkerIds(List<Integer> markerIds) {
        List<Marker> markers = new ArrayList<>();

        try {

            CriteriaBuilder cb  = em.getCriteriaBuilder();

            // Initialize criteria with Marker Entity as Result
            CriteriaQuery<Marker> criteria = cb.createQuery(Marker.class);

            //Set Root entity and selected entities
            Root<Marker> root = criteria.from(Marker.class);
            criteria.select(root);


            criteria.where(root.get("markerId").in(markerIds));

            criteria.orderBy(cb.asc(root.get("markerId")));

            markers = em.createQuery(criteria).getResultList();

            return markers;

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());

        }
    }

    /**
     * Retruns List of Marker Entities for given Marker Names
     * @param markerNames - Marker
     * @return
     */
    @Transactional
    @Override
    public List<Marker> getMarkersByMarkerNames(List<String> markerNames) {

        List<Marker> markers = new ArrayList<>();

        try {

            CriteriaBuilder cb  = em.getCriteriaBuilder();

            CriteriaQuery<Marker> criteria = cb.createQuery(Marker.class);

            Root<Marker> root = criteria.from(Marker.class);
            criteria.select(root);

            criteria.where(root.get("markerName").in(markerNames));

            criteria.orderBy(cb.asc(root.get("markerId")));

            markers = em.createQuery(criteria).getResultList();

            return markers;


        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());


        }
    }

}
