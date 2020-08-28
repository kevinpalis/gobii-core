package org.gobiiproject.gobidomain.services.brapi;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.gobiiproject.gobidomain.GobiiDomainException;
import org.gobiiproject.gobidomain.PageToken;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.brapi.CallSetDTO;
import org.gobiiproject.gobiimodel.dto.brapi.CallSetsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.system.GenotypesRunTimeCursors;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.modelmapper.CvMapper;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.JsonNodeUtils;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.DnaRunDao;
import org.gobiiproject.gobiisampletrackingdao.MarkerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

@Transactional
public class CallSetServiceImpl implements CallSetService {

    Logger LOGGER = LoggerFactory.getLogger(CallSetServiceImpl.class);

    @Autowired
    private DnaRunDao dnaRunDao;

    @Autowired
    private MarkerDao markerDao = null;

    @Autowired
    private CvDao cvDao;

    public PagedResult<CallSetDTO> getCallSets(Integer pageSize, Integer pageNum,
                                               Integer variantSetDbId, CallSetDTO callSetsFilter)
        throws GobiiException {

        PagedResult<CallSetDTO> pagedResult = new PagedResult<>();

        List<CallSetDTO> callSets = new ArrayList<>();

        try {

            Objects.requireNonNull(pageSize, "pageSize : Required non null");
            Objects.requireNonNull(pageNum, "pageNum : Required non null");
            Objects.requireNonNull(callSetsFilter, "callSetsFilter : Required non null");

            Integer rowOffset = pageNum * pageSize;

            DnaRun dnaRunFilter = new DnaRun();
            ModelMapper.mapDtoToEntity(callSetsFilter, dnaRunFilter);

            List<DnaRun> dnaRuns = dnaRunDao.getDnaRuns(
                pageSize, rowOffset,
                callSetsFilter.getCallSetDbId(), callSetsFilter.getCallSetName(),
                variantSetDbId, callSetsFilter.getStudyDbId(),
                callSetsFilter.getSampleDbId(), callSetsFilter.getSampleName(),
                callSetsFilter.getGermplasmDbId(), callSetsFilter.getGermplasmName());

            callSets = mapDnaRunsToCallSetDtos(dnaRuns);

            pagedResult.setResult(callSets);
            pagedResult.setCurrentPageSize(callSets.size());
            pagedResult.setCurrentPageNum(pageNum);

            return pagedResult;
        }
        catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            throw new GobiiException(
                GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN, e.getMessage());
        }
    }


    public CallSetDTO getCallSetById(Integer callSetDbId) throws GobiiException {


        try {

            Objects.requireNonNull(callSetDbId);

            DnaRun dnaRun = dnaRunDao.getDnaRunById(callSetDbId);

            List<Cv> dnaSampleGroupCvs =
                cvDao.getCvListByCvGroup(CvGroupTerm.CVGROUP_DNASAMPLE_PROP.getCvGroupName(), null);

            List<Cv> germplasmGroupCvs =
                cvDao.getCvListByCvGroup(CvGroupTerm.CVGROUP_GERMPLASM_PROP.getCvGroupName(), null);

            CallSetDTO callSet = this.mapDnaRunEntityToCallSetDto(
                dnaRun, dnaSampleGroupCvs, germplasmGroupCvs);

            return callSet;
        }
        catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            throw new GobiiException(
                GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN, e.getMessage());

        }
    }

    @Override
    public PagedResult<CallSetDTO> getCallSetsBySearchQuery(
        CallSetsSearchQueryDTO callSetsSearchQuery, Integer pageSize, Integer pageNum) {

        PagedResult<CallSetDTO> returnVal = new PagedResult<>();

        List<CallSetDTO> callSets;

        Integer rowOffset = 0;

        if(pageNum != null && pageSize != null) {
            rowOffset = pageNum*pageSize;
        }

        try {

            Objects.requireNonNull(pageNum, "pageNum : Required non null");
            Objects.requireNonNull(pageSize, "pageSize : Required non null");

            List<DnaRun> dnaRuns = dnaRunDao.getDnaRuns(
                callSetsSearchQuery.getCallSetDbIds(), callSetsSearchQuery.getCallSetNames(),
                callSetsSearchQuery.getSampleDbIds(), callSetsSearchQuery.getSampleNames(),
                callSetsSearchQuery.getSamplePUIs(), callSetsSearchQuery.getGermplasmPUIs(),
                callSetsSearchQuery.getGermplasmDbIds(), callSetsSearchQuery.getGermplasmNames(),
                callSetsSearchQuery.getVariantSetDbIds(), pageSize,
                null, rowOffset, true);

            callSets = mapDnaRunsToCallSetDtos(dnaRuns);

            returnVal.setResult(callSets);
            returnVal.setCurrentPageSize(callSets.size());
            returnVal.setCurrentPageNum(pageNum);
            return returnVal;
        }
        catch (GobiiException gE) {

            LOGGER.error(gE.getMessage(), gE.getMessage());

            throw new GobiiDomainException(
                gE.getGobiiStatusLevel(), gE.getGobiiValidationStatusType(), gE.getMessage()
            );
        }
        catch (Exception e) {
            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }


    @Override
    public PagedResult<CallSetDTO>
    getCallSetsByGenotypesExtractQuery(GenotypeCallsSearchQueryDTO genotypesSearchQuery,
                                       Integer pageSize, String pageToken) {

        final int markerBinSize = 1000;

        PagedResult<CallSetDTO> returnVal = new PagedResult<>();

        List<CallSetDTO> callSets = new ArrayList<>();

        GenotypesRunTimeCursors cursors = new GenotypesRunTimeCursors();

        List<DnaRun> dnaRuns;

        List<Marker> markers = new ArrayList<>();

        Map<String, Integer> nextPageCursorMap = new HashMap<>();

        try {

            if(pageToken != null) {

                Map<String, Integer> pageTokenParts = PageToken.decode(pageToken);
                cursors.markerBinCursor = pageTokenParts.getOrDefault("markerBinCursor", 0);
                cursors.dnaRunIdCursor = pageTokenParts.getOrDefault("dnaRunIdCursor", 0);
            }

            int remainingPageSize = 0;

            while(callSets.size() < pageSize) {

                cursors.markerDatasetIds = new HashSet<>();
                cursors.dnaRunDatasetIds = new HashSet<>();

                remainingPageSize = pageSize - callSets.size();

                if (!genotypesSearchQuery.isVariantsQueriesEmpty() ||
                !CollectionUtils.isEmpty(genotypesSearchQuery.getVariantSetDbIds())) {

                    markers = markerDao.getMarkers(
                        genotypesSearchQuery.getVariantDbIds(),
                        genotypesSearchQuery.getVariantNames(),
                        genotypesSearchQuery.getVariantSetDbIds(),
                        markerBinSize, cursors.markerBinCursor);

                    if(markers.size() == 0) {
                        returnVal.setResult(callSets);
                        returnVal.setCurrentPageSize(callSets.size());
                        break;
                    }

                    for(Marker marker : markers) {
                        cursors.markerDatasetIds.addAll(JsonNodeUtils.getKeysFromJsonObject(
                            marker.getDatasetMarkerIdx()));
                    }

                }

                if(!CollectionUtils.isEmpty(genotypesSearchQuery.getVariantSetDbIds())) {
                    cursors.dnaRunDatasetIds
                        .addAll(new HashSet<>(genotypesSearchQuery.getVariantSetDbIds()));
                    cursors.dnaRunDatasetIds.retainAll(cursors.markerDatasetIds);
                }
                else {
                    cursors.dnaRunDatasetIds.addAll(cursors.markerDatasetIds);
                }

                dnaRuns = dnaRunDao.getDnaRuns(
                    genotypesSearchQuery.getCallSetDbIds(), genotypesSearchQuery.getCallSetNames(),
                    genotypesSearchQuery.getSampleDbIds(), genotypesSearchQuery.getSampleNames(),
                    genotypesSearchQuery.getSamplePUIs(), genotypesSearchQuery.getGermplasmPUIs(),
                    genotypesSearchQuery.getGermplasmDbIds(), genotypesSearchQuery.getGermplasmNames(),
                    cursors.dnaRunDatasetIds, remainingPageSize, cursors.dnaRunIdCursor, null, true);

                if(dnaRuns.size() > 0) {
                    cursors.dnaRunIdCursor = dnaRuns.get(dnaRuns.size() - 1).getDnaRunId();
                }

                callSets.addAll(mapDnaRunsToCallSetDtos(dnaRuns));

                if(callSets.size() < pageSize && markers.size() > 0) {
                    cursors.markerBinCursor = markers.get(markers.size() - 1).getMarkerId();
                    cursors.dnaRunIdCursor = 0;
                }
                else if(callSets.size() < pageSize && markers.size() == 0) {
                    break;
                }
                else if(callSets.size() == pageSize) {
                    nextPageCursorMap = new HashMap<>();
                    nextPageCursorMap.put("markerBinCursor", cursors.markerBinCursor);
                    nextPageCursorMap.put("dnaRunIdCursor", cursors.dnaRunIdCursor);
                    break;
                }
            }
            if(nextPageCursorMap.size() > 0) {
                String nextPageToken = PageToken.encode(nextPageCursorMap);
                returnVal.setNextPageToken(nextPageToken);
            }
            returnVal.setResult(callSets);
            returnVal.setCurrentPageSize(callSets.size());
            return returnVal;
        }
        catch (GobiiException gE) {

            LOGGER.error(gE.getMessage(), gE.getMessage());

            throw new GobiiDomainException(
                gE.getGobiiStatusLevel(), gE.getGobiiValidationStatusType(), gE.getMessage()
            );
        }
        catch (Exception e) {
            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }


    private List<CallSetDTO> mapDnaRunsToCallSetDtos(List<DnaRun> dnaRuns) {

        List<CallSetDTO> callSets = new ArrayList<>();

        List<Cv> dnaSampleGroupCvs = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_DNASAMPLE_PROP.getCvGroupName(), null);

        List<Cv> germplasmGroupCvs = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_GERMPLASM_PROP.getCvGroupName(), null);

        for (DnaRun dnaRun : dnaRuns) {
            CallSetDTO callSet =
                this.mapDnaRunEntityToCallSetDto(dnaRun, dnaSampleGroupCvs,
                    germplasmGroupCvs);
            callSets.add(callSet);
        }

        return callSets;
    }

    private CallSetDTO mapDnaRunEntityToCallSetDto(DnaRun dnaRun, List<Cv> dnaSampleGroupCvs,
                                                   List<Cv> germplasmGroupCvs) {

        CallSetDTO callSet = new CallSetDTO();

        ModelMapper.mapEntityToDto(dnaRun, callSet);

        Iterator<String> datasetIdsIter = dnaRun.getDatasetDnaRunIdx().fieldNames();

        while (datasetIdsIter.hasNext()) {
            callSet
                .getVariantSetDbIds()
                .add(datasetIdsIter.next());
        }

        Map<String, String> additionalInfo = new HashMap<>();

        if(dnaRun.getDnaSample() != null) {

            if (!JsonNodeUtils.isEmpty(dnaRun.getDnaSample().getProperties())) {
                additionalInfo = CvMapper.mapCvIdToCvTerms(dnaSampleGroupCvs,
                    dnaRun.getDnaSample().getProperties());
            }

            if (dnaRun.getDnaSample().getGermplasm() != null &&
                !JsonNodeUtils.isEmpty(dnaRun.getDnaSample().getGermplasm().getProperties())) {
                additionalInfo = CvMapper.mapCvIdToCvTerms(germplasmGroupCvs,
                    dnaRun.getDnaSample().getGermplasm().getProperties(), additionalInfo);
            }
        }

        callSet.setAdditionalInfo(additionalInfo);

        return callSet;
    }


}
