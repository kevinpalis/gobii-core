package org.gobiiproject.gobiiclient.gobii.dbops.crud;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiiapimodel.hateos.Link;
import org.gobiiproject.gobiiapimodel.hateos.LinkCollection;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.gobii.Helpers.*;
import org.gobiiproject.gobiimodel.dto.auditable.OrganizationDTO;
import org.gobiiproject.gobiimodel.dto.auditable.PlatformDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ProtocolDTO;
import org.gobiiproject.gobiimodel.dto.children.EntityPropertyDTO;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.dto.children.VendorProtocolDTO;
import org.gobiiproject.gobiimodel.types.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Angel on 5/9/2016.
 */
public class DtoCrudRequestPlatformTest implements DtoCrudRequestTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.authenticate());
    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }


    @Test
    @Override
    public void create() throws Exception {

        // BEGIN:   ****** THIS PART WILL HAVE TO BE REFACTORED LATER *********
//        DtoRequestNameIdList dtoRequestNameIdList = new DtoRequestNameIdList();
//        NameIdListDTO nameIdListDTORequest = new NameIdListDTO();
//        nameIdListDTORequest.setEntityName("cvgroupterms");
//        nameIdListDTORequest.setFilter("platform_type");
//
//        NameIdListDTO nameIdListDTO = dtoRequestNameIdList.process(nameIdListDTORequest);
        RestUri namesUri = GobiiClientContext.getInstance(null, false).getUriFactory().nameIdListByQueryParams();
        namesUri.setParamValue("entity", GobiiEntityNameType.CV.toString().toLowerCase());
        namesUri.setParamValue("filterType", StringUtils.capitalize(GobiiFilterType.NAMES_BY_TYPE_NAME.toString()));
        namesUri.setParamValue("filterValue", "platform_type");

        GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResourceForPlatformTerms = new GobiiEnvelopeRestResource<>(namesUri);
        PayloadEnvelope<NameIdDTO> resultEnvelope = gobiiEnvelopeRestResourceForPlatformTerms
                .get(NameIdDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<NameIdDTO> platformTypes = resultEnvelope.getPayload().getData();


        List<NameIdDTO> platformProperTerms = new ArrayList<>(platformTypes);
        EntityParamValues entityParamValues = TestDtoFactory
                .makeConstrainedEntityParams(platformProperTerms, 1);
        // END:   ****** THIS PART WILL HAVE TO BE REFACTORED LATER *********


        PlatformDTO newPlatformDto = TestDtoFactory
                .makePopulatedPlatformDTO(GobiiProcessType.CREATE, 1, entityParamValues);

        PayloadEnvelope<PlatformDTO> payloadEnvelope = new PayloadEnvelope<>(newPlatformDto, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<PlatformDTO,PlatformDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false).getUriFactory()
                .resourceColl(RestResourceId.GOBII_PLATFORM));
        PayloadEnvelope<PlatformDTO> platformDTOResponseEnvelope = gobiiEnvelopeRestResource.post(PlatformDTO.class,
                payloadEnvelope);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(platformDTOResponseEnvelope.getHeader()));
        PlatformDTO platformDTOResponse = platformDTOResponseEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, platformDTOResponse);
        Assert.assertTrue(platformDTOResponse.getPlatformId() > 0);
        GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.PLATFORM, platformDTOResponse.getPlatformId());


        RestUri restUriPlatformForGetById = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_PLATFORM);
        restUriPlatformForGetById.setParamValue("id", platformDTOResponse.getPlatformId().toString());
        GobiiEnvelopeRestResource<PlatformDTO,PlatformDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriPlatformForGetById);
        PayloadEnvelope<PlatformDTO> resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                .get(PlatformDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
        PlatformDTO platformDTOResponseForParams = resultEnvelopeForGetByID.getPayload().getData().get(0);


        Assert.assertNotEquals("Parameter collection is null", null, platformDTOResponseForParams.getProperties());
        Assert.assertTrue("No properties were returned",
                platformDTOResponseForParams.getProperties().size() > 0);

        List<EntityPropertyDTO> missing = entityParamValues
                .getMissingEntityProperties(platformDTOResponseForParams.getProperties());

        String missingItems = null;

        if (missing.size() > 0) {

            for (EntityPropertyDTO currentEntityPropDTO : missing) {
                missingItems += "Name: " + currentEntityPropDTO.getPropertyName()
                        + "Value: " + currentEntityPropDTO.getPropertyValue()
                        + "\n";
            }
        }

        Assert.assertNull("There are missing entity property items", missingItems);

        Assert.assertTrue("Parameter values do not match",
                entityParamValues.compare(platformDTOResponseForParams.getProperties()));


    }

    @Test
    @Override
    public void update() throws Exception {


        // BEGIN:   ****** THIS PART WILL HAVE TO BE REFACTORED LATER *********
        //get terms for platform properties:
//        DtoRequestNameIdList dtoRequestNameIdList = new DtoRequestNameIdList();
//        NameIdListDTO nameIdListDTORequest = new NameIdListDTO();
//        nameIdListDTORequest.setEntityName("cvgroupterms");
//        nameIdListDTORequest.setFilter("platform_type");
//        NameIdListDTO nameIdListDTO = dtoRequestNameIdList.process(nameIdListDTORequest);
//        List<NameIdDTO> platformProperTerms = new ArrayList<>(nameIdListDTO
//                .getNamesById());

        RestUri namesUri = GobiiClientContext.getInstance(null, false).getUriFactory().nameIdListByQueryParams();
        namesUri.setParamValue("entity", GobiiEntityNameType.CV.toString().toLowerCase());
        namesUri.setParamValue("filterType", StringUtils.capitalize(GobiiFilterType.NAMES_BY_TYPE_NAME.toString()));
        namesUri.setParamValue("filterValue", "platform_type");

        GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResourceForPlatformTerms = new GobiiEnvelopeRestResource<>(namesUri);
        PayloadEnvelope<NameIdDTO> resultEnvelope = gobiiEnvelopeRestResourceForPlatformTerms
                .get(NameIdDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<NameIdDTO> platformTypes = resultEnvelope.getPayload().getData();
        List<NameIdDTO> platformProperTerms = new ArrayList<>(platformTypes);

        EntityParamValues entityParamValues = TestDtoFactory
                .makeConstrainedEntityParams(platformProperTerms, 1);
        // END:   ****** THIS PART WILL HAVE TO BE REFACTORED LATER *********

        // create a new platform for our test
        PlatformDTO newPlatformDto = TestDtoFactory
                .makePopulatedPlatformDTO(GobiiProcessType.CREATE, 1, entityParamValues);

        PayloadEnvelope<PlatformDTO> payloadEnvelope = new PayloadEnvelope<>(newPlatformDto, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<PlatformDTO,PlatformDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_PLATFORM));
        PayloadEnvelope<PlatformDTO> platformDTOResponseEnvelope = gobiiEnvelopeRestResource.post(PlatformDTO.class,
                payloadEnvelope);
        PlatformDTO newPlatformDTOResponse = platformDTOResponseEnvelope.getPayload().getData().get(0);
        GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.PLATFORM, newPlatformDTOResponse.getPlatformId());

        // re-retrieve the platform we just created so we start with a fresh READ mode dto

        RestUri restUriPlatformForGetById = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_PLATFORM);
        restUriPlatformForGetById.setParamValue("id", newPlatformDTOResponse.getPlatformId().toString());
        GobiiEnvelopeRestResource<PlatformDTO,PlatformDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriPlatformForGetById);
        PayloadEnvelope<PlatformDTO> resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                .get(PlatformDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
        PlatformDTO platformDTOReceived = resultEnvelopeForGetByID.getPayload().getData().get(0);


        // so this would be the typical workflow for the client app
        String newName = UUID.randomUUID().toString();
        platformDTOReceived.setPlatformName(newName);

        EntityPropertyDTO propertyToUpdate = platformDTOReceived.getProperties().get(0);
        String updatedPropertyName = propertyToUpdate.getPropertyName();
        String updatedPropertyValue = UUID.randomUUID().toString();
        propertyToUpdate.setPropertyValue(updatedPropertyValue);

        restUriPlatformForGetById.setParamValue("id", platformDTOReceived.getPlatformId().toString());
        PayloadEnvelope<PlatformDTO> platformDTOResponseEnvelopeUpdate = gobiiEnvelopeRestResourceForGetById.put(PlatformDTO.class,
                new PayloadEnvelope<>(platformDTOReceived, GobiiProcessType.UPDATE));

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(platformDTOResponseEnvelopeUpdate.getHeader()));

        PlatformDTO PlatformDTORequest = platformDTOResponseEnvelopeUpdate.getPayload().getData().get(0);

//        PlatformDTO PlatformDTOResponse = dtoRequestPlatform.process(platformDTOReceived);
//        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(PlatformDTOResponse));

        restUriPlatformForGetById.setParamValue("id", PlatformDTORequest.getPlatformId().toString());
        resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                .get(PlatformDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));


        PlatformDTO dtoRequestPlatformReRetrieved = resultEnvelopeForGetByID.getPayload().getData().get(0);


        Assert.assertTrue(dtoRequestPlatformReRetrieved.getPlatformName().equals(newName));
        EntityPropertyDTO matchedProperty = dtoRequestPlatformReRetrieved
                .getProperties()
                .stream()
                .filter(m -> m.getPropertyName().equals(updatedPropertyName))
                .collect(Collectors.toList())
                .get(0);

        Assert.assertTrue(matchedProperty.getPropertyValue().equals(updatedPropertyValue));
    }


    @Test
    @Override
    public void get() throws Exception {


        // get a list of platforms
        RestUri restUriPlatform = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_PLATFORM);
        GobiiEnvelopeRestResource<PlatformDTO,PlatformDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriPlatform);
        PayloadEnvelope<PlatformDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(PlatformDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<PlatformDTO> platformDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(platformDTOList);
        Assert.assertTrue(platformDTOList.size() > 0);
        Assert.assertNotNull(platformDTOList.get(0).getPlatformName());


        // use an artibrary platform id
        Integer platformId = platformDTOList.get(0).getPlatformId();
        RestUri restUriPlatformForGetById = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_PLATFORM);
        restUriPlatformForGetById.setParamValue("id", platformId.toString());
        GobiiEnvelopeRestResource<PlatformDTO,PlatformDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriPlatformForGetById);
        PayloadEnvelope<PlatformDTO> resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                .get(PlatformDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        PlatformDTO platformDTO = resultEnvelopeForGetByID.getPayload().getData().get(0);
        Assert.assertTrue(platformDTO.getPlatformId() > 0);
        Assert.assertNotNull(platformDTO.getPlatformName());
    }

    @Test
    public void testEmptyResult() throws Exception {

        DtoRestRequestUtils<PlatformDTO> dtoDtoRestRequestUtils =
                new DtoRestRequestUtils<>(PlatformDTO.class, RestResourceId.GOBII_PLATFORM);
        Integer maxId = dtoDtoRestRequestUtils.getMaxPkVal();
        Integer nonExistentId = maxId + 1;


        PayloadEnvelope<PlatformDTO> resultEnvelope
                = dtoDtoRestRequestUtils.getResponseEnvelopeForEntityId(nonExistentId.toString());


        Assert.assertTrue("Request for platformId with ID " + nonExistentId.toString() + " should not have retrieved a result", resultEnvelope.getPayload().getData().size() == 0);
        Assert.assertNotNull(resultEnvelope.getPayload());
        Assert.assertNotNull(resultEnvelope.getPayload().getData());
    }


    @Test
    @Override
    public void getList() throws Exception {

        RestUri restUriPlatform = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_PLATFORM);
        GobiiEnvelopeRestResource<PlatformDTO,PlatformDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriPlatform);
        PayloadEnvelope<PlatformDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(PlatformDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<PlatformDTO> platformDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(platformDTOList);
        Assert.assertTrue(platformDTOList.size() > 0);
        Assert.assertNotNull(platformDTOList.get(0).getPlatformName());


        LinkCollection linkCollection = resultEnvelope.getPayload().getLinkCollection();
        Assert.assertTrue(linkCollection.getLinksPerDataItem().size() == platformDTOList.size());

        List<Integer> itemsToTest = new ArrayList<>();
        if (platformDTOList.size() > 50) {
            itemsToTest = TestUtils.makeListOfIntegersInRange(10, platformDTOList.size());

        } else {
            for (int idx = 0; idx < platformDTOList.size(); idx++) {
                itemsToTest.add(idx);
            }
        }

        for (Integer currentIdx : itemsToTest) {
            PlatformDTO currentPlatformDto = platformDTOList.get(currentIdx);

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
            Assert.assertTrue(currentPlatformDto.getPlatformName().equals(platformDTOFromLink.getPlatformName()));
            Assert.assertTrue(currentPlatformDto.getPlatformId().equals(platformDTOFromLink.getPlatformId()));
        }

    }


    //this test fails on SYS_INT but not with local test
    @Ignore
    public void getPlatformByvendorProtocolId() throws Exception {

        // ************************ create Vendor Protocol entry in the database

        // ********** SET UP THE PROTOCOL
        Integer protocolId = (new GlobalPkColl<DtoCrudRequestProtocolTest>()
                .getAPkVal(DtoCrudRequestProtocolTest.class, GobiiEntityNameType.PROTOCOL));
        RestUri restUriForGetProtocolById = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_PROTOCOL);
        restUriForGetProtocolById.setParamValue("id", protocolId.toString());
        GobiiEnvelopeRestResource<ProtocolDTO,ProtocolDTO> gobiiEnvelopeRestResourceForGetProtocolById =
                new GobiiEnvelopeRestResource<>(restUriForGetProtocolById);
        PayloadEnvelope<ProtocolDTO> resultEnvelopeForGetProtocolByID = gobiiEnvelopeRestResourceForGetProtocolById
                .get(ProtocolDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetProtocolByID.getHeader()));
        ProtocolDTO protocolDTO = resultEnvelopeForGetProtocolByID.getPayload().getData().get(0);
        GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.PROTOCOL, protocolDTO.getProtocolId());

        // ********** CREATE ORGANIZATION FOR THE PROTOCOL

        OrganizationDTO newOrganizationDto = TestDtoFactory
                .makePopulatedOrganizationDTO(GobiiProcessType.CREATE, 1);

        PayloadEnvelope<OrganizationDTO> payloadEnvelope = new PayloadEnvelope<>(newOrganizationDto, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<OrganizationDTO,OrganizationDTO> gobiiEnvelopeRestResourceOrganization = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_ORGANIZATION));
        PayloadEnvelope<OrganizationDTO> organizationDTOResponseEnvelope = gobiiEnvelopeRestResourceOrganization.post(OrganizationDTO.class,
                payloadEnvelope);
        OrganizationDTO organizationDTO = organizationDTOResponseEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, organizationDTO);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(organizationDTOResponseEnvelope.getHeader()));
        Assert.assertTrue(organizationDTO.getOrganizationId() > 0);

        GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.ORGANIZATION,
                organizationDTO.getOrganizationId());


            // CREATE VENDOR-PROTOCOL BY POSTING VENDOR TO PROTOCOL
            // ********** POST VENDOR ORGANIZATION TO PROTOCOL
            // ********** SET VENDOR-PROTOCOL NAME

            String organizationName = organizationDTO.getName();
            String vendorProtocolName = organizationName + "_" + UUID.randomUUID().toString();
            VendorProtocolDTO vendorProtocolDTO = new VendorProtocolDTO(organizationDTO.getOrganizationId(),
                    protocolId,
                    vendorProtocolName);
            organizationDTO.getVendorProtocols().add(vendorProtocolDTO);

            RestUri restUriProtocoLVendor = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .childResourceByUriIdParam(RestResourceId.GOBII_PROTOCOL,
                            RestResourceId.GOBII_VENDORS);
            restUriProtocoLVendor.setParamValue("id", protocolId.toString());
            GobiiEnvelopeRestResource<OrganizationDTO,OrganizationDTO> protocolVendorResource =
                    new GobiiEnvelopeRestResource<>(restUriProtocoLVendor);
            PayloadEnvelope<OrganizationDTO> vendorPayloadEnvelope =
                    new PayloadEnvelope<>(organizationDTO, GobiiProcessType.CREATE);
            PayloadEnvelope<OrganizationDTO> protocolVendorResult =
                    protocolVendorResource.post(OrganizationDTO.class, vendorPayloadEnvelope);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(protocolVendorResult.getHeader()));
            OrganizationDTO postPostOrganizationDTO = protocolVendorResult.getPayload().getData().get(0);
            Assert.assertTrue(postPostOrganizationDTO.getVendorProtocols().size() == 1);
            Assert.assertTrue(postPostOrganizationDTO.getVendorProtocols().get(0).getVendorProtocolId() > 0);
            Assert.assertTrue(postPostOrganizationDTO.getVendorProtocols().get(0).getName().equals(vendorProtocolName));


            // ************ VERIFY THAT VENDOR-PROTOCOL WAS CREATED
            RestUri namesUri = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .nameIdListByQueryParams();
            GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(namesUri);
            namesUri.setParamValue("entity", GobiiEntityNameType.VENDOR_PROTOCOL.toString().toLowerCase());

            PayloadEnvelope<NameIdDTO> resultEnvelopeProtocoLVendornames = gobiiEnvelopeRestResource
                    .get(NameIdDTO.class);

            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeProtocoLVendornames.getHeader()));

            List<NameIdDTO> nameIdDTOs = resultEnvelopeProtocoLVendornames.getPayload().getData();

            Assert.assertTrue(nameIdDTOs.size() > 0);

            Assert.assertTrue(nameIdDTOs
                    .stream()
                    .filter(nameIdDTO -> nameIdDTO.getName().toLowerCase().equals(vendorProtocolName))
                    .count() == 1);

        GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.VENDOR_PROTOCOL, protocolDTO.getProtocolId());

        Integer vendorProtocolId = (new GlobalPkColl<DtoCrudRequestVendorProtocolTest>()
                .getAPkVal(DtoCrudRequestVendorProtocolTest.class, GobiiEntityNameType.VENDOR_PROTOCOL));

        RestUri restUriPlatformForGetByVendorProtocolId = GobiiClientContext.getInstance(null, false).getUriFactory().resourceColl(RestResourceId.GOBII_PLATFORM)
                .appendSegment(RestResourceId.GOBII_PROTOCOL)
                .addUriParam("vendorProtocolId")
                .setParamValue("vendorProtocolId", Integer.toString(vendorProtocolId));

        GobiiEnvelopeRestResource<PlatformDTO,PlatformDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriPlatformForGetByVendorProtocolId);
        PayloadEnvelope<PlatformDTO> resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                .get(PlatformDTO.class);

        PlatformDTO platformDTO = resultEnvelopeForGetByID.getPayload().getData().get(0);
        Assert.assertTrue(platformDTO.getPlatformId() > 0);
        Assert.assertNotNull(platformDTO.getPlatformName());
    }
}
