package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.Organization;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrganizationDaoImpl implements OrganizationDao {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<Organization> getOrganizations(Integer offset, Integer pageSize) throws Exception {
        List<Organization> orgList = null;
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);

            Root<Organization> organizationRoot = criteriaQuery.from(Organization.class);
            criteriaQuery.select(organizationRoot);
            criteriaQuery.orderBy(criteriaBuilder.asc(organizationRoot.get("organizationId")));

            orgList = em.createQuery(criteriaQuery).setFirstResult(offset).setMaxResults(pageSize).getResultList();
            return orgList;
        } catch (Exception e) {
            log.error("Error getting org list: %s", e.getMessage());
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    public Organization getOrganization(Integer organizationId) throws Exception {
        return em.find(Organization.class, organizationId);
    }

    @Override
    public Organization createOrganization(Organization organization) throws Exception {
        em.persist(organization);
        em.flush();
        return organization;
    }

    @Override
    public Organization updateOrganization(Organization organization) throws Exception {
        em.merge(organization);
        em.flush();
        em.refresh(organization);
        return organization;
    }

    @Override
    public void deleteOrganization(Organization organization) throws Exception {
        try {
            em.remove(organization);
            em.flush();
        } catch (javax.persistence.PersistenceException pe) {
            throw new GobiiException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.FOREIGN_KEY_VIOLATION,
                    "Associated resources found. Cannot complete the action unless they are deleted.");
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public Organization getOrganizationByName(String name) {
        log.debug("Looking up org: " + name);
        if (name == null) return null;
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);

            Root<Organization> organizationRoot = criteriaQuery.from(Organization.class);
            criteriaQuery.select(organizationRoot);
            criteriaQuery.where(
                    criteriaBuilder.equal(criteriaBuilder.upper(organizationRoot.get("name")), name.toUpperCase()));
            criteriaQuery.orderBy(criteriaBuilder.asc(organizationRoot.get("organizationId")));

            Organization organization = em.createQuery(criteriaQuery).getSingleResult();
            return organization;
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            log.error("Error getting org: %s", e.getMessage());
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    
    
}