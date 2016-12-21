package org.gobiiproject.gobiiclient.dtorequests.dbops.crud;

import org.gobiiproject.gobiiapimodel.hateos.Link;
import org.gobiiproject.gobiiapimodel.hateos.LinkCollection;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.RestUri;
import org.gobiiproject.gobiiapimodel.types.ServiceRequestId;
import org.gobiiproject.gobiiclient.core.ClientContext;
import org.gobiiproject.gobiiclient.core.restmethods.RestResource;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.*;
import org.gobiiproject.gobiimodel.dto.container.ProtocolDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by VCalaminos on 2016-12-14.
 */
public class DtoCrudRequestProtocolTest implements DtoCrudRequestTest{

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

        ProtocolDTO newProtocolDto = TestDtoFactory
                .makePopulatedProtocolDTO(GobiiProcessType.CREATE, 1);

        PayloadEnvelope<ProtocolDTO> payloadEnvelope = new PayloadEnvelope<>(newProtocolDto, GobiiProcessType.CREATE);
        RestResource<ProtocolDTO> restResource = new RestResource<>(ClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(ServiceRequestId.URL_PROTOCOL));
        PayloadEnvelope<ProtocolDTO> protocolDTOResponseEnvelope = restResource.post(ProtocolDTO.class,
                payloadEnvelope);
        ProtocolDTO protocolDTOResponse = protocolDTOResponseEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, protocolDTOResponse);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(protocolDTOResponseEnvelope.getHeader()));
        Assert.assertTrue(protocolDTOResponse.getProtocolId() > 0);

        GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.PROTOCOLS,
                protocolDTOResponse.getProtocolId());


        RestUri restUriProtocolForGetById = ClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(ServiceRequestId.URL_PROTOCOL);
        restUriProtocolForGetById.setParamValue("id", protocolDTOResponse.getProtocolId().toString());
        RestResource<ProtocolDTO> restResourceForGetById = new RestResource<>(restUriProtocolForGetById);
        PayloadEnvelope<ProtocolDTO> resultEnvelopeForGetByID = restResourceForGetById
                .get(ProtocolDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
        ProtocolDTO protocolDTOResponseForParams = resultEnvelopeForGetByID.getPayload().getData().get(0);
    }


    @Test
    @Override
    public void update() throws Exception {

        // create a new organization for our test
        ProtocolDTO newProtocolDto = TestDtoFactory
                .makePopulatedProtocolDTO(GobiiProcessType.CREATE, 1);

        PayloadEnvelope<ProtocolDTO> payloadEnvelope = new PayloadEnvelope<>(newProtocolDto, GobiiProcessType.CREATE);
        RestResource<ProtocolDTO> restResource = new RestResource<>(ClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(ServiceRequestId.URL_PROTOCOL));
        PayloadEnvelope<ProtocolDTO> protocolDTOResponseEnvelope = restResource.post(ProtocolDTO.class,
                payloadEnvelope);
        ProtocolDTO newProtocolDTOResponse = protocolDTOResponseEnvelope.getPayload().getData().get(0);

        // re-retrieve the organization we just created so we start with a fresh READ mode dto

        RestUri restUriProtocolForGetById = ClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(ServiceRequestId.URL_PROTOCOL);
        restUriProtocolForGetById.setParamValue("id", newProtocolDTOResponse.getProtocolId().toString());
        RestResource<ProtocolDTO> restResourceForGetById = new RestResource<>(restUriProtocolForGetById);
        PayloadEnvelope<ProtocolDTO> resultEnvelopeForGetByID = restResourceForGetById
                .get(ProtocolDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
        ProtocolDTO protocolDTOReceived = resultEnvelopeForGetByID.getPayload().getData().get(0);


        String newName = UUID.randomUUID().toString();
        protocolDTOReceived.setName(newName);
        restResourceForGetById.setParamValue("id", protocolDTOReceived.getProtocolId().toString());
        PayloadEnvelope<ProtocolDTO> protocolDTOResponseEnvelopeUpdate = restResourceForGetById.put(ProtocolDTO.class,
                new PayloadEnvelope<>(protocolDTOReceived, GobiiProcessType.UPDATE));

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(protocolDTOResponseEnvelopeUpdate.getHeader()));

        ProtocolDTO ProtocolDTORequest = protocolDTOResponseEnvelopeUpdate.getPayload().getData().get(0);


        restUriProtocolForGetById.setParamValue("id", ProtocolDTORequest.getProtocolId().toString());
        resultEnvelopeForGetByID = restResourceForGetById
                .get(ProtocolDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));


        ProtocolDTO dtoRequestProtocolReRetrieved = resultEnvelopeForGetByID.getPayload().getData().get(0);


        Assert.assertTrue(dtoRequestProtocolReRetrieved.getName().equals(newName));
    }


    @Test
    @Override
    public void get() throws Exception {


        RestUri restUriProtocol = ClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(ServiceRequestId.URL_PROTOCOL);
        RestResource<ProtocolDTO> restResource = new RestResource<>(restUriProtocol);
        PayloadEnvelope<ProtocolDTO> resultEnvelope = restResource
                .get(ProtocolDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<ProtocolDTO> protocolDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(protocolDTOList);
        Assert.assertTrue(protocolDTOList.size() > 0);
        Assert.assertNotNull(protocolDTOList.get(0).getName());


        Integer protocolId = protocolDTOList.get(0).getProtocolId();
        RestUri restUriProtocolForGetById = ClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(ServiceRequestId.URL_PROTOCOL);
        restUriProtocolForGetById.setParamValue("id", protocolId.toString());
        RestResource<ProtocolDTO> restResourceForGetById = new RestResource<>(restUriProtocolForGetById);
        PayloadEnvelope<ProtocolDTO> resultEnvelopeForGetByID = restResourceForGetById
                .get(ProtocolDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        ProtocolDTO protocolDTO = resultEnvelopeForGetByID.getPayload().getData().get(0);
        Assert.assertTrue(protocolDTO.getProtocolId() > 0);
        Assert.assertNotNull(protocolDTO.getName());
    }


    @Test
    public void testEmptyResult() throws Exception {

        DtoRestRequestUtils<ProtocolDTO> dtoDtoRestRequestUtils =
                new DtoRestRequestUtils<>(ProtocolDTO.class,ServiceRequestId.URL_PROTOCOL);
        Integer maxId = dtoDtoRestRequestUtils.getMaxPkVal();
        Integer nonExistentId = maxId + 1;


        PayloadEnvelope<ProtocolDTO> resultEnvelope =
                dtoDtoRestRequestUtils.getResponseEnvelopeForEntityId(nonExistentId.toString());

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        Assert.assertNotNull(resultEnvelope.getPayload());
        Assert.assertNotNull(resultEnvelope.getPayload().getData());
        Assert.assertTrue(resultEnvelope.getPayload().getData().size() == 0 );
    }



    @Test
    @Override
    public void getList() throws Exception {

        RestUri restUriProtocol = ClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(ServiceRequestId.URL_PROTOCOL);
        RestResource<ProtocolDTO> restResource = new RestResource<>(restUriProtocol);
        PayloadEnvelope<ProtocolDTO> resultEnvelope = restResource
                .get(ProtocolDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<ProtocolDTO> protocolDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(protocolDTOList);
        Assert.assertTrue(protocolDTOList.size() > 0);
        Assert.assertNotNull(protocolDTOList.get(0).getName());


        LinkCollection linkCollection = resultEnvelope.getPayload().getLinkCollection();
        Assert.assertTrue(linkCollection.getLinksPerDataItem().size() == protocolDTOList.size());

        List<Integer> itemsToTest = new ArrayList<>();
        if (protocolDTOList.size() > 50) {
            itemsToTest = TestUtils.makeListOfIntegersInRange(10, protocolDTOList.size());

        } else {
            for (int idx = 0; idx < protocolDTOList.size(); idx++) {
                itemsToTest.add(idx);
            }
        }

        for (Integer currentItemIdx : itemsToTest) {
            ProtocolDTO currentProtocolDto = protocolDTOList.get(currentItemIdx);

            Link currentLink = linkCollection.getLinksPerDataItem().get(currentItemIdx);

            RestUri restUriProtocolForGetById = ClientContext.getInstance(null, false)
                    .getUriFactory()
                    .RestUriFromUri(currentLink.getHref());
            RestResource<ProtocolDTO> restResourceForGetById = new RestResource<>(restUriProtocolForGetById);
            PayloadEnvelope<ProtocolDTO> resultEnvelopeForGetByID = restResourceForGetById
                    .get(ProtocolDTO.class);
            Assert.assertNotNull(resultEnvelopeForGetByID);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
            ProtocolDTO protocolDTOFromLink = resultEnvelopeForGetByID.getPayload().getData().get(0);
            Assert.assertTrue(currentProtocolDto.getName().equals(protocolDTOFromLink.getName()));
            Assert.assertTrue(currentProtocolDto.getProtocolId().equals(protocolDTOFromLink.getProtocolId()));
        }

    }

}
