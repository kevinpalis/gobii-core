package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiimodel.dto.brapi.StudiesDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface StudiesService {

    PagedResult<StudiesDTO> getStudies(Integer pageSize, Integer page,
                                       Integer projectId);

}
