package org.gobiiproject.gobiisampletrackingdao;

import org.apache.commons.collections.CollectionUtils;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Germplasm;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GermplasmDaoImpl implements GermplasmDao {

    Logger LOGGER = LoggerFactory.getLogger(GermplasmDaoImpl.class);

    @PersistenceContext
    protected EntityManager em;

    /**
     * Dao for Germplasm Table to fetch germplasm for given external codes.
     *
     * @param externalCodes
     * @return
     * @throws GobiiDaoException
     */
    @Override
    public List<Germplasm> getGermplamsByExternalCodes(
        Set<String> externalCodes,
        Integer pageSize,
        Integer rowOffset) throws GobiiDaoException {

        List<Germplasm> germplasms = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();

        try {

            Objects.requireNonNull(externalCodes, "externalCodes: Required non null");
            Objects.requireNonNull(pageSize, "pageSize: Required non null");
            Objects.requireNonNull(rowOffset, "rowOffset: Required non null");

            if(CollectionUtils.isNotEmpty(externalCodes)) {

                CriteriaBuilder cb  = em.getCriteriaBuilder();
                CriteriaQuery<Germplasm> criteria = cb.createQuery(Germplasm.class);
                Root<Germplasm> root = criteria.from(Germplasm.class);


                predicates.add(root.get("externalCode").in(externalCodes));
                criteria.select(root);
                criteria.where(predicates.toArray(new Predicate[]{}));
                criteria.orderBy(cb.asc(root.get("germplasmId")));

                germplasms =
                    em.createQuery(criteria)
                        .setMaxResults(pageSize)
                        .setFirstResult(rowOffset)
                        .getResultList();

            }
            return germplasms;
        }
        catch (IllegalStateException | PersistenceException | IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                e.getMessage() + " Cause Message: " + e.getCause().getMessage());
        }

    }

}
