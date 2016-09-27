package org.gobiiproject.gobiiclient.dtorequests;

import javafx.application.Platform;
import org.gobiiproject.gobiiapimodel.hateos.Link;
import org.gobiiproject.gobiiapimodel.hateos.LinkCollection;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.RestUri;
import org.gobiiproject.gobiiapimodel.restresources.UriFactory;
import org.gobiiproject.gobiiapimodel.types.ServiceRequestId;
import org.gobiiproject.gobiiclient.core.ClientContext;
import org.gobiiproject.gobiiclient.core.restmethods.RestResource;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.Authenticator;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.EntityParamValues;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.TestDtoFactory;
import org.gobiiproject.gobiiclient.dtorequests.Helpers.TestUtils;
import org.gobiiproject.gobiimodel.dto.container.*;
import org.gobiiproject.gobiimodel.headerlesscontainer.PlatformDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
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
public class DtoRequestPlatformTest {

    private static UriFactory uriFactory;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(Authenticator.authenticate());
        String currentCropContextRoot = ClientContext.getInstance(null, false).getCurrentCropContextRoot();
        DtoRequestPlatformTest.uriFactory = new UriFactory(currentCropContextRoot);

    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(Authenticator.deAuthenticate());
    }


    @Test
    public void testCreatePlatformWithHttpPost() throws Exception {

        // BEGIN:   ****** THIS PART WILL HAVE TO BE REFACTORED LATER *********
        DtoRequestNameIdList dtoRequestNameIdList = new DtoRequestNameIdList();
        NameIdListDTO nameIdListDTORequest = new NameIdListDTO();
        nameIdListDTORequest.setEntityName("cvgroupterms");
        nameIdListDTORequest.setFilter("platform_type");

        NameIdListDTO nameIdListDTO = dtoRequestNameIdList.process(nameIdListDTORequest);
        List<NameIdDTO> platformProperTerms = new ArrayList<>(nameIdListDTO
                .getNamesById());
        EntityParamValues entityParamValues = TestDtoFactory
                .makeConstrainedEntityParams(platformProperTerms, 1);
        // END:   ****** THIS PART WILL HAVE TO BE REFACTORED LATER *********



        PlatformDTO newPlatformDto = TestDtoFactory
                .makePopulatedPlatformDTO(GobiiProcessType.CREATE, 1, entityParamValues);

        PayloadEnvelope<PlatformDTO> payloadEnvelope = new PayloadEnvelope<>(newPlatformDto, GobiiProcessType.CREATE);
        RestResource<PlatformDTO> restResource = new RestResource<>(DtoRequestPlatformTest
                .uriFactory
                .resourceColl(ServiceRequestId.URL_PLATFORM));
        PayloadEnvelope<PlatformDTO> platformDTOResponseEnvelope = restResource.post(PlatformDTO.class,
                payloadEnvelope);
        PlatformDTO platformDTOResponse = platformDTOResponseEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, platformDTOResponse);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(platformDTOResponseEnvelope.getHeader()));
        Assert.assertTrue(platformDTOResponse.getPlatformId() > 0);


        RestUri restUriPlatformForGetById = DtoRequestPlatformTest
                .uriFactory
                .resourceByUriIdParam(ServiceRequestId.URL_PLATFORM);
        restUriPlatformForGetById.setParamValue("id", platformDTOResponse.getPlatformId().toString());
        RestResource<PlatformDTO> restResourceForGetById = new RestResource<>(restUriPlatformForGetById);
        PayloadEnvelope<PlatformDTO> resultEnvelopeForGetByID = restResourceForGetById
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

        Assert.assertNull("There are missing entity property items",missingItems);

        Assert.assertTrue("Parameter values do not match",
                entityParamValues.compare(platformDTOResponseForParams.getProperties()));


    }

    @Test
    public void testUpdatePlatformWithHttpPut() throws Exception {


        // BEGIN:   ****** THIS PART WILL HAVE TO BE REFACTORED LATER *********
        //get terms for platform properties:
        DtoRequestNameIdList dtoRequestNameIdList = new DtoRequestNameIdList();
        NameIdListDTO nameIdListDTORequest = new NameIdListDTO();
        nameIdListDTORequest.setEntityName("cvgroupterms");
        nameIdListDTORequest.setFilter("platform_type");
        NameIdListDTO nameIdListDTO = dtoRequestNameIdList.process(nameIdListDTORequest);
        List<NameIdDTO> platformProperTerms = new ArrayList<>(nameIdListDTO
                .getNamesById());
        EntityParamValues entityParamValues = TestDtoFactory
                .makeConstrainedEntityParams(platformProperTerms, 1);
        // END:   ****** THIS PART WILL HAVE TO BE REFACTORED LATER *********

        // create a new platform for our test
        PlatformDTO newPlatformDto = TestDtoFactory
                .makePopulatedPlatformDTO(GobiiProcessType.CREATE, 1, entityParamValues);

        PayloadEnvelope<PlatformDTO> payloadEnvelope = new PayloadEnvelope<>(newPlatformDto, GobiiProcessType.CREATE);
        RestResource<PlatformDTO> restResource = new RestResource<>(DtoRequestPlatformTest
                .uriFactory
                .resourceColl(ServiceRequestId.URL_PLATFORM));
        PayloadEnvelope<PlatformDTO> platformDTOResponseEnvelope = restResource.post(PlatformDTO.class,
                payloadEnvelope);
        PlatformDTO newPlatformDTOResponse = platformDTOResponseEnvelope.getPayload().getData().get(0);

        // re-retrieve the platform we just created so we start with a fresh READ mode dto

        RestUri restUriPlatformForGetById = DtoRequestPlatformTest
                .uriFactory
                .resourceByUriIdParam(ServiceRequestId.URL_PLATFORM);
        restUriPlatformForGetById.setParamValue("id", newPlatformDTOResponse.getPlatformId().toString());
        RestResource<PlatformDTO> restResourceForGetById = new RestResource<>(restUriPlatformForGetById);
        PayloadEnvelope<PlatformDTO> resultEnvelopeForGetByID = restResourceForGetById
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
        PayloadEnvelope<PlatformDTO> platformDTOResponseEnvelopeUpdate = restResourceForGetById.put(PlatformDTO.class,
                new PayloadEnvelope<>(platformDTOReceived, GobiiProcessType.UPDATE));

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(platformDTOResponseEnvelopeUpdate.getHeader()));

        PlatformDTO PlatformDTORequest = platformDTOResponseEnvelopeUpdate.getPayload().getData().get(0);

//        PlatformDTO PlatformDTOResponse = dtoRequestPlatform.process(platformDTOReceived);
//        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(PlatformDTOResponse));

        restUriPlatformForGetById.setParamValue("id",PlatformDTORequest.getPlatformId().toString());
        resultEnvelopeForGetByID = restResourceForGetById
                .get(PlatformDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));


        PlatformDTO dtoRequestPlatformReRetrieved =resultEnvelopeForGetByID.getPayload().getData().get(0);


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
    public void testGetPlatformDetailsWithHttpGet() throws Exception {


        // get a list of platforms
        RestUri restUriPlatform = DtoRequestPlatformTest.uriFactory.resourceColl(ServiceRequestId.URL_PLATFORM);
        RestResource<PlatformDTO> restResource = new RestResource<>(restUriPlatform);
        PayloadEnvelope<PlatformDTO> resultEnvelope = restResource
                .get(PlatformDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<PlatformDTO> platformDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(platformDTOList);
        Assert.assertTrue(platformDTOList.size() > 0 );
        Assert.assertNotNull(platformDTOList.get(0).getPlatformName());


        // use an artibrary platform id
        Integer platformId = platformDTOList.get(0).getPlatformId();
        RestUri restUriPlatformForGetById = DtoRequestPlatformTest
                .uriFactory
                .resourceByUriIdParam(ServiceRequestId.URL_PLATFORM);
        restUriPlatformForGetById.setParamValue("id", platformId.toString());
        RestResource<PlatformDTO> restResourceForGetById = new RestResource<>(restUriPlatformForGetById);
        PayloadEnvelope<PlatformDTO> resultEnvelopeForGetByID = restResourceForGetById
                .get(PlatformDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        PlatformDTO platformDTO = resultEnvelopeForGetByID.getPayload().getData().get(0);
        Assert.assertTrue(platformDTO.getPlatformId() > 0);
        Assert.assertNotNull(platformDTO.getPlatformName());
    }

    @Test
    public void getPlatformsWithHttpGet() throws Exception {

        RestUri restUriPlatform = DtoRequestPlatformTest.uriFactory.resourceColl(ServiceRequestId.URL_PLATFORM);
        RestResource<PlatformDTO> restResource = new RestResource<>(restUriPlatform);
        PayloadEnvelope<PlatformDTO> resultEnvelope = restResource
                .get(PlatformDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<PlatformDTO> platformDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(platformDTOList);
        Assert.assertTrue(platformDTOList.size() > 0 );
        Assert.assertNotNull(platformDTOList.get(0).getPlatformName());


        LinkCollection linkCollection = resultEnvelope.getPayload().getLinkCollection();
        for(int currentItemIdx = 0; currentItemIdx < platformDTOList.size(); currentItemIdx++  ) {
            PlatformDTO currentPlatformDto = platformDTOList.get(currentItemIdx);

            Link currentLink = linkCollection.getLinksPerDataItem().get(currentItemIdx);

            RestUri restUriPlatformForGetById = DtoRequestPlatformTest
                    .uriFactory
                    .RestUriFromUri(currentLink.getHref());
            RestResource<PlatformDTO> restResourceForGetById = new RestResource<>(restUriPlatformForGetById);
            PayloadEnvelope<PlatformDTO> resultEnvelopeForGetByID = restResourceForGetById
                    .get(PlatformDTO.class);
            Assert.assertNotNull(resultEnvelopeForGetByID);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
            PlatformDTO platformDTOFromLink = resultEnvelopeForGetByID.getPayload().getData().get(0);
            Assert.assertTrue(currentPlatformDto.getPlatformName().equals(platformDTOFromLink.getPlatformName()));
            Assert.assertTrue(currentPlatformDto.getPlatformId().equals(platformDTOFromLink.getPlatformId()));
        }

    }

}
