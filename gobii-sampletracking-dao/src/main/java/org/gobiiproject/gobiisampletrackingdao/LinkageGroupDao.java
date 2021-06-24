package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.LinkageGroup;

import java.util.List;
import java.util.Set;

public interface LinkageGroupDao {

    List<LinkageGroup> getLinkageGroups(Integer pageSize, Integer rowOffset,
                                        Integer linkageGroupId, Integer mapsetId);

    List<LinkageGroup> getLinkageGroupsByNames(Set<String> linkageGroupNames,
                                               Integer mapsetId,
                                               Integer pageSize,
                                               Integer rowOffset);
}
