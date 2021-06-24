package org.gobiiproject.gobiiprocess.digester.utils.validation;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.entity.DnaSample;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiiprocess.digester.DigesterFileExtensions;
import org.gobiiproject.gobiiprocess.digester.csv.CSVFileReaderInterface;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.Failure;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.FailureTypes;

class ValidationUtil {
    static boolean isNullAndEmpty(String value) {
        return value == null || value.trim().equalsIgnoreCase("");
    }

    /**
     * Validates required columns are present and are not null or empty.
     *
     * @param fileName      fileName
     * @param columns       Columns
     * @param inputFileList input file as  a list
     * @param failureList   failure list
     */
    static void validateColumns(String fileName, List<String> columns, List<String[]> inputFileList, List<Failure> failureList) throws MaximumErrorsValidationException {
        if (columns.size() == 0) return;
        List<String> headers = Arrays.asList(inputFileList.get(0).clone());
        TreeSet<Integer> sortedColumnNumbers = new TreeSet<>();
        if (getColumnIndices(fileName, columns, inputFileList, sortedColumnNumbers, failureList))
            for (String[] line : inputFileList)
                for (Integer colNo : sortedColumnNumbers)
                    if (ValidationUtil.isNullAndEmpty(line[colNo])) {
                        createFailure(FailureTypes.NULL_VALUE, Collections.singletonList(headers.get(colNo)), failureList);
                    }
    }

    static void validateMatrixSizeMarkerColumns(String fileName, List<String> columns, List<String[]> inputFileList, List<Failure> failureList, boolean markerFast) throws MaximumErrorsValidationException {
        if (columns.size() == 0) return;
        //List<String> headers = Arrays.asList(inputFileList.get(0).clone());
        verifyEqualMatrixSizeMarker(failureList,getFileColumns(fileName,columns,failureList),markerFast);

    }

    /**
     * Validates required unique columns are present and are not null or empty.
     *
     * @param fileName    fileName
     * @param columns     Columns
     * @param inputFile   input file as  a list
     * @param failureList failure list
     */
    static void validateUniqueColumns(String fileName, List<String> columns, List<String[]> inputFile, List<Failure> failureList) throws MaximumErrorsValidationException {
        if (columns.size() == 0) return;
        List<String> headers = Arrays.asList(inputFile.get(0).clone());
        TreeSet<Integer> sortedColumnNumbers = new TreeSet<>();
        if (getColumnIndices(fileName, columns, inputFile, sortedColumnNumbers, failureList))
            for (Integer colNo : sortedColumnNumbers) {
                // For each column check if null or duplicate exists.
                TreeSet<String> map = new TreeSet<>();
                for (String[] line : inputFile)
                    if (ValidationUtil.isNullAndEmpty(line[colNo])) {
                        try {
                            createFailure(FailureTypes.NULL_VALUE, Collections.singletonList(headers.get(colNo)), failureList);
                        } catch (MaximumErrorsValidationException e) {
                            break;
                        }
                    } else {
                        if (map.contains(line[colNo])) {
                            try {
                                createFailure(FailureTypes.DUPLICATE_FOUND, Collections.singletonList(headers.get(colNo)), line[colNo], failureList);
                            } catch (MaximumErrorsValidationException e) {
                                break;
                            }
                        } else map.add(line[colNo]);
                    }
            }
    }

    /**
     * Validates that a particular column is same in both the files
     *
     * @param filePath    File Path
     * @param condition   Condition Unit
     * @param failureList failure list
     */
    static void validateColumnBetweenFiles(String filePath, ConditionUnit condition, List<Failure> failureList) throws MaximumErrorsValidationException {
        String parentDirectory = new File(filePath).getParent();
        if (condition.typeName != null) {
            if (DigesterFileExtensions.allowedExtensions.contains(condition.typeName.substring(condition.typeName.indexOf('.') + 1))) {
                if (condition.fieldToCompare != null && condition.fieldColumns != null) {
                    String comparisonFileName = condition.typeName;
                    List<String> fieldColumns = condition.fieldColumns;
                    List<String> fieldToCompare = condition.fieldToCompare;
                    List<String> filesList = new ArrayList<>();

                    String externalFieldFileExtension = condition.typeName.substring(condition.typeName.indexOf('.') + 1);//E.G. "gerplasm"

                    if (getFilesWithExtension(parentDirectory, comparisonFileName, filesList, failureList)) {
                        if (filesList.size() != 1) {
                            processFileError(comparisonFileName, filesList.size(), failureList);
                            return;
                        }

                        List<String> fileColumnElements, comparisonFileColumnElements;
                        if (fieldToCompare.size() > 1)
                            fileColumnElements = getFileColumns(filePath, fieldColumns, failureList);
                        else fileColumnElements = getFileColumn(filePath, fieldColumns.get(0), failureList);

                        if (fileColumnElements.size() == 0 && condition.required.equalsIgnoreCase(ValidationConstants.YES)) {
                            createFailure(FailureTypes.COLUMN_NOT_FOUND, fieldColumns, failureList);
                            return;
                        }
                        Collections.sort(fileColumnElements);

                        String comparisonFilePath = parentDirectory + "/" + comparisonFileName;
                        if (fieldToCompare.size() > 1)
                            comparisonFileColumnElements = getFileColumns(comparisonFilePath, fieldToCompare, failureList);
                        else
                            comparisonFileColumnElements = getFileColumn(comparisonFilePath, fieldToCompare.get(0), failureList);
                        Collections.sort(comparisonFileColumnElements);

                        // If it is only unique list
                        if (condition.uniqueFileCheck != null && condition.uniqueFileCheck.equalsIgnoreCase(ValidationConstants.YES)) {
                            fileColumnElements = fileColumnElements.stream().distinct().collect(Collectors.toList());
                            comparisonFileColumnElements = comparisonFileColumnElements.stream().distinct().collect(Collectors.toList());
                        }

                        if (!fileColumnElements.equals(comparisonFileColumnElements))
                            createFailure(FailureTypes.VALUE_MISMATCH, new ArrayList<>(Arrays.asList(String.join(",", fieldColumns),externalFieldFileExtension+"."+String.join(",", fieldToCompare))), failureList);
                    }
                } else {
                    if (condition.fieldToCompare != null)
                        printMissingFieldError("File", "fieldToCompare", failureList);
                    else if (condition.fieldColumns != null)
                        printMissingFieldError("File", "fieldColumns", failureList);
                }
            } else
                createFailure(FailureTypes.INVALID_FILE_EXTENSIONS, new ArrayList<>(), condition.typeName, failureList);

        } else
            printMissingFieldError("File", "typeName", failureList);
    }


    /**
     * Validates that a particular column is a subset of the column in another file
     *
     * @param filePath    File Path
     * @param condition   Condition Unit
     * @param failureList failure list
     */
    static void validateColumnSubsetBetweenFiles(String filePath, ConditionUnit condition, List<Failure> failureList) throws MaximumErrorsValidationException {
        String parentDirectory = new File(filePath).getParent();
        if (condition.typeName != null) {
            if (DigesterFileExtensions.allowedExtensions.contains(condition.typeName.substring(condition.typeName.indexOf('.') + 1))) {
                if (condition.fieldToCompare != null && condition.fieldColumns != null) {
                    String comparisonFileName = condition.typeName;
                    List<String> fieldColumns = condition.fieldColumns;
                    List<String> fieldToCompare = condition.fieldToCompare;
                    List<String> filesList = new ArrayList<>();

                    String externalFieldFileExtension = condition.typeName.substring(condition.typeName.indexOf('.') + 1);//E.G. "gerplasm"

                    if (getFilesWithExtension(parentDirectory, comparisonFileName, filesList, failureList)) {
                        if (filesList.size() != 1) {
                            processFileError(comparisonFileName, filesList.size(), failureList);
                            return;
                        }
                        
                        Set<String> fileColumnElements, comparisonFileColumnElements;
                        if (fieldToCompare.size() > 1)
                            fileColumnElements = new HashSet<>(getFileColumns(filePath, fieldColumns, failureList));
                        else fileColumnElements = new HashSet<>(getFileColumn(filePath, fieldColumns.get(0), failureList));

                        if (fileColumnElements.size() == 0 && condition.required.equalsIgnoreCase(ValidationConstants.YES)) {
                            createFailure(FailureTypes.COLUMN_NOT_FOUND, fieldColumns, failureList);
                            return;
                        }

                        String comparisonFilePath = parentDirectory + "/" + comparisonFileName;
                        if (fieldToCompare.size() > 1)
                            comparisonFileColumnElements = new HashSet<>(getFileColumns(comparisonFilePath, fieldToCompare, failureList));
                        else
                            comparisonFileColumnElements = new HashSet<>(getFileColumn(comparisonFilePath, fieldToCompare.get(0), failureList));


                        if (!comparisonFileColumnElements.containsAll(fileColumnElements))
                            createFailure(FailureTypes.VALUE_MISMATCH, new ArrayList<>(Arrays.asList(String.join(",", fieldColumns),externalFieldFileExtension+"."+String.join(",", fieldToCompare))), failureList);
                    }
                } else {
                    if (condition.fieldToCompare != null)
                        printMissingFieldError("File", "fieldToCompare", failureList);
                    else if (condition.fieldColumns != null)
                        printMissingFieldError("File", "fieldColumns", failureList);
                }
            } else
                createFailure(FailureTypes.INVALID_FILE_EXTENSIONS, new ArrayList<>(), condition.typeName, failureList);

        } else
            printMissingFieldError("File", "typeName", failureList);
    }

    /**
     * Checks that the file exists
     *
     * @param fileName    fileName
     * @param condition   Condition unit
     * @param failureList failure list
     */
    static void validateUniqueColumnListHelper(String fileName, ConditionUnit condition, List<Failure> failureList) throws MaximumErrorsValidationException {
        List<String> uniqueColumns = condition.uniqueColumns;
        List<List<String>> fileColumns = new ArrayList<>();
        for (String column : uniqueColumns) {
            List<String> fileColumn = getFileColumn(fileName, column, failureList);
            if (fileColumn.size() != 0) fileColumns.add(fileColumn);
            else {
                createFailure(FailureTypes.COLUMN_NOT_FOUND, Collections.singletonList(column), failureList);
                return;
            }
        }
        if (!verifyEqualSizeColumn(failureList, fileColumns)) return;

        List<String> concatList = new ArrayList<>();
        int size = fileColumns.get(0).size();//only calculate loop size once
        for (int i = 0; i < size; i++) {
            StringBuilder value = new StringBuilder();
            for (List<String> column : fileColumns) {
                if (value.toString().equals("")) value = new StringBuilder(column.get(i));
                else value.append("$@$").append(column.get(i));
            }
            if (concatList.contains(value.toString()))
                createFailure(FailureTypes.NOT_UNIQUE, uniqueColumns, failureList);
            else concatList.add(value.toString());
        }
    }

    /**
     * Validation based on the existence of a file.
     * Do something if a file exists and something else if it doesn't exist.
     *
     * @param fileName    Name of the file
     * @param condition   Condition unit
     * @param failureList failure list
     */
    static void validateFileExistenceCheck(String fileName, ConditionUnit condition, List<Failure> failureList, GobiiCropConfig cropConfig) throws MaximumErrorsValidationException {
        if (condition.fileExistenceCheck != null) {
            String existenceFile = condition.fileExistenceCheck;
            List<String> files = new ArrayList<>();
            boolean shouldFileExist = condition.fileExists.equalsIgnoreCase(ValidationConstants.YES);
            getFilesWithExtension(new File(fileName).getParent(), existenceFile, files, failureList);
            if (files.size() > 1) {
                processFileError(existenceFile, files.size(), failureList);
                return;
            }
            if ((shouldFileExist && files.size() == 1) || (!shouldFileExist && files.size() == 0)) {
                if (condition.type != null) {
                    if (condition.type.equalsIgnoreCase(ValidationConstants.FILE))
                        validateColumnBetweenFiles(fileName, condition, failureList);
                    else if (condition.type.equalsIgnoreCase(ValidationConstants.DB))
                        validateDatabaseCalls(fileName, condition, failureList, cropConfig);
                    else
                        createFailure(FailureTypes.UNDEFINED_CONDITION_TYPE, new ArrayList<>(), condition.type, failureList);
                }
            }
        }
    }

    static boolean getFilesWithExtension(String dir, String fileExtension, List<String> filesWithExtension, List<Failure> failureList) throws MaximumErrorsValidationException {
        try {
            DirectoryStream<Path> files = Files.newDirectoryStream(Paths.get(dir), fileExtension);
            for (Path entry : files) filesWithExtension.add(entry.getFileName().toString());
        } catch (Exception e) {
            createFailure(FailureTypes.ERROR_ACCESSING_FILE, new ArrayList<>(), dir, failureList);
            return false;
        }
        return true;
    }

    /**
     * Reads the particular column from the file.
     * If column does not exist it returns an empty set.
     *
     * @param filepath    filePath
     * @param column      column name
     * @param failureList failure list
     * @return column values
     */
    private static List<String> getFileColumn(String filepath, String column, List<Failure> failureList) throws MaximumErrorsValidationException {
        List<String> fileColumnElements = new ArrayList<>();
        List<String[]> collectList = new ArrayList<>();
        if (!readFileIntoMemory(filepath, collectList, failureList)) return fileColumnElements;
        List<String> fileHeaders = Arrays.asList(collectList.remove(0));
        fileHeaders = fileHeaders.stream().map(String::trim).collect(Collectors.toList());
        int fileColumnIndex = fileHeaders.indexOf(column);
        if (fileColumnIndex >= 0)
            for (String[] line : collectList)
                if (line.length > fileColumnIndex) fileColumnElements.add(line[fileColumnIndex]);
                else createFailure(FailureTypes.COLUMN_VALUE_NOT_FOUND, Collections.singletonList(column), failureList);
        return fileColumnElements;
    }

    /**
     * If all the elements are found it adds the columns combination to a list and returns it.
     * Duplicated are allowed here
     * Else returns dummy list
     *
     * @param filepath    file name
     * @param columns     columns list
     * @param failureList failure list
     * @return list
     * @throws MaximumErrorsValidationException maximum error exception
     */
    static List<String> getFileColumns(String filepath, List<String> columns, List<Failure> failureList) throws MaximumErrorsValidationException {
        List<List<String>> fileColumns = new ArrayList<>();
        for (String column : columns) {
            List<String> fileColumn = getFileColumn(filepath, column, failureList);
            if (fileColumn.size() != 0) fileColumns.add(fileColumn);
            else {
                createFailure(FailureTypes.COLUMN_NOT_FOUND, Collections.singletonList(column), failureList);
                return new ArrayList<>();
            }
        }

        if (!verifyEqualSizeColumn(failureList, fileColumns)) return new ArrayList<>();
        List<String> concatList = new ArrayList<>();
        for (int i = 0; i < fileColumns.get(0).size(); i++) {
            StringBuilder value = new StringBuilder();
            for (List<String> column : fileColumns)
                if (value.toString().equals("")) value = new StringBuilder(column.get(i));
                else value.append("$@$").append(column.get(i));
            concatList.add(value.toString());
        }
        return concatList;
    }

    /**
     * Read file into a list
     *
     * @param fileName    file name
     * @param collectList list to save file contents
     * @param failureList failure list
     * @return list
     */
    static boolean readFileIntoMemory(String fileName, List<String[]> collectList, List<Failure> failureList) throws MaximumErrorsValidationException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            collectList.addAll(stream.map(line -> line.split("\t",-1)).collect(Collectors.toList()));
            if (collectList.size() == 0) {
                createFailure(FailureTypes.EMPTY_FILE, new ArrayList<>(), fileName, failureList);
                return false;
            }
        } catch (IOException e) {
            createFailure(FailureTypes.EXCEPTION_IN_READING_FILE, new ArrayList<>(), fileName, failureList);
            return false;
        }
        return true;
    }

    /**
     * Gets the column numbers.
     *
     * @param fileName            File Name
     * @param columns             Column Names
     * @param collect             inMemoryFile
     * @param sortedColumnNumbers sorted column numbers
     * @param failureList         failure list
     * @return status
     */
    private static boolean getColumnIndices(String fileName, List<String> columns, List<String[]> collect, TreeSet<Integer> sortedColumnNumbers, List<Failure> failureList) throws MaximumErrorsValidationException {
        List<String> fileHeaders = Arrays.asList(collect.remove(0));
        fileHeaders = fileHeaders.stream().map(String::trim).collect(Collectors.toList());
        for (String columnName : columns)
            if (fileHeaders.contains(columnName)) sortedColumnNumbers.add(fileHeaders.indexOf(columnName));
            else createFailure(FailureTypes.COLUMN_NOT_FOUND, Collections.singletonList(columnName), failureList);

        if (sortedColumnNumbers.size() == 0) return false;
        for (String[] line : collect)
            if (line.length <= sortedColumnNumbers.last()) {
                createFailure(FailureTypes.CORRUPTED_FILE, new ArrayList<>(), fileName, failureList);
                return false;
            }
        return true;
    }

    /**
     * Verifies all the columns are of equal size
     *
     * @param failureList failure list
     * @param fileColumns file columns
     * @return status
     */
    private static boolean verifyEqualSizeColumn(List<Failure> failureList, List<List<String>> fileColumns) throws MaximumErrorsValidationException {
        int size = fileColumns.get(0).size();
        for (List<String> column : fileColumns)
            if (column.size() != size) {
                createFailure(FailureTypes.INVALID_COLUMN_SIZE, new ArrayList<>(), failureList);
                return false;
            }
        return true;
    }

    /**
     * Verifies all the columns are of equal size to the matrix
     *
     * @param failureList failure list
     * @param column file columns
     * @return status
     */
    private static boolean verifyEqualMatrixSizeColumn(List<Failure> failureList, List<String> column,Integer matrixSize) throws MaximumErrorsValidationException {
        if(matrixSize==null)return true;
        int size = matrixSize;
            if (column.size() != size) {
                createFailure(FailureTypes.INVALID_COLUMN_MATRIX_SIZE + " ("+column.size() + " " + size + ")", new ArrayList<>(), failureList);
                return false;
            }
        return true;
    }

     static boolean verifyEqualMatrixSizeDnarun(List<Failure> failureList, List<String> fileColumns,boolean markerFast) throws MaximumErrorsValidationException {
        return verifyEqualMatrixSizeMarker(failureList,fileColumns,!markerFast);
    }
     static boolean verifyEqualMatrixSizeMarker(List<Failure> failureList, List<String> fileColumns,boolean markerFast) throws MaximumErrorsValidationException{
        Integer size = markerFast?CSVFileReaderInterface.getLastMatrixRowSize():CSVFileReaderInterface.getLastMatrixColSize();
        return verifyEqualMatrixSizeColumn(failureList,fileColumns,size);
    }



    static void validateDatabaseCalls(String fileName, ConditionUnit condition, List<Failure> failureList, GobiiCropConfig cropConfig) {
        try {
            if (condition.typeName != null) {
                if (condition.typeName.equalsIgnoreCase(ValidationConstants.CV) ||
                    condition.typeName.equalsIgnoreCase(ValidationConstants.REFERENCE) ||
                    condition.typeName.equalsIgnoreCase(ValidationConstants.LINKAGE_GROUP) ||
                    condition.typeName.equalsIgnoreCase(ValidationConstants.DNARUN) ||
                    condition.typeName.equalsIgnoreCase(ValidationConstants.MARKER) ||
                    condition.typeName.equalsIgnoreCase(ValidationConstants.EXTERNAL_CODE) ||
                    condition.typeName.equalsIgnoreCase(ValidationConstants.DNASAMPLE) ||
                    condition.typeName.equalsIgnoreCase(ValidationConstants.DNASAMPLE_NAME) ||
                    condition.typeName.equalsIgnoreCase(ValidationConstants.DNASAMPLE_NAME_NUM)
                ) {
                    if (condition.fieldToCompare != null) {
                        if (checkForHeaderExistence(fileName, condition.fieldToCompare, condition.required, failureList))
                            if (condition.typeName.equalsIgnoreCase(ValidationConstants.CV) ||
                                condition.typeName.equalsIgnoreCase(ValidationConstants.REFERENCE) ||
                                condition.typeName.equalsIgnoreCase(ValidationConstants.EXTERNAL_CODE)) {
                                validateDB(fileName, condition, failureList);
                            } else {
                                if (condition.foreignKey != null) {
                                    if (condition.fieldToCompare.size() == 1)
                                        validateDbWithForeignKey(fileName, condition, failureList);
                                    else validateDNASampleNameAndNum(fileName, condition, failureList,cropConfig);
                                } else printMissingFieldError("DB", "foreignKey", failureList);
                            }
                    } else printMissingFieldError("DB", "fieldToCompare", failureList);
                } else printMissingFieldError("DB", "unsupported typeName", failureList);
            } else printMissingFieldError("DB", "typeName", failureList);
        } catch (MaximumErrorsValidationException e) {
            ////Don't do any thing. This implies that particular error list is full.;
        }
    }

    /**
     * Validate terms in CV table
     *
     * @param fileName    fileName
     * @param condition   condition
     * @param failureList failure list
     */
    private static void validateDB(String fileName,
                                   ConditionUnit condition,
                                   List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        Set<String> fieldNameList = new HashSet<>();
        String fieldToCompare = condition.fieldToCompare.get(0);
        String typeName = condition.typeName;
        if (readColumnIntoSet(fileName, fieldToCompare, fieldNameList, failureList)) {
            List<String> names = new ArrayList<>(fieldNameList);
            if (typeName.equalsIgnoreCase(ValidationConstants.EXTERNAL_CODE)) {
                typeName = GobiiEntityNameType.GERMPLASM.name();
            }
            List<String> invalidNames = ValidationDataUtil.validateNames(
                names,
                typeName,
                fieldToCompare,
                failureList);

            if (typeName.equalsIgnoreCase(ValidationConstants.CV)) {
                createFailure(FailureTypes.UNDEFINED_CV_VALUE,
                    Collections.singletonList(fieldToCompare),
                    invalidNames,
                    failureList);
            }
            else if (typeName.equalsIgnoreCase(ValidationConstants.REFERENCE)) {
                createFailure(FailureTypes.UNDEFINED_REFERENCE_VALUE,
                    Collections.singletonList(fieldToCompare),
                    invalidNames,
                    failureList);
            }
            else{
                //In theory, 'germplasm' works as a field to compare
                createFailure(FailureTypes.UNDEFINED_VALUE,
                    Collections.singletonList(fieldToCompare),
                    invalidNames,
                    failureList);
            }
        }
    }

    private static void validateDNASampleNameAndNum(String fileName,
                                                    ConditionUnit condition,
                                                    List<Failure> failureList,
                                                    GobiiCropConfig cropConfig
    ) throws MaximumErrorsValidationException {
        List<String> fieldToCompare = condition.fieldToCompare;
        Set<String> foreignKeyList = new HashSet<>();
        if (readForeignKey(fileName, condition.foreignKey, foreignKeyList, failureList)) {
            Map<String, Set<List<String>>> mapForeignkeyAndName = new HashMap<>();
            if (createProjectIdSampleNameAndNumGroup(fileName, condition, mapForeignkeyAndName, failureList)) {
                Map<String, String> foreignKeyValueFromDB;
                if (foreignKeyList.size() != 1) {
                    multipleProjectIdError(condition, failureList);
                    return;
                }
                if (condition.typeName.equalsIgnoreCase(ValidationConstants.DNASAMPLE_NAME_NUM)) {
                    String projectId = foreignKeyList.iterator().next();//There's only 1
                    foreignKeyValueFromDB = ValidationDataUtil.validateProjectId(
                        projectId, failureList);
                    if (foreignKeyValueFromDB.size() == 0) {
                        undefinedForeignKey(condition, projectId, failureList);
                        return;
                    }
                } else {
                    createFailure(FailureTypes.UNDEFINED_FOREIGN_KEY,
                        Collections.singletonList(condition.foreignKey),
                        failureList);
                    return;
                }
                for (Map.Entry<String, Set<List<String>>> ent : mapForeignkeyAndName.entrySet()) {
                    if (foreignKeyValueFromDB.keySet().contains(ent.getKey())) {
                        List<DnaSample> queryParams  = new ArrayList<>();
                        for (List<String> name : ent.getValue()) {
                            DnaSample queryParam = new DnaSample();
                            queryParam.setDnaSampleName(name.get(0));
                            queryParam.setDnaSampleName(""+name.get(1));//Num is stringly typed
                            queryParams.add(queryParam);
                        }
                        List<String> invalidNames =
                            ValidationDataUtil.validateSampleNums(
                                queryParams,
                                ent.getKey(),
                                failureList);
                        createFailure(
                            FailureTypes.UNDEFINED_DNASAMPLE_NAME_NUM_VALUE,
                            fieldToCompare,
                            invalidNames,
                            failureList);
                    } else undefinedForeignKey(condition, ent.getKey(), failureList);
                }
            }
        }
    }

    private static void validateDbWithForeignKey(String fileName,
                                                 ConditionUnit condition,
                                                 List<Failure> failureList
    ) throws MaximumErrorsValidationException {

        String fieldToCompare = condition.fieldToCompare.get(0);
        String typeName = condition.typeName;
        Set<String> foreignKeyList = new HashSet<>();
        if (readForeignKey(fileName, condition.foreignKey, foreignKeyList, failureList)) {
            Map<String, Set<String>> mapForeignkeyAndName = new HashMap<>();
            if (createForeignKeyGroup(fileName, condition, mapForeignkeyAndName, failureList)) {
                Map<String, String> foreignKeyValueFromDB = new HashMap<>();
                if ((foreignKeyList.size() != 1) &&
                    condition.foreignKey.equalsIgnoreCase("platform_id")) {
                    multiplePlatformIdError(condition, failureList);
                    return;
                }
                if (condition.typeName.equalsIgnoreCase(ValidationConstants.MARKER) ||
                    condition.typeName.equalsIgnoreCase(ValidationConstants.DNASAMPLE)) {

                    switch (condition.foreignKey.toLowerCase()){
                        case "platform_id":
                            for (String platformId : foreignKeyList) {
                                foreignKeyValueFromDB = ValidationDataUtil.validatePlatformId(
                                    platformId, failureList);
                                if (foreignKeyValueFromDB.size() == 0) {
                                    undefinedForeignKey(condition, platformId, failureList);
                                    return;
                                }
                            }
                            break;
                        case "map_id":
                            for (String mapId : foreignKeyList) {
                                foreignKeyValueFromDB = ValidationDataUtil.validateMapId(
                                    mapId, failureList);
                                if (foreignKeyValueFromDB.size() == 0) {
                                    undefinedForeignKey(condition, mapId, failureList);
                                    return;
                                }
                            }
                            break;
                        case "experiment_id":
                            for (String experimentId : foreignKeyList) {
                                foreignKeyValueFromDB = ValidationDataUtil.validateExperimentId(
                                    experimentId, failureList);
                                if (foreignKeyValueFromDB.size() == 0) {
                                    undefinedForeignKey(condition, experimentId, failureList);
                                    return;
                                }
                            }
                            break;
                        case "project_id":
                            for (String projectId : foreignKeyList) {
                                foreignKeyValueFromDB = ValidationDataUtil.validateProjectId(
                                    projectId,
                                    failureList);
                                if (foreignKeyValueFromDB.size() == 0) {
                                    undefinedForeignKey(condition, projectId, failureList);
                                    return;
                                }
                            }
                            break;
                        default:
                            undefinedForeignKey(condition,condition.foreignKey,failureList);
                    }
                } else {
                    foreignKeyValueFromDB = ValidationDataUtil.getAllowedForeignKeyList(
                        condition.typeName,
                        failureList);
                }

                for (Map.Entry<String, Set<String>> ent : mapForeignkeyAndName.entrySet()) {

                    String foreignKey=ent.getKey();

                    if (foreignKeyValueFromDB.keySet().contains(foreignKey)) {

                        // Remove duplicate names
                        List<String> names = new ArrayList<>();
                        Set<String> distinctNames = new LinkedHashSet<>();
                        for (String name : ent.getValue()) {
                            distinctNames.add(name);
                        }
                        names.addAll(distinctNames);

                        String failureReason = null;
                        switch(condition.typeName.toLowerCase()) {
                            case ValidationConstants.LINKAGE_GROUP:
                                failureReason = FailureTypes.UNDEFINED_LINKAGE_GROUP_NAME__VALUE;
                                break;
                            case ValidationConstants.DNARUN:
                                failureReason = FailureTypes.UNDEFINED_DNARUN_NAME__VALUE;
                                break;
                            case ValidationConstants.DNASAMPLE:
                                failureReason = FailureTypes.UNDEFINED_DNASAMPLE_NAME_VALUE;
                                break;
                            case ValidationConstants.MARKER:
                                failureReason = FailureTypes.UNDEFINED_MARKER_NAME__VALUE;
                                break;
                            default:
                                Logger.logError(
                                    "ValidationUtils",
                                    "No valid ValidationConstant defined for validation "
                                        + condition.typeName);
                        }

                        List<String> invalidNames =
                            ValidationDataUtil.validateNames(
                                names,
                                typeName,
                                foreignKey,
                                failureList);

                        createFailure(
                            failureReason,
                            Collections.singletonList(fieldToCompare),
                            invalidNames,
                            failureList);
                    } else undefinedForeignKey(condition, ent.getKey(), failureList);
                }//end for entry in entryset
            }
            else { //createForeignKeyGroup
             Logger.logWarning("Vaidation","Unable to create foreignKeyGroup");
            }
        }
        else{//readForeignKey
            Logger.logWarning("Vaidation","Unable to read foreign key");
        }
    }

    private static void undefinedForeignKey(ConditionUnit condition,
                                            String value,
                                            List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        createFailure(FailureTypes.UNDEFINED_VALUE, Collections.singletonList(condition.foreignKey), value, failureList);
    }

    private static void multiplePlatformIdError(ConditionUnit condition,
                                                List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        Failure failure = new Failure();
        failure.reason = FailureTypes.MULTIPLE_PLATFORM_ID;
        failure.columnName.add(condition.fieldToCompare.get(0));
        failure.columnName.add(condition.foreignKey);
        ValidationUtil.addMessageToList(failure, failureList);
    }

    private static void multipleProjectIdError(ConditionUnit condition,
                                               List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        Failure failure = new Failure();
        failure.reason = FailureTypes.MULTIPLE_PROJECT_ID;
        failure.columnName.add(condition.fieldToCompare.get(0));
        failure.columnName.add(condition.foreignKey);
        ValidationUtil.addMessageToList(failure, failureList);
    }

    /**
     * Go through the file and creates a foreignKey Id.
     * Result will be a map with one key and several values to it
     *
     * @param fileName             fileName
     * @param condition            Condition
     * @param mapForeignkeyAndName foreignKeyMap
     * @param failureList          failure list
     * @return status true if succeeded
     */
    private static boolean createForeignKeyGroup(String fileName,
                                                 ConditionUnit condition,
                                                 Map<String, Set<String>> mapForeignkeyAndName,
                                                 List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        List<String> foreignKey = getFileColumn(fileName, condition.foreignKey, failureList);
        List<String> fileColumn = getFileColumn(fileName, condition.fieldToCompare.get(0), failureList);
        if (foreignKey.size() != fileColumn.size()) {
            createFailure(FailureTypes.INVALID_COLUMN_SIZE, new ArrayList<>(Arrays.asList(condition.fieldToCompare.get(0), condition.foreignKey)), failureList);
            return false;
        }
        for (int i = 0; i < foreignKey.size(); i++) {
            Set<String> strings = mapForeignkeyAndName.get(foreignKey.get(i));
            if (strings != null) strings.add(fileColumn.get(i));
            else {
                strings = new HashSet<>();
                strings.add(fileColumn.get(i));
                mapForeignkeyAndName.put(foreignKey.get(i), strings);
            }
        }
        return true;
    }

    /**
     * Go through the file and creates a foreignKey Id.
     * Result will be a map with one key and several values to it
     *
     * @param fileName             fileName
     * @param condition            Condition
     * @param mapForeignkeyAndName foreignKeyMap
     * @param failureList          failure list
     * @return status
     */
    private static boolean createProjectIdSampleNameAndNumGroup(String fileName, ConditionUnit condition, Map<String, Set<List<String>>> mapForeignkeyAndName, List<Failure> failureList) throws MaximumErrorsValidationException {
        List<String> foreignKey = getFileColumn(fileName, condition.foreignKey, failureList);
        List<String> fileColumn1 = getFileColumn(fileName, condition.fieldToCompare.get(0), failureList);
        List<String> fileColumn2 = getFileColumn(fileName, condition.fieldToCompare.get(1), failureList);
        if (foreignKey.size() != fileColumn1.size() || foreignKey.size() != fileColumn2.size()) {
            createFailure(FailureTypes.INVALID_COLUMN_SIZE, new ArrayList<>(Arrays.asList(condition.fieldToCompare.get(0), condition.foreignKey)), failureList);
            return false;
        }
        for (int i = 0; i < foreignKey.size(); i++) {
            Set<List<String>> strings = mapForeignkeyAndName.get(foreignKey.get(i));
            if (strings != null) strings.add(new ArrayList<>(Arrays.asList(fileColumn1.get(i), fileColumn2.get(i))));
            else {
                strings = new HashSet<>();
                strings.add(new ArrayList<>(Arrays.asList(fileColumn1.get(i), fileColumn2.get(i))));
                mapForeignkeyAndName.put(foreignKey.get(i), strings);
            }
        }
        return true;
    }

    /**
     * Process the DB response. If there is no id add it to the failure list
     */
    private static void processResponseList(List<NameIdDTO> nameIdDTOList, List<String> fieldToCompare, String reason, List<Failure> failureList) throws MaximumErrorsValidationException {
        for (NameIdDTO nameIdDTO : nameIdDTOList)
            if (nameIdDTO.getId() == 0) createFailure(reason, fieldToCompare, nameIdDTO.getName(), failureList);
    }
    private static void processResponseList(List<NameIdDTO> nameIdDTOList, String fieldToCompare, String reason, List<Failure> failureList) throws MaximumErrorsValidationException {
        processResponseList(nameIdDTOList,Collections.singletonList(fieldToCompare),reason,failureList);
    }

        /**
		 * Reads specified key.
		 * Can simplify while refactoring
		 */
    private static boolean readForeignKey(String fileName, String foreignKey, Set<String> foreignKeyList, List<Failure> failureList) throws MaximumErrorsValidationException {
        if (checkForHeaderExistence(fileName, Collections.singletonList(foreignKey), "yes", failureList))
            return readColumnIntoSet(fileName, foreignKey, foreignKeyList, failureList);
        return false;
    }

    /**
     * Reads column into the set. If the header does not exist it wont return a failure.
     * Use  checkForHeaderExistence to check for the header
     */
    private static boolean readColumnIntoSet(String fileName,String fieldToCompare, Set<String> fieldNameList, List<Failure> failureList) throws MaximumErrorsValidationException {
        List<String[]> collectList = new ArrayList<>();
        if (readFileIntoMemory(fileName, collectList, failureList)) {
            List<String> headers = Arrays.asList(collectList.get(0));
            int fieldIndex = headers.indexOf(fieldToCompare);
            if (fieldIndex < 0) return false;
            collectList.remove(0);
            for (String[] fileRow : collectList) {
                List<String> fileRowList = Arrays.asList(fileRow);
                if (fileRowList.size() > fieldIndex)
                    if (!isNullAndEmpty(fileRowList.get(fieldIndex)))
                        fieldNameList.add(fileRowList.get(fieldIndex));
            }
            return true;
        } else return false;
    }

    private static void printMissingFieldError(String s1, String s2, List<Failure> failureList) throws MaximumErrorsValidationException {
        createFailure(FailureTypes.CORRUPTED_VALIDATION_FILE, new ArrayList<>(), "Condition type defined " + s1 + " but " + s2 + " not defined.", failureList);
    }

    static void processFileError(String fileName, int size, List<Failure> failureList) throws MaximumErrorsValidationException {
        if (size > 1)
            createFailure(FailureTypes.MORE_THAN_ONE_FILE, new ArrayList<>(), fileName, failureList);
        else if (size < 1)
            createFailure(FailureTypes.FILE_NOT_FOUND, new ArrayList<>(), fileName, failureList);
    }

    /**
     * Adds a failure to the failure list.
     * If the same error exists append the values else create a new one.
     * If max errors are more than 5 throw exception
     *
     * @param failure     failure
     * @param failureList failure list
     * @throws MaximumErrorsValidationException exception when maximum number of failures occured
     */
    static void addMessageToList(Failure failure, List<Failure> failureList) throws MaximumErrorsValidationException {
        String reason = failure.reason;
        boolean matchNotFound = true;
        int noOfMatches = 0;
        for (Failure fail : failureList)
            if (fail.reason.equalsIgnoreCase(reason)) {
                // Both the columns are not null and they are equal. If they are null even thought they are same errors they are not related
                if (failure.columnName.size() > 0 && fail.columnName.size() > 0 && fail.columnName.containsAll(failure.columnName) && failure.columnName.containsAll(fail.columnName)) {
                    fail.values.addAll(failure.values);
                    noOfMatches = fail.values.size();
                    matchNotFound = false;
                }
            }
        if (matchNotFound) failureList.add(failure);
       /* int noOfFailures = 0;
        for (Failure fail : failureList) {
            noOfFailures = noOfFailures + fail.values.size();
        }*/
        if (noOfMatches >= 3) throw new MaximumErrorsValidationException();
    }

    /**
     * Check for the header in a file.
     * Check the required field and returns status appropriately
     *
     * @param fieldName   field name
     * @param fileName    fileName
     * @param required    column required or not
     * @param failureList failure list
     * @return header exists or not
     * @throws MaximumErrorsValidationException exception
     */
    private static boolean checkForHeaderExistence(String fileName, List<String> fieldName, String required, List<Failure> failureList) throws MaximumErrorsValidationException {
        List<String> headers = new ArrayList<>();
        if (getHeaders(fileName, headers, failureList)) {
            boolean headerExist = headers.contains(fieldName.get(0));
            // If header exists proceed
            if (headerExist) return true;
                // Header does not exist and condition states this as required
            else if (required.equalsIgnoreCase("yes")) {
                createFailure(FailureTypes.COLUMN_NOT_FOUND, Collections.singletonList(fieldName.get(0)), failureList);
                return false;
            } else return false;
        } else return false;
    }

    private static boolean getHeaders(String fileName, List<String> headers, List<Failure> failureList) throws MaximumErrorsValidationException {
        List<String[]> collectList = new ArrayList<>();
        if (readFileIntoMemory(fileName, collectList, failureList)) {
            headers.addAll(Arrays.asList(collectList.get(0)));
            return true;
        } else return false;

    }

    static void createFailure(String reason, List<String> columnName, List<Failure> failureList) throws MaximumErrorsValidationException {
        Failure failure = new Failure();
        failure.reason = reason;
        failure.columnName.addAll(columnName);
        ValidationUtil.addMessageToList(failure, failureList);
    }

    static void createFailure(String reason, List<String> columnName, String value, List<Failure> failureList) throws MaximumErrorsValidationException {
        Failure failure = new Failure();
        failure.reason = reason;
        if (columnName.size() > 0)
            failure.columnName.addAll(columnName);
        failure.values.add(value);
        ValidationUtil.addMessageToList(failure, failureList);
    }

    static void createFailure(String reason,
                              List<String> columnName,
                              List<String> values,
                              List<Failure> failureList) throws MaximumErrorsValidationException {
        for(String value : values) {
            createFailure(
                reason,
                columnName,
                value,
                failureList);
        }
    }
}
