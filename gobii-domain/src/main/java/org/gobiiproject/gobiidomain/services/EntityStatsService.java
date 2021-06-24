package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.system.EntityStatsDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

/**
 * Created by Phil on 4/21/2016.
 * Modified by Yanii on 1/27/2017
 */
public interface EntityStatsService {

    List<EntityStatsDTO> getAll() throws GobiiDomainException;
    EntityStatsDTO getEntityLastModified(GobiiEntityNameType gobiiEntityNameType) throws GobiiDomainException;
    EntityStatsDTO getEntityCount(GobiiEntityNameType gobiiEntityNameType) throws GobiiDomainException;
    EntityStatsDTO getEntityCountOfChildren(GobiiEntityNameType gobiiEntityNameTypeParent,
                                            Integer parentId,
                                            GobiiEntityNameType gobiiEntityNameTypeChild ) throws GobiiDomainException;
}
