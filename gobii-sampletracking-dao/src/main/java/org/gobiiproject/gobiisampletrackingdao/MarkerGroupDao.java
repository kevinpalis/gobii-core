package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;

import org.gobiiproject.gobiimodel.entity.MarkerGroup;

public interface MarkerGroupDao {

	MarkerGroup createMarkerGroup(MarkerGroup markerGroup);

	List<MarkerGroup> getMarkerGroups(Integer offset, Integer pageSize);

	MarkerGroup getMarkerGroup(Integer markerGroupId);

	MarkerGroup updateMarkerGroup(MarkerGroup markerGroup);

	void deleteMarkerGroup(MarkerGroup markerGroup) throws Exception;

	Long uploadMarkerGroupsFromFile(String filePath) throws GobiiDaoException;

	Long mapMarkerIdsForMarkerNamesAndPlatformIds(String filePath,
                                                  String outputFilePath) throws GobiiDaoException;

	Long mapMarkerIdsForMarkerNamesAndPlatformNames(String filePath,
                                                    String outputFilePath) throws GobiiDaoException;

}