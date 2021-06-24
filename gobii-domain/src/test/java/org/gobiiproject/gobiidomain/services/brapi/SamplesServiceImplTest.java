package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiimodel.dto.brapi.SamplesDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiisampletrackingdao.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@WebAppConfiguration
public class SamplesServiceImplTest {

    @InjectMocks
    private SamplesServiceImpl samplesBrapiService;

    @Mock
    private DnaSampleDao dnaSampleDao;

    @Mock
    private CvDao cvDao;

    MockSetup mockSetup;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockSetup  = new MockSetup();
    }

    @Test
    public void testMainFieldsMapping() throws Exception {

        int pageSize = 10;

        mockSetup.createMockDnaSamples(pageSize);

        when (
                dnaSampleDao.getDnaSamples(pageSize, 0,
                        null, null,
                        null, null)
        ).thenReturn(mockSetup.mockDnaSamples);

        when (
            cvDao.getCvListByCvGroup(
                any(String.class), any(GobiiCvGroupType.class))
        ).thenReturn(mockSetup.mockDnaSampleProps);


        PagedResult<SamplesDTO> samplesBrapi = samplesBrapiService.getSamples(
            pageSize,0, null, null, null);

        assertEquals("Size mismatch", mockSetup.mockDnaSamples.size(),
            samplesBrapi.getResult().size());

        for(int i = 0; i < 10; i++) {

            assertEquals("germplasmDbId check failed",
                    mockSetup.mockDnaSamples.get(i).getGermplasm().getGermplasmId(),
                    samplesBrapi.getResult().get(i).getGermplasmDbId());

            assertEquals("sampleDbId check failed!",
                    mockSetup.mockDnaSamples.get(i).getDnaSampleId(),
                    samplesBrapi.getResult().get(i).getSampleDbId());

            assertEquals("observationUnitDbId check failed!",
                    mockSetup.mockDnaSamples.get(i).getGermplasm().getExternalCode(),
                    samplesBrapi.getResult().get(i).getGermplasmPUI());

            assertEquals("sampelName check failed!",
                    mockSetup.mockDnaSamples.get(i).getDnaSampleName(),
                    samplesBrapi.getResult().get(i).getSampleName());


            assertEquals("sampleNum check failed!",
                    mockSetup.mockDnaSamples.get(i).getDnaSampleNum(),
                    samplesBrapi.getResult().get(i).getWell());

            assertEquals("samplePUI check failed!",
                    mockSetup.mockDnaSamples.get(i).getDnaSampleUuid(),
                    samplesBrapi.getResult().get(i).getSamplePUI());

            assertEquals("projectId check failed!",
                    mockSetup.mockDnaSamples.get(i).getProject().getProjectId(),
                    samplesBrapi.getResult().get(i).getSampleGroupDbId());
        }

    }
}
