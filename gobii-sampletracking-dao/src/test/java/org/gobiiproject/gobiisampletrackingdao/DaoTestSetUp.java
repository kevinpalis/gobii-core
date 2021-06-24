package org.gobiiproject.gobiisampletrackingdao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.entity.*;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class DaoTestSetUp {

    private EntityManager em;
    private CvDao cvDao;

    public DaoTestSetUp(EntityManager em, CvDao cvDao) {
        this.em = em;
        this.cvDao = cvDao;
    }

    final int testPageSize = 10;

    Random random = new Random();

    private List<DnaRun> createdDnaRuns = new ArrayList<>();
    private List<Project> createdProjects = new ArrayList<>();
    private List<Germplasm> createdGermplasms = new ArrayList<>();
    private List<Experiment> createdExperiments = new ArrayList<>();
    private List<Contact> createdContacts = new ArrayList<>();
    private List<DnaSample> createdDnaSamples = new ArrayList<>();
    private List<Analysis> createdAnalyses = new ArrayList<>();
    private List<Dataset> createdDatasets = new ArrayList<>();
    private List<Mapset> createdMapsets = new ArrayList<>();
    private List<LinkageGroup> createdLinkageGroups = new ArrayList<>();
    private List<Protocol> createdProtocols = new ArrayList<>();
    private List<Platform> createdPlatforms = new ArrayList<>();
    private List<Marker> createdMarkers = new ArrayList<>();
    private List<MarkerLinkageGroup> createdMarkerLinkageGroups = new ArrayList<>();
    private List<VendorProtocol> createdVendorProtocols = new ArrayList<>();
    private List<Organization> createdOrganizations = new ArrayList<>();
    private List<LoaderTemplate> createdLoaderTemplates = new ArrayList<>();

    public void createTestContacts(int numOfContacts) {
        for(int i = 0; i < numOfContacts; i++) {
            //Add PI contact
            Contact contact = new Contact();
            contact.setFirstName(RandomStringUtils.random(7, true, true));
            contact.setLastName(RandomStringUtils.random(7, true, true));
            contact.setCode(RandomStringUtils.random(7, true, true));
            contact.setEmail(RandomStringUtils.random(7, true, true));
            em.persist(contact);
            createdContacts.add(contact);
        }
    }

    public void createTestProjects(int numOfProjects) {
        Cv newStatus = cvDao.getCvs(
            "new",
            CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);
        if(createdContacts.size() == 0) {
            createTestContacts((int) Math.ceil((double) numOfProjects/2));
        }
        for(int i = 0; i < numOfProjects; i++) {
            //Add Project
            Project project = new Project();
            Contact piContact = createdContacts
                .get(random.nextInt(createdContacts.size()));
            project.setContact(piContact);
            project.setProjectName(RandomStringUtils.random(7, true, true));
            project.setStatus(newStatus);
            em.persist(project);
            createdProjects.add(project);
        }
    }

    public void createTestGermplasms(int numOfGermplasms) {
        Cv newStatus = cvDao.getCvs("new", CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);
        for(int i = 0; i < numOfGermplasms; i++) {
            Germplasm germplasm = new Germplasm();
            germplasm.setGermplasmName(RandomStringUtils.random(7, true, true));
            germplasm.setExternalCode(RandomStringUtils.random(7, true, true));
            germplasm.setStatus(newStatus);
            em.persist(germplasm);
            createdGermplasms.add(germplasm);
        }
    }

    public void createTestDnaSamples(int numOfDnaSamples) {
        Cv newStatus = cvDao.getCvs(
            "new",
            CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);
        if(createdGermplasms.size() == 0) {
            createTestGermplasms( (int) Math.ceil((double) numOfDnaSamples/2));
        }
        if(createdProjects.size() == 0) {
            createTestProjects( (int) Math.ceil((double) numOfDnaSamples/2));
        }
        for(int i = 0; i < numOfDnaSamples; i++) {
            DnaSample dnaSample = new DnaSample();
            dnaSample.setDnaSampleName(RandomStringUtils.random(7, true, true));
            dnaSample.setProject(createdProjects.get(random.nextInt(createdProjects.size())));
            dnaSample.setGermplasm(createdGermplasms.get(random.nextInt(createdGermplasms.size())));
            dnaSample.setStatus(newStatus);
            dnaSample.setDnaSampleUuid(RandomStringUtils.random(10, true, true));
            em.persist(dnaSample);
            createdDnaSamples.add(dnaSample);
        }
    }

    public void createTestProtocols(int numOfProtocols) {

        if(createdPlatforms.size() == 0) {
            createTestPlatforms(numOfProtocols);
        }

        for(int i = 0; i < numOfProtocols; i++) {

            Protocol protocol = new Protocol();

            protocol.setName(RandomStringUtils.random(7, true, true));
            protocol.setDescription(RandomStringUtils.random(16, true, true));
            protocol.setPlatform(createdPlatforms.get(random.nextInt(createdPlatforms.size())));

            em.persist(protocol);
            createdProtocols.add(protocol);
        }

    }

    public void createTestExperiments(int numOfExperiments) {

        Cv newStatus = cvDao.getCvs(
            "new",
            CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);

        if(createdProjects.size() == 0) {
            createTestProjects( (int) Math.ceil((double) numOfExperiments/2));
        }

        if(createdProtocols.size() == 0) {
            createTestProtocols(numOfExperiments/2);
        }

        //Add Experiment
        for(int i = 0; i < numOfExperiments; i++){

            Experiment experiment = new Experiment();

            experiment.setExperimentName(RandomStringUtils.random(7, true, true));
            experiment.setExperimentCode(RandomStringUtils.random(7, true, true));
            experiment.setProject(createdProjects.get(random.nextInt(createdProjects.size())));
            experiment.setStatus(newStatus);

            VendorProtocol vendorProtocol = new VendorProtocol();

            Organization vendor = new Organization();
            vendor.setName(RandomStringUtils.random(7, true, true));
            vendor.setStatus(newStatus);

            em.persist(vendor);

            vendorProtocol.setProtocol(
                createdProtocols.get(random.nextInt(createdProtocols.size())));

            vendorProtocol.setVendor(vendor);
            em.persist(vendorProtocol);

            experiment.setVendorProtocol(vendorProtocol);
            em.persist(experiment);

            createdExperiments.add(experiment);
        }
    }

    public void createTestAnalyses(int numOfAnalysis) {
        Cv newStatus = cvDao.getCvs(
            "new",
            CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);
        List<Cv> analysisTypes = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM);
        for(int i = 0; i < numOfAnalysis; i++) {
            Analysis analysis = new Analysis();
            assertTrue("System defined analysis type values are not found.",
                analysisTypes.size() > -1);
            Integer analysisTypeIndex = random.nextInt(analysisTypes.size());
            String analysisName = RandomStringUtils.random(7, true, true);
            analysis.setAnalysisName(analysisName);
            analysis.setType(analysisTypes.get(analysisTypeIndex));
            analysis.setStatus(newStatus);
            em.persist(analysis);
            createdAnalyses.add(analysis);
        }
    }

    public void createTestDatasets(int numOfDatasets) {

        List<Cv> datasetTypes = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM);

        if(createdExperiments.size() == 0) {
            createTestExperiments( (int) Math.ceil((double) numOfDatasets/2));
        }
        if(createdAnalyses.size() == 0) {
            createTestAnalyses( (int) Math.ceil((double) numOfDatasets/2));
        }

        for(int i = 0; i < numOfDatasets; i++) {
            Dataset dataset = new Dataset();

            String datasetName = RandomStringUtils.random(7, true, true);
            dataset.setDatasetName(datasetName);
            dataset.setCallingAnalysis(createdAnalyses
                .get(random.nextInt(createdAnalyses.size())));
            dataset.setExperiment(createdExperiments
                .get(random.nextInt(createdExperiments.size())));
            dataset.setType(datasetTypes
                .get(random.nextInt(datasetTypes.size())));
            
            DatasetStats datasetStats = new DatasetStats();
            datasetStats.setDataset(dataset);
            datasetStats.setMarkerCount(10);
            datasetStats.setMarkerCount(10);
            em.persist(datasetStats); 
            
            em.persist(dataset);

            createdDatasets.add(dataset);

        }

        em.flush();

        for(Dataset dataset : createdDatasets) {
            DatasetStats datasetStats = new DatasetStats();
            datasetStats.setDatasetId(dataset.getDatasetId());
            datasetStats.setMarkerCount(100);
            datasetStats.setDnarunCount(100);
        }
    }

    public void createTestMapsets(int numOfMapsets) {

        Cv newStatus = cvDao.getCvs(
            "new",
            CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);

        List<Cv> mapsetTypes = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM);

        for(int i = 0; i < numOfMapsets; i++) {
            Mapset mapset = new Mapset();
            mapset.setType(mapsetTypes.get(random.nextInt(mapsetTypes.size())));
            mapset.setMapsetName(RandomStringUtils.random(7, true, true));
            mapset.setMapsetDescription(RandomStringUtils.random(10, true, true));
            mapset.setMapsetCode(RandomStringUtils.random(7, true, true));
            mapset.setStatus(newStatus);
            em.persist(mapset);
            createdMapsets.add(mapset);
        }
    }

    public void createTestLinkageGroups(int numOfLinkageGroups) {

        if(createdMapsets.size() == 0) {
            createTestMapsets( (int) Math.ceil((double) testPageSize/2));
        }

        for(int i = 0; i < numOfLinkageGroups; i++) {

            LinkageGroup linkageGroup = new LinkageGroup();

            linkageGroup.setLinkageGroupName(RandomStringUtils.random(7, true, true));

            linkageGroup.setLinkageGrpupStart(
                NumberUtils.toScaledBigDecimal(random.nextFloat()+10, 3, null));

            linkageGroup.setLinkageGroupStop(
                NumberUtils.toScaledBigDecimal(random.nextFloat()+20, 3, null));

            linkageGroup.setMapset(createdMapsets.get(random.nextInt(createdMapsets.size())));

            em.persist(linkageGroup);
            createdLinkageGroups.add(linkageGroup);
        }
    }


    public void createTestDnaRuns(int numOfDnaRuns) {

        if(createdDnaSamples.size() == 0) {
            createTestDnaSamples( (int) Math.ceil((double) numOfDnaRuns/2));
        }
        if(createdExperiments.size() == 0) {
            createTestExperiments( (int) Math.ceil((double) numOfDnaRuns/2));
        }
        if(createdDatasets.size() == 0) {
            createTestDatasets( (int) Math.ceil((double) numOfDnaRuns/2));
        }

        ObjectMapper objectMapper = new ObjectMapper();

        for(int i = 0; i < testPageSize; i++) {

            DnaRun dnaRun = new DnaRun();

            dnaRun.setDnaRunName(RandomStringUtils.random(7, true, true));
            dnaRun.setExperiment(
                createdExperiments
                    .get(random.nextInt(createdExperiments.size())));
            dnaRun.setDnaSample(
                createdDnaSamples
                    .get(random.nextInt(createdDnaSamples.size())));
            ObjectNode dnaRunDaatsetIdx =  objectMapper.createObjectNode();
            Integer datasetId =
                createdDatasets
                    .get(random.nextInt(createdDatasets.size())).getDatasetId();
            Integer hdf5Index = random.nextInt(1000);
            dnaRunDaatsetIdx.put(datasetId.toString(), hdf5Index.toString());
            dnaRun.setDatasetDnaRunIdx(dnaRunDaatsetIdx);
            em.persist(dnaRun);

            createdDnaRuns.add(dnaRun);
        }

    }

    public void createTestPlatforms(int numPlatforms) {

        Cv newStatus = cvDao.getCvs("new", CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);

        for(int i = 0; i < numPlatforms; i++) {

            Platform platform = new Platform();
            platform.setPlatformName(RandomStringUtils.random(4, true, true));
            platform.setDescription(RandomStringUtils.random(7, true, true));
            platform.setPlatformCode(RandomStringUtils.random(5, true, true));
            platform.setStatus(newStatus);

            em.persist(platform);

            createdPlatforms.add(platform);
        }

    }

    public void createTestMarkers(int numOfMarkers) {

        Cv newStatus = cvDao.getCvs("new", CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);

        if(createdPlatforms.size() == 0) {
            createTestPlatforms((int) Math.ceil((double) numOfMarkers/2));
        }

        if(createdDatasets.size() == 0) {
            createTestDatasets( (int) Math.ceil((double) numOfMarkers/2));
        }

        ObjectMapper objectMapper = new ObjectMapper();

        for(int i = 0; i < numOfMarkers; i++) {
            Marker marker = new Marker();

            marker.setMarkerName(RandomStringUtils.random(5, true, true));
            marker.setPlatform(createdPlatforms.get(random.nextInt(createdPlatforms.size())));
            marker.setStatus(newStatus);

            ObjectNode markerDaatsetIdx =  objectMapper.createObjectNode();
            Integer datasetId =
                createdDatasets
                    .get(random.nextInt(createdDatasets.size())).getDatasetId();
            Integer hdf5Index = random.nextInt(1000);
            markerDaatsetIdx.put(datasetId.toString(), hdf5Index.toString());
            marker.setDatasetMarkerIdx(markerDaatsetIdx);
            em.persist(marker);

            createdMarkers.add(marker);
        }
    }

    public void createTestMarkerLinkageGroups(int numMarkerLinkageGroups) {

        if(createdMarkers.size() == 0) {
            createTestMarkers(numMarkerLinkageGroups);
        }

        if(createdLinkageGroups.size() == 0) {
           createTestLinkageGroups((int) Math.ceil((double) numMarkerLinkageGroups/2));
        }

        for(int i = 0; i < numMarkerLinkageGroups; i++) {
            MarkerLinkageGroup markerLinkageGroup = new MarkerLinkageGroup();
            markerLinkageGroup.setLinkageGroup(
                createdLinkageGroups.get(random.nextInt(createdLinkageGroups.size())));
            markerLinkageGroup.setMarker(createdMarkers.get(i));

            BigDecimal position = NumberUtils.toScaledBigDecimal(random.nextFloat()+1000, 3, null);

            markerLinkageGroup.setStart(position);
            markerLinkageGroup.setStop(position);

            em.persist(markerLinkageGroup);

            createdMarkerLinkageGroups.add(markerLinkageGroup);
        }
    }

    public void createTestVendorProtocols(int numVendorProtocols) {
        if (createdProtocols.size() == 0) {
            createTestProtocols(10);
        }

        if (createdOrganizations.size() == 0) {
            createTestOrganizations(10);
        }

        Cv newStatus = cvDao.getCvs("new", CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);
        for (int i = 0; i<numVendorProtocols; i++) {
            VendorProtocol vp = new VendorProtocol();
            vp.setName(RandomStringUtils.random(10, true, true));
            vp.setStatus(newStatus);
            vp.setProtocol( createdProtocols.get( random.nextInt(createdProtocols.size())));
            vp.setVendor( createdOrganizations.get( random.nextInt(createdOrganizations.size())));
            em.persist(vp);

            createdVendorProtocols.add(vp);
        }
    }

    public void createTestOrganizations(int numOrganizations) {
        Cv newStatus = cvDao.getCvs("new", CvGroupTerm.CVGROUP_STATUS.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);
        for (int i = 0; i < numOrganizations; i++) {
            Organization org = new Organization();
            org.setName( RandomStringUtils.random(10, true, true));
            org.setAddress(RandomStringUtils.random(20, true, true));
            org.setWebsite(RandomStringUtils.random(20, true, true));
            org.setStatus(newStatus);
            em.persist(org);
            createdOrganizations.add(org);
        }
    }

    public void createTestLoaderTemplates(int numLoaderTemplates) {
        List<Cv> payloadTypes = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_PAYLOADTYPE.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM);
        if(createdContacts.size() == 0) {
            createTestContacts((int) Math.ceil((double) numLoaderTemplates/2));
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode template =  mapper.createObjectNode();

        for(int i = 0; i < numLoaderTemplates; i++) {
            LoaderTemplate loaderTemplate = new LoaderTemplate();
            loaderTemplate.setTemplateName(RandomStringUtils.random(7, true, true));
            loaderTemplate
                .setCreatedBy(createdContacts.get(random.nextInt(createdContacts.size())));
            loaderTemplate
                .setModifiedBy(createdContacts.get(random.nextInt(createdContacts.size())));
            loaderTemplate.setCreatedDate(new Date());
            loaderTemplate.setModifiedDate(new Date());
            loaderTemplate.setTemplateType(
                payloadTypes.get(random.nextInt(payloadTypes.size())));
            loaderTemplate.setTemplate(template);
            em.persist(loaderTemplate);
            createdLoaderTemplates.add(loaderTemplate);
        }

    }

    public List<DnaRun> getCreatedDnaRuns() {
        return createdDnaRuns;
    }

    public void setCreatedDnaRuns(List<DnaRun> createdDnaRuns) {
        this.createdDnaRuns = createdDnaRuns;
    }

    public List<MarkerLinkageGroup> getCreatedMarkerLinkageGroups() {
        return createdMarkerLinkageGroups;
    }

    public void setCreatedMarkerLinkageGroups(List<MarkerLinkageGroup> createdMarkerLinkageGroups) {
        this.createdMarkerLinkageGroups = createdMarkerLinkageGroups;
    }

    public List<Germplasm> getCreatedGermplasms() {
        return createdGermplasms;
    }

    public void setCreatedGermplasms(List<Germplasm> createdGermplasms) {
        this.createdGermplasms = createdGermplasms;
    }

    public List<Experiment> getCreatedExperiments() {
        return createdExperiments;
    }

    public void setCreatedExperiments(List<Experiment> createdExperiments) {
        this.createdExperiments = createdExperiments;
    }

    public List<Contact> getCreatedContacts() {
        return createdContacts;
    }

    public void setCreatedContacts(List<Contact> createdContacts) {
        this.createdContacts = createdContacts;
    }

    public List<Project> getCreatedProjects() {
        return createdProjects;
    }

    public void setCreatedProjects(List<Project> createdProjects) {
        this.createdProjects = createdProjects;
    }

    public List<DnaSample> getCreatedDnaSamples() {
        return createdDnaSamples;
    }

    public void setCreatedDnaSamples(List<DnaSample> createdDnaSamples) {
        this.createdDnaSamples = createdDnaSamples;
    }

    public List<Analysis> getCreatedAnalyses() {
        return createdAnalyses;
    }

    public void setCreatedAnalyses(List<Analysis> createdAnalyses) {
        this.createdAnalyses = createdAnalyses;
    }

    public List<Dataset> getCreatedDatasets() {
        return createdDatasets;
    }

    public void setCreatedDatasets(List<Dataset> createdDatasets) {
        this.createdDatasets = createdDatasets;
    }

    public List<Mapset> getCreatedMapsets() {
        return createdMapsets;
    }

    public void setCreatedMapsets(List<Mapset> createdMapsets) {
        this.createdMapsets = createdMapsets;
    }

    public List<LinkageGroup> getCreatedLinkageGroups() {
        return createdLinkageGroups;
    }

    public void setCreatedLinkageGroups(List<LinkageGroup> createdLinkageGroups) {
        this.createdLinkageGroups = createdLinkageGroups;
    }

    public List<Protocol> getCreatedProtocols() {
        return createdProtocols;
    }

    public void setCreatedProtocols(List<Protocol> createdProtocols) {
        this.createdProtocols = createdProtocols;
    }

    public List<Platform> getCreatedPlatforms() {
        return createdPlatforms;
    }

    public void setCreatedPlatforms(List<Platform> createdPlatforms) {
        this.createdPlatforms = createdPlatforms;
    }

    public List<Marker> getCreatedMarkers() {
        return createdMarkers;
    }

    public void setCreatedMarkers(List<Marker> createdMarkers) {
        this.createdMarkers = createdMarkers;
    }

    public List<VendorProtocol> getCreatedVendorProtocols() {
        return createdVendorProtocols;
    }

    public void setCreatedVendorProtocols(List<VendorProtocol> createdVendorProtocols) {
        this.createdVendorProtocols = createdVendorProtocols;
    }

    public List<Organization> getCreatedOrganizations() {
        return createdOrganizations;
    }

    public void setCreatedOrganizations(List<Organization> createdOrganizations) {
        this.createdOrganizations = createdOrganizations;
    }

    public List<LoaderTemplate> getCreatedLoaderTemplates() {
        return createdLoaderTemplates;
    }

    public void setCreatedLoaderTemplates(List<LoaderTemplate> createdLoaderTemplates) {
        this.createdLoaderTemplates = createdLoaderTemplates;
    }
}
