package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiisampletrackingdao.DnaRunDaoImpl;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GenotypesCallsServiceImplTest {


    @InjectMocks
    private GenotypeCallsServiceImpl genotypeCallsService;

    @Mock
    private DnaRunDaoImpl dnaRunDao;

    private MockSetup mockSetup;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockSetup  = new MockSetup();
    }

    public void getGenotypeCallsByCallSetIdTest() {



    }


}
