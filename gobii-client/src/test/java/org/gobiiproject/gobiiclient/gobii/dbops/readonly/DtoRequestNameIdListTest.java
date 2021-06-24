// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaserb
// Create Date:   2016-03-25
// ************************************************************************
package org.gobiiproject.gobiiclient.gobii.dbops.readonly;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiiapimodel.hateos.Link;
import org.gobiiproject.gobiiapimodel.hateos.LinkCollection;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.*;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkColl;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestUtils;
import org.gobiiproject.gobiimodel.dto.auditable.ExperimentDTO;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.dto.auditable.PlatformDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ProtocolDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiFilterType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DtoRequestNameIdListTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.authenticate());

//        (new GlobalPkColl<DtoCrudRequestAnalysisTest>()).getPkVals(DtoCrudRequestAnalysisTest.class,GobiiEntityNameType.ANALYSIS,10);
        (new GlobalPkColl<DtoCrudRequestProjectTest>()).getPkVals(DtoCrudRequestProjectTest.class, GobiiEntityNameType.PROJECT, 10);
        (new GlobalPkColl<DtoCrudRequestMapsetTest>()).getPkVals(DtoCrudRequestMapsetTest.class, GobiiEntityNameType.MAPSET, 10);
        (new GlobalPkColl<DtoCrudRequestContactTest>()).getPkVals(DtoCrudRequestContactTest.class, GobiiEntityNameType.CONTACT, 10);
        (new GlobalPkColl<DtoCrudRequestReferenceTest>()).getPkVals(DtoCrudRequestReferenceTest.class, GobiiEntityNameType.REFERENCE, 10);
        (new GlobalPkColl<DtoCrudRequestDataSetTest>()).getPkVals(DtoCrudRequestDataSetTest.class, GobiiEntityNameType.DATASET, 10);
        (new GlobalPkColl<DtoCrudRequestProtocolTest>()).getPkVals(DtoCrudRequestProtocolTest.class, GobiiEntityNameType.PROTOCOL, 10);
        (new GlobalPkColl<DtoCrudRequestVendorProtocolTest>()).getPkVals(DtoCrudRequestVendorProtocolTest.class, GobiiEntityNameType.VENDOR_PROTOCOL, 10);
    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }

    public static PayloadEnvelope<NameIdDTO> testNameRetrieval(GobiiEntityNameType gobiiEntityNameType,
                                   GobiiFilterType gobiiFilterType,
                                   String filterValue) throws Exception {
        RestUri namesUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .nameIdListByQueryParams();
        GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(namesUri);
        namesUri.setParamValue("entity", gobiiEntityNameType.toString().toLowerCase());

        if (GobiiFilterType.NONE != gobiiFilterType) {
            namesUri.setParamValue("filterType", StringUtils.capitalize(gobiiFilterType.toString().toUpperCase()));
            namesUri.setParamValue("filterValue", filterValue);
        }


        PayloadEnvelope<NameIdDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(NameIdDTO.class);

        String assertionErrorStem = "Error testing name-id retrieval of entity "
                + gobiiEntityNameType.toString();

        if (GobiiFilterType.NONE != gobiiFilterType) {

            assertionErrorStem += " with filter type "
                    + gobiiFilterType.toString()
                    + " and filter value "
                    + filterValue;
        }

        assertionErrorStem += ": ";

        Assert.assertFalse(assertionErrorStem,
                TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

        List<NameIdDTO> nameIdDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(assertionErrorStem
                        + "null name id list",
                nameIdDTOList);

        Assert.assertTrue(assertionErrorStem
                        + "empty name id list",
                nameIdDTOList.size() > 0);

        Assert.assertNotNull(assertionErrorStem
                        + "null name",
                nameIdDTOList.get(0).getName());

        Assert.assertNotNull(assertionErrorStem
                        + "null ",
                nameIdDTOList.get(0).getId());

        Assert.assertTrue(assertionErrorStem
                        + "id <= 0",
                nameIdDTOList.get(0).getId() > 0);

        return resultEnvelope;

    }


    @Test
    public void testGetAnalysisNames() throws Exception {

        testNameRetrieval(GobiiEntityNameType.ANALYSIS, GobiiFilterType.NONE, null);

    } // testGetAnalysisNames()

    @Test
    public void testGetAnalysisNamesByTypeId() throws Exception {

        testNameRetrieval(GobiiEntityNameType.ANALYSIS, GobiiFilterType.NAMES_BY_TYPEID, "1");
    }

    @Test
    public void testGetNamesWithBadEntityValue() throws Exception {

        RestUri namesUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .nameIdListByQueryParams();
        GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(namesUri);

        namesUri.setParamValue("entity", "foo");
        PayloadEnvelope<NameIdDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(NameIdDTO.class);

        Assert.assertTrue("There should be exactly one error for bad entity type",
                1 == resultEnvelope
                        .getHeader()
                        .getStatus()
                        .getStatusMessages()
                        .stream()
                        .filter(m -> m.getMessage().toLowerCase().contains("unsupported entity for list request"))
                        .count());

    }

    @Test
    public void testGetAnalysisNamesByTypeIdErrorBadFilterType() throws Exception {


        RestUri namesUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .nameIdListByQueryParams();
        GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(namesUri);

        namesUri.setParamValue("entity", GobiiEntityNameType.ANALYSIS.toString().toLowerCase());
        namesUri.setParamValue("filterType", "foo");
        namesUri.setParamValue("filterValue", "33");
        PayloadEnvelope<NameIdDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(NameIdDTO.class);

        Assert.assertTrue("There should be exactly one error for the unsupported filter type",
                1 == resultEnvelope
                        .getHeader()
                        .getStatus()
                        .getStatusMessages()
                        .stream()
                        .filter(m -> m.getMessage().toLowerCase().contains("unsupported filter"))
                        .count());

    }

    @Test
    public void testGetAnalysisNamesByTypeIdErrorEmptyFilterValue() throws Exception {


        RestUri namesUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .nameIdListByQueryParams();
        GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(namesUri);

        namesUri.setParamValue("entity", GobiiEntityNameType.ANALYSIS.toString().toLowerCase());
        namesUri.setParamValue("filterType", StringUtils.capitalize(GobiiFilterType.NAMES_BY_TYPEID.toString().toUpperCase()));
        // normally would also specify "filterValue" here

        PayloadEnvelope<NameIdDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(NameIdDTO.class);

        Assert.assertTrue("There should be exactly one error for filter value not supplied",
                1 == resultEnvelope
                        .getHeader()
                        .getStatus()
                        .getStatusMessages()
                        .stream()
                        .filter(m -> m.getMessage().toLowerCase().contains("a value was not supplied for filter"))
                        .count());

    }


    @Test
    public void testGetContactsByIdForContactType() throws Exception {

        testNameRetrieval(GobiiEntityNameType.CONTACT, GobiiFilterType.NAMES_BY_TYPE_NAME, "Admin");

    } // testGetMarkers()

    @Test
    public void testGetContactNames() throws Exception {


        testNameRetrieval(GobiiEntityNameType.CONTACT, GobiiFilterType.NONE, null);


    } // testGetMarkers()

    @Test
    public void testGetProjectNames() throws Exception {


        testNameRetrieval(GobiiEntityNameType.PROJECT, GobiiFilterType.NONE, null);

    } // testGetMarkers()


    @Test
    public void testGetProjectNamesByContactId() throws Exception {

        testNameRetrieval(GobiiEntityNameType.PROJECT, GobiiFilterType.NAMES_BY_TYPEID, "1");
    }

    @Test
    public void testGetExperimentNamesByProjectId() throws Exception {


        Integer projectId = (new GlobalPkColl<DtoCrudRequestProjectTest>().getAPkVal(DtoCrudRequestProjectTest.class, GobiiEntityNameType.PROJECT));

        Integer manifestId = (new GlobalPkColl<DtoCrudRequestManifestTest>().getAPkVal(DtoCrudRequestManifestTest.class, GobiiEntityNameType.MANIFEST));
        Integer idOfProtocolThatHasAVendor = (new GlobalPkColl<DtoCrudRequestVendorProtocolTest>().getAPkVal(DtoCrudRequestVendorProtocolTest.class, GobiiEntityNameType.VENDOR_PROTOCOL));
        //GlobalPkValues.getInstance()
        RestUri namesUri = GobiiClientContext.getInstance(null, false)
            .getUriFactory()
            .nameIdListByQueryParams();
        namesUri.setParamValue("entity", GobiiEntityNameType.VENDOR_PROTOCOL.toString().toLowerCase());
        namesUri.setParamValue("filterType", StringUtils.capitalize(GobiiFilterType.NAMES_BY_TYPEID.toString().toUpperCase()));
        namesUri.setParamValue("filterValue", idOfProtocolThatHasAVendor.toString());
        GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResourceForVendorsProtocol = new GobiiEnvelopeRestResource<>(namesUri);

        PayloadEnvelope<NameIdDTO> resultEnvelopeProtocoLVendornames = gobiiEnvelopeRestResourceForVendorsProtocol
            .get(NameIdDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeProtocoLVendornames.getHeader()));

        List<NameIdDTO> nameIdDTOs = resultEnvelopeProtocoLVendornames.getPayload().getData();
        Integer idOfArbitraryProtocolVendorRecord = nameIdDTOs.get(0).getId();

        //*** SET UP THE EXPERIMENT DTO

        ExperimentDTO experimentDTORequest = new ExperimentDTO();
        // experimentDTORequest.setExperimentId(1);
        experimentDTORequest.setManifestId(manifestId);
        experimentDTORequest.setProjectId(projectId);
        experimentDTORequest.setCreatedBy(1);
        experimentDTORequest.setModifiedBy(1);
        experimentDTORequest.setExperimentCode("foocode");
        experimentDTORequest.setExperimentDataFile("foofile");
        experimentDTORequest.setStatusId(1);
        experimentDTORequest.setExperimentName(UUID.randomUUID().toString());
        experimentDTORequest.setVendorProtocolId(idOfArbitraryProtocolVendorRecord);

        RestUri experimentsUri = GobiiClientContext.getInstance(null, false)
            .getUriFactory()
            .resourceColl(RestResourceId.GOBII_EXPERIMENTS);
        GobiiEnvelopeRestResource<ExperimentDTO,ExperimentDTO> gobiiEnvelopeRestResourceForExperiments = new GobiiEnvelopeRestResource<>(experimentsUri);
        PayloadEnvelope<ExperimentDTO> payloadEnvelope = new PayloadEnvelope<>(experimentDTORequest, GobiiProcessType.CREATE);
        PayloadEnvelope<ExperimentDTO> resultEnvelope = gobiiEnvelopeRestResourceForExperiments
            .post(ExperimentDTO.class, payloadEnvelope);
        testNameRetrieval(GobiiEntityNameType.EXPERIMENT, GobiiFilterType.NAMES_BY_TYPEID, projectId.toString());

    }

    @Test
    public void testGetExperimentNames() throws Exception {


        testNameRetrieval(GobiiEntityNameType.EXPERIMENT, GobiiFilterType.NONE, null);
    }

    @Test
    public void testGetCvTermsByGroup() throws Exception {


        testNameRetrieval(GobiiEntityNameType.CV, GobiiFilterType.NAMES_BY_TYPE_NAME, "status");
    }

    @Test
    public void testGetPlatformNames() throws Exception {


        //testNameRetrieval(GobiiEntityNameType.PLATFORM, GobiiFilterType.NONE, null);
        RestUri namesUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .nameIdListByQueryParams();
        GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(namesUri);
        namesUri.setParamValue("entity", GobiiEntityNameType.PLATFORM.toString().toLowerCase());

        PayloadEnvelope<NameIdDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(NameIdDTO.class);

        List<NameIdDTO> nameIdDTOList = resultEnvelope.getPayload().getData();

        String assertionErrorStem = "Error retrieving Platform Names: ";

        Assert.assertFalse(assertionErrorStem,
                TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

        Assert.assertNotNull(assertionErrorStem
                        + "null name id list",
                nameIdDTOList);

        Assert.assertTrue(assertionErrorStem
                        + "empty name id list",
                nameIdDTOList.size() > 0);

        Assert.assertNotNull(assertionErrorStem
                        + "null name",
                nameIdDTOList.get(0).getName());

        Assert.assertNotNull(assertionErrorStem
                        + "null ",
                nameIdDTOList.get(0).getId());

        Assert.assertTrue(assertionErrorStem
                        + "id <= 0",
                nameIdDTOList.get(0).getId() > 0);


        // verify that we can retrieve platofrmDtos from the links we got for the platform name IDs
        LinkCollection linkCollection = resultEnvelope.getPayload().getLinkCollection();
        Assert.assertTrue(linkCollection.getLinksPerDataItem().size() == nameIdDTOList.size());

        List<Integer> itemsToTest = new ArrayList<>();
        if (nameIdDTOList.size() > 50) {
            itemsToTest = TestUtils.makeListOfIntegersInRange(10, nameIdDTOList.size());

        } else {
            for (int idx = 0; idx < nameIdDTOList.size(); idx++) {
                itemsToTest.add(idx);
            }
        }

        for (Integer currentIdx : itemsToTest) {

            NameIdDTO currentPlatformNameDto = nameIdDTOList.get(currentIdx);

            Link currentLink = linkCollection.getLinksPerDataItem().get(currentIdx);
            RestUri restUriPlatformForGetById = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .RestUriFromUri(currentLink.getHref());
            GobiiEnvelopeRestResource<PlatformDTO,PlatformDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriPlatformForGetById);
            PayloadEnvelope<PlatformDTO> resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                    .get(PlatformDTO.class);
            Assert.assertNotNull(resultEnvelopeForGetByID);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
            PlatformDTO platformDTOFromLink = resultEnvelopeForGetByID.getPayload().getData().get(0);

            Assert.assertTrue(currentPlatformNameDto.getName().equals(platformDTOFromLink.getPlatformName()));
            Assert.assertTrue(currentPlatformNameDto.getId().equals(platformDTOFromLink.getPlatformId()));
        }


    } // testGetMarkers()

    @Test
    public void testGetPlatformNamesByTypeId() throws Exception {

        testNameRetrieval(GobiiEntityNameType.PLATFORM, GobiiFilterType.NAMES_BY_TYPEID, "1");

    } // testGetMarkers()


    @Test
    public void testGetMarkerGroupNames() throws Exception {

        testNameRetrieval(GobiiEntityNameType.MARKER_GROUP, GobiiFilterType.NONE, null);

    } // testGetMarkerGroupNames()

    @Test
    public void testGetReferenceNames() throws Exception {


        testNameRetrieval(GobiiEntityNameType.REFERENCE, GobiiFilterType.NONE, null);

    } // testGetMarkers()

    @Test
    public void testGetMapNames() throws Exception {


        testNameRetrieval(GobiiEntityNameType.MAPSET, GobiiFilterType.NONE, null);

    } // testGetMarkers()

    @Test
    public void testGetMapsSetNamesByType() throws Exception {
        testNameRetrieval(GobiiEntityNameType.MAPSET, GobiiFilterType.NAMES_BY_TYPEID, "1");

    } // testGetMarkers()


    @Test
    public void testGetCvTypes() throws Exception {


        testNameRetrieval(GobiiEntityNameType.CVGROUP, GobiiFilterType.NONE, null);

    } // testGetMarkers()

    @Test
    public void testGetCvNames() throws Exception {


        testNameRetrieval(GobiiEntityNameType.CV, GobiiFilterType.NONE, null);

    } // testGetMarkers()

    @Test
    public void testGetRoles() throws Exception {
        testNameRetrieval(GobiiEntityNameType.ROLE, GobiiFilterType.NONE, null);
    } // testGetMarkers()


    @Test
    public void testGetManifestNames() throws Exception {
        testNameRetrieval(GobiiEntityNameType.MANIFEST, GobiiFilterType.NONE, null);
    }

    @Test
    public void testGetOrganizationNames() throws Exception {


        testNameRetrieval(GobiiEntityNameType.ORGANIZATION, GobiiFilterType.NONE, null);

    }

    @Test
    public void testGetDataSetNamesByExperimentId() throws Exception {


        Integer experimentId = (new GlobalPkColl<DtoCrudRequestExperimentTest>().getAPkVal(DtoCrudRequestExperimentTest.class, GobiiEntityNameType.EXPERIMENT));
        testNameRetrieval(GobiiEntityNameType.DATASET, GobiiFilterType.NAMES_BY_TYPEID, experimentId.toString());

    }

    @Test
    public void testGetDataSetNames() throws Exception {


        testNameRetrieval(GobiiEntityNameType.DATASET, GobiiFilterType.NONE, null);
    }

    @Test
    public void testGetProtocols() throws Exception {

        testNameRetrieval(GobiiEntityNameType.PROTOCOL, GobiiFilterType.NONE, null);
    }

    @Test
    public void testGetProtocolVendors() throws Exception {
        testNameRetrieval(GobiiEntityNameType.VENDOR_PROTOCOL, GobiiFilterType.NONE, null);
    }

    @Test
    public void testGetProtocolVendorsByProtocolId() throws Exception {
        Integer protocolId = (new GlobalPkColl<DtoCrudRequestProtocolTest>()
                .getAPkVal(DtoCrudRequestProtocolTest.class, GobiiEntityNameType.PROTOCOL));

        testNameRetrieval(GobiiEntityNameType.VENDOR_PROTOCOL, GobiiFilterType.NAMES_BY_TYPEID, protocolId.toString());
    }


    @Test
    public void testGetProtocolsByPlatformId() throws Exception {

        RestUri restUriProtocol = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_PROTOCOL);
        GobiiEnvelopeRestResource<ProtocolDTO,ProtocolDTO> restResource = new GobiiEnvelopeRestResource<>(restUriProtocol);
        PayloadEnvelope<ProtocolDTO> resultEnvelope = restResource
                .get(ProtocolDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<ProtocolDTO> protocolDTOList = resultEnvelope.getPayload().getData();

        Integer platformId = null;
        for (Integer idx = 0; (platformId == null) && (idx < protocolDTOList.size()); idx++) {

            ProtocolDTO currentProtocolDTO = protocolDTOList.get(idx);
            if( ( currentProtocolDTO.getPlatformId() != null ) &&
                    currentProtocolDTO.getPlatformId() > 0 ) {
                platformId = currentProtocolDTO.getPlatformId();
            }
        }

        Assert.assertNotNull(platformId);


        testNameRetrieval(GobiiEntityNameType.PROTOCOL, GobiiFilterType.NAMES_BY_TYPEID, platformId.toString());
    }
}
