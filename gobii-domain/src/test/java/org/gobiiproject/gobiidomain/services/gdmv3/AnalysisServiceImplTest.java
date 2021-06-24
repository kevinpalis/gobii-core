package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.entity.Reference;
import org.gobiiproject.gobiisampletrackingdao.AnalysisDao;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.DatasetDao;
import org.gobiiproject.gobiisampletrackingdao.ReferenceDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

import lombok.extern.slf4j.Slf4j;

@WebAppConfiguration
@Slf4j
public class AnalysisServiceImplTest {
    @Mock
    private AnalysisDao analysisDao;

    @Mock
    private DatasetDao datasetDao;

    @Mock
    private CvDao cvDao;

    @Mock
    private ReferenceDao referenceDao;

    @Mock
    private ContactDao contactDao;

    @InjectMocks
    private AnalysisServiceImpl analysisServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAnalyses() throws Exception {
        assert analysisDao != null;
        List<Analysis> mockItems = new ArrayList<>();
        Integer offset = 0;
        Integer pageSize = 1000;
        when(analysisDao.getAnalyses(0, 1000)).thenReturn(mockItems);

        PagedResult<AnalysisDTO> results = analysisServiceImpl.getAnalyses(0, 1000);
        assert results != null;
        verify(analysisDao, times(1)).getAnalyses(offset, pageSize);

    }

    @Test
    public void testGetAnalysisById() throws Exception {
        Analysis mockAnalysis = new Analysis();
        when(analysisDao.getAnalysis(123)).thenReturn(mockAnalysis);

        AnalysisDTO sampleDTO = analysisServiceImpl.getAnalysis(123);
        assert sampleDTO != null;
        verify(analysisDao, times(1)).getAnalysis(123);
    }

    @Test
    public void testCreateAnalysisType() throws Exception {
        assert cvDao != null;

        when(
            cvDao.getCvGroupByNameAndType( CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName(), 2)
        ).thenReturn(
            new org.gobiiproject.gobiimodel.entity.CvGroup()
        );
        
        when(
            cvDao.createCv(
                any(Cv.class)
            )
        ).thenReturn(new Cv());

        when(
            cvDao.getNewStatus()
        ).thenReturn(new Cv());

        CvTypeDTO analysisTypeRequest = new CvTypeDTO();
        analysisTypeRequest.setTypeName("test-name");
        analysisTypeRequest.setTypeDescription("test-description");
        CvTypeDTO analysisType = analysisServiceImpl.createAnalysisType(analysisTypeRequest, "test-user");
        assert analysisType != null;
        verify(
            cvDao, times(1)
        ).getCvGroupByNameAndType(CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName(), 2);
        verify(
            cvDao, times(1)
        ).createCv(any(Cv.class));
        
    }

    @Test
    public void testCreateAnalysisOk()  throws Exception {
        AnalysisDTO request = new AnalysisDTO();
        request.setAnalysisTypeId(456);
        request.setAnalysisName("test-analysis");
        request.setReferenceId(678);


        Cv analysisType = new Cv();
        analysisType.setCvId(456);
        analysisType.setTerm("test-analysis-type");
        
        CvGroup group = new CvGroup();
        group.setCvGroupId(321);
        group.setCvGroupName(CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName());

        analysisType.setCvGroup(group);

        Contact mockContact = new Contact();
        mockContact.setContactId(788);
        mockContact.setUsername("test-editor");

        when(cvDao.getCvByCvId(456)).thenReturn(analysisType);
        when(contactDao.getContactByUsername("test-editor")).thenReturn(
            mockContact
        );

        //mock reference
        Reference mockReference = new Reference();
        mockReference.setReferenceId(678);
        when(referenceDao.getReference(678)).thenReturn(mockReference);

        when(analysisDao.createAnalysis(any(Analysis.class))).thenReturn(
            new Analysis()
        ); 

        ArgumentCaptor<Analysis> arg = ArgumentCaptor.forClass(Analysis.class);
        analysisServiceImpl.createAnalysis(request, "test-editor");

        verify(analysisDao).createAnalysis(arg.capture());
        log.info(arg.getValue().getAnalysisName());
        assertTrue(arg.getValue().getAnalysisName().equals("test-analysis"));
    }

    @Test
    public void testCreateAnalysisNoReferenceOk()  throws Exception {
        AnalysisDTO request = new AnalysisDTO();
        request.setAnalysisTypeId(456);
        request.setAnalysisName("test-analysis");


        Cv analysisType = new Cv();
        analysisType.setCvId(456);
        analysisType.setTerm("test-analysis-type");
        
        CvGroup group = new CvGroup();
        group.setCvGroupId(321);
        group.setCvGroupName(CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName());

        analysisType.setCvGroup(group);

        Contact mockContact = new Contact();
        mockContact.setContactId(788);
        mockContact.setUsername("test-editor");

        when(cvDao.getCvByCvId(456)).thenReturn(analysisType);
        when(contactDao.getContactByUsername("test-editor")).thenReturn(
            mockContact
        );


        when(analysisDao.createAnalysis(any(Analysis.class))).thenReturn(
            new Analysis()
        ); 

        ArgumentCaptor<Analysis> arg = ArgumentCaptor.forClass(Analysis.class);
        analysisServiceImpl.createAnalysis(request, "test-editor");

        verify(analysisDao).createAnalysis(arg.capture());
        log.info(arg.getValue().getAnalysisName());
        assertTrue(arg.getValue().getAnalysisName().equals("test-analysis"));
        assertNull(arg.getValue().getReference());
    }

    @Test(expected = GobiiException.class)
    public void testCreateAnalysisTypeNotFound() throws Exception {
        AnalysisDTO request = new AnalysisDTO();
        request.setAnalysisTypeId(345);
        request.setAnalysisName("test-analysis");
        request.setReferenceId(678);
        when (cvDao.getCvByCvId(345)).thenReturn(null);

        analysisServiceImpl.createAnalysis(request, "test-editor");
    }

    @Test(expected = GobiiException.class)
    public void testCreateAnalysisInvalidCvType() throws Exception {
        AnalysisDTO request = new AnalysisDTO();
        request.setAnalysisTypeId(345);
        request.setAnalysisName("test-analysis");
        request.setReferenceId(678);

        Cv mockCv = new Cv();
        mockCv.setCvId(345);
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName()); //incorrect 
        mockCv.setCvGroup(mockGroup);
        when (cvDao.getCvByCvId(345)).thenReturn(mockCv);

        analysisServiceImpl.createAnalysis(request, "test-editor");
        verify(cvDao, times(1)).getCvByCvId(345);

    }

    @Test(expected = GobiiException.class)
    public void testCreateAnalysisInvalidReference() throws Exception {
        AnalysisDTO request = new AnalysisDTO();
        request.setAnalysisTypeId(345);
        request.setAnalysisName("test-analysis");
        request.setReferenceId(678);

        Cv mockCv = new Cv();
        mockCv.setCvId(345);
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName()); //incorrect 
        mockCv.setCvGroup(mockGroup);
        when (cvDao.getCvByCvId(345)).thenReturn(mockCv);

        when (referenceDao.getReference(678)).thenReturn(null);

        analysisServiceImpl.createAnalysis(request, "test-editor");
        verify(cvDao, times(1)).getCvByCvId(345);

    }

    
    @Test
    public void testGetAnalysesOk() throws Exception {
        List<Analysis> mockList = new ArrayList<>();

        Analysis mockAnalysis = new Analysis();
        mockList.add(mockAnalysis);

        when(analysisDao.getAnalyses(0, 1000)).thenReturn(mockList);

        PagedResult<AnalysisDTO> result = analysisServiceImpl.getAnalyses(0, 1000);

        assertTrue(result.getCurrentPageNum() == 0 );
        assertTrue(result.getCurrentPageSize() == 1);
        assertTrue(result.getResult().size() == 1);
    }

    @Test
    public void testGetAnalysisTypesOk() throws Exception {
        List<Cv> mockList = new ArrayList<>();
        mockList.add(new Cv());

        when (cvDao.getCvs(null, CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName(), null, 0, 1000)).thenReturn(mockList);

        PagedResult<CvTypeDTO> result = analysisServiceImpl.getAnalysisTypes(0, 1000);

        assertTrue(result.getCurrentPageNum() == 0 );
        assertTrue(result.getCurrentPageSize() == 1);
        assertTrue(result.getResult().size() == 1);
    }

    @Test
    public void testUpdateAnalysisOk() throws Exception {
        AnalysisDTO request = new AnalysisDTO();
        request.setAnalysisTypeId(345);
        request.setAnalysisName("test-analysis-new");
        request.setDescription("new-description");
        request.setReferenceId(678);

        Reference mockReference = new Reference();
        mockReference.setReferenceId(678);
        when(referenceDao.getReference(678)).thenReturn(mockReference);

        Cv mockCv = new Cv();
        mockCv.setCvId(345);
        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName()); //incorrect 
        mockCv.setCvGroup(mockGroup);
        when (cvDao.getCvByCvId(345)).thenReturn(mockCv);

        Analysis mockAnalysis = new Analysis();
        mockAnalysis.setAnalysisId(123);
        mockAnalysis.setAnalysisName("test-name");
        when (analysisDao.getAnalysis(123)).thenReturn(mockAnalysis);

        Contact mockContact = new Contact();
        mockContact.setContactId(112);
        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);
     
        Cv mockNewStatus = new Cv();
        when(cvDao.getNewStatus()).thenReturn(mockNewStatus);
        when(analysisDao.updateAnalysis(any(Analysis.class))).thenReturn(new Analysis());

        ArgumentCaptor<Analysis> arg = ArgumentCaptor.forClass(Analysis.class);
        analysisServiceImpl.updateAnalysis(123, request, "test-editor");

        verify(analysisDao).updateAnalysis(arg.capture());

        assertTrue(arg.getValue().getAnalysisName().equals("test-analysis-new"));
        assertTrue(arg.getValue().getReference().getReferenceId() == 678);
        assertTrue(arg.getValue().getType().getCvId() == 345);
        assertTrue(arg.getValue().getDescription().equals("new-description"));   
    }

    @Test
    public void testUpdateAnalysisOkReferenceAndTypeNull() throws Exception {
        AnalysisDTO request = new AnalysisDTO();
        request.setAnalysisName("test-analysis-new");
        request.setDescription("new-description");

        Analysis mockAnalysis = new Analysis();
        mockAnalysis.setAnalysisId(123);
        mockAnalysis.setAnalysisName("test-name");
        when (analysisDao.getAnalysis(123)).thenReturn(mockAnalysis);

        Contact mockContact = new Contact();
        mockContact.setContactId(112);
        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);
     
        Cv mockNewStatus = new Cv();
        when(cvDao.getNewStatus()).thenReturn(mockNewStatus);
        when(analysisDao.updateAnalysis(any(Analysis.class))).thenReturn(new Analysis());

        ArgumentCaptor<Analysis> arg = ArgumentCaptor.forClass(Analysis.class);
        analysisServiceImpl.updateAnalysis(123, request, "test-editor");

        verify(analysisDao).updateAnalysis(arg.capture());

        assertTrue(arg.getValue().getAnalysisName().equals("test-analysis-new"));
        assertNull(arg.getValue().getReference()); //since no reference
        assertTrue(arg.getValue().getDescription().equals("new-description"));   
    }

    @Test
    public void testDeleteOk() throws Exception {
        when(analysisDao.getAnalysis(123)).thenReturn(new Analysis());
        when(datasetDao.getDatasetCountByAnalysisId(123)).thenReturn(0);
        when(datasetDao.getDatasetCountWithAnalysesContaining(123)).thenReturn(0);
        analysisServiceImpl.deleteAnalysis(123);
        
        verify(analysisDao, times(1)).deleteAnalysis(any(Analysis.class));
    }

    @Test(expected = GobiiException.class)
    public void testDeleteNotOk1() throws Exception {
        when(analysisDao.getAnalysis(123)).thenReturn(null);
        analysisServiceImpl.deleteAnalysis(123);
        
        verify(analysisDao, times(0)).deleteAnalysis(any(Analysis.class));
    }


    @Test(expected = GobiiException.class)
    public void testDeleteNotOk2() throws Exception {
        when(analysisDao.getAnalysis(123)).thenReturn(new Analysis());
        when(datasetDao.getDatasetCountByAnalysisId(123)).thenReturn(0);
        when(datasetDao.getDatasetCountWithAnalysesContaining(123)).thenReturn(10);
        analysisServiceImpl.deleteAnalysis(123);
        
        verify(analysisDao, times(0)).deleteAnalysis(any(Analysis.class));
    }

    @Test(expected = GobiiException.class)
    public void testDeleteNotOk3() throws Exception {
        when(analysisDao.getAnalysis(123)).thenReturn(new Analysis());
        when(datasetDao.getDatasetCountByAnalysisId(123)).thenReturn(10);
        when(datasetDao.getDatasetCountWithAnalysesContaining(123)).thenReturn(0);
        analysisServiceImpl.deleteAnalysis(123);
        
        verify(analysisDao, times(0)).deleteAnalysis(any(Analysis.class));
    }


    @Test(expected = GobiiException.class)
    public void testCreateAnalysisTypeWithExc() throws Exception {
        when(cvDao.getCvGroupByNameAndType(any(String.class), eq(2))).thenReturn(null);
        CvTypeDTO request = new CvTypeDTO();
        analysisServiceImpl.createAnalysisType(request, "any");
        verify(cvDao, times(0)).createCv(any(Cv.class));
    }


    
    

}