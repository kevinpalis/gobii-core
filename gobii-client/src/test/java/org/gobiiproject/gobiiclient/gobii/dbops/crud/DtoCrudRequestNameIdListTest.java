package org.gobiiproject.gobiiclient.gobii.dbops.crud;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.core.gobii.GobiiTestConfiguration;
import org.gobiiproject.gobiiclient.gobii.Helpers.ADLEncapsulator;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestDtoFactory;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestUtils;
import org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds.NameIdDTOComparator;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.config.TestExecConfig;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.auditable.ExperimentDTO;
import org.gobiiproject.gobiimodel.dto.auditable.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.auditable.PlatformDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ProjectDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ReferenceDTO;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.CvDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.CvGroupDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.MarkerDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.DnaSampleDTO;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiFilterType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by VCalaminos on 1/15/2018.
 */

@Ignore
public class DtoCrudRequestNameIdListTest {

    //private static Logger LOGGER = LoggerFactory.getLogger(DtoCrudRequestNameIdListTest.class);


    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.authenticate());

        // do an ADL load
        GobiiTestConfiguration gobiiTestConfiguration = new GobiiTestConfiguration();
        TestExecConfig testExecConfig = gobiiTestConfiguration.getConfigSettings().getTestExecConfig();
        ADLEncapsulator adlEncapsulator = new ADLEncapsulator();

        File tempDir = adlEncapsulator.setUpAdlTest(testExecConfig);

        Assert.assertNotNull(adlEncapsulator.getErrorMsg(), tempDir);

        File fileFromRepo = new File("src/test/resources/nameIdListLoadTest");

        adlEncapsulator.copyFilesToLocalDir(fileFromRepo, tempDir);

        adlEncapsulator.setInputDirectory(tempDir.getAbsolutePath());
        Assert.assertTrue(adlEncapsulator.getErrorMsg(), adlEncapsulator.executeBatchGobiiADL());

    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }

    private PayloadEnvelope<CvDTO> createCv(CvDTO newCvDTO) throws Exception {

        PayloadEnvelope<CvDTO> cvCreatePayloadEnvelope = new PayloadEnvelope<>(newCvDTO, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<CvDTO, CvDTO> cvCreateGobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_CV));
        PayloadEnvelope<CvDTO> cvCreateResultEnvelope = cvCreateGobiiEnvelopeRestResource.post(CvDTO.class,
                cvCreatePayloadEnvelope);


        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(cvCreateResultEnvelope.getHeader()));
        CvDTO cvDTOResponse = cvCreateResultEnvelope.getPayload().getData().get(0);
        Assert.assertNotEquals(null, cvDTOResponse);
        Assert.assertTrue(cvDTOResponse.getCvId() > 0);

        return cvCreateResultEnvelope;

    }

    private void checkNameIdListResponseAll(PayloadEnvelope<NameIdDTO> responsePayloadEnvelope, List<NameIdDTO> nameIdDTOListInput, String[] nameIdDTONotExisting) {

        // Convert String Array to List
        List<String> nameIdDTONotExistingList = Arrays.asList(nameIdDTONotExisting);

        Assert.assertFalse(responsePayloadEnvelope.getHeader().getStatus().getStatusMessages().toString(),TestUtils.checkAndPrintHeaderMessages(responsePayloadEnvelope.getHeader()));

        List<NameIdDTO> nameIdDTOListResponse = responsePayloadEnvelope.getPayload().getData();
        Assert.assertNotEquals(null, nameIdDTOListResponse);
        Assert.assertEquals(nameIdDTOListInput.size(), nameIdDTOListResponse.size());

        for (NameIdDTO currentNameIdDTO : nameIdDTOListResponse) {

            if (nameIdDTONotExistingList.contains(currentNameIdDTO.getName())) {

                Assert.assertTrue(currentNameIdDTO.getId() <= 0);
            } else {

                Assert.assertTrue(currentNameIdDTO.getId() > 0);

            }
        }
    }


    private void searchNameIdDTO(List<NameIdDTO> nameIdDTOListResponse, String[] nameListSearch, boolean doesExist) {

        Collections.sort(nameIdDTOListResponse);
        Integer index;

        for (String name : nameListSearch) {

            NameIdDTO searchNameDTO = new NameIdDTO();
            searchNameDTO.setName(name);

            NameIdDTOComparator nameIdDTOComparator = new NameIdDTOComparator();
            index = Collections.binarySearch(nameIdDTOListResponse, searchNameDTO, nameIdDTOComparator);

            if (doesExist) {
                Assert.assertTrue(index > -1);
            } else {
                Assert.assertTrue(index <= -1);
            }
        }

    }


    private void checkNameIdListResponseAbsent(PayloadEnvelope<NameIdDTO> responsePayloadEnvelope, String[] nameIdDTOListInput, String[] nameIdDTONotExisting) {

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(responsePayloadEnvelope.getHeader()));

        List<NameIdDTO> nameIdDTOListResponse = responsePayloadEnvelope.getPayload().getData();
        Assert.assertNotEquals(null, nameIdDTOListResponse);
        Assert.assertEquals(nameIdDTOListInput.length, nameIdDTOListResponse.size());

        for (NameIdDTO nameIdDTO : nameIdDTOListResponse) {

            Assert.assertTrue(nameIdDTO.getId() == 0);

        }

        // check all the names that should be present in the list
        searchNameIdDTO(nameIdDTOListResponse, nameIdDTOListInput, true);


        // check all the names that should not be present in the list
        searchNameIdDTO(nameIdDTOListResponse, nameIdDTONotExisting, false);

    }

    private void checkNameIdListResponseExists(PayloadEnvelope<NameIdDTO> responsePayloadEnvelope, String[] nameIdDTOListInput, String[] nameIdDTONotExisting) {

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(responsePayloadEnvelope.getHeader()));

        List<NameIdDTO> nameIdDTOListResponse = responsePayloadEnvelope.getPayload().getData();
        Assert.assertNotEquals(null, nameIdDTOListResponse);
        Assert.assertEquals(nameIdDTOListInput.length, nameIdDTOListResponse.size());

        for (NameIdDTO nameIdDTO : nameIdDTOListResponse) {

            Assert.assertTrue(nameIdDTO.getId() > 0);
        }


        // check all the names that should be present in the list
        searchNameIdDTO(nameIdDTOListResponse, nameIdDTOListInput, true);

        // check all the names that should not be present in the list
        searchNameIdDTO(nameIdDTOListResponse, nameIdDTONotExisting, false);

    }

    private Integer getCvGroupIdByGroupName(String cvGroupName) throws Exception {

        // get cvGroupId
        RestUri restUriCvGroupDetails = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .cvGroupByQueryParams()
                .setParamValue("groupName", cvGroupName)
                .setParamValue("cvGroupTypeId", GobiiCvGroupType.GROUP_TYPE_USER.getGroupTypeId().toString());

        GobiiEnvelopeRestResource<CvGroupDTO, CvGroupDTO> cvGroupDTOGobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriCvGroupDetails);
        PayloadEnvelope<CvGroupDTO> cvGroupDTOResultEnvelope = cvGroupDTOGobiiEnvelopeRestResource.get(CvGroupDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(cvGroupDTOResultEnvelope.getHeader()));
        CvGroupDTO cvGroupDTO = cvGroupDTOResultEnvelope.getPayload().getData().get(0);
        Assert.assertTrue(cvGroupDTO.getCvGroupId() > 0);
        Assert.assertEquals(cvGroupDTO.getName(), cvGroupName);

        Integer cvGroupId = cvGroupDTO.getCvGroupId();

        return cvGroupId;
    }

    public static MarkerDTO createMarkerForTest(MarkerDTO markerDTO) throws Exception {

        RestUri createMarkerUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_MARKERS);
        GobiiEnvelopeRestResource<MarkerDTO,MarkerDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(createMarkerUri);
        PayloadEnvelope<MarkerDTO> resultEnvelope = gobiiEnvelopeRestResource
                .post(MarkerDTO.class, new PayloadEnvelope<>(markerDTO, GobiiProcessType.CREATE));

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        MarkerDTO markerDTOResponse = resultEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, markerDTOResponse);
        Assert.assertNotNull(markerDTOResponse.getMarkerName());
        Assert.assertNotNull(markerDTOResponse.getPlatformId());
        Assert.assertTrue(markerDTOResponse.getPlatformId() > 0);
        Assert.assertTrue(markerDTOResponse.getMarkerId() > 0);

        return markerDTOResponse;
    }

    private PlatformDTO createPlatformForTest() throws Exception {

        PlatformDTO platformDTO = new PlatformDTO();
        platformDTO.setPlatformName(UUID.randomUUID().toString());
        platformDTO.setPlatformCode(UUID.randomUUID().toString());
        platformDTO.setTypeId(1);
        platformDTO.setStatusId(1);
        platformDTO.setCreatedBy(1);

        RestUri createPlatformUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_PLATFORM);
        PayloadEnvelope<PlatformDTO> payloadEnvelope = new PayloadEnvelope<>(platformDTO, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<PlatformDTO, PlatformDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(createPlatformUri);
        PayloadEnvelope<PlatformDTO> responseEnvelope = gobiiEnvelopeRestResource.post(PlatformDTO.class, payloadEnvelope);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(responseEnvelope.getHeader()));
        PlatformDTO platformDTOResponse = responseEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, platformDTOResponse);
        Assert.assertTrue(platformDTOResponse.getPlatformId() > 0);
        Assert.assertNotNull(platformDTOResponse.getPlatformName());

        return platformDTOResponse;
    }

    private List<CvDTO> createCvTerms(Integer cvGroupId) throws Exception {
        return createCvTerms(cvGroupId, false);
    }

    private List<CvDTO> createCvTerms(Integer cvGroupId, Boolean withDuplicates) throws Exception {

        // create list of cv terms

        CvDTO newCvDTO1 = TestDtoFactory.makePopulatedCvDTO(GobiiProcessType.CREATE, 1);
        newCvDTO1.setGroupId(cvGroupId);

        CvDTO newCvDTO2 = TestDtoFactory.makePopulatedCvDTO(GobiiProcessType.CREATE, 1);
        newCvDTO2.setGroupId(cvGroupId);

        CvDTO newCvDTO3 = TestDtoFactory.makePopulatedCvDTO(GobiiProcessType.CREATE, 1);
        newCvDTO3.setGroupId(cvGroupId);

        RestUri restUriCvGroup = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_CVGROUP)
                .addUriParam("groupId")
                .setParamValue("groupId", cvGroupId.toString())
                .appendSegment(RestResourceId.GOBII_CV);

        GobiiEnvelopeRestResource<CvDTO, CvDTO> cvDTOGobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriCvGroup);
        PayloadEnvelope<CvDTO> cvDTOResultEnvelope = cvDTOGobiiEnvelopeRestResource.get(CvDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(cvDTOResultEnvelope.getHeader()));
        List<CvDTO> existingCvDTOList = cvDTOResultEnvelope.getPayload().getData();

        List<String> existingCvTerms = new ArrayList<>();

        if (existingCvDTOList.size() > 0) {

            for (CvDTO currentCvDTO : existingCvDTOList) {
                if (!currentCvDTO.getTerm().equals(null)) {
                    existingCvTerms.add(currentCvDTO.getTerm());
                }
            }
        }

        // check if cv terms already exists; if FALSE, create new CvDTO

        if (!existingCvTerms.contains(newCvDTO1.getTerm())) {

            createCv(newCvDTO1);
        }

        if (!existingCvTerms.contains(newCvDTO2.getTerm())) {

            createCv(newCvDTO2);
        }

        if (!existingCvTerms.contains(newCvDTO3.getTerm())) {
            createCv(newCvDTO3);
        }

        List<CvDTO> cvDTOList = new ArrayList<>();
        cvDTOList.add(newCvDTO1);

        if (withDuplicates) {
            cvDTOList.add(newCvDTO1);
        }

        cvDTOList.add(newCvDTO2);
        cvDTOList.add(newCvDTO3);

        return cvDTOList;
    }

    private List<NameIdDTO> createNameIdDTOList(List<CvDTO> cvDTOList) {

        List<NameIdDTO> nameIdDTOList = new ArrayList<>();

        for (CvDTO cvDTO : cvDTOList) {

            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName(cvDTO.getTerm());

            nameIdDTOList.add(nameIdDTO);
        }

        return nameIdDTOList;

    }


    private PayloadEnvelope<NameIdDTO> getNamesByNameList(List<NameIdDTO> nameIdDTOList, GobiiEntityNameType gobiiEntityNameType, GobiiFilterType gobiiFilterType, String filterValue) throws Exception {

        PayloadEnvelope<NameIdDTO> payloadEnvelope = new PayloadEnvelope<>();
        payloadEnvelope.getHeader().setGobiiProcessType(GobiiProcessType.CREATE);
        payloadEnvelope.getPayload().setData(nameIdDTOList);

        RestUri namesUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .nameIdListByQueryParams();
        GobiiEnvelopeRestResource<NameIdDTO, NameIdDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(namesUri);
        namesUri.setParamValue("entity", gobiiEntityNameType.toString().toLowerCase());
        namesUri.setParamValue("filterType", StringUtils.capitalize(gobiiFilterType.toString().toUpperCase()));
        namesUri.setParamValue("filterValue", filterValue);

        PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = gobiiEnvelopeRestResource.post(NameIdDTO.class, payloadEnvelope);

        return responsePayloadEnvelope;
    }


    private CvDTO createNotExistingCv() {

        String notExistingCvTerm = "notexisting_cvterm-" + UUID.randomUUID().toString();
        CvDTO notExistingCvDTO = new CvDTO();
        notExistingCvDTO.setTerm(notExistingCvTerm);

        return notExistingCvDTO;
    }

    @Test
    public void testGetCvTermsForGermplasmSpeciesAndNameList() throws Exception {

        String cvGroupName = CvGroupTerm.CVGROUP_GERMPLASM_SPECIES.getCvGroupName();
        Integer cvGroupId = getCvGroupIdByGroupName(cvGroupName);

        List<CvDTO> cvDTOList = createCvTerms(cvGroupId);

        CvDTO notExistingCvDto = createNotExistingCv();

        List<CvDTO> cvDTOListInput = new ArrayList<>();
        cvDTOListInput.add(notExistingCvDto);
        cvDTOListInput.addAll(cvDTOList);

        List<NameIdDTO> nameIdDTOList = createNameIdDTOList(cvDTOListInput);

        String[] nameAbsent = new String[]{notExistingCvDto.getTerm()};

        PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, GobiiEntityNameType.CV, GobiiFilterType.NAMES_BY_NAME_LIST, cvGroupName);

        checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, nameAbsent);
    }

    @Test
    public void testGetCvTermsForGermplasmTypeAndNameList() throws Exception {

        String cvGroupName = CvGroupTerm.CVGROUP_GERMPLASM_TYPE.getCvGroupName();
        Integer cvGroupId = getCvGroupIdByGroupName(cvGroupName);

        List<CvDTO> cvDTOList = createCvTerms(cvGroupId);

        CvDTO notExistingCvDto = createNotExistingCv();
        cvDTOList.add(notExistingCvDto);

        List<NameIdDTO> nameIdDTOList = createNameIdDTOList(cvDTOList);

        String[] nameAbsent = new String[]{notExistingCvDto.getTerm()};

        PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, GobiiEntityNameType.CV, GobiiFilterType.NAMES_BY_NAME_LIST, cvGroupName);

        checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, nameAbsent);

    }

    @Test
    public void testGetCvTermsForMarkerStrandAndNameList() throws Exception {

        String cvGroupName = CvGroupTerm.CVGROUP_MARKER_STRAND.getCvGroupName();
        Integer cvGroupId = getCvGroupIdByGroupName(CvGroupTerm.CVGROUP_MARKER_STRAND.getCvGroupName());

        List<CvDTO> cvDTOList = createCvTerms(cvGroupId);

        CvDTO notExistingCvDto = createNotExistingCv();
        cvDTOList.add(notExistingCvDto);

        List<NameIdDTO> nameIdDTOList = createNameIdDTOList(cvDTOList);

        String[] nameAbsent = new String[]{notExistingCvDto.getTerm()};

        PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, GobiiEntityNameType.CV, GobiiFilterType.NAMES_BY_NAME_LIST, cvGroupName);

        checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, nameAbsent);
    }


    @Test
    public void testGetReferencesByNameList() throws Exception {

        ReferenceDTO newReferenceDto1 = TestDtoFactory
                .makePopulatedReferenceDTO(GobiiProcessType.CREATE, 1);
        newReferenceDto1.setName(UUID.randomUUID().toString());

        ReferenceDTO newReferenceDto2 = TestDtoFactory
                .makePopulatedReferenceDTO(GobiiProcessType.CREATE, 1);
        newReferenceDto2.setName(UUID.randomUUID().toString());

        ReferenceDTO newReferenceDto3 = TestDtoFactory
                .makePopulatedReferenceDTO(GobiiProcessType.CREATE, 1);
        newReferenceDto3.setName(UUID.randomUUID().toString());

        //ReferenceDTO notExistingReferenceDto =  
            TestDtoFactory
                .makePopulatedReferenceDTO(GobiiProcessType.CREATE, 1); //TODO: this should be an assertion?

        // create reference 1
        PayloadEnvelope<ReferenceDTO> payloadEnvelopeReference1 = new PayloadEnvelope<>(newReferenceDto1, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<ReferenceDTO, ReferenceDTO> gobiiEnvelopeRestResourceReference1 = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_REFERENCE));
        PayloadEnvelope<ReferenceDTO> referenceDTO1ResponseEnvelope = gobiiEnvelopeRestResourceReference1.post(ReferenceDTO.class,
                payloadEnvelopeReference1);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(referenceDTO1ResponseEnvelope.getHeader()));

        ReferenceDTO referenceDTO1Response = referenceDTO1ResponseEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, referenceDTO1Response);
        Assert.assertTrue(referenceDTO1Response.getReferenceId() > 0);

        // create reference 2
        PayloadEnvelope<ReferenceDTO> payloadEnvelopeReference2 = new PayloadEnvelope<>(newReferenceDto2, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<ReferenceDTO, ReferenceDTO> gobiiEnvelopeRestResourceReference2 = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_REFERENCE));
        PayloadEnvelope<ReferenceDTO> referenceDTO2ResponseEnvelope = gobiiEnvelopeRestResourceReference2.post(ReferenceDTO.class,
                payloadEnvelopeReference2);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(referenceDTO2ResponseEnvelope.getHeader()));

        ReferenceDTO referenceDTO2Response = referenceDTO2ResponseEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, referenceDTO2Response);
        Assert.assertTrue(referenceDTO2Response.getReferenceId() > 0);

        // create reference 3
        PayloadEnvelope<ReferenceDTO> payloadEnvelopeReference3 = new PayloadEnvelope<>(newReferenceDto3, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<ReferenceDTO, ReferenceDTO> gobiiEnvelopeRestResourceReference3 = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_REFERENCE));
        PayloadEnvelope<ReferenceDTO> referenceDTO3ResponseEnvelope = gobiiEnvelopeRestResourceReference3.post(ReferenceDTO.class,
                payloadEnvelopeReference3);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(referenceDTO3ResponseEnvelope.getHeader()));

        ReferenceDTO referenceDTO3Response = referenceDTO3ResponseEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, referenceDTO3Response);
        Assert.assertTrue(referenceDTO3Response.getReferenceId() > 0);

        // create NameIdDTOs; 2 existing in the database, and 1 not existing

        NameIdDTO nameIdDTO1 = new NameIdDTO();
        nameIdDTO1.setName(newReferenceDto1.getName());

        NameIdDTO nameIdDTO2 = new NameIdDTO();
        nameIdDTO2.setName(newReferenceDto2.getName());

        NameIdDTO nameIdDTO3 = new NameIdDTO();
        nameIdDTO3.setName(newReferenceDto3.getName());

        String notExistingReference = "notexisting_reference-" + UUID.randomUUID().toString();

        String[] nameAbsent = new String[]{notExistingReference};

        NameIdDTO nameIdDTONotExisting = new NameIdDTO();
        nameIdDTONotExisting.setName(notExistingReference);

        List<NameIdDTO> nameIdDTOList = new ArrayList<>();
        nameIdDTOList.add(nameIdDTONotExisting);
        nameIdDTOList.add(nameIdDTO1);
        nameIdDTOList.add(nameIdDTO2);
        nameIdDTOList.add(nameIdDTO3);

        PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, GobiiEntityNameType.REFERENCE, GobiiFilterType.NAMES_BY_NAME_LIST, "test");

        checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, nameAbsent);

    }


    @Test
    public void testWithDuplicateNames() throws Exception {

        String cvGroupName = CvGroupTerm.CVGROUP_GERMPLASM_SPECIES.getCvGroupName();
        Integer cvGroupId = getCvGroupIdByGroupName(cvGroupName);

        List<CvDTO> cvDTOList = createCvTerms(cvGroupId, true);

        List<NameIdDTO> nameIdDTOList = createNameIdDTOList(cvDTOList);

        PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, GobiiEntityNameType.CV, GobiiFilterType.NAMES_BY_NAME_LIST, cvGroupName);


        Assert.assertTrue("The error message should contain 'There were duplicate values in the list'",
                responsePayloadEnvelope.getHeader()
                        .getStatus()
                        .getStatusMessages()
                        .stream()
                        .filter(m -> m.getMessage().contains("There were duplicate values in the list"))
                        .count()
                        > 0);

        Assert.assertTrue(responsePayloadEnvelope.getPayload().getData().size() == 0);

    }

    /**
     *  Get project ID loaded by ADL
     *
     * @return Integer project ID
     */

    private Integer getProjectId() throws Exception {

        Integer projectId = null;

        String projectName = "sim_dominant_proj_01";

        RestUri restUriProject = GobiiClientContext.getInstance(null, false).getUriFactory().resourceColl(RestResourceId.GOBII_PROJECTS);
        GobiiEnvelopeRestResource<ProjectDTO, ProjectDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriProject);
        PayloadEnvelope<ProjectDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(ProjectDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<ProjectDTO> projectDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(projectDTOList);
        Assert.assertTrue(projectDTOList.size() > 0);
        Assert.assertNotNull(projectDTOList.get(0).getProjectName());

        for (ProjectDTO projectDTO : projectDTOList) {

            if (projectDTO.getProjectName().equals(projectName)) {

                projectId = projectDTO.getProjectId();
                break;

            }

        }


        Assert.assertNotNull("Project Id not found for project name: " + projectName, projectId);


        return projectId;
    }


    private List<NameIdDTO> getDnaSampleNamesDTOList (String[] dnaSampleNamesExisting, String[] dnaSampleNamesAbsent, boolean addNum) {

        List<NameIdDTO> nameIdDTOList = new ArrayList<>();

        String paramName = "dnaSampleNum";
        for (String dnaSampleName : dnaSampleNamesAbsent) {

            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName(dnaSampleName);

            if (addNum) {

                DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
                dnaSampleDTO.setDnaSampleNum(1);

                nameIdDTO.getParameters().put(paramName, dnaSampleDTO.getDnaSampleNum());
            }

            nameIdDTOList.add(nameIdDTO);
        }

        Integer dnaSampleNum = 1;

        for (String dnaSampleName : dnaSampleNamesExisting) {

            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName(dnaSampleName);

            if (addNum) {

                DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
                dnaSampleDTO.setDnaSampleNum(dnaSampleNum++);

                nameIdDTO.getParameters().put(paramName, dnaSampleDTO.getDnaSampleNum());

            }

            nameIdDTOList.add(nameIdDTO);
        }


        return nameIdDTOList;
    }


    @Test
    public void testGetDnaSampleNamesByList() throws Exception {

        // get project ID for loaded dna samples by ADL

        if ( TestUtils.isBackEndSupported()) {

            Integer projectId = getProjectId();

            GobiiEntityNameType gobiiEntityNameType = GobiiEntityNameType.DNASAMPLE;

            String[] dnasampleNamesExisting = new String[]{"dnasamplename_Deb_FQ_dom_1", "dnasamplename_Deb_FQ_dom_2", "dnasamplename_Deb_FQ_dom_3", "dnasamplename_Deb_FQ_dom_4"};
            String[] dnasampleNamesAbsent = new String[]{"notdnasample1", "notdnasample2", "notdnasample3"};

            List<NameIdDTO> nameIdDTOList = getDnaSampleNamesDTOList(dnasampleNamesExisting, dnasampleNamesAbsent, false);

            /**
             * Test get DNA Sample Names by NAMES_BY_NAME_LIST
             * */
            GobiiFilterType gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST;

            PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, projectId.toString());

            checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, dnasampleNamesAbsent);

            /**
             * Test get DNA Sample Names by NAMES_BY_NAME_LIST_RETURN_ABSENT
             * Will return items on the list that are not found in the database
             */
            gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_ABSENT;

            responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, projectId.toString());

            checkNameIdListResponseAbsent(responsePayloadEnvelope, dnasampleNamesAbsent, dnasampleNamesExisting);


            /**
             * Test get DNA Sample Names by NAMES_BY_NAMES_LIST_RETURN_EXISTS
             * Will return items on the list that are existing in the database
             */
            gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS;

            responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, projectId.toString());

            checkNameIdListResponseExists(responsePayloadEnvelope, dnasampleNamesExisting, dnasampleNamesAbsent);

        }

    }

    @Test
    public void testGetDnaSampleNamesByListAndNum() throws Exception {

        // get project ID for loaded dna samples by ADL
        if( TestUtils.isBackEndSupported()) {

            Integer projectId = getProjectId();

            GobiiEntityNameType gobiiEntityNameType = GobiiEntityNameType.DNASAMPLE;

            String[] dnasampleNamesExisting = new String[]{"dnasamplename_Deb_FQ_dom_1", "dnasamplename_Deb_FQ_dom_2", "dnasamplename_Deb_FQ_dom_3", "dnasamplename_Deb_FQ_dom_4"};
            String[] dnasampleNamesAbsent = new String[]{"notdnasample1", "notdnasample2", "notdnasample3"};

            List<NameIdDTO> nameIdDTOList = getDnaSampleNamesDTOList(dnasampleNamesExisting, dnasampleNamesAbsent, true);

            /**
             * Test get DNA Sample Names by NAMES_BY_NAME_LIST
             * */
            GobiiFilterType gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST;

            PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, projectId.toString());

            checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, dnasampleNamesAbsent);

            /**
             * Test get DNA Sample Names by NAMES_BY_NAME_LIST_RETURN_ABSENT
             * Will return items on the list that are not found in the database
             */
            gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_ABSENT;

            responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, projectId.toString());

            checkNameIdListResponseAbsent(responsePayloadEnvelope, dnasampleNamesAbsent, dnasampleNamesExisting);


            /**
             * Test get DNA Sample Names by NAMES_BY_NAMES_LIST_RETURN_EXISTS
             * Will return items on the list that are existing in the database
             */
            gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS;

            responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, projectId.toString());

            checkNameIdListResponseExists(responsePayloadEnvelope, dnasampleNamesExisting, dnasampleNamesAbsent);

        }
    }

    @Test
    public void testGetMarkerNamesByList() throws Exception {

        // create markers for test

        MarkerDTO markerDTO1 = TestDtoFactory
                .makeMarkerDTO(UUID.randomUUID().toString());

        Integer platformId = markerDTO1.getPlatformId();

        MarkerDTO markerDTO2 = TestDtoFactory
                .makeMarkerDTO(UUID.randomUUID().toString());
        markerDTO2.setPlatformId(platformId);

        MarkerDTO markerDTO3 = TestDtoFactory
                .makeMarkerDTO(UUID.randomUUID().toString());
        markerDTO3.setPlatformId(platformId);


        markerDTO1 = createMarkerForTest(markerDTO1);
        markerDTO2 = createMarkerForTest(markerDTO2);
        markerDTO3 = createMarkerForTest(markerDTO3);

        GobiiEntityNameType gobiiEntityNameType = GobiiEntityNameType.MARKER;

        String[] markerNameExisting = new String[]{markerDTO1.getMarkerName(), markerDTO2.getMarkerName(), markerDTO3.getMarkerName()};

        List<NameIdDTO> nameIdDTOList = new ArrayList<>();


        String[] markerNameAbsent = new String[]{"absent-marker1", "absent-marker2", "absent-marker3"};

        for (String markerName : markerNameAbsent) {

            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName(markerName);

            nameIdDTOList.add(nameIdDTO);
        }

        for (String markerName : markerNameExisting) {

            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName(markerName);

            nameIdDTOList.add(nameIdDTO);
        }


        /**
         * Test get Marker names by NAMES_BY_NAME_LIST
         */
        GobiiFilterType gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST;

        PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, platformId.toString());

        checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, markerNameAbsent);

        /**
         * Test get Marker names by NAMES_BY_NAME_LIST_RETURN_ABSENT
         * Will return items on the list that are not found in the database
         */
        gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_ABSENT;

        responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, platformId.toString());

        checkNameIdListResponseAbsent(responsePayloadEnvelope, markerNameAbsent, markerNameExisting);

        /**
         * Test get Marker names by NAMES_BY_NAME_LIST_RETURN_EXISTS
         */
        gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS;

        responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, platformId.toString());

        checkNameIdListResponseExists(responsePayloadEnvelope, markerNameExisting, markerNameAbsent);

    }

    @Test
    public void testGetPlatformNamesByList() throws Exception {

        // create platforms for test
        PlatformDTO platformDTO1 = createPlatformForTest();
        PlatformDTO platformDTO2 = createPlatformForTest();
        PlatformDTO platformDTO3 = createPlatformForTest();

        GobiiEntityNameType gobiiEntityNameType = GobiiEntityNameType.PLATFORM;

        List<NameIdDTO> nameIdDTOList = new ArrayList<>();

        String[] platformNameAbsent = new String[]{"absent-platform1", "absent-platform2", "absent-platform3"};

        for (String platformName : platformNameAbsent) {

            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName(platformName);

            nameIdDTOList.add(nameIdDTO);

        }

        String[] platformNameExisting = new String[]{platformDTO1.getPlatformName(), platformDTO2.getPlatformName(), platformDTO3.getPlatformName()};

        for (String platformName : platformNameExisting) {

            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName(platformName);

            nameIdDTOList.add(nameIdDTO);
        }

        GobiiFilterType gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST;

        PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, "test");

        checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, platformNameAbsent);

    }

    /**
     * This test is ignored since right now we don't have a separate web service to create linkage groups that can be used for this test.
     **/

    @Test
    public void testGetLinkageGroupNamesByList() throws Exception {

        // get mapset ID for loaded linkage group names by ADL
        if( TestUtils.isBackEndSupported()) {

            String mapsetName = "Test_Physical";
            Integer mapsetId = null;

            RestUri restUriMapset = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceColl(RestResourceId.GOBII_MAPSET);
            GobiiEnvelopeRestResource<MapsetDTO, MapsetDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriMapset);
            PayloadEnvelope<MapsetDTO> resultEnvelope = gobiiEnvelopeRestResource
                    .get(MapsetDTO.class);

            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
            List<MapsetDTO> mapsetDTOList = resultEnvelope.getPayload().getData();
            Assert.assertNotNull(mapsetDTOList);
            Assert.assertTrue(mapsetDTOList.size() > 0);
            Assert.assertNotNull(mapsetDTOList.get(0).getName());

            for (MapsetDTO mapsetDTO : mapsetDTOList) {

                if (mapsetDTO.getName().equals(mapsetName)) {

                    mapsetId = mapsetDTO.getMapsetId();
                    break;
                }

            }

            Assert.assertNotNull("Mapset ID not found for mapset name: " + mapsetName, mapsetId);

            GobiiEntityNameType gobiiEntityNameType = GobiiEntityNameType.LINKAGE_GROUP;

            String[] linkageGroupNameExisting = new String[]{"LG_1", "LG_2", "LG_3", "LG_4"};

            List<NameIdDTO> nameIdDTOList = new ArrayList<>();

            String[] linkageGroupNameAbsent = new String[]{"notlg1", "notlg2", "notlg3"};

            for (String lgName : linkageGroupNameAbsent) {

                NameIdDTO nameIdDTO = new NameIdDTO();
                nameIdDTO.setName(lgName);

                nameIdDTOList.add(nameIdDTO);
            }

            for (String lgName : linkageGroupNameExisting) {

                NameIdDTO nameIdDTO = new NameIdDTO();
                nameIdDTO.setName(lgName);

                nameIdDTOList.add(nameIdDTO);
            }


            GobiiFilterType gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST;

            PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, mapsetId.toString());

            checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, linkageGroupNameAbsent);

        }
    }

    /**
     * This test is ignored since right now we don't have a separate web service to create dna run that can be used for this test.
     **/
    @Test
    public void testGetDnaRunNamesByList() throws Exception {

        if( TestUtils.isBackEndSupported()) {

            // get experiment ID for loaded dna run names by SQL

            String experimentName = "sim_dominant_exp_01";
            Integer experimentId = null;

            RestUri restUriExperiment = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceColl(RestResourceId.GOBII_EXPERIMENTS);
            GobiiEnvelopeRestResource<ExperimentDTO, ExperimentDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriExperiment);
            PayloadEnvelope<ExperimentDTO> resultEnvelope = gobiiEnvelopeRestResource
                    .get(ExperimentDTO.class);

            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
            List<ExperimentDTO> experimentDTOList = resultEnvelope.getPayload().getData();
            Assert.assertNotNull(experimentDTOList);
            Assert.assertTrue(experimentDTOList.size() > 0);
            Assert.assertNotNull(experimentDTOList.get(0).getExperimentName());

            for (ExperimentDTO experimentDTO : experimentDTOList) {

                if (experimentDTO.getExperimentName().equals(experimentName)) {

                    experimentId = experimentDTO.getExperimentId();
                    break;
                }

            }

            Assert.assertNotNull("Experiment ID not found for experiment name: " + experimentName, experimentId);

            GobiiEntityNameType gobiiEntityNameType = GobiiEntityNameType.DNARUN;

            String[] dnaRunNameExisting = new String[]{"dnarunname_Deb_FQ_dom_1", "dnarunname_Deb_FQ_dom_2", "dnarunname_Deb_FQ_dom_3", "dnarunname_Deb_FQ_dom_4"};

            List<NameIdDTO> nameIdDTOList = new ArrayList<>();

            String[] dnaRunNameAbsent = new String[]{"notdnarun1", "notdnarun2", "notdnarun3"};

            for (String dnaRunName : dnaRunNameAbsent) {

                NameIdDTO nameIdDTO = new NameIdDTO();
                nameIdDTO.setName(dnaRunName);

                nameIdDTOList.add(nameIdDTO);
            }

            for (String dnaRunName : dnaRunNameExisting) {

                NameIdDTO nameIdDTO = new NameIdDTO();
                nameIdDTO.setName(dnaRunName);

                nameIdDTOList.add(nameIdDTO);
            }

            GobiiFilterType gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST;

            PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, experimentId.toString());

            checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, dnaRunNameAbsent);

        }

    }


    /**
     *  This test is ignored since right now we don't have a separate web service to create germplasms that can be used for this test.
     **/


    @Test
    public void testGetGermplasmNamesByList() throws Exception {

        if( TestUtils.isBackEndSupported()) {

            GobiiEntityNameType gobiiEntityNameType = GobiiEntityNameType.GERMPLASM;

            String[] germplasmExisting = new String[]{"Deb_FQ_dom_xc_1", "Deb_FQ_dom_xc_2", "Deb_FQ_dom_xc_3", "Deb_FQ_dom_xc_4"};

            List<NameIdDTO> nameIdDTOList = new ArrayList<>();

            String[] germplasmAbsent = new String[]{"notgermplasm1", "notgermplasm2", "notgermplasm3"};

            for (String germplasmCode : germplasmAbsent) {

                NameIdDTO nameIdDTO = new NameIdDTO();
                nameIdDTO.setName(germplasmCode);

                nameIdDTOList.add(nameIdDTO);

            }

            for (String germplasmCode : germplasmExisting) {

                NameIdDTO nameIdDTO = new NameIdDTO();
                nameIdDTO.setName(germplasmCode);

                nameIdDTOList.add(nameIdDTO);

            }

            GobiiFilterType gobiiFilterType = GobiiFilterType.NAMES_BY_NAME_LIST;

            PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = getNamesByNameList(nameIdDTOList, gobiiEntityNameType, gobiiFilterType, "test");

            checkNameIdListResponseAll(responsePayloadEnvelope, nameIdDTOList, germplasmAbsent);

        }
    }
}
