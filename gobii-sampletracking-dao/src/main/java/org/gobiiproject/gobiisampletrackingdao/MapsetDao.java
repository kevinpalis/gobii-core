package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;

import org.gobiiproject.gobiimodel.entity.Mapset;

public interface MapsetDao {

    List<Mapset> getMapsetsWithCounts(
            Integer pageSize, Integer rowOffset,
            Integer mapsetId, Integer experimentId);

    Mapset getMapsetWithCountsById(Integer mapsetId);


    List<Mapset> getMapsets(
        Integer pageSize, Integer offset, Integer mapsetTypeId
    ) throws Exception;

	Mapset createMapset(Mapset createdMapset);

    Mapset getMapset(Integer mapsetId);
    
    Mapset getMapsetByName(String name);

	Mapset updateMapset(Mapset mapset);

	void deleteMapset(Mapset mapset) throws Exception; 
}
