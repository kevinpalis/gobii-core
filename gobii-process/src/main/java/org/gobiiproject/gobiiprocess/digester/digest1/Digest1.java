package org.gobiiproject.gobiiprocess.digester.digest1;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.dto.instructions.loader.DigesterResult;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiFile;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiLoaderInstruction;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiLoaderMetadata;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiLoaderProcedure;
import org.gobiiproject.gobiimodel.types.DatasetOrientationType;
import org.gobiiproject.gobiimodel.types.GobiiFileType;
import org.gobiiproject.gobiimodel.types.ServerType;
import org.gobiiproject.gobiimodel.utils.HelperFunctions;
import org.gobiiproject.gobiimodel.utils.InstructionFileValidator;
import org.gobiiproject.gobiimodel.utils.SimpleTimer;
import org.gobiiproject.gobiimodel.utils.email.ProcessMessage;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiiprocess.HDF5Interface;
import org.gobiiproject.gobiiprocess.JobStatus;
import org.gobiiproject.gobiiprocess.LoaderScripts;
import org.gobiiproject.gobiiprocess.digester.DatabaseQuerier;
import org.gobiiproject.gobiiprocess.digester.Digest;
import org.gobiiproject.gobiiprocess.digester.GobiiDigester;
import org.gobiiproject.gobiiprocess.digester.HelperFunctions.MobileTransform;
import org.gobiiproject.gobiiprocess.digester.HelperFunctions.SequenceInPlaceTransform;
import org.gobiiproject.gobiiprocess.digester.csv.CSVFileReaderV2;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;

public class Digest1 implements Digest {
   
    private GobiiLoaderProcedure procedure;
    private ConfigSettings configuration;

    private LoaderScripts loaderScripts;
    private JobStatus jobStatus;
    
    private final ProcessMessage pm = new ProcessMessage();

    
    public Digest1(GobiiLoaderProcedure loaderProcedure, ConfigSettings configuration) {
        this.procedure = loaderProcedure;
        this.configuration = configuration;
        this.loaderScripts = new LoaderScripts(configuration.getFileSystemRoot());
        jobStatus = getJobStatus(procedure.getJobName());
    }

    public JobStatus getJobStatus() {
        return this.jobStatus;
    }

    public DigesterResult digest() throws GobiiException {

        
        boolean success = false;
        Map<String, File> loaderInstructionMap = new HashMap<>();
        List<String> loaderInstructionList = new ArrayList<>();

        boolean isMarkerFast;

        String cropType = procedure.getMetadata().getGobiiCropType();
        String dstFilePath = getDestinationFile(procedure, procedure.getInstructions().get(0));//Intermediate 'file
        String datasetType = procedure.getMetadata().getDatasetType().getName();
        GobiiFileType loadType = procedure.getMetadata().getGobiiFile().getGobiiFileType();
        String loadTypeName = "";//No load type name if default
        if (loadType.equals(GobiiFileType.GENERIC)) loadTypeName = loadType.name();
        GobiiCropConfig gobiiCropConfig = getGobiiCropConfig(configuration, cropType);
        SpringContextLoaderSingleton.init(cropType, configuration);
        JobStatus jobStatus = getJobStatus(procedure.getJobName());
        jobStatus.set(
            JobProgressStatusType.CV_PROGRESSSTATUS_DIGEST.getCvName(),
            "Beginning old instruction file digest");

        Logger.logDebug(
            "Crop Context loaded",
            "Crop config successfully loaded from config location");


        SimpleTimer.start("FileRead");
        Integer dataSetId = procedure.getMetadata().getDataset().getId();

        //Error logs go to a file based on crop (for human readability) and
        Logger.logInfo("Digester", "Beginning read of instruction file");

        if (procedure == null ||
            procedure.getInstructions() == null ||
            procedure.getInstructions().isEmpty()) {

            Logger.logError("Digester", "No instruction for file ");
            throw new GobiiException("Null Loader instruction");
        }

        if (procedure.getMetadata().getGobiiCropType() == null) {
            procedure.getMetadata().setGobiiCropType(cropType);
        }

        pm.addIdentifier("Project", procedure.getMetadata().getProject());
        pm.addIdentifier("Platform", procedure.getMetadata().getPlatform());
        pm.addIdentifier("Experiment", procedure.getMetadata().getExperiment());
        pm.addIdentifier("Dataset", procedure.getMetadata().getDataset());
        pm.addIdentifier("Mapset", procedure.getMetadata().getMapset());
        pm.addIdentifier("Dataset Type", procedure.getMetadata().getDatasetType());

        jobStatus.set(
            JobProgressStatusType.CV_PROGRESSSTATUS_INPROGRESS.getCvName(),
            "Beginning Digest");

        File dstDir = new File(dstFilePath);
        if (!dstDir.isDirectory()) { //Note: if dstDir is a non-existant
            dstDir = new File(dstFilePath.substring(0, dstFilePath.lastIndexOf("/")));
        }

        try {
            //Convert to directory
            pm.addFolderPath("Destination Directory", dstDir.getAbsolutePath()+"/",configuration);
            pm.addFolderPath("Input Directory", procedure.getMetadata().getGobiiFile().getSource()+"/", configuration);
        }
        catch(Exception e) {
            throw new GobiiException(e);
        }

        Path cropPath = Paths.get(configuration.getFileSystemRoot() +
            "crops/"
            + procedure.getMetadata().getGobiiCropType().toLowerCase());

        if (!(Files.exists(cropPath) &&
            Files.isDirectory(cropPath))) {
            Logger.logError("Digester", "Unknown Crop Type: "
                + procedure.getMetadata().getGobiiCropType());
            throw new GobiiException("No Crop directory");
        }
        if (HDF5Interface.getPathToHDF5Files() == null)
            HDF5Interface.setPathToHDF5Files(cropPath.toString() + "/hdf5/");

        String errorPath = getLogName(procedure, procedure.getMetadata().getGobiiCropType());

        jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_VALIDATION.getCvName(),
                "Beginning Validation");
        // Instruction file Validation
        InstructionFileValidator instructionFileValidator = new InstructionFileValidator(procedure);
        instructionFileValidator.processInstructionFile();
        String validationStatus = instructionFileValidator.validateMarkerUpload();
        if (validationStatus != null) {
            Logger.logError("Marker validation failed.", validationStatus);
        }

        validationStatus = instructionFileValidator.validateSampleUpload();
        if (validationStatus != null) {
            Logger.logError("Sample validation failed.", validationStatus);
        }

        validationStatus = instructionFileValidator.validate();
        if (validationStatus != null) {
            Logger.logError("Validation failed.", validationStatus);
        }

        //TODO: HACK - Job's name is
        pm.setUser(procedure.getMetadata().getContactEmail());

        boolean qcCheck;//  zero.isQcCheck();

        SimpleTimer.start("FileRead");

        jobStatus.set(
            JobProgressStatusType.CV_PROGRESSSTATUS_DIGEST.getCvName(),
            "Beginning file digest");
        //Pre-processing - make sure all files exist, find the cannonical dataset id
        for (GobiiLoaderInstruction inst : procedure.getInstructions()) {
            if (inst == null) {
                Logger.logError(
                    "Digester", "Missing or malformed instruction in instruction file");
                continue;
            }
        }

        if (procedure.getMetadata().getGobiiFile() == null) {
            Logger.logError("Digester", "Instruction has bad 'file' column");
        }
        GobiiFileType instructionFileType =
            procedure.getMetadata().getGobiiFile().getGobiiFileType();

        if (instructionFileType == null) {
            Logger.logError("Digester", "Instruction has missing file format");
        }


        //Section - Processing
        Logger.logTrace("Digester", "Beginning List Processing");
        success = true;
        switch (procedure.getMetadata().getGobiiFile().getGobiiFileType()) { //All instructions should have the same file type, all file types go through CSVFileReader(V2)
            case HAPMAP:
                //INTENTIONAL FALLTHROUGH
            case VCF:
                //INTENTIONAL FALLTHROUGH
            case GENERIC:
                CSVFileReaderV2.parseInstructionFile(procedure, loaderScripts.getPath());
                break;
            default:
                System.err.println("Unable to deal with file type " + procedure.getMetadata().getGobiiFile().getGobiiFileType());
                break;
        }

        //Database Validation
        jobStatus.set(
            JobProgressStatusType.CV_PROGRESSSTATUS_VALIDATION.getCvName(),
            "Database Validation");
        databaseValidation(loaderInstructionMap, procedure.getMetadata(), gobiiCropConfig);

        boolean sendQc = false;

        qcCheck = procedure.getMetadata().isQcCheck();

        boolean isVCF =
            GobiiFileType.VCF.equals(procedure.getMetadata().getGobiiFile().getGobiiFileType());

        for (GobiiLoaderInstruction inst : procedure.getInstructions()) {

            // Section - Matrix Post-processing
            // Dataset is the first non-empty dataset type
            // Switch used for VCF transforms is currently a change in dataset type.
            // See 'why is VCF a data type' GSD
            if (isVCF) {
                procedure.getMetadata().getDataset().setName("VCF");
            }

            String fromFile = getDestinationFile(procedure, inst);

            SequenceInPlaceTransform intermediateFile =
                new SequenceInPlaceTransform(fromFile, errorPath);

            if (procedure.getMetadata().getDatasetType().getName() != null
                && inst.getTable().equals(GobiiDigester.VARIANT_CALL_TABNAME)) {

                errorPath = GobiiDigester.getLogName(
                    dstFilePath,
                    procedure.getMetadata().getGobiiCropType(),
                    "Matrix_Processing"); //Temporary Error File Name

                if (DatasetOrientationType.SAMPLE_FAST.equals(procedure.getMetadata().getDatasetOrientationType())) {
                    //Rotate to marker fast before loading it - all data is marker fast in the system
                    File transposeDir = new File(new File(fromFile).getParentFile(), "transpose");
                    intermediateFile.transform(MobileTransform.getTransposeMatrix(transposeDir.getPath()));
                    isMarkerFast=false;
                }else{
                    isMarkerFast=true;
                }
            }
            jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_TRANSFORMATION.getCvName(), "Metadata Transformation");
            String instructionName = inst.getTable();
            loaderInstructionMap.put(instructionName, new File(getDestinationFile(procedure, inst)));
            loaderInstructionList.add(instructionName);//TODO Hack - for ordering

            if (GobiiDigester.LINKAGE_GROUP_TABNAME.equals(instructionName) ||
                GobiiDigester.GERMPLASM_TABNAME.equals(instructionName) ||
                GobiiDigester.GERMPLASM_PROP_TABNAME.equals(instructionName)) {

                success &= HelperFunctions.tryExec(
                    loaderScripts.getLgDuplicatesScript()
                        + " -i "
                        + getDestinationFile(procedure, inst));

            }
            if (GobiiDigester.MARKER_TABNAME.equals(instructionName)) {//Convert 'alts' into a jsonb array
                intermediateFile.transform(MobileTransform.PGArray);
            }

            intermediateFile.returnFile(); // replace intermediateFile where it came from

            //DONE WITH TRANSFORMS

        }
    

        DigesterResult digesterResult = new DigesterResult
                .Builder()
                .setSuccess(success)
                .setSendQc(sendQc)
                .setCropType(cropType)
                .setCropConfig(gobiiCropConfig)
                .setIntermediateFilePath(dstFilePath)
                .setLoadType(loadTypeName)
                .setLoaderInstructionsMap(loaderInstructionMap)
                .setLoaderInstructionsList(loaderInstructionList)
                .setDatasetType(datasetType)
                .setDatasetId(dataSetId)
                .setJobName(procedure.getJobName())
                .setContactEmail(procedure.getMetadata().getContactEmail())
                .build();

        return digesterResult;

    }

    private GobiiCropConfig getGobiiCropConfig(ConfigSettings configuration,
                                                      String cropType) {

        GobiiCropConfig gobiiCropConfig;

        try {
            gobiiCropConfig = configuration.getCropConfig(cropType);
            Logger.logDebug(
                "Crop Config Load",
                "Crop config successfully loaded");

        } catch (Exception e) {
            Logger.logError("Digester", "Unknown loading error", e);
            throw new GobiiException(e);
        }
        if (gobiiCropConfig == null) {
            Logger.logError("Digester", "Unknown Crop Type: " + cropType + " in the Configuration File");
            throw new GobiiException(
                "Digester : Unknown Crop Type: " + cropType + " in the Configuration File");
        }
        return gobiiCropConfig;
    }

    /**
     * Converts the File input into the FIRST of the source files.
     *
     * @param file Reference to Instruction's File object.
     * @return String representation of first of source files
     */
    private String getSourceFileName(GobiiFile file) {
        String source = file.getSource();
        return getSourceFileName(source);
    }

    private String getSourceFileName(String source) {
        File sourceFolder = new File(source);
        File[] f = sourceFolder.listFiles();
        if (f.length != 0) source = f[0].getName();
        else {
            source = sourceFolder.getName();//Otherwise we get full paths in source.
        }
        return source;
    }

    /**
     * Generates a log file location given a crop name, crop type, and process ID. (Given by the process calling this method).
     * <p>
     * Currently works by placing logs in the intermediate file directory.
     *
     * @return The logfile location for this process
     */
    private String getLogName(GobiiLoaderProcedure procedure, String cropName) {
        String destination = procedure.getMetadata().getGobiiFile().getDestination();
        String table = procedure.getInstructions().get(0).getTable();
        return destination + "/" + cropName + "_Table-" + table + ".log";
    }
    
    private void databaseValidation(Map<String, File> loaderInstructionMap, 
        GobiiLoaderMetadata metadata, GobiiCropConfig gobiiCropConfig) {
    
        DatabaseQuerier querier = new DatabaseQuerier(
            gobiiCropConfig.getServer(ServerType.GOBII_PGSQL));

        //If we're doing a DS upload and there is no DS_Marker
        if (loaderInstructionMap.containsKey(GobiiDigester.VARIANT_CALL_TABNAME) && 
            loaderInstructionMap.containsKey(GobiiDigester.DS_MARKER_TABNAME) && 
            !loaderInstructionMap.containsKey(GobiiDigester.MARKER_TABNAME)) {
            querier.checkMarkerInPlatform(
                loaderInstructionMap.get(GobiiDigester.DS_MARKER_TABNAME), metadata.getPlatform().getId());
        }
        //If we're doing a DS upload and there is no DS_Sample
        if (loaderInstructionMap.containsKey(GobiiDigester.VARIANT_CALL_TABNAME) && 
            loaderInstructionMap.containsKey(GobiiDigester.DS_SAMPLE_TABNAME) && 
            !loaderInstructionMap.containsKey(GobiiDigester.SAMPLE_TABNAME)) {
            querier.checkDNARunInExperiment(loaderInstructionMap.get(GobiiDigester.DS_SAMPLE_TABNAME), metadata.getExperiment().getId());
        }

        if (loaderInstructionMap.containsKey(GobiiDigester.MARKER_TABNAME)) {
            querier.checkMarkerExistence(loaderInstructionMap.get(GobiiDigester.MARKER_TABNAME));
        }
        if (loaderInstructionMap.containsKey(GobiiDigester.GERMPLASM_TABNAME)) {
            querier.checkGermplasmTypeExistence(loaderInstructionMap.get(GobiiDigester.GERMPLASM_TABNAME));
            querier.checkGermplasmSpeciesExistence(loaderInstructionMap.get(GobiiDigester.GERMPLASM_TABNAME));
        }
        querier.close();
    }
    
    private JobStatus getJobStatus(String jobName) {
        try {
            JobStatus jobStatus = new JobStatus(jobName);
            return jobStatus;
        } catch (Exception e) {
            Logger.logError("GobiiFileReader", "Error Checking Status", e);
            throw new GobiiException("Unable to find the instruction file job");
        }
    }
    
    private String getDestinationFile(GobiiLoaderProcedure procedure, GobiiLoaderInstruction instruction) {
        String destination = procedure.getMetadata().getGobiiFile().getDestination();
        char last = destination.charAt(destination.length() - 1);
        if (last == '\\' || last == '/') {
            return destination + "digest." + instruction.getTable();
        } else return destination + "/" + "digest." + instruction.getTable();
    }
    
    /**
     * Generates a log file location given a crop name, crop type, and process ID. (Given by the process calling this method).
     * <p>
     * Currently works by placing logs in the intermediate file directory.
     *
     * @return The logfile location for this process
     */
    private String getLogName(GobiiLoaderMetadata metadata, String cropName, String process) {
        String destination = metadata.getGobiiFile().getDestination();
        return destination + "/" + cropName + "_Process-" + process + ".log";
    }

}
