package org.gobiiproject.gobiimodel.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.gobiiproject.gobiimodel.config.GobiiException;

/**
 * Created by araquel on 3/16/2017.
 */
public class InstructionFileAccess<T> {

    //private final String LOADER_FILE_EXT = ".json";


    Class<T> instanceType;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public InstructionFileAccess(Class instanceType) {
        this.instanceType = instanceType;
    }

    public Boolean writeInstructions(String instructionFileFqpn,
                                     T instructions) throws GobiiException {

        Boolean returnVal = null;

        try {

            File instructionFile = new File(instructionFileFqpn);
            if (!instructionFile.exists()) {

                String filePath = instructionFile.getAbsolutePath().
                        substring(0, instructionFile.getAbsolutePath().lastIndexOf(File.separator));

                File destinationDirectory = new File(filePath);

                if (destinationDirectory.exists()) {

                    if (destinationDirectory.isDirectory()) {

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                        objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
                        String instructionsAsJson = objectMapper.writeValueAsString(instructions);
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(instructionFileFqpn));
                        bufferedWriter.write(instructionsAsJson);
                        bufferedWriter.flush();
                        bufferedWriter.close();

                        returnVal = true;
                    } else {
                        throw new GobiiException("Path of specified instruction file name is not a directory: "
                                + destinationDirectory);
                    } // if-else directory is really a directory

                } else {
                    throw new GobiiException("Path of specified instruction file does not exist: "
                            + destinationDirectory);

                } // if-else destination directory exists

            } else {

                throw new GobiiException("The specified instruction file already exists: "
                        + instructionFileFqpn);
            } // if-else file does not arleady exist

        } catch (Exception e) {
            String message = e.getMessage() + "; fqpn: " + instructionFileFqpn;
            throw new GobiiException(message);
        }

        return returnVal;

    } // writeInstructions


    public T getProcedure(String instructionFileFqpn) {

        T returnVal = null;

        try {

            File file = new File(instructionFileFqpn);

            FileInputStream fileInputStream = new FileInputStream(file);

            org.codehaus.jackson.map.ObjectMapper objectMapper = new org.codehaus.jackson.map.ObjectMapper();

            returnVal = objectMapper.readValue(fileInputStream, instanceType);

        } catch (Exception e) {
            String message = e.getMessage() + "; fqpn: " + instructionFileFqpn;
            throw new GobiiException(message);
        }

        return returnVal;

    }
    // it is irritating that we seem to need a separate function that does everything the same as getINstruction()
    // except except the type parameterization for the List type. There is probably a more elegant and hence
    // less redundant way tot do this; however, for now, encapsulating this code in one class is a huge improvement
    // over the way things were.
    public List<T> getInstructions(String instructionFileFqpn, Class<T[]> listType) throws GobiiException {

        List<T> returnVal = null;

        try {

            T[] instructions = null;

            File file = new File(instructionFileFqpn);

            FileInputStream fileInputStream = new FileInputStream(file);

            org.codehaus.jackson.map.ObjectMapper objectMapper = new org.codehaus.jackson.map.ObjectMapper();

            instructions = objectMapper.readValue(fileInputStream, listType);

            returnVal = Arrays.asList(instructions);

        } catch (Exception e) {
            String message = e.getMessage() + "; fqpn: " + instructionFileFqpn;

            throw new GobiiException(message);
        }

        return returnVal;

    }

    public boolean doesPathExist(String pathName) throws GobiiException {
        return new File(pathName).exists();
    }

    public void verifyDirectoryPermissions(String pathName) throws GobiiException {

        File pathToCreate = new File(pathName);
        if (!pathToCreate.canRead() && !pathToCreate.setReadable(true, false)) {
            throw new GobiiException("Unable to set read permissions on directory " + pathName);
        }

        if (!pathToCreate.canWrite() && !pathToCreate.setWritable(true, false)) {
            throw new GobiiException("Unable to set write permissions on directory " + pathName);
        }
    }


    public void makeDirectory(String pathName) throws GobiiException {

        if (!doesPathExist(pathName)) {

            File pathToCreate = new File(pathName);

            if (!pathToCreate.mkdirs()) {
                throw new GobiiException("Unable to create directory " + pathName);
            }

            if ((!pathToCreate.canRead()) && !(pathToCreate.setReadable(true, false))) {
                throw new GobiiException("Unable to set read on directory " + pathName);
            }

            if ((!pathToCreate.canWrite()) && !(pathToCreate.setWritable(true, false))) {
                throw new GobiiException("Unable to set write on directory " + pathName);
            }


        } else {
            throw new GobiiException("The specified path already exists: " + pathName);
        }
    }


    public void createDirectory(String instructionFileDirectory) throws GobiiException {


        if (null != instructionFileDirectory) {

            if (!this.doesPathExist(instructionFileDirectory)) {

                this.makeDirectory(instructionFileDirectory);

            } else {
                this.verifyDirectoryPermissions(instructionFileDirectory);
            }
        }

    } // createDirectories()


    public void writePlainFile(String fileFqpn, byte[] byteArray) throws GobiiException {

        try {

            File file = new File(fileFqpn);

            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(file));
            stream.write(byteArray);
            stream.close();

        } catch (IOException e) {
            throw new GobiiException("Error wriring file " + fileFqpn + ": " + e.getMessage());
        }

    }

    public String makeFileName(String pathToFile, String fileNameStem) {
        if (pathToFile.charAt(pathToFile.length() - 1) != '/') {
            pathToFile += '/';
        }
        String returnVal = pathToFile + fileNameStem;
        return returnVal;

    }


    public String writeFile(String path,
                          String fileName,
                          byte[] byteArray) throws Exception {


        this.createDirectory(path);
        String fqpn = this.makeFileName(path, fileName);
        this.writePlainFile(fqpn, byteArray);
        return fqpn;
    }

    public void deleteFile(String fqpn) throws Exception {

        File file = new File(fqpn);
        if( file.exists() ) {
            if ( ! file.delete() ) {
                throw new GobiiException("Unable to delete file: " + file.getName());
            }
        } else {
            throw new GobiiException("The specified file does not exist: " + file.getName());
        }
    }

    public File readFile(String fqpn) throws Exception {

        File returnVal = null;

        File file = new File(fqpn);
        if (file.exists()) {
            returnVal = file;
        }
        return returnVal;
    }

}
