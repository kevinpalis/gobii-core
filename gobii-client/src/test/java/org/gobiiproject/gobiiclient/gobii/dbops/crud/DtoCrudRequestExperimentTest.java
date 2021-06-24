// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-25
// ************************************************************************
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
import org.gobiiproject.gobiiclient.gobii.Helpers.DtoRestRequestUtils;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkColl;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkValues;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestUtils;
import org.gobiiproject.gobiimodel.dto.auditable.ExperimentDTO;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiiapimodel.payload.HeaderStatusMessage;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiFilterType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DtoCrudRequestExperimentTest implements DtoCrudRequestTest {


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
    public void get() throws Exception {

        //DtoRequestExperiment dtoRequestExperiment = new DtoRequestExperiment();

//        ExperimentDTO experimentDTO = new ExperimentDTO();
//        experimentDTO.setExperimentId(2);
//        dtoRequestExperiment.process(experimentDTO);


        Integer experimentId = (new GlobalPkColl<DtoCrudRequestExperimentTest>().getAPkVal(DtoCrudRequestExperimentTest.class, GobiiEntityNameType.EXPERIMENT));

        RestUri experimentsUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_EXPERIMENTS);
        experimentsUri.setParamValue("id", experimentId.toString());
        GobiiEnvelopeRestResource<ExperimentDTO,ExperimentDTO> gobiiEnvelopeRestResourceForExperiments = new GobiiEnvelopeRestResource<>(experimentsUri);
        PayloadEnvelope<ExperimentDTO> resultEnvelope = gobiiEnvelopeRestResourceForExperiments
                .get(ExperimentDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        ExperimentDTO experimentDTOResponse = resultEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(experimentDTOResponse, null);
        Assert.assertTrue(experimentDTOResponse.getExperimentId() > 0);

    }

    @Test
    public void testEmptyResult() throws Exception {

        DtoRestRequestUtils<ExperimentDTO> dtoDtoRestRequestUtils =
                new DtoRestRequestUtils<>(ExperimentDTO.class, RestResourceId.GOBII_EXPERIMENTS);
        Integer maxId = dtoDtoRestRequestUtils.getMaxPkVal();
        Integer nonExistentId = maxId + 1;



        PayloadEnvelope<ExperimentDTO> resultEnvelope =
                dtoDtoRestRequestUtils.getResponseEnvelopeForEntityId(nonExistentId.toString());


        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        Assert.assertNotNull(resultEnvelope.getPayload());
        Assert.assertNotNull(resultEnvelope.getPayload().getData());
        Assert.assertTrue("Request for experiment with ID " + nonExistentId.toString() + " should not have retrieved a result",
                resultEnvelope.getPayload().getData().size() == 0 );
    }


    @Test
    @Override
    public void create() throws Exception {

        //DtoRequestExperiment dtoRequestExperiment = new DtoRequestExperiment();

        Integer projectId = (new GlobalPkColl<DtoCrudRequestProjectTest>().getAPkVal(DtoCrudRequestProjectTest.class, GobiiEntityNameType.PROJECT));
        Integer manifestId = (new GlobalPkColl<DtoCrudRequestManifestTest>().getAPkVal(DtoCrudRequestManifestTest.class, GobiiEntityNameType.MANIFEST));
        Integer idOfProtocolThatHasAVendor = (new GlobalPkColl<DtoCrudRequestVendorProtocolTest>().getAPkVal(DtoCrudRequestVendorProtocolTest.class, GobiiEntityNameType.VENDOR_PROTOCOL));

        // ** GET THE VENDOR_PROTOCOL ID *****************



    

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

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));


        //ExperimentDTO experimentDTOResponse = dtoRequestExperiment.process(experimentDTORequest);
        ExperimentDTO experimentDTOResponse = resultEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(experimentDTOResponse, null);
        Assert.assertTrue(experimentDTOResponse.getExperimentId() > 0);
        GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.EXPERIMENT, experimentDTOResponse.getExperimentId());

    } // testGetMarkers()

    @Test
    public void testCreateExistingExperiment() throws Exception {

//        DtoRequestExperiment dtoRequestExperiment = new DtoRequestExperiment();
//        ExperimentDTO experimentDTORequest = new ExperimentDTO();
//        experimentDTORequest.setExperimentId(2);
//        ExperimentDTO ExperimentDTOExisting = dtoRequestExperiment.process(experimentDTORequest);

        Integer experimentId = (new GlobalPkColl<DtoCrudRequestExperimentTest>().getAPkVal(DtoCrudRequestExperimentTest.class, GobiiEntityNameType.EXPERIMENT));

        RestUri experimentsUriById = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_EXPERIMENTS);
        experimentsUriById.setParamValue("id", experimentId.toString());
        GobiiEnvelopeRestResource<ExperimentDTO,ExperimentDTO> gobiiEnvelopeRestResourceForExperiments = new GobiiEnvelopeRestResource<>(experimentsUriById);
        PayloadEnvelope<ExperimentDTO> resultEnvelope = gobiiEnvelopeRestResourceForExperiments
                .get(ExperimentDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        ExperimentDTO experimentDTOExisting = resultEnvelope.getPayload().getData().get(0);


        RestUri experimentCollUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_EXPERIMENTS);
        GobiiEnvelopeRestResource<ExperimentDTO,ExperimentDTO> gobiiEnvelopeRestResourceForProjectPost =
                new GobiiEnvelopeRestResource<>(experimentCollUri);
        PayloadEnvelope<ExperimentDTO> payloadEnvelope = new PayloadEnvelope<>(experimentDTOExisting,
                GobiiProcessType.CREATE);
        resultEnvelope = gobiiEnvelopeRestResourceForProjectPost
                .post(ExperimentDTO.class, payloadEnvelope);

        //ExperimentDTO ExperimentDTOResponse = dtoRequestExperiment.process(ExperimentDTOExisting);
        List<HeaderStatusMessage> headerStatusMessages = resultEnvelope
                .getHeader()
                .getStatus()
                .getStatusMessages()
                .stream()
                .filter(m -> m.getGobiiValidationStatusType().equals(GobiiValidationStatusType.VALIDATION_COMPOUND_UNIQUE))
                .collect(Collectors.toList());


        Assert.assertNotNull(headerStatusMessages);
        Assert.assertTrue(headerStatusMessages.size() > 0);
        HeaderStatusMessage headerStatusMessageValidation = headerStatusMessages.get(0);
        Assert.assertTrue(headerStatusMessageValidation.getMessage().toLowerCase().contains("name"));
        Assert.assertTrue(headerStatusMessageValidation.getMessage().toLowerCase().contains("project"));

    } // testCreateExperiment()

    @Test
    @Override
    public void update() throws Exception {

//        DtoRequestExperiment dtoRequestExperiment = new DtoRequestExperiment();
//        ExperimentDTO experimentDTORequest = new ExperimentDTO();
//        experimentDTORequest.setExperimentId(2);
//        ExperimentDTO experimentDTOReceived = dtoRequestExperiment.process(experimentDTORequest);

        Integer experimentId = (new GlobalPkColl<DtoCrudRequestExperimentTest>().getAPkVal(DtoCrudRequestExperimentTest.class, GobiiEntityNameType.EXPERIMENT));
        RestUri experimentsUriById = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_EXPERIMENTS);
        experimentsUriById.setParamValue("id", experimentId.toString());
        GobiiEnvelopeRestResource<ExperimentDTO,ExperimentDTO> gobiiEnvelopeRestResourceForExperimentsById = new GobiiEnvelopeRestResource<>(experimentsUriById);
        PayloadEnvelope<ExperimentDTO> resultEnvelope = gobiiEnvelopeRestResourceForExperimentsById
                .get(ExperimentDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        ExperimentDTO experimentDTOReceived = resultEnvelope.getPayload().getData().get(0);

        String newDataFile = UUID.randomUUID().toString();

        experimentDTOReceived.setExperimentDataFile(newDataFile);


        PayloadEnvelope<ExperimentDTO> postRequestEnvelope = new PayloadEnvelope<>(experimentDTOReceived, GobiiProcessType.UPDATE);
        resultEnvelope = gobiiEnvelopeRestResourceForExperimentsById
                .put(ExperimentDTO.class, postRequestEnvelope);

        // ExperimentDTO experimentDTOResponse = dtoRequestExperiment.process(experimentDTOReceived);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));


        ExperimentDTO dtoRequestExperimentExperimentReRetrieved = resultEnvelope.getPayload().getData().get(0);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

        Assert.assertTrue(dtoRequestExperimentExperimentReRetrieved.getExperimentDataFile().equals(newDataFile));

    }

    @Test
    @Override
    public void getList() throws Exception {

        RestUri restUriExperiment = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_EXPERIMENTS);
        GobiiEnvelopeRestResource<ExperimentDTO,ExperimentDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriExperiment);
        PayloadEnvelope<ExperimentDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(ExperimentDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<ExperimentDTO> experimentDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(experimentDTOList);
        Assert.assertTrue(experimentDTOList.size() > 0);
        Assert.assertNotNull(experimentDTOList.get(0).getExperimentName());


        LinkCollection linkCollection = resultEnvelope.getPayload().getLinkCollection();
        Assert.assertTrue(linkCollection.getLinksPerDataItem().size() == experimentDTOList.size());

        List<Integer> itemsToTest = new ArrayList<>();
        if (experimentDTOList.size() > 50) {
            itemsToTest = TestUtils.makeListOfIntegersInRange(10, experimentDTOList.size());

        } else {
            for (int idx = 0; idx < experimentDTOList.size(); idx++) {
                itemsToTest.add(idx);
            }
        }

        for (Integer currentIdx : itemsToTest) {
            ExperimentDTO currentExperimentDto = experimentDTOList.get(currentIdx);

            Link currentLink = linkCollection.getLinksPerDataItem().get(currentIdx);

            RestUri restUriExperimentForGetById = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .RestUriFromUri(currentLink.getHref());
            GobiiEnvelopeRestResource<ExperimentDTO,ExperimentDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriExperimentForGetById);
            PayloadEnvelope<ExperimentDTO> resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                    .get(ExperimentDTO.class);
            Assert.assertNotNull(resultEnvelopeForGetByID);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
            ExperimentDTO experimentDTOFromLink = resultEnvelopeForGetByID.getPayload().getData().get(0);
            Assert.assertTrue(currentExperimentDto.getExperimentName().equals(experimentDTOFromLink.getExperimentName()));
            Assert.assertTrue(currentExperimentDto.getExperimentId().equals(experimentDTOFromLink.getExperimentId()));
        }

    }

}
