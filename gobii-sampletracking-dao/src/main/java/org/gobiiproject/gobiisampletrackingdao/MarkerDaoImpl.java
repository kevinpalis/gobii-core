package org.gobiiproject.gobiisampletrackingdao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;

import com.vladmihalcea.hibernate.type.array.StringArrayType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.IntegerUtils;
import org.hibernate.Session;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("unchecked")
public class MarkerDaoImpl implements MarkerDao {

    Logger LOGGER = LoggerFactory.getLogger(MarkerDaoImpl.class);

    @PersistenceContext
    protected EntityManager em;

    @Override
    public List<Marker> getMarkers(Integer pageSize, Integer rowOffset,
                                   Integer markerId, Integer datasetId) throws GobiiException {

        List<Marker> markers;

        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize, "pageSize : Required non null");
            Objects.requireNonNull(pageSize, "rowOffset : Required non null");

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

            CriteriaQuery<Marker> criteriaQuery = criteriaBuilder.createQuery(Marker.class);

            Root<Marker> markerRoot = criteriaQuery.from(Marker.class);
            markerRoot.fetch("platform", JoinType.LEFT);
            markerRoot.fetch("reference", JoinType.LEFT);

            if (markerId != null) {
                predicates.add(criteriaBuilder.equal(markerRoot.get("markerId"), markerId));
            }

            if (datasetId != null) {

                Expression<Boolean> datasetIdExists = criteriaBuilder.function("JSONB_EXISTS", Boolean.class,
                        markerRoot.get("datasetMarkerIdx"), criteriaBuilder.literal(datasetId.toString()));

                predicates.add(criteriaBuilder.isTrue(datasetIdExists));

            }

            criteriaQuery.select(markerRoot);
            criteriaQuery.where(predicates.toArray(new Predicate[] {}));

            markers = em.createQuery(criteriaQuery).setFirstResult(rowOffset).setMaxResults(pageSize).getResultList();

            return markers;

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }

    }

    @Override
    public Marker getMarkerById(Integer markerId) {

        try {

            Objects.requireNonNull(markerId, "markerId : Required non null");

            // To overload the getMarkers. There should be only one marker for a marker id
            // So, to check any discrepancy with that from database side
            Integer pageSize = 2;
            Integer rowOffset = 0;

            List<Marker> markersById = this.getMarkers(pageSize, rowOffset, markerId, null);

            if (markersById.size() > 1) {

                LOGGER.error("More than one duplicate entries found.");

                throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.NONE,
                        "More than one marker entity exists for the same Id");

            } else if (markersById.size() == 0) {
                throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "Marker Entity for given id does not exist");
            }

            return markersById.get(0);

        } catch (GobiiException ge) {
            throw ge;
        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());

        }

    }

    @Override
    public List<Marker> getMarkersByMarkerIdCursor(Integer pageSize, Integer markerIdCursor, Integer markerId,
            Integer datasetId) {

        List<Marker> markers;
        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize, "pageSize : Required non null");

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Marker> criteriaQuery = criteriaBuilder.createQuery(Marker.class);

            Root<Marker> markerRoot = criteriaQuery.from(Marker.class);

            markerRoot.fetch("platform", JoinType.LEFT);
            markerRoot.fetch("reference", JoinType.LEFT);

            if (markerId != null) {
                predicates.add(criteriaBuilder.equal(markerRoot.get("markerId"), markerId));
            }

            if (markerIdCursor != null) {
                predicates.add(criteriaBuilder.gt(markerRoot.get("markerId"), markerIdCursor));
            }

            if (datasetId != null) {

                Expression<Boolean> datasetIdExists = criteriaBuilder.function("JSONB_EXISTS", Boolean.class,
                        markerRoot.get("datasetMarkerIdx"), criteriaBuilder.literal(datasetId.toString()));

                predicates.add(criteriaBuilder.isTrue(datasetIdExists));

            }

            criteriaQuery.select(markerRoot);
            criteriaQuery.orderBy(criteriaBuilder.asc(markerRoot.get("markerId")));
            criteriaQuery.where(predicates.toArray(new Predicate[] {}));

            markers = em.createQuery(criteriaQuery).setMaxResults(pageSize).getResultList();

            return markers;

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    public List<Marker> getMarkersByDatasetId(Integer datasetId, Integer pageSize,
                                              Integer rowOffset) {
        return getMarkers(pageSize, rowOffset, null, datasetId);
    }

    /**
     * To filter markers by mapset and its associated fields like linkage group
     * and marker positions.
     * @param pageSize - page size
     * @param rowOffset - row offset
     * @param mapsetId - ID of the genome map to filter markers
     * @param mapsetName - Name of the genome map to filter markers
     * @param linkageGroupId - ID of the linkage group
     * @param linkageGroupName - Name of the linkage group
     * @param minPosition - minimum position of markers to be fetched
     * @param maxPosition - maximum position of marker to be fetched
     * @param datasetId - ID of the dataset.
     * @return List of marker entity.
     */
    @Override
    public List<Marker> getMarkersByMap(Integer pageSize,
                                        Integer rowOffset,
                                        Integer mapsetId,
                                        String mapsetName,
                                        Integer linkageGroupId,
                                        String linkageGroupName,
                                        BigDecimal minPosition,
                                        BigDecimal maxPosition,
                                        Integer datasetId) {

        String queryString = "SELECT {marker.*} FROM marker AS marker " +
            "INNER JOIN marker_linkage_group AS mlg USING(marker_id) " +
            "INNER JOIN linkage_group AS lg USING(linkage_group_id) " +
            "INNER JOIN mapset AS mapset ON(mapset.mapset_id = lg.map_id) " +
            "WHERE (:mapsetId IS NULL OR mapset.mapset_id = :mapsetId) " +
            "AND (:mapsetName IS NULL OR mapset.name = :mapsetName) " +
            "AND (:linkageGroupId IS NULL OR lg.linkage_group_id = :linkageGroupId) " +
            "AND (:linkageGroupName IS NULL OR lg.name = :linkageGroupName) " +
            "AND (:minPosition IS NULL OR mlg.start >= :minPosition)" +
            "AND (:maxPosition IS NULL OR mlg.stop <= :maxPosition) " +
            "AND (:datasetId IS NULL " +
            "   OR JSONB_EXISTS(marker.dataset_marker_idx, CAST(:datasetId AS TEXT))) " +
            "LIMIT :pageSize OFFSET :rowOffset";

        try {

            Objects.requireNonNull(pageSize, "pageSize: Required non null");
            Objects.requireNonNull(rowOffset, "rowOffset: Required non null");
            if( !ObjectUtils.anyNotNull(mapsetId, mapsetName)) {
                throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "mapset fields are null");
            }

            Session session = em.unwrap(Session.class);


            List<Marker> markers = session.createNativeQuery(queryString)
                .addEntity("marker", Marker.class)
                .setParameter("pageSize", pageSize, IntegerType.INSTANCE)
                .setParameter("rowOffset", rowOffset, IntegerType.INSTANCE)
                .setParameter("datasetId", datasetId, IntegerType.INSTANCE)
                .setParameter("mapsetId", mapsetId, IntegerType.INSTANCE)
                .setParameter("mapsetName", mapsetName, StringType.INSTANCE)
                .setParameter("linkageGroupId", linkageGroupId, IntegerType.INSTANCE)
                .setParameter("linkageGroupName", linkageGroupName, StringType.INSTANCE)
                .setParameter("minPosition", minPosition, BigDecimalType.INSTANCE)
                .setParameter("maxPosition", maxPosition, BigDecimalType.INSTANCE)
                .list();

            return markers;

        }
        catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());

        }


    }

    /**
     * Returns List of Marker Entities for given markerIds
     * 
     * @param markerIds - List of Marker Ids
     * @return Lis of Marker Entities
     */
    @Override
    public List<Marker>
    getMarkers(Set<Integer> markerIds, Set<String> markerNames,
               Set<String> datasetIds, Integer pageSize,
               Integer markerIdCursor) throws GobiiException {

        List<Marker> markers;
        List<Predicate> predicates = new ArrayList<>();

        String[] datasetIdsArray = new String[] {};

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            // Initialize criteria with Marker Entity as Result
            CriteriaQuery<Marker> criteria = cb.createQuery(Marker.class);

            // Set Root entity and selected entities
            Root<Marker> root = criteria.from(Marker.class);
            criteria.select(root);
            root.fetch("platform");

            if(!CollectionUtils.isEmpty(markerIds)) {
                predicates.add(root.get("markerId").in(markerIds));
            }

            if(!CollectionUtils.isEmpty(markerNames)) {
                predicates.add(root.get("markerName").in(markerNames));
            }

            if(!CollectionUtils.isEmpty(datasetIds)) {

                datasetIdsArray = datasetIds.toArray(new String[0]);

                ParameterExpression datasetIdsExp = cb.parameter(
                        String[].class,
                        "datasetIds");
                Expression<Boolean> datasetIdExists =
                    cb.function(
                        "JSONB_EXISTS_ANY",
                        Boolean.class,
                        root.get("datasetMarkerIdx"),
                        datasetIdsExp);

                predicates.add(cb.isTrue(datasetIdExists));

            }

            if(!IntegerUtils.isNullOrZero(markerIdCursor)) {
                predicates.add(cb.gt(root.get("markerId"), markerIdCursor));
            }

            criteria.where(predicates.toArray(new Predicate[]{}));
            criteria.orderBy(cb.asc(root.get("markerId")));

            TypedQuery query = em.createQuery(criteria);

            if(!CollectionUtils.isEmpty(datasetIds)) {
                query
                    .unwrap(org.hibernate.query.Query.class)
                    .setParameter(
                        "datasetIds",
                        datasetIdsArray,
                        StringArrayType.INSTANCE);
            }
            if(!IntegerUtils.isNullOrZero(pageSize)) {
                query.setMaxResults(pageSize);
            }
            markers = query.getResultList();
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
     * Returns List of Marker Entities for given markerIds
     * @param markerIds - List of Marker Ids
     * @return Lis of Marker Entities
     */
    @Override
    public List<Marker> getMarkersByMarkerIds(Set<Integer> markerIds) {
        return this.getMarkers(markerIds, null, null, null, null);
    }

    /**
     * Retruns List of Marker Entities for given Marker Names
     * @param markerNames - Marker
     * @return
     */
    @Override
    public List<Marker> getMarkersByMarkerNames(Set<String> markerNames) {
        return this.getMarkers(null, markerNames, null, null, null);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<Marker> getMarkersByPlatformMarkerNameTuples(List<List<String>> markerTuples) {
        List<Marker> markers;
        List<Predicate> predicates = new ArrayList<>();
  
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Marker> criteriaQuery = criteriaBuilder.createQuery(Marker.class);
  
            Root<Marker> root = criteriaQuery.from(Marker.class);
            
            root.fetch("platform", JoinType.LEFT);

            for (List<String> markerTuple: markerTuples) {
                predicates.add(
                    criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("platform").get("platformName"), markerTuple.get(0)),
                        criteriaBuilder.equal(root.get("markerName"), markerTuple.get(1))
                    )
                );
            }

            Predicate combinedPredicates = criteriaBuilder.or(predicates.toArray(new Predicate[]{}));
            criteriaQuery.select(root);
            criteriaQuery.where(combinedPredicates);
  
            markers = em.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
          log.error(e.getMessage(), e);
  
          throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                  e.getMessage() + " Cause Message: " + e.getCause().getMessage());
  
        }
        return markers;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<Marker> getMarkersByPlatformIdMarkerNameTuples(List<List<String>> markerTuples) {
        List<Marker> markers;
        List<Predicate> predicates = new ArrayList<>();

        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Marker> criteriaQuery = criteriaBuilder.createQuery(Marker.class);

            Root<Marker> root = criteriaQuery.from(Marker.class);

            root.fetch("platform", JoinType.LEFT);

            for (List<String> markerTuple: markerTuples) {
                Integer platformId = Integer.parseInt(markerTuple.get(0));
                predicates.add(
                    criteriaBuilder.and(
                        criteriaBuilder
                            .equal(root.get("platform").get("platformId"), platformId),
                        criteriaBuilder.equal(root.get("markerName"), markerTuple.get(1))
                    )
                );
            }

            Predicate combinedPredicates = criteriaBuilder.or(predicates.toArray(new Predicate[]{}));
            criteriaQuery.select(root);
            criteriaQuery.where(combinedPredicates);

            markers = em.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());

        }
        return markers;
    }


    @Override
    public List<Marker> queryMarkersByNamesAndPlatformId(Set<String> markerNames,
                                                         Integer platformId,
                                                         Integer pageSize,
                                                         Integer rowOffset
    ) throws GobiiDaoException {
        List<Marker> markers;
        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize, "pageSize: Required non null");
            Objects.requireNonNull(rowOffset, "rowOffset: Required non null");


            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Marker> criteria = cb.createQuery(Marker.class);

            Root<Marker> root = criteria.from(Marker.class);
            criteria.select(root);

            if(!CollectionUtils.isEmpty(markerNames)) {
                predicates.add(root.get("markerName").in(markerNames));
            }

            if(!IntegerUtils.isNullOrZero(platformId)) {
                Join<Object, Object> platformJoin = root.join("platform");
                predicates.add(cb.equal(platformJoin.get("platformId"), platformId));
            }

            criteria.where(predicates.toArray(new Predicate[]{}));
            criteria.orderBy(cb.asc(root.get("markerId")));

            TypedQuery query = em.createQuery(criteria);


            markers = query
                .setMaxResults(pageSize)
                .setFirstResult(rowOffset)
                .getResultList();

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
