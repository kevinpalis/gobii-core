package org.gobiiproject.gobiisampletrackingdao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.Mapset;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.IntegerType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapsetDaoImpl implements MapsetDao {


    String mapsByExperimentIdListQueryString = "SELECT DISTINCT {mapset.*}, " +
        "{typecv.*}, COUNT(DISTINCT linkage_group_id) AS linkage_group_count, " +
        " COUNT(marker_id) AS marker_count FROM experiment " +
        "INNER JOIN dataset USING(experiment_id) " +
        "INNER JOIN marker " +
        "ON(jsonb_exists(marker.dataset_marker_idx, " +
            "CAST(dataset.dataset_id AS TEXT))) " +
        "INNER JOIN marker_linkage_group USING(marker_id) " +
        "INNER JOIN linkage_group USING(linkage_group_id) " +
        "INNER JOIN mapset ON(mapset.mapset_id = linkage_group.map_id) " +
        "LEFT JOIN cv typecv ON(mapset.type_id = typecv.cv_id) " +
        "WHERE (:experimentId IS NULL " +
            "OR experiment.experiment_id = :experimentId) " +
        "AND (:mapsetId IS NULL OR mapset.mapset_Id = :mapsetId) " +
        "GROUP BY mapset.mapset_id, typecv.cv_id " +
        "LIMIT :pageSize OFFSET :rowOffset";

    String mapsListQueryString = "SELECT DISTINCT {mapset.*}, {typecv.*}, " +
        "COUNT(DISTINCT linkage_group_id) AS linkage_group_count, " +
        "COUNT(marker_id) AS marker_count " +
        "FROM mapset " +
        "LEFT OUTER JOIN cv typecv ON(mapset.type_id = typecv.cv_id) " +
        "LEFT OUTER JOIN linkage_group " +
        "ON(mapset.mapset_id = linkage_group.map_id) " +
        "LEFT OUTER JOIN marker_linkage_group USING(linkage_group_id) " +
        "WHERE (:mapsetId IS NULL OR mapset.mapset_Id = :mapsetId) " +
        "GROUP BY mapset.mapset_id, typecv.cv_id " +
        "LIMIT :pageSize OFFSET :rowOffset";

    @PersistenceContext
    protected EntityManager em;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<Mapset> getMapsetsWithCounts(
            Integer pageSize, Integer rowOffset, Integer mapsetId,
            Integer experimentId) throws GobiiException {

        List<Mapset> mapsetsWithCounts = new ArrayList<>();

        try {

            Session session = em.unwrap(Session.class);

            String queryString = mapsListQueryString;

            if(experimentId != null) {
                queryString = mapsByExperimentIdListQueryString;
            }

            NativeQuery query = session.createNativeQuery(queryString)
                    .addEntity("mapset", Mapset.class)
                    .addJoin("typecv", "mapset.type")
                    .addScalar("linkage_group_count", IntegerType.INSTANCE)
                    .addScalar("marker_count", IntegerType.INSTANCE)
                    .setParameter("mapsetId", mapsetId, IntegerType.INSTANCE)
                    .setParameter("pageSize", pageSize, IntegerType.INSTANCE)
                    .setParameter("rowOffset", rowOffset, IntegerType.INSTANCE);

            if(experimentId != null) {
                query.setParameter("experimentId", experimentId);
            }

            List<Object[]> resultTuples = query.list();

            for(Object[] tuple : resultTuples) {

                Mapset mapset = (Mapset) tuple[0];

                mapset.setLinkageGroupCount((Integer) tuple[1]);
                mapset.setMarkerCount((Integer) tuple[2]);

                mapsetsWithCounts.add(mapset);

            }

            return mapsetsWithCounts;

        }
        catch (Exception e) {

            log.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: "
                        + e.getCause().getMessage());

        }

    }


  @Override
   public Mapset getMapsetWithCountsById(Integer mapsetId) {
      try {

          List<Mapset> mapsetsById = this.getMapsetsWithCounts(null, null,
                  mapsetId, null);

          if (mapsetsById.size() > 1) {

              log.error("More than one duplicate entries found.");

              throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                      GobiiValidationStatusType.NONE,
                      "More than one Mapset entity exists for the same Id");

          } else if (mapsetsById.size() == 0) {
              throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                      GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                      "Mapset Entity for given id does not exist");
          }

          return mapsetsById.get(0);

      }
      catch(GobiiException ge) {
          throw ge;
      }
      catch (Exception e) {

          log.error(e.getMessage(), e);

          throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                  GobiiValidationStatusType.UNKNOWN,
                  e.getMessage() + " Cause Message: " + e.getCause().getMessage());

      }

  }

  @Override
  public List<Mapset> getMapsets(Integer pageSize, Integer offset, Integer mapsetTypeId) throws Exception {
      List<Mapset> mapsets = new ArrayList<>();
      List<Predicate> predicates = new ArrayList<>();

      try {
          CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
          CriteriaQuery<Mapset> criteriaQuery = criteriaBuilder.createQuery(Mapset.class);

          Root<Mapset> root = criteriaQuery.from(Mapset.class);
          
          root.fetch("reference", JoinType.LEFT);
          root.fetch("type", JoinType.LEFT);

          if (mapsetTypeId != null) {
              predicates.add(
                  criteriaBuilder.equal(root.get("type").get("cvId"), mapsetTypeId)
              );
          }
          criteriaQuery.select(root);
          criteriaQuery.where(predicates.toArray(new Predicate[] {}));

          mapsets = em.createQuery(criteriaQuery).setFirstResult(offset).setMaxResults(pageSize).getResultList();
      } catch (Exception e) {
        log.error(e.getMessage(), e);

        throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());

      }
      return mapsets;
  }

  @Override
  public Mapset createMapset(Mapset createdMapset) {
      em.persist(createdMapset);
      em.flush();
      em.refresh(createdMapset, getMapsetHints());
      return createdMapset;

  }

  private Map<String, Object> getMapsetHints() {
    EntityGraph<?> graph = this.em.getEntityGraph("graph.mapset");
    Map<String, Object> hints = new HashMap<>();
    hints.put("javax.persistence.fetchgraph", graph);
    return hints;
  }

  @Override
  public Mapset getMapset(Integer mapsetId) {
      return em.find(Mapset.class, mapsetId, this.getMapsetHints());
  }

  @Override
  public Mapset getMapsetByName(String name) {
    try {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<Mapset> cq = cb.createQuery(Mapset.class);
      Root<Mapset> from = cq.from(Mapset.class);
      cq.select(from);
      cq.where(cb.equal(from.get("mapsetName"), name));
    
      Mapset mapset = em.createQuery(cq).getSingleResult();
      return mapset;
    } catch (NoResultException nre) {
      return null;
    } catch (Exception e) {
        throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
        e.getMessage() + " Cause Message: " + e.getCause().getMessage());
    }


  }

  @Override
  public Mapset updateMapset(Mapset mapset) {
      Mapset updatedMapset = em.merge(mapset);
      em.flush();
      return updatedMapset;
  }

  @Override
  public void deleteMapset(Mapset mapset) throws Exception {
      em.remove(mapset);
      em.flush();
  }
}
