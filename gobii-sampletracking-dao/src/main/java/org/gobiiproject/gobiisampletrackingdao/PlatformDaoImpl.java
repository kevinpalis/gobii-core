package org.gobiiproject.gobiisampletrackingdao;

import java.util.*;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.gobiiproject.gobiimodel.entity.Platform;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlatformDaoImpl implements PlatformDao {

    @PersistenceContext
    EntityManager em;

    @Override
    public Platform createPlatform(Platform platform) {
        em.persist(platform);
        em.flush();
        em.refresh(platform, this.getHints());
        return platform;
    }

    private Map<String, Object> getHints() {
        EntityGraph<?> graph = em.getEntityGraph("graph.platform");
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);
        return hints;
    }

    @Override
    public List<Platform> getPlatforms(Integer offset, Integer pageSize, Integer platformTypeId) {
        List<Platform> platforms = null;
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Platform> criteriaQuery = criteriaBuilder.createQuery(Platform.class);

            Root<Platform> platformRoot = criteriaQuery.from(Platform.class);
            criteriaQuery.select(platformRoot);

            platformRoot.fetch("type", JoinType.LEFT);

            criteriaQuery.orderBy(criteriaBuilder.asc(platformRoot.get("platformId")));
            if (platformTypeId != null) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(platformRoot.get("type").get("cvId"), platformTypeId));
                criteriaQuery.where(predicates.toArray(new Predicate[] {}));
            }

            platforms = em.createQuery(criteriaQuery).setFirstResult(offset).setMaxResults(pageSize).getResultList();
            return platforms;
        } catch (Exception e) {
            log.error("Error getting org list: %s", e.getMessage());
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    public List<Platform> getPlatforms(Set<String> platformNames,
                                       Integer pageSize,
                                       Integer rowOffset) {
        List<Platform> platforms = null;
        List<Predicate> predicates = new ArrayList<>();
        try {

            Objects.requireNonNull(pageSize, "pageSize: Required non null");
            Objects.requireNonNull(rowOffset, "rowOffset: Required non null");

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Platform> criteriaQuery = criteriaBuilder.createQuery(Platform.class);

            Root<Platform> platformRoot = criteriaQuery.from(Platform.class);
            criteriaQuery.select(platformRoot);

            if(CollectionUtils.isNotEmpty(platformNames)) {
                predicates.add(platformRoot.get("platformName").in(platformNames));
            }

            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            criteriaQuery.orderBy(criteriaBuilder.asc(platformRoot.get("platformId")));

            platforms = em
                .createQuery(criteriaQuery)
                .setFirstResult(rowOffset)
                .setMaxResults(pageSize)
                .getResultList();
            return platforms;
        } catch (Exception e) {
            log.error("Error getting org list: %s", e.getMessage());
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    public Platform getPlatform(Integer platformId) {
        return em.find(Platform.class, platformId, this.getHints());
    }

    @Override
    public Platform updatePlatform(Platform platform) {
        em.merge(platform);
        em.flush();
        em.refresh(platform, this.getHints());
        return platform;
    }

    @Override
    public void deletePlatform(Platform platform) {
        em.remove(platform);
        em.flush();

    }
    
}