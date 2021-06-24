package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.LinkageGroup;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.IntegerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LinkageGroupDaoImpl implements LinkageGroupDao {

    Logger LOGGER = LoggerFactory.getLogger(LinkageGroupDaoImpl.class);

    @PersistenceContext
    protected EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<LinkageGroup> getLinkageGroups(
        Integer pageSize,
        Integer rowOffset,
        Integer linkageGroupId,
        Integer mapsetId
    ) {


        List<LinkageGroup> linkageGroups = new ArrayList<>();

        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize);
            Objects.requireNonNull(rowOffset);

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<LinkageGroup> criteriaQuery =
                cb.createQuery(LinkageGroup.class);

            Root<LinkageGroup> linkageGroup =
                criteriaQuery.from(LinkageGroup.class);
            criteriaQuery.select(linkageGroup);

            Join<Object, Object> mapset =
                (Join<Object, Object>) linkageGroup.fetch("mapset");

            if(linkageGroupId != null) {
                predicates.add(
                    cb.equal(
                        linkageGroup.get("linkageGroupId"),
                        linkageGroupId));
            }

            if(mapsetId != null) {
                predicates.add(cb.equal(mapset.get("mapsetId"), mapsetId));
            }

            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            linkageGroups = em.createQuery(criteriaQuery)
                    .setFirstResult(rowOffset)
                    .setMaxResults(pageSize)
                    .getResultList();

            return linkageGroups;

        }
        catch(Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: "
                        + e.getCause().getMessage());
        }

    }

    @Override
    public List<LinkageGroup> getLinkageGroupsByNames(Set<String> linkageGroupNames,
                                               Integer mapsetId,
                                               Integer pageSize,
                                               Integer rowOffset) {

        List<LinkageGroup> linkageGroups;

        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize, "pgaeSize: Required non null");
            Objects.requireNonNull(rowOffset, "rowOffset: Required non null");
            Objects.requireNonNull(linkageGroupNames, "linkageGroupNames: Required non null");

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<LinkageGroup> criteriaQuery =
                cb.createQuery(LinkageGroup.class);
            Root<LinkageGroup> linkageGroup =
                criteriaQuery.from(LinkageGroup.class);
            criteriaQuery.select(linkageGroup);

            Join<Object, Object> mapset = linkageGroup.join("mapset");

            predicates.add(linkageGroup.get("linkageGroupName").in(linkageGroupNames));

            if(!IntegerUtils.isNullOrZero(mapsetId)) {
                predicates.add(cb.equal(mapset.get("mapsetId"), mapsetId));
            }

            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            linkageGroups = em.createQuery(criteriaQuery)
                .setFirstResult(rowOffset)
                .setMaxResults(pageSize)
                .getResultList();

            return linkageGroups;

        }
        catch(Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: "
                    + e.getCause().getMessage());
        }
    }

}
