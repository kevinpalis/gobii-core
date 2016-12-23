package org.gobiiproject.gobiiclient.dtorequests.dbops.crud;

import org.gobiiproject.gobiiapimodel.hateos.Link;
import org.gobiiproject.gobiiapimodel.hateos.LinkCollection;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.RestUri;
import org.gobiiproject.gobiiapimodel.types.ServiceRequestId;
import org.gobiiproject.gobiiclient.core.common.ClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.Authenticator;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.DtoRestRequestUtils;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.GlobalPkColl;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.GlobalPkValues;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.TestDtoFactory;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.TestUtils;
import org.gobiiproject.gobiimodel.headerlesscontainer.OrganizationDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.ProtocolDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by VCalaminos on 2016-12-14.
 */

public class DtoCrudRequestVendorProtocolTest implements DtoCrudRequestTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(Authenticator.authenticate());
    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(Authenticator.deAuthenticate());
    }

    @Test
    @Override
    public void create() throws Exception {

        // ********** SET UP THE PROTOCOL
        Integer protocolId = (new GlobalPkColl<DtoCrudRequestProtocolTest>()
                .getAPkVal(DtoCrudRequestProtocolTest.class, GobiiEntityNameType.PROTOCOLS));
        RestUri restUriForGetProtocolById = ClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(ServiceRequestId.URL_PROTOCOL);
        restUriForGetProtocolById.setParamValue("id", protocolId.toString());
        GobiiEnvelopeRestResource<ProtocolDTO> gobiiEnvelopeRestResourceForGetProtocolById =
                new GobiiEnvelopeRestResource<>(restUriForGetProtocolById);
        PayloadEnvelope<ProtocolDTO> resultEnvelopeForGetProtocolByID = gobiiEnvelopeRestResourceForGetProtocolById
                .get(ProtocolDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetProtocolByID.getHeader()));
        ProtocolDTO protocolDTO = resultEnvelopeForGetProtocolByID.getPayload().getData().get(0);


        // ********** SET UP THE ORGANIZATION FOR THE VENDOR
        Integer organizationId = (new GlobalPkColl<DtoCrudRequestOrganizationTest>()
                .getAPkVal(DtoCrudRequestOrganizationTest.class, GobiiEntityNameType.ORGANIZATIONS));

        RestUri restUriForGetOrganizationById = ClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(ServiceRequestId.URL_ORGANIZATION);
        restUriForGetOrganizationById.setParamValue("id", organizationId.toString());
        GobiiEnvelopeRestResource<OrganizationDTO> gobiiEnvelopeRestResourceForGetOrganizationById =
                new GobiiEnvelopeRestResource<>(restUriForGetOrganizationById);
        PayloadEnvelope<OrganizationDTO> resultEnvelopeForGetOrganizationByID = gobiiEnvelopeRestResourceForGetOrganizationById
                .get(OrganizationDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetOrganizationByID.getHeader()));
        OrganizationDTO organizationDTO = resultEnvelopeForGetOrganizationByID.getPayload().getData().get(0);

        // ********** PREDICT RESULTING NAME
        String protocolName = protocolDTO.getName();
        String organizationName = organizationDTO.getName();
        String predictedVendorProtocolName = organizationName + "_" + protocolName;

        // ********** POST VENDOR ORGANIZATION TO PROTOCOL
        RestUri restUriProtocoLVendor = ClientContext.getInstance(null, false)
                .getUriFactory()
                .childResourceByUriIdParam(ServiceRequestId.URL_PROTOCOL,
                        ServiceRequestId.URL_VENDORS);
        restUriProtocoLVendor.setParamValue("id", protocolId.toString());
        GobiiEnvelopeRestResource<OrganizationDTO> protocolVendorResource =
                new GobiiEnvelopeRestResource<>(restUriProtocoLVendor);
        PayloadEnvelope<OrganizationDTO> vendorPayloadEnvelope =
                new PayloadEnvelope<>(organizationDTO, GobiiProcessType.CREATE);
        PayloadEnvelope<OrganizationDTO> protocolVendorResult =
                protocolVendorResource.post(OrganizationDTO.class,vendorPayloadEnvelope);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(protocolVendorResult.getHeader()));


        //need services for GET /protocols/{id}/vendors/
        //                  GET /protocols/{id}/vendors/{protocol_vendor_name}

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

    }

}
