package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.cvnames.JobType;
import org.gobiiproject.gobiimodel.entity.Job;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.hibernate.NullPrecedence;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JobDaoImpl implements JobDao {

    Logger LOGGER = LoggerFactory.getLogger(JobDaoImpl.class);

    @PersistenceContext
    protected EntityManager em;

    /**
     * Dao implementation to create a job entry
     * 
     * @param job entity to create
     * @return Created job entity with generated id
     */
    @Override
    public Job create(Job job) throws GobiiDaoException {
        try {
            em.persist(job);
            em.flush();
            em.refresh(job, DaoUtils.getHints(em, "graph.job"));
            return job;
        } catch (PersistenceException pe) {
            throw new GobiiDaoException("Unable to create job");
        }
    }

    @Override
    public Job update(Job jobToBeUpdated) throws GobiiDaoException {
        try {
            Job job = em.merge(jobToBeUpdated);
            em.flush();
            em.refresh(job, DaoUtils.getHints(em, "graph.job"));
            return job;
        } catch (PersistenceException pe) {
            throw new GobiiDaoException("Unable to update job");
        }
    }

    @Override
    public Job getByName(String jobName) throws GobiiDaoException {
        Job job;
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Job> criteriaQuery = criteriaBuilder.createQuery(Job.class);

            Root<Job> jobRoot = criteriaQuery.from(Job.class);
            criteriaQuery.select(jobRoot);
            jobRoot.fetch("status");
            criteriaQuery.where(criteriaBuilder.equal(jobRoot.get("jobName"), jobName));

            job = em.createQuery(criteriaQuery).getSingleResult();
        } catch (NonUniqueResultException nuQ) {
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNIQUE_KEY_VIOLATION,
                    "Multiple jobs for same name");
        } catch (NoResultException nRe) {
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "Job not found");
        } catch (IllegalArgumentException e) {
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.BAD_REQUEST,
                    "Invalid Request or system error");
        }

        if (Objects.isNull(job)) {
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.BAD_REQUEST,
                    "Entity does not exist");
        }
        return job;
    }

    @Override
    public Job getById(Integer jobId) throws GobiiDaoException {
        Job job;
        try {
            job = em.find(Job.class, jobId, DaoUtils.getHints(em, "graph.job"));
        } catch (IllegalArgumentException e) {
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.BAD_REQUEST,
                    "Invalid Request or system error");
        }

        if (Objects.isNull(job)) {
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR, 
                GobiiValidationStatusType.BAD_REQUEST,
                "Entity does not exist");
        }
        return job;
    }

    @Override
    public List<Job> getJobs(
        Integer page, Integer pageSize, 
        Integer contactId, String username,
        List<JobType> jobTypes) {
           
        List<Job> jobs = new ArrayList<>();

        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Job> criteriaQuery = criteriaBuilder.createQuery(Job.class);

            Root<Job> jobRoot = criteriaQuery.from(Job.class);
            jobRoot.fetch("status");
            jobRoot.fetch("type");
            jobRoot.fetch("payloadType");
            jobRoot.fetch("submittedBy");

            List<Predicate> predicates = new ArrayList<Predicate>();

            if (contactId != null && contactId > 0) {
                predicates.add(criteriaBuilder.equal(jobRoot.get("submittedBy"), contactId));
            }
            
            if (!LineUtils.isNullOrEmpty(username)) {
                predicates.add(
                    criteriaBuilder.equal(jobRoot.get("submittedBy").get("username"), username)
                );
            }
            
            if(jobTypes != null && jobTypes.size() > 0) {
                In<String> inClause = criteriaBuilder.in(jobRoot.get("type").get("term"));

                for(JobType jobType : jobTypes) {
                    inClause.value(jobType.getCvName());
                }
                predicates.add(inClause);
            }
            
            criteriaQuery
                .select(jobRoot)
                .where(predicates.toArray(new Predicate[] {}))
                .orderBy(criteriaBuilder.desc(jobRoot.get("submittedDate")));

            if (pageSize == null || pageSize <= 0)
                pageSize = 1000;
            jobs = em
                .createQuery(criteriaQuery)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

            return jobs;
        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN, e.getMessage());

        }
    }
}