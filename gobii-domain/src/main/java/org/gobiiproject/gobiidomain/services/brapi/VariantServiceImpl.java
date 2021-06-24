package org.gobiiproject.gobiidomain.services.brapi;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.PageToken;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.brapi.VariantDTO;
import org.gobiiproject.gobiimodel.dto.brapi.VariantsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.system.GenotypesRunTimeCursors;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.utils.JsonNodeUtils;
import org.gobiiproject.gobiisampletrackingdao.DnaRunDao;
import org.gobiiproject.gobiisampletrackingdao.MarkerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

@Transactional
public class VariantServiceImpl implements VariantService {

    Logger LOGGER = LoggerFactory.getLogger(GenotypeCallsService.class);

    @Autowired
    protected MarkerDao markerDao;

    @Autowired
    private DnaRunDao dnaRunDao = null;

    @Override
    public PagedResult<VariantDTO> getVariants(
        Integer pageSize,
        String pageToken,
        Integer variantDbId,
        Integer variantSetDbId) throws GobiiException {

        PagedResult<VariantDTO> returnVal = new PagedResult<>();

        List<VariantDTO> variants;

        Integer markerIdCursor = null;

        try {

            Objects.requireNonNull(pageSize, "pageSize : Required non null");

            if(pageToken != null) {
                Map<String, Integer> pageTokenParts = PageToken.decode(pageToken);
                markerIdCursor = pageTokenParts.getOrDefault("markerIdCursor", 0);
            }

            List<Marker> markers =
                markerDao.getMarkersByMarkerIdCursor(
                    pageSize,
                    markerIdCursor,
                    variantDbId,
                    variantSetDbId);

            variants = mapMarkersToVariants(markers);
            returnVal.setResult(variants);
            returnVal.setCurrentPageSize(pageSize);

            //Set page token only if there are variants
            if(markers.size() > 0) {
                Map<String, Integer> nextPageCursorMap = new HashMap<>();
                //set next page offset and column offset as page token parts
                nextPageCursorMap
                    .put("markerIdCursor", markers.get(markers.size() - 1).getMarkerId());
                String nextPageToken = PageToken.encode(nextPageCursorMap);
                returnVal.setNextPageToken(nextPageToken);
            }

            return  returnVal;

        }
        catch (NullPointerException e) {
            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }

    private List<VariantDTO> mapMarkersToVariants(List<Marker> markers) {

        List<VariantDTO> variants = new ArrayList<>();

        for(Marker marker : markers) {

            variants.add(mapMakrerToVariant(marker));
        }

        return variants;
    }

    private VariantDTO mapMakrerToVariant(Marker marker) {

        VariantDTO variantDTO = new VariantDTO();

        ModelMapper.mapEntityToDto(marker, variantDTO);

        if(marker.getDatasetMarkerIdx() != null) {

            List<String> variantSetDbIds = JsonNodeUtils.getListFromIterator(
                marker.getDatasetMarkerIdx().fieldNames());

            variantDTO.setVariantSetDbIds(variantSetDbIds);

        }

        if(marker.getMarkerName() != null) {
            variantDTO.setVariantNames(new ArrayList<>());
            variantDTO.getVariantNames().add(marker.getMarkerName());
        }

        return variantDTO;

    }

    @Override
    public VariantDTO getVariantByVariantDbId(Integer variantDbId) throws GobiiException {

        try {

            Objects.requireNonNull(variantDbId, "variantDbId : Required non null");
            Marker marker = markerDao.getMarkerById(variantDbId);
            return mapMakrerToVariant(marker);
        }
        catch (NullPointerException e) {
            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

    }

    @Override
    public PagedResult<VariantDTO> getVariantsByVariantSearchQuery(
        VariantsSearchQueryDTO variantsSearchQuery,
        Integer pageSize,
        String pageToken) throws GobiiException {

        PagedResult<VariantDTO> returnVal = new PagedResult<>();
        List<VariantDTO> variants = new ArrayList<>();
        Integer markerIdCursor = 0;
        Map<String, Integer> nextPageCursorMap;

        try {

            if(pageToken != null) {
                Map<String, Integer> pageTokenParts = PageToken.decode(pageToken);
                markerIdCursor = pageTokenParts.getOrDefault("markerIdCursor", 0);
            }

            List<Marker> markers = markerDao.getMarkers(
                variantsSearchQuery.getVariantDbIds(), variantsSearchQuery.getVariantNames(),
                variantsSearchQuery.getVariantSetDbIds(), pageSize, markerIdCursor);

            variants.addAll(mapMarkersToVariants(markers));

            if(variants.size() == pageSize) {
                nextPageCursorMap = new HashMap<>();
                nextPageCursorMap.put("markerIdCursor", markerIdCursor);
                String nextPageToken = PageToken.encode(nextPageCursorMap);
                returnVal.setNextPageToken(nextPageToken);
            }

            returnVal.setResult(variants);
            returnVal.setCurrentPageSize(variants.size());
            return returnVal;
        }
        catch (NullPointerException e) {
            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }

    @Override
    public PagedResult<VariantDTO> getVariantsByGenotypesExtractQuery(
        GenotypeCallsSearchQueryDTO genotypesSearchQuery,
        Integer pageSize,
        String pageToken) throws GobiiException {

        final int dnaRunBinSize = 1000;

        PagedResult<VariantDTO> returnVal = new PagedResult<>();

        List<VariantDTO> variants = new ArrayList<>();

        GenotypesRunTimeCursors cursors = new GenotypesRunTimeCursors();

        List<DnaRun> dnaRuns = new ArrayList<>();

        List<Marker> markers;

        Map<String, Integer> nextPageCursorMap = new HashMap<>();

        try {

            if(pageToken != null) {
                Map<String, Integer> pageTokenParts = PageToken.decode(pageToken);

                cursors.markerIdCursor = pageTokenParts.getOrDefault("markerIdCursor", 0);
                cursors.dnaRunBinCursor = pageTokenParts.getOrDefault("dnaRunBinCursor", 0);
            }

            int remainingPageSize = 0;

            while(variants.size() < pageSize) {

                cursors.markerDatasetIds = new HashSet<>();
                cursors.dnaRunDatasetIds = new HashSet<>();

                remainingPageSize = pageSize - variants.size();

                if (!genotypesSearchQuery.isCallSetsQueriesEmpty() ||
                !CollectionUtils.isEmpty(genotypesSearchQuery.getVariantSetDbIds())) {

                    dnaRuns = dnaRunDao.getDnaRuns(
                        genotypesSearchQuery.getCallSetDbIds(),
                        genotypesSearchQuery.getCallSetNames(),
                        genotypesSearchQuery.getSampleDbIds(),
                        genotypesSearchQuery.getSampleNames(),
                        genotypesSearchQuery.getSamplePUIs(),
                        genotypesSearchQuery.getGermplasmPUIs(),
                        genotypesSearchQuery.getGermplasmDbIds(),
                        genotypesSearchQuery.getGermplasmNames(),
                        genotypesSearchQuery.getVariantSetDbIds(),
                        null,
                        dnaRunBinSize,
                        cursors.dnaRunBinCursor,
                        null,
                        false);

                    if(dnaRuns.size() == 0) {
                        break;
                    }

                    for(DnaRun dnaRun : dnaRuns) {
                        cursors.dnaRunDatasetIds.addAll(
                            JsonNodeUtils.getKeysFromJsonObject(dnaRun.getDatasetDnaRunIdx()));
                    }
                }

                if(!CollectionUtils.isEmpty(genotypesSearchQuery.getVariantSetDbIds())) {
                    cursors.markerDatasetIds
                        .addAll(new HashSet<>(genotypesSearchQuery.getVariantSetDbIds()));
                    cursors.markerDatasetIds.retainAll(cursors.dnaRunDatasetIds);
                }
                else {
                    cursors.markerDatasetIds.addAll(cursors.dnaRunDatasetIds);
                }

                markers = markerDao.getMarkers(genotypesSearchQuery.getVariantDbIds(),
                    genotypesSearchQuery.getVariantNames(),
                    cursors.markerDatasetIds,
                    remainingPageSize, cursors.markerIdCursor);

                if(markers.size() > 0) {
                    cursors.markerIdCursor = markers.get(markers.size() - 1).getMarkerId();
                }

                variants.addAll(mapMarkersToVariants(markers));

                if(variants.size() < pageSize && dnaRuns.size() > 0) {
                    cursors.dnaRunBinCursor = dnaRuns.get(dnaRuns.size() - 1).getDnaRunId();
                    cursors.markerIdCursor = 0;
                }
                else if(variants.size() < pageSize) {
                    break;
                }
                else if(variants.size() == pageSize) {
                    nextPageCursorMap = new HashMap<>();
                    nextPageCursorMap.put("markerIdCursor", cursors.markerIdCursor);
                    nextPageCursorMap.put("dnaRunBinCursor", cursors.dnaRunBinCursor);
                    break;
                }
            }
            if(nextPageCursorMap.size() > 0) {
                String nextPageToken = PageToken.encode(nextPageCursorMap);
                returnVal.setNextPageToken(nextPageToken);
            }
            returnVal.setResult(variants);
            returnVal.setCurrentPageSize(variants.size());
            return returnVal;
        }
        catch (NullPointerException e) {
            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }


}
