package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.brapi.LinkageGroupDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class LinkakgeGroupServiceImpl implements LinkageGroupService {

    Logger LOGGER = LoggerFactory.getLogger(LinkakgeGroupServiceImpl.class);

    /**
     * Gets the list of LinkageGroups in the database by Page Number and Page Size
     * @param pageSize - Page Size to be fetched
     * @param pageNum - Page Number to be fetched
     * @return List of Brapi Specified Linkage Group DTO
     * @throws GobiiDomainException
     */
    @Override
    public List<LinkageGroupDTO> getLinkageGroups(Integer pageSize,
                                                  Integer pageNum) throws GobiiDomainException {

        List<LinkageGroupDTO> returnVal = new ArrayList<>();

        try {
            return returnVal;
        } catch (Exception e) {
            LOGGER.error("Gobii service error: Unknown system error. ", e);
            throw new GobiiDomainException(e);
        }

    }

    /**
     * Gets the list of LinkageGroups in the database by MapId, Page Number and Page Size
     * @param mapId - Linkage Group Id
     * @param pageSize - Page Size to be fetched
     * @param pageNum - Page Number to be fetched
     * @return List of Brapi Linkage Group DTO
     * @throws GobiiDomainException
     */
    @Override
    public PagedResult<LinkageGroupDTO> getLinkageGroupsByMapId(Integer mapId,
                                                                Integer pageSize,
                                                                Integer pageNum) throws GobiiDomainException {

        PagedResult<LinkageGroupDTO> returnVal = new PagedResult<>();

        try {

            return returnVal;

        } catch (Exception e) {

            LOGGER.error("Gobii service error: Unknown system error", e);
            throw new GobiiDomainException(e);

        }

    }

}
