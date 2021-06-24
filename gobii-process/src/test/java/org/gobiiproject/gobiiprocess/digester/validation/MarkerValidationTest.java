package org.gobiiproject.gobiiprocess.digester.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(SafePowerMockRunner.class)
@PrepareForTest(ValidationDataUtil.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*"})
public class MarkerValidationTest {

    private static String tempFolderLocation;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() throws IOException {
        tempFolderLocation = tempFolder.getRoot().getPath();
        File source = new File("src/test/resources/validation/marker");
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
     * Marker validation.
     */
    @Test
    public void markerAllPassTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(tempFolder.getRoot().getAbsolutePath() + "/allPass", tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        /*
        {
            NameIdDTO nameIdDTOResponse = new NameIdDTO();
            nameIdDTOResponse.setName("5fa68c87-6331-4fa7-a5f2-7a5428567eb5s");
            nameIdDTOResponse.setId(0);
            typeResponse.add(nameIdDTOResponse);
        }*/

        try {
            PowerMockito
                    .when(ValidationDataUtil.validateNames(Matchers.any(), eq(GobiiEntityNameType.REFERENCE.toString()), eq("reference_name"), any()))
                    .thenReturn(new ArrayList<>());
            PowerMockito
                    .when(ValidationDataUtil.validateNames(Matchers.any(), eq(GobiiEntityNameType.CV.toString()), eq("strand_name"), any()))
                    .thenReturn(new ArrayList<>());
        } catch (MaximumErrorsValidationException e) {
            e.printStackTrace();
        }
        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/allPass"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not marker", "marker", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not success", ValidationTestSuite.SUCCESS_TEXT, fileErrors[0].status);
    }

    /**
     * Marker validation.
     * Has all required fields, one error speeciesName and one error typeName
     */
    @Test
    public void markerCvFailTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(tempFolder.getRoot().getAbsolutePath() + "/cvFail", tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        List<String> invalidReferences = new ArrayList<>();
        invalidReferences.add("5fa68c87-6331-4fa7-a5f2-7a5428567eb5s");

        List<String> invalidStrands = new ArrayList<>();
        invalidStrands.add("BOTTOM");

        try {
            PowerMockito
                    .when(ValidationDataUtil.validateNames(Matchers.any(), eq("reference"), eq("reference_name"), any()))
                    .thenReturn(invalidReferences);
            PowerMockito
                    .when(ValidationDataUtil.validateNames(Matchers.any(), eq(GobiiEntityNameType.CV.toString()), eq("strand_name"), any()))
                    .thenReturn(invalidStrands);
        } catch (MaximumErrorsValidationException e) {
            e.printStackTrace();
        }
        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/cvFail"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not marker", "marker", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 2, failures.size());

        for (Failure failure : failures) {
            switch (failure.columnName.get(0)) {
                case "strand_name":
                    assertEquals("Unexpected failure reason", "Undefined CV value", failure.reason);
                    assertEquals("Unexpected failure", "BOTTOM", failure.values.get(0));
                    break;
                case "reference_name":
                    assertEquals("Unexpected failure reason", "Reference value does not exist in DB", failure.reason);
                    assertEquals("Unexpected failure", "5fa68c87-6331-4fa7-a5f2-7a5428567eb5s", failure.values.get(0));
                    break;
                default:
                    assertEquals("Undefined failure reason" + failure.columnName, "1", "0");
                    break;
            }
        }
    }

    /**
     * Marker validation.
     * Missing one required field
     */
    @Test
    public void markerMissingRequiredFieldTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(tempFolder.getRoot().getAbsolutePath() + "/missingRequiredColumns", tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/missingRequiredColumns"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not marker", "marker", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());
        assertEquals("Unexpected failure reason", "Column not found", failures.get(0).reason);
        assertEquals("Unexpected column name", "name", failures.get(0).columnName.get(0));
    }

    /**
     * Marker validation.
     * Missing values in required field
     */
    @Test
    public void markerMissingValuesInRequiredFieldTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(tempFolder.getRoot().getAbsolutePath() + "/missingValuesInRequiredColumns", tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/missingValuesInRequiredColumns"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);

        assertEquals("Expected file name is not marker", "marker", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());
        assertEquals("Unexpected failure reason", "NULL VALUE", failures.get(0).reason);
        assertEquals("Unexpected column name", "platform_id", failures.get(0).columnName.get(0));
    }
}