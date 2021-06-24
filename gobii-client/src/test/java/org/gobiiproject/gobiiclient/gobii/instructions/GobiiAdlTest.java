// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Chaitanya Sarma
// Create Date:   2018-05-18
// ************************************************************************
package org.gobiiproject.gobiiclient.gobii.instructions;

import java.io.File;
import java.util.Scanner;

import org.gobiiproject.gobiiclient.core.gobii.GobiiTestConfiguration;
import org.gobiiproject.gobiiclient.gobii.Helpers.ADLEncapsulator;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestUtils;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestNameIdListTest;
import org.gobiiproject.gobiimodel.config.TestExecConfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
@Ignore
public class GobiiAdlTest {


    private static boolean backendSupoorted;
    private static TestExecConfig testExecConfig;
    private static Logger LOGGER = LoggerFactory.getLogger(DtoCrudRequestNameIdListTest.class);

    @BeforeClass
    public static void setUpClass() throws Exception {
        backendSupoorted = TestUtils.isBackEndSupported();
        GobiiTestConfiguration gobiiTestConfiguration = new GobiiTestConfiguration();
        testExecConfig = gobiiTestConfiguration.getConfigSettings().getTestExecConfig();
    }


    /***
     * Note: there are a couple of issues with this test. First of all, checking
     * the backend processing flag does not work: locally, mine is set to false and the test
     * runs anyway.
     * Moreover, when it does run, it times out. However, the error message does not display,
     * and instead there is an index out of bounds exception.
     */
    @Test
    public void testADLBatchProcessing() throws Exception {


        System.out.print("====== BEGIN GOBII ADL RUN");

        long startTime = System.currentTimeMillis();

        ADLEncapsulator adlEncapsulator = new ADLEncapsulator();
        File tempDir = adlEncapsulator.setUpAdlTest(testExecConfig);

        Assert.assertNotNull(adlEncapsulator.getErrorMsg(), tempDir);

        // check if include_scenarios.txt exists
        File scenarioFile = new File("src/test/resources/gobiiAdl/include_scenarios.txt");

        String scenariosRunTxt = "Scenarios ran: ";
        Integer scenarioCount = 0;

        if (scenarioFile.exists() && !scenarioFile.isDirectory()) {

            Scanner sc = new Scanner(scenarioFile);

            while (sc.hasNextLine()) {

                String scenarioName = sc.nextLine();

                File fileFromRepo = new File("src/test/resources/gobiiAdl/" + scenarioName);

                File newScenarioDir = new File(tempDir.getAbsoluteFile() + "/" + scenarioName);
                newScenarioDir.mkdir();

                adlEncapsulator.copyFilesToLocalDir(fileFromRepo, newScenarioDir);

            }


        } else {

            File fileFromRepo = new File("src/test/resources/gobiiAdl");

            adlEncapsulator.copyFilesToLocalDir(fileFromRepo, tempDir);

        }


        if (tempDir.exists() && tempDir.isDirectory() && tempDir.listFiles().length > 0) {

            scenarioCount = tempDir.listFiles().length;

            for (File currentFile : tempDir.listFiles()) {

                System.out.print("\nLoading Scenario: " + currentFile.getName());
                System.out.print("\n");

                scenariosRunTxt += "\n -- " + currentFile.getName();

                adlEncapsulator.setInputScenario(currentFile.getAbsolutePath());
                boolean isLoadSuccessful = adlEncapsulator.executeSingleScenarioGobiiADL();

                Assert.assertTrue(adlEncapsulator.getErrorMsg(), isLoadSuccessful);

                System.out.print("Finished loading scenario.\n");

            }

        }


        long totalRunTime = System.currentTimeMillis() - startTime;
        long totalRunTimeMins = (totalRunTime / 1000) / 60;
        long totalRunTimeSecs = (totalRunTime / 1000) % 60;

        System.out.print("\nTotal scenarios: " + scenarioCount);
        System.out.print("\nTotal time for all scenarios: " + totalRunTimeMins + " minutes and " + totalRunTimeSecs + " seconds.");
        System.out.print("\n" + scenariosRunTxt);
        System.out.print("\n====== END GOBII ADL RUN");

    }
}
