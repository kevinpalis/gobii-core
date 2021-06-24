/**
 * ContactDaoImpl.java
 * 
 * Default ContactDao implementation
 * @author RNDuldulao, Jr.
 * @since 2020-03-23
 */

package org.gobiiproject.gobiisampletrackingdao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.VendorProtocol;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VendorProtocolDaoImpl implements VendorProtocolDao {

    @PersistenceContext
    protected EntityManager em;


    @Override
    public List<VendorProtocol> getVendorProtocols(Integer page, Integer pageSize) {
        List<VendorProtocol> vendorProtocols = new ArrayList<>();

        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<VendorProtocol> criteriaQuery =
                    criteriaBuilder.createQuery(VendorProtocol.class);

            Root<VendorProtocol> vendorProtocolRoot =
                    criteriaQuery.from(VendorProtocol.class);
            vendorProtocolRoot.fetch("vendor", JoinType.LEFT);
            vendorProtocolRoot.fetch("protocol", JoinType.LEFT);
            criteriaQuery.select(vendorProtocolRoot);
           
            criteriaQuery.orderBy(
                    criteriaBuilder.asc(
                            vendorProtocolRoot.get("vendorProtocolId")));

            vendorProtocols = em
                    .createQuery(criteriaQuery)
                    .setFirstResult(page * pageSize).setMaxResults(pageSize)
                    .getResultList();

            return vendorProtocols;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                    e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    public VendorProtocol getVendorProtocol(Integer protocolId, Integer vendorId) {
        try {

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

            CriteriaQuery<VendorProtocol> criteriaQuery =
                criteriaBuilder.createQuery(VendorProtocol.class);

            Root<VendorProtocol> vendorProtocolRoot =
                criteriaQuery.from(VendorProtocol.class);

            criteriaQuery.select(vendorProtocolRoot);
            criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(vendorProtocolRoot.get("protocol").get("protocolId"),
                    protocolId),
                criteriaBuilder.equal(vendorProtocolRoot.get("vendor").get("organizationId"),
                    vendorId)));

            VendorProtocol vendorProtocol = em
                .createQuery(criteriaQuery)
                .getSingleResult();

            return vendorProtocol;

        }
        catch (NoResultException nrE) {
            return null;
        }
        catch(NonUniqueResultException nuniqE) {
            throw nuniqE;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    public VendorProtocol createVendorProtocol(VendorProtocol vendorProtocol) {
        try {
            em.persist(vendorProtocol);
            //Check unique constraint before flushing
            try {
                this.checkUniqueConstraint(vendorProtocol);
            }
            catch (GobiiException gE) {
                throw gE;
            }
            em.flush();
            return vendorProtocol;
        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR, GobiiValidationStatusType.BAD_REQUEST,
                "Missing required values or Bad Request");
        }
    }

    private void checkUniqueConstraint(VendorProtocol vendorProtocolToBeChecked) {

        try {
            this.getVendorProtocol(vendorProtocolToBeChecked.getProtocol().getProtocolId(),
                vendorProtocolToBeChecked.getVendor().getOrganizationId());
        }
        catch (NonUniqueResultException nuniqE) {
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.ENTITY_ALREADY_EXISTS,
                "Entity already exists");
        }

    }
     
 }