package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.MarkerPositions;
import org.gobiiproject.gobiimodel.dto.brapi.MarkerPositionsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.MarkerLinkageGroup;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiisampletrackingdao.MarkerLinkageGroupDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Transactional
public class MarkerPositionsServiceImpl implements MarkerPositionsService {

    Logger LOGGER = LoggerFactory.getLogger(MarkerPositionsServiceImpl.class);

    @Autowired
    private MarkerLinkageGroupDao markerLinkageGroupDao;

    @Override
    public PagedResult<MarkerPositions> getMarkerPositions(
        Integer pageSize, Integer pageNum,
        MarkerPositions markerPositionsFilter, BigDecimal minPosition,
        BigDecimal maxPosition, Integer variantSetDbId) throws GobiiException {

        List<MarkerPositions> markerPositionsList = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize);
            Objects.requireNonNull(pageNum);

            Integer rowOffset = pageNum*pageSize;

            List<MarkerLinkageGroup> markerLinkageGroups =
                markerLinkageGroupDao.getMarkerLinkageGroups(
                    pageSize,
                    rowOffset,
                    markerPositionsFilter.getMapDbId(),
                    markerPositionsFilter.getMapName(),
                    null,
                    markerPositionsFilter.getLinkageGroupName(),
                    markerPositionsFilter.getVariantDbId(),
                    null,
                    minPosition,
                    maxPosition,
                    variantSetDbId);

            MarkerPositions markerPositions;

            for(MarkerLinkageGroup markerLinkageGroup : markerLinkageGroups) {
                markerPositions = new MarkerPositions();
                ModelMapper.mapEntityToDto(markerLinkageGroup, markerPositions);
                markerPositionsList.add(markerPositions);
            }

            return PagedResult.createFrom(pageNum, markerPositionsList);
        }
        catch (NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                "Internal Server Error. Please check the error log");
        }
    }

    @Override
    public PagedResult<MarkerPositions> getMarkerPositionsBySearchQuery(
        MarkerPositionsSearchQueryDTO searchQuery,
        Integer pageSize,
        Integer pageNum) throws GobiiException {

        List<MarkerPositions> markerPositionsList = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize, "pageSize: required non null");
            Objects.requireNonNull(pageNum, "pageNum: required non null");

            Integer rowOffset = pageNum*pageSize;

            List<MarkerLinkageGroup> markerLinkageGroups =
                markerLinkageGroupDao.getMarkerLinkageGroups(
                    pageSize,
                    rowOffset,
                    searchQuery.getMapDbIds(),
                    searchQuery.getMapNames(),
                    null,
                    searchQuery.getLinkageGroupNames(),
                    searchQuery.getVariantDbIds(),
                    null,
                    searchQuery.getMinPosition(),
                    searchQuery.getMaxPosition(),
                    searchQuery.getVariantSetDbIds());

            MarkerPositions markerPositions;

            for(MarkerLinkageGroup markerLinkageGroup : markerLinkageGroups) {
                markerPositions = new MarkerPositions();
                ModelMapper.mapEntityToDto(markerLinkageGroup, markerPositions);
                markerPositionsList.add(markerPositions);
            }

            return PagedResult.createFrom(pageNum, markerPositionsList);

        }
        catch (NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                "Internal Server Error. Please check the error log");
        }
    }
}
