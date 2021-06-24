package org.gobiiproject.gobiisampletrackingdao;

import java.security.PublicKey;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.DnaSample;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.IntegerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data access object Implementation for dnaSample Entity in the database.
 * Only gets paged results for result set of size more than one.
 * Default page size is 1000. Page Size and row offset can be set as function parameters
 *
 */
public class DnaSampleDaoImpl implements DnaSampleDao {

    Logger LOGGER = LoggerFactory.getLogger(DnaSampleDao.class);

    @PersistenceContext
    protected EntityManager em;

    /**
     * Gets list of dnaSample entities that match the given filter parameters.
     * @param pageSize size of the page to be fetched.
     * @param rowOffset Row Limit Offset.
     * @param projectId to filter dnaSamples belonging to given project id
     *                   Foreign key with ManyToOne
     *                   relation(Many DnaSample to one germplasm).
     * @param dnaSampleId to filter dnaSamples by dnaSampleId. this filter
     *                    should fetch only one value as it is primary key.
     * @param germplasmId to filter dnaSamples by germplasmId.
     *                   Foreign key with ManyToOne relation to
     *                    Germplasm Entity(Many DnaSample to one germplasm).
     * @param germplasmExternalCode to filter dnasamples by
     *                              germplasm external code.
     *
     * @return List of dnaSample entity
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public List<DnaSample> getDnaSamples(
        Integer pageSize, Integer rowOffset,
        Integer projectId, Integer dnaSampleId,
        Integer germplasmId, String germplasmExternalCode
    ) throws GobiiException {

        List<DnaSample> dnaSamples;

        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull("pageSize: Required not null");
            Objects.requireNonNull("rowOffset: Required not null");

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

            CriteriaQuery<DnaSample> criteriaQuery = criteriaBuilder.createQuery(DnaSample.class);

            Root<DnaSample> dnaSampleRoot = criteriaQuery.from(DnaSample.class);
            criteriaQuery.select(dnaSampleRoot);

            Join<Object, Object> germaplsmJoin =
                (Join<Object, Object>) dnaSampleRoot.fetch("germplasm");

            Join<Object, Object> projectJoin =
                (Join<Object, Object>) dnaSampleRoot.fetch("project");

            if(dnaSampleId != null) {
                predicates
                    .add(criteriaBuilder.equal(dnaSampleRoot.get("dnaSampleId"), dnaSampleId));
            }

            if(projectId != null) {
                predicates.add(criteriaBuilder.equal(projectJoin.get("projectId"), projectId));
            }

            if(germplasmId != null) {
                predicates
                    .add(criteriaBuilder.equal(germaplsmJoin.get("germplasmId"), germplasmId));
            }


            if(germplasmExternalCode != null) {
                predicates
                    .add(criteriaBuilder.equal(germaplsmJoin.get("externalCode"),
                        germplasmExternalCode));
            }

            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            dnaSamples = em.createQuery(criteriaQuery)
                .setMaxResults(pageSize)
                .setFirstResult(rowOffset)
                .getResultList();

            return dnaSamples;

        }
        catch(Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() +
                        " Cause Message: " + e.getCause().getMessage());
        }
    }

    /**
     * Gets list of dnaSample entities by pageNum and pageSize.
     * @param rowOffset rowOffset for dnasample rows
     * @param pageSize size of the page to be fetched. required
     *
     * @return List of dnaSample entity
     */
    @Override
    public List<DnaSample> getDnaSamples(Integer pageSize, Integer rowOffset) {

        return getDnaSamples(pageSize, rowOffset,
                null, null,
                null, null);

    }

    /**
     * Gets dnaSample entity by dnaSampleId.
     *
     * @return dnaSample entity for given dnaSampleId or null
     */
    @Override
    public DnaSample getDnaSampleByDnaSampleId(Integer dnaSampleId)
        throws GobiiException {

        List<DnaSample> dnaSamples = getDnaSamples(2, 0,
                null, dnaSampleId,
                null, null);

        if(dnaSamples.size() > 1) {

            LOGGER.error("dnaSampleId has multiple entities " +
                "association violating unique key constraint");

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNIQUE_KEY_VIOLATION,
                    "dnaSampleId has multiple " +
                        "entities association violating unique key constraint");
        }
        else if(dnaSamples.size() == 1) {
            return dnaSamples.get(0);
        }
        else {
            return null;
        }

    }

    /**
     * Gets list of dnaSample entities that match the given filter criteria.
     * @param projectId project id for which samples need to be fetched
     * @param pageSize size of the page to be fetched
     * @param rowOffset - Offset of rows after which page should start.
     * @return List of dnaSample entity
     */
    @Override
    public List<DnaSample>
    getDnaSamplesByProjectId(
        Integer projectId, Integer pageSize, Integer rowOffset
    ) {
        return getDnaSamples(pageSize, rowOffset,
                projectId, null,
                null, null);
    }

    /**
     * Gets list of dnaSample entities that match the given filter criteria.
     * @param germplasmId germplasm id for which samples need to be fetched
     * @param pageSize size of the page to be fetched
     * @param rowOffset - Offset of rows after which page should start.
     * @return List of dnaSample entity
     */
    @Override
    public List<DnaSample> getDnaSamplesByGermplasmId(
        Integer germplasmId, Integer pageSize, Integer rowOffset
    ) {

        return getDnaSamples(pageSize, rowOffset,
                null, null,
                germplasmId,  null);

    }

    /**
     * Gets list of dnaSample entities that match the given filter criteria.
     * @param germplasmExternalCode  germplasm external code for which samples need to be fetched
     * @param pageSize size of the page to be fetched
     * @param rowOffset - Offset of rows after which page should start.
     * @return List of dnaSample entity
     */
    @Override
    public List<DnaSample>
    getDnaSamplesByGermplasmExternalCode(
        String germplasmExternalCode,
        Integer pageSize, Integer rowOffset
    ) {
        return getDnaSamples(pageSize, rowOffset,
                null, null,
                null, germplasmExternalCode);

    }

    @Override
    public List<DnaSample> getDnaSamples(
        Set<Integer> dnaSampleIds,
        Set<String> dnaSampleNames,
        Set<String> dnaSampleUuids,
        Set<Integer> germplasmIds,
        Set<String> germplasmNames,
        Set<String> germplasmExternalCodes,
        Set<Integer> projectIds,
        Integer pageSize,
        Integer rowOffset) throws GobiiDaoException {

        List<DnaSample> dnasamples;

        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize, "pageSize : Required non null");
            Objects.requireNonNull(rowOffset, "rowOffset : Required non null");

            CriteriaBuilder cb  = em.getCriteriaBuilder();

            CriteriaQuery<DnaSample> criteria = cb.createQuery(DnaSample.class);

            //Set Root entity and selected entities
            Root<DnaSample> root = criteria.from(DnaSample.class);

            Join<Object, Object> germplasmJoin = (Join<Object, Object>) root.fetch("germplasm");;

            Join<Object, Object> projectJoin = (Join<Object, Object>) root.fetch("project");;

            criteria.select(root);

            if(!CollectionUtils.isEmpty(dnaSampleNames)) {
                predicates.add(root.get("dnaSampleName").in(dnaSampleNames));
            }

            if(!CollectionUtils.isEmpty(dnaSampleIds)) {
                predicates.add(root.get("dnaSampleId").in(dnaSampleIds));
            }

            if(!CollectionUtils.isEmpty(dnaSampleUuids)) {
                predicates.add(root.get("dnaSampleUuid").in(dnaSampleUuids));
            }

            if(!CollectionUtils.isEmpty(germplasmIds)) {
                predicates.add(germplasmJoin.get("germplasmId").in(germplasmIds));
            }

            if(!CollectionUtils.isEmpty(germplasmNames)) {
                predicates.add(germplasmJoin.get("germplasmName").in(germplasmNames));
            }

            if(!CollectionUtils.isEmpty(germplasmExternalCodes)) {
                predicates.add(germplasmJoin.get("externalCode").in(germplasmExternalCodes));
            }

            if(!CollectionUtils.isEmpty(projectIds)) {
                predicates.add(projectJoin.get("projectId").in(projectIds));
            }

            criteria.where(predicates.toArray(new Predicate[]{}));
            criteria.orderBy(cb.asc(root.get("dnaSampleId")));

            Query query =  em.createQuery(criteria);
            query
                .setMaxResults(pageSize)
                .setFirstResult(rowOffset);

            dnasamples = query.getResultList();

            return dnasamples;

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());

        }

    }

    @Override
    public List<DnaSample> getDnaSamples(Set<String> dnaSampleNames,
                                         Integer projectId,
                                         Integer pageSize,
                                         Integer rowOffset) throws GobiiDaoException {
        Set<Integer> projectIds = null;
        if(!IntegerUtils.isNullOrZero(projectId)) {
            projectIds  = new HashSet<>();
            projectIds.add(projectId);
        }
        return this.getDnaSamples(
            null, // dnaSampleIds
            dnaSampleNames,
            null, // dnaSampleUuids
            null, // germplasmIds
            null, // germplasmNames
            null, // germplasmExternalCodes
            projectIds,
            pageSize,
            rowOffset);
    }

    @Override
    public List<DnaSample> queryByNameAndNum(List<DnaSample> queryParams,
                                             Integer projectId) throws GobiiDaoException {

        List<DnaSample> dnasamples;

        List<Predicate> predicates = new ArrayList<>();

        try {

            CriteriaBuilder cb  = em.getCriteriaBuilder();

            CriteriaQuery<DnaSample> criteria = cb.createQuery(DnaSample.class);

            //Set Root entity and selected entities
            Root<DnaSample> root = criteria.from(DnaSample.class);

            criteria.select(root);
            Join<Object, Object> projectJoin =  root.join("project");;

            for(DnaSample queryParam : queryParams) {
                if(StringUtils.isNotEmpty(queryParam.getDnaSampleNum())) {
                    predicates.add(cb.and(
                        cb.equal(root.get("dnaSampleName"), queryParam.getDnaSampleName()),
                        cb.equal(root.get("dnaSampleNum"), queryParam.getDnaSampleNum())
                    ));
                }
                else {
                    predicates.add(cb.equal(root.get("dnaSampleName"),
                        queryParam.getDnaSampleName()));
                }
            }

            if(!IntegerUtils.isNullOrZero(projectId)) {
                criteria.where(cb.and(
                    cb.or(predicates.toArray(new Predicate[]{})),
                    cb.equal(projectJoin.get("projectId"), projectId))
                );
            }
            else {
                criteria.where(cb.or(predicates.toArray(new Predicate[]{})));
            }
            criteria.orderBy(cb.asc(root.get("dnaSampleId")));

            Query query =  em.createQuery(criteria);

            dnasamples = query.getResultList();

            return dnasamples;

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());

        }

    }




}
