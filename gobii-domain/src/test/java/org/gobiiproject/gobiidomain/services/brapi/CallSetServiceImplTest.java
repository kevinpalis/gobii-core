package org.gobiiproject.gobiidomain.services.brapi;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.gobiiproject.gobiidomain.PageToken;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.brapi.CallSetDTO;
import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.utils.JsonNodeUtils;
import org.gobiiproject.gobiisampletrackingdao.CvDaoImpl;
import org.gobiiproject.gobiisampletrackingdao.DnaRunDaoImpl;
import org.gobiiproject.gobiisampletrackingdao.MarkerDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebAppConfiguration
public class CallSetServiceImplTest {

    @InjectMocks
    private CallSetServiceImpl callSetBrapiService;

    @Mock
    private DnaRunDaoImpl dnaRunDao;

    @Mock
    private MarkerDaoImpl markerDao;

    @Mock
    private CvDaoImpl cvDao;

    MockSetup mockSetup;

    Integer pageSize = 10;
    Integer pageNum = 0;

    Random random = new Random();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockSetup  = new MockSetup();
    }

    @Test
    public void getCallsetsTest() throws Exception {

        Integer testPageSize = pageSize - 2;

        mockSetup.createMockDnaRuns(pageSize);

        when (
            dnaRunDao.getDnaRuns(pageSize, pageNum,
                null, null, null, null, null, null, null, null)
        ).thenReturn(mockSetup.mockDnaRuns.subList(0, testPageSize));


        when (cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_DNASAMPLE_PROP.getCvGroupName(), null)
        ).thenReturn(mockSetup.mockDnaSampleProps);

        when (cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_GERMPLASM_PROP.getCvGroupName(), null))
            .thenReturn(mockSetup.mockGermplasmProps);


        PagedResult<CallSetDTO>  callSetsPageResult =
            callSetBrapiService.getCallSets(
                pageSize, 0, null, new CallSetDTO());

        assertEquals("Page Size mismatch",
            testPageSize,
            callSetsPageResult.getCurrentPageSize());

        assertEquals("Wrong page number",
            pageNum,
            callSetsPageResult.getCurrentPageNum());

        for(int i = 0; i < testPageSize; i++) {

            testMainFieldMapping(callSetsPageResult.getResult().get(i),
                mockSetup.mockDnaRuns.get(i));

            if(!MapUtils.isEmpty(
                callSetsPageResult.getResult().get(i).getAdditionalInfo())) {
                testAdditionalInfoFieldMapping(
                    callSetsPageResult.getResult().get(i).getAdditionalInfo(),
                    mockSetup.mockDnaRuns.get(i));
            }
        }
    }

    @Test
    public void getCallSetsByGenotypesSearchQueryTest() {

        mockSetup.createMockDnaRuns(pageSize);
        mockSetup.createMockMarkers(pageSize);

        GenotypeCallsSearchQueryDTO genotypeCallsSearchQueryDTO = new GenotypeCallsSearchQueryDTO();

        List<DnaRun> testDnaRunsSubList = mockSetup.mockDnaRuns
            .subList(0, random.nextInt(pageSize - 4) + 3);

        List<Marker> testMarkerSubList = mockSetup.mockMarkers
            .subList(0, random.nextInt(pageSize - 4) + 3);

        Set<String> testCallSetNames = testDnaRunsSubList
            .stream()
            .map(DnaRun::getDnaRunName)
            .collect(Collectors.toSet());

        Set<String> testMarkerNames = testMarkerSubList
            .stream()
            .map(Marker::getMarkerName)
            .collect(Collectors.toSet());

        genotypeCallsSearchQueryDTO.setCallSetNames(testCallSetNames);
        genotypeCallsSearchQueryDTO.setVariantNames(testMarkerNames);

        Set<String> testDnaRunDatasetIds = new HashSet<>();
        Set<String> testMarkerDatasetIds = new HashSet<>();

        testDnaRunsSubList.forEach(dnaRun -> {
            try {
                testDnaRunDatasetIds
                    .addAll(JsonNodeUtils.getKeysFromJsonObject(dnaRun.getDatasetDnaRunIdx()));
            }
            catch (Exception e) {
                fail("Failed to extract dataset ids");
            }
        });

        testMarkerSubList.forEach(marker -> {
            try {
                testMarkerDatasetIds
                    .addAll(JsonNodeUtils.getKeysFromJsonObject(marker.getDatasetMarkerIdx()));
            }
            catch (Exception e) {
                fail("Failed to extract dataset ids");
            }
        });

        List<DnaRun> expectedDnaRuns = testDnaRunsSubList.stream().filter(dnaRun -> {
            boolean  isExpected = false;
            try {
                for(String datasetId :
                    JsonNodeUtils.getKeysFromJsonObject(dnaRun.getDatasetDnaRunIdx())) {
                    if (testMarkerDatasetIds.contains(datasetId)) {
                        isExpected = true;
                    }
                }
            }
            catch (Exception e) {
                    fail("Failed to extract dataset ids");
            }
            return isExpected;
        }).collect(Collectors.toList());

        Map<String, Integer> pageCursorMap = new HashMap<>();
        pageCursorMap.put("markerBinCursor", 0);
        pageCursorMap.put("dnaRunIdCursor", 0);
        String pageToken = PageToken.encode(pageCursorMap);

        String nextPageToken = null;

        if(expectedDnaRuns.size() > 0) {
            pageCursorMap.put("markerBinCursor", 0);
            pageCursorMap.put("dnaRunIdCursor",
                expectedDnaRuns.get(expectedDnaRuns.size() - 1).getDnaRunId());
            nextPageToken = PageToken.encode(pageCursorMap);
        }


        when(markerDao.getMarkers(genotypeCallsSearchQueryDTO.getVariantDbIds(),
            genotypeCallsSearchQueryDTO.getVariantNames(),
            genotypeCallsSearchQueryDTO.getVariantSetDbIds(), 1000, 0))
            .thenReturn(testMarkerSubList);

        when (
            dnaRunDao.getDnaRuns(
               genotypeCallsSearchQueryDTO.getCallSetDbIds(),
                genotypeCallsSearchQueryDTO.getCallSetNames(),
                genotypeCallsSearchQueryDTO.getSampleDbIds(),
                genotypeCallsSearchQueryDTO.getSampleNames(),
                genotypeCallsSearchQueryDTO.getSamplePUIs(),
                genotypeCallsSearchQueryDTO.getGermplasmPUIs(),
                genotypeCallsSearchQueryDTO.getGermplasmDbIds(),
                genotypeCallsSearchQueryDTO.getGermplasmNames(),
               testMarkerDatasetIds, null, expectedDnaRuns.size(), 0, null, true)
        ).thenReturn(expectedDnaRuns);


        when (cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_DNASAMPLE_PROP.getCvGroupName(), null)
        ).thenReturn(mockSetup.mockDnaSampleProps);

        when (cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_GERMPLASM_PROP.getCvGroupName(), null))
            .thenReturn(mockSetup.mockGermplasmProps);

        PagedResult<CallSetDTO> callSetsPagedResult =
            callSetBrapiService
                .getCallSetsByGenotypesExtractQuery(
                    genotypeCallsSearchQueryDTO, expectedDnaRuns.size(), pageToken);

        assertEquals("Page Size mismatch",
            (Integer) expectedDnaRuns.size(),
            callSetsPagedResult.getCurrentPageSize());

        assertEquals("Page Token mismatch",
            nextPageToken,
            callSetsPagedResult.getNextPageToken());

        for(int i = 0; i < expectedDnaRuns.size(); i++) {

            testMainFieldMapping(callSetsPagedResult.getResult().get(i),
                expectedDnaRuns.get(i));

            if(!MapUtils.isEmpty(
                callSetsPagedResult.getResult().get(i).getAdditionalInfo())) {
                testAdditionalInfoFieldMapping(
                    callSetsPagedResult.getResult().get(i).getAdditionalInfo(),
                    expectedDnaRuns.get(i));
            }
        }

    }

    @Test
    public void getCallsetByIdTest()  {

        mockSetup.createMockDnaRuns(pageSize);

        when (
            dnaRunDao.getDnaRunById(any(Integer.TYPE))
        ).thenReturn(mockSetup.mockDnaRuns.get(0));


        when (cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_DNASAMPLE_PROP.getCvGroupName(), null)
        ).thenReturn(mockSetup.mockDnaSampleProps);

        when (cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_GERMPLASM_PROP.getCvGroupName(), null))
            .thenReturn(mockSetup.mockGermplasmProps);

        CallSetDTO  callSet = callSetBrapiService.getCallSetById(any(Integer.TYPE));

        testMainFieldMapping(callSet, mockSetup.mockDnaRuns.get(0));
            if(!MapUtils.isEmpty(callSet.getAdditionalInfo())) {
                testAdditionalInfoFieldMapping(
                    callSet.getAdditionalInfo(),
                    mockSetup.mockDnaRuns.get(0)
                );
            }
    }

    private void testMainFieldMapping(CallSetDTO callSet, DnaRun dnaRun) {
        assertEquals("CallSetId : DnaRunId mapping failed",
            dnaRun.getDnaRunId(),
            callSet.getCallSetDbId());
        assertEquals("CallSetName : DnaRunName mapping failed",
            dnaRun.getDnaRunName(),
            callSet.getCallSetName());

        assertEquals("StudyDbId : ExperimentId mapping failed",
            dnaRun.getExperiment().getExperimentId(),
            callSet.getStudyDbId());

        assertEquals("SampleDbId : DnaSampleId mapping failed",
            dnaRun.getDnaSample().getDnaSampleId(),
            callSet.getSampleDbId());

        assertEquals("SampleName : DnaSampleName mapping failed",
            dnaRun.getDnaSample().getDnaSampleName(),
            callSet.getSampleName());

        if(!CollectionUtils.isEmpty(
            callSet.getVariantSetDbIds())) {

            assertTrue("VariantSetId : DatasetIds mapping failed",
                !JsonNodeUtils.isEmpty(
                    dnaRun.getDatasetDnaRunIdx()));

            assertEquals("VariantSetId : DatasetIds mapping failed",
                dnaRun.getDatasetDnaRunIdx().size(),
                callSet
                    .getVariantSetDbIds().size()
            );

            for (String variantSetId :
                callSet.getVariantSetDbIds()) {

                assertTrue("VariantSetId : DatasetIds mapping failed",
                    dnaRun.getDatasetDnaRunIdx()
                        .has(variantSetId));

            }
        }
    }

    private void testAdditionalInfoFieldMapping(
        Map<String, String> additionalInfo, DnaRun dnaRun
    ) {

        assertTrue("AdditionalInfo : DnaSample.Properties " +
                "mapping failed",
            !(
                JsonNodeUtils.isEmpty(
                    dnaRun.getDnaSample().getProperties()) ||
                    JsonNodeUtils.isEmpty(
                        dnaRun.getDnaSample().getGermplasm().getProperties())
            ));

        for(String infoKey :
            additionalInfo.keySet()) {

            Optional<Cv> cvDnaSampleProps = mockSetup.mockDnaSampleProps
                .stream()
                .parallel()
                .filter(cv -> cv.getTerm() == infoKey)
                .findFirst();

            Optional<Cv> cvGermplasmProps = mockSetup.mockGermplasmProps
                .stream()
                .parallel()
                .filter(cv -> cv.getTerm() == infoKey)
                .findFirst();

            if(cvDnaSampleProps.isPresent() && dnaRun.getDnaSample().getProperties().size() > 0) {
                assertTrue("AdditionalInfo : DnaSample.Properties " +
                        "mapping failed",
                    dnaRun.getDnaSample()
                        .getProperties().get(
                        cvDnaSampleProps.get().getCvId().toString())
                        .asText() ==
                        additionalInfo.get(infoKey)
                );
            }
            else if(cvGermplasmProps.isPresent() &&
                dnaRun.getDnaSample().getGermplasm().getProperties().size() > 0
            ) {
                assertTrue("AdditionalInfo : Germplasm.Properties " +
                        "mapping failed",
                    dnaRun.getDnaSample().getGermplasm()
                        .getProperties().get(
                        cvGermplasmProps.get().getCvId().toString())
                        .asText()
                        ==
                        additionalInfo.get(infoKey)
                );
            }
            else {
                fail("AdditionalInfo : Properties " +
                    "mapping failed");
            }

        }

    }

}
