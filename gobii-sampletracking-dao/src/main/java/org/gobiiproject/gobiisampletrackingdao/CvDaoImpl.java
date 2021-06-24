/**
 * CvDaoImpl.java
 * 
 * @author original author
 * @author Rodolfo N. Duldulao, Jr.
 */
package org.gobiiproject.gobiisampletrackingdao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CvDaoImpl implements CvDao {


    @PersistenceContext
    protected EntityManager em;

    @Override
    public Cv getCvByCvId(Integer cvId) throws Exception {

        Objects.requireNonNull(cvId, "Cv Id should not be null");

        return em.find(Cv.class, cvId, this.getCvHints());

    }

    @Override
    public List<Cv> getCvListByCvGroup(
        String cvGroupName,
        GobiiCvGroupType cvType) throws GobiiException {


        try {
            Objects.requireNonNull(
                    cvGroupName,
                    "CV group name should not be null");
            return this.getCvs(null, cvGroupName, cvType, null, null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                e.getMessage());
        }
    }

    @Override
    public List<Cv> getCvs(String cvTerm, String cvGroupName, GobiiCvGroupType cvType) {
        return this.getCvs(cvTerm, cvGroupName, cvType, null, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Cv> getCvs(String cvTerm,
                           String cvGroupName,
                           GobiiCvGroupType cvType,
                           Integer page,
                           Integer pageSize)
            throws GobiiException {

        List<Predicate> predicates = new ArrayList<>();

        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Cv> criteriaQuery = criteriaBuilder.createQuery(Cv.class);

            Root<Cv> cv = criteriaQuery.from(Cv.class);
            criteriaQuery.select(cv);

            Join<Object, Object> cvGroup = (Join<Object, Object>) cv.fetch("cvGroup");

            if (cvTerm != null) {
                predicates.add(criteriaBuilder.equal(cv.get("term"), cvTerm));
            }

            if (cvGroupName != null) {
                predicates.add(criteriaBuilder.equal(cvGroup.get("cvGroupName"), cvGroupName));
            }

            if (cvType != null) {
                predicates.add(
                    criteriaBuilder.equal(cvGroup.get("cvGroupType"),
                    cvType.getGroupTypeId()));
            }

            criteriaQuery.where(predicates.toArray(new Predicate[] {}));
            criteriaQuery.orderBy(criteriaBuilder.asc(cv.get("cvId")));

            List<Cv> cvs;
            if (page == null) {

                cvs = em.createQuery(criteriaQuery).getResultList();
            } else {
                if (pageSize == null || pageSize <= 0)
                    pageSize = 1000;
                cvs = em.createQuery(criteriaQuery)
                    .setFirstResult(page * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
            }

            return cvs;
        } catch (Exception e) {

            log.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN, e.getMessage());

        }

    }

    @Override
    public CvGroup getCvGroupByNameAndType(String cvGroupName,
                                           Integer type) throws GobiiException {
        try {

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<CvGroup> criteriaQuery = criteriaBuilder.createQuery(CvGroup.class);
            Root<CvGroup> cvGroup = criteriaQuery.from(CvGroup.class);
            criteriaQuery.select(cvGroup);
            criteriaQuery.where(
                criteriaBuilder.equal(cvGroup.get("cvGroupName"), cvGroupName), 
                criteriaBuilder.equal(cvGroup.get("cvGroupType"), type)
            );
            
            TypedQuery<CvGroup> q = em.createQuery(criteriaQuery);
            CvGroup result = q.getSingleResult();
            return result;

        } catch (Exception e) {
            throw new GobiiException(e);
        }
    }

    @Override
    public Cv createCv(Cv cv) {
        em.persist(cv);
        em.flush();

        return cv;
    }

	@Override
	public Cv getNewStatus() {
        return this.getStatusCv("new");
	}

	@Override
	public Cv getModifiedStatus() {
        return this.getStatusCv("modified");
    }
    
    private Cv getStatusCv(String status) {
        List<Cv> cvList = this.getCvs(
            status,
            org.gobiiproject.gobiimodel.cvnames.CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM
        );
        Cv cv = null;
        if (!cvList.isEmpty()) {
            cv = cvList.get(0);
        }
        return cv;
    }

    @Override
    public CvGroup getCvGroupById(Integer id) {
        return em.find(CvGroup.class, id);
    }

    @Override
    public Cv updateCv(Cv cv) {
        em.merge(cv);
        em.flush();
        em.refresh(cv, this.getCvHints());
        return cv;
    }

    private Map<String, Object> getCvHints() {
        EntityGraph<?> graph = this.em.getEntityGraph("graph.cv");
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);
        return hints;
    }

    @Override
    public void deleteCv(Cv cv) throws Exception {
        em.remove(cv);
        em.flush();
    }

    @Override
    public List<CvGroup> getCvGroups(Integer pageSize,
                                     Integer offset,
                                     GobiiCvGroupType type) {
        try {

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<CvGroup> criteriaQuery = criteriaBuilder.createQuery(CvGroup.class);
            Root<CvGroup> cvGroup = criteriaQuery.from(CvGroup.class);
            criteriaQuery.select(cvGroup);

            if(!type.equals(GobiiCvGroupType.GROUP_TYPE_UNKNOWN)) {
                criteriaQuery
                    .where(criteriaBuilder.equal(cvGroup.get("cvGroupType"), type.getGroupTypeId()));
            }

            TypedQuery<CvGroup> q = em.createQuery(criteriaQuery);
            return q.setFirstResult(offset).setMaxResults(pageSize).getResultList();

        } catch (Exception e) {
            throw new GobiiException(e);
        }
    }
}
