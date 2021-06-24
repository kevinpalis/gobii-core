package org.gobiiproject.gobiisampletrackingdao;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

import javax.persistence.*;
import javax.persistence.criteria.*;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.DnaSample;
import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.entity.Germplasm;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.IntegerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

public class DnaRunDaoImpl implements DnaRunDao {

    Logger LOGGER = LoggerFactory.getLogger(DnaRunDaoImpl.class);

    @PersistenceContext
    protected EntityManager em;

    /**
     * Lists the DnaRun Entities which matches the given criterias.
     * @param pageSize - Number of rows to be fetched
     * @param rowOffset - Number of rows to be offseted before fetching tuples
     * @param dnaRunId - Dna Run Id which uniquely identifies dna run
     * @param datasetId - Dataset Id to which dnaruns belong to.
     * @param dnaSampleId - dnaSampleId filter
     *                    to fetch only dnaruns for given dnasample Id.
     * @param dnaSampleName - dnaSample Id filter
     *                      to fetch only dnaruns for given dnasample Name.
     * @return List of DnaRun entities
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<DnaRun> getDnaRuns(Integer pageSize,
                                   Integer rowOffset,
                                   @Nullable Integer dnaRunId,
                                   @Nullable String dnaRunName,
                                   @Nullable Integer datasetId,
                                   @Nullable Integer experimentId,
                                   @Nullable Integer dnaSampleId,
                                   @Nullable String dnaSampleName,
                                   @Nullable Integer germplasmId,
                                   @Nullable String germplasmName,
                                   boolean fetchAssociations) {


        List<DnaRun> dnaRuns;

        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize, "Page size should not be null");
            Objects.requireNonNull(rowOffset, "Row Offset should not be null");


            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

            CriteriaQuery<DnaRun> criteriaQuery =
                    criteriaBuilder.createQuery(DnaRun.class);

            Root<DnaRun> dnaRunRoot = criteriaQuery.from(DnaRun.class);
            criteriaQuery.select(dnaRunRoot);

            Join<Object, Object> dnaSampleJoin = null;
            Join<Object, Object> germplasmJoin = null;
            Join<Object, Object> experimentJoin = null;

            if(fetchAssociations) {
                dnaSampleJoin= (Join<Object, Object>) dnaRunRoot.fetch("dnaSample");
                germplasmJoin = (Join<Object, Object>) dnaSampleJoin.fetch("germplasm");
                experimentJoin = (Join<Object, Object>) dnaRunRoot.fetch("experiment");
                germplasmJoin.fetch("germplasmType", JoinType.LEFT);
            }
            else {
                if (dnaSampleId != null || dnaSampleName != null
                    || germplasmId != null || germplasmName != null) {

                    dnaSampleJoin = dnaRunRoot.join("dnaSample");

                    if (germplasmId != null || germplasmName != null) {
                        germplasmJoin = dnaSampleJoin.join("germplasm");
                    }
                }
                if(experimentId != null) {
                    experimentJoin = dnaRunRoot.join("experiment");
                }
            }

            if(dnaSampleId != null && dnaSampleJoin != null) {
                predicates.add(
                    criteriaBuilder.equal(dnaSampleJoin.get("dnaSampleId"), dnaSampleId));
            }

            if(dnaSampleName != null && dnaSampleJoin != null) {
                predicates.add(
                    criteriaBuilder.equal(dnaSampleJoin.get("dnaSampleName"), dnaSampleName));
            }


            if(germplasmId != null && germplasmJoin != null) {
                predicates.add(
                    criteriaBuilder.equal(germplasmJoin.get("germplasmId"), germplasmId));
            }

            if(germplasmName != null && germplasmJoin != null) {
                predicates.add(
                    criteriaBuilder.equal(germplasmJoin.get("germplasmName"), germplasmName));
            }

            if(experimentId != null && experimentJoin != null) {
                predicates.add(
                    criteriaBuilder.equal(experimentJoin.get("experimentId"), experimentId));
            }

            if(datasetId != null) {
                Expression<Boolean> datasetIdExists =
                    criteriaBuilder.function("JSONB_EXISTS", Boolean.class,
                        dnaRunRoot.get("datasetDnaRunIdx"),
                        criteriaBuilder.literal(datasetId.toString()));
                predicates.add(criteriaBuilder.isTrue(datasetIdExists));
            }

            if(dnaRunId != null) {
                predicates.add(criteriaBuilder.equal(dnaRunRoot.get("dnaRunId"), dnaRunId));
            }

            if(dnaRunName != null) {
                predicates.add(criteriaBuilder.equal(dnaRunRoot.get("dnaRunName"), dnaRunName));
            }

            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            dnaRuns = em.createQuery(criteriaQuery)
                .setFirstResult(rowOffset)
                .setMaxResults(pageSize)
                .getResultList();

            return dnaRuns;

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
    public List<DnaRun> getDnaRuns(Integer pageSize,
                                   Integer rowOffset,
                                   Integer dnaRunId,
                                   String dnaRunName,
                                   Integer datasetId,
                                   Integer experimentId,
                                   Integer dnaSampleId,
                                   String dnaSampleName,
                                   Integer germplasmId,
                                   String germplasmName) {

        return this.getDnaRuns(
            pageSize,
            rowOffset,
            dnaRunId,
            dnaRunName,
            datasetId,
            experimentId,
            dnaSampleId,
            dnaSampleName,
            germplasmId,
            germplasmName,
            true);

    }


    @Override
    public List<DnaRun> getDnaRunsByDnaRunIdCursor(
            Integer pageSize,
            @Nullable  Integer dnaRunIdCursor,
            @Nullable  Integer datasetId) {

        List<DnaRun> dnaRuns;
        List<Predicate> predicates = new ArrayList<>();

        Objects.requireNonNull(pageSize, "Page Size is required");

        try {

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

            CriteriaQuery<DnaRun> criteriaQuery = criteriaBuilder.createQuery(DnaRun.class);

            Root<DnaRun> dnaRunRoot = criteriaQuery.from(DnaRun.class);

            if(dnaRunIdCursor != null) {
                predicates.add(
                    criteriaBuilder.gt(dnaRunRoot.get("dnaRunId"),
                    dnaRunIdCursor));
            }

            if(datasetId != null) {
                Expression<Boolean> datasetIdExists = criteriaBuilder.function(
                    "JSONB_EXISTS",
                    Boolean.class,
                    dnaRunRoot.get("datasetDnaRunIdx"),
                    criteriaBuilder.literal(datasetId.toString()));

                predicates.add(criteriaBuilder.isTrue(datasetIdExists));
            }

            criteriaQuery.select(dnaRunRoot);
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            dnaRuns = em.createQuery(criteriaQuery)
                .setMaxResults(pageSize)
                .getResultList();

            return dnaRuns;

        }
        catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    /**
     * Get DnaRun Entity by dnaRun Id.
     * dnaRunId is unique id for dnaRunEntity.
     * @param dnaRunId - dna run id for which DnaRun Entity needs to be fetched.
     * @return - DnaRun Entity with the given id
     */
    @Override
    public DnaRun getDnaRunById(Integer dnaRunId,
                                boolean fetchAssociations) throws GobiiException {

        List<DnaRun> dnaRunsById = this.getDnaRuns(
            2,          // Page size
            0,          // Row Offset
            dnaRunId,
            null,       // Dnarun Name
            null,       // Dataset Id
            null,       // Experiment Id
            null,       // Dnasample Id
            null,       // Dnasample Name
            null,       // Germplasm Id
            null,       // Germplasm Name
            fetchAssociations);

        if (dnaRunsById.size() > 1) {

            LOGGER.error("More than one duplicate entries found.");

            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.NONE,
                    "More than one dnarun entity exists for the same Id");

        } else if (dnaRunsById.size() == 0) {
            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "Dna run Entity for given id does not exist");
        }

        return dnaRunsById.get(0);
    }

    /**
     * Get DnaRun Entity by dnaRun Id.
     * dnaRunId is unique id for dnaRunEntity.
     * @param dnaRunId - dna run id for which DnaRun Entity needs to be fetched.
     * @return - DnaRun Entity with the given id
     */
    @Override
    public DnaRun getDnaRunById(Integer dnaRunId) throws GobiiException {
        return this.getDnaRunById(dnaRunId, true);
    }

    /**
     * Lists DnaRun entities for given dataset Id
     * @param datasetId - ID of dataset to which dnaruns belong.
     * @param pageSize - Number of rows to be fetched
     * @param rowOffset - Number of rows to offset before fetching
     * @return - List of result DnaRun Entities.
     */
    @Override
    public List<DnaRun>
    getDnaRunsByDatasetId(Integer datasetId,
                          Integer pageSize,
                          Integer rowOffset) {

        return getDnaRuns(
            pageSize,  // Page size
            rowOffset, // Row Offset
            null,      // DnaRun Id
            null,      // Dnarun Name
            datasetId, // Dataset Id
            null,      // Experiment Id
            null,      // Dnasample Id
            null,      // Dnasample Name
            null,      // Germplasm Id
            null,      // Germplasm Name
            true );

    }

    /**
     * Lists DnaRun entities for given dataset Id
     * @param datasetId - ID of dataset to which dnaruns belong.
     * @param pageSize - Number of rows to be fetched
     * @param rowOffset - Number of rows to offset before fetching
     * @param fetchAssociations - Optional
     * @return - List of result DnaRun Entities.
     */
    @Override
    public List<DnaRun>
    getDnaRunsByDatasetId(Integer datasetId,
                          Integer pageSize,
                          Integer rowOffset,
                          boolean fetchAssociations) {

        return getDnaRuns(
            pageSize,
            rowOffset,
            null,
            null,
            datasetId,
            null,
            null,
            null,
            null,
            null,
            fetchAssociations);

    }

    @Override
    public List<DnaRun> getDnaRuns(Set<Integer> dnaRunIds,
                                   Set<String> dnaRunNames,
                                   Set<Integer> dnaSampleIds,
                                   Set<String> dnaSampleNames,
                                   Set<String> dnaSampleUuids,
                                   Set<String> germplasmExternalCodes,
                                   Set<Integer> germplasmIds,
                                   Set<String> germplasmNames,
                                   Set<String> datasetIds,
                                   Set<Integer> experimentIds,
                                   Integer pageSize,
                                   Integer dnaRunIdCursor,
                                   Integer rowOffset,
                                   boolean fetchAssociations) {

        List<DnaRun> dnaruns;

        List<Predicate> predicates = new ArrayList<>();

        String[] datasetIdsArray = new String[]{};

        try {

            Objects.requireNonNull(pageSize, "pageSize: Required non null");

            CriteriaBuilder cb  = em.getCriteriaBuilder();
            CriteriaQuery<DnaRun> criteria = cb.createQuery(DnaRun.class);

            //Set Root entity and selected entities
            Root<DnaRun> root = criteria.from(DnaRun.class);
            Join<Object, Object> dnaSampleJoin;
            Join<Object, Object> germplasmJoin;
            Join<Object, Object> experimentJoin;

            if(CollectionUtils.isNotEmpty(experimentIds)) {
                if(fetchAssociations) {
                    experimentJoin = (Join<Object, Object>) root.fetch("experiment");
                }
                else {
                    experimentJoin = root.join("experiment");
                }
                predicates.add(experimentJoin.get("experimentId").in(experimentIds));
            }

            if(!CollectionUtils.isEmpty(dnaSampleIds) ||
                !CollectionUtils.isEmpty(dnaSampleNames) ||
                !CollectionUtils.isEmpty(dnaSampleUuids) ||
                !CollectionUtils.isEmpty(germplasmExternalCodes) ||
                !CollectionUtils.isEmpty(germplasmIds) ||
                !CollectionUtils.isEmpty(germplasmNames)) {


                if(fetchAssociations) {
                     dnaSampleJoin = (Join<Object, Object>) root.fetch("dnaSample");
                }
                else {
                    dnaSampleJoin = root.join("dnaSample");
                }

                if(!CollectionUtils.isEmpty(dnaSampleIds)) {
                    predicates.add(dnaSampleJoin.get("dnaSampleId").in(dnaSampleIds));
                }

                if(!CollectionUtils.isEmpty(dnaSampleNames)) {
                    predicates.add(dnaSampleJoin.get("dnaSampleName").in(dnaSampleNames));
                }

                if(!CollectionUtils.isEmpty(dnaSampleUuids)) {
                    predicates.add(dnaSampleJoin.get("dnaSampleUuid").in(dnaSampleUuids));
                }

                if(!CollectionUtils.isEmpty(germplasmExternalCodes) ||
                    !CollectionUtils.isEmpty(germplasmIds) ||
                    !CollectionUtils.isEmpty(germplasmNames)) {

                    if(fetchAssociations) {
                        germplasmJoin = (Join<Object, Object>) dnaSampleJoin.fetch("germplasm");
                        germplasmJoin.fetch("germplasmType", JoinType.LEFT);
                    } else {
                        germplasmJoin = dnaSampleJoin.join("germplasm");
                    }
                    if(!CollectionUtils.isEmpty(germplasmExternalCodes)) {
                        predicates.add(
                            germplasmJoin.get("externalCode").in(germplasmExternalCodes));
                    }
                    if(!CollectionUtils.isEmpty(germplasmIds)) {
                        predicates.add(germplasmJoin.get("germplasmId").in(germplasmIds));
                    }
                    if(!CollectionUtils.isEmpty(germplasmNames)) {
                        predicates.add(germplasmJoin.get("germplasmName").in(germplasmNames));
                    }
                } else if(fetchAssociations) {
                    germplasmJoin = (Join<Object, Object>) dnaSampleJoin.fetch("germplasm");
                    germplasmJoin.fetch("germplasmType", JoinType.LEFT);
                }
            }
            else {
                if(fetchAssociations) {
                    dnaSampleJoin = (Join<Object, Object>) root.fetch("dnaSample");
                    dnaSampleJoin.fetch("germplasm").fetch("germplasmType", JoinType.LEFT);
                    root.fetch("experiment");
                }
            }

            criteria.select(root);

            if(!CollectionUtils.isEmpty(dnaRunIds)) {
                predicates.add(root.get("dnaRunId").in(dnaRunIds));
            }

            if(!CollectionUtils.isEmpty(dnaRunNames)) {
                predicates.add(root.get("dnaRunName").in(dnaRunNames));
            }


            if(!CollectionUtils.isEmpty(datasetIds)) {

                datasetIdsArray = datasetIds.toArray(new String[0]);

                ParameterExpression<String[]> datasetIdsExp =
                    cb.parameter(String[].class, "datasetIds");

                Expression<Boolean> datasetIdExists = cb.function(
                    "JSONB_EXISTS_ANY",
                    Boolean.class,
                    root.get("datasetDnaRunIdx"),
                    datasetIdsExp);

                predicates.add(cb.isTrue(datasetIdExists));

            }
            /*Fetch markers only if the dnaRunIds or markerNames
             * are not empty*/
            if(predicates.size() == 0) {
                String errorMsg = "All predicates are null. " +
                    "Either dnaRunIds or dnaRunNames are required";
                LOGGER.error(errorMsg);
                throw new GobiiDaoException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    errorMsg);
            }

            if(!IntegerUtils.isNullOrZero(dnaRunIdCursor)) {
                predicates.add(cb.gt(root.get("dnaRunId"), dnaRunIdCursor));
            }

            criteria.where(predicates.toArray(new Predicate[]{}));

            criteria.orderBy(cb.asc(root.get("dnaRunId")));

            Query query =  em.createQuery(criteria);

            if(!CollectionUtils.isEmpty(datasetIds)) {
                query.unwrap(org.hibernate.query.Query.class)
                    .setParameter("datasetIds", datasetIdsArray, StringArrayType.INSTANCE);
            }

            query.setMaxResults(pageSize);

            if(!IntegerUtils.isNullOrZero(rowOffset)) {
                query.setFirstResult(rowOffset);
            }

            dnaruns = query.getResultList();

            return dnaruns;

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }

    }

    /**
     * Lists DnaRun Entitis with given DnaRun Id
     * @param dnaRunIds - List of IDs of dnaRun.
     * @return - List of DnaRun Entity result
     */
    @Override
    public List<DnaRun> getDnaRunsByDanRunIds(Set<Integer> dnaRunIds) {

            return this.getDnaRuns(
                dnaRunIds,          // Dnarun Ids
                null,               // Dnarun names
                null,               // Dna Sample Ids
                null,               // Dna Samples names
                null,               // Dna Sample Uuids
                null,               // Germplasm External Codes
                null,               // Germplasm Ids
                null,               // Germplasm Names
                null,               // Dataset Ids
                null,               // Experiment Ids
                dnaRunIds.size(),   // Page Size
                null,               // Page Id Cursor
                null,               // Row Offset
                false);             // To Fetch association or not
    }

    @Override
    public List<DnaRun> getDnaRunsByDnaRunNames(Set<String> dnaRunNames,
                                                Integer experimentId,
                                                Integer pageSize,
                                                Integer rowOffset) throws GobiiDaoException {
        Set<Integer> experimentIds = null;
        if(!IntegerUtils.isNullOrZero(experimentId)) {
            experimentIds = new HashSet<>();
            experimentIds.add(experimentId);
        }
        return this.getDnaRuns(
            null,          // Dnarun Ids
            dnaRunNames,   // Dnarun names
            null,          // Dna Sample Ids
            null,          // Dna Samples names
            null,          // Dna Sample Uuids
            null,          // Germplasm External Codes
            null,          // Germplasm Ids
            null,          // Germplasm Names
            null,          // Dataset Ids
            experimentIds, // Experiment Ids
            pageSize,      // Page Size
            null,          // Page Id Cursor
            rowOffset,     // Row Offset
            false);        // To Fetch association or not
    }

}
