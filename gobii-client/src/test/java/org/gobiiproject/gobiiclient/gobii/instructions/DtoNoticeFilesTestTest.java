// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-25
// ************************************************************************
package org.gobiiproject.gobiiclient.gobii.instructions;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiclient.core.common.HttpMethodResult;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiimodel.config.ServerConfigItem;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.junit.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DtoNoticeFilesTestTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.authenticate());

    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }


    @Ignore
    public void testGetFileFromServer() throws Exception {


        //*************************************************************
        // ****************** Prove that file is not there
        ClassLoader classLoader = getClass().getClassLoader();
        String confidentialityFileName = "confidentiality.txt";
        RestUri restUriDelete = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .file(GobiiFileProcessDir.NOTICES, confidentialityFileName);

        HttpMethodResult httpMethodResultDeleteNonExistent = GobiiClientContext.getInstance(null, false)
                .getHttp()
                .delete(restUriDelete);

        Assert.assertTrue("Expected "
                        + HttpStatus.SC_NOT_ACCEPTABLE
                        + " got: "
                        + httpMethodResultDeleteNonExistent.getResponseCode()
                        + ": "
                        + httpMethodResultDeleteNonExistent.getReasonPhrase() + ": " + httpMethodResultDeleteNonExistent.getPlainPayload(),
                httpMethodResultDeleteNonExistent.getResponseCode() == HttpStatus.SC_NOT_ACCEPTABLE);


        //*************************************************************
        // NOW UPLOAD THE FILE
        File defaultConfidentialityNotice = new File(classLoader.getResource(confidentialityFileName).getFile());

        RestUri restUriUpload = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .file(GobiiFileProcessDir.NOTICES, confidentialityFileName);

        HttpMethodResult httpMethodResultUpload = GobiiClientContext.getInstance(null, false)
                .getHttp()
                .upload(restUriUpload, defaultConfidentialityNotice);
        Assert.assertTrue("Expected "
                        + HttpStatus.SC_OK
                        + " got: "
                        + httpMethodResultUpload.getResponseCode()
                        + ": "
                        + httpMethodResultUpload.getReasonPhrase() + ": " + httpMethodResultUpload.getPlainPayload(),
                httpMethodResultUpload.getResponseCode() == HttpStatus.SC_OK);


        //*************************************************************
        // ****************** Prove that file is there implicitly (i.e., delete succeeds)
        HttpMethodResult httpMethodResultDeleteExistent = GobiiClientContext.getInstance(null, false)
                .getHttp()
                .delete(restUriDelete);
        Assert.assertTrue("Expected "
                        + HttpStatus.SC_OK
                        + " got: "
                        + httpMethodResultDeleteExistent.getResponseCode()
                        + ": "
                        + httpMethodResultDeleteExistent.getReasonPhrase() + ": " + httpMethodResultDeleteExistent.getPlainPayload(),
                httpMethodResultDeleteExistent.getResponseCode() == HttpStatus.SC_OK);


        //*************************************************************
        // ****************** Prove that file was deleted (i.e., delete fails)
        HttpMethodResult httpMethodResultDeleteExistentThatWasDeleted = GobiiClientContext.getInstance(null, false)
                .getHttp()
                .delete(restUriDelete);
        Assert.assertTrue("Expected "
                        + HttpStatus.SC_NOT_ACCEPTABLE
                        + " got: "
                        + httpMethodResultDeleteExistentThatWasDeleted.getResponseCode()
                        + ": "
                        + httpMethodResultDeleteExistentThatWasDeleted.getReasonPhrase() + ": " + httpMethodResultDeleteExistentThatWasDeleted.getPlainPayload(),
                httpMethodResultDeleteExistentThatWasDeleted.getResponseCode() == HttpStatus.SC_NOT_ACCEPTABLE);


    }

    @Ignore
    @SuppressWarnings("unused")
    public void testGetConfidentialityNotice() throws Exception {
        //*************************************************************
        // ****************** Remove the file if it's already there
        ClassLoader classLoader = getClass().getClassLoader();
        String confidentialityFileName = "confidentiality.txt";
        RestUri restUriDelete = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .file(GobiiFileProcessDir.NOTICES, confidentialityFileName);

        HttpMethodResult httpMethodResultDeleteNonExistent = GobiiClientContext.getInstance(null, false)
                .getHttp()
                .delete(restUriDelete);

        // we don't check the result because if the file wasn't already there, the result will be HttpStatus.SC_NOT_ACCEPTABLE
        // SC_OK otherwise; we don't care as long as it's not there.

        //*****************************************************************
        // CHECK THAT NOTICE IS EMPTY
        String testCrop = GobiiClientContextAuth.getTestExecConfig().getTestCrop();
        GobiiClientContext.resetConfiguration();
        Assert.assertTrue(GobiiClientContextAuth.authenticate());

        ServerConfigItem serverConfigItemNoNotice = GobiiClientContext
                .getInstance(null, false)
                .getServerConfig(testCrop);

        Assert.assertTrue("There should not be a confidentiality notice for crop" + testCrop,
                StringUtils.isEmpty(serverConfigItemNoNotice.getConfidentialityNotice()));


        //*************************************************************
        // NOW UPLOAD THE FILE
        File defaultConfidentialityNoticeToUpload = new File(classLoader.getResource(confidentialityFileName).getFile());

        RestUri restUriUpload = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .file(GobiiFileProcessDir.NOTICES, confidentialityFileName);

        HttpMethodResult httpMethodResultUpload = GobiiClientContext.getInstance(null, false)
                .getHttp()
                .upload(restUriUpload, defaultConfidentialityNoticeToUpload);
        Assert.assertTrue("Expected "
                        + HttpStatus.SC_OK
                        + " got: "
                        + httpMethodResultUpload.getResponseCode()
                        + ": "
                        + httpMethodResultUpload.getReasonPhrase() + ": " + httpMethodResultUpload.getPlainPayload(),
                httpMethodResultUpload.getResponseCode() == HttpStatus.SC_OK);


        //*****************************************************************
        // CHECK THAT WE GET THE NOTICE VIA THE GOBII_WEB SERVICE
        GobiiClientContext.resetConfiguration();
        Assert.assertTrue(GobiiClientContextAuth.authenticate());

        ServerConfigItem serverConfigItemHasNotice = GobiiClientContext
                .getInstance(null, false)
                .getServerConfig(testCrop);

        Assert.assertFalse("There should be a confidentiality notice for crop" + testCrop,
                serverConfigItemHasNotice.getConfidentialityNotice().isEmpty());


        StringBuilder lines = new StringBuilder();
        Files.readAllLines(Paths.get(defaultConfidentialityNoticeToUpload.getPath())).forEach(fileLine -> lines.append(fileLine));
        String uploadedContent = lines.toString();

        Assert.assertEquals("The uplaoded file content differs from the one in the serverConfig",
                uploadedContent, serverConfigItemHasNotice.getConfidentialityNotice());

        //*************************************************************
        // ****************** Remove the file we uplaoded
        HttpMethodResult httpMethodResultDeleteExistent = GobiiClientContext.getInstance(null, false)
                .getHttp()
                .delete(restUriDelete);
        Assert.assertTrue("Expected "
                        + HttpStatus.SC_OK
                        + " got: "
                        + httpMethodResultDeleteExistent.getResponseCode()
                        + ": "
                        + httpMethodResultDeleteExistent.getReasonPhrase() + ": " + httpMethodResultDeleteExistent.getPlainPayload(),
                httpMethodResultDeleteExistent.getResponseCode() == HttpStatus.SC_OK);


    }


}
