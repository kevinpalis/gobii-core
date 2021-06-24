/**
 * ReferenceServiceImpl.java
 * 
 * @author Rodolfo N. Duldulao, Jr.
 */
package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.dto.gdmv3.ReferenceDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface ReferenceService {

    PagedResult<ReferenceDTO> getReferences(Integer page, Integer pageSize);

	ReferenceDTO createReference(ReferenceDTO request, String createdBy) throws Exception;

	ReferenceDTO getReference(Integer referenceId) throws Exception;

	ReferenceDTO updateReference(Integer referenceId, ReferenceDTO request, String updatedBy) throws Exception;

	void deleteReference(Integer referenceId) throws Exception;
    
}