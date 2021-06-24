package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.UnknownEntityException;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetRequestDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.entity.Project;
import org.gobiiproject.gobiisampletrackingdao.AnalysisDao;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.DatasetDao;
import org.gobiiproject.gobiisampletrackingdao.DnaRunDao;
import org.gobiiproject.gobiisampletrackingdao.ExperimentDao;
import org.gobiiproject.gobiisampletrackingdao.MarkerDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class DatasetServiceImplTest {

    @Mock
    private DatasetDao datasetDao;

    @Mock
    private ExperimentDao experimentDao;

    @Mock
    private CvDao cvDao;

    @Mock
    private AnalysisDao analysisDao;

    @Mock
    private ContactDao contactDao;

    @Mock
    private MarkerDao markerDao;

    @Mock
    private DnaRunDao dnaRunDao;

    @InjectMocks
    private DatasetServiceImpl datasetServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = GobiiException.class)
    public void testCreateDatasetNullExperiment() throws Exception {
        // Mock request
        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetName("test-name");
        request.setExperimentId(1);
        request.setCallingAnalysisId(1);

        when(experimentDao.getExperiment(1)).thenReturn(null);

        datasetServiceImpl.createDataset(request, "test-user");

    }

    @Test
    public void testCreateDatasetSimple() throws Exception {
        // Mock request
        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetName("test-name");
        request.setExperimentId(1);
        request.setCallingAnalysisId(1);

        String user = "test-user";

        Experiment mockExperiment = new Experiment();
        mockExperiment.setExperimentId(1);
        mockExperiment.setExperimentName("test-experiment");

        Project mockProject = new Project();
        mockProject.setProjectId(1);
        mockProject.setProjectName("test-project");

        Contact mockContact = new Contact();
        mockContact.setContactId(1);
        mockContact.setFirstName("firstName");
        mockContact.setLastName("lastName");

        mockProject.setContact(mockContact);
        mockExperiment.setProject(mockProject);

        when(experimentDao.getExperiment(1)).thenReturn(mockExperiment // mock experiment
        );

        Analysis mockAnalysis = new Analysis();
        mockAnalysis.setAnalysisName("test-name");
        mockAnalysis.setAnalysisId(1);

        when(analysisDao.getAnalysis(1)).thenReturn(mockAnalysis);

        Cv mockNewStatus = new Cv();
        mockNewStatus.setTerm("new");
        mockNewStatus.setCvId(1);

        when(cvDao.getNewStatus()).thenReturn(mockNewStatus);
        Contact mockEditor = new Contact();
        mockEditor.setContactId(2);
        mockEditor.setUsername("test-user");
        when(contactDao.getContactByUsername("test-user")).thenReturn(mockEditor);

        Dataset mockDataset = new Dataset();
        mockDataset.setExperiment(mockExperiment);

        when(datasetDao.saveDataset(any(Dataset.class))).thenReturn(mockDataset);

        ArgumentCaptor<Dataset> arg = ArgumentCaptor.forClass(Dataset.class);
        datasetServiceImpl.createDataset(request, user);

        verify(experimentDao, times(1)).getExperiment(1);
        verify(analysisDao, times(1)).getAnalysis(1);
        verify(datasetDao).saveDataset(arg.capture());

        assertTrue(arg.getValue().getDatasetName().equals("test-name"));

    }

    @Test(expected = GobiiException.class)
    public void testCreateDatasetCallingAnalysisNotFound() throws Exception {
        // Mock request
        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetName("test-name");
        request.setExperimentId(1);
        request.setCallingAnalysisId(1);

        when(experimentDao.getExperiment(1)).thenReturn(new Experiment());

        when(analysisDao.getAnalysis(1)).thenReturn(null);

        datasetServiceImpl.createDataset(request, "test-user");
    }

    @Test(expected = GobiiException.class)
    public void testCreateDatasetAnalysesNotFound() throws Exception {
        // Mock request
        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetName("test-name");
        request.setExperimentId(1);
        request.setCallingAnalysisId(1);
        request.setAnalysisIds(new Integer[]{1, 2});

        when(experimentDao.getExperiment(1)).thenReturn(new Experiment());

        when(analysisDao.getAnalysis(1)).thenReturn(new Analysis());

        when(analysisDao.getAnalysesByAnalysisIds(anySet())).thenReturn(new ArrayList<Analysis>());

        datasetServiceImpl.createDataset(request, "test-user");
    }

    @Test
    public void testCreateDatasetWithDatasetTypeId() throws Exception {
        // Mock request
        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetName("test-name");
        request.setExperimentId(1);
        request.setCallingAnalysisId(1);
        request.setDatasetTypeId(2);

        String user = "test-user";

        Experiment mockExperiment = new Experiment();
        mockExperiment.setExperimentId(1);
        mockExperiment.setExperimentName("test-experiment");

        Project mockProject = new Project();
        mockProject.setProjectId(1);
        mockProject.setProjectName("test-project");

        Contact mockContact = new Contact();
        mockContact.setContactId(1);
        mockContact.setFirstName("firstName");
        mockContact.setLastName("lastName");

        mockProject.setContact(mockContact);
        mockExperiment.setProject(mockProject);

        when(experimentDao.getExperiment(1)).thenReturn(mockExperiment // mock experiment
        );

        Analysis mockAnalysis = new Analysis();
        mockAnalysis.setAnalysisName("test-name");
        mockAnalysis.setAnalysisId(1);

        when(analysisDao.getAnalysis(1)).thenReturn(mockAnalysis);

        Cv mockDatasetTypeCv = new Cv();
        CvGroup cvGroup = new CvGroup();
        cvGroup.setCvGroupName(CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName());
        mockDatasetTypeCv.setCvGroup(cvGroup);
        mockDatasetTypeCv.setCvId(2);

        when(cvDao.getCvByCvId(2)).thenReturn(mockDatasetTypeCv);

        Cv mockNewStatus = new Cv();
        mockNewStatus.setTerm("new");
        mockNewStatus.setCvId(1);

        when(cvDao.getNewStatus()).thenReturn(mockNewStatus);
        Contact mockEditor = new Contact();
        mockEditor.setContactId(2);
        mockEditor.setUsername("test-user");
        when(contactDao.getContactByUsername("test-user")).thenReturn(mockEditor);

        Dataset mockDataset = new Dataset();
        mockDataset.setExperiment(mockExperiment);

        when(datasetDao.saveDataset(any(Dataset.class))).thenReturn(mockDataset);

        ArgumentCaptor<Dataset> arg = ArgumentCaptor.forClass(Dataset.class);
        datasetServiceImpl.createDataset(request, user);

        verify(experimentDao, times(1)).getExperiment(1);
        verify(analysisDao, times(1)).getAnalysis(1);
        verify(datasetDao).saveDataset(arg.capture());

        assertTrue(arg.getValue().getDatasetName().equals("test-name"));
        assertTrue(arg.getValue().getType().getCvId() == 2);
    }

    @Test
    public void testCreateWithAnalysisIds() throws Exception {
        // Mock request
        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetName("test-name");
        request.setExperimentId(1);
        request.setCallingAnalysisId(1);
        request.setAnalysisIds(new Integer[] { 10, 11 });

        String user = "test-user";

        Experiment mockExperiment = getMockExperiment();

        when(experimentDao.getExperiment(1)).thenReturn(mockExperiment // mock experiment
        );

        Analysis mockAnalysis = new Analysis();
        mockAnalysis.setAnalysisName("test-name");
        mockAnalysis.setAnalysisId(1);
        when(analysisDao.getAnalysis(1)).thenReturn(mockAnalysis);

        Cv mockNewStatus = new Cv();
        mockNewStatus.setTerm("new");
        mockNewStatus.setCvId(1);

        when(cvDao.getNewStatus()).thenReturn(mockNewStatus);
        Contact mockEditor = new Contact();
        mockEditor.setContactId(2);
        mockEditor.setUsername("test-user");
        when(contactDao.getContactByUsername("test-user")).thenReturn(mockEditor);

        Dataset mockDataset = new Dataset();
        mockDataset.setExperiment(mockExperiment);

        List<Analysis> mockAnalysisList = this.getMockAnalysisList();

        when(analysisDao.getAnalysesByAnalysisIds(anySet())).thenReturn(mockAnalysisList);

        when(datasetDao.saveDataset(any(Dataset.class))).thenReturn(mockDataset);

        ArgumentCaptor<Dataset> arg = ArgumentCaptor.forClass(Dataset.class);
        datasetServiceImpl.createDataset(request, user);

        verify(experimentDao, times(1)).getExperiment(1);
        verify(analysisDao, times(1)).getAnalysis(1);
        verify(datasetDao).saveDataset(arg.capture());

        assertTrue(arg.getValue().getDatasetName().equals("test-name"));
        assertTrue(arg.getValue().getAnalyses().length == 2);
    }

    @Test(expected = GobiiException.class)
    public void testUpdateDatasetNotFound() throws Exception {
        when(datasetDao.getDataset(123)).thenReturn(null);

        DatasetRequestDTO request = new DatasetRequestDTO();
        datasetServiceImpl.updateDataset(123, request, "test-user");
    }

    @Test
    public void testUpdateDatasetOk() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetName("test-name");
        mockDataset.setExperiment(new Experiment());

        Experiment mockExperiment = getMockExperiment();
        mockDataset.setExperiment(mockExperiment);

        Analysis mockAnalysis = new Analysis();
        mockAnalysis.setAnalysisName("test-name");
        mockAnalysis.setAnalysisId(1);
        when(analysisDao.getAnalysis(1)).thenReturn(mockAnalysis);

        when(experimentDao.getExperiment(1)).thenReturn(mockExperiment);

        when(datasetDao.getDataset(123)).thenReturn(mockDataset);

        Cv mockNewStatus = new Cv();
        mockNewStatus.setTerm("new");
        mockNewStatus.setCvId(1);

        when(cvDao.getNewStatus()).thenReturn(mockNewStatus);
        Contact mockEditor = new Contact();
        mockEditor.setContactId(2);
        mockEditor.setUsername("test-user");
        when(contactDao.getContactByUsername("test-user")).thenReturn(mockEditor);

        when(analysisDao.getAnalysesByAnalysisIds(anySet())).thenReturn(this.getMockAnalysisList());

        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetName("new-name");
        request.setExperimentId(1);
        request.setCallingAnalysisId(1);
        request.setAnalysisIds(new Integer[] { 10, 11 });
        request.setDatasetTypeId(13);

        Dataset mockDataset2 = new Dataset();
        mockDataset.setExperiment(mockExperiment);

        Cv mockDatasetTypeCv = new Cv();
        CvGroup cvGroup = new CvGroup();
        cvGroup.setCvGroupName(CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName());
        mockDatasetTypeCv.setCvGroup(cvGroup);
        mockDatasetTypeCv.setCvId(13);

        when(cvDao.getCvByCvId(13)).thenReturn(mockDatasetTypeCv);

        when(datasetDao.updateDataset(any(Dataset.class))).thenReturn(mockDataset2);

        ArgumentCaptor<Dataset> arg = ArgumentCaptor.forClass(Dataset.class);
        datasetServiceImpl.updateDataset(123, request, "test-user");

        verify(datasetDao).updateDataset(arg.capture());

        assertTrue(arg.getValue().getDatasetName().equals("new-name"));
        assertTrue(arg.getValue().getExperiment().getExperimentId() == 1);
        assertTrue(arg.getValue().getCallingAnalysis().getAnalysisId() == 1);
    }

    @Test
    public void testUpdateDatasetNoUpdatesOk() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetName("test-name");
        mockDataset.setExperiment(new Experiment());

        when(datasetDao.getDataset(123)).thenReturn(mockDataset);

        DatasetRequestDTO request = new DatasetRequestDTO();

        datasetServiceImpl.updateDataset(123, request, "test-user");

        verify(datasetDao, times(0)).updateDataset(any(Dataset.class));
    }

    @Test
    public void testUpdateDatasetNameOnlyOk() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetName("test-name");
        mockDataset.setExperiment(new Experiment());

        Experiment mockExperiment = getMockExperiment();
        mockDataset.setExperiment(mockExperiment);

        when(datasetDao.getDataset(123)).thenReturn(mockDataset);

        Contact mockEditor = new Contact();
        mockEditor.setContactId(2);
        mockEditor.setUsername("test-user");
        when(contactDao.getContactByUsername("test-user")).thenReturn(mockEditor);

        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetName("new-name");

        Dataset mockDataset2 = new Dataset();
        mockDataset.setExperiment(mockExperiment);

        when(datasetDao.updateDataset(any(Dataset.class))).thenReturn(mockDataset2);

        ArgumentCaptor<Dataset> arg = ArgumentCaptor.forClass(Dataset.class);
        datasetServiceImpl.updateDataset(123, request, "test-user");

        verify(datasetDao).updateDataset(arg.capture());

        assertTrue(arg.getValue().getDatasetName().equals("new-name"));
    }

    // @Test
    // public void testUpdateDatasetExperimentUpdateOnlyOk() throws Exception {
    //     Dataset mockDataset = new Dataset();
    //     mockDataset.setDatasetName("test-name");
    //     mockDataset.setExperiment(new Experiment());

    //     Experiment mockExperiment = getMockExperiment();
    //     mockExperiment.setExperimentId(1);

    //     when(experimentDao.getExperiment(1)).thenReturn(mockExperiment);

    //     when(datasetDao.getDataset(123)).thenReturn(mockDataset);

    //     Cv mockModStatus = new Cv();
    //     mockModStatus.setTerm("modified");
    //     mockModStatus.setCvId(1);

    //     when(cvDao.getModifiedStatus()).thenReturn(mockModStatus);
    //     Contact mockEditor = new Contact();
    //     mockEditor.setContactId(2);
    //     mockEditor.setUsername("test-user");
    //     when(contactDao.getContactByUsername("test-user")).thenReturn(mockEditor);

    //     DatasetRequestDTO request = new DatasetRequestDTO();
    //     request.setExperimentId(1);

    //     Dataset mockDataset2 = new Dataset();
    //     mockDataset.setExperiment(mockExperiment);

    //     when(datasetDao.updateDataset(any(Dataset.class))).thenReturn(mockDataset2);

    //     ArgumentCaptor<Dataset> arg = ArgumentCaptor.forClass(Dataset.class);
    //     datasetServiceImpl.updateDataset(123, request, "test-user");
    //     verify(datasetDao).updateDataset(arg.capture());

    //     assertTrue(arg.getValue().getDatasetName().equals("test-name"));
    //     assertTrue(arg.getValue().getExperiment().getExperimentId() == 1);
    // } -- updating Experiment Id no longer allowed

    // @Test(expected = GobiiException.class)
    // public void testUpdateDatasetExperimentUpdateOnlyNotFound() throws Exception {
    //     Dataset mockDataset = new Dataset();
    //     mockDataset.setDatasetName("test-name");
    //     mockDataset.setExperiment(new Experiment());

    //     when(datasetDao.getDataset(123)).thenReturn(mockDataset);

    //     when(experimentDao.getExperiment(1)).thenReturn(null);

    //     DatasetRequestDTO request = new DatasetRequestDTO();
    //     request.setExperimentId(1);

    //     datasetServiceImpl.updateDataset(123, request, "test-user");
    //     verify(datasetDao, times(0)).updateDataset(any(Dataset.class));
    // }

    @Test(expected = GobiiException.class)
    public void testUpdateDatasetExperimentCallingAnalysisOnlyNotFound() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetName("test-name");
        mockDataset.setExperiment(new Experiment());

        when(datasetDao.getDataset(123)).thenReturn(mockDataset);

        when(analysisDao.getAnalysis(111)).thenReturn(null);

        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setCallingAnalysisId(111);
        ;

        datasetServiceImpl.updateDataset(123, request, "test-user");
        verify(datasetDao, times(0)).updateDataset(any(Dataset.class));
    }

    @Test(expected = UnknownEntityException.class)
    public void testUpdateDatasetUnknownType() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetName("test-name");
        mockDataset.setExperiment(new Experiment());

        when(datasetDao.getDataset(123)).thenReturn(mockDataset);

        when(cvDao.getCvByCvId(13)).thenReturn(null);

        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetTypeId(13);

        datasetServiceImpl.updateDataset(123, request, "test-user");

        verify(datasetDao, times(0)).updateDataset(any(Dataset.class));
    }

    @Test(expected = InvalidException.class)
    public void testUpdateDatasetIncorrectType() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetName("test-name");
        mockDataset.setExperiment(new Experiment());

        Cv mockDatasetTypeCv = new Cv();
        CvGroup cvGroup = new CvGroup();
        cvGroup.setCvGroupName(CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName());
        mockDatasetTypeCv.setCvGroup(cvGroup);
        mockDatasetTypeCv.setCvId(13);

        when(cvDao.getCvByCvId(13)).thenReturn(mockDatasetTypeCv);
        when(datasetDao.getDataset(123)).thenReturn(mockDataset);

        when(cvDao.getCvByCvId(13)).thenReturn(mockDatasetTypeCv);

        DatasetRequestDTO request = new DatasetRequestDTO();
        request.setDatasetTypeId(13);

        datasetServiceImpl.updateDataset(123, request, "test-user");

        verify(datasetDao, times(0)).updateDataset(any(Dataset.class));
    }

    private Experiment getMockExperiment() {
        Experiment mockExperiment = new Experiment();
        mockExperiment.setExperimentId(1);
        mockExperiment.setExperimentName("test-experiment");

        Project mockProject = new Project();
        mockProject.setProjectId(1);
        mockProject.setProjectName("test-project");

        Contact mockContact = new Contact();
        mockContact.setContactId(1);
        mockContact.setFirstName("firstName");
        mockContact.setLastName("lastName");

        mockProject.setContact(mockContact);
        mockExperiment.setProject(mockProject);
        return mockExperiment;
    }

    @Test
    public void testDeleteOk() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetName("test-name");
        mockDataset.setExperiment(new Experiment());
        mockDataset.setDatasetId(144);

        when(datasetDao.getDataset(114)).thenReturn(mockDataset);

        List<Marker> mockMarkers = new ArrayList<>(); // empty means Ok
        when(markerDao.getMarkersByDatasetId(114, 1, 0)).thenReturn(mockMarkers);

        List<DnaRun> mockRuns = new ArrayList<>();
        when(dnaRunDao.getDnaRunsByDatasetId(114, 1, 0)).thenReturn(mockRuns);

        // invoke
        datasetServiceImpl.deleteDataset(114);
        verify(datasetDao, times(1)).deleteDataset(any(Dataset.class));
    }

    @Test(expected = GobiiException.class)
    public void testDeleteWithMarkersPresent() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetName("test-name");
        mockDataset.setExperiment(new Experiment());
        mockDataset.setDatasetId(144);

        when(datasetDao.getDataset(114)).thenReturn(mockDataset);

        List<Marker> mockMarkers = new ArrayList<>(); // empty means Ok
        mockMarkers.add(new Marker());
        when(markerDao.getMarkersByDatasetId(114, 1, 0)).thenReturn(mockMarkers);

        // invoke
        datasetServiceImpl.deleteDataset(114);
        verify(datasetDao, times(0)).deleteDataset(any(Dataset.class));
    }

    @Test(expected = GobiiException.class)
    public void testDeleteWithRunsPresent() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetName("test-name");
        mockDataset.setExperiment(new Experiment());
        mockDataset.setDatasetId(144);

        when(datasetDao.getDataset(114)).thenReturn(mockDataset);

        List<Marker> mockMarkers = new ArrayList<>(); // empty means Ok
        when(markerDao.getMarkersByDatasetId(114, 1, 0)).thenReturn(mockMarkers);

        List<DnaRun> mockRuns = new ArrayList<>();
        mockRuns.add(new DnaRun());
        when(dnaRunDao.getDnaRunsByDatasetId(114, 1, 0)).thenReturn(mockRuns);

        // invoke
        datasetServiceImpl.deleteDataset(114);
        verify(datasetDao, times(0)).deleteDataset(any(Dataset.class));
    }

    private List<Analysis> getMockAnalysisList() {
        List<Analysis> mockAnalysisList = new ArrayList<>();
        Analysis analysis1 = new Analysis();
        analysis1.setAnalysisId(10);
        Analysis analysis2 = new Analysis();
        analysis2.setAnalysisId(11);
        mockAnalysisList.add(analysis1);
        mockAnalysisList.add(analysis2);

        return mockAnalysisList;

    }

    @Test
    public void testGetDatasets() throws Exception {

        List<Dataset> mockDatasets = new ArrayList<>();

        Dataset mockDataset1 = new Dataset();
        mockDataset1.setDatasetId(1);
        mockDataset1.setDatasetName("set1");

        mockDatasets.add(mockDataset1);

        when(datasetDao.getDatasets(1000, 0, null, null, null, null, null)).thenReturn(mockDatasets);

        PagedResult<DatasetDTO> result = datasetServiceImpl.getDatasets(0, 1000, null, null);
        assertTrue(result.getCurrentPageNum() == 0);
        assertTrue(result.getCurrentPageSize() == 1);
        assertTrue(result.getResult().size() == 1);
    }

    @Test
    public void testCreateDatasetType() throws Exception {
        CvGroup mockCvGroup = new CvGroup();
        mockCvGroup.setCvGroupId(21);
        mockCvGroup.setCvGroupName(CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName());
        mockCvGroup.setCvGroupType(2);

        when(cvDao.getCvGroupByNameAndType(CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName(), 2))
                .thenReturn(mockCvGroup);

        Cv mockNewStatus = new Cv();
        mockNewStatus.setCvId(24);
        mockNewStatus.setTerm("new");

        when(cvDao.getNewStatus()).thenReturn(mockNewStatus);

        when(cvDao.createCv(any(Cv.class))).thenReturn(new Cv()); //

        ArgumentCaptor<Cv> arg = ArgumentCaptor.forClass(Cv.class);

        datasetServiceImpl.createDatasetType("type1", "type2", "user");
        verify(cvDao).createCv(arg.capture());

        assertTrue(arg.getValue().getTerm().equals("type1"));
        assertTrue(arg.getValue().getDefinition().equals("type2"));
        assertTrue(arg.getValue().getCvGroup().getCvGroupId() == 21);

    }

    @Test
    public void testCreateDatasetTypeNoDefinitionOk() throws Exception {
        CvGroup mockCvGroup = new CvGroup();
        mockCvGroup.setCvGroupId(21);
        mockCvGroup.setCvGroupName(CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName());
        mockCvGroup.setCvGroupType(2);

        when(cvDao.getCvGroupByNameAndType(CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName(), 2))
                .thenReturn(mockCvGroup);

        Cv mockNewStatus = new Cv();
        mockNewStatus.setCvId(24);
        mockNewStatus.setTerm("new");

        when(cvDao.getNewStatus()).thenReturn(mockNewStatus);

        when(cvDao.createCv(any(Cv.class))).thenReturn(new Cv()); //

        ArgumentCaptor<Cv> arg = ArgumentCaptor.forClass(Cv.class);

        datasetServiceImpl.createDatasetType("type1", null, "user");
        verify(cvDao).createCv(arg.capture());

        assertTrue(arg.getValue().getTerm().equals("type1"));
        assertNull(arg.getValue().getDefinition());
        assertTrue(arg.getValue().getCvGroup().getCvGroupId() == 21);

    }

    @Test (expected = GobiiDaoException.class)
    public void testCreateDatasetTypeGroupNotFound() throws Exception {
       when(cvDao.getCvGroupByNameAndType(CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName(),2)).thenReturn(
           null
       );

       datasetServiceImpl.createDatasetType("test", "datasetTypeDescription", "user");
       verify(cvDao, times(0)).createCv(any(Cv.class));

    }

    @Test
    public void testGetDatasetTypes() throws Exception {

        List<Cv> mockDatasetTypes = new ArrayList<>();

        CvGroup mockGroup = new CvGroup();
        mockGroup.setCvGroupName(CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName());
        mockGroup.setCvGroupType(2);
        mockGroup.setCvGroupId(999);

        Cv type1 = new Cv();
        type1.setTerm("testType");
        type1.setCvGroup(mockGroup);

        mockDatasetTypes.add(type1);
        when(cvDao.getCvs(null, CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName(), null, 0, 1000))
                .thenReturn(mockDatasetTypes);

        PagedResult<CvTypeDTO> result = datasetServiceImpl.getDatasetTypes(0, 1000);
        assertTrue(result.getCurrentPageNum() == 0);
        assertTrue(result.getCurrentPageSize() == 1);
        assertTrue(result.getResult().size() == 1);
    }

    @Test
    public void testGetDatasetOk() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetId(444);
        mockDataset.setDatasetName("boo-set");
        mockDataset.setAnalyses(new Integer[] { 456 });

        when(datasetDao.getDataset(444)).thenReturn(mockDataset);

        List<Analysis> mockAnalysisList = new ArrayList<>();
        Analysis mockAnalysis = new Analysis();
        mockAnalysis.setAnalysisId(456);
        mockAnalysisList.add(mockAnalysis);

        when(analysisDao.getAnalysesByAnalysisIds(anySet())).thenReturn(mockAnalysisList);

        DatasetDTO result = datasetServiceImpl.getDataset(444);

        verify(datasetDao, times(1)).getDataset(444);

        assertTrue(result.getAnalyses().size() == 1);

    }

    @Test
    public void testGetDatasetNoAnalysisOk() throws Exception {
        Dataset mockDataset = new Dataset();
        mockDataset.setDatasetId(444);
        mockDataset.setDatasetName("boo-set");


        when(datasetDao.getDataset(444)).thenReturn(mockDataset);

        List<Analysis> mockAnalysisList = new ArrayList<>();
        Analysis mockAnalysis = new Analysis();
        mockAnalysis.setAnalysisId(456);
        mockAnalysisList.add(mockAnalysis);

        DatasetDTO result = datasetServiceImpl.getDataset(444);

        verify(datasetDao, times(1)).getDataset(444);
        verify(analysisDao, times(0)).getAnalysesByAnalysisIds(anySet());

        assertNull(result.getAnalyses());

    }
}