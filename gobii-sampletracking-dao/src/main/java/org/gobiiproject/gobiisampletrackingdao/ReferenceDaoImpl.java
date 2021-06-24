package org.gobiiproject.gobiisampletrackingdao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.gobiiproject.gobiimodel.entity.Reference;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReferenceDaoImpl implements ReferenceDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Reference getReference(Integer id) {
        return em.find(Reference.class, id);
    }

    @Override
    public List<Reference> getReferences(Integer offset, Integer pageSize) {
        return getReferences(null, pageSize, offset);
    }

    @Override
    public List<Reference> getReferences(Set<String> referenceNames,
                                         Integer pageSize,
                                         Integer offset) throws GobiiDaoException {
        List<Reference> references;
        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize, "pageSize: Required non null");
            Objects.requireNonNull(pageSize, "offset: Required non null");


            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Reference> criteriaQuery = criteriaBuilder.createQuery(Reference.class);

            Root<Reference> referenceRoot = criteriaQuery.from(Reference.class);
            criteriaQuery.select(referenceRoot);

            if(CollectionUtils.isNotEmpty(referenceNames)) {
                predicates.add(referenceRoot.get("referenceName").in(referenceNames));
            }
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            criteriaQuery.orderBy(criteriaBuilder.asc(referenceRoot.get("referenceId")));

            references = em
                .createQuery(criteriaQuery)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

            return references;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }

    }

    @Override
    public Reference createReference(Reference reference) throws Exception {
        em.persist(reference);
        em.flush();
        em.refresh(reference);
        return reference;
    }

    @Override
    public Reference updateReference(Reference reference) throws Exception {
        em.merge(reference);
        em.flush();
        return reference;
    }

    @Override
    public void deleteReference(Reference reference) {
        em.remove(reference);
        em.flush();
    }

    

}