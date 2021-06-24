package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiimodel.dto.brapi.LinkageGroupDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

import java.util.List;

public interface LinkageGroupService {

    List<LinkageGroupDTO> getLinkageGroups(Integer pageSize, Integer pageNum);
    PagedResult<LinkageGroupDTO> getLinkageGroupsByMapId(
            Integer mapId, Integer pageSize, Integer pageNum);
}
