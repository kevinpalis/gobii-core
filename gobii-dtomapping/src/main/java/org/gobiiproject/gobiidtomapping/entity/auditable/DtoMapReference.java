
package org.gobiiproject.gobiidtomapping.entity.auditable;

import org.gobiiproject.gobiidtomapping.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMap;
import org.gobiiproject.gobiimodel.headerlesscontainer.ReferenceDTO;

import java.util.List;

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
