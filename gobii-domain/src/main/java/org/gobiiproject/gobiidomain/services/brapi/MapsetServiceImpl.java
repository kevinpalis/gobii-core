package org.gobiiproject.gobiidomain.services.brapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Mapset;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiisampletrackingdao.MapsetDao;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
@Slf4j
public class MapsetServiceImpl implements MapsetService {

    @Autowired
    private MapsetDao mapsetDao;

    /**
     * Gets the list of Genome Maps in the database by Page Number and Page Size
     *
     * @param pageNum  - Page Number to be fetched
     * @param pageSize - Page Size to be fetched
     * @return List of Brapi Specified Genome Maps DTO
     * @throws GobiiException
     */
    @Override
    public PagedResult<MapsetDTO> getMapSets(
        Integer pageSize,
        Integer pageNum,
        Integer studyDbId
    ) throws GobiiException {

        PagedResult<MapsetDTO> pagedResult = new PagedResult<>();

        try {

            Objects.requireNonNull(pageSize, "pageSize : Required non null");
            Objects.requireNonNull(pageNum, "pageNum : Required non null");

            List<MapsetDTO> mapsetDTOS = new ArrayList<>();

            Integer rowOffset = pageNum*pageSize;

            List<Mapset> mapsets = mapsetDao.getMapsetsWithCounts(
                pageSize,
                rowOffset,
                null,
                studyDbId);

            for (Mapset mapset : mapsets) {
                MapsetDTO mapsetDTO = new MapsetDTO();
                ModelMapper.mapEntityToDto(mapset, mapsetDTO);
                mapsetDTOS.add(mapsetDTO);
            }

            pagedResult.setResult(mapsetDTOS);
            pagedResult.setCurrentPageNum(pageNum);
            pagedResult.setCurrentPageSize(mapsetDTOS.size());

            return pagedResult;

        } catch (NullPointerException e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }

    @Override
    public MapsetDTO getMapSetById(Integer mapDbId) throws GobiiException {
        MapsetDTO mapsetDTO = new MapsetDTO();
        try {
            Objects.requireNonNull(mapDbId, "mapDbId : Required non null");
            Mapset mapset = mapsetDao.getMapsetWithCountsById(mapDbId);
            ModelMapper.mapEntityToDto(mapset, mapsetDTO);
            return mapsetDTO;
        }
        catch(NullPointerException e) {
            throw new GobiiDomainException(e);
        }
    }
}
