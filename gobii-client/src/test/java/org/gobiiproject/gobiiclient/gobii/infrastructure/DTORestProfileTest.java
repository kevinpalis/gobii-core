package org.gobiiproject.gobiiclient.gobii.infrastructure;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkColl;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestDtoFactory;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestUtils;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestAnalysisTest;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestMarkerTest;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestNameIdListTest;
import org.gobiiproject.gobiiclient.gobii.dbops.readonly.DtoRequestNameIdListTest;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.MarkerDTO;
import org.gobiiproject.gobiimodel.dto.rest.RestProfileDTO;
import org.gobiiproject.gobiimodel.types.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DTORestProfileTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.authenticate());
    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }


    @Test
    public void changePostMax() throws Exception {

        // We know that the /names/analysis profile has been defined

        // create arbitrary entities

        (new GlobalPkColl<DtoCrudRequestAnalysisTest>()).getPkVals(DtoCrudRequestAnalysisTest.class, GobiiEntityNameType.ANALYSIS, 10);


        PayloadEnvelope<NameIdDTO> analysisNameIdResponseEnvelopePreUpdate = DtoRequestNameIdListTest
                .testNameRetrieval(GobiiEntityNameType.ANALYSIS,
                        GobiiFilterType.NONE,
                        null);


        Integer initialMaxGet = analysisNameIdResponseEnvelopePreUpdate.getHeader().getMaxGet();
        Integer initialMaxPost = analysisNameIdResponseEnvelopePreUpdate.getHeader().getMaxPost();
        Integer initialMaxPut = analysisNameIdResponseEnvelopePreUpdate.getHeader().getMaxPut();


        Assert.assertNotNull("The max GET limit for names/"
                        + GobiiEntityNameType.ANALYSIS.toString()
                        + " should not be null",
                initialMaxGet);

        Assert.assertNotNull("The max POST limit for names/"
                        + GobiiEntityNameType.ANALYSIS.toString()
                        + " should not be null",
                initialMaxPost);

        Assert.assertNotNull("The max PUT limit for names/"
                        + GobiiEntityNameType.ANALYSIS.toString()
                        + " should not be null",
                initialMaxPut);


        Assert.assertTrue("The max GET limit for names/"
                        + GobiiEntityNameType.ANALYSIS.toString()
                        + " should have a value",
                initialMaxGet > 0);

        Assert.assertTrue("The max POST limit for names/"
                        + GobiiEntityNameType.ANALYSIS.toString()
                        + " should have a value",
                initialMaxPost > 0);

        Assert.assertTrue("The max PUT limit for names/"
                        + GobiiEntityNameType.ANALYSIS.toString()
                        + " should have a value",
                initialMaxPut > 0);


        Integer newMaxGet = initialMaxGet + 5;
        Integer newMaxPost = initialMaxPost + 6;
        Integer newMaxPut = initialMaxPut + 7;


        this.updateRestProfile(new RestProfileDTO(RestResourceId.GOBII_NAMES,
                RestMethodType.GET,
                GobiiEntityNameType.ANALYSIS.toString(),
                newMaxGet));

        this.updateRestProfile(new RestProfileDTO(RestResourceId.GOBII_NAMES,
                RestMethodType.POST,
                GobiiEntityNameType.ANALYSIS.toString(),
                newMaxPost));

        this.updateRestProfile(new RestProfileDTO(RestResourceId.GOBII_NAMES,
                RestMethodType.PUT,
                GobiiEntityNameType.ANALYSIS.toString(),
                newMaxPut));

        PayloadEnvelope<NameIdDTO> analysisNameIdResponseEnvelopePostUpdate = DtoRequestNameIdListTest
                .testNameRetrieval(GobiiEntityNameType.ANALYSIS,
                        GobiiFilterType.NONE,
                        null);


        Integer newRetrievedMaxGet = analysisNameIdResponseEnvelopePostUpdate.getHeader().getMaxGet();
        Integer newRetrievedMaxPost = analysisNameIdResponseEnvelopePostUpdate.getHeader().getMaxPost();
        Integer newRetrievedMaxPut = analysisNameIdResponseEnvelopePostUpdate.getHeader().getMaxPut();

        Assert.assertEquals("The post-put retrieved GET max ("
                        + newRetrievedMaxGet
                        + "  does not match the value that was put ("
                        + newMaxGet
                        + ")",
                newMaxGet,
                newRetrievedMaxGet);

        Assert.assertEquals("The post-put retrieved POST max("
                        + newRetrievedMaxPost
                        + ") does not match the value that was put ("
                        + newMaxPost
                        + ")",
                newMaxPost,
                newRetrievedMaxPost);

        Assert.assertEquals("The post-put retrieved PUT max ("
                        + newRetrievedMaxPut
                        + " ) does not match the value that was put ("
                        + newMaxPost,
                newMaxPut,
                newRetrievedMaxPut);

        newMaxGet = 10;

        // check if the number of names that will be retrieve will equal the max GET limit

        this.updateRestProfile(new RestProfileDTO(RestResourceId.GOBII_NAMES,
                RestMethodType.GET,
                GobiiEntityNameType.ANALYSIS.toString(),
                newMaxGet));

        PayloadEnvelope<NameIdDTO> getAnalysisNameResponseEnvelope = DtoRequestNameIdListTest
                .testNameRetrieval(GobiiEntityNameType.ANALYSIS,
                        GobiiFilterType.NONE,
                        null);

        Integer nameIdListSize = getAnalysisNameResponseEnvelope.getPayload().getData().size();

        Assert.assertEquals("The size of the list retrieved (" +
                nameIdListSize +
                " does not match the max GET limit (" +
                newMaxGet + ")",
                newMaxGet,
                nameIdListSize);

        // reset to initial max GET

        this.updateRestProfile(new RestProfileDTO(RestResourceId.GOBII_NAMES,
                RestMethodType.GET,
                GobiiEntityNameType.ANALYSIS.toString(),
                initialMaxGet));

    }


    @Test
    public void changePostMaxMarkerNames() throws Exception {

        (new GlobalPkColl<DtoCrudRequestMarkerTest>()).getPkVals(DtoCrudRequestMarkerTest.class, GobiiEntityNameType.MARKER, 10);

        PayloadEnvelope<NameIdDTO> analysisNameIdResponseEnvelopePreUpdate = DtoRequestNameIdListTest
                .testNameRetrieval(GobiiEntityNameType.MARKER,
                        GobiiFilterType.NONE,
                        null);

        Integer initialMaxPost = analysisNameIdResponseEnvelopePreUpdate.getHeader().getMaxPost();

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

        MarkerDTO markerDTO4 = TestDtoFactory
                .makeMarkerDTO(UUID.randomUUID().toString());
        markerDTO4.setPlatformId(platformId);

        MarkerDTO markerDTO5 = TestDtoFactory
                .makeMarkerDTO(UUID.randomUUID().toString());
        markerDTO5.setPlatformId(platformId);

        markerDTO1 = DtoCrudRequestNameIdListTest.createMarkerForTest(markerDTO1);
        markerDTO2 = DtoCrudRequestNameIdListTest.createMarkerForTest(markerDTO2);
        markerDTO3 = DtoCrudRequestNameIdListTest.createMarkerForTest(markerDTO3);
        markerDTO4 = DtoCrudRequestNameIdListTest.createMarkerForTest(markerDTO4);
        markerDTO5 = DtoCrudRequestNameIdListTest.createMarkerForTest(markerDTO5);

        List<NameIdDTO> nameIdDTOListInput = new ArrayList<>();

        String[] markerDTOs = new String[]{markerDTO1.getMarkerName(), markerDTO2.getMarkerName(), markerDTO3.getMarkerName(), markerDTO4.getMarkerName(), markerDTO5.getMarkerName()};

        for (String markerName : markerDTOs) {

            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName(markerName);

            nameIdDTOListInput.add(nameIdDTO);

        }

        Integer newMaxPost = 3;

        this.updateRestProfile(new RestProfileDTO(RestResourceId.GOBII_NAMES,
                RestMethodType.POST,
                GobiiEntityNameType.MARKER.toString(),
                newMaxPost));

        PayloadEnvelope<NameIdDTO> payloadEnvelope = new PayloadEnvelope<>();
        payloadEnvelope.getHeader().setGobiiProcessType(GobiiProcessType.CREATE);
        payloadEnvelope.getPayload().setData(nameIdDTOListInput);

        RestUri namesUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .nameIdListByQueryParams();
        GobiiEnvelopeRestResource<NameIdDTO, NameIdDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(namesUri);
        namesUri.setParamValue("entity", GobiiEntityNameType.MARKER.toString().toLowerCase());
        namesUri.setParamValue("filterType", StringUtils.capitalize(GobiiFilterType.NAMES_BY_NAME_LIST.toString()));
        namesUri.setParamValue("filterValue", platformId.toString());

        PayloadEnvelope<NameIdDTO> responsePayloadEnvelope = gobiiEnvelopeRestResource.post(NameIdDTO.class, payloadEnvelope);

        String validationMessage = "The POST to resource names/"+GobiiEntityNameType.MARKER.toString().toLowerCase()+
                " exceeds the max POST limit of " + newMaxPost;

        Assert.assertTrue("The error message should be " +
                "'"+validationMessage+"'",
                responsePayloadEnvelope.getHeader()
                .getStatus()
                .getStatusMessages()
                .stream()
                .filter(m -> m.getMessage().contains(validationMessage))
                .count()
                >0);

        // reset to inital max POST

        this.updateRestProfile(new RestProfileDTO(RestResourceId.GOBII_NAMES,
                RestMethodType.POST,
                GobiiEntityNameType.MARKER.toString(),
                initialMaxPost));

    }

    private PayloadEnvelope<RestProfileDTO> updateRestProfile(RestProfileDTO restProfileDTO) throws Exception {

        RestUri restProfilesUri =
                GobiiClientContext
                        .getInstance(null, false)
                        .getUriFactory()
                        .resourceColl(RestResourceId.GOBII_REST_PROFILES);
        GobiiEnvelopeRestResource<RestProfileDTO,RestProfileDTO> restEnvelopeRestProfile = new GobiiEnvelopeRestResource<>(restProfilesUri);
        PayloadEnvelope<RestProfileDTO> resultEnvelope = restEnvelopeRestProfile
                .put(RestProfileDTO.class, new PayloadEnvelope<>(restProfileDTO, GobiiProcessType.UPDATE));

        Assert.assertFalse("Error updating rest request profile",
                TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));


        return resultEnvelope;
    }
}
