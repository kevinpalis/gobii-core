
package org.gobiiproject.gobiidtomapping.entity.auditable;

import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.auditable.ReferenceDTO;

/**
 * Created by Angel on 5/4/2016.
 * Modified by Yanii on 1/26/2017
 */
public interface DtoMapReference extends DtoMap<ReferenceDTO> {

    ReferenceDTO create(ReferenceDTO referenceDTO) throws GobiiDtoMappingException;
    ReferenceDTO replace(Integer referenceId, ReferenceDTO referenceDTO) throws  GobiiDtoMappingException;
    ReferenceDTO get(Integer referenceId) throws GobiiDtoMappingException;
    List<ReferenceDTO> getList() throws GobiiDtoMappingException;

}
