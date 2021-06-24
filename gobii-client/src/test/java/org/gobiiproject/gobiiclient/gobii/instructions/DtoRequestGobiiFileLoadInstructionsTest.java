// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-25
// ************************************************************************
package org.gobiiproject.gobiiclient.gobii.instructions;

import org.gobiiproject.gobiiapimodel.hateos.LinkCollection;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.restresources.gobii.GobiiUriFactory;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkColl;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestUtils;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestContactTest;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestDataSetTest;
import org.gobiiproject.gobiimodel.cvnames.JobPayloadType;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.dto.children.PropNameId;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.auditable.ExperimentDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.*;
import org.gobiiproject.gobiimodel.types.*;

import org.gobiiproject.gobiimodel.utils.DateUtils;
import org.junit.*;

import java.util.LinkedList;


public class DtoRequestGobiiFileLoadInstructionsTest {

    private static GobiiUriFactory gobiiUriFactory;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.authenticate());
        String currentCropContextRoot = GobiiClientContext.getInstance(null, false).getCurrentCropContextRoot();
        DtoRequestGobiiFileLoadInstructionsTest.gobiiUriFactory = new GobiiUriFactory(
            currentCropContextRoot,
            GobiiClientContext.getInstance(null, false).getCurrentClientCropType());

    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }

    @Test
    @SuppressWarnings("unused")
    public void testSendLoaderInstructionFile() throws Exception {


        LoaderInstructionFilesDTO loaderInstructionFilesDTOToSend = new LoaderInstructionFilesDTO();


        String instructionFileName = "testapp_" + DateUtils.makeDateIdString();

        String digesterInputDirectory = GobiiClientContext
                .getInstance(null, false)
                .getFileLocationOfCurrenCrop(GobiiFileProcessDir.LOADER_INSTRUCTIONS);


        String digesterOutputDirectory =
                GobiiClientContext.getInstance(null, false)
                        .getFileLocationOfCurrenCrop(GobiiFileProcessDir.LOADER_INTERMEDIATE_FILES);

        loaderInstructionFilesDTOToSend.setInstructionFileName(instructionFileName);

        Integer instructionOneDataSetId = (new GlobalPkColl<DtoCrudRequestDataSetTest>()).getAPkVal(DtoCrudRequestDataSetTest.class, GobiiEntityNameType.DATASET);
        Integer instructionOneContactId = (new GlobalPkColl<DtoCrudRequestContactTest>()).getAPkVal(DtoCrudRequestContactTest.class, GobiiEntityNameType.CONTACT);
        String gobiiCropType = GobiiClientContext.getInstance(null, false).getCurrentClientCropType();

        GobiiLoaderProcedure procedure = new GobiiLoaderProcedure();

        procedure.setInstructions(new LinkedList<>());
        procedure.setMetadata(new GobiiLoaderMetadata());
        procedure.getMetadata().setDataset(new PropNameId());
        procedure.getMetadata().getDataset().setId(instructionOneDataSetId);
        procedure.getMetadata().setContactId(instructionOneContactId);
        procedure.getMetadata().setGobiiCropType(gobiiCropType);

        String instructionOneTableName = "foo_table";

        GobiiLoaderInstruction gobiiLoaderInstructionOne = new GobiiLoaderInstruction();
        gobiiLoaderInstructionOne.setTable(instructionOneTableName);
        gobiiLoaderInstructionOne.setTable("matrix"); // need this for job record creation test


        // column one
        String instructionOneColumnOneName = "my_foo_column";
        DataSetType dataSetTypeTableOneColumnOne = DataSetType.IUPAC;
        String findTextTableOneColumnOne = "foo-replace-me";
        String replaceTextTextTableOneColumnOne = "bar-replace-me";
        GobiiFileColumn gobiiFileColumnOne = new GobiiFileColumn()
                .setCCoord(1)
                .setRCoord((1))
                .setGobiiColumnType(GobiiColumnType.VCF_MARKER)
                .setFilterFrom(".*")
                .setFilterTo(".*")
                .setName(instructionOneColumnOneName)
                .setFindText(findTextTableOneColumnOne)
                .setReplaceText(replaceTextTextTableOneColumnOne);

        // column two
        GobiiFileColumn gobiiFileColumnTwo = new GobiiFileColumn()
                .setCCoord(1)
                .setRCoord(1)
                .setGobiiColumnType(GobiiColumnType.CSV_COLUMN)
                .setFilterFrom(".*")
                .setFilterTo(".*")
                .setName("my_bar");

        gobiiLoaderInstructionOne.getGobiiFileColumns().add(gobiiFileColumnOne);
        gobiiLoaderInstructionOne.getGobiiFileColumns().add(gobiiFileColumnTwo);

        // GobiiFile Config
        String testSourceDirName = digesterInputDirectory + "file_one_dir";
        String testDestinationDirName = digesterOutputDirectory + "file_one_dir";
        procedure.getMetadata().getGobiiFile().setDelimiter(",")
                .setSource("c://your-dir")
                .setDestination("c://mydir")
                .setGobiiFileType(GobiiFileType.VCF)
                .setSource(testSourceDirName)
                .setDestination(testDestinationDirName);

        procedure.getInstructions().add(gobiiLoaderInstructionOne);

        loaderInstructionFilesDTOToSend.setGobiiLoaderProcedure(procedure);


        // INSTRUCTION ONE END
        // **********************************************************************

        /*** InstructionTwo START - Test for wrong projectId for given experiment. Valid experimentId will be used here ***/
        //get existing dataset
        Integer dataSetId = (new GlobalPkColl<DtoCrudRequestDataSetTest>().getAPkVal(DtoCrudRequestDataSetTest.class, GobiiEntityNameType.DATASET));

        RestUri datasetsUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_DATASETS);
        datasetsUri.setParamValue("id", dataSetId.toString());

        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceDataSet = new GobiiEnvelopeRestResource<>(datasetsUri);
        PayloadEnvelope<DataSetDTO> resultEnvelopeDataSet = gobiiEnvelopeRestResourceDataSet.get(DataSetDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeDataSet.getHeader()));
        DataSetDTO dataSetDTOResponse = resultEnvelopeDataSet.getPayload().getData().get(0);

        Integer experimentId = dataSetDTOResponse.getExperimentId();


        GobiiLoaderInstruction gobiiLoaderInstructionTwo = new GobiiLoaderInstruction();

        String instructionTwoTableName = "test_table2";

        //get experimentDTO
        RestUri experimentsUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_EXPERIMENTS);
        experimentsUri.setParamValue("id", experimentId.toString());

        GobiiEnvelopeRestResource<ExperimentDTO,ExperimentDTO> gobiiEnvelopeRestResourceExperiment = new GobiiEnvelopeRestResource<>(experimentsUri);
        PayloadEnvelope<ExperimentDTO> resultEnvelopeExperiment = gobiiEnvelopeRestResourceExperiment.get(ExperimentDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeExperiment.getHeader()));
        ExperimentDTO experimentDTOResponse = resultEnvelopeExperiment.getPayload().getData().get(0);

        Integer projectId = experimentDTOResponse.getProjectId();

        // invalid projectId
        Integer invalidProjectId = projectId + 1;

        gobiiLoaderInstructionTwo.setTable(instructionTwoTableName);

        // column one
        String instructionTwoColumnOneName = "test2_column_one";
        DataSetType dataSetTypeTwoColumnOne = DataSetType.IUPAC;
        GobiiFileColumn gobiiFileTwoColumnOne = new GobiiFileColumn()
                .setGobiiColumnType(GobiiColumnType.CONSTANT)
                .setConstantValue("test2")
                .setName(instructionTwoColumnOneName);

        gobiiLoaderInstructionTwo.getGobiiFileColumns().add(gobiiFileTwoColumnOne);

        // gobiifile config
        String testSourceTwoDirName = digesterInputDirectory + "file_two_dir";
        String testDestinationTwoDirName = digesterOutputDirectory + "file_two_dir";

        procedure.getInstructions().add(gobiiLoaderInstructionTwo);

        /***** InstructionTwo END *****/

        //The primary instruction does not have a payload type
        PayloadEnvelope<LoaderInstructionFilesDTO> payloadEnvelope = new PayloadEnvelope<>(loaderInstructionFilesDTOToSend, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<LoaderInstructionFilesDTO,LoaderInstructionFilesDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(DtoRequestGobiiFileLoadInstructionsTest.gobiiUriFactory.resourceColl(RestResourceId.GOBII_FILE_LOAD_INSTRUCTIONS));
        PayloadEnvelope<LoaderInstructionFilesDTO> loaderInstructionFileDTOResponseEnvelope = gobiiEnvelopeRestResource.post(LoaderInstructionFilesDTO.class,
                payloadEnvelope);


        // first one should have failed due to lack of a payload type
        Assert.assertNotEquals(null, loaderInstructionFileDTOResponseEnvelope);

        Assert.assertTrue("There should have been a missing payload type error",
                loaderInstructionFileDTOResponseEnvelope
                        .getHeader()
                        .getStatus()
                        .getStatusMessages()
                        .stream()
                        .filter(m -> m.getMessage().toLowerCase().contains("primary instruction does not have a payload type"))
                        .count() > 0);

        JobPayloadType payloadTypeToTest = JobPayloadType.CV_PAYLOADTYPE_MATRIX;
        loaderInstructionFilesDTOToSend.getProcedure().getMetadata().setJobPayloadType(payloadTypeToTest);
        loaderInstructionFileDTOResponseEnvelope = gobiiEnvelopeRestResource.post(LoaderInstructionFilesDTO.class,
                payloadEnvelope);

        //now it should succeed
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(loaderInstructionFileDTOResponseEnvelope.getHeader()));

        LoaderInstructionFilesDTO loaderInstructionFileDTOResponse = loaderInstructionFileDTOResponseEnvelope.getPayload().getData().get(0);
        Assert.assertNotEquals(null, loaderInstructionFileDTOResponse);

        //Now re-retrieve with the link we got back
        Assert.assertNotNull(loaderInstructionFileDTOResponseEnvelope.getPayload().getLinkCollection());
        Assert.assertNotNull(loaderInstructionFileDTOResponseEnvelope.getPayload().getLinkCollection().getLinksPerDataItem());
//        Assert.assertNotNull(loaderInstructionFileDTOResponseEnvelope.getPayload().getLinkCollection().getLinksPerDataItem().get(0));



        Assert.assertTrue("The number of remapped tables does not match the number of tables sent",
                loaderInstructionFilesDTOToSend.getProcedure().getInstructions().size() ==
                        loaderInstructionFileDTOResponse.getProcedure().getInstructions().size());


        Assert.assertNotNull("The job status field should not be null: " + loaderInstructionFileDTOResponse.getProcedure().getMetadata().getGobiiJobStatus(),
                loaderInstructionFilesDTOToSend.getProcedure().getMetadata().getGobiiJobStatus());

        Assert.assertTrue("The default reported status should not be " + JobProgressStatusType.CV_PROGRESSSTATUS_NOSTATUS.getCvName(),
                !loaderInstructionFilesDTOToSend.getProcedure().getMetadata().getGobiiJobStatus().equals(JobProgressStatusType.CV_PROGRESSSTATUS_NOSTATUS.getCvName()));


        // ************** NOW RETRIFVE THE FILE WE JUST CREATED AND MAKE SURE IT'S REALLY THERE USING THE HATEOS LINKS

        LinkCollection linkCollection = loaderInstructionFileDTOResponseEnvelope.getPayload().getLinkCollection();
        Assert.assertNotNull(linkCollection);
        Assert.assertTrue(linkCollection.getLinksPerDataItem().size() == 1);
        RestUri restUriFromLink = gobiiUriFactory.RestUriFromUri(linkCollection.getLinksPerDataItem().get(0).getHref());
        GobiiEnvelopeRestResource<LoaderInstructionFilesDTO,LoaderInstructionFilesDTO> gobiiEnvelopeRestResourceForGet = new GobiiEnvelopeRestResource<>(restUriFromLink);
        PayloadEnvelope<LoaderInstructionFilesDTO> resultEnvelope = gobiiEnvelopeRestResourceForGet
                .get(LoaderInstructionFilesDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));
        LoaderInstructionFilesDTO loaderInstructionFilesDTOretrieveResponse = resultEnvelope.getPayload().getData().get(0);

        Assert.assertNotNull(loaderInstructionFilesDTOretrieveResponse.getProcedure());

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(loaderInstructionFileDTOResponseEnvelope.getHeader()));

        Assert.assertTrue(
                2 == loaderInstructionFilesDTOretrieveResponse
                        .getProcedure().getInstructions()
                        .size()
        );

        Assert.assertNotNull(
                loaderInstructionFilesDTOretrieveResponse
                        .getProcedure().getMetadata()
        );


        Assert.assertTrue(
                loaderInstructionFilesDTOretrieveResponse
                        .getProcedure()
                        .getMetadata().getDataset().getId()
                        .equals(instructionOneDataSetId)
        );


        Assert.assertTrue(
                loaderInstructionFilesDTOretrieveResponse
                        .getProcedure()
                        .getInstructions()
                        .get(0)
                        .getGobiiFileColumns()
                        .get(0)
                        .getName().equals(instructionOneColumnOneName)
        );

        // ************** VERIFY THAT A JOB RECORD WAS CREATED FOR OUR INSTRUCTION
        String jobName = loaderInstructionFilesDTOretrieveResponse.getInstructionFileName();
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

        Assert.assertTrue("The instruction file's payload type " + payloadTypeToTest.getCvName() + "does not match the retrievedtype " + submittedJobDto.getPayloadType(),
                submittedJobDto.getPayloadType().equals(payloadTypeToTest.getCvName()));

        Assert.assertTrue("The instruction file's payload type " + payloadTypeToTest.getCvName() + "does not match the retrievedtype " + submittedJobDto.getPayloadType(),
                submittedJobDto.getPayloadType().equals(payloadTypeToTest.getCvName()));


        //VERIFY THAT THE DATSET RECORD HAS THE JOB ID FOR THE JOB
        Integer loadedDataSetId = loaderInstructionFileDTOResponse.getProcedure().getMetadata().getDataset().getId();
        RestUri dataSEtUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_DATASETS);
        dataSEtUri.setParamValue("id", loadedDataSetId.toString());
        GobiiEnvelopeRestResource<DataSetDTO,DataSetDTO> gobiiEnvelopeRestResourceForLoadedDataset = new GobiiEnvelopeRestResource<>(dataSEtUri);
        PayloadEnvelope<DataSetDTO> resultEnvelopeForLoadedDataset = gobiiEnvelopeRestResourceForLoadedDataset
                .get(DataSetDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForLoadedDataset.getHeader()));
        DataSetDTO dataSetDTOResponsForLoadedDataset = resultEnvelopeForLoadedDataset.getPayload().getData().get(0);

        Assert.assertNotNull("Dataset record does not have a job id",
                dataSetDTOResponsForLoadedDataset.getJobId());
        Assert.assertEquals("The loade dataset record does not have the correct job ID",
                submittedJobDto.getJobId(),  dataSetDTOResponsForLoadedDataset.getJobId());



        // ************** VERIFY THAT WE CAN MEANINGFULLY TEST FOR NON EXISTENT DIRECTORIES
        String newInstructionFileName = "testapp_" + DateUtils.makeDateIdString();
        loaderInstructionFilesDTOToSend.setInstructionFileName(newInstructionFileName);

        loaderInstructionFilesDTOToSend
                .getProcedure()
                .getMetadata()
                .getGobiiFile()
                .setSource("foo")
                .setDestination("bar")
                .setRequireDirectoriesToExist(true); // <== should result in validation error


        payloadEnvelope = new PayloadEnvelope<>(loaderInstructionFilesDTOToSend, GobiiProcessType.CREATE);
        gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(DtoRequestGobiiFileLoadInstructionsTest.gobiiUriFactory.resourceColl(RestResourceId.GOBII_FILE_LOAD_INSTRUCTIONS));
        loaderInstructionFileDTOResponseEnvelope = gobiiEnvelopeRestResource.post(LoaderInstructionFilesDTO.class,
                payloadEnvelope);

        Assert.assertTrue("Non-existent files are not being reported properly: there are not two ENTITY_DOES_NOT_EXIST errors",
                1 ==
                        loaderInstructionFileDTOResponseEnvelope.getHeader().getStatus()
                                .getStatusMessages()
                                .stream()
                                .filter(r ->
                                        r.getGobiiValidationStatusType()
                                                .equals(GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST))
                                .count()

        );


        // ************** VERIFY THAT THE DIRECTORIES WE SHOULD HAVE CREATED DO EXIST
        String newInstructionFileNameNoError = "testapp_" + DateUtils.makeDateIdString();
        loaderInstructionFilesDTOToSend.setInstructionFileName(newInstructionFileNameNoError);
        loaderInstructionFilesDTOToSend
                .getProcedure()
                .getMetadata()
                .getGobiiFile()
                .setSource(testSourceDirName)
                .setDestination(testDestinationDirName)
                .setRequireDirectoriesToExist(true); // <== should result in validation error

        payloadEnvelope = new PayloadEnvelope<>(loaderInstructionFilesDTOToSend, GobiiProcessType.CREATE);
        gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(DtoRequestGobiiFileLoadInstructionsTest.gobiiUriFactory.resourceColl(RestResourceId.GOBII_FILE_LOAD_INSTRUCTIONS));
        loaderInstructionFileDTOResponseEnvelope = gobiiEnvelopeRestResource.post(LoaderInstructionFilesDTO.class,
                payloadEnvelope);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(loaderInstructionFileDTOResponseEnvelope.getHeader()));

        // ************** VERIFY THAT WE GET AN ERROR WHEN A FILE ALREADY EXISTS
        loaderInstructionFilesDTOToSend.setInstructionFileName(newInstructionFileNameNoError);
        payloadEnvelope = new PayloadEnvelope<>(loaderInstructionFilesDTOToSend, GobiiProcessType.CREATE);
        gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(DtoRequestGobiiFileLoadInstructionsTest.gobiiUriFactory.resourceColl(RestResourceId.GOBII_FILE_LOAD_INSTRUCTIONS));
        loaderInstructionFileDTOResponseEnvelope = gobiiEnvelopeRestResource.post(LoaderInstructionFilesDTO.class,
                payloadEnvelope);

        Assert.assertTrue("At least one error message should contain 'already exists'",
                loaderInstructionFileDTOResponseEnvelope.getHeader()
                        .getStatus()
                        .getStatusMessages()
                        .stream()
                        .filter(m -> m.getMessage().toLowerCase().contains("already exists"))
                        .count()
                        > 0);


        // ************** VERIFY THAT WE ERROR ON USER INPUT FILE THAT SHOULD EXISTS BUT DOESN'T EXIST

        loaderInstructionFilesDTOToSend.setInstructionFileName("testapp_" + DateUtils.makeDateIdString());

        loaderInstructionFilesDTOToSend
                .getProcedure()
                .getMetadata()
                .getGobiiFile()
                .setSource("foo")
                .setDestination("bar")
                .setCreateSource(false); // <== should result in validation error


        payloadEnvelope = new PayloadEnvelope<>(loaderInstructionFilesDTOToSend, GobiiProcessType.CREATE);
        gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(DtoRequestGobiiFileLoadInstructionsTest.gobiiUriFactory.resourceColl(RestResourceId.GOBII_FILE_LOAD_INSTRUCTIONS));
        PayloadEnvelope<LoaderInstructionFilesDTO> testForuserInputFileExistsCausesErrorEnvelopse = gobiiEnvelopeRestResource.post(LoaderInstructionFilesDTO.class,
                payloadEnvelope);

        Assert.assertTrue(
                1 ==
                        testForuserInputFileExistsCausesErrorEnvelopse.getHeader()
                                .getStatus()
                                .getStatusMessages()
                                .stream()
                                .filter(r ->
                                        r.getGobiiValidationStatusType()
                                                .equals(GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST))
                                .count()
        );

        // ************** VERIFY THAT WE HANDLE USER INPUT FILE ALREADY EXISTS
        // we're going to test for the existence of the previous instruction file we created;
        // that would not be the real use case; however, it is a file we created on the server
        // so it's handy way to test this functionality
        String instructionFileDirectory = GobiiClientContext
                .getInstance(null, false)
                .getFileLocationOfCurrenCrop(GobiiFileProcessDir.LOADER_INSTRUCTIONS);
        String bogusUserInputFile = instructionFileDirectory + newInstructionFileNameNoError + ".json";

        loaderInstructionFilesDTOToSend.setInstructionFileName("testapp_" + DateUtils.makeDateIdString());
        loaderInstructionFilesDTOToSend
                .getProcedure()
                .getMetadata()
                .getGobiiFile()
                .setSource(bogusUserInputFile)
                .setDestination(testDestinationDirName)
                .setCreateSource(false); // <== should result in validation error

        payloadEnvelope = new PayloadEnvelope<>(loaderInstructionFilesDTOToSend, GobiiProcessType.CREATE);
        gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(DtoRequestGobiiFileLoadInstructionsTest.gobiiUriFactory.resourceColl(RestResourceId.GOBII_FILE_LOAD_INSTRUCTIONS));
        PayloadEnvelope<LoaderInstructionFilesDTO> testForuserInputFileExistsNoErrorEnvelope = gobiiEnvelopeRestResource.post(LoaderInstructionFilesDTO.class,
                payloadEnvelope);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(testForuserInputFileExistsNoErrorEnvelope.getHeader()));

    }
}
