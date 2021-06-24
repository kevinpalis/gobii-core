package org.gobiiproject.gobiisampletrackingdao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.entity.Protocol;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtocolDaoImpl implements ProtocolDao {

    @PersistenceContext
    protected EntityManager em;

    @Override
    public Protocol getProtocolById(Integer protocolId) {
        Protocol protocol = em.find(Protocol.class, protocolId, this.getProtocolHints());
        if (protocol == null) {
            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                "Entity does not exists");
        }
        return protocol;
    }

    @Override
    public List<Protocol> getProtocols(Integer pageSize,
                                       Integer pageOffset,
                                       Integer platformId) {

        List<Protocol> protocols;
        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize, "pageSize : Required non null");
            Objects.requireNonNull(pageOffset, "pageOffset: Required non null");

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Protocol> criteriaQuery = criteriaBuilder.createQuery(Protocol.class);

            Root<Protocol> root = criteriaQuery.from(Protocol.class);

            root.fetch("platform");

            if(platformId != null) {
                predicates.add(criteriaBuilder.equal(root.get("platform").get("platformId"),
                    platformId));
            }

            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            protocols = em.createQuery(criteriaQuery)
                .setMaxResults(pageSize)
                .setFirstResult(pageOffset)
                .getResultList();

            return protocols;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }
    }

    @Override
    public Protocol createProtocol(Protocol protocolToBeCreated) throws GobiiDaoException {
        try {
            em.persist(protocolToBeCreated);

            //Check unique constraint before flushing
            try {
                this.checkUniqueConstraint(protocolToBeCreated);
            }
            catch (GobiiException gE) {
                throw gE;
            }
            em.flush();
            return protocolToBeCreated;
        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Missing required values or Bad Request");
        }
    }

    @Override
    public Protocol patchProtocol(Protocol protocolToBePatched) {
        try {
            em.merge(protocolToBePatched);
            em.flush();
            em.refresh(protocolToBePatched, this.getProtocolHints());
            //Check unique constraint before flushing
            try {
                this.checkUniqueConstraint(protocolToBePatched);
            }
            catch (GobiiException gE) {
                throw gE;
            }

            return protocolToBePatched;
        }
        catch (GobiiException gE) {
            throw gE;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Missing required values or Bad Request");
        }
    }

    @Override
    public void deleteProtocol(Protocol protocol) {
        try {
            em.remove(protocol);
            em.flush();
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Entity does not exist or a bad Request");
        }

    }

    private void checkUniqueConstraint(Protocol protocolToBeChecked) {

        try {
            //Check Unique key constraint(protocolName, platformId).
            //Database table does not have those constraints in place.
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Protocol> criteriaQuery = criteriaBuilder.createQuery(Protocol.class);
            Root<Protocol> root = criteriaQuery.from(Protocol.class);
            criteriaQuery.select(root);
            root.fetch("platform");
            criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(root.get("platform").get("platformId"),
                    protocolToBeChecked.getPlatform().getPlatformId()),
                criteriaBuilder.equal(root.get("name"), protocolToBeChecked.getName())));

            em.createQuery(criteriaQuery)
                .getSingleResult();

        }
        catch(NonUniqueResultException nuniqE) {
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.ENTITY_ALREADY_EXISTS,
                "Entity already exists");
        }
    }

    private Map<String, Object> getProtocolHints() {
        EntityGraph<?> graph = this.em.getEntityGraph("protocol.platform");
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);
        return hints;
    }

}
