package org.gobiiproject.gobiisampletrackingdao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.MarkerGroup;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Session;

@Slf4j
public class MarkerGroupDaoImpl implements MarkerGroupDao {

    @PersistenceContext
    private EntityManager em;


    @Override
    public MarkerGroup createMarkerGroup(MarkerGroup markerGroup) {
        em.persist(markerGroup);
        em.flush();
        return markerGroup;
    }


	@Override
	public List<MarkerGroup> getMarkerGroups(Integer offset, Integer pageSize) {
        List<MarkerGroup> markerGroups = new ArrayList<>();
  
        try {

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
  
            Root<MarkerGroup> root = criteriaQuery.from(MarkerGroup.class);
            criteriaQuery
                .multiselect(
                    root.get("markerGroupId"),
                    root.get("name"),
                    root.get("code"),
                    root.get("germplasmGroup"),
                    root.get("status"));
  
            List<Object[]> resultList = em
                .createQuery(criteriaQuery)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

            resultList.stream().forEach(result -> {
                MarkerGroup markerGroup = new MarkerGroup();
                markerGroup.setMarkerGroupId((Integer) result[0]);
                markerGroup.setName((String) result[1]);
                markerGroup.setCode((String) result[2]);
                markerGroup.setGermplasmGroup((String) result[3]);
                markerGroup.setStatus((Cv) result[4]);
                markerGroups.add(markerGroup);
            });

        } catch (Exception e) {
          log.error(e.getMessage(), e);
  
          throw new GobiiDaoException(
              GobiiStatusLevel.ERROR,
              GobiiValidationStatusType.UNKNOWN,
              e.getMessage() + " Cause Message: " + e.getCause().getMessage());
  
        }
        return markerGroups;  
        
		
	}


	@Override
	public MarkerGroup getMarkerGroup(Integer markerGroupId) {
		return em.find(MarkerGroup.class, markerGroupId);
	}


	@Override
	public MarkerGroup updateMarkerGroup(MarkerGroup markerGroup) {
        em.merge(markerGroup);
        em.flush();
        em.refresh(markerGroup);
		return markerGroup;
	}


	@Override
	public void deleteMarkerGroup(MarkerGroup markerGroup) throws Exception {
        em.remove(markerGroup);
        em.flush();
	}

	@Override
    public Long uploadMarkerGroupsFromFile(String filePath) throws GobiiDaoException {
        try {
            StoredProcedureQuery storedProcedureQuery = em
                .createStoredProcedureQuery("load_marker_groups")
                .registerStoredProcedureParameter(
                    "_file",
                    String.class,
                    ParameterMode.IN)
                .registerStoredProcedureParameter(
                    "markerGroupsCount",
                    Long.TYPE,
                    ParameterMode.OUT)
                .setParameter("_file", filePath);

            storedProcedureQuery.execute();

            return (Long) storedProcedureQuery.getOutputParameterValue("markerGroupsCount");
        }
        catch (PersistenceException e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());

        }
    }

    @Override
    public Long mapMarkerIdsForMarkerNamesAndPlatformIds(String filePath,
                                                         String outputFilePath
    ) throws GobiiDaoException {

        try {

            StoredProcedureQuery storedProcedureQuery = em
                .createStoredProcedureQuery(
                    "map_marker_ids_for_marker_groups_markername_platformid"
                )
                .registerStoredProcedureParameter(
                    "_in_file",
                    String.class,
                    ParameterMode.IN)
                .registerStoredProcedureParameter(
                    "_op_file",
                    String.class,
                    ParameterMode.IN)
                .registerStoredProcedureParameter(
                    "markerGroupsCount",
                    Long.TYPE,
                    ParameterMode.OUT)
                .setParameter("_in_file", filePath)
                .setParameter("_op_file", outputFilePath);

            storedProcedureQuery.execute();

            return (Long) storedProcedureQuery.getOutputParameterValue("markerGroupsCount");
        }
        catch (PersistenceException e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());

        }
    }

    @Override
    public Long mapMarkerIdsForMarkerNamesAndPlatformNames(String filePath,
                                                           String outputFilePath
    ) throws GobiiDaoException {

        try {

            StoredProcedureQuery storedProcedureQuery = em
                .createStoredProcedureQuery(
                    "map_marker_ids_for_marker_groups_markername_platformname"
                )
                .registerStoredProcedureParameter(
                    "_in_file",
                    String.class,
                    ParameterMode.IN)
                .registerStoredProcedureParameter(
                    "_op_file",
                    String.class,
                    ParameterMode.IN)
                .registerStoredProcedureParameter(
                    "markerGroupsCount",
                    Long.TYPE,
                    ParameterMode.OUT)
                .setParameter("_in_file", filePath)
                .setParameter("_op_file", outputFilePath);

            storedProcedureQuery.execute();

            return (Long) storedProcedureQuery.getOutputParameterValue("markerGroupsCount");
        }
        catch (PersistenceException e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());

        }
    }

}