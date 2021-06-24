package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.LoaderTemplate;
import org.gobiiproject.gobiimodel.types.GobiiLoaderPayloadTypes;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class LoaderTemplateDaoImpl implements LoaderTemplateDao {

    Logger LOGGER = LoggerFactory.getLogger(LoaderTemplate.class);

    @PersistenceContext
    protected EntityManager em;

    /**
     * Dao implementation for creating loader template
     * @param loaderTemplate entity to create
     * @return Created loader template with Id
     */
    @Override
    public LoaderTemplate create(LoaderTemplate loaderTemplate) throws GobiiDaoException {
        try {
            em.persist(loaderTemplate);
            em.flush();
            em.refresh(loaderTemplate, DaoUtils.getHints(em, "graph.loader_template"));
            return loaderTemplate;
        }
        catch (PersistenceException pe) {
            throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Unable to create loader template. Constraint violation");
        }
    }

    /**
     * Get template by Id
     * @param templateId    template id to fetch.
     * @return {@link LoaderTemplate}
     * @throws GobiiDaoException when loader template not found for
     * given id or other invalid requests
     */
    @Override
    public LoaderTemplate getById(Integer templateId) throws GobiiDaoException {
        LoaderTemplate loaderTemplate;
        try {
            loaderTemplate = em.find(
                LoaderTemplate.class,
                templateId,
                DaoUtils.getHints(em, "graph.loader_template"));
        }
        catch (IllegalArgumentException e) {
           throw new GobiiDaoException(
               GobiiStatusLevel.ERROR,
               GobiiValidationStatusType.BAD_REQUEST,
               "Invalid Request or system error");
        }

        if(Objects.isNull(loaderTemplate)) {
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "Entity does not exist");
        }
        return loaderTemplate;
    }

    @Override
    public List<LoaderTemplate> list(Integer pageSize,
                                     Integer rowOffset,
                                     GobiiLoaderPayloadTypes payloadType

    )  throws GobiiDaoException {

        List<LoaderTemplate> loaderTemplates;

        try {
            Objects.requireNonNull(pageSize, "pageSize: Required non null");
            Objects.requireNonNull(rowOffset, "rowOffset: Required non null");

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<LoaderTemplate> criteriaQuery = cb.createQuery(LoaderTemplate.class);

            Root<LoaderTemplate> loaderTemplateRoot = criteriaQuery.from(LoaderTemplate.class);
            Join<Object, Object> payloadTypeCvJoin =
                (Join<Object, Object>) loaderTemplateRoot.fetch("templateType");
            loaderTemplateRoot.fetch("createdBy");
            loaderTemplateRoot.fetch("modifiedBy");

            criteriaQuery.select(loaderTemplateRoot);
            if(payloadType != null) {
                criteriaQuery.where(cb.equal(payloadTypeCvJoin.get("term"), payloadType.getTerm()));
            }

            loaderTemplates = em
                .createQuery(criteriaQuery)
                .setFirstResult(rowOffset)
                .setMaxResults(pageSize)
                .getResultList();

            return loaderTemplates;
        }
        catch (NullPointerException nE) {
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                nE.getMessage());
        }
    }



}
