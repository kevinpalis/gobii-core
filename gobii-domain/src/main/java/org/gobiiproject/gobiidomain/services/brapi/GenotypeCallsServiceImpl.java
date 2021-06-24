package org.gobiiproject.gobiidomain.services.brapi;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.PageToken;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsDTO;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsResult;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.system.GenotypesRunTimeCursors;
import org.gobiiproject.gobiimodel.dto.system.Hdf5InterfaceResultDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResultTyped;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.BrapiDefaults;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.JsonNodeUtils;
import org.gobiiproject.gobiisampletrackingdao.DnaRunDao;
import org.gobiiproject.gobiisampletrackingdao.MarkerDao;
import org.gobiiproject.gobiisampletrackingdao.hdf5.HDF5Interface;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Transactional
@Slf4j
public class GenotypeCallsServiceImpl implements GenotypeCallsService {

    final String unphasedSep = "/";
    final String unknownChar = "N/N";

    @Autowired
    private DnaRunDao dnaRunDao = null;

    @Autowired
    private MarkerDao markerDao = null;

    @Autowired
    private HDF5Interface hdf5Interface;

    /**
     * Get Genotypes to callSetDbId.
     * BrAPI field callSetDbId corresponds to dnaRunId in GDM system
     *
     * @param callSetDbId - Corresponds to dnaRunId for which
     *                    genotype calls need to fetched.
     *
     * @param pageSize - Number of genotype calls
     *                 per page to be fetched.
     *
     * @param pageToken - Cursor to identify where the page starts.
     *                  DnaRun can be in more than one dataset.
     *                  Assume, a given dnaRun is in multiple
     *                  dataset {7,5,6} and each with set of markers of their own.
     *                  {datasetId-markerId} pageToken means,
     *                  fetch Genotypes from datasetIds greater
     *                  than or equal to given datasetId and
     *                  markerId ascending order cursors until the page fills.
     *                  nextPageToken will be where datasetId
     *                  and markerId starts for next page.
     *
     * @return List of Genotype calls for given page.
     *
     */
    @Override
    public PagedResultTyped<GenotypeCallsResult>
    getGenotypeCallsByCallSetId(Integer callSetDbId,
                                Integer pageSize,
                                String pageToken) {

        PagedResultTyped<GenotypeCallsResult> returnVal = new PagedResultTyped<>();

        GenotypesRunTimeCursors cursors = new GenotypesRunTimeCursors();

        List<GenotypeCallsDTO> genotypeCalls = new ArrayList<>();

        try {

            Map<String, Integer> pageTokenParts = PageToken.decode(pageToken);

            if(pageTokenParts != null ) {
                cursors.startDatasetId = pageTokenParts.getOrDefault("datasetID", null);
                cursors.markerIdCursor = pageTokenParts.getOrDefault("markerId", null);
            }

            if(pageSize == null) {
                pageSize = Integer.parseInt(BrapiDefaults.pageSize);
            }

            int genotypesToBeRead = pageSize;

            //Get DNA run
            DnaRun dnaRun = dnaRunDao.getDnaRunById(callSetDbId);

            // Parse list of datasetIds the dnarun belongs to
            List<String> dnaRunDatasetIds =
                JsonNodeUtils.getKeysFromJsonObject(dnaRun.getDatasetDnaRunIdx());

            // Sort dataset ids
            Collections.sort(dnaRunDatasetIds);

            int datasetIdCursorStart = 0;

            if(cursors.startDatasetId != null && cursors.startDatasetId > 0) {
                datasetIdCursorStart = dnaRunDatasetIds.indexOf(cursors.startDatasetId.toString());
            }

            // Read Genotypes for makers in dataset until page is filled
            for(int datasetIdCursor = datasetIdCursorStart;
                datasetIdCursor < dnaRunDatasetIds.size(); datasetIdCursor++) {

                cursors.markerHdf5IndexMap = new HashMap<>();

                cursors.dnarunHdf5IndexMap = new HashMap<>();

                String datasetId = dnaRunDatasetIds.get(datasetIdCursor);

                cursors.dnarunHdf5IndexMap.put(datasetId, new ArrayList<>());

                cursors.dnarunHdf5IndexMap
                    .get(datasetId)
                    .add(dnaRun.getDatasetDnaRunIdx().get(datasetId).textValue());


                List<Marker> markers =
                    markerDao.getMarkersByMarkerIdCursor(
                        genotypesToBeRead, cursors.markerIdCursor,
                        null, Integer.parseInt(datasetId));

                GenotypeCallsDTO genotypeCall;

                int indexOffset = genotypeCalls.size();

                // Add Marker and DnaRun Metadata associated with genotype calls.
                // Extract HdF5 index for each marker and map it by dataset id.
                for(Marker marker : markers) {

                    genotypeCall = new GenotypeCallsDTO();

                    ModelMapper.mapEntityToDto(marker, genotypeCall);

                    ModelMapper.mapEntityToDto(dnaRun, genotypeCall);

                    genotypeCall.setVariantSetDbId(Integer.parseInt(datasetId));

                    if(!cursors.markerHdf5IndexMap.containsKey(datasetId)) {
                        cursors.markerHdf5IndexMap.put(datasetId, new ArrayList<>());
                    }

                    cursors.markerHdf5IndexMap
                        .get(datasetId)
                        .add(marker.getDatasetMarkerIdx().get(datasetId).textValue());

                    genotypeCalls.add(genotypeCall);

                }

                Hdf5InterfaceResultDTO extractResult =
                    extractGenotypes(cursors.markerHdf5IndexMap, cursors.dnarunHdf5IndexMap);

                readGenotypesFromFile(genotypeCalls, extractResult.getGenotypeFile(), indexOffset);

                FileUtils.deleteDirectory(new File(extractResult.getOutputFolder()));

                if(markers.size() >= genotypesToBeRead) {
                    break;
                }
                else {
                    genotypesToBeRead -= markers.size();
                }


            }

            if(genotypeCalls.size() >= pageSize) {

                Map<String ,Integer> nextPageCursorMap = new HashMap<>();

                nextPageCursorMap.put(
                    "datasetId", genotypeCalls.get(genotypeCalls.size() - 1).getVariantSetDbId());

                nextPageCursorMap.put(
                    "markerId", genotypeCalls.get(genotypeCalls.size() - 1).getVariantDbId());

                String nextPageToken = PageToken.encode(nextPageCursorMap);

                returnVal.setNextPageToken(nextPageToken);

            }

            returnVal.setResult(getGenotypeCallsResult(genotypeCalls));
            returnVal.setCurrentPageSize(genotypeCalls.size());


            return returnVal;

        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                "Internal Server Error. Please check the error log");
        }

    }



    /**
     * Gets the genotype calls from all datasets for given markerId.
     * @param markerId - markerId given by user.
     * @param pageToken - String token with datasetId and markerId
     *                  combination of last page's last element.
     *                  If unspecified, first page will be extracted.
     * @param pageSize - Page size to extract. If not specified default page size.
     * @return List of Genotype calls for given dnarunId.
     */
    @Override
    public PagedResultTyped<GenotypeCallsResult>
    getGenotypeCallsByVariantDbId(Integer markerId,
                                  Integer pageSize,
                                  String pageToken) {

        PagedResultTyped<GenotypeCallsResult> returnVal = new PagedResultTyped<>();
        List<GenotypeCallsDTO> genotypeCalls = new ArrayList<>();
        GenotypesRunTimeCursors cursors = new GenotypesRunTimeCursors();
        String nextPageToken;

        try {

            Map<String, Integer> pageTokenParts = PageToken.decode(pageToken);

            if(pageTokenParts != null ) {
                cursors.startDatasetId = pageTokenParts.getOrDefault("datasetId", null);
                cursors.dnaRunIdCursor = pageTokenParts.getOrDefault("dnaRunId", null);
            }

            if(pageSize == null) {
                pageSize = Integer.parseInt(BrapiDefaults.pageSize);
            }

            int genotypesToBeRead = pageSize;

            Marker marker = markerDao.getMarkerById(markerId);

            // Parse list of datasetIds the dnarun belongs to
            List<String> markerDatasetIds =
                JsonNodeUtils.getKeysFromJsonObject(marker.getDatasetMarkerIdx());

            // Sort dataset ids
            Collections.sort(markerDatasetIds);

            int datasetIdCursorStart = 0;

            if(cursors.startDatasetId != null && cursors.startDatasetId > 0) {
                datasetIdCursorStart = markerDatasetIds.indexOf(cursors.startDatasetId.toString());
            }

            // Read Genotypes for makers in dataset until page is filled
            for(int datasetIdCursor = datasetIdCursorStart;
                datasetIdCursor < markerDatasetIds.size(); datasetIdCursor++) {

                cursors.markerHdf5IndexMap = new HashMap<>();
                cursors.dnarunHdf5IndexMap = new HashMap<>();

                String datasetId = markerDatasetIds.get(datasetIdCursor);
                cursors.markerHdf5IndexMap.put(datasetId, new ArrayList<>());
                cursors.markerHdf5IndexMap
                    .get(datasetId)
                    .add(marker.getDatasetMarkerIdx().get(datasetId).textValue());
                List<DnaRun> dnaRuns =
                    dnaRunDao.getDnaRunsByDnaRunIdCursor(
                        genotypesToBeRead,
                        cursors.dnaRunIdCursor,
                        Integer.parseInt(datasetId));

                int indexOffset = genotypeCalls.size();

                // Add Marker and DnaRun Metadata associated with genotype calls.
                // Extract HdF5 index for each marker and map it by dataset id.
                for(DnaRun dnaRun : dnaRuns) {

                    GenotypeCallsDTO genotypeCall = new GenotypeCallsDTO();

                    ModelMapper.mapEntityToDto(marker, genotypeCall);

                    ModelMapper.mapEntityToDto(dnaRun, genotypeCall);

                    genotypeCall.setVariantSetDbId(Integer.parseInt(datasetId));

                    if(!cursors.dnarunHdf5IndexMap.containsKey(datasetId)) {
                        cursors.dnarunHdf5IndexMap.put(datasetId, new ArrayList<>());
                    }

                    cursors.dnarunHdf5IndexMap
                        .get(datasetId)
                        .add(dnaRun.getDatasetDnaRunIdx().get(datasetId).textValue());

                    genotypeCalls.add(genotypeCall);

                }

                if(dnaRuns.size() > 0) {
                    Hdf5InterfaceResultDTO extractResult = extractGenotypes(
                        cursors.markerHdf5IndexMap, cursors.dnarunHdf5IndexMap);
                    readGenotypesFromFile(genotypeCalls, extractResult.getGenotypeFile(),
                        indexOffset);
                    FileUtils.deleteDirectory(new File(extractResult.getOutputFolder()));
                }
                else {
                    continue;
                }

                if(dnaRuns.size() >= genotypesToBeRead) {
                    break;
                }
                else {
                    genotypesToBeRead -= dnaRuns.size();
                }
            }

            if(genotypeCalls.size() >= pageSize) {
                Map<String ,Integer> nextPageCursorMap = new HashMap<>();
                nextPageCursorMap.put(
                    "datasetId", genotypeCalls.get(genotypeCalls.size() - 1).getVariantSetDbId());
                nextPageCursorMap.put(
                    "dnaRunId", genotypeCalls.get(genotypeCalls.size() - 1).getCallSetDbId());
                nextPageToken = PageToken.encode(nextPageCursorMap);
                returnVal.setNextPageToken(nextPageToken);
            }
            returnVal.setCurrentPageSize(genotypeCalls.size());

            returnVal.setResult(getGenotypeCallsResult(genotypeCalls));

            return returnVal;

        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.UNKNOWN,
                "Internal Server Error. Please check the error log");
        }
    }

    /**
     * Gets the genotype calls in given datasets.
     * @param datasetId - datasetId given by user.
     * @param pageSize - Page size to extract.
     *                 If not specified default page size.
     * @param pageToken - String token with datasetId and
     *                  markerId combination of last page's last element.
     *                  If unspecified, first page will be extracted.
     * @return List of Genotype calls for given dnarunId.
     */
    @Override
    public PagedResultTyped<GenotypeCallsResult>
    getGenotypeCallsByVariantSetDbId(Integer datasetId, String mapsetName,
                                     String linkageGroupName, BigDecimal minPosition,
                                     BigDecimal maxPosition, Integer pageSize,
                                     String pageToken) {

        PagedResultTyped<GenotypeCallsResult> returnVal = new PagedResultTyped<>();

        //Result
        List<GenotypeCallsDTO> genotypeCalls = new ArrayList<>();

        GenotypesRunTimeCursors cursors = new GenotypesRunTimeCursors();

        //next page token for the next page
        String nextPageToken;

        List<DnaRun> dnaRuns;
        List<Marker> markers;

        try {

            if(pageToken != null) {
                Map<String, Integer> pageTokenParts = PageToken.decode(pageToken);
                cursors.pageOffset = pageTokenParts.getOrDefault("pageOffset", 0);
                cursors.dnaRunOffset = pageTokenParts.getOrDefault("dnaRunOffset", 0);
            }

            dnaRuns = dnaRunDao.getDnaRunsByDatasetId(datasetId, pageSize,
                cursors.dnaRunOffset, false);

            /**
             * Case 1: When fetched dnaRuns is equal to the page size requested.
             */
            if(dnaRuns.size() == pageSize) {
                cursors.markerPageSize = 1;
                cursors.columnOffset = 0;
                cursors.nextDnaRunOffset = cursors.dnaRunOffset + dnaRuns.size();
                cursors.nextPageOffset = cursors.pageOffset;
            }
            //case 2: total number of dnarun in the dataset
            //is less than page size
            else if(cursors.dnaRunOffset == 0 && dnaRuns.size() < pageSize) {

                cursors.markerPageSize =
                    (int) Math.ceil(((double)pageSize) / (double)dnaRuns.size());

                int columnExcess = ((cursors.markerPageSize*dnaRuns.size()) - pageSize);

                if(columnExcess > 0) {
                    cursors.nextDnaRunOffset = dnaRuns.size() - columnExcess;
                }
                else {
                    cursors.nextDnaRunOffset = 0;
                }

                if(cursors.nextDnaRunOffset > 0) {
                    cursors.nextPageOffset = cursors.pageOffset + cursors.markerPageSize - 1;
                }
                else {
                    cursors.nextPageOffset = cursors.pageOffset + cursors.markerPageSize;
                }

                cursors.columnOffset = 0;
            }
            //case 3: columnoffset > 0 and dnruns size is
            //less than page size.
            else if(cursors.dnaRunOffset > 0 && dnaRuns.size() < pageSize) {

                int remainingPageSize = pageSize - dnaRuns.size();

                List<DnaRun> remainingDnaRuns =
                    dnaRunDao.getDnaRunsByDatasetId(datasetId, remainingPageSize, 0);


                if(remainingDnaRuns.size() < remainingPageSize) {

                    int prevPageExcess = 0;

                    if(dnaRuns.size() == 0) {
                        cursors.pageOffset += 1;
                        cursors.columnOffset = 0;
                    }
                    else {
                        prevPageExcess = 1;
                        cursors.columnOffset = cursors.dnaRunOffset;
                    }

                    dnaRuns = remainingDnaRuns;

                    cursors.markerPageSize =
                        (int) Math.ceil(((double)remainingPageSize) / (double)dnaRuns.size());

                    cursors.nextDnaRunOffset =
                        dnaRuns.size() - (
                            (cursors.markerPageSize*dnaRuns.size()) - remainingPageSize
                        );

                    if(prevPageExcess == 1) {
                        cursors.markerPageSize += 1;
                    }

                    if(cursors.nextDnaRunOffset > 0) {
                        cursors.nextPageOffset = cursors.pageOffset + cursors.markerPageSize - 1;
                    }
                    else {
                        cursors.nextPageOffset = cursors.pageOffset + cursors.markerPageSize;
                    }


                }
                else if(remainingDnaRuns.size() == remainingPageSize) {

                    if(dnaRuns.size() == 0) {
                        cursors.pageOffset += 1;
                        cursors.columnOffset = 0;
                        cursors.markerPageSize = 1;
                        cursors.nextPageOffset = cursors.pageOffset;
                    }
                    else {
                        cursors.columnOffset = remainingPageSize;
                        cursors.markerPageSize = 2;
                        cursors.nextPageOffset = cursors.pageOffset + 1;
                    }

                    cursors.nextDnaRunOffset = remainingPageSize;

                    remainingDnaRuns.addAll(dnaRuns);

                    dnaRuns = remainingDnaRuns;

                }

            }

            if(mapsetName == null) {
                markers = markerDao.getMarkersByDatasetId(datasetId, cursors.markerPageSize,
                    cursors.pageOffset);
            }
            else {
                markers = markerDao.getMarkersByMap(cursors.markerPageSize, cursors.pageOffset,
                    null, mapsetName, null, linkageGroupName, minPosition, maxPosition, datasetId);
            }

            if(markers.size() == 0) {
                returnVal.setResult(getGenotypeCallsResult(genotypeCalls));
                returnVal.setCurrentPageSize(0);
                return  returnVal;
            }

            //HDF5 index map for markers
            for(Marker marker : markers) {

                if(!cursors.markerHdf5IndexMap.containsKey(datasetId.toString())) {
                    cursors.markerHdf5IndexMap.put(datasetId.toString(), new ArrayList<>());
                }
                cursors.markerHdf5IndexMap
                    .get(datasetId.toString())
                    .add(marker.getDatasetMarkerIdx().get(datasetId.toString()).textValue());

            }

            Integer orderIndex = 0;

            //HDF5 index for dnaruns
            for(DnaRun dnaRun : dnaRuns) {

                if(!cursors.dnarunHdf5IndexMap.containsKey(datasetId.toString())) {
                    cursors.dnarunHdf5IndexMap.put(datasetId.toString(), new ArrayList<>());
                }

                cursors.dnarunHdf5IndexMap
                    .get(datasetId.toString())
                    .add(dnaRun.getDatasetDnaRunIdx().get(datasetId.toString()).textValue());

                if(cursors.dnarunHdf5OrderMap.containsKey(
                    Integer.parseInt(
                        dnaRun.getDatasetDnaRunIdx().get(datasetId.toString()).asText())
                )) {

                    cursors.columnOffset -= 1;
                }

                cursors.dnarunHdf5OrderMap.put(
                    Integer.parseInt(
                        dnaRun.getDatasetDnaRunIdx().get(datasetId.toString()).asText()),
                    orderIndex);

                orderIndex++;

            }


            Hdf5InterfaceResultDTO extractResult =
                this.extractGenotypes(
                    cursors.markerHdf5IndexMap,
                    cursors.dnarunHdf5IndexMap);

            this.readGenotypesFromFile(
                genotypeCalls,
                extractResult.getGenotypeFile(),
                pageSize,
                datasetId,
                cursors.columnOffset,
                markers,
                dnaRuns,
                new ArrayList<>(cursors.dnarunHdf5OrderMap.values()));

            returnVal.setResult(getGenotypeCallsResult(genotypeCalls));

            //Set page token only if there are genotypes
            if(genotypeCalls.size() > 0) {
                Map<String, Integer> nextPageCursorMap = new HashMap<>();

                //set next page offset and column offset as page token parts
                nextPageCursorMap.put("pageOffset", cursors.nextPageOffset);
                nextPageCursorMap.put("dnaRunOffset", cursors.nextDnaRunOffset);

                nextPageToken = PageToken.encode(nextPageCursorMap);

                if(genotypeCalls.size() >= pageSize) {
                    returnVal.setNextPageToken(nextPageToken);
                }
                returnVal.setCurrentPageSize(genotypeCalls.size());
            }
            FileUtils.deleteDirectory(new File(extractResult.getOutputFolder()));
        }
        catch (NullPointerException | IOException e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

        return returnVal;
    }


    private void indexMarkers(List<Marker> markers,
                              Map<String, List<String>> markerHdf5IndexMap,
                              Map<String, List<Marker>> markersByDatasetId,
                              Set<String> markerDatasetIds) {

        List<String> datasetsFromJsonNode;

        try {

            for (Marker marker : markers) {

                datasetsFromJsonNode =
                    JsonNodeUtils.getKeysFromJsonObject(marker.getDatasetMarkerIdx());

                for (String datasetId : datasetsFromJsonNode) {

                    if (!markerHdf5IndexMap.containsKey(datasetId)) {
                        markerHdf5IndexMap.put(datasetId, new ArrayList<>());
                    }

                    if (!markersByDatasetId.containsKey(datasetId)) {
                        markersByDatasetId.put(datasetId, new ArrayList<>());
                    }

                    markersByDatasetId.get(datasetId).add(marker);

                    markerHdf5IndexMap
                        .get(datasetId)
                        .add(marker.getDatasetMarkerIdx().get(datasetId).textValue());
                }

                markerDatasetIds.addAll(datasetsFromJsonNode);
            }
        }
        catch (Exception e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

    }

    private void indexDnaRuns(List<DnaRun> dnaRuns,
                              Set<String> dnaRunDatasetIds,
                              Map<String, List<String>> dnarunHdf5IndexMap,
                              Map<String, SortedMap<Integer, Integer>> dnarunHdf5OrderMap,
                              Map<String, List<DnaRun>> dnarunsByDatasetId,
                              Map<String, Integer> dnaRunOrderIndexMap) {

        Integer dnaRunOrderIndex;
        List<String> datasetsFromJsonNode;

        try {

            for (DnaRun dnaRun : dnaRuns) {

                datasetsFromJsonNode =
                    JsonNodeUtils.getKeysFromJsonObject(dnaRun.getDatasetDnaRunIdx());

                for (String datasetId : datasetsFromJsonNode) {

                    if (!dnarunHdf5IndexMap.containsKey(datasetId)) {

                        dnarunHdf5IndexMap.put(datasetId, new ArrayList<>());

                        dnarunHdf5OrderMap.put(datasetId, new TreeMap<>());

                        dnaRunOrderIndex = 0;

                    } else {
                        dnaRunOrderIndex = dnaRunOrderIndexMap.get(datasetId);
                    }

                    if (!dnarunsByDatasetId.containsKey(datasetId)) {
                        dnarunsByDatasetId.put(datasetId, new ArrayList<>());
                    }

                    dnarunsByDatasetId.get(datasetId).add(dnaRun);

                    dnarunHdf5IndexMap
                        .get(datasetId)
                        .add(dnaRun.getDatasetDnaRunIdx().get(datasetId).textValue());

                    dnarunHdf5OrderMap
                        .get(datasetId)
                        .put(Integer.parseInt(dnaRun.getDatasetDnaRunIdx().get(datasetId).asText()),
                            dnaRunOrderIndex);

                    dnaRunOrderIndex++;

                    dnaRunOrderIndexMap.put(datasetId, dnaRunOrderIndex);
                }

                dnaRunDatasetIds.addAll(datasetsFromJsonNode);
            }
        }
        catch (Exception e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

    }

    /**
     * Gets the genotype calls in given datasets.
     * @param genotypesSearchQuery - Search Query DTO.
     * @param pageToken - String token with datasetId and markerId combination
     *                  of last page's last element. If unspecified,
     *                  first page will be extracted.
     * @param pageSize - Page size to extract.
     *                 If not specified default page size.
     * @return List of Genotype calls for given dnarunId.
     */
    @Override
    public PagedResultTyped<GenotypeCallsResult>
    getGenotypeCallsByExtractQuery(GenotypeCallsSearchQueryDTO genotypesSearchQuery,
                                   Integer pageSize,
                                   String pageToken) {

        final int dnaRunBinSize = 1000;
        final int markerBinSize = 1000;

        PagedResultTyped<GenotypeCallsResult> returnVal = new PagedResultTyped<>();

        List<GenotypeCallsDTO> genotypeCalls = new ArrayList<>();

        List<Marker> markers;

        List<DnaRun> dnaRuns;

        Map<String, SortedMap<Integer, Integer>> dnarunHdf5OrderMap;

        GenotypesRunTimeCursors cursors = new GenotypesRunTimeCursors();

        Map<String, Integer> nextPageCursorMap = new HashMap<>();

        boolean sampleMetaDataQueriesFound = false;

        try {

            if(pageToken != null) {
                Map<String, Integer> pageTokenParts = PageToken.decode(pageToken);
                cursors.datasetIdCursor = pageTokenParts.getOrDefault("datasetIdCursor", 0);
                cursors.pageOffset = pageTokenParts.getOrDefault("pageOffset", 0);
                cursors.dnaRunOffset = pageTokenParts.getOrDefault("dnaRunOffset", 0);
                cursors.dnaRunBinCursor = pageTokenParts.getOrDefault("dnaRunBinCursor", 0);
                cursors.markerBinCursor = pageTokenParts.getOrDefault("markerBinCursor", 0);
            }

            int remainingPageSize;

            while(genotypeCalls.size() < pageSize) {

                dnaRuns = new ArrayList<>();
                dnarunHdf5OrderMap = new HashMap<>();

                cursors.markerDatasetIds = new HashSet<>();
                cursors.dnaRunDatasetIds = new HashSet<>();
                cursors.markerHdf5IndexMap= new HashMap<>();
                cursors.dnarunHdf5IndexMap = new HashMap<>();
                cursors.markersByDatasetId= new HashMap<>();
                cursors.dnarunsByDatasetId = new HashMap<>();
                cursors.dnaRunOrderIndexMap = new HashMap<>();

                remainingPageSize = pageSize - genotypeCalls.size();

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
                        genotypesSearchQuery.getVariantSetDbIds(), null,
                        dnaRunBinSize, cursors.dnaRunBinCursor, null, false);

                    sampleMetaDataQueriesFound = true;

                }

                if(dnaRuns.size() > 0) {
                    indexDnaRuns(dnaRuns, cursors.dnaRunDatasetIds,
                        cursors.dnarunHdf5IndexMap, dnarunHdf5OrderMap,
                        cursors.dnarunsByDatasetId, cursors.dnaRunOrderIndexMap);
                }

                if (!genotypesSearchQuery.isVariantsQueriesEmpty() ||
                !CollectionUtils.isEmpty(genotypesSearchQuery.getVariantSetDbIds())) {

                    markers = markerDao.getMarkers(genotypesSearchQuery.getVariantDbIds(),
                        genotypesSearchQuery.getVariantNames(),
                        genotypesSearchQuery.getVariantSetDbIds(),
                        markerBinSize, cursors.markerBinCursor);

                    indexMarkers(markers, cursors.markerHdf5IndexMap,
                        cursors.markersByDatasetId, cursors.markerDatasetIds);

                    if(!sampleMetaDataQueriesFound) {

                        dnaRuns = dnaRunDao.getDnaRuns(null, null, null, null,
                            null, null, null, null, cursors.markerDatasetIds,null, dnaRunBinSize,
                            cursors.dnaRunBinCursor, null, false);

                        if(dnaRuns.size() > 0) {
                            indexDnaRuns(dnaRuns, cursors.dnaRunDatasetIds,
                                cursors.dnarunHdf5IndexMap, dnarunHdf5OrderMap,
                                cursors.dnarunsByDatasetId, cursors.dnaRunOrderIndexMap);
                        }
                    }
                } else {

                    markers = markerDao.getMarkers(
                        null, null, cursors.dnaRunDatasetIds,
                        markerBinSize, cursors.markerBinCursor);

                    indexMarkers(markers, cursors.markerHdf5IndexMap,
                        cursors.markersByDatasetId, cursors.markerDatasetIds);
                }

                if (!CollectionUtils.isEmpty(genotypesSearchQuery.getVariantSetDbIds())) {
                    cursors.dnaRunDatasetIds.retainAll(genotypesSearchQuery.getVariantSetDbIds());
                    cursors.markerDatasetIds.retainAll(genotypesSearchQuery.getVariantSetDbIds());
                }

                //Retains only common dataset ids
                cursors.markerDatasetIds.retainAll(cursors.dnaRunDatasetIds);

                List<String> commonDatasetIds = new ArrayList<>(cursors.markerDatasetIds);

                Collections.sort(commonDatasetIds);

                for (Integer datasetIdIdx = cursors.datasetIdCursor;
                     datasetIdIdx < commonDatasetIds.size(); datasetIdIdx++
                ) {

                    String datasetId = commonDatasetIds.get(datasetIdIdx);

                    Map<String, List<String>> dnaRunExtractIndex = new HashMap<>();

                    List<String> dnaRunHdf5IndicesDataset =
                        cursors.dnarunHdf5IndexMap.get(datasetId);

                    int totalDnaRuns = dnaRunHdf5IndicesDataset.size();
                    int nextDnaRunOffset;
                    int numOfMarkersReq;

                    if (pageSize < totalDnaRuns) {

                        if ((cursors.dnaRunOffset + pageSize) < totalDnaRuns) {

                            dnaRunHdf5IndicesDataset =
                                dnaRunHdf5IndicesDataset
                                    .subList(cursors.dnaRunOffset, cursors.dnaRunOffset + pageSize);

                            numOfMarkersReq = 1;

                            nextDnaRunOffset = cursors.dnaRunOffset + pageSize;

                            cursors.columnOffset = 0;

                        } else {

                            dnaRunHdf5IndicesDataset =
                                dnaRunHdf5IndicesDataset
                                    .subList(cursors.dnaRunOffset, totalDnaRuns);

                            int numOfDnaRunsNeeded = pageSize - dnaRunHdf5IndicesDataset.size();

                            numOfMarkersReq = 2;

                            if (numOfDnaRunsNeeded < cursors.dnaRunOffset) {

                                List<String> neededDnaRunIdxs =
                                    cursors.dnarunHdf5IndexMap.get(datasetId)
                                        .subList(0, numOfDnaRunsNeeded);

                                neededDnaRunIdxs.addAll(dnaRunHdf5IndicesDataset);

                                dnaRunHdf5IndicesDataset = neededDnaRunIdxs;

                                cursors.columnOffset = nextDnaRunOffset = numOfDnaRunsNeeded;

                            } else {
                                cursors.dnarunHdf5IndexMap
                                    .get(datasetId)
                                    .subList(0, cursors.dnaRunOffset)
                                    .addAll(dnaRunHdf5IndicesDataset);

                                dnaRunHdf5IndicesDataset =
                                    cursors.dnarunHdf5IndexMap.get(datasetId);

                                cursors.columnOffset = nextDnaRunOffset = cursors.dnaRunOffset;

                            }
                        }


                    } else {

                        numOfMarkersReq = (int) Math.ceil(
                            (double) remainingPageSize / dnaRunHdf5IndicesDataset.size());

                        if(cursors.dnaRunOffset == totalDnaRuns) {
                            cursors.dnaRunOffset = 0;
                        }

                        nextDnaRunOffset = dnaRunHdf5IndicesDataset.size() -
                            ((numOfMarkersReq * dnaRunHdf5IndicesDataset.size()) -
                                remainingPageSize);

                        cursors.columnOffset = cursors.dnaRunOffset;
                    }

                    dnaRunExtractIndex.put(datasetId, dnaRunHdf5IndicesDataset);

                    Map<String, List<String>> markerExtractIndex = new HashMap<>();

                    List<String> markersHdf5IndicesDataset =
                        cursors.markerHdf5IndexMap.get(datasetId);

                    if (cursors.pageOffset > markersHdf5IndicesDataset.size()) {
                        cursors.pageOffset = 0;
                        cursors.dnaRunOffset = 0;
                        continue;
                    }

                    int markerLimit = cursors.pageOffset + numOfMarkersReq;

                    if (markerLimit > markersHdf5IndicesDataset.size()) {
                        markerLimit = markersHdf5IndicesDataset.size();
                    }

                    if (cursors.pageOffset == markerLimit) {
                        cursors.pageOffset -= 1;
                    }

                    markerExtractIndex
                        .put(datasetId,
                            markersHdf5IndicesDataset.subList(cursors.pageOffset, markerLimit));

                    Hdf5InterfaceResultDTO extractResult =
                        this.extractGenotypes(markerExtractIndex, dnaRunExtractIndex);

                    this.readGenotypesFromFile(
                        genotypeCalls, extractResult.getGenotypeFile(),
                        remainingPageSize, Integer.parseInt(datasetId),
                        cursors.columnOffset,
                        cursors.markersByDatasetId
                            .get(datasetId)
                            .subList(cursors.pageOffset, markerLimit),
                        cursors.dnarunsByDatasetId.get(datasetId),
                        new ArrayList<>(dnarunHdf5OrderMap.get(datasetId).values()));

                    FileUtils.deleteDirectory(new File(extractResult.getOutputFolder()));

                    if (genotypeCalls.size() >= pageSize) {

                        int nextPageOffset;

                        if (nextDnaRunOffset > 0 && nextDnaRunOffset < totalDnaRuns) {
                            nextPageOffset = (cursors.pageOffset + numOfMarkersReq - 1);
                        } else {
                            nextPageOffset = cursors.pageOffset + numOfMarkersReq;
                        }

                        //set next page offset and column offset
                        // as page token parts
                        nextPageCursorMap.put("pageOffset", nextPageOffset);
                        nextPageCursorMap.put("dnaRunOffset", nextDnaRunOffset);
                        nextPageCursorMap.put("datasetIdCursor", datasetIdIdx);
                        break;
                    } else {
                        remainingPageSize = pageSize - genotypeCalls.size();
                        cursors.pageOffset = 0;
                        cursors.dnaRunOffset = 0;
                    }
                }

                if(genotypeCalls.size() < pageSize) {

                    cursors.datasetIdCursor = cursors.pageOffset = cursors.dnaRunOffset = 0;

                    if(markers.size() < markerBinSize) {
                        cursors.markerBinCursor = 0;
                        if(dnaRuns.size() < dnaRunBinSize) {
                            nextPageCursorMap = null;
                            break;
                        }
                        else {
                            cursors.dnaRunBinCursor = dnaRuns.get(dnaRuns.size() - 1).getDnaRunId();
                        }
                    }
                    else {
                       cursors.markerBinCursor = markers.get(markers.size() - 1).getMarkerId();
                    }
                }
                else {
                    nextPageCursorMap.put("markerBinCursor", cursors.markerBinCursor);
                    nextPageCursorMap.put("dnaRunBinCursor", cursors.dnaRunBinCursor);
                    break;
                }

            }

            if(nextPageCursorMap != null) {
                String nextPageToken = PageToken.encode(nextPageCursorMap);
                returnVal.setNextPageToken(nextPageToken);
            }
            returnVal.setCurrentPageSize(genotypeCalls.size());

            returnVal.setResult(getGenotypeCallsResult(genotypeCalls));

        }
        catch (IOException e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

        return returnVal;
    }

    @Override
    public String getGenotypeCallsAsString(Integer datasetId,
                                           Integer pageNum) throws GobiiException {

        String returnVal = "";

        Objects.requireNonNull(pageNum, "pageNum : Non Null parameter");
        Objects.requireNonNull(datasetId, "markerRowOffset : Non Null parameter");

        //TODO: Add properties in gobii-web.xml to configure maximum page sizes
        int pageSize = 10000;
        int dnaRunRowOffset = 0;

        Integer markerRowOffset = pageNum*pageSize;

        List<String> headerValues = new ArrayList<>();
        Map<String, List<String>> markerHdf5IndexMap= new HashMap<>();
        Map<String, List<String>> dnarunHdf5IndexMap = new HashMap<>();

        try {

            List<Marker> markers = markerDao.getMarkersByDatasetId(
                datasetId,
                pageSize,
                markerRowOffset);

            if(markers.size() == 0) {
                return returnVal;
            }

            //Add header for first page
            if(pageNum == 0) {
                headerValues.add("MarkerName");
            }

            List<DnaRun> dnaRuns = dnaRunDao.getDnaRunsByDatasetId(datasetId, pageSize,
                    dnaRunRowOffset, false);

            while(dnaRuns.size() >= pageSize) {
                dnaRunRowOffset += pageSize;
                dnaRuns.addAll(dnaRunDao.getDnaRunsByDatasetId(datasetId, pageSize,
                        dnaRunRowOffset, false));
            }

            for(Marker marker : markers) {
                if(!markerHdf5IndexMap.containsKey(datasetId.toString())) {
                    markerHdf5IndexMap.put(datasetId.toString(), new ArrayList<>());
                }
                markerHdf5IndexMap.get(datasetId.toString()).add(
                        marker.getDatasetMarkerIdx().get(datasetId.toString()).textValue());
            }

            for(DnaRun dnaRun : dnaRuns) {
                if(!dnarunHdf5IndexMap.containsKey(datasetId.toString())) {
                    dnarunHdf5IndexMap.put(datasetId.toString(), new ArrayList<>());
                }
                dnarunHdf5IndexMap.get(datasetId.toString()).add(
                        dnaRun.getDatasetDnaRunIdx().get(datasetId.toString()).textValue());
                if(pageNum == 0) {
                    headerValues.add(dnaRun.getDnaRunName());
                }
            }

            //Append header for first page
            String headerString = null;
            if(pageNum == 0 && headerValues.size() > 0) {
                headerString = String.join(",", headerValues);
            }

            Hdf5InterfaceResultDTO extractResult = this.extractGenotypes(markerHdf5IndexMap,
                dnarunHdf5IndexMap);

            returnVal = this.readGenotypesFromFile(
                extractResult.getGenotypeFile(), markers,
                dnaRuns, headerString);

            FileUtils.deleteDirectory(new File(extractResult.getOutputFolder()));

            return returnVal;
        }
        catch (NullPointerException | IOException e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

    }

    /**
     * Extracts genotypes from the hdf5.
     *
     */
    private Hdf5InterfaceResultDTO extractGenotypes(Map<String, List<String>> markerHdf5IndexMap,
                                                    Map<String, List<String>> sampleHdf5IndexMap
    ) throws GobiiException {

        String tempFolder = UUID.randomUUID().toString();

        try {
            return hdf5Interface.getHDF5Genotypes(
                true,
                markerHdf5IndexMap,
                sampleHdf5IndexMap,
                tempFolder);
        }
        catch (FileNotFoundException fE) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Genotypes Extraction failed. System Error.");
        }
    }


    private void readGenotypesFromFile (
        List<GenotypeCallsDTO> returnVal,
        String extractListPath,
        int indexOffset
    ) {
        try {
            File genotypCallsFile = new File(extractListPath);
            FileInputStream fstream = new FileInputStream(genotypCallsFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            int i = indexOffset;

            int chrEach;

            StringBuilder genotype = new StringBuilder();

            while ((chrEach = br.read()) != -1) {
                char genotypesChar = (char) chrEach;
                if (genotypesChar == '\t' || genotypesChar == '\n') {
                    returnVal.get(i).setGenotype(new HashMap<>());
                    String[] genotypeValues = new String[]{genotype.toString()};
                    returnVal.get(i).getGenotype().put("values", genotypeValues);
                    i++;
                    genotype.setLength(0);
                } else {
                    genotype.append(genotypesChar);
                }
            }
            br.close();
            fstream.close();
        }
        catch (IOException e) {
            log.error( "Gobii Extraction service failed to read from result file",e);
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Genotypes Extraction failed. System Error.");
        }


    }



    private Integer readGenotypesFromFile (List<GenotypeCallsDTO> returnVal,
                                           String genotypeMatrixFilePath,
                                           Integer pageSize,
                                           Integer datasetId,
                                           Integer columnOffset,
                                           List<Marker> markers,
                                           List<DnaRun> dnaruns,
                                           List<Integer> dnarunOrder) throws GobiiException {


        try {
            File genotypCallsFile = new File(genotypeMatrixFilePath);
            FileInputStream fstream = new FileInputStream(genotypCallsFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            int i = 0; // row index
            int j = 0; // column index
            int k = 0; // genotypes count
            int chrEach;

            StringBuilder genotype = new StringBuilder();

            while ((chrEach = br.read()) != -1 && k < pageSize) {

                char genotypesChar = (char) chrEach;

                if (genotypesChar == '\t' || genotypesChar == '\n') {

                    if (j < columnOffset) {
                        j += 1;
                        genotype.setLength(0);
                        continue;
                    }

                    columnOffset = 0;

                    GenotypeCallsDTO genotypeCall = new GenotypeCallsDTO();

                    genotypeCall.setCallSetDbId(dnaruns.get(dnarunOrder.get(j)).getDnaRunId());
                    genotypeCall.setCallSetName(dnaruns.get(dnarunOrder.get(j)).getDnaRunName());
                    genotypeCall.setVariantDbId(markers.get(i).getMarkerId());
                    genotypeCall.setVariantName(markers.get(i).getMarkerName());
                    genotypeCall.setVariantSetDbId(datasetId);
                    genotypeCall.setSepUnphased(unphasedSep);
                    genotypeCall.setUnknownString(unknownChar);

                    genotypeCall.setGenotype(new HashMap<>());
                    String[] genotypeValues = new String[]{genotype.toString()};
                    genotypeCall.getGenotype().put("values", genotypeValues);

                    returnVal.add(genotypeCall);


                    if (genotypesChar == '\t') {
                        j++;
                    } else {
                        i++;
                        j = 0;
                    }

                    k++;

                    genotype.setLength(0);

                } else {
                    genotype.append(genotypesChar);
                    if (genotype.length() == 2) {
                        genotype.insert(1, unphasedSep);
                    }
                }

            }

            br.close();
            fstream.close();

            return j;
        }
        catch (IOException e) {
            log.error( "Gobii Extraction service failed to read from result file", e);
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Genotypes Extraction failed. System Error.");
        }
    }

    private String readGenotypesFromFile(String genotypeMatrixFilePath,
                                         List<Marker> markerMetadataList,
                                         List<DnaRun> dnarunMetadataList,
                                         String header)  throws GobiiException {

        File genotypCallsFile = new File(genotypeMatrixFilePath);
        FileInputStream fstream;

        try {
            fstream = new FileInputStream(genotypCallsFile);

            int i = 0;

            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            int chrEach;

            StringBuilder genotypes = new StringBuilder();

            if(header != null && !header.isEmpty()) {
                genotypes.append(header);
                genotypes.append('\n');
            }

            genotypes.append(markerMetadataList.get(i).getMarkerName());
            genotypes.append(',');
            StringBuilder genotype = new StringBuilder();

            while ((chrEach = br.read()) != -1) {
                char genotypesChar = (char) chrEach;
                if(genotypesChar == '\t') {
                    genotypes.append(genotype);
                    genotypes.append(',');
                    genotype.setLength(0);
                }
                else if(genotypesChar == '\n') {
                    i++;
                    genotypes.append(genotype);
                    genotypes.append('\n');
                    genotype.setLength(0);
                    if(i < markerMetadataList.size()) {
                        genotypes.append(markerMetadataList.get(i).getMarkerName());
                        genotypes.append(',');
                    }
                }
                else {
                    genotype.append(genotypesChar);
                    if(genotype.length() == 2) {
                        genotype.insert(1, unphasedSep);
                    }
                }
            }

            br.close();
            fstream.close();

            return genotypes.toString();
        }
        catch (IOException e) {
            log.error( "Gobii Extraction service failed to read from result file", e);
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Genotypes Extraction failed. System Error.");
        }

    }

    private GenotypeCallsResult getGenotypeCallsResult(List<GenotypeCallsDTO> genotypeCalls) {
        return new GenotypeCallsResult(unphasedSep, null, unknownChar, genotypeCalls);
    }
}

