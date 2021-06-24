package org.gobiiproject.gobiidtomapping.entity.noaudit.impl;

import java.util.List;
import org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds.DtoMapNameIdParams;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

/**
 * Created by Phil on 10/16/2016.
 */
public interface DtoMapNameIdFetch {

    GobiiEntityNameType getEntityTypeName() throws GobiiException;
    List<NameIdDTO> getNameIds(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException;
}
