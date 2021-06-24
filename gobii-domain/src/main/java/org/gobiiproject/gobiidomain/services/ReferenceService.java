package org.gobiiproject.gobiidomain.services;

import java.util.List;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.auditable.ReferenceDTO;

/**
 * Created by Angel on 5/4/2016.
 * Modified by Yanii on 1/26/2017
 */
public interface ReferenceService {

    List<ReferenceDTO> getReferences() throws GobiiDomainException;
    ReferenceDTO createReference(ReferenceDTO referenceDTO) throws GobiiDomainException;
    ReferenceDTO replaceReference(Integer referenceId, ReferenceDTO referenceDTO) throws GobiiDomainException;
    ReferenceDTO getReferenceById(Integer referenceId) throws GobiiDomainException;

}
