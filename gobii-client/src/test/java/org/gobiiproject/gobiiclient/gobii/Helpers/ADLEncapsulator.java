package org.gobiiproject.gobiiclient.gobii.Helpers;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.gobiiproject.gobiimodel.config.TestExecConfig;
import org.gobiiproject.gobiimodel.utils.HelperFunctions;

@SuppressWarnings("unused")
public class ADLEncapsulator {
    private static final String INPUT_HOST = "-h";
    private static final String INPUT_USER = "-u";
    private static final String INPUT_PASSWORD = "-p";
    private static final String INPUT_TIMEOUT = "-t";
    private static final String INPUT_SCENARIO = "-s";
    private static final String INPUT_DIRECTORY = "-d";
    private static final String SPACE = " ";
    private static final String JAVA_COMMAND = "java -jar";
    private static final String OUTPUT_FILE_NAME = "output.txt";
    private static final String ERROR_FILE_NAME = "error.txt";
    private static final String INPUT_EXTRACT = "-no_extract";
    private static final String INPUT_FILE_COMPARATOR = "-fc";

    private String inputHost, inputUser, inputPassword, inputScenario, inputDirectory, adlJarPath, errorMsg, inputExtract;
    private String inputFileComparator = null;
    private Integer inputTimeout;

    private String getInputHost() {
        return inputHost;
    }

    public void setInputHost(String inputHost) {
        this.inputHost = inputHost;
    }

    private String getInputUser() {
        return inputUser;
    }

    public void setInputUser(String inputUser) {
        this.inputUser = inputUser;
    }

    private String getInputPassword() {
        return inputPassword;
    }

    public void setInputPassword(String inputPassword) {
        this.inputPassword = inputPassword;
    }

    private Integer getInputTimeout() {
        return inputTimeout;
    }

    public void setInputTimeout(Integer inputTimeout) {
        this.inputTimeout = inputTimeout;
    }

    private String getInputScenario() {
        return inputScenario;
    }

    public void setInputScenario(String inputScenario) {
        this.inputScenario = inputScenario;
    }

    private String getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    private String getAdlJarPath() {
        return adlJarPath;
    }

    public void setAdlJarPath(String adlJarPath) {
        this.adlJarPath = adlJarPath;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    private void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    private String getInputExtract() {
        return inputExtract;
    }

    public void setInputExtract(String inputExtract) {
        this.inputExtract = inputExtract;
    }

    private String getInputFileComparator() {
        return inputFileComparator;
    }

    public void setInputFileComparator(String inputFileComparator) {
        this.inputFileComparator = inputFileComparator;
    }

    private String getBasicCommand() {
        String servercommand = JAVA_COMMAND + SPACE;
        servercommand += getAdlJarPath() + SPACE;
        servercommand += INPUT_HOST + SPACE + getInputHost() + SPACE;
        servercommand += INPUT_USER + SPACE + getInputUser() + SPACE;
        servercommand += INPUT_PASSWORD + SPACE + getInputPassword() + SPACE;
        servercommand += INPUT_TIMEOUT + SPACE + getInputTimeout() + SPACE;

        if (getInputFileComparator() != null) {

            servercommand += INPUT_FILE_COMPARATOR + SPACE + getInputFileComparator() + SPACE;

        }

        return servercommand;
    }

    private String getBatchAdlCommand() {
        String servercommand = getBasicCommand();
        servercommand += INPUT_DIRECTORY + SPACE + getInputDirectory() + SPACE;
        return servercommand;
    }

    private String getScenarioAdlCommand() {
        String servercommand = getBasicCommand();
        servercommand += INPUT_SCENARIO + SPACE + getInputScenario() + SPACE;
        return servercommand;
    }


    public File setUpAdlTest(TestExecConfig testExecConfig) throws Exception {

        if (TestUtils.isBackEndSupported()) {

            String configUtilCommandlineStem = testExecConfig.getConfigUtilCommandlineStem();

            String[] split = configUtilCommandlineStem.split(" ");

            if (split.length >= 3) {

                this.setAdlJarPath(split[2] + "gobiiadl.jar");
                this.setInputHost(testExecConfig.getInitialConfigUrl());
                this.setInputUser(testExecConfig.getLdapUserForUnitTest());
                this.setInputPassword(testExecConfig.getLdapPasswordForUnitTest());
                this.setInputTimeout(testExecConfig.getAsynchOpTimeoutMinutes());

                // copy to temp folder

                String tempDirName = "adlTest-" + UUID.randomUUID();
                String tempDirString = testExecConfig.getTestFileDownloadDirectory() + "/" + tempDirName;

                File tempDir = new File(tempDirString);

                tempDir.mkdir();

                return tempDir;

            } else {

                setErrorMsg("Error in getting the path to ADL");

                return null;

            }

        } else {

            setErrorMsg("Backend support is not provided in this context: system-critical unit tests will not be run");

            return null;
        }

    }


    public boolean executeBatchGobiiADL() throws Exception{

        if (getAdlJarPath() == null || getInputHost() == null || getInputUser() == null || getInputPassword() == null || getInputTimeout() == null || getInputDirectory() == null) {
            setErrorMsg("Please set all the input parameters and try again.");
        }

        String outputFileName = getInputDirectory() + "/" + OUTPUT_FILE_NAME;
        String errorFileName = getInputDirectory() + "/" + ERROR_FILE_NAME;

        boolean returnVal = HelperFunctions.tryExec(getBatchAdlCommand(),
                outputFileName,
                errorFileName);

        if (!returnVal) {

            reportErrorMessage(errorFileName, outputFileName);

        }

        return returnVal;

    } // func()


    public boolean executeSingleScenarioGobiiADL() throws Exception {

        if (getAdlJarPath() == null || getInputHost() == null || getInputUser() == null || getInputPassword() == null || getInputTimeout() == null || getInputScenario() == null) {
            setErrorMsg("Please set all the input parameters and try again.");
        }

        String outputFileName = getInputScenario() + "/" + OUTPUT_FILE_NAME;
        String errorFileName = getInputScenario() + "/" + ERROR_FILE_NAME;

        boolean returnVal = HelperFunctions.tryExec(getScenarioAdlCommand(),
                outputFileName,
                errorFileName);

        if (!returnVal) {

            reportErrorMessage(errorFileName, outputFileName);

        }


        return returnVal;

    }

    public void reportErrorMessage(String errorFileName, String outputFileName) throws Exception {

        String message = "gobiiadl failed but there was no output error file reported";
        File errorFile = new File(errorFileName);
        if( errorFile.exists()) {
            message = FileUtils.readFileToString(errorFile);
        } else {
            File outputFile = new File(getClass().getClassLoader().getResource(outputFileName).getFile());
            if( outputFile.exists()) {
                message = FileUtils.readFileToString(errorFile);
            }
        }

        setErrorMsg(message);


    }

    public void copyFilesToLocalDir(File sourceDir, File destinationDir) throws Exception {

        if (sourceDir.exists() && sourceDir.isDirectory() && sourceDir.list().length > 0) {

            for (File currentFile : sourceDir.listFiles()) {

                if (currentFile.isDirectory()) {

                    if (!new File(destinationDir.getAbsoluteFile() + "/" + currentFile.getName()).exists()) {
                        File newDir = new File(destinationDir.getAbsoluteFile() + "/" + currentFile.getName());
                        newDir.mkdir();

                        copyFilesToLocalDir(currentFile, newDir);
                    }

                } else {

                    FileUtils.copyFile(currentFile, new File(destinationDir.getAbsoluteFile() + "/" + currentFile.getName()));
                }
            }

        }


    }
}
