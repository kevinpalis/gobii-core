// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-25
// ************************************************************************
package org.gobiiproject.gobiiclient.gobii.infrastructure;

import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiTestConfiguration;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.gobii.Helpers.*;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.ServerConfigItem;
import org.gobiiproject.gobiimodel.dto.noaudit.CvDTO;
import org.gobiiproject.gobiimodel.dto.system.PingDTO;

import org.gobiiproject.gobiimodel.dto.system.ConfigSettingsDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class DtoRequestMultiDbTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.authenticate());
    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }


    @Test
    public void testGetPingDatabaseConfig() throws Exception {

//        ConfigSettings configSettings = new GobiiTestConfiguration().getConfigSettings(); // we're deliberately going to the source instead of using GobiiClientContext


//        List<String> activeCropTypes = configSettings
//                .getActiveCropConfigs()
//                .stream()
//                .map(GobiiCropConfig::getGobiiCropType)
//                .collect(Collectors.toList());


        RestUri confgSettingsUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_CONFIGSETTINGS);

        GobiiEnvelopeRestResource<ConfigSettingsDTO,ConfigSettingsDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(confgSettingsUri);
        PayloadEnvelope<ConfigSettingsDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(ConfigSettingsDTO.class);

        TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader());
        Assert.assertTrue("Config settings were not retrieved",
                resultEnvelope.getPayload().getData().size() > 0);
        ConfigSettingsDTO configSettingsDTOResponse = resultEnvelope.getPayload().getData().get(0);


        PingDTO pingDTORequest = TestDtoFactory.makePingDTO();
        for (ServerConfigItem currentServerConfigItem : configSettingsDTOResponse.getServerConfigs().values()) {

            // should cause server to assign the correct datasource
            String currentCropType = currentServerConfigItem.getGobiiCropType();
            Assert.assertTrue(GobiiClientContextAuth.authenticate(currentCropType));


            //DtoRequestPing dtoRequestPing = new DtoRequestPing();
            GobiiEnvelopeRestResource<PingDTO,PingDTO> gobiiEnvelopeRestResourcePingDTO = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceColl(RestResourceId.GOBII_PING));

            PayloadEnvelope<PingDTO> resultEnvelopePing = gobiiEnvelopeRestResourcePingDTO.post(PingDTO.class,
                    new PayloadEnvelope<>(pingDTORequest, GobiiProcessType.CREATE));
            //PayloadEnvelope<ContactDTO> resultEnvelopeNewContact = dtoRequestContact.process(new PayloadEnvelope<>(newContactDto, GobiiProcessType.CREATE));

            Assert.assertNotNull(resultEnvelopePing);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopePing.getHeader()));
            Assert.assertTrue(resultEnvelopePing.getPayload().getData().size() > 0);
            PingDTO currentPingDTOResponse = resultEnvelopePing.getPayload().getData().get(0);


            Assert.assertNotNull("The ping response does not contain the db url for crop "
                    + currentPingDTOResponse.getDbMetaData());

            Assert.assertTrue("The ping response contains null data ",
                    + currentPingDTOResponse
                    .getDbMetaData()
                    .stream()
                    .filter(item -> LineUtils.isNullOrEmpty(item))
                    .count() == 0);

        }

    }


    @Test
    public void testCreateCvOnMultipleDb() throws Exception {

        ConfigSettings configSettings = new GobiiTestConfiguration().getConfigSettings(); // we're deliberately going to the source instead of using GobiiClientContext


        List<String> activeCropTypes = configSettings
                .getActiveCropConfigs()
                .stream()
                .map(GobiiCropConfig::getGobiiCropType)
                .collect(Collectors.toList());


        for (String currentCropType : activeCropTypes) {

            // should cause server to assign the correct datasource
            Assert.assertTrue(GobiiClientContextAuth.authenticate(currentCropType));

            CvDTO currentCvDtoRequest = TestDtoFactory
                    .makePopulatedCvDTO(GobiiProcessType.CREATE, 1);
            currentCvDtoRequest.setDefinition("Destination DB should be: " + currentCropType.toString());

            PayloadEnvelope<CvDTO> payloadEnvelope = new PayloadEnvelope<>(currentCvDtoRequest, GobiiProcessType.CREATE);
            GobiiEnvelopeRestResource<CvDTO,CvDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceColl(RestResourceId.GOBII_CV));
            PayloadEnvelope<CvDTO> cvDTOResponseEnvelope = gobiiEnvelopeRestResource.post(CvDTO.class,
                    payloadEnvelope);
            CvDTO cvDTOResponse = cvDTOResponseEnvelope.getPayload().getData().get(0);

            Assert.assertNotEquals(null, cvDTOResponse);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(cvDTOResponseEnvelope.getHeader()));
            Assert.assertTrue(cvDTOResponse.getCvId() > 0);

            GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.CV,
                    cvDTOResponse.getCvId());


            RestUri restUriCvForGetById = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceByUriIdParam(RestResourceId.GOBII_CV);
            restUriCvForGetById.setParamValue("id", cvDTOResponse.getCvId().toString());
            GobiiEnvelopeRestResource<CvDTO,CvDTO> restResourceForGetById = new GobiiEnvelopeRestResource<>(restUriCvForGetById);
            PayloadEnvelope<CvDTO> resultEnvelopeForGetByID = restResourceForGetById
                    .get(CvDTO.class);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
            CvDTO cvDTOResponseForParams = resultEnvelopeForGetByID.getPayload().getData().get(0);

            GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.CV, cvDTOResponse.getCvId());

        }
    }


}
