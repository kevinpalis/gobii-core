package org.gobiiproject.gobidomain.services.brapi;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.brapi.CallSetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.utils.JsonNodeUtils;
import org.gobiiproject.gobiisampletrackingdao.CvDaoImpl;
import org.gobiiproject.gobiisampletrackingdao.DnaRunDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by VCalaminos on 7/18/2019.
 */
@WebAppConfiguration
public class CallSetServiceImplTest {

    final int testPageSize = 10;

    @InjectMocks
    private CallSetServiceImpl callSetBrapiService;

    @Mock
    private DnaRunDaoImpl dnaRunDao;

    @Mock
    private CvDaoImpl cvDao;

    Random random = new Random();

    MockSetup mockSetup;

    final Integer pageSize = 10;
    final Integer pageNum = 0;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockSetup  = new MockSetup();
    }

    @Test
    public void getCallsets() throws Exception {

        mockSetup.createMockDnaRuns(pageSize);

        when (
            dnaRunDao.getDnaRuns(any(Integer.TYPE), any(Integer.TYPE),
                any(Integer.TYPE), any(String.class),
                any(Integer.TYPE), any(Integer.TYPE),
                any(Integer.TYPE), any(String.class),
                any(Integer.TYPE), any(String.class))
        ).thenReturn(mockSetup.mockDnaRuns);


        when (cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_DNASAMPLE_PROP.getCvGroupName(), null)).thenReturn(
                new ArrayList<>()
        );

        when (cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_GERMPLASM_PROP.getCvGroupName(), null))
            .thenReturn(mockSetup.mockGermplasmProps);


        PagedResult<CallSetDTO>  callSetsPageResult =
            callSetBrapiService.getCallSets(
                pageSize, 0, null, new CallSetDTO());

        assertEquals("Page Size mismatch",
            pageSize,
            callSetsPageResult.getCurrentPageSize());

        assertEquals("Wrong page number",
            pageNum,
            callSetsPageResult.getCurrentPageNum());

        for(int i = 0; i < testPageSize; i++) {

            testMainFieldMapping(callSetsPageResult.getResult().get(i),
                mockSetup.mockDnaRuns.get(i));

            if(!MapUtils.isEmpty(
                callSetsPageResult
                .getResult().get(i).getAdditionalInfo())) {

                assertTrue("AdditionalInfo : DnaSample.Properties " +
                        "mapping failed",
                    !(
                        JsonNodeUtils.isEmpty(
                            mockSetup.mockDnaRuns.get(i)
                            .getDnaSample().getProperties()) ||
                        JsonNodeUtils.isEmpty(
                            mockSetup.mockDnaRuns.get(i)
                                .getDnaSample().getGermplasm().getProperties())
                    ));

                for(String infoKey :
                    callSetsPageResult.getResult()
                        .get(i).getAdditionalInfo().keySet()) {


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

                    if(cvDnaSampleProps.isPresent()) {
                        assertTrue("AdditionalInfo : DnaSample.Properties " +
                                "mapping failed",
                            mockSetup.mockDnaRuns
                                .get(i).getDnaSample()
                                .getProperties().get(
                                cvDnaSampleProps.get().getCvId().toString())
                                .asText()
                                == callSetsPageResult.getResult()
                                .get(i).getAdditionalInfo().get(infoKey)
                        );
                    }
                    else if(cvGermplasmProps.isPresent()) {
                        assertTrue("AdditionalInfo : Germplasm.Properties " +
                                "mapping failed",
                            mockSetup.mockDnaRuns
                                .get(i).getDnaSample().getGermplasm()
                                .getProperties().get(
                                cvGermplasmProps.get().getCvId().toString())
                                .asText()
                                == callSetsPageResult.getResult()
                                .get(i).getAdditionalInfo().get(infoKey)
                        );
                    }
                    else {
                        fail("AdditionalInfo : Germplasm.Properties " +
                            "mapping failed");
                    }

                }
            }
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
            callSet.getVariantSetIds())) {

            assertTrue("VariantSetId : DatasetIds mapping failed",
                !JsonNodeUtils.isEmpty(
                    dnaRun.getDatasetDnaRunIdx()));

            assertEquals("VariantSetId : DatasetIds mapping failed",
                dnaRun.getDatasetDnaRunIdx().size(),
                callSet
                    .getVariantSetIds().size()
            );

            for (Integer variantSetId :
                callSet.getVariantSetIds()) {

                assertTrue("VariantSetId : DatasetIds mapping failed",
                    dnaRun.getDatasetDnaRunIdx()
                        .has(variantSetId.toString()));

            }
        }

    }

    private void testAdditionalInfoFieldMapping() {

    }

}
