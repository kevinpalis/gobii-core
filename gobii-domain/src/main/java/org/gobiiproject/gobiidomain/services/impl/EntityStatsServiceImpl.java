package org.gobiiproject.gobiidomain.services.impl;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.EntityStatsService;
import org.gobiiproject.gobiidtomapping.entity.noaudit.DtoMapEntityStats;
import org.gobiiproject.gobiimodel.dto.system.EntityStatsDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Phil on 4/21/2016.
 */
public class EntityStatsServiceImpl implements EntityStatsService {

    Logger LOGGER = LoggerFactory.getLogger(EntityStatsServiceImpl.class);

    @Autowired
    DtoMapEntityStats dtoMapEntityStats;


    @Override
    public List<EntityStatsDTO> getAll() throws GobiiDomainException {

        List<EntityStatsDTO> returnVal;

        try {

            returnVal = dtoMapEntityStats.getAll();

            // When we have roles and permissions, this will be set programmatically

            returnVal.forEach(es ->
                es.getAllowedProcessTypes().add(GobiiProcessType.READ)
            );

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

        return returnVal;
    }

    @Override
    public EntityStatsDTO getEntityLastModified(GobiiEntityNameType gobiiEntityNameType) throws GobiiDomainException {

        EntityStatsDTO returnVal;

        try {

            returnVal = dtoMapEntityStats.getEntityLastModified(gobiiEntityNameType);

            // When we have roles and permissions, this will be set programmatically
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

        return returnVal;
    }

    @Override
    public EntityStatsDTO getEntityCount(GobiiEntityNameType gobiiEntityNameType) throws GobiiDomainException {

        EntityStatsDTO returnVal;

        try {

            returnVal = dtoMapEntityStats.getEntityCount(gobiiEntityNameType);

            // When we have roles and permissions, this will be set programmatically
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

        return returnVal;
    }

    @Override
    public EntityStatsDTO getEntityCountOfChildren(GobiiEntityNameType gobiiEntityNameTypeParent,
                                            Integer parentId,
                                            GobiiEntityNameType gobiiEntityNameTypeChild ) throws GobiiDomainException {
        EntityStatsDTO returnVal;

        try {

            returnVal = dtoMapEntityStats.getEntityCountOfChildren(gobiiEntityNameTypeParent, parentId, gobiiEntityNameTypeChild);

            // When we have roles and permissions, this will be set programmatically
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

        return returnVal;
    }


}
