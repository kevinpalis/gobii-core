package org.gobiiproject.gobiiprocess.digester.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.gobiiproject.gobiiprocess.SafePowerMockRunner;
import org.gobiiproject.gobiiprocess.digester.utils.validation.DigestFileValidator;
import org.gobiiproject.gobiiprocess.digester.utils.validation.MaximumErrorsValidationException;
import org.gobiiproject.gobiiprocess.digester.utils.validation.ValidationDataUtil;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.Failure;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.ValidationError;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Matchers;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(SafePowerMockRunner.class)
@PrepareForTest(ValidationDataUtil.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*", "javax.xml.*", "org.xml.*"})
public class DatasetMarkerValidationTest {

    private static String tempFolderLocation;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() throws IOException {
        tempFolderLocation = tempFolder.getRoot().getPath();
        File source = new File("src/test/resources/validation/dataset_marker");
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
     * datasetMarkerGroup validation.
     */
    @Test
    public void datasetMarkerAllPassTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/allPass",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);
        Map<String, String> foreignKeyValueFromDBPlatformId = new HashMap<>();
        foreignKeyValueFromDBPlatformId.put("8", "Dart_clone");


        mockForTestCase(foreignKeyValueFromDBPlatformId, new ArrayList<>());
        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/allPass"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not dataset_marker", "dataset_marker", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not success", ValidationTestSuite.SUCCESS_TEXT, fileErrors[0].status);
    }

    /**
     * datasetMarkerGroup validation.
     */
    @Test
    public void missingPlatformIdAllPassTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/missingPlatformId",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);
        Map<String, String> foreignKeyValueFromDBPlatformId = new HashMap<>();
        foreignKeyValueFromDBPlatformId.put("81", "Dart_clone");


        mockForTestCase(foreignKeyValueFromDBPlatformId, new ArrayList<>());
        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/missingPlatformId"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not dataset_marker", "dataset_marker", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not success", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);
        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());
        assertEquals("Unexpected column name", "platform_id", failures.get(0).columnName.get(0));
        assertEquals("Unexpected failure reason", "Undefined value in DB", failures.get(0).reason);
        assertEquals("Unexpected failure", "81", failures.get(0).values.get(0));
    }

    /**
     * datasetMarkerGroup validation.
     * Missing one required field
     */
    @Test
    public void datasetMarkerMissingRequiredFieldTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/missingRequiredColumns",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);
        Map<String, String> foreignKeyValueFromDBPlatformId = new HashMap<>();
        foreignKeyValueFromDBPlatformId.put("8", "Dart_clone");


        mockForTestCase(foreignKeyValueFromDBPlatformId, new ArrayList<>());
        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/missingRequiredColumns"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not dataset_marker", "dataset_marker", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());
        assertEquals("Unexpected failure reason", "Column not found", failures.get(0).reason);
        assertEquals("Unexpected column name", "marker_idx", failures.get(0).columnName.get(0));
    }

    /**
     * datasetMarkerGroup validation.
     */
    @Test
    public void markerNameFailTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/markerNameFailTest",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        Map<String, String> foreignKeyValueFromDBPlatformId = new HashMap<>();
        foreignKeyValueFromDBPlatformId.put("8", "Dart_clone");

        List<String> invalidNames = new ArrayList<>();
        invalidNames.add("dommarkerss4");

        mockForTestCase(foreignKeyValueFromDBPlatformId, invalidNames);
        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/markerNameFailTest"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not dataset_marker", "dataset_marker", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not success", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());

        assertEquals("Unexpected failure reason", "marker does not exist in DB", failures.get(0).reason);
        assertEquals("Unexpected column name", "marker_name", failures.get(0).columnName.get(0));
        assertEquals("Unexpected column value", "dommarkerss4", failures.get(0).values.get(0));
    }

    private void mockForTestCase(Map<String, String> foreignKeyValueFromDBPlatformId, List<String> invalidNames) {
        try {
            PowerMockito
                    .when(ValidationDataUtil.validatePlatformId(eq("8"), any())).thenReturn(foreignKeyValueFromDBPlatformId);
            PowerMockito
                    .when(ValidationDataUtil.validateNames(Matchers.any(), eq("marker"), eq("8"), any()))
                    .thenReturn(invalidNames);
        } catch (MaximumErrorsValidationException e) {
            e.printStackTrace();
        }
    }
}




