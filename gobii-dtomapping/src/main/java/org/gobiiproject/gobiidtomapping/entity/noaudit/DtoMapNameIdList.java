package org.gobiiproject.gobiidtomapping.entity.noaudit;

import java.util.List;
import org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds.DtoMapNameIdParams;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;

/**
 * Created by Phil on 4/6/2016.
 */
public interface DtoMapNameIdList {
    List<NameIdDTO> getNameIdList(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException;
}
