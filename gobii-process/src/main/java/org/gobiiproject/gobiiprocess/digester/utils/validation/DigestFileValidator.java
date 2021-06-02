package org.gobiiproject.gobiiprocess.digester.utils.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.gobii.masticator.aspects.TableAspect;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.dto.instructions.validation.ValidationConstants;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import  org.gobiiproject.gobiimodel.types.DatasetOrientationType;
import org.gobiiproject.gobiimodel.dto.instructions.validation.errorMessage.Failure;
import org.gobiiproject.gobiimodel.dto.instructions.validation.errorMessage.FailureTypes;
import org.gobiiproject.gobiimodel.dto.instructions.validation.ValidationResult;
import static org.gobiiproject.gobiiprocess.digester.DigesterFileExtensions.allowedExtensions;
import static org.gobiiproject.gobiiprocess.digester.utils.validation.ValidationWebServicesUtil.loginToServer;

public class DigestFileValidator {

    static class InputParameters {
        String rootDir, validationFile, url, password, userName;
    }

    private enum ValidationType{
        WEBSERVICE,
        DATABASE
    }

    private String rootDir, rulesFile, url, password, username;

    private ValidationType validationType;
    private String dbUrl;

    public DigestFileValidator(String rootDir, String url, String username, String password) {
        this(rootDir, null, url, username, password);
    }

    public DigestFileValidator(String rootDir, String validationFile, String dbUrl){
        this.rootDir = rootDir;
        this.rulesFile = validationFile;
        this.dbUrl = dbUrl;
        this.validationType = ValidationType.DATABASE;
    }

    public DigestFileValidator(String rootDir, String validationFile, String url, String username, String password) {
        this.rootDir = rootDir;
        this.url = url;
        this.rulesFile = validationFile;
        this.username = username;
        this.password = password;
        this.validationType = ValidationType.WEBSERVICE;
    }

    public static void main(String[] args) {
        InputParameters inputParameters = new InputParameters();
        readInputParameters(args, inputParameters);
        DigestFileValidator digestFileValidator = new DigestFileValidator(inputParameters.rootDir, inputParameters.validationFile, inputParameters.url, inputParameters.userName, inputParameters.password);
        digestFileValidator.performValidation((GobiiCropConfig)null, null);
    }

    public void performValidation(GobiiCropConfig cropConfig) {
        this.performValidation(cropConfig, null);
    }

    public void performValidation(GobiiCropConfig cropConfig, DatasetOrientationType orientation) {
        String validationOutput = rootDir + "/" + "ValidationResult-" + new SimpleDateFormat("hhmmss").format(new Date()) + ".json";
        /*
         * Read validation Rules
         * Login into server
         * Perform validation
         * */
        try {
            SequenceWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValues(new FileWriter(new File(validationOutput)));
            List<ValidationUnit> validations = readRules(writer);
            ValidationResult validationResult = new ValidationResult();
            validationResult.fileName = FilenameUtils.getExtension(validations.get(0).getDigestFileName());
            List<Failure> failures = new ArrayList<>();
            if (loginToServer(url, username, password, null, failures)) {
                try {
                    List<ValidationResult> validationResultList = doValidations(validations, cropConfig, orientation);
                    writer.write(validationResultList);
                } catch (Exception e) {
                    validationResult.status = ValidationConstants.FAILURE;
                    Failure failure = new Failure();
                    failure.reason = FailureTypes.VALIDATION_ERROR;
                    failure.values.add(e.getMessage());
                    validationResult.failures.add(failure);
                    List<ValidationResult> validationResultList = new ArrayList<>();
                    validationResultList.add(validationResult);
                    writer.write(validationResultList);
                    Logger.logError("DigestFileValidator",e);
                }
            } else {
                validationResult.status = ValidationConstants.FAILURE;
                validationResult.failures.addAll(failures);
                List<ValidationResult> validationResultList = new ArrayList<>();
                validationResultList.add(validationResult);
                writer.write(validationResultList);
            }
            writer.close();
        } catch (IOException e) {
            Logger.logError("I/O Error ", e);
        }
    }

    public void performValidation(String dbConnectionString, DatasetOrientationType orientation) {
        String validationOutput = rootDir + "/" + "ValidationResult-" + new SimpleDateFormat("hhmmss").format(new Date()) + ".json";
        /*
         * Read validation Rules
         * Login into server
         * Perform validation
         * */
        try {
            SequenceWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValues(new FileWriter(new File(validationOutput)));
            List<ValidationUnit> validations = readRules(writer);
            ValidationResult validationResult = new ValidationResult();
            validationResult.fileName = FilenameUtils.getExtension(validations.get(0).getDigestFileName());
            List<Failure> failures = new ArrayList<>();
            try {
                List<ValidationResult> validationResultList = doValidations(validations, dbConnectionString, orientation);
                writer.write(validationResultList);
            } catch (Exception e) {
                validationResult.status = ValidationConstants.FAILURE;
                Failure failure = new Failure();
                failure.reason = FailureTypes.VALIDATION_ERROR;
                failure.values.add(e.getMessage());
                validationResult.failures.add(failure);
                List<ValidationResult> validationResultList = new ArrayList<>();
                validationResultList.add(validationResult);
                writer.write(validationResultList);
                Logger.logError("DigestFileValidator",e);
            }
            writer.close();
        } catch (IOException e) {
            Logger.logError("I/O Error ", e);
        }
    }


    /**
     * Run Validations
     *
     * @param validations validations
     */
    private List<ValidationResult> doValidations(List<ValidationUnit> validations, GobiiCropConfig cropConfig,
												 DatasetOrientationType orientation) {
        List<ValidationResult> validationResultList = new ArrayList<>();
        for (ValidationUnit validation : validations) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.fileName = FilenameUtils.getExtension(validation.getDigestFileName());
            List<Failure> failureList = validate(validation, cropConfig, orientation);
            if (failureList != null) {
                if (failureList.size() > 0) {
                    validationResult.status = ValidationConstants.FAILURE;
                    validationResult.failures.addAll(failureList);
                    validationResultList.add(validationResult);
                } else {
                    validationResult.status = ValidationConstants.SUCCESS;
                    validationResultList.add(validationResult);
                }
            }
        }
        return validationResultList;
        // READ ERRORS
        // ValidationError[] fileErrors = mapper.readValue(new File(validationOutput), ValidationError[].class);
    }

    /**
     * Run Validations
     *
     * @param validations validations
     */
    private List<ValidationResult> doValidations(List<ValidationUnit> validations, String dbConnectionString,
                                                 DatasetOrientationType orientation) {
        List<ValidationResult> validationResultList = new ArrayList<>();
        for (ValidationUnit validation : validations) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.fileName = FilenameUtils.getExtension(validation.getDigestFileName());
            List<Failure> failureList = validate(validation, dbConnectionString, orientation);
            if (failureList != null) {
                if (failureList.size() > 0) {
                    validationResult.status = ValidationConstants.FAILURE;
                    validationResult.failures.addAll(failureList);
                    validationResultList.add(validationResult);
                } else {
                    validationResult.status = ValidationConstants.SUCCESS;
                    validationResultList.add(validationResult);
                }
            }
        }
        return validationResultList;
        // READ ERRORS
        // ValidationError[] fileErrors = mapper.readValue(new File(validationOutput), ValidationError[].class);
    }



    /**
     * Read the command line arguments
     *
     * @param args            arguments
     * @param inputParameters input parameters
     */
    private static void readInputParameters(String[] args, InputParameters inputParameters) {
        Options o = new Options()
                .addOption("r", true, "Fully qualified path to digest directory")
                .addOption("v", true, "Validation rule file path")
                .addOption("h", true, "Host server URL")
                .addOption("u", true, "User Name")
                .addOption("p", true, "Password");
        if (args.length != 8 && args.length != 10) {
            new HelpFormatter().printHelp("DigestFileValidator", o);
            System.exit(1);
        }
        try {
            CommandLine cli = new DefaultParser().parse(o, args);
            if (cli.hasOption("r")) inputParameters.rootDir = cli.getOptionValue("r");
            if (cli.hasOption("v")) inputParameters.validationFile = cli.getOptionValue("v");
            if (cli.hasOption("h")) inputParameters.url = cli.getOptionValue("h");
            if (cli.hasOption("u")) inputParameters.userName = cli.getOptionValue("u");
            if (cli.hasOption("p")) inputParameters.password = cli.getOptionValue("p");

            if (ValidationUtil.isNullAndEmpty(inputParameters.rootDir) || ValidationUtil.isNullAndEmpty(inputParameters.url) || ValidationUtil.isNullAndEmpty(inputParameters.userName) || ValidationUtil.isNullAndEmpty(inputParameters.password)) {
                new HelpFormatter().printHelp("DigestFileValidator", o);
                System.exit(1);
            }
            if (inputParameters.validationFile == null) {
                inputParameters.validationFile = "validationConfig.json";
            }
            if (inputParameters.url.charAt(inputParameters.url.length() - 1) != '/')
                inputParameters.url = inputParameters.url + "/";
        } catch (org.apache.commons.cli.ParseException exp) {
            new HelpFormatter().printHelp("DigestFileValidator", o);
            System.exit(1);
        }
        Logger.logDebug("Entered Options are ", inputParameters.rootDir + " , " + inputParameters.validationFile + " , " + inputParameters.url + " , " + inputParameters.userName + " , " + inputParameters.password);
    }

    /**
     * Reads rules JSON file stores in an object and returns it.
     *
     * @param writer error writer
     */
    private List<ValidationUnit> readRules(SequenceWriter writer) throws IOException {
        List<ValidationUnit> validations = new ArrayList<>();
        try {
            // Convert JSON string from file to Object
            ValidationRules validationRules;
            if (rulesFile != null) {
                File rules = new File(rulesFile);
                validationRules = new ObjectMapper().readValue(rules, ValidationRules.class);
            } else {
                validationRules = new ObjectMapper()
                        .readValue(DigestFileValidator.class.getResourceAsStream("/validationConfig.json"), ValidationRules.class);
            }
            validations = validationRules.getValidations();
        } catch (IOException e) {
            validationFailed(writer, rulesFile, "Exception in reading rules file.\n" + e.getMessage());
            writer.close();
            throw e;
        }
        if (validations.size() == 0) {
            validationFailed(writer, rulesFile, "No validations defined.");
            writer.close();
            throw new IOException("No validations defined.");
        }
        validateRules(validations, writer);
        return validations;
    }

    /**
     * Validates rules defined in the JSON
     * Checks if fileName is valid or not.
     * Checks if column name and required fields are defined in all conditionUnits or not.
     *
     * @param validations List of validations read from JSON
     * @param writer      JSON Writer
     */
    private void validateRules(List<ValidationUnit> validations, SequenceWriter writer) throws IOException {
        boolean validationFailed = false;
        List<String> encounteredDigestExtensions = new ArrayList<>();
        for (ValidationUnit validation : validations) {
            if (!allowedExtensions.contains(FilenameUtils.getExtension(validation.getDigestFileName())))
                validationFailed = validationFailed(writer, FilenameUtils.getExtension(validation.getDigestFileName()), "Entered fileName is not a valid " + validation.getDigestFileName());
            else if (encounteredDigestExtensions.contains(validation.getDigestFileName()))
                validationFailed = validationFailed(writer, FilenameUtils.getExtension(validation.getDigestFileName()), validation.getDigestFileName() + " is already defined.");
            else encounteredDigestExtensions.add(validation.getDigestFileName());
            List<ConditionUnit> conditions = validation.getConditions();
            for (ConditionUnit condition : conditions)
                if (condition.columnName == null || condition.required == null)
                    validationFailed = validationFailed(writer, FilenameUtils.getExtension(validation.getDigestFileName()), validation.getDigestFileName() + " conditions does not have all required fields.");
        }
        if (validationFailed) {
            writer.close();
        }
    }

    private boolean validationFailed(SequenceWriter writer, String fileName, String value) throws IOException {
        ValidationResult validationResult = new ValidationResult();
        validationResult.fileName = fileName;
        Failure failure = new Failure();
        failure.reason = FailureTypes.CORRUPTED_VALIDATION_FILE;
        failure.values.add(value);
        validationResult.failures.add(failure);
        writer.write(validationResult);
        return true;
    }

    public List<Failure> validate(ValidationUnit validation, GobiiCropConfig cropConfig, DatasetOrientationType orientation) {
        trimSpaces(validation);
        List<Failure> failureList = new ArrayList<>();
        switch (FilenameUtils.getExtension(validation.getDigestFileName())) {
            case "germplasm":
            case "germplasm_prop":
            case "dnasample":
            case "dnasample_prop":
            case "dnarun":
            case "dnarun_prop":
            case "marker":
            case "marker_prop":
            case "linkage_group":
            case "marker_linkage_group":
            case "dataset_dnarun":
            case "dataset_marker":
                if (!new Validator(orientation).validate(validation, rootDir, failureList, cropConfig)) failureList = null;
                break;
            default:
                try {
                    ValidationUtil.createFailure(FailureTypes.INVALID_FILE_EXTENSIONS, new ArrayList<>(), " Given extension " + validation.getDigestFileName() + " is invalid.", failureList);
                } catch (MaximumErrorsValidationException e) {
                    //No action needed
                }
        }
        return failureList;
    }

    public List<Failure> validate(ValidationUnit validation, String dbConnection, DatasetOrientationType orientation) {
        trimSpaces(validation);
        List<Failure> failureList = new ArrayList<>();
        switch (FilenameUtils.getExtension(validation.getDigestFileName())) {
            case "germplasm":
            case "germplasm_prop":
            case "dnasample":
            case "dnasample_prop":
            case "dnarun":
            case "dnarun_prop":
            case "marker":
            case "marker_prop":
            case "linkage_group":
            case "marker_linkage_group":
            case "dataset_dnarun":
            case "dataset_marker":
                if (!new Validator(orientation).validate(validation, rootDir, failureList, dbConnection)) failureList = null;
                break;
            default:
                try {
                    ValidationUtil.createFailure(FailureTypes.INVALID_FILE_EXTENSIONS, new ArrayList<>(), " Given extension " + validation.getDigestFileName() + " is invalid.", failureList);
                } catch (MaximumErrorsValidationException e) {
                    //No action needed
                }
        }
        return failureList;
    }


    private void trimSpaces(ValidationUnit validationUnit) {
        validationUnit.setDigestFileName(validationUnit.getDigestFileName().trim());
        for (ConditionUnit conditionUnit : validationUnit.getConditions()) {
            if (conditionUnit.columnName != null) conditionUnit.columnName = conditionUnit.columnName.trim();
            if (conditionUnit.required != null) conditionUnit.required = conditionUnit.required.trim();
            if (conditionUnit.nullAllowed != null) conditionUnit.nullAllowed = conditionUnit.nullAllowed.trim();
            if (conditionUnit.unique != null) conditionUnit.unique = conditionUnit.unique.trim();
            if (conditionUnit.fileExistenceCheck != null)
                conditionUnit.fileExistenceCheck = conditionUnit.fileExistenceCheck.trim();
            if (conditionUnit.fileExists != null) conditionUnit.fileExists = conditionUnit.fileExists.trim();
            if (conditionUnit.type != null) conditionUnit.type = conditionUnit.type.trim();
            if (conditionUnit.typeName != null) conditionUnit.typeName = conditionUnit.typeName.trim();
            if (conditionUnit.fieldToCompare != null) {
                for (int i = 0; i < conditionUnit.fieldToCompare.size(); i++)
                    conditionUnit.fieldToCompare.set(i, conditionUnit.fieldToCompare.get(i).trim());
            }
            if (conditionUnit.uniqueColumns != null)
                conditionUnit.uniqueColumns = conditionUnit.uniqueColumns.stream().map(String::trim).collect(Collectors.toList());
        }
    }
}

