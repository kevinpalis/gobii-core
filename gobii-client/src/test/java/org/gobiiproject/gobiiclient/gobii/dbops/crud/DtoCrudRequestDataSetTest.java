/// ************************************************************************
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
import org.gobiiproject.gobiiclient.gobii.Helpers.EntityParamValues;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkColl;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkValues;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestDtoFactory;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestUtils;
import org.gobiiproject.gobiimodel.dto.auditable.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;


import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiFilterType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DtoCrudRequestDataSetTest implements DtoCrudRequestTest {

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


//        DtoRequestDataSet dtoRequestDataSet = new DtoRequestDataSet();
//        DataSetDTO dataSetDTORequest = new DataSetDTO();
//        dataSetDTORequest.setDataSetId(2);
//        DataSetDTO dataSetDTOResponse = dtoRequestDataSet.process(dataSetDTORequest);

        Integer dataSetid = (new GlobalPkColl<DtoCrudRequestDataSetTest>().getAPkVal(DtoCrudRequestDataSetTest.class, GobiiEntityNameType.DATASET));

        RestUri projectsUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_DATASETS);
        projectsUri.setParamValue("id", dataSetid.toString());
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForProjects = new GobiiEnvelopeRestResource<>(projectsUri);
        PayloadEnvelope<DataSetDTO> resultEnvelope = gobiiEnvelopeRestResourceForProjects
                .get(DataSetDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        DataSetDTO dataSetDTOResponse = resultEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, dataSetDTOResponse);
        Assert.assertNotEquals(null, dataSetDTOResponse.getDataFile());
        Assert.assertNotNull(dataSetDTOResponse.getCallingAnalysisId());
        Assert.assertTrue(dataSetDTOResponse.getCallingAnalysisId() > 0);
        Assert.assertTrue(dataSetDTOResponse
                .getAnalysesIds()
                .stream()
                .filter(a -> a.equals(null))
                .toArray().length == 0);


    } //

    @Test
    public void testEmptyResult() throws Exception {

        DtoRestRequestUtils<DataSetDTO> dtoDtoRestRequestUtils =
                new DtoRestRequestUtils<>(DataSetDTO.class, RestResourceId.GOBII_DATASETS);
        Integer maxId = dtoDtoRestRequestUtils.getMaxPkVal();
        Integer nonExistentId = maxId + 1;

        PayloadEnvelope<DataSetDTO> resultEnvelope =
                dtoDtoRestRequestUtils.getResponseEnvelopeForEntityId(nonExistentId.toString());

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        Assert.assertNotNull(resultEnvelope.getPayload());
        Assert.assertNotNull(resultEnvelope.getPayload().getData());
        Assert.assertTrue(resultEnvelope.getPayload().getData().size() == 0);
    }

    @Test
    @Override
    public void create() throws Exception {

        RestUri namesUri = GobiiClientContext.getInstance(null, false).getUriFactory().nameIdListByQueryParams();
        namesUri.setParamValue("entity", GobiiEntityNameType.CV.toString().toLowerCase());
        namesUri.setParamValue("filterType", StringUtils.capitalize(GobiiFilterType.NAMES_BY_TYPE_NAME.toString()));
        namesUri.setParamValue("filterValue", "analysis_type");

        GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResourceForAnalysisTerms = new GobiiEnvelopeRestResource<>(namesUri);
        PayloadEnvelope<NameIdDTO> resultEnvelopeAnalysis = gobiiEnvelopeRestResourceForAnalysisTerms
                .get(NameIdDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeAnalysis.getHeader()));
        List<NameIdDTO> analysisTypes = resultEnvelopeAnalysis.getPayload().getData();

        List<NameIdDTO> analysisProperTerms = new ArrayList<>(analysisTypes);
        EntityParamValues entityParamValues = TestDtoFactory
                .makeConstrainedEntityParams(analysisProperTerms, 1);

        // ******** make analyses we'll need for the new data set
        AnalysisDTO analysisDTORequest = TestDtoFactory
                .makePopulatedAnalysisDTO(GobiiProcessType.CREATE, 1, entityParamValues);

        PayloadEnvelope<AnalysisDTO> payloadEnvelopeAnalysis = new PayloadEnvelope<>(analysisDTORequest, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<AnalysisDTO,AnalysisDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_ANALYSIS));
        PayloadEnvelope<AnalysisDTO> analysisDTOResponseEnvelope = gobiiEnvelopeRestResource.post(AnalysisDTO.class,
                payloadEnvelopeAnalysis);
        AnalysisDTO callingAnalysisDTO = analysisDTOResponseEnvelope.getPayload().getData().get(0);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(analysisDTOResponseEnvelope.getHeader()));

        List<AnalysisDTO> analyses = new ArrayList<>();
        analyses.add(TestDtoFactory
                .makePopulatedAnalysisDTO(GobiiProcessType.CREATE,
                        2,
                        entityParamValues));
        analyses.add(TestDtoFactory
                .makePopulatedAnalysisDTO(GobiiProcessType.CREATE,
                        3,
                        entityParamValues));
        analyses.add(TestDtoFactory
                .makePopulatedAnalysisDTO(GobiiProcessType.CREATE,
                        4,
                        entityParamValues));

        List<Integer> analysisIds = new ArrayList<>();
        for (AnalysisDTO currentAnalysis : analyses) {

            payloadEnvelopeAnalysis = new PayloadEnvelope<>(currentAnalysis, GobiiProcessType.CREATE);
            gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceColl(RestResourceId.GOBII_ANALYSIS));
            analysisDTOResponseEnvelope = gobiiEnvelopeRestResource.post(AnalysisDTO.class,
                    payloadEnvelopeAnalysis);
            AnalysisDTO createdAnalysis = analysisDTOResponseEnvelope.getPayload().getData().get(0);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(analysisDTOResponseEnvelope.getHeader()));

            analysisIds.add(createdAnalysis.getAnalysisId());
        }


        // ********** make raw data set dto and add anlyses
        //DtoRequestDataSet dtoRequestDataSet = new DtoRequestDataSet();
        DataSetDTO dataSetDTORequest = TestDtoFactory
                .makePopulatedDataSetDTO(1,
                        callingAnalysisDTO.getAnalysisId(),
                        analysisIds);

        RestUri projectsCollUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_DATASETS);
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForDataSetPost = new GobiiEnvelopeRestResource<>(projectsCollUri);
        PayloadEnvelope<DataSetDTO> resultEnvelope = gobiiEnvelopeRestResourceForDataSetPost
                .post(DataSetDTO.class, new PayloadEnvelope<>(dataSetDTORequest, GobiiProcessType.CREATE));

        //DataSetDTO dataSetDTOResponse = dtoRequestDataSet.process(dataSetDTORequest);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        DataSetDTO dataSetDTOResponse = resultEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, dataSetDTOResponse);
        Assert.assertTrue(dataSetDTOResponse.getDataSetId() > 0);
        Assert.assertTrue(dataSetDTOResponse.getCallingAnalysisId() > 0);
        Assert.assertNotNull(dataSetDTOResponse.getAnalysesIds());
        Assert.assertTrue(dataSetDTOResponse.getAnalysesIds().size() > 0);
        Assert.assertTrue(dataSetDTOResponse.getDatatypeId() > 0);

        GlobalPkValues.getInstance().addPkVal(GobiiEntityNameType.DATASET, dataSetDTOResponse.getDataSetId());


//        DataSetDTO dataSetDTOReRequest = new DataSetDTO();
//        dataSetDTOReRequest.setDataSetId(dataSetDTOResponse.getDataSetId());
//        //DataSetDTO dataSetDTOReResponse = dtoRequestDataSet.process(dataSetDTOReRequest);


        RestUri projectsByIdUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_DATASETS);
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForDataSetGet = new GobiiEnvelopeRestResource<>(projectsByIdUri);
        gobiiEnvelopeRestResourceForDataSetGet.setParamValue("id", dataSetDTOResponse.getDataSetId().toString());
        resultEnvelope = gobiiEnvelopeRestResourceForDataSetGet
                .get(DataSetDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

        DataSetDTO dataSetDTOReResponse = resultEnvelope.getPayload().getData().get(0);

        Assert.assertNotEquals(null, dataSetDTOReResponse);
        Assert.assertTrue(dataSetDTOReResponse.getDataSetId() > 0);
        Assert.assertTrue(dataSetDTOReResponse.getCallingAnalysisId() > 0);
        Assert.assertNotNull(dataSetDTOReResponse.getAnalysesIds());
        Assert.assertTrue(dataSetDTOReResponse.getAnalysesIds().size() > 0);
        Assert.assertTrue(0 == dataSetDTOReResponse
                .getAnalysesIds()
                .stream()
                .filter(a -> a.equals(null))
                .count());

    }

    @Test
    @Override
    public void update() throws Exception {

        // ******** make analyses we'll need for the new data set
        RestUri namesUri = GobiiClientContext.getInstance(null, false).getUriFactory().nameIdListByQueryParams();
        namesUri.setParamValue("entity", GobiiEntityNameType.CV.toString().toLowerCase());
        namesUri.setParamValue("filterType", StringUtils.capitalize(GobiiFilterType.NAMES_BY_TYPE_NAME.toString()));
        namesUri.setParamValue("filterValue", "analysis_type");

        GobiiEnvelopeRestResource<NameIdDTO,NameIdDTO> gobiiEnvelopeRestResourceForAnalysisTerms = new GobiiEnvelopeRestResource<>(namesUri);
        PayloadEnvelope<NameIdDTO> resultEnvelopeAnalysis = gobiiEnvelopeRestResourceForAnalysisTerms
                .get(NameIdDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeAnalysis.getHeader()));
        List<NameIdDTO> analysisTypes = resultEnvelopeAnalysis.getPayload().getData();

        List<NameIdDTO> analysisProperTerms = new ArrayList<>(analysisTypes);
        EntityParamValues entityParamValues = TestDtoFactory
                .makeConstrainedEntityParams(analysisProperTerms, 1);

        AnalysisDTO analysisDTORequest = TestDtoFactory
                .makePopulatedAnalysisDTO(GobiiProcessType.CREATE, 1, entityParamValues);

        PayloadEnvelope<AnalysisDTO> payloadEnvelopeAnalysis = new PayloadEnvelope<>(analysisDTORequest, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<AnalysisDTO,AnalysisDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_ANALYSIS));
        PayloadEnvelope<AnalysisDTO> analysisDTOResponseEnvelope = gobiiEnvelopeRestResource.post(AnalysisDTO.class,
                payloadEnvelopeAnalysis);
        AnalysisDTO newCallingAnalysisDTO = analysisDTOResponseEnvelope.getPayload().getData().get(0);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(analysisDTOResponseEnvelope.getHeader()));

        List<AnalysisDTO> analysesToCreate = new ArrayList<>();
        List<AnalysisDTO> analysesNew = new ArrayList<>();
        analysesToCreate.add(TestDtoFactory
                .makePopulatedAnalysisDTO(GobiiProcessType.CREATE,
                        2,
                        entityParamValues));
        analysesToCreate.add(TestDtoFactory
                .makePopulatedAnalysisDTO(GobiiProcessType.CREATE,
                        3,
                        entityParamValues));
        analysesToCreate.add(TestDtoFactory
                .makePopulatedAnalysisDTO(GobiiProcessType.CREATE,
                        4,
                        entityParamValues));

        List<Integer> analysisIds = new ArrayList<>();
        for (AnalysisDTO currentAnalysis : analysesToCreate) {

            payloadEnvelopeAnalysis = new PayloadEnvelope<>(currentAnalysis, GobiiProcessType.CREATE);
            gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceColl(RestResourceId.GOBII_ANALYSIS));
            analysisDTOResponseEnvelope = gobiiEnvelopeRestResource.post(AnalysisDTO.class,
                    payloadEnvelopeAnalysis);
            AnalysisDTO newAnalysis = analysisDTOResponseEnvelope.getPayload().getData().get(0);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(analysisDTOResponseEnvelope.getHeader()));

            analysesNew.add(newAnalysis);
            analysisIds.add(newAnalysis.getAnalysisId());
        }


        //DtoRequestDataSet dtoRequestDataSet = new DtoRequestDataSet();

        // create a new aataSet for our test
        DataSetDTO newDataSetDto = TestDtoFactory
                .makePopulatedDataSetDTO(1,
                        newCallingAnalysisDTO.getAnalysisId(),
                        analysisIds);

        //DataSetDTO newDataSetDTOResponse = dtoRequestDataSet.process(newDataSetDto);


        RestUri projectsCollUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_DATASETS);
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForDataSetPost = new GobiiEnvelopeRestResource<>(projectsCollUri);
        PayloadEnvelope<DataSetDTO> resultEnvelope = gobiiEnvelopeRestResourceForDataSetPost
                .post(DataSetDTO.class, new PayloadEnvelope<>(newDataSetDto, GobiiProcessType.CREATE));


        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));


        DataSetDTO newDataSetDTOResponse = resultEnvelope.getPayload().getData().get(0);

        // re-retrieve the aataSet we just created so we start with a fresh READ mode dto
//        DataSetDTO dataSetDTORequest = new DataSetDTO();
//        dataSetDTORequest.setDataSetId(newDataSetDTOResponse.getDataSetId());
//        DataSetDTO dataSetDTOReceived = dtoRequestDataSet.process(dataSetDTORequest);

        RestUri projectsByIdUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_DATASETS);
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForDataSetById = new GobiiEnvelopeRestResource<>(projectsByIdUri);
        gobiiEnvelopeRestResourceForDataSetById.setParamValue("id", newDataSetDTOResponse.getDataSetId().toString());
        resultEnvelope = gobiiEnvelopeRestResourceForDataSetById
                .get(DataSetDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

        DataSetDTO dataSetDTOReceived = resultEnvelope.getPayload().getData().get(0);

        // so this would be the typical workflow for the client app
        String newDataFile = UUID.randomUUID().toString();
        dataSetDTOReceived.setDataFile(newDataFile);
        Integer anlysisIdRemovedFromList = dataSetDTOReceived.getAnalysesIds().remove(0);
        Integer newCallingAnalysisId = anlysisIdRemovedFromList;
        dataSetDTOReceived.setCallingAnalysisId(newCallingAnalysisId);


        //DataSetDTO dataSetDTOResponse = dtoRequestDataSet.process(dataSetDTOReceived);

        resultEnvelope = gobiiEnvelopeRestResourceForDataSetById
                .put(DataSetDTO.class, new PayloadEnvelope<>(dataSetDTOReceived, GobiiProcessType.UPDATE));

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

        DataSetDTO dataSetDTOResponse = resultEnvelope.getPayload().getData().get(0);

        gobiiEnvelopeRestResourceForDataSetById.setParamValue("id", dataSetDTOResponse.getDataSetId().toString());
        resultEnvelope = gobiiEnvelopeRestResourceForDataSetById
                .get(DataSetDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        DataSetDTO dtoRequestDataSetReRetrieved = resultEnvelope.getPayload().getData().get(0);

//        dataSetDTORequest.setGobiiProcessType(GobiiProcessType.READ);
//        dataSetDTORequest.setDataSetId(dataSetDTOResponse.getDataSetId());
//        DataSetDTO dtoRequestDataSetReRetrieved =
//                dtoRequestDataSet.process(dataSetDTORequest);

        Assert.assertTrue(dtoRequestDataSetReRetrieved.getDataSetId().equals(dataSetDTOReceived.getDataSetId()));
        Assert.assertTrue(dtoRequestDataSetReRetrieved.getDataFile().equals(newDataFile));
        Assert.assertTrue(dtoRequestDataSetReRetrieved.getCallingAnalysisId().equals(newCallingAnalysisId));
        Assert.assertTrue(dtoRequestDataSetReRetrieved
                .getAnalysesIds()
                .stream()
                .filter(a -> a.equals(anlysisIdRemovedFromList))
                .toArray().length == 0);
    }


    @Test
    @Override
    public void getList() throws Exception {

        RestUri restUriDataSet = GobiiClientContext.getInstance(null, false)
                .getUriFactory().resourceColl(RestResourceId.GOBII_DATASETS);
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriDataSet);
        PayloadEnvelope<DataSetDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(DataSetDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<DataSetDTO> dataSetDTOList = resultEnvelope.getPayload().getData();
        Assert.assertNotNull(dataSetDTOList);
        Assert.assertTrue(dataSetDTOList.size() > 0);
        Assert.assertNotNull(dataSetDTOList.get(0).getDatasetName());


        LinkCollection linkCollection = resultEnvelope.getPayload().getLinkCollection();
        Assert.assertTrue(linkCollection.getLinksPerDataItem().size() == dataSetDTOList.size());

        List<Integer> itemsToTest = new ArrayList<>();
        if (dataSetDTOList.size() > 50) {
            itemsToTest = TestUtils.makeListOfIntegersInRange(10, dataSetDTOList.size());

        } else {
            for (int idx = 0; idx < dataSetDTOList.size(); idx++) {
                itemsToTest.add(idx);
            }
        }

        for (Integer currentIdx : itemsToTest) {
            DataSetDTO currentDataSetDto = dataSetDTOList.get(currentIdx);

            Link currentLink = linkCollection.getLinksPerDataItem().get(currentIdx);

            RestUri restUriDataSetForGetById = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .RestUriFromUri(currentLink.getHref());
            GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriDataSetForGetById);
            PayloadEnvelope<DataSetDTO> resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                    .get(DataSetDTO.class);
            Assert.assertNotNull(resultEnvelopeForGetByID);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
            DataSetDTO dataSetDTOFromLink = resultEnvelopeForGetByID.getPayload().getData().get(0);
            Assert.assertTrue(currentDataSetDto.getDatasetName().equals(dataSetDTOFromLink.getDatasetName()));
            Assert.assertTrue(currentDataSetDto.getDataSetId().equals(dataSetDTOFromLink.getDataSetId()));

            Assert.assertNotNull(dataSetDTOFromLink.getAnalysesIds());
            for (Integer currentAnalysisId : dataSetDTOFromLink.getAnalysesIds()) {

                RestUri restUriAnalysisForGetById = GobiiClientContext.getInstance(null, false)
                        .getUriFactory()
                        .resourceByUriIdParam(RestResourceId.GOBII_ANALYSIS);
                restUriAnalysisForGetById.setParamValue("id", currentAnalysisId.toString());
                GobiiEnvelopeRestResource<AnalysisDTO,AnalysisDTO> gobiiEnvelopeRestResourceAnalysisForGetById = new GobiiEnvelopeRestResource<>(restUriAnalysisForGetById);
                PayloadEnvelope<AnalysisDTO> resultEnvelopeForGetById = gobiiEnvelopeRestResourceAnalysisForGetById
                        .get(AnalysisDTO.class);

                Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
                AnalysisDTO analysisDTO = resultEnvelopeForGetById.getPayload().getData().get(0);
                Assert.assertTrue(analysisDTO.getAnalysisId() > 0);
                Assert.assertNotNull(analysisDTO.getAnalysisName());

            }

        }

    }

    @Test
    public void getPagedList() throws Exception {


        // This our first sorting implementaiton. So we need to do some extensive tesing.
        // In order for this to work, we need to know what dataset records are in the system.
        // And if there aren't enough to test paging with, we will need to create some.
        // We're going to start with the GET for all datasets. Since we don't assume the
        // sort order order for that method, we're going to have to secondarily resort the
        // records here. So by the end of this setup phase, we will have N dataset records with
        // known IDs and known sort order. Then we will get the dataset records via the paging
        // method and make sure that the page boundaries line up where we expect them to.
        Integer minimalRequiredDatasetsForTest = 100;

        RestUri restUriDataSet = GobiiClientContext.getInstance(null, false)
                .getUriFactory().resourceColl(RestResourceId.GOBII_DATASETS);
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(restUriDataSet);
        PayloadEnvelope<DataSetDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(DataSetDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        List<DataSetDTO> retrievedDataSets = resultEnvelope.getPayload().getData();


        // if we don't have enough datasets for our test, created new ones
        if (retrievedDataSets.size() < minimalRequiredDatasetsForTest) {
            Integer totalNewRequired = minimalRequiredDatasetsForTest - retrievedDataSets.size();
            (new GlobalPkColl<DtoCrudRequestDataSetTest>()).getPkVals(DtoCrudRequestDataSetTest.class, GobiiEntityNameType.DATASET, totalNewRequired);
        }


        // since other unit tets have given these records arbitrary and meaningless names, we will
        // now rename them all in way that makes it easier to debug and evaluate how, given a specific
        // sort, paging is working
        Assert.assertTrue("The total number of test records cannot exceed " + (new Integer(26 * 26)).toString(),
                retrievedDataSets.size() <= (26 * 26));
        List<String> sortedNames = new ArrayList<>();
        Integer totalNames = 0;
        for (char leftCharacter = 65; leftCharacter <= 90 && totalNames < retrievedDataSets.size(); leftCharacter++) {
            for (char rightCharacter = 65; rightCharacter <= 90 && totalNames < retrievedDataSets.size(); rightCharacter++) {
                String currentName = Character.toString(leftCharacter) + Character.toString(rightCharacter);
                sortedNames.add(currentName + " (" + totalNames + ")");
                totalNames++;
            }
        }


        //now update the names accordingly
        for (Integer idx = 0; idx < retrievedDataSets.size(); idx++) {
            DataSetDTO currentDataset = retrievedDataSets.get(idx);
            String currentName = sortedNames.get(idx);
            currentDataset.setDatasetName(currentName);

            RestUri datasetByIdUri = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceByUriIdParam(RestResourceId.GOBII_DATASETS)
                    .setParamValue("id", currentDataset.getDataSetId().toString());
            GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForDataSetById = new GobiiEnvelopeRestResource<>(datasetByIdUri);
            resultEnvelope = gobiiEnvelopeRestResourceForDataSetById
                    .put(DataSetDTO.class, new PayloadEnvelope<>(currentDataset, GobiiProcessType.UPDATE));
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        }


        // now retrieve the datasets and sort the same way that the paged query sorts -- by name and ID
        //we'll compare the paging result with the sorted list
        resultEnvelope = gobiiEnvelopeRestResource
                .get(DataSetDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        retrievedDataSets = resultEnvelope.getPayload().getData();
        List<DataSetDTO> sortedDataSets = retrievedDataSets
                .stream()
                .sorted(Comparator.comparing(DataSetDTO::getDatasetName).thenComparing(DataSetDTO::getDataSetId))
                .collect(Collectors.toList());

        final Integer pageSize = 4;
        Map<Integer, List<DataSetDTO>> pageMap = new LinkedHashMap<>();
        Integer currentPage = 0;
        for (Integer idx = 0; idx < sortedDataSets.size(); idx++) {

            List<DataSetDTO> currentList;
            Integer currentMod = idx % pageSize;
            if (currentMod == 0) {
                currentList = new ArrayList<>();
                pageMap.put(++currentPage, currentList);
            } else {
                currentList = pageMap.get(currentPage);
            }

            currentList.add(sortedDataSets.get(idx));
        }

        // now iterate pages, per page map
        List<Integer> pageList = new ArrayList<>(pageMap.keySet());
        String queryKey = null;
        for (Integer currentPageKey : pageList) {

            Integer pageNo = currentPageKey - 1; // pages are zero based
            RestUri pagedUriDataSet = GobiiClientContext.getInstance(null, false)
                    .getUriFactory().pagedList(RestResourceId.GOBII_DATASETS,
                            pageSize,
                            pageNo,
                            queryKey);
            GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForPaged = new GobiiEnvelopeRestResource<>(pagedUriDataSet);
            PayloadEnvelope<DataSetDTO> resultEnvelopeForPaged = gobiiEnvelopeRestResourceForPaged
                    .get(DataSetDTO.class);

            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForPaged.getHeader()));

            Assert.assertNotNull("Error in pagination object: Current page is not set",
                    resultEnvelopeForPaged.getHeader().getPagination().getCurrentPage());

            Assert.assertNotNull("Error in pagination object: Page size is not set",
                    resultEnvelopeForPaged.getHeader().getPagination().getPageSize());

            Assert.assertNotNull("Error in pagination object: Query ID is not set",
                    resultEnvelopeForPaged.getHeader().getPagination().getPagedQueryId());

            Assert.assertNotNull("Error in pagination object: Query Time is not set",
                    resultEnvelopeForPaged.getHeader().getPagination().getQueryTime());

            Assert.assertNotNull("Error in pagination object: Total pages is not set",
                    resultEnvelopeForPaged.getHeader().getPagination().getTotalPages());


            if (pageNo == 0) {
                queryKey = resultEnvelopeForPaged.getHeader().getPagination().getPagedQueryId();
            } else {
                Assert.assertTrue("The query key for subsquent pages did not match the one retrieved for the first p[page: " +
                                queryKey + " initial vs." + resultEnvelopeForPaged.getHeader().getPagination().getPagedQueryId() + " subsequent",
                        queryKey.equals(resultEnvelopeForPaged.getHeader().getPagination().getPagedQueryId()));
            }

            List<DataSetDTO> currentPageDTOs = resultEnvelopeForPaged.getPayload().getData();
            List<DataSetDTO> currentReferenceDTOs = pageMap.get(currentPageKey); // but reference the map by current key

            Assert.assertTrue("The retrieved page datasets differs in length from the refereference datasets",
                    currentPageDTOs.size() == currentReferenceDTOs.size());

            for (Integer idx = 0; idx < currentPageDTOs.size(); idx++) {
                DataSetDTO currentRetrievedDto = currentPageDTOs.get(idx);
                DataSetDTO currentReferenceDTO = currentReferenceDTOs.get(idx);

                Assert.assertTrue("The names of the page item do not match for page # " + pageNo,
                        currentRetrievedDto.getDatasetName().contains(currentReferenceDTO.getDatasetName()));

                Assert.assertEquals("The dataset Ids of the page item do not match",
                        currentReferenceDTO.getDataSetId(), currentRetrievedDto.getDataSetId());


            }


        } // iterate pages

        Integer exceedingPageNo = pageMap.keySet().size(); // pages are zero based
        RestUri pagedUriDataSet = GobiiClientContext.getInstance(null, false)
                .getUriFactory().pagedList(RestResourceId.GOBII_DATASETS,
                        pageSize,
                        exceedingPageNo + 1,
                        queryKey);
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForPaged = new GobiiEnvelopeRestResource<>(pagedUriDataSet);
        PayloadEnvelope<DataSetDTO> resultEnvelopeForPaged = gobiiEnvelopeRestResourceForPaged
                .get(DataSetDTO.class);


        // should have failed
        Assert.assertFalse(resultEnvelopeForPaged.getHeader().getStatus().isSucceeded());


        Assert.assertTrue(
        resultEnvelopeForPaged
                .getHeader()
                .getStatus()
                .getStatusMessages()
                .stream()
                .filter(sm -> sm.getMessage().contains("exceeds the number of available pages"))
                .count() > 0 );

        //Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForPaged.getHeader()));

    } // getPagedList()

    @Test
    public void getDataSetsByTypeId() throws Exception {

        Integer dataSetid = (new GlobalPkColl<DtoCrudRequestDataSetTest>().getAPkVal(DtoCrudRequestDataSetTest.class, GobiiEntityNameType.DATASET));

        RestUri restUriForDataSets = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_DATASETS);
        restUriForDataSets.setParamValue("id", dataSetid.toString());
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForDataSet = new GobiiEnvelopeRestResource<>(restUriForDataSets);
        PayloadEnvelope<DataSetDTO> resultEnvelopeDataSet = gobiiEnvelopeRestResourceForDataSet
                .get(DataSetDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeDataSet.getHeader()));
        DataSetDTO dataSetDTOResponse = resultEnvelopeDataSet.getPayload().getData().get(0);

        Integer typeId = dataSetDTOResponse.getDatatypeId();

        RestUri restUriForDataTypes = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_DATASETTYPES)
                .addUriParam("id")
                .setParamValue("id", typeId.toString());

        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForDataTypes = new GobiiEnvelopeRestResource<>(restUriForDataTypes);
        PayloadEnvelope<DataSetDTO> resultEnvelopeDataTypes = gobiiEnvelopeRestResourceForDataTypes
                .get(DataSetDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeDataTypes.getHeader()));

        List<DataSetDTO> dataSetDTOList = resultEnvelopeDataTypes.getPayload().getData();

        Assert.assertNotNull(dataSetDTOList);
        Assert.assertTrue(dataSetDTOList.size() >= 0);

        if (dataSetDTOList.size() > 0) {
            Assert.assertNotNull(dataSetDTOList.get(0).getDatasetName());
        }

        LinkCollection linkCollection = resultEnvelopeDataTypes.getPayload().getLinkCollection();
        Assert.assertTrue(linkCollection.getLinksPerDataItem().size() == dataSetDTOList.size());

        List<Integer> itemsToTest = new ArrayList<>();
        if (dataSetDTOList.size() > 50) {
            itemsToTest = TestUtils.makeListOfIntegersInRange(10, dataSetDTOList.size());

        } else {
            for (int idx = 0; idx < dataSetDTOList.size(); idx++) {
                itemsToTest.add(idx);
            }
        }

        for (Integer currentIdx : itemsToTest) {
            DataSetDTO currentDatasetDto = dataSetDTOList.get(currentIdx);

            Link currentLink = linkCollection.getLinksPerDataItem().get(currentIdx);

            RestUri restUriDForGetById = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .RestUriFromUri(currentLink.getHref());
            GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriDForGetById);
            PayloadEnvelope<DataSetDTO> resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                    .get(DataSetDTO.class);
            Assert.assertNotNull(resultEnvelopeForGetByID);
            Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
            DataSetDTO dataDTOFromLink = resultEnvelopeForGetByID.getPayload().getData().get(0);
            Assert.assertTrue(currentDatasetDto.getDatasetName().equals(dataDTOFromLink.getDatasetName()));
            Assert.assertTrue(currentDatasetDto.getDataSetId().equals(dataDTOFromLink.getDataSetId()));
        }

    }
}
