package org.gobiiproject.gobiiprocess.digester.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.DnaSampleDTO;
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
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*"})
public class DnarunValidationTest {

    private static String tempFolderLocation;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() throws IOException {
        tempFolderLocation = tempFolder.getRoot().getPath();
        File source = new File("src/test/resources/validation/dnarun");
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
     * DnaRun validation.
     */
    @Test
    public void dnarunAllPassTest() throws IOException {
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
        assertEquals("Expected file name is not dnarun", "dnarun", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not success", ValidationTestSuite.SUCCESS_TEXT, fileErrors[0].status);
    }

    /**
     * DnaRun validation.
     * Missing one required field
     */
    @Test
    public void dnarunMissingRequiredFieldTest() throws IOException {
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
        assertEquals("Expected file name is not dnarun", "dnarun", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not FAILURE", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 1, failures.size());
        assertEquals("Unexpected failure reason", "Column not found", failures.get(0).reason);
        assertEquals("Unexpected column name", "experiment_id", failures.get(0).columnName.get(0));
    }

    /**
     * DnaRun validation.
     * Mismatch comparison file
     */
    @Test
    public void dnarunMismatchComparisionTest() throws IOException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/mismatchComparisonColumn",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        PowerMockito.mockStatic(ValidationDataUtil.class);

        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/mismatchComparisonColumn"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not dnarun", "dnarun", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not success", ValidationTestSuite.FAILURE_TEXT, fileErrors[0].status);

        List<Failure> failures = fileErrors[0].failures;
        assertEquals("Failures are more than the expected", 2, failures.size());
        assertEquals("Unexpected failure reason", "Column value mismatch", failures.get(0).reason);
        assertEquals("Unexpected failure reason", "dnasample_name", failures.get(0).columnName.get(0));
        assertEquals("Unexpected failure reason", "dnasample.name", failures.get(0).columnName.get(1));
        assertEquals("Unexpected failure reason", "Column value mismatch", failures.get(1).reason);
        assertEquals("Unexpected failure reason", "dnasample_name,num", failures.get(1).columnName.get(0));
        assertEquals("Unexpected failure reason", "dnasample.name,num", failures.get(1).columnName.get(1));
    }

    /**
     * DnaRun validation.
     * Missing comparison file
     */
    @Ignore
    @Test
    public void dnarunNameAndNumCombinationSuccessTest() throws IOException, MaximumErrorsValidationException {
        DigestFileValidator digestFileValidator = new DigestFileValidator(
            tempFolder.getRoot().getAbsolutePath() + "/nameAndNumCombinationSuccess",
            tempFolder.getRoot().getAbsolutePath() + "/validationConfig.json");

        Map<String, String> mapsetDTOList = new HashMap<>();
        mapsetDTOList.put("1", "Illumina_Infinium?");

        List<NameIdDTO> nameIdDTOList = new ArrayList<>();
        {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName("dnasamplename_dom_1");
            nameIdDTO.setId(1);
            DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
            dnaSampleDTO.setDnaSampleNum(Integer.parseInt("1"));
            nameIdDTO.getParameters().put("dnaSampleNum", dnaSampleDTO.getDnaSampleNum());
            nameIdDTOList.add(nameIdDTO);
        }
        {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName("dnasamplename_dom_2");
            nameIdDTO.setId(2);
            DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
            dnaSampleDTO.setDnaSampleNum(Integer.parseInt("2"));
            nameIdDTO.getParameters().put("dnaSampleNum", dnaSampleDTO.getDnaSampleNum());
            nameIdDTOList.add(nameIdDTO);
        }
        {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName("dnasamplename_dom_3");
            nameIdDTO.setId(3);
            DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
            dnaSampleDTO.setDnaSampleNum(Integer.parseInt("3"));
            nameIdDTO.getParameters().put("dnaSampleNum", dnaSampleDTO.getDnaSampleNum());
            nameIdDTOList.add(nameIdDTO);
        }
        {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName("dnasamplename_dom_4");
            nameIdDTO.setId(4);
            DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
            dnaSampleDTO.setDnaSampleNum(Integer.parseInt("4"));
            nameIdDTO.getParameters().put("dnaSampleNum", dnaSampleDTO.getDnaSampleNum());
            nameIdDTOList.add(nameIdDTO);
        }
        {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName("dnasamplename_dom_5");
            nameIdDTO.setId(5);
            DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
            dnaSampleDTO.setDnaSampleNum(Integer.parseInt("5"));
            nameIdDTO.getParameters().put("dnaSampleNum", dnaSampleDTO.getDnaSampleNum());
            nameIdDTOList.add(nameIdDTO);
        }
        {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName("dnasamplename_dom_6");
            nameIdDTO.setId(6);
            DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
            dnaSampleDTO.setDnaSampleNum(Integer.parseInt("6"));
            nameIdDTO.getParameters().put("dnaSampleNum", dnaSampleDTO.getDnaSampleNum());
            nameIdDTOList.add(nameIdDTO);
        }
        {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName("dnasamplename_dom_7");
            nameIdDTO.setId(7);
            DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
            dnaSampleDTO.setDnaSampleNum(Integer.parseInt("7"));
            nameIdDTO.getParameters().put("dnaSampleNum", dnaSampleDTO.getDnaSampleNum());
            nameIdDTOList.add(nameIdDTO);
        }
        {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName("dnasamplename_dom_8");
            nameIdDTO.setId(8);
            DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
            dnaSampleDTO.setDnaSampleNum(Integer.parseInt("8"));
            nameIdDTO.getParameters().put("dnaSampleNum", dnaSampleDTO.getDnaSampleNum());
            nameIdDTOList.add(nameIdDTO);
        }
        {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName("dnasamplename_dom_9");
            nameIdDTO.setId(9);
            DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
            dnaSampleDTO.setDnaSampleNum(Integer.parseInt("9"));
            nameIdDTO.getParameters().put("dnaSampleNum", dnaSampleDTO.getDnaSampleNum());
            nameIdDTOList.add(nameIdDTO);
        }
        {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName("dnasamplename_dom_10");
            nameIdDTO.setId(10);
            DnaSampleDTO dnaSampleDTO = new DnaSampleDTO();
            dnaSampleDTO.setDnaSampleNum(Integer.parseInt("10"));
            nameIdDTO.getParameters().put("dnaSampleNum", dnaSampleDTO.getDnaSampleNum());
            nameIdDTOList.add(nameIdDTO);
        }

        List<NameIdDTO> nameIdDTOList1 = new ArrayList<>();
        PowerMockito.mockStatic(ValidationDataUtil.class);
        PowerMockito
                .when(ValidationDataUtil.validatePlatformId(eq("1"), any()))
                .thenReturn(mapsetDTOList);
        PowerMockito
                .when(ValidationDataUtil.validateNames(Matchers.any(), eq("dnasample"), eq("1"), any()))
                .thenReturn(new ArrayList<>());
        PowerMockito
                .when(ValidationDataUtil.validateSampleNums(Matchers.any(), eq("1"), any()))
                .thenReturn(new ArrayList<>());
        digestFileValidator.performValidation(null);
        List<Path> pathList =
                Files.list(Paths.get(tempFolder.getRoot().getAbsolutePath() + "/nameAndNumCombinationSuccess"))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        assertEquals("There should be one validation output json file", 1, pathList.size());

        ValidationError[] fileErrors = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
        assertEquals("Expected file name is not dnarun", "dnarun", fileErrors[0].fileName);
        assertEquals("Expected STATUS is not SUCCESS", ValidationTestSuite.SUCCESS_TEXT, fileErrors[0].status);
    }
}