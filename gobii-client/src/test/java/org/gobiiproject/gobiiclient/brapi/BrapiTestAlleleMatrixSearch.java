package org.gobiiproject.gobiiclient.brapi;

import org.apache.commons.lang.ObjectUtils;
import org.apache.http.HttpStatus;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiibrapi.core.common.BrapiStatus;
import org.gobiiproject.gobiibrapi.core.responsemodel.BrapiResponseDataList;
import org.gobiiproject.gobiibrapi.core.responsemodel.BrapiResponseEnvelope;
import org.gobiiproject.gobiiclient.core.brapi.BrapiClientContextAuth;
import org.gobiiproject.gobiiclient.core.brapi.BrapiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.core.common.HttpCore;
import org.gobiiproject.gobiiclient.core.common.HttpMethodResult;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiTestConfiguration;
import org.gobiiproject.gobiimodel.config.TestExecConfig;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Phil on 12/15/2016.
 */
public class BrapiTestAlleleMatrixSearch {

    private static TestExecConfig testExecConfig = null;

    private static List<File> filesToCleanUp = new ArrayList<>();
    private static HttpCore httpCore = null;

    @BeforeClass
    public static void setUpClass() throws Exception {

        //we need gobiiclient authentication in order to use
        // GobiiClientContext for creating the URLs and so forth
        // However, the actual client calls are made with the
        // httpCore instance that we get from BrapiClientContextAuth
        // so that we are testing what happens when the authentication
        // token is set through the Bearer header
        Assert.assertTrue(GobiiClientContextAuth.authenticate());

        // but for the BRAPI calls we use the raw httpCore
        testExecConfig = new GobiiTestConfiguration().getConfigSettings().getTestExecConfig();
        httpCore = BrapiClientContextAuth.authenticate();
        Assert.assertNotNull("Could not create http core component",
                httpCore);


    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
        for (File currentFile : filesToCleanUp) {
            currentFile.delete();
        }
    }


    /***
     * Submit an extractor instruction and return the job ID that was used
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    private String makeAlleleMatrixSearchRequest() throws Exception {

        String returnVal;


        RestUri restUriStudiesSearch = GobiiClientContext.getInstance(null, false)
                .getUriFactory(GobiiControllerType.BRAPI)
                .resourceColl(RestResourceId.BRAPI_ALLELE_MATRIX_SEARCH)
                .addQueryParam("matrixDbId", "1");

        //because of how BrapiEnvelopeRestResource is type-parameterized, we have to provide BrapiResponseDataList
        //or a class that is derived from it as the third parameter; in this case, we don't need it because this call
        //only provides metadata. This is not elegant.
        BrapiEnvelopeRestResource<ObjectUtils.Null, ObjectUtils.Null, BrapiResponseDataList> brapiEnvelopeRestResource =
                new BrapiEnvelopeRestResource<>(restUriStudiesSearch,
                        ObjectUtils.Null.class,
                        ObjectUtils.Null.class,
                        BrapiResponseDataList.class,
                        httpCore);

        BrapiResponseEnvelope searchResult = brapiEnvelopeRestResource.posttQueryRequest();
        BrapiTestResponseStructure.validatateBrapiResponseStructure(searchResult.getBrapiMetaData());

        List<BrapiStatus> brapiStatus = searchResult
                .getBrapiMetaData()
                .getStatus()
                .stream()
                .filter(s -> s.getCode().equals("asynchid"))
                .collect(Collectors.toList());

        Assert.assertTrue("The asynchid was not reported",
                brapiStatus.size() > 0);

        returnVal = brapiStatus.get(0).getMessage();

        return returnVal;

    }


    /***
     * Upload a file and return the File object for the local file
     * @param jobId
     * @param sourceFile
     * @param destinationFileName
     * @param gobiiFileProcessDir
     * @return
     * @throws Exception
     */
    private void uploadFile(String jobId,
                            File sourceFile,
                            String destinationFileName,
                            GobiiFileProcessDir gobiiFileProcessDir) throws Exception {


        Assert.assertTrue("The specified test file does not exist: " + sourceFile.getAbsolutePath(),
                sourceFile.exists());

        // For the call to the service, we are supplying artifical file names
        // But we have to do it so that
        // the extractor files service will "correctly" report
        // that the job is done because there is a file there
        RestUri restUriUpload = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .fileForJob(jobId, gobiiFileProcessDir, destinationFileName);

        // even though we're in a BRAPI test, we have to use the httpcore from GOBii because it's the one that
        // will use the GOBii specific authentication header; otherwise, authentication will fail
        HttpMethodResult httpMethodResult = httpCore
                .upload(restUriUpload, sourceFile);

        Assert.assertTrue("Expected "
                        + HttpStatus.SC_OK
                        + " got: "
                        + httpMethodResult.getResponseCode()
                        + ": "
                        + httpMethodResult.getReasonPhrase() + ": " + httpMethodResult.getPlainPayload(),
                httpMethodResult.getResponseCode() == HttpStatus.SC_OK);

    }

    /***
     * Download a file and return the File object for the downloaded file
     * @param jobId
     * @param gobiiFileProcessDir
     * @param sourceFileName
     * @param destinationFileName
     * @return
     * @throws Exception
     */
    private File downloadFile(String jobId,
                              GobiiFileProcessDir gobiiFileProcessDir,
                              String sourceFileName,
                              String destinationFileName) throws Exception {

        File returnVal = null;

        String tesetClientDestinationPath = testExecConfig
                .getTestFileDownloadDirectory() + "/" + destinationFileName;
        RestUri restUriForDownload = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .fileForJob(jobId, GobiiFileProcessDir.EXTRACTOR_INSTRUCTIONS, sourceFileName)
                .withDestinationFqpn(tesetClientDestinationPath);


        // even though we're in a BRAPI test, we have to use the httpcore from GOBii because it's the one that
        // will use the GOBii specific authentication header; otherwise, authentication will fail
        HttpMethodResult httpMethodResultFromDownload =
                httpCore
                        .get(restUriForDownload);

        Assert.assertTrue("Expected "
                        + HttpStatus.SC_OK
                        + " got: "
                        + httpMethodResultFromDownload.getResponseCode()
                        + ": "
                        + httpMethodResultFromDownload.getReasonPhrase(),
                httpMethodResultFromDownload.getResponseCode() == HttpStatus.SC_OK);

        Assert.assertNotNull("File name is null",
                httpMethodResultFromDownload.getFileName());

        returnVal = new File(httpMethodResultFromDownload.getFileName());
        filesToCleanUp.add(returnVal);
        return returnVal;
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void getAlleleMatrix() throws Exception {

        // STEP ONE: SUBMIT AN EXTRACTION JOB SO THAT THE DESTINATION DIRECTORY GETS CREATED BY THE SERVER
        String jobId = this.makeAlleleMatrixSearchRequest();
        Assert.assertNotNull("Job ID was not created",
                jobId);
        ClassLoader classLoader = getClass().getClassLoader();


        // STEP TWO: UPLOAD FILES TO THE JOB'S EXTRACT DESTINATION DIRECTORY
        // the file we're uploading are arbitrary, but they are named per what the
        // extract status service expects with respect to the files that need to be present
        // in order for an extract to be considered completed
        String expectedExractDsNameMap = "DS1.map";
        String testSourceFileNameMap = "ssr_allele_samples.txt";
        String resourcePathMap = "datasets/" + testSourceFileNameMap;
        File testResultFileMap = new File(classLoader.getResource(resourcePathMap).getFile());
        this.uploadFile(jobId, testResultFileMap, expectedExractDsNameMap, GobiiFileProcessDir.EXTRACTOR_OUTPUT);

        String expectedExractDsNameGenotype = "DS1.genotype";
        String testSourceFileNameGenotype = "ssr_alleledata.txt";
        String resourcePathGenotype = "datasets/" + testSourceFileNameGenotype;
        File testResultFileGenotype = new File(classLoader.getResource(resourcePathGenotype).getFile());
        uploadFile(jobId, testResultFileGenotype, expectedExractDsNameGenotype, GobiiFileProcessDir.EXTRACTOR_OUTPUT);

        // STEP 3-A : DOWNLOAD THE EXTRACTOR INSTRUCTION FILE
        String instructionFileName = jobId + ".json";
        File downloadedInstructionFile = this.downloadFile(jobId,
                GobiiFileProcessDir.EXTRACTOR_INSTRUCTIONS,
                instructionFileName,
                instructionFileName);

        // STEP 3-B : UPLOAD THE EXTRACTOR INSTRUCTION FILE TO THE 'DONE' DIRECTORY TO TRICK THE ALLELE-MATRIX STATUS METHOD
        this.uploadFile(jobId,
                downloadedInstructionFile,
                instructionFileName,
                GobiiFileProcessDir.EXTRACTOR_DONE);


        // STEP FOUR: CALL ALLELE MATRICES SEARCH STATUS
        RestUri restUriStudiesSearch = GobiiClientContext.getInstance(null, false)
                .getUriFactory(GobiiControllerType.BRAPI)
                .resourceColl(RestResourceId.BRAPI_ALLELE_MATRIX_SEARCH_STATUS)
                .addUriParam("jobId", jobId);

        //because of how BrapiEnvelopeRestResource is type-parameterized, we have to provide BrapiResponseDataList
        //or a class that is derived from it as the third parameter; in this case, we don't need it because this call
        //only provides metadata. This is not elegant.
        BrapiEnvelopeRestResource<ObjectUtils.Null, ObjectUtils.Null, BrapiResponseDataList> brapiEnvelopeRestResource =
                new BrapiEnvelopeRestResource<>(restUriStudiesSearch,
                        ObjectUtils.Null.class,
                        ObjectUtils.Null.class,
                        BrapiResponseDataList.class,
                        httpCore);

        BrapiResponseEnvelope searchResult = brapiEnvelopeRestResource.getMetaDataResponse();
        BrapiTestResponseStructure.validatateBrapiResponseStructure(searchResult.getBrapiMetaData());

        List<BrapiStatus> brapiStati = searchResult
                .getBrapiMetaData()
                .getStatus()
                .stream()
                .filter(s -> s.getCode().equals("asynchstatus"))
                .collect(Collectors.toList());

        Assert.assertTrue("The asynchstatus was not reported",
                brapiStati.size() > 0);


        Assert.assertTrue("The status of the job should be PENDING",
                brapiStati.get(0).getMessage().equals("PENDING"));

/*
        Assert.assertTrue("File list should contain two items",
                searchResult.getBrapiMetaData().getDatafiles().size() == 4);

        String fileHttpLinkMap = searchResult
                .getBrapiMetaData()
                .getDatafiles()
                .stream()
                .filter(f -> f.contains("http") && f.contains("map"))
                .collect(Collectors.toList())
                .get(0);

        String fileHttpLinkGenotype = searchResult
                .getBrapiMetaData()
                .getDatafiles()
                .stream()
                .filter(f -> f.contains("http") && f.contains("genotype"))
                .collect(Collectors.toList())
                .get(0);

        Assert.assertTrue("file result " + fileHttpLinkMap + " does not contain expected file: " + expectedExractDsNameMap,
                fileHttpLinkMap.contains(expectedExractDsNameMap));

        Assert.assertTrue("file result " + fileHttpLinkGenotype + " does not contain expected file: " + expectedExractDsNameGenotype,
                fileHttpLinkGenotype.contains(expectedExractDsNameGenotype));


        // STEP 5: Download the files with generic components as would be done by a BRAPI consumer
        String destinationPath = testExecConfig
                .getTestFileDownloadDirectory();

        // map file
        String downloadedFilePathMap = destinationPath + "/" + expectedExractDsNameMap + ".downloaded";
        File downloadedFileMap = new File(downloadedFilePathMap);
        filesToCleanUp.add(downloadedFileMap);
        URL urlMap = new URL(fileHttpLinkMap);
        FileUtils.copyURLToFile(urlMap, downloadedFileMap);

        Assert.assertTrue("The file from the BRAPI service's link was not downloaded: " + fileHttpLinkMap,
                downloadedFileMap.exists());

        FileUtils.contentEquals(testResultFileMap, downloadedFileMap);

        // genotype file
        String downloadedFilePathGenotype = destinationPath + "/" + expectedExractDsNameGenotype + ".downloaded";
        File downloadedFileGenotype = new File(downloadedFilePathGenotype);
        filesToCleanUp.add(downloadedFileGenotype);
        URL urlGenotype = new URL(fileHttpLinkGenotype);
        FileUtils.copyURLToFile(urlGenotype, downloadedFileGenotype);

        Assert.assertTrue("The file from the BRAPI service's link was not downloaded: " + fileHttpLinkGenotype,
                downloadedFileGenotype.exists());

        FileUtils.contentEquals(testResultFileGenotype, downloadedFileGenotype);
*/

        //TODO: Fix the test case uncomment the above code and delete the below cleanup logic as it is already taken care of.
        String destinationPath = testExecConfig
                .getTestFileDownloadDirectory();
        String downloadedFilePathMap = destinationPath + "/" + expectedExractDsNameMap + ".downloaded";
        File downloadedFileMap = new File(downloadedFilePathMap);
        filesToCleanUp.add(downloadedFileMap);
        String downloadedFilePathGenotype = destinationPath + "/" + expectedExractDsNameGenotype + ".downloaded";
        File downloadedFileGenotype = new File(downloadedFilePathGenotype);
        filesToCleanUp.add(downloadedFileGenotype);
    }

}
