package org.gobiiproject.gobiidomain.services.brapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;

import org.gobiiproject.gobiimodel.dto.brapi.VariantSetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiisampletrackingdao.CvDaoImpl;
import org.gobiiproject.gobiisampletrackingdao.DatasetDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class VariantSetsServiceImplTest {

    @InjectMocks
    private VariantSetsServiceImpl variansetService;

    @Mock
    private DatasetDaoImpl datasetDao;

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
    public void testMainFieldsMapping() {


        List<Object[]> tuples = mockSetup.createMockDatasetAnalysisTuples(pageSize);

        when (
            datasetDao.getDatasetsWithAnalysesAndCounts(
                pageSize, pageNum,
                null, null,
                null, null)
        ).thenReturn(tuples);

        PagedResult<VariantSetDTO> pagedResult = variansetService.getVariantSets(
            pageSize, pageNum, null, null, null, null);


        List<VariantSetDTO> variantSets = pagedResult.getResult();


        assertEquals("Size mismatch", tuples.size(), variantSets.size());

        for(int i = 0; i < 10; i++) {

            assertEquals("variansetName check failed",
                ((Dataset)tuples.get(i)[0]).getDatasetName(),
                    variantSets.get(i).getVariantSetName());

            assertEquals("variansetid check failed",
                    ((Dataset)tuples.get(i)[0]).getDatasetId(),
                    variantSets.get(i).getVariantSetDbId());

            assertEquals("studyDbId check failed",
                    ((Dataset)tuples.get(i)[0]).getExperiment().getExperimentId(),
                    variantSets.get(i).getStudyDbId());

            assertEquals("referenceDbId check failed",
                    ((Dataset)tuples.get(i)[0]).getCallingAnalysis().getReference().getReferenceId(),
                    variantSets.get(i).getReferenceSetDbId());

            assertEquals("created check failed",
                    ((Dataset)tuples.get(i)[0]).getCreatedDate(),
                    variantSets.get(i).getCreated());

            assertEquals("updated check failed",
                    ((Dataset)tuples.get(i)[0]).getModifiedDate(),
                    variantSets.get(i).getUpdated());


            //Only one analysis added in mock, so, just check the mapping for that one is correct
            assertTrue("Analysis got mapped to the Variantset Analysis",
                    variantSets.get(i).getAnalyses().iterator().hasNext());




        }

    }



}
