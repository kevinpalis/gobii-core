package org.gobiiproject.gobiiclient.gobii.instructions;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiiapimodel.hateos.LinkCollection;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.restresources.gobii.GobiiUriFactory;
import org.gobiiproject.gobiiclient.gobii.Helpers.EntityParamValues;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestDtoFactory;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestUtils;
import org.gobiiproject.gobiimodel.dto.auditable.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.children.PropNameId;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.ExtractorInstructionFilesDTO;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.GobiiDataSetExtract;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.GobiiExtractorInstruction;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.types.*;
import org.gobiiproject.gobiimodel.utils.DateUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Phil on 6/15/2016.
 */
public class DtoRequestFileExtractorInstructionsTest {

    //private final String INSTRUCTION_FILE_EXT = ".json";
    private static GobiiUriFactory gobiiUriFactory;


    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.authenticate());
        String currentCropContextRoot = GobiiClientContext.getInstance(null, false).getCurrentCropContextRoot();
        gobiiUriFactory = new GobiiUriFactory(currentCropContextRoot, GobiiClientContext.getInstance(null, false).getCurrentClientCropType());
    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }


    @Test// fails on SYS_INT
    public void testSendExtractorInstructionFile() throws Exception {


        String cropTypeFromContext = GobiiClientContext.getInstance(null, false).getCurrentClientCropType();

        // ************** DEFINE DTO
        ExtractorInstructionFilesDTO extractorInstructionFilesDTOToSend = new ExtractorInstructionFilesDTO();


        String instructionFileName = "testapp_" + DateUtils.makeDateIdString();
        extractorInstructionFilesDTOToSend.setInstructionFileName(instructionFileName);

        // ****** Setup dataset
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




        // ************** INSTRUCTION ONE
        GobiiExtractorInstruction gobiiExtractorInstructionOne = new GobiiExtractorInstruction();
        gobiiExtractorInstructionOne.setContactId(1);
        gobiiExtractorInstructionOne.setGobiiCropType(cropTypeFromContext);

        // ************** DATA SET EXTRACT ONE
        DataSetDTO dataSetDTORequest = TestDtoFactory
            .makePopulatedDataSetDTO(1,
                callingAnalysisDTO.getAnalysisId(),
                analysisIds);

        RestUri projectsCollUri = GobiiClientContext.getInstance(null, false)
            .getUriFactory()
            .resourceColl(RestResourceId.GOBII_DATASETS);
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForDataSetPost = new GobiiEnvelopeRestResource<>(projectsCollUri);
        PayloadEnvelope<DataSetDTO> resultEnvelopeDataset = gobiiEnvelopeRestResourceForDataSetPost
            .post(DataSetDTO.class, new PayloadEnvelope<>(dataSetDTORequest, GobiiProcessType.CREATE));

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeDataset.getHeader()));
        DataSetDTO dataSetDTOResponse = resultEnvelopeDataset.getPayload().getData().get(0);


        GobiiDataSetExtract gobiiDataSetExtractOne = new GobiiDataSetExtract();
        gobiiDataSetExtractOne.setGobiiExtractFilterType(GobiiExtractFilterType.WHOLE_DATASET);
        GobiiFileType DataSetExtractOneFileType = GobiiFileType.HAPMAP;
        gobiiDataSetExtractOne.setGobiiFileType(DataSetExtractOneFileType);
        String dataSetExtractOneName = dataSetDTOResponse.getDatasetName();
        gobiiDataSetExtractOne.setDataSet(new PropNameId(dataSetDTOResponse.getId(),dataSetDTOResponse.getDatasetName()));
        gobiiDataSetExtractOne.setAccolate(true);


        // ************** DATA SET EXTRACT two

        DataSetDTO dataSetDTORequest2 = TestDtoFactory
            .makePopulatedDataSetDTO(2,
                callingAnalysisDTO.getAnalysisId(),
                analysisIds);


        gobiiEnvelopeRestResourceForDataSetPost = new GobiiEnvelopeRestResource<>(projectsCollUri);
        resultEnvelopeDataset = gobiiEnvelopeRestResourceForDataSetPost
            .post(DataSetDTO.class, new PayloadEnvelope<>(dataSetDTORequest, GobiiProcessType.CREATE));

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeDataset.getHeader()));
        DataSetDTO dataSetDTOResponse2 = resultEnvelopeDataset.getPayload().getData().get(0);



        GobiiDataSetExtract gobiiDataSetExtractTwo = new GobiiDataSetExtract();
        gobiiDataSetExtractTwo.setGobiiExtractFilterType(GobiiExtractFilterType.WHOLE_DATASET);
        GobiiFileType DataSetExtractFileTypeTwo = GobiiFileType.FLAPJACK;
        gobiiDataSetExtractTwo.setGobiiFileType(DataSetExtractFileTypeTwo);
        String DataSetExtractNameTwo = dataSetDTOResponse2.getDatasetName();
        gobiiDataSetExtractTwo.setAccolate(true);
        //gobiiDataSetExtractTwo.getMarkerList().add("m1");
        //gobiiDataSetExtractTwo.getSampleList().add("s1");
        gobiiDataSetExtractTwo.setDataSet(new PropNameId(dataSetDTOResponse2.getId(),dataSetDTOResponse2.getDatasetName()));


        gobiiExtractorInstructionOne.getDataSetExtracts().add(gobiiDataSetExtractOne);
        gobiiExtractorInstructionOne.getDataSetExtracts().add(gobiiDataSetExtractTwo);

        extractorInstructionFilesDTOToSend.getGobiiExtractorInstructions().add(gobiiExtractorInstructionOne);


        // INSTRUCTION ONE END
        // **********************************************************************


        // **********************************************************************
        // INSTRUCTION TWO BEGIN
        GobiiExtractorInstruction gobiiExtractorInstructionTwo = new GobiiExtractorInstruction();
        gobiiExtractorInstructionTwo.setContactId(1);
        // gobiiExtractorInstructionTwo.setGobiiCropType(cropTypeFromContext); // DON'T SET IT # 2

        // column one
        gobiiDataSetExtractOne = new GobiiDataSetExtract();
        gobiiDataSetExtractOne.setGobiiExtractFilterType(GobiiExtractFilterType.WHOLE_DATASET);
        gobiiDataSetExtractOne.setAccolate(true);
        gobiiDataSetExtractOne.setGobiiFileType(DataSetExtractOneFileType);
        gobiiDataSetExtractOne.setDataSet(new PropNameId(dataSetDTOResponse.getId(),dataSetDTOResponse.getDatasetName()));

        // column two
        gobiiDataSetExtractTwo = new GobiiDataSetExtract();
        gobiiDataSetExtractTwo.setGobiiExtractFilterType(GobiiExtractFilterType.WHOLE_DATASET);
        gobiiDataSetExtractTwo.setAccolate(true);
        gobiiDataSetExtractTwo.setGobiiFileType(DataSetExtractFileTypeTwo);
        gobiiDataSetExtractTwo.setDataSet(new PropNameId(dataSetDTOResponse2.getId(),dataSetDTOResponse2.getDatasetName()));


        gobiiExtractorInstructionTwo.getDataSetExtracts().add(gobiiDataSetExtractOne);
        gobiiExtractorInstructionTwo.getDataSetExtracts().add(gobiiDataSetExtractTwo);

        extractorInstructionFilesDTOToSend.getGobiiExtractorInstructions().add(gobiiExtractorInstructionTwo);

        PayloadEnvelope<ExtractorInstructionFilesDTO> payloadEnvelope = new PayloadEnvelope<>(extractorInstructionFilesDTOToSend, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<ExtractorInstructionFilesDTO,ExtractorInstructionFilesDTO> gobiiEnvelopeRestResourceForPost = new GobiiEnvelopeRestResource<>(gobiiUriFactory
                .resourceColl(RestResourceId.GOBII_FILE_EXTRACTOR_INSTRUCTIONS));
        PayloadEnvelope<ExtractorInstructionFilesDTO> extractorInstructionFileDTOResponseEnvelope = gobiiEnvelopeRestResourceForPost.post(ExtractorInstructionFilesDTO.class,
                payloadEnvelope);

        //DtoRequestFileExtractorInstructions dtoRequestFileExtractorInstructions = new DtoRequestFileExtractorInstructions();
        //ExtractorInstructionFilesDTO extractorInstructionFilesDTOResponse = dtoRequestFileExtractorInstructions.process(extractorInstructionFilesDTOToSend);


        Assert.assertNotEquals(null, extractorInstructionFileDTOResponseEnvelope);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(extractorInstructionFileDTOResponseEnvelope.getHeader()));
        // ************** NOW RETRIFVE THE FILE WE JUST CREATED AND MAKE SURE IT'S REALLY THERE, IMPLICITLY TESTING LINK

        LinkCollection linkCollection = extractorInstructionFileDTOResponseEnvelope.getPayload().getLinkCollection();
        Assert.assertNotNull(linkCollection);
        Assert.assertTrue(linkCollection.getLinksPerDataItem().size() == 1);


        RestUri restUriFromLink = gobiiUriFactory.RestUriFromUri(linkCollection.getLinksPerDataItem().get(0).getHref());
        GobiiEnvelopeRestResource<ExtractorInstructionFilesDTO,ExtractorInstructionFilesDTO> gobiiEnvelopeRestResourceForGet = new GobiiEnvelopeRestResource<>(restUriFromLink);
        PayloadEnvelope<ExtractorInstructionFilesDTO> resultEnvelope = gobiiEnvelopeRestResourceForGet
                .get(ExtractorInstructionFilesDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

        ExtractorInstructionFilesDTO extractorInstructionFilesDTOretrieveResponse = resultEnvelope.getPayload().getData().get(0);

        for (GobiiExtractorInstruction currentExtractorInstruction : extractorInstructionFilesDTOretrieveResponse.getGobiiExtractorInstructions()) {
            Assert.assertTrue("The sent crop type of the retrieved crop types does not match",
                    currentExtractorInstruction.getGobiiCropType().equals(cropTypeFromContext));
        }

        Assert.assertTrue(
                2 == extractorInstructionFilesDTOretrieveResponse
                        .getGobiiExtractorInstructions()
                        .size()
        );

        Assert.assertTrue(
                extractorInstructionFilesDTOretrieveResponse
                        .getGobiiExtractorInstructions()
                        .get(0)
                        .getDataSetExtracts()
                        .get(0)
                        .getDataSet().getName()
                        .equals(dataSetExtractOneName)
        );

        // ************** VERIFY THAT A JOB RECORD WAS CREATED FOR OUR INSTRUCTION
        String jobName = extractorInstructionFilesDTOretrieveResponse.getInstructionFileName();
        RestUri restUriForJob = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_JOB)
                .addUriParam("jobName", jobName);
        GobiiEnvelopeRestResource<JobDTO,JobDTO> gobiiEnvelopeRestResourceForJob = new GobiiEnvelopeRestResource<>(restUriForJob);
        PayloadEnvelope<JobDTO> resultEnvelopeForJob = gobiiEnvelopeRestResourceForJob
                .get(JobDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForJob.getHeader()));

        Assert.assertTrue("No job record was created for job " + jobName,
                resultEnvelopeForJob.getPayload().getData().size() == 1);

        JobDTO submittedJobDto = resultEnvelopeForJob.getPayload().getData().get(0);
        Assert.assertTrue("The job name of the retrieved DTO for the submitted job does not match the requested job name" + jobName,
                submittedJobDto.getJobName().equals(jobName));


        //VERIFY THAT THE DATSET RECORD HAS THE JOB ID FOR THE JOB
        Integer extractedDataSetId = extractorInstructionFilesDTOretrieveResponse.getGobiiExtractorInstructions().get(0).getDataSetExtracts().get(0).getDataSet().getId();
        RestUri dataSetUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_DATASETS);
        dataSetUri.setParamValue("id", extractedDataSetId.toString());
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForExtractedDataset = new GobiiEnvelopeRestResource<>(dataSetUri);
        PayloadEnvelope<DataSetDTO> resultEnvelopeForExrtractedDataset = gobiiEnvelopeRestResourceForExtractedDataset
                .get(DataSetDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForExrtractedDataset.getHeader()));
        DataSetDTO dataSetDTOResponsForLoadedDataset = resultEnvelopeForExrtractedDataset.getPayload().getData().get(0);

        Assert.assertNotNull("Dataset record for extract does not have a job id",
                dataSetDTOResponsForLoadedDataset.getJobId());
        Assert.assertEquals("The dataset record for extract does not have the correct job ID",
                submittedJobDto.getJobId(),  dataSetDTOResponsForLoadedDataset.getJobId());

        // Test link we got from GET
        linkCollection = resultEnvelope.getPayload().getLinkCollection();
        Assert.assertNotNull(linkCollection);
        Assert.assertTrue(linkCollection.getLinksPerDataItem().size() == 1);
        RestUri newRestUriFromLink = gobiiUriFactory.RestUriFromUri(linkCollection.getLinksPerDataItem().get(0).getHref());
        gobiiEnvelopeRestResourceForGet = new GobiiEnvelopeRestResource<>(newRestUriFromLink);
        resultEnvelope = gobiiEnvelopeRestResourceForGet
                .get(ExtractorInstructionFilesDTO.class);

        ExtractorInstructionFilesDTO extractorInstructionFilesDTOFromSecondRetrieval = resultEnvelope.getPayload().getData().get(0);

        Assert.assertTrue(
                2 == extractorInstructionFilesDTOFromSecondRetrieval
                        .getGobiiExtractorInstructions()
                        .size()
        );

        Assert.assertTrue(
                extractorInstructionFilesDTOFromSecondRetrieval
                        .getGobiiExtractorInstructions()
                        .get(0)
                        .getDataSetExtracts()
                        .get(0)
                        .getDataSet().getName()
                        .equals(dataSetExtractOneName)
        );

        // ************** VERIFY THAT WE HANDLE USER INPUT FILE ALREADY EXISTS
        // we're going to test for the existence of the previous instruction file we created;
        // that would not be the real use case; however, it is a file we created on the server
        // so it's handy way to test this functionality

//        ExtractorInstructionFilesDTO testForuserInputFileExistsError =
//                dtoRequestFileExtractorInstructions.process(extractorInstructionFilesDTOToSend);

        payloadEnvelope = new PayloadEnvelope<>(extractorInstructionFilesDTOToSend, GobiiProcessType.CREATE);
        extractorInstructionFileDTOResponseEnvelope = gobiiEnvelopeRestResourceForPost.post(ExtractorInstructionFilesDTO.class,
                payloadEnvelope);


        Assert.assertTrue(1 ==
                extractorInstructionFileDTOResponseEnvelope
                        .getHeader()
                        .getStatus()
                        .getStatusMessages()
                        .stream()
                        .filter(m -> m.getGobiiValidationStatusType().equals(GobiiValidationStatusType.VALIDATION_NOT_UNIQUE))
                        .collect(Collectors.toList())
                        .size());


        //testGetExtractorInstructionStatus(

        RestUri restUriExtractorInstructionsForGetByFilename = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_FILE_EXTRACTOR_INSTRUCTIONS)
                .setParamValue("id",extractorInstructionFilesDTOFromSecondRetrieval.getInstructionFileName());
        GobiiEnvelopeRestResource<ExtractorInstructionFilesDTO,ExtractorInstructionFilesDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriExtractorInstructionsForGetByFilename);
        PayloadEnvelope<ExtractorInstructionFilesDTO> resultEnvelopeForGetStatusByFileName = gobiiEnvelopeRestResourceForGetById
                .get(ExtractorInstructionFilesDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetStatusByFileName.getHeader()));
        ExtractorInstructionFilesDTO resultExtractorInstructionFilesDTO = resultEnvelopeForGetStatusByFileName.getPayload().getData().get(0);
        Assert.assertNotNull("There's no id returned",resultExtractorInstructionFilesDTO.getId());

    }

}

