package org.gobiiproject.gobiiclient.gobii.dbops.crud;

import org.gobiiproject.gobiiapimodel.hateos.LinkCollection;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkColl;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkValues;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestUtils;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.dto.auditable.OrganizationDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ProtocolDTO;
import org.gobiiproject.gobiimodel.dto.children.VendorProtocolDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by VCalaminos on 2016-12-14.
 */

public class DtoCrudRequestVendorProtocolTest implements DtoCrudRequestTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.authenticate());
    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }

    private final Integer TOTAL_VENDORS_PER_PROTOCOL = 10;

    @Test
    @Override
    public void create() throws Exception {

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


        // ********** CREATE MULTIPLE ORGANIZATION FOR THE PROTOCOL
        List<Integer> vendorPkVals = (new GlobalPkColl<DtoCrudRequestOrganizationTest>()
                .getFreshPkVals(DtoCrudRequestOrganizationTest.class, GobiiEntityNameType.ORGANIZATION, TOTAL_VENDORS_PER_PROTOCOL));
        for (Integer currentVendorPk : vendorPkVals) {
            // ********** SET UP THE ORGANIZATION FOR THE VENDOR


            RestUri restUriForGetOrganizationById = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceByUriIdParam(RestResourceId.GOBII_ORGANIZATION);
            restUriForGetOrganizationById.setParamValue("id", currentVendorPk.toString());
            GobiiEnvelopeRestResource<OrganizationDTO,OrganizationDTO> gobiiEnvelopeRestResourceForGetOrganizationById =
                    new GobiiEnvelopeRestResource<>(restUriForGetOrganizationById);
            PayloadEnvelope<OrganizationDTO> resultEnvelopeForGetOrganizationByID = gobiiEnvelopeRestResourceForGetOrganizationById
                    .get(OrganizationDTO.class);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetOrganizationByID.getHeader()));
            OrganizationDTO organizationDTO = resultEnvelopeForGetOrganizationByID.getPayload().getData().get(0);


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


            // ************* VERIFY THAT REPOSTING THE SAME VENDOR TO THE SAME PROTOCOL GIVES A VALIDATION ERROR
            protocolVendorResult =
                    protocolVendorResource.post(OrganizationDTO.class, vendorPayloadEnvelope);

            Assert.assertTrue(
                    protocolVendorResult
                            .getHeader()
                            .getStatus()
                            .getStatusMessages()
                            .stream()
                            .filter(headerStatusMessage ->
                                    headerStatusMessage
                                            .getGobiiStatusLevel().equals(GobiiStatusLevel.VALIDATION) &&
                                            headerStatusMessage
                                                    .getGobiiValidationStatusType()
                                                    .equals(GobiiValidationStatusType.ENTITY_ALREADY_EXISTS)
                            ).count() == 1);
        } // iterate through total vendors for protocol

        GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.VENDOR_PROTOCOL, protocolDTO.getProtocolId());


        // ************* VERIFY THAT WE CAN RETRIEVE THE CREATED VENDOR THROUGH THE PROTOCOL URL
//        RestUri restUriOrganizationThroughProtocol = GobiiClientContext.getInstance(null, false)
//                .getUriFactory()
//                .resourceColl(RestResourceId.GOBII_PROTOCOL)
//                .addUriParam("id")
//                .appendSegment(RestResourceId.GOBII_VENDORS)
//                .addUriParam("vendorProtocolName");
//        restUriOrganizationThroughProtocol.setParamValue("id", protocolId.toString());
//        restUriOrganizationThroughProtocol.setParamValue("vendorProtocolName", predictedVendorProtocolName);
//
//        GobiiEnvelopeRestResource<OrganizationDTO> protocolVendorResourceForOrganizationThroughProtocol =
//                new GobiiEnvelopeRestResource<>(restUriOrganizationThroughProtocol);
//        protocolVendorResult =
//                protocolVendorResourceForOrganizationThroughProtocol.get(OrganizationDTO.class);
//        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(protocolVendorResult.getHeader()));
//        OrganizationDTO organizationDTOThroughProtocol = protocolVendorResult.getPayload().getData().get(0);
//        Assert.assertTrue(organizationDTOThroughProtocol.getOrganizationId().equals(organizationDTO.getOrganizationId()));


        //need services for GET /protocols/{id}/vendors/
        //                  GET /protocols/{id}/vendors/{protocol_vendor_name}

    }


    @Test
    @SuppressWarnings("all")
    public void multiProtocolTest() throws Exception {

        // ********** PRE-CREATE ENTITIES
        GlobalPkColl globalPkColl = new GlobalPkColl<DtoCrudRequestProtocolTest>();
        List<Integer> vendorPkVals = globalPkColl.getFreshPkVals(DtoCrudRequestOrganizationTest.class, GobiiEntityNameType.ORGANIZATION, TOTAL_VENDORS_PER_PROTOCOL);
        List<Integer> protocolPkVals = globalPkColl
                .getFreshPkVals(DtoCrudRequestProtocolTest.class, GobiiEntityNameType.PROTOCOL, TOTAL_VENDORS_PER_PROTOCOL);


        List<OrganizationDTO> finalPostedVendors = new ArrayList<>();
        // ** ITERATE PROTOCOL
        for (Integer currentProtocolId : protocolPkVals) {

            RestUri restUriProtocoLVendor = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .childResourceByUriIdParam(RestResourceId.GOBII_PROTOCOL,
                            RestResourceId.GOBII_VENDORS);
            restUriProtocoLVendor.setParamValue("id", currentProtocolId.toString());
            GobiiEnvelopeRestResource<OrganizationDTO,OrganizationDTO> protocolVendorResource =
                    new GobiiEnvelopeRestResource<>(restUriProtocoLVendor);

            // ** ITERATE VENDORS
            for (Integer currentVendorId : vendorPkVals) {

                // RETRIEVE THE VENDOR
                RestUri restUriForGetOrganizationById = GobiiClientContext.getInstance(null, false)
                        .getUriFactory()
                        .resourceByUriIdParam(RestResourceId.GOBII_ORGANIZATION);
                restUriForGetOrganizationById.setParamValue("id", currentVendorId.toString());
                GobiiEnvelopeRestResource<OrganizationDTO,OrganizationDTO> gobiiEnvelopeRestResourceForGetOrganizationById =
                        new GobiiEnvelopeRestResource<>(restUriForGetOrganizationById);
                PayloadEnvelope<OrganizationDTO> resultEnvelopeForGetOrganizationByID = gobiiEnvelopeRestResourceForGetOrganizationById
                        .get(OrganizationDTO.class);
                Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetOrganizationByID.getHeader()));
                OrganizationDTO organizationDTO = resultEnvelopeForGetOrganizationByID.getPayload().getData().get(0);


                // POST THE VENDOR
                PayloadEnvelope<OrganizationDTO> vendorPayloadEnvelope =
                        new PayloadEnvelope<>(organizationDTO, GobiiProcessType.CREATE);
                PayloadEnvelope<OrganizationDTO> protocolVendorResult =
                        protocolVendorResource.post(OrganizationDTO.class, vendorPayloadEnvelope);
                Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(protocolVendorResult.getHeader()));
                OrganizationDTO postedVendorDTO = protocolVendorResult.getPayload().getData().get(0);

                if (postedVendorDTO.getVendorProtocols().size() == vendorPkVals.size()) {
                    finalPostedVendors.add(postedVendorDTO);
                }
            }
        }

        for (OrganizationDTO currentOrganizationDTO : finalPostedVendors) {

            Assert.assertNotNull(currentOrganizationDTO.getVendorProtocols());
            Assert.assertTrue(currentOrganizationDTO.getVendorProtocols().size() == TOTAL_VENDORS_PER_PROTOCOL);

            for (VendorProtocolDTO currentVendorProtocolDTO : currentOrganizationDTO.getVendorProtocols()) {
                Assert.assertNotNull(currentVendorProtocolDTO.getVendorProtocolId());
                Assert.assertNotNull(currentVendorProtocolDTO.getName());
                Assert.assertTrue(currentOrganizationDTO.getOrganizationId().equals(currentVendorProtocolDTO.getOrganizationId()));
            }

            // verify we get the same result through the plain organization service
            RestUri restUriOrganization = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceByUriIdParam(RestResourceId.GOBII_ORGANIZATION)
                    .setParamValue("id", currentOrganizationDTO.getOrganizationId().toString());

            GobiiEnvelopeRestResource<OrganizationDTO,OrganizationDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriOrganization);
            PayloadEnvelope<OrganizationDTO> resultEnvelope = gobiiEnvelopeRestResource
                    .get(OrganizationDTO.class);

            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

            OrganizationDTO organizationDTOFromDedicatedUrl = resultEnvelope.getPayload().getData().get(0);
            Assert.assertTrue(organizationDTOFromDedicatedUrl.getOrganizationId().equals(currentOrganizationDTO.getOrganizationId()));
            Assert.assertNotNull(organizationDTOFromDedicatedUrl.getVendorProtocols());
            Assert.assertTrue(organizationDTOFromDedicatedUrl.getVendorProtocols().size() ==
                    currentOrganizationDTO.getVendorProtocols().size());



            // UPDATE VENDOR-PROTOCOL
            // verify that we can update with the current organizationDTO
            String newOrgName = UUID.randomUUID().toString();
            String newVendorProtocolName = UUID.randomUUID().toString();

            currentOrganizationDTO.setName(newOrgName);
            VendorProtocolDTO vendorProtocolDTOToUpdate = currentOrganizationDTO.getVendorProtocols().get(0);
            vendorProtocolDTOToUpdate.setName(newVendorProtocolName);

            RestUri restUriProtocoLVendor = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .childResourceByUriIdParam(RestResourceId.GOBII_PROTOCOL,
                            RestResourceId.GOBII_VENDORS);

            restUriProtocoLVendor.setParamValue("id", vendorProtocolDTOToUpdate.getProtocolId().toString());
            GobiiEnvelopeRestResource<OrganizationDTO,OrganizationDTO> protocolVendorResource =
                    new GobiiEnvelopeRestResource<>(restUriProtocoLVendor);
            PayloadEnvelope<OrganizationDTO> vendorPayloadEnvelope =
                    new PayloadEnvelope<>(currentOrganizationDTO, GobiiProcessType.UPDATE);
            PayloadEnvelope<OrganizationDTO> protocolVendorResult =
                    protocolVendorResource.put(OrganizationDTO.class, vendorPayloadEnvelope);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(protocolVendorResult.getHeader()));


            // re-retrieve updated DTO
            restUriOrganization.setParamValue("id", currentOrganizationDTO.getOrganizationId().toString());
            gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriOrganization);
            resultEnvelope = gobiiEnvelopeRestResource.get(OrganizationDTO.class);

            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
            OrganizationDTO updatedVendorDTO = resultEnvelope.getPayload().getData().get(0);

            Assert.assertNotNull(updatedVendorDTO.getVendorProtocols());
            Assert.assertTrue(updatedVendorDTO.getName().equals(newOrgName));
            List<VendorProtocolDTO> updatedVendorProtocolDTOList = updatedVendorDTO
                    .getVendorProtocols()
                    .stream()
                    .filter(vendorProtocolDTO -> vendorProtocolDTO.getProtocolId().equals(vendorProtocolDTOToUpdate.getProtocolId())
                            && vendorProtocolDTOToUpdate.getOrganizationId().equals(vendorProtocolDTOToUpdate.getOrganizationId()))
                    .collect(Collectors.toList());

            Assert.assertTrue(updatedVendorProtocolDTOList.size() == 1);
            VendorProtocolDTO updatedVendorProtocolDTO = updatedVendorProtocolDTOList.get(0);
            Assert.assertNotNull(updatedVendorProtocolDTO);
            Assert.assertTrue(updatedVendorProtocolDTO.getName().equals(newVendorProtocolName));
        }


        Map<Integer, List<VendorProtocolDTO>> vendorProtocolsByProtocolid = new HashMap<>();
        for (OrganizationDTO currentOrganizationDTO : finalPostedVendors) {

            currentOrganizationDTO.getVendorProtocols().forEach(currentVendorProtocol ->
            {
                Integer currentProtocolId = currentVendorProtocol.getProtocolId();
                if (!vendorProtocolsByProtocolid.containsKey(currentProtocolId)) {
                    vendorProtocolsByProtocolid.put(currentProtocolId, new ArrayList<>());
                }

                vendorProtocolsByProtocolid.get(currentProtocolId).add(currentVendorProtocol);
            });
        }

        // verify that we can retrieve the VENDOR-PROTOCOL through the protocol DTO
        for (Map.Entry<Integer, List<VendorProtocolDTO>> currentMapEntry : vendorProtocolsByProtocolid.entrySet()) {

            Integer currentProtocolId = currentMapEntry.getKey();
            List<VendorProtocolDTO> currentVendorProtocolList = currentMapEntry.getValue();

            RestUri restUriProtocolByProtocolId = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceColl(RestResourceId.GOBII_PROTOCOL)
                    .addUriParam("id")
                    .setParamValue("id", currentProtocolId.toString());

            GobiiEnvelopeRestResource<ProtocolDTO,ProtocolDTO> gobiiEnvelopeRestResourceForGetProtocolById =
                    new GobiiEnvelopeRestResource<>(restUriProtocolByProtocolId);
            PayloadEnvelope<ProtocolDTO> resultEnvelopeForGetProtocolByID = gobiiEnvelopeRestResourceForGetProtocolById
                    .get(ProtocolDTO.class);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetProtocolByID.getHeader()));
            ProtocolDTO protocolDTO = resultEnvelopeForGetProtocolByID.getPayload().getData().get(0);
            Assert.assertTrue(currentVendorProtocolList.size() == protocolDTO.getVendorProtocols().size());
            for (VendorProtocolDTO currentVendorProtocolDto : currentVendorProtocolList) {
                Assert.assertTrue(
                        1 == protocolDTO
                                .getVendorProtocols()
                                .stream()
                                .filter(vp -> vp.getName().equals(currentVendorProtocolDto.getName()))
                                .count()
                );
            }

        }

    }


    @Test
    @Override
    public void update() throws Exception {


    }


    @Test
    @Override
    public void testEmptyResult() throws Exception {
    }


    @Test
    @Override
    public void get() throws Exception {

    }


    @Test
    @Override
    public void getList() throws Exception {

        Integer protocolId = (new GlobalPkColl<DtoCrudRequestVendorProtocolTest>()
                .getAPkVal(DtoCrudRequestVendorProtocolTest.class, GobiiEntityNameType.VENDOR_PROTOCOL));

        RestUri restUriVendorsForProtocol = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_PROTOCOL)
                .addUriParam("protocolId")
                .setParamValue("protocolId", protocolId.toString())
                .appendSegment(RestResourceId.GOBII_VENDORS);

        GobiiEnvelopeRestResource<OrganizationDTO,OrganizationDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriVendorsForProtocol);
        PayloadEnvelope<OrganizationDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(OrganizationDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<OrganizationDTO> organizationDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(organizationDTOList);
        Assert.assertTrue(organizationDTOList.size() >= TOTAL_VENDORS_PER_PROTOCOL);
        Assert.assertNotNull(organizationDTOList.get(0).getName());


        LinkCollection linkCollection = resultEnvelope.getPayload().getLinkCollection();
        Assert.assertTrue(linkCollection.getLinksPerDataItem().size() == organizationDTOList.size());

//        List<Integer> itemsToTest = new ArrayList<>();
//        if (organizationDTOList.size() > 50) {
//            itemsToTest = TestUtils.makeListOfIntegersInRange(10, organizationDTOList.size());
//
//        } else {
//            for (int idx = 0; idx < organizationDTOList.size(); idx++) {
//                itemsToTest.add(idx);
//            }
//        }
//
//        for (Integer currentIdx : itemsToTest) {
//            ProjectDTO currentProjectDto = projectDTOList.get(currentIdx);
//
//            Link currentLink = linkCollection.getLinksPerDataItem().get(currentIdx);
//
//            RestUri restUriProjectForGetById = GobiiClientContext.getInstance(null, false)
//                    .getUriFactory()
//                    .RestUriFromUri(currentLink.getHref());
//            GobiiEnvelopeRestResource<ProjectDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriProjectForGetById);
//            PayloadEnvelope<ProjectDTO> resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
//                    .get(ProjectDTO.class);
//            Assert.assertNotNull(resultEnvelopeForGetByID);
//            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
//            ProjectDTO projectDTOFromLink = resultEnvelopeForGetByID.getPayload().getData().get(0);
//            Assert.assertTrue(currentProjectDto.getDatasetName().equals(projectDTOFromLink.getDatasetName()));
//            Assert.assertTrue(currentProjectDto.getProjectId().equals(projectDTOFromLink.getProjectId()));
//        }

    }

}
