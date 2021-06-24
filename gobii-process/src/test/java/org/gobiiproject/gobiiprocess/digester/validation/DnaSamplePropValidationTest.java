package org.gobiiproject.gobiiprocess.digester.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.gobiiproject.gobiiprocess.SafePowerMockRunner;
import org.gobiiproject.gobiiprocess.digester.utils.validation.DigestFileValidator;
import org.gobiiproject.gobiiprocess.digester.utils.validation.ValidationDataUtil;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.Failure;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.ValidationError;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(SafePowerMockRunner.class)
@PrepareForTest(ValidationDataUtil.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*"})
public class DnaSamplePropValidationTest {

    private static String tempFolderLocation;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() throws IOException {
        tempFolderLocation = tempFolder.getRoot().getPath();
        File source = new File("src/test/resources/validation/dnasample_prop");
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

    /**
     * Dnasample propsvalidation.
     */
    @Test
    public void dnasamplePropAllPassTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/allPass",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/allPass"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);

        assertEquals("Expected file name is not dnasample_prop", "dnasample_prop", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not success", ValidationTestSuite.SUCCESS_TEXT, fileErrors[0].status);
    }

    /**
     * dnasample props validation.
     * Missing one required field
     */
    @Test
    public void dnasamplePropMissingRequiredFieldTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/missingRequiredColumns",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/missingRequiredColumns"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);

        assertEquals("Expected file name is not dnasample_prop", "dnasample_prop", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 2, failures.size());
        assertEquals("Unexpected failure reason", "Column not found", failures.get(0).reason);
        assertEquals("Unexpected column name", "project_id", failures.get(0).columnName.get(0));
        assertEquals("Unexpected failure reason", "Column not found", failures.get(1).reason);
        assertEquals("Unexpected column name", "project_id", failures.get(1).columnName.get(0));
    }

    /**
     * DnasampleProp validation.
     * Missing comparison file
     */
    @Test
    public void dnasamplePropMissingComparisonFileTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/missingComparisonFile",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/missingComparisonFile"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);

        assertEquals("Expected file name is not dnasample_prop", "dnasample_prop", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 5, failures.size());
        assertEquals("Unexpected failure reason", "File not found", failures.get(0).reason);
        assertEquals("Unexpected values", "digest.dnasample", failures.get(0).values.get(0));
        assertEquals("Unexpected failure reason", "File not found", failures.get(1).reason);
        assertEquals("Unexpected values", "digest.dnasample", failures.get(1).values.get(0));
        assertEquals("Unexpected failure reason", "File not found", failures.get(2).reason);
        assertEquals("Unexpected values", "digest.dnasample", failures.get(2).values.get(0));
        assertEquals("Unexpected failure reason", "File not found", failures.get(1).reason);
        assertEquals("Unexpected values", "digest.dnasample", failures.get(1).values.get(0));
        assertEquals("Unexpected failure reason", "File not found", failures.get(2).reason);
        assertEquals("Unexpected values", "digest.dnasample", failures.get(2).values.get(0));
    }

    /**
     * DnaSampleProp validation.
     * Mismatch comparison file
     */
    @Test
    public void dnasamplePropMismatchComparisionTest() throws IOException {
        DigestFileValidator digestFileValidator =
            new DigestFileValidator(
                tempFolder.getRoot().getAbsolutePath() + "/mismatchComparisonColumn",
                tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/mismatchComparisonColumn"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);

        assertEquals("Expected file name is not dnasample_prop", "dnasample_prop", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not success", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());
        assertEquals("Unexpected failure reason", "Column value mismatch", failures.get(0).reason);
    }

    /**
     * DnaSampleProp validation.
     * Mismatch comparison file
     */
    @Test
    public void dnasamplePropcolumnCombinationNotUniqueTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/columnCombinationNotUnique",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/columnCombinationNotUnique"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);

        assertEquals("Expected file name is not dnasample_prop", "dnasample_prop", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not success", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());
        assertEquals("Unexpected failure reason", "Not unique", failures.get(0).reason);
    }
}