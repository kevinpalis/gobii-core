package org.gobiiproject.gobiiclient.gobii.infrastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiclient.core.common.HttpMethodResult;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiTestConfiguration;
import org.gobiiproject.gobiimodel.config.TestExecConfig;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.utils.DateUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Phil on 7/7/2017.
 */
@SuppressWarnings("rawtypes")
 public class TestUploadDownload {

    private static TestExecConfig testExecConfig = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.authenticate());
        testExecConfig = new GobiiTestConfiguration().getConfigSettings().getTestExecConfig();
    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());

        for (File currentFile : uploadedFiles.values()) {
            currentFile.delete();
        }
    }

    static Map<String, File> uploadedFiles = new HashMap<>();

    static String makeFqpn(String fileName) {

        String returnVal;

        String destinationPath = testExecConfig
                .getTestFileDownloadDirectory();

        File destinationFolder = new File(destinationPath);
        if (!destinationFolder.exists()) {
            destinationFolder.mkdir();
        }

        returnVal = destinationPath + "/" + fileName;

        return returnVal;

    }

    @Test
    public void uploadFile() throws Exception {

        String jobId = DateUtils.makeDateIdString();
        String fileName = "test-" + jobId + ".txt";
        FileWriter fileWriter = new FileWriter(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (Integer idx = 0; idx < 100; idx++) {
            bufferedWriter.write("Random line number " + idx);
        }
        bufferedWriter.close();
        fileWriter.close();
        File file = new File(fileName);

        RestUri restUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .fileForJob(jobId, GobiiFileProcessDir.EXTRACTOR_INSTRUCTIONS, fileName);


        HttpMethodResult httpMethodResult = GobiiClientContext.getInstance(null, false)
                .getHttp()
                .upload(restUri, file);

        Assert.assertTrue("Expected "
                        + HttpStatus.SC_OK
                        + " got: "
                        + httpMethodResult.getResponseCode()
                        + ": "
                        + httpMethodResult.getReasonPhrase(),
                httpMethodResult.getResponseCode() == HttpStatus.SC_OK);

        uploadedFiles.put(jobId, file);

    }

    @Test
    public void downloadFiles() throws Exception {

        if (uploadedFiles.size() <= 0) {
            uploadFile();
        }

        Iterator it = uploadedFiles.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry currentPair = (Map.Entry) it.next();
            String jobId = currentPair.getKey().toString();
            File uploadedFile = (File) currentPair.getValue();
            String fileName = uploadedFile.getName();


            String downloadFqpn = makeFqpn(fileName);
            RestUri restUri = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .fileForJob(jobId, GobiiFileProcessDir.EXTRACTOR_INSTRUCTIONS, fileName)
                    .withDestinationFqpn(downloadFqpn);


            HttpMethodResult httpMethodResult = GobiiClientContext.getInstance(null, false)
                    .getHttp()
                    .get(restUri);

            Assert.assertTrue("Expected "
                            + HttpStatus.SC_OK
                            + " got: "
                            + httpMethodResult.getResponseCode()
                            + ": "
                            + httpMethodResult.getReasonPhrase(),
                    httpMethodResult.getResponseCode() == HttpStatus.SC_OK);

            Assert.assertNotNull("File name is null",
                    httpMethodResult.getFileName());

            File downloadedFile = new File(httpMethodResult.getFileName());

            Assert.assertTrue("The downloaded file cannot be found with the name " + httpMethodResult.getFileName(),
                    downloadedFile.exists());

            Assert.assertTrue("The uplaoded and downloaded files do not contain the same content",
                    FileUtils.contentEquals(downloadedFile, uploadedFile));

            downloadedFile.delete();
        }
    }

    @Test
    public void testUpDownSpreadsheet() throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();
        String testSourceFileName = "report_from_kdc.xlsx";
        String resourcePathMap = "spreadsheets/" + testSourceFileName;
        File fileToUpload = new File(classLoader.getResource(resourcePathMap).getFile());

        this.readXlsFile(fileToUpload);

        String dummyJobIdDir = DateUtils.makeDateIdString();

        RestUri restUriUpload = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .fileForJob(dummyJobIdDir, GobiiFileProcessDir.RAW_USER_FILES, testSourceFileName);
        HttpMethodResult httpMethodResult = GobiiClientContext.getInstance(null, false)
                .getHttp()
                .upload(restUriUpload, fileToUpload);
        Assert.assertTrue("Expected "
                        + HttpStatus.SC_OK
                        + " got: "
                        + httpMethodResult.getResponseCode()
                        + ": "
                        + httpMethodResult.getReasonPhrase() + ": " + httpMethodResult.getPlainPayload(),
                httpMethodResult.getResponseCode() == HttpStatus.SC_OK);


        String downloadFqpn = makeFqpn(testSourceFileName);
        RestUri restUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .fileForJob(dummyJobIdDir, GobiiFileProcessDir.RAW_USER_FILES, testSourceFileName)
                .withDestinationFqpn(downloadFqpn);


        HttpMethodResult httpMethodResultFromDownload = GobiiClientContext.getInstance(null, false)
                .getHttp()
                .get(restUri);

        File downloadedFile = new File(httpMethodResultFromDownload.getFileName());


        Assert.assertTrue("The uploaded and downloaded xlsf file does not have the same content",
                FileUtils.contentEquals(downloadedFile, fileToUpload));
        this.readXlsFile(downloadedFile);

    }

    @SuppressWarnings("unused")
    private void readXlsFile(File file) throws Exception {
        SpreadsheetMLPackage spread = SpreadsheetMLPackage.load(file);
//        for (WorksheetPart sheet : spread.) {
//            System.out.println(sheet.getPartName().getName());
//            Worksheet ws = sheet.getJaxbElement();
//            SheetData data = ws.getSheetData();
//            for (Row r : data.getRow()) {
//                System.out.println("row " + r.getR());
//                for (Cell c : r.getC()) {
//                    if (c.getT().equals(STCellType.S)) {
//                        System.out.println("  " + c.getR() + " contains " +
//                                sharedStrings.getJaxbElement().getSi().get(Integer.parseInt(c.getV())).getT()
//                        );
//                    } else {
//                        // TODO: handle other cell types
//                        System.out.println("  " + c.getR() + " contains " + c.getV());
//                    }
//                }
//            }
    }
}
