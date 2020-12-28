package org.gobiiproject.gobiidomain.services.gdmv3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.LoaderInstruction;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import javax.persistence.Table;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Utils {

    static ConfigSettings configSettings = new ConfigSettings();

    static ObjectMapper mapper = new ObjectMapper();

    static String getProcessDir(String cropType,
                                GobiiFileProcessDir gobiiFileProcessDir
    ) throws GobiiDomainException {
        try {

            String inputFileDir = Utils.configSettings.getProcessingPath(
                cropType,
                gobiiFileProcessDir);
            makeDir(inputFileDir);
            return inputFileDir;
        }
        catch (Exception e) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Unable to create input files directory");
        }
    }

    static void writeByteArrayToFile(String filePath,
                                     byte[] fileContent) throws GobiiDomainException {
        try {
            File file = new File(filePath);
            makeDir(file.getParent());
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
            stream.write(fileContent);
            stream.close();
        }
        catch (IOException e) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Unable to create input files directory");
        }
    }

    static File writeInputStreamToFile(String filePath,
                                       InputStream fileStream) throws GobiiDomainException {
        try {
            File file = new File(filePath);
            makeDir(file.getParent());
            Files.copy(fileStream,
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
            IOUtils.closeQuietly(fileStream);
            return file;
        }
        catch (IOException e) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Unable to create input files directory");
        }
    }

    static void makeDir(String dirPath) {
        File file = new File(dirPath);
        if(!file.exists()) {
            file.mkdirs();
        }
    }

    static void setField(Object instance, String fieldName, Object value
    ) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);

    }

    /**
     * @param cvs List of cvs
     * @return map of Cvs by their names
     */
    static Map<String, Cv> mapCvNames(List<Cv> cvs) {
        Map<String, Cv> cvMap = new HashMap<>();
        for(Cv cv : cvs) {
            cvMap.put(cv.getTerm(), cv);
        }
        return cvMap;
    }

    /**
     *
     * @param inputFileStream   input file stream to be loaded.
     * @param jobName           name of the job
     * @param cropType          type of the crop
     * @return path of the input file
     */
    static File writeInputFile(InputStream inputFileStream,
                                 String fileName,
                                 String jobName,
                                 String cropType) throws GobiiDomainException {

        String rawFilesDir = Utils.getProcessDir(cropType, GobiiFileProcessDir.RAW_USER_FILES);
        String inputFilePath = Paths.get(rawFilesDir, jobName, fileName).toString();
        return writeInputStreamToFile(inputFilePath, inputFileStream);
    }

    /**
     * Creates output directory for given job and crop type.
     * @param jobName   Name of the job
     * @param cropType  Crop type for which the data is loaded.
     * @return  output directory where digest files needs to be created for the loading job.
     */
    static String getOutputDir(String jobName, String cropType) {
        String interMediateFilesDir = Utils.getProcessDir(cropType,
            GobiiFileProcessDir.LOADER_INTERMEDIATE_FILES);
        String outputFilesDir = Paths.get(interMediateFilesDir, jobName).toString();
        Utils.makeDir(outputFilesDir);
        return outputFilesDir;
    }

    static void writeInstructionFile(LoaderInstruction loaderInstruction,
                                     String jobName,
                                     String cropType) throws GobiiDomainException {
        try {
            String loaderInstructionText = mapper.writeValueAsString(loaderInstruction);
            String instructionFileName = jobName + ".json";

            String instructionFilesDir = Utils.getProcessDir(
                cropType,
                GobiiFileProcessDir.LOADER_INSTRUCTIONS);

            String instructionFilePath = Paths.get(
                instructionFilesDir,
                instructionFileName).toString();

            Utils.writeByteArrayToFile(
                instructionFilePath,
                loaderInstructionText.getBytes());
        }
        catch (JsonProcessingException jE) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Unable to submit job file");
        }
    }

    /**
     * @param dnaRunTemplateMap
     * @param propertyFieldNames
     * @return
     */
    public static Map<String, List<String>> getFileColumnsApiFieldsMap(
        Map<String, Object> dnaRunTemplateMap,
        HashSet<String> propertyFieldNames) {

        if(propertyFieldNames == null) {
            propertyFieldNames = new HashSet<>();
        }
        Map<String, List<String>> fileColumnsApiFieldsMap = new HashMap<>();
        List<String> fileField;

        for(String apiField : dnaRunTemplateMap.keySet()) {
            if(propertyFieldNames.contains(apiField)) {
                Map<String, List<String>> properties =
                    (HashMap<String, List<String>>) dnaRunTemplateMap.get(apiField);
                for(String property : properties.keySet()) {
                    fileField = properties.get(property);
                    if(fileField.size() > 0) {
                        if(!fileColumnsApiFieldsMap.containsKey(fileField.get(0))) {
                            fileColumnsApiFieldsMap.put(fileField.get(0), new ArrayList<>());
                        }
                        fileColumnsApiFieldsMap.get(fileField.get(0)).add(apiField+"."+property);
                    }
                }
            }
            else {
                fileField = (List<String>) dnaRunTemplateMap.get(apiField);
                if (fileField.size() > 0) {
                    if(!fileColumnsApiFieldsMap.containsKey(fileField.get(0))) {
                        fileColumnsApiFieldsMap.put(fileField.get(0), new ArrayList<>());
                    }
                    fileColumnsApiFieldsMap.get(fileField.get(0)).add(apiField);
                }
            }
        }
        return fileColumnsApiFieldsMap;
    }

    public static String getTableName(Class<?> entity) throws NullPointerException {
        return entity.getDeclaredAnnotation(GobiiAspectTable.class).name();
    }

    public static String[] getHeaders(File inputFile) throws GobiiDomainException {
        BufferedReader br;
        String fileHeader;
        try {
            br = new BufferedReader(new FileReader(inputFile));
            fileHeader = br.readLine();
        }
        catch (IOException io) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                "No able to read file header");
        }

        String[] fileColumns = fileHeader.split("\t");
        return fileColumns;
    }

}
