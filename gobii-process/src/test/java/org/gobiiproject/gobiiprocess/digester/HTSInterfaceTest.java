package org.gobiiproject.gobiiprocess.digester;

import org.apache.commons.io.FileUtils;
import org.gobiiproject.gobiiprocess.vcfinterface.HTSInterface;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HTSInterfaceTest {
    private static String tempFolderLocation;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() throws IOException {
        tempFolderLocation = tempFolder.getRoot().getPath();
        File source = new File("src/test/resources/validation/HTS");
        FileUtils.copyDirectory(source, tempFolder.getRoot());
    }

    /**
     * According to JUnit no exception is thrown when temp folder is not
     * deleted. This method ensures that temp folder is always deleted.
     */
    @AfterClass
    public static void checkAndCleanTempFile() {
        try {
            FileUtils.deleteDirectory(new File(tempFolderLocation));
        } catch (IOException e) {
        }
    }


    @Test
   // @Ignore //TODO - need to set up 'clean' unit test file and figure out why 'set up' is not firing
    public void setupAndRead() throws Exception{
        //setUp();
        String filePath=tempFolderLocation+"\\HTS\\MicroVCFTest.vcf";
      filePath = "src/test/resources/validation/HTS/MicroVCFTest.vcf";
        org.gobiiproject.gobiiprocess.vcfinterface.HTSInterface.setupVariantOnlyInputLine(new File(filePath));
        List<String> line = org.gobiiproject.gobiiprocess.vcfinterface.HTSInterface.getVariantOnlyInputLine("/");
        String[] expectedArr = "A/C, C/A".split(", ");
        Assert.assertEquals("Length of input line is 2",2,line.size());
        Assert.assertEquals("First Read Line is not equal", Arrays.deepToString(expectedArr),Arrays.deepToString(line.toArray()));

        List<String> line1 = org.gobiiproject.gobiiprocess.vcfinterface.HTSInterface.getVariantOnlyInputLine("/");
        String[] expectedArr2 = "G/G, T/T".split(", ");
        Assert.assertEquals("Second Read Line is not equal", Arrays.deepToString(expectedArr2),Arrays.deepToString(line1.toArray()));
    }

    @Test
    // @Ignore //TODO - need to set up 'clean' unit test file and figure out why 'set up' is not firing
    public void setupAndReadIndels() throws Exception{
        //setUp();
        String filePath=tempFolderLocation+"\\HTS\\MicroVCFTest.vcf";
        filePath = "src/test/resources/validation/HTS/MicroVCFTestIndel.vcf";
        org.gobiiproject.gobiiprocess.vcfinterface.HTSInterface.setupVariantOnlyInputLine(new File(filePath));
        List<String> line = org.gobiiproject.gobiiprocess.vcfinterface.HTSInterface.getVariantOnlyInputLine("/");
        String[] expectedArr = "A/ACAAC, ACAAC/A".split(", ");
        Assert.assertEquals("Length of input line is 2",2,line.size());
        Assert.assertEquals("First Read Line is not equal", Arrays.deepToString(expectedArr),Arrays.deepToString(line.toArray()));

        List<String> line1 = org.gobiiproject.gobiiprocess.vcfinterface.HTSInterface.getVariantOnlyInputLine("/");
        String[] expectedArr2 = "G/G, N/N".split(", ");
        Assert.assertEquals("Second Read Line is not equal", Arrays.deepToString(expectedArr2),Arrays.deepToString(line1.toArray()));
    }

}
