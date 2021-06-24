package org.gobiiproject.gobiisampletrackingdao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.gobiiproject.gobiimodel.entity.Marker;

public interface MarkerDao {

    List<Marker> getMarkers(Integer pageSize, Integer rowOffset,
                            Integer markerId, Integer datasetId);

    List<Marker> getMarkers(Set<Integer> markerIds,
                            Set<String> markerNames,
                            Set<String> datasetIds,
                            Integer pageSize,
                            Integer markerIdCursor);

    List<Marker> getMarkersByMap(Integer pageSize, Integer rowOffset,
                                 Integer mapsetId, String mapsetName,
                                 Integer linkageGroupId, String linkageGroupName,
                                 BigDecimal minPositions, BigDecimal maxPosition,
                                 Integer datasetId);

    Marker getMarkerById(Integer markerId);

    List<Marker> getMarkersByMarkerIdCursor(Integer pageSize,
                                            Integer markerIdCursor,
                                            Integer markerId,
                                            Integer datasetId);

    List<Marker> getMarkersByDatasetId(Integer datasetId,
                                       Integer pageSize, Integer rowOffset);


    List<Marker> getMarkersByMarkerIds(Set<Integer> markerIds);

    List<Marker> getMarkersByMarkerNames(Set<String> markerNames);

	List<Marker> getMarkersByPlatformMarkerNameTuples(List<List<String>> markerTuples);
    List<Marker> getMarkersByPlatformIdMarkerNameTuples(List<List<String>> markerTuples);

	List<Marker> queryMarkersByNamesAndPlatformId(Set<String> markerNames,
                                                  Integer platformId,
                                                  Integer pageSize,
                                                  Integer rowOffset) throws GobiiDaoException;

}
