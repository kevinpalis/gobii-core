package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.MarkerLinkageGroup;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface MarkerLinkageGroupDao {

    List<MarkerLinkageGroup>
    getMarkerLinkageGroups(Integer pageSize , Integer rowOffset,
                           Integer mapsetId, String mapsetName,
                           Integer linkageGroupId, String linkageGroupName,
                           Integer markerId, String markerName,
                           BigDecimal minPosition, BigDecimal maxPosition,
                           Integer datasetId);

    List<MarkerLinkageGroup>
    getMarkerLinkageGroups(Integer pageSize , Integer rowOffset,
                           Set<Integer> mapsetIds, Set<String> mapsetNames,
                           Set<Integer> linkageGroupIds, Set<String> linkageGroupNames,
                           Set<Integer> markerIds, Set<String> markerNames,
                           BigDecimal minPosition, BigDecimal maxPosition,
                           Set<String> datasetIds);


}
