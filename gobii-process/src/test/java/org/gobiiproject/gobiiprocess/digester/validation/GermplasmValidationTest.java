package org.gobiiproject.gobiiprocess.digester.validation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(SafePowerMockRunner.class)
@PrepareForTest(ValidationDataUtil.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*", })
public class GermplasmValidationTest {

    private static String tempFolderLocation;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() throws IOException {
        tempFolderLocation = tempFolder.getRoot().getPath();
        File source = new File("src/test/resources/validation/germplasm");
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
     * Germplasm validation.
     */
    @Test
    public void germplasmAllPassTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/allPass",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);



        try {
            PowerMockito
                    .when(ValidationDataUtil.validateNames(Matchers.any(), eq(GobiiEntityNameType.CV.toString()), eq("type_name"), any()))
                    .thenReturn(new ArrayList<>());
            PowerMockito
                    .when(ValidationDataUtil.validateNames(Matchers.any(), eq(GobiiEntityNameType.CV.toString()), eq("species_name"), any()))
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
        assertEquals("Expected file name is not germplasm", "germplasm", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not success", ValidationTestSuite.SUCCESS_TEXT, fileErrors[0].status);
    }

    /**
     * Germplasm validation.
     * Has all required fields, one error speeciesName and one error typeName
     */
    @Test
    public void germplasmCvFailTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/cvFail",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        List<String> invalidTypes = new ArrayList<>();
        invalidTypes.add("Inbred line");

        List<String> invalidSpecies = new ArrayList<>();
        invalidSpecies.add("x Aegilotriticume");

        try {
            PowerMockito
                    .when(ValidationDataUtil.validateNames(Matchers.any(), eq(GobiiEntityNameType.CV.toString()), eq("type_name"), any()))
                    .thenReturn(invalidTypes);
            PowerMockito
                    .when(ValidationDataUtil.validateNames(Matchers.any(), eq(GobiiEntityNameType.CV.toString()), eq("species_name"), any()))
                    .thenReturn(invalidSpecies);
        } catch (MaximumErrorsValidationException e) {
            e.printStackTrace();
        }
        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/cvFail"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not germplasm", "germplasm", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 2, failures.size());

        for (Failure failure : failures) {
            switch (failure.columnName.get(0)) {
                case "species_name":
                    assertEquals("Unexpected failure reason", "Undefined CV value", failure.reason);
                    assertEquals("Unexpected failure", "x Aegilotriticume", failure.values.get(0));
                    break;
                case "type_name":
                    assertEquals("Unexpected failure reason", "Undefined CV value", failure.reason);
                    assertEquals("Unexpected failure", "Inbred line", failure.values.get(0));
                    break;
                default:
                    assertEquals("Undefined failure reason" + failure.columnName, "1", "0");
                    break;
            }
        }
    }

    /**
     * Germplasm validation.
     * Missing one required field
     */
    @Test
    public void germplasmMissingRequiredFieldTest() throws IOException {
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

        assertEquals("Expected file name is not germplasm", "germplasm", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());
        assertEquals("Unexpected failure reason", "Column not found", failures.get(0).reason);
        assertEquals("Unexpected column name", "external_code", failures.get(0).columnName.get(0));
    }

    /**
     * Germplasm validation.
     * Missing values in required field
     */
    @Test
    public void germplasmMissingValuesInRequiredFieldTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/missingValuesInRequiredColumns",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/missingValuesInRequiredColumns"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not germplasm", "germplasm", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());
        assertEquals("Unexpected failure reason", "NULL VALUE", failures.get(0).reason);
        assertEquals("Unexpected column name", "name", failures.get(0).columnName.get(0));
    }

    /**
     * Germplasm validation.
     * Repeated required field
     */
    @Test
    public void germplasmNonUniqueRequiredColumnsTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/nonUniqueRequiredColumns",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/nonUniqueRequiredColumns"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not germplasm", "germplasm", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());
        assertEquals("Duplicate Found", "Duplicate Found", failures.get(0).reason);
        assertEquals("Unexpected column name", "external_code", failures.get(0).columnName.get(0));
        assertEquals("Unexpected number of duplicates", 3, failures.get(0).values.size());
    }
}