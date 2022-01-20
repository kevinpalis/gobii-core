package org.gobiiproject.gobiiprocess.digester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.type.MapType;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.gobii.masticator.Masticator;
import org.gobii.masticator.aspects.AspectParser;
import org.gobii.masticator.aspects.FileAspect;
import org.gobiiproject.gobiiapimodel.payload.Header;
import org.gobiiproject.gobiiapimodel.payload.HeaderStatusMessage;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.restresources.gobii.GobiiUriFactory;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.dto.Marshal;
import org.gobiiproject.gobiimodel.dto.instructions.loader.*;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.IflConfig;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.LoaderInstruction3;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.ExtractorInstructionFilesDTO;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.GobiiDataSetExtract;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.GobiiExtractorInstruction;
import org.gobiiproject.gobiimodel.types.DatasetOrientationType;
import org.gobiiproject.gobiimodel.types.GobiiAutoLoginType;
import org.gobiiproject.gobiimodel.types.GobiiExtractFilterType;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.types.GobiiFileType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.ServerType;
import org.gobiiproject.gobiimodel.utils.DateUtils;
import org.gobiiproject.gobiimodel.utils.FileSystemInterface;
import org.gobiiproject.gobiimodel.utils.HelperFunctions;
import org.gobiiproject.gobiimodel.utils.InstructionFileValidator;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiimodel.utils.SimpleTimer;
import org.gobiiproject.gobiimodel.utils.email.MailInterface;
import org.gobiiproject.gobiimodel.utils.email.ProcessMessage;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiiprocess.HDF5Interface;
import org.gobiiproject.gobiiprocess.JobStatus;
import org.gobiiproject.gobiiprocess.LoaderScripts;
import org.gobiiproject.gobiiprocess.digester.HelperFunctions.MobileTransform;
import org.gobiiproject.gobiiprocess.digester.HelperFunctions.SequenceInPlaceTransform;
import org.gobiiproject.gobiiprocess.digester.csv.CSVFileReaderV2;
import org.gobiiproject.gobiiprocess.digester.utils.validation.DigestFileValidator;
import org.gobiiproject.gobiiprocess.digester.utils.validation.ValidationConstants;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.ValidationError;
import org.gobiiproject.gobiiprocess.services.MarkerGroupService;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;
import org.gobiiproject.gobiisampletrackingdao.DatasetDao;

import static org.gobiiproject.gobiimodel.utils.FileSystemInterface.rmIfExist;
import static org.gobiiproject.gobiimodel.utils.HelperFunctions.*;
import static org.gobiiproject.gobiimodel.utils.error.Logger.logError;

/**
 * Base class for processing instruction files. Start of chain of control for Digester. Takes first argument as instruction file, or promts user.
 * The File Reader runs off the Instruction Files, which tell it where the input files are, and how to process them.
 * {@link CSVFileReaderV2} and deal with specific file formats. Overall logic and program flow come from this class.
 * <p>
 * This class deals with external commands and scripts, and coordinates uploads to the IFL and directly talks to HDF5 and MonetDB.
 *
 * @author jdl232 Josh L.S.
 */
@SuppressWarnings("unused")
public class GobiiDigester {
    private static String rootDir = "../";
    public static final String VARIANT_CALL_TABNAME = "matrix";
    public static final String LINKAGE_GROUP_TABNAME = "linkage_group";
    public static final String GERMPLASM_PROP_TABNAME = "germplasm_prop";
    public static final String GERMPLASM_TABNAME = "germplasm";
    public static final String MARKER_TABNAME = "marker";
    public static final String MARKER_GROUP_TABNAME = "marker_group";
    public static final String DS_MARKER_TABNAME = "dataset_marker";
    public static final String DS_SAMPLE_TABNAME = "dataset_dnarun";
    public static final String SAMPLE_TABNAME = "dnarun";
    private static boolean verbose;
    private static GobiiExtractorInstruction qcExtractInstruction = null;
    private static final String masticatorModuleName = "MASTICATOR";
    private static LoaderScripts loaderScripts;
    private static final ProcessMessage pm = new ProcessMessage();
    private static ConfigSettings configuration;
    private static ObjectMapper jsonMapper = new ObjectMapper();

    // Trinary - was this load marker fast(true), sample fast(false),
    // or unknown/not applicable(null)
    public static Boolean isMarkerFast=null;

    private static Boolean isV3Instruction(LoaderInstruction3 loaderInstruction) {
        return StringUtils.isNotEmpty(loaderInstruction.getInstructionType()) &&
            loaderInstruction.getInstructionType().equals("v3");
    }

    /**
     * Main class of Digester Jar file. Uses command line parameters to determine instruction file,
     * and runs whole program.
     *
     * @param args See Digester.jar -? to get a list of arguments
     * throws FileNotFoundException, IOException, ParseException, InterruptedException
     */
    public static void main(String[] args) throws Exception {

        LoaderInstruction3 loaderInstructions;

        String errorPath;
        String configLocation = null;

        //Section - Setup
        Options appOptions = new Options()
                .addOption("v", "verbose", false, "Verbose output")
                .addOption("e", "errlog", true, "Error log override location")
                .addOption("r", "rootDir", true, "Fully qualified path to gobii root directory")
                .addOption("c", "config", true, "Fully qualified path to gobii configuration file")
                .addOption("h", "hdfFiles", true, "Fully qualified path to hdf files");
        LoaderGlobalConfigs.addOptions(appOptions);
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cli = parser.parse(appOptions, args);
            if (cli.hasOption("rootDir")) rootDir = cli.getOptionValue("rootDir");
            if (cli.hasOption("verbose")) verbose = true;
            if (cli.hasOption("config")) configLocation = cli.getOptionValue("config");
            if (cli.hasOption("hdfFiles")) HDF5Interface.setPathToHDF5Files(
                cli.getOptionValue("hdfFiles"));
            LoaderGlobalConfigs.setFromFlags(cli);
            args = cli.getArgs();
        } catch (org.apache.commons.cli.ParseException exp) {
            String helpMessage = (
                "Also accepts input file directly after arguments\n" +
                "Example: java -jar Digester.jar " +
                "-c /home/jdl232/customConfig.properties " +
                "-v /home/jdl232/testLoad.json");
            new HelpFormatter().printHelp(
                "java -jar Digester.jar ",
                helpMessage,
                appOptions,
                null,
                true);
            System.exit(2);
        }

        if (configLocation == null) {
            configLocation = getDefaultConfigLocation(rootDir);
        }

        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Object holding all path to loader scripts and binaries
        loaderScripts = new LoaderScripts(rootDir);

        HDF5Interface.setPathToHDF5(loaderScripts.getHdf5BinariesPath());

        // Read config file.
        setConfigSettings(configLocation);

        MailInterface mailInterface = new MailInterface(configuration);

        // Get instruction file. instruction file could be either old format or new format.
        File instructionFile = new File(getInstructionFile(args));

        if(!instructionFile.exists()) {
            throw new GobiiException("Instruction file does not exist");
        }
        
        String logFile = setLogger(configuration, instructionFile.getAbsolutePath());

        Digest digest = new DigestFactory().getDigest(instructionFile, configuration);
        
        // Job status object for instruction file
        JobStatus jobStatus = digest.getJobStatus();

        if(jobStatus == null) {
            Logger.logError("Instruction Digest", "Unable to find the Job");
            System.exit(2);
        }

        DigesterResult digestResult;
        try {
            digestResult = digest.digest();
        }
        catch (GobiiException gE) {
            jobStatus.setError(gE.getMessage());
            Logger.logError("Instruction Digest", gE.getMessage());
            throw gE;
        }

        // Validate Intermediate digest files.
        validateData(digestResult);

        // Load meta data if instruction file processing and validation is successful.
        boolean metaDataLoaded = false;
        if (digestResult.isSuccess() && Logger.success()) {
            metaDataLoaded = loadMetaData(digestResult, jobStatus);
            if (!metaDataLoaded) {
                Logger.logError("FileReader", "No new data was uploaded.");
                jobStatus.setError("No new data was uploaded.");
            }
        }
        else {
            Logger.logWarning("Digester", "Aborted - Unsuccessfully Generated Files");
            jobStatus.setError("Unsuccessfully Generated Files - No Data Upload");
        }

        // Load genotype matrix
        boolean dataLoaded = metaDataLoaded;
        if(metaDataLoaded &&
            Logger.success() &&
            digestResult.hasGenotypeMatrix()) {

            dataLoaded &= loadGenoypeMatrix(digestResult, jobStatus);
        }

        System.out.println(dataLoaded);
        System.out.println(Logger.getAllErrors());

        // Send Qc
        //if (dataLoaded && Logger.success()) {
        //    if (digestResult.isSendQc()) {//QC - Subsection #2 of 3
        //        qcExtractInstruction = createQCExtractInstruction(procedure.getMetadata(), procedure.getMetadata().getGobiiCropType());
        //        setQCExtractPaths(procedure.getMetadata());
        //    }

        //    Logger.logInfo("Digester", "Successful Data Upload");
        //    if (digestResult.isSendQc()) {
        //        jobStatus.set(
        //            JobProgressStatusType.CV_PROGRESSSTATUS_QCPROCESSING.getCvName(),
        //            "Processing QC Job");
        //        sendQCExtract(configuration, digestResult.getCropType());
        //    } else {
        //        jobStatus.set(
        //            JobProgressStatusType.CV_PROGRESSSTATUS_COMPLETED.getCvName(),
        //            "Successful Data Load");
        //    }
        //} else { //endIf(success)
        //    Logger.logWarning("Digester", "Unsuccessful Upload");
        //    jobStatus.setError("Unsuccessfully Uploaded Files");
        //}

        //Send Email
        finalizeProcessing(
            digestResult,
            configuration,
            mailInterface,
            instructionFile.getAbsolutePath(),
            logFile);

    }

    private static void validateData(DigesterResult digesterResult) {

        try {
            //Metadata Validation
            boolean reportedValidationFailures = false;
            if (LoaderGlobalConfigs.isEnableValidation()) {
                File digestFilesDir = getDestinationDir(digesterResult.getIntermediateFilePath());
                DigestFileValidator digestFileValidator =
                    new DigestFileValidator(digestFilesDir.getAbsolutePath());
                digestFileValidator.performValidation(digesterResult.getCropConfig());
                //Call validations here, update 'success' to false with any call to ErrorLogger.logError()
                List<Path> pathList =
                    Files
                        .list(Paths.get(digestFilesDir.getAbsolutePath()))
                        .filter(Files::isRegularFile)
                        .filter(path -> String.valueOf(path.getFileName()).endsWith(".json"))
                        .collect(Collectors.toList());

                if (pathList.size() < 1) {
                    Logger.logError("Validation", "Unable to find validation checks");
                }
                ValidationError[] fileErrors =
                    new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationError[].class);
                boolean hasAnyFailedStatuses = false;
                for (ValidationError status : fileErrors) {
                    if (status.status.equalsIgnoreCase(ValidationConstants.FAILURE)) {
                        hasAnyFailedStatuses = true;
                    }
                }
                for (ValidationError status : fileErrors) {
                    if (status.status.equalsIgnoreCase(ValidationConstants.FAILURE)) {
                        if (!reportedValidationFailures) {//Lets only add this to the error log once
                            Logger.logError("Validation", "Validation failures");
                            reportedValidationFailures = true;
                        }
                        for (int i = 0; i < status.failures.size(); i++)
                            pm.addValidateTableElement(status.fileName, status.status, status.failures.get(i).reason, status.failures.get(i).columnName, status.failures.get(i).values);
                    }
                    if (status.status.equalsIgnoreCase(ValidationConstants.SUCCESS)) {
                        //If any failed statii(statuses) exist, we should have this table, otherwise it should not exist
                        if (hasAnyFailedStatuses) {
                            pm.addValidateTableElement(status.fileName, status.status);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            Logger.logError("Validation", "Validation failed", e);
        }

    }

    private static boolean loadMetaData(DigesterResult digesterResult,
                                        JobStatus jobStatus) {

        File dstDir = getDestinationDir(digesterResult.getIntermediateFilePath());


        jobStatus.set(
            JobProgressStatusType.CV_PROGRESSSTATUS_METADATALOAD.getCvName(),
            "Loading Metadata");
        String errorPath = getLogName(
            digesterResult.getIntermediateFilePath(),
            digesterResult.getCropType(),
            "IFLs");
        String connectionString =
            " -c " + HelperFunctions.getPostgresConnectionString(digesterResult.getCropConfig());

        //Load PostgreSQL
        boolean loadedData = false;
        for (String tableName : digesterResult.getLoaderInstructionsList()) {

            if(!digesterResult.getLoaderInstructionsMap().containsKey(tableName)) {
                continue;
            }

            if (!VARIANT_CALL_TABNAME.equals(tableName)) {
                String inputFile = " -i " + digesterResult.getLoaderInstructionsMap().get(tableName);
                //Output here is temporary files, needs terminal /
                String outputFile = " -o " + dstDir.getAbsolutePath() + "/";
                Logger.logInfo(
                    "Digester",
                    "Running IFL: " + loaderScripts.getIfl()
                        + " <conntection string> " + inputFile + outputFile);
                //Lines affected returned by method call - THIS IS NOW IGNORED
                HelperFunctions.tryExec(
                    loaderScripts.getIfl() + connectionString + inputFile + outputFile + " -l",
                    verbose ? dstDir.getAbsolutePath() + "/iflOut" : null,
                    errorPath);

                IFLLineCounts counts = calculateTableStats(
                    digesterResult.getLoaderInstructionsMap(),
                    dstDir,
                    tableName);

                if (counts.loadedData == 0) {
                    Logger.logDebug("FileReader", "No data loaded for table " + tableName);
                } else {
                    loadedData = true;

                }
                if (counts.invalidData > 0 && !isVariableLengthTable(tableName)) {
                    Logger.logWarning("FileReader", "Invalid data in table " + tableName);
                } else {

                    // Load markergroup if aspect found
                    if(tableName.equals(MARKER_TABNAME) &&
                       digesterResult
                        .getLoaderInstructionsMap()
                        .containsKey(MARKER_GROUP_TABNAME)) {

                        MarkerGroupService markerGroupService = SpringContextLoaderSingleton
                            .getInstance()
                            .getBean(MarkerGroupService.class);

                        markerGroupService
                            .addMarkerGroups(
                                digesterResult
                                    .getLoaderInstructionsMap()
                                    .get(MARKER_GROUP_TABNAME));
                    }
                    
                    //If there are no issues in the load,
                    // clean up temporary intermediate files
                    if (!LoaderGlobalConfigs.isKeepAllIntermediates()) {
                        // And if 'delete intermediate files' is true,
                        // clean up all IFL files (we don't need them any more
                        deleteIFLFiles(
                            dstDir,
                            tableName,
                            !LoaderGlobalConfigs.isDeleteIntermediateFiles());
                    }

                }
            }
        }
        return loadedData;
    }


    private static boolean loadGenoypeMatrix(DigesterResult digesterResult,
                                             JobStatus jobStatus
    ) throws Exception {

        boolean hdf5Success = false;

        //Load Monet/HDF5
        String errorPath =
            getLogName(
                digesterResult.getIntermediateFilePath(),
                digesterResult.getCropType(),
                "Matrix_Upload");

        String variantFilename = "DS" + digesterResult.getDatasetId().toString();
        File variantFile = digesterResult.getLoaderInstructionsMap().get(VARIANT_CALL_TABNAME);

        if (variantFile != null && digesterResult.getDatasetId() == null) {
            logError("Digester", "Data Set ID is null for variant call");
        }
        if ((variantFile != null) && digesterResult.getDatasetId() != null) {
            //Create an HDF5 and a Monet
            jobStatus
                .set(JobProgressStatusType.CV_PROGRESSSTATUS_MATRIXLOAD.getCvName(),
                    "Matrix Upload");
            HDF5Interface.setPathToHDF5Files(
                loaderScripts.getPathToHdf5Files(digesterResult.getCropType()));
            hdf5Success = HDF5Interface.createHDF5FromDataset(
                pm,
                digesterResult.getDatasetType(),
                configuration,
                digesterResult.getDatasetId(),
                digesterResult.getCropType(),
                errorPath,
                variantFile);
            rmIfExist(variantFile.getPath());
        }
        return hdf5Success;
    }

    /**
     * Finalize processing step
     * *Include log files
     * *Send Email
     * *update status
     *
     * @param configuration
     * @param mailInterface
     * @param instructionFile
     * @param logFile
     * @throws Exception
     */
    private static void finalizeProcessing(
        DigesterResult digesterResult,
        ConfigSettings configuration,
        MailInterface mailInterface,
        String instructionFile,
        String logFile) throws Exception {

            String instructionFilePath = HelperFunctions.completeInstruction(
                instructionFile,
                configuration.getProcessingPath(
                    digesterResult.getCropType(),
                    GobiiFileProcessDir.LOADER_DONE));

            try {
                pm.addPath("Instruction File", instructionFilePath, configuration, false);
                pm.addPath("Error Log", logFile, configuration, false);
                pm.setUser(digesterResult.getContactEmail());
                pm.setBody(digesterResult.getJobName(),
                    digesterResult.getLoadType(),
                    SimpleTimer.stop("FileRead"),
                    Logger.getFirstErrorReason(),
                    Logger.success(),
                    Logger.getAllErrorStringsHTML());
                mailInterface.send(pm);

        } catch (Exception e) {
            Logger.logError("MailInterface", "Error Sending Mail", e);
        }

    }

    private static void databaseValidation(Map<String, File> loaderInstructionMap, GobiiLoaderMetadata metadata, GobiiCropConfig gobiiCropConfig) {
        DatabaseQuerier querier = new DatabaseQuerier(gobiiCropConfig.getServer(ServerType.GOBII_PGSQL));

        //If we're doing a DS upload and there is no DS_Marker
        if (loaderInstructionMap.containsKey(VARIANT_CALL_TABNAME) && loaderInstructionMap.containsKey(DS_MARKER_TABNAME) && !loaderInstructionMap.containsKey(MARKER_TABNAME)) {
            querier.checkMarkerInPlatform(
                loaderInstructionMap.get(DS_MARKER_TABNAME), metadata.getPlatform().getId());
        }
        //If we're doing a DS upload and there is no DS_Sample
        if (loaderInstructionMap.containsKey(VARIANT_CALL_TABNAME) && loaderInstructionMap.containsKey(DS_SAMPLE_TABNAME) && !loaderInstructionMap.containsKey(SAMPLE_TABNAME)) {
            querier.checkDNARunInExperiment(loaderInstructionMap.get(DS_SAMPLE_TABNAME), metadata.getExperiment().getId());
        }

        if (loaderInstructionMap.containsKey(MARKER_TABNAME)) {
            querier.checkMarkerExistence(loaderInstructionMap.get(MARKER_TABNAME));
        }
        if (loaderInstructionMap.containsKey(GERMPLASM_TABNAME)) {
            querier.checkGermplasmTypeExistence(loaderInstructionMap.get(GERMPLASM_TABNAME));
            querier.checkGermplasmSpeciesExistence(loaderInstructionMap.get(GERMPLASM_TABNAME));
        }
        querier.close();
    }

    private static GobiiExtractorInstruction createQCExtractInstruction(GobiiLoaderMetadata metadata, String crop) {
        GobiiExtractorInstruction gobiiExtractorInstruction;
        Logger.logInfo("Digester", "qcCheck detected");
        Logger.logInfo("Digester", "Entering into the QC Subsection #1 of 3...");
        gobiiExtractorInstruction = new GobiiExtractorInstruction();
        gobiiExtractorInstruction.setContactEmail(metadata.getContactEmail());
        gobiiExtractorInstruction.setContactId(metadata.getContactId());
        gobiiExtractorInstruction.setGobiiCropType(crop);
        gobiiExtractorInstruction.getMapsetIds().add(metadata.getMapset().getId());
        gobiiExtractorInstruction.setQcCheck(true);
        Logger.logInfo("Digester", "Done with the QC Subsection #1 of 3!");
        return gobiiExtractorInstruction;
    }

    private static void setQCExtractPaths(GobiiLoaderMetadata metadata) {
        Logger.logInfo("Digester", "Entering into the QC Subsection #2 of 3...");
        GobiiDataSetExtract gobiiDataSetExtract = new GobiiDataSetExtract();
        gobiiDataSetExtract.setAccolate(false);  // It is unused/unsupported at the moment
        gobiiDataSetExtract.setDataSet(metadata.getDataset());
        gobiiDataSetExtract.setGobiiDatasetType(metadata.getDatasetType());

        // According to Liz, the Gobii extract filter type is always "WHOLE_DATASET" for any QC job
        gobiiDataSetExtract.setGobiiExtractFilterType(GobiiExtractFilterType.WHOLE_DATASET);
        gobiiDataSetExtract.setGobiiFileType(GobiiFileType.HAPMAP);
        // It is going to be set by the Gobii web services
        gobiiDataSetExtract.setGobiiJobStatus(null);
        qcExtractInstruction.getDataSetExtracts().add(gobiiDataSetExtract);
        Logger.logInfo("Digester", "Done with the QC Subsection #2 of 3!");
    }

    private static void sendQCExtract(ConfigSettings configuration, String crop) throws Exception {
        Logger.logInfo("Digester", "Entering into the QC Subsection #3 of 3...");
        ExtractorInstructionFilesDTO extractorInstructionFilesDTOToSend = new ExtractorInstructionFilesDTO();
        extractorInstructionFilesDTOToSend.getGobiiExtractorInstructions().add(qcExtractInstruction);
        extractorInstructionFilesDTOToSend.setInstructionFileName("extractor_" + DateUtils.makeDateIdString());
        GobiiClientContext gobiiClientContext = GobiiClientContext.getInstance(configuration, crop, GobiiAutoLoginType.USER_RUN_AS);
        if (LineUtils.isNullOrEmpty(gobiiClientContext.getUserToken())) {
            Logger.logError("Digester", "Unable to log in with user " + GobiiAutoLoginType.USER_RUN_AS.toString());
            return;
        }
        String currentCropContextRoot = GobiiClientContext.getInstance(null, false).getCurrentCropContextRoot();
        GobiiUriFactory gobiiUriFactory = new GobiiUriFactory(currentCropContextRoot, crop);
        PayloadEnvelope<ExtractorInstructionFilesDTO> payloadEnvelope = new PayloadEnvelope<>(extractorInstructionFilesDTOToSend, GobiiProcessType.CREATE);
        GobiiEnvelopeRestResource<ExtractorInstructionFilesDTO, ExtractorInstructionFilesDTO> gobiiEnvelopeRestResourceForPost = new GobiiEnvelopeRestResource<>(gobiiUriFactory
                .resourceColl(RestResourceId.GOBII_FILE_EXTRACTOR_INSTRUCTIONS));
        PayloadEnvelope<ExtractorInstructionFilesDTO> extractorInstructionFileDTOResponseEnvelope = gobiiEnvelopeRestResourceForPost.post(ExtractorInstructionFilesDTO.class,
                payloadEnvelope);

        if (extractorInstructionFileDTOResponseEnvelope != null) {

            Header header = extractorInstructionFileDTOResponseEnvelope.getHeader();
            if (header.getStatus().isSucceeded()) {
                Logger.logInfo("Digester", "Extractor Request Sent");

            } else {

                String messages = extractorInstructionFileDTOResponseEnvelope.getHeader().getStatus().messages();

                for (HeaderStatusMessage currentStatusMesage : header.getStatus().getStatusMessages()) {
                    messages += (currentStatusMesage.getMessage()) + "; ";
                }

                Logger.logError("Digester", "Error sending extract request: " + messages);

            }
        } else {
            Logger.logInfo("Digester", "Error Sending Extractor Request");
        }
        Logger.logInfo("Digester", "Done with the QC Subsection #3 of 3!");
    }

    /**
     * Read ppd and nodups files to determine their length, and add the row corresponding to the key to the digester message status.
     * Assumes IFL was run with output of dstDir on key in instructionMap.
     *
     * @param loaderInstructionMap Map of key/location of loader instructions
     * @param dstDir               Destination directory for IFL call run on key's table
     * @param key                  Key in loaderInstructionMap
     * @return
     */
    private static IFLLineCounts calculateTableStats(Map<String, File> loaderInstructionMap,
                                                     File dstDir,
                                                     String key) {

        String ppdFile = new File(dstDir, "ppd_digest." + key).getAbsolutePath();
        //If there is a deduplicated PPD file, use it instead of the ppd file
        String ddpPpdFile = new File(dstDir, "ddp_ppd_digest." + key).getAbsolutePath();
        if (new File(ddpPpdFile).exists()) {
            ppdFile = ddpPpdFile;
        }

        String noDupsFile = new File(dstDir, "nodups_ppd_digest." + key).getAbsolutePath();
        //If there is a deduplicated nodups file, use it instead of the nodups file
        String ddpNoDupsFile = new File(dstDir, "nodups_ddp_ppd_digest." + key).getAbsolutePath();
        if (new File(ddpNoDupsFile).exists()) {
            noDupsFile = ddpNoDupsFile;
        }


        //Default to 'we had an error'
        String totalLinesVal, linesLoadedVal, existingLinesVal, invalidLinesVal;

        //-1 lines for header
        int totalLines = FileSystemInterface.lineCount(loaderInstructionMap.get(key).getAbsolutePath()) - 1;
        int ppdLines = FileSystemInterface.lineCount(ppdFile) - 1;
        int noDupsLines = FileSystemInterface.lineCount(noDupsFile) - 1;
        //They're -1 if the file is missing.
        if (totalLines < 0) totalLines = 0;
        if (ppdLines < 0) ppdLines = 0;
        if (noDupsLines < 0) noDupsLines = 0;

        boolean noDupsFileExists = new File(noDupsFile).exists();
        if (!noDupsFileExists) noDupsLines = ppdLines;
        //Begin Business Logic Zone
        int loadedLines = noDupsLines;
        int existingLines = ppdLines - noDupsLines;
        int invalidLines = totalLines - ppdLines;
        //End Business Logic Zone - regular logic can resume

        //If total lines/file lines less than 0, something's wrong. Also if total lines is < changed, something's wrong.


        if (isVariableLengthTable(key)) {
            totalLinesVal = totalLines + "";
            linesLoadedVal = loadedLines + "";
            //Existing and Invalid may be absolutely random numbers in EAV JSON objects
            //Also, loaded may be waaaay above total, this is normal. So lets not report these two fields at all
            existingLinesVal = "";
            invalidLinesVal = "";

            //We can still warn people if no lines were loaded
            if (loadedLines == 0) {
                linesLoadedVal = "<b style=\"background-color:yellow\">" + loadedLines + "</b>";
            }
        } else {
            totalLinesVal = totalLines + "";
            linesLoadedVal = loadedLines + "";//Header
            existingLinesVal = existingLines + "";
            invalidLinesVal = invalidLines + "";
            if (!noDupsFileExists) {
                existingLinesVal = "";
            }
            if (invalidLines != 0) {
                invalidLinesVal = "<b style=\"background-color:red\">" + invalidLines + "</b>";
            }
            if (loadedLines == 0) {
                linesLoadedVal = "<b style=\"background-color:yellow\">" + loadedLines + "</b>";
            }
        }
        IFLLineCounts counts = new IFLLineCounts();
        counts.loadedData = loadedLines;
        counts.existingData = existingLines;
        counts.invalidData = invalidLines;
        pm.addEntry(key, totalLinesVal, linesLoadedVal, existingLinesVal, invalidLinesVal);
        return counts;
    }

    /**
     * Returns a human readable name for the job.
     *
     * @param cropName Name of the crop being run
     * @return a human readable name for the job
     */
    private static String getJobReadableIdentifier(String cropName, GobiiLoaderProcedure procedure) {
        return getJobReadableIdentifier(cropName,
            getSourceFileName(procedure.getMetadata().getGobiiFile()));
    }

    private static String getJobReadableIdentifier(String cropName, String sourceFileName) {
        cropName = cropName.charAt(0) + cropName.substring(1).toLowerCase();// MAIZE -> Maize
        String jobName = "[GOBII - Loader]: " + cropName + " - digest of \"" + sourceFileName + "\"";
        return jobName;
    }

    /**
     * Converts the File input into the FIRST of the source files.
     *
     * @param file Reference to Instruction's File object.
     * @return String representation of first of source files
     */
    public static String getSourceFileName(GobiiFile file) {
        String source = file.getSource();
        return getSourceFileName(source);
    }

    public static String getSourceFileName(String source) {
        File sourceFolder = new File(source);
        File[] f = sourceFolder.listFiles();
        if (f.length != 0) source = f[0].getName();
        else {
            source = sourceFolder.getName();//Otherwise we get full paths in source.
        }
        return source;
    }


    /**
     * Determine crop type by looking at the intruction file's location for the name of a crop.
     *
     * @param instructionFile
     * @return GobiiCropType
     */
    private static String divineCrop(String instructionFile) {
        String upper = instructionFile.toUpperCase();
        String from = "/CROPS/";
        int fromIndex = upper.indexOf(from) + from.length();
        String crop = upper.substring(fromIndex, upper.indexOf('/', fromIndex));
        return crop;
    }

    @SuppressWarnings("unused")
    private static String getJDBCConnectionString(GobiiCropConfig config) {
        return HelperFunctions.getJdbcConnectionString(config);
    }

    /**
     * Given a string key, determine if the table is one-to-one with relation to the input file size.
     * If not, several metrics become meaningless.
     *
     * @param tableKey
     * @return true if the table will have different PPD rows than input rows
     */
    private static boolean isVariableLengthTable(String tableKey) {
        return tableKey.contains("_prop");
    }

    /**
     * Deletes all files in directory that contain '.tablename' suffix
     *
     * @param directory
     * @param tableName
     */
    private static void deleteIFLFiles(File directory, String tableName, boolean onlyTemps) {
        File[] fileList = directory.listFiles();
        if (fileList == null) return;
        for (File f : fileList) {
            if (f.getName().endsWith("." + tableName)) {
                if(!onlyTemps || (!f.getName().startsWith("digest."))) {
                    rmIfExist(f);
                }
            }
        }
    }

    private static String getDefaultConfigLocation(String rootDir) {
        return Paths.get(rootDir, "config", "gobii-web.xml").toString();
    }

    private static void setConfigSettings(String configLocation) {
        try {
            configuration = new ConfigSettings(configLocation);
            Logger.logDebug(
                "Config file path",
                "Opened config settings at " + configLocation);
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new GobiiException(e1);
        }
    }

    private static String getInstructionFile(String[] args) {
        String instructionFile;
        if (args.length == 0 || "".equals(args[0])) {
            Scanner s = new Scanner(System.in);
            System.out.println("Enter Loader Instruction File Location:");
            instructionFile = s.nextLine();
            s.close();
        } else {
            instructionFile = args[0];
        }
        return instructionFile;
    }

    private static File getDestinationDir(String dstFilePath) {
        File dstDir = new File(dstFilePath);
        if (!dstDir.isDirectory()) { //Note: if dstDir is a non-existant
            dstDir = new File(dstFilePath.substring(0, dstFilePath.lastIndexOf("/")));
        }
        return dstDir;
    }

    private static GobiiCropConfig getGobiiCropConfig(ConfigSettings configuration,
                                                      String cropType) {

        GobiiCropConfig gobiiCropConfig;

        try {
            gobiiCropConfig = configuration.getCropConfig(cropType);
            Logger.logDebug(
                "Crop Config Load",
                "Crop config successfully loaded");

        } catch (Exception e) {
            logError("Digester", "Unknown loading error", e);
            throw new GobiiException(e);
        }
        if (gobiiCropConfig == null) {
            logError("Digester", "Unknown Crop Type: " + cropType + " in the Configuration File");
            throw new GobiiException(
                "Digester : Unknown Crop Type: " + cropType + " in the Configuration File");
        }
        return gobiiCropConfig;
    }

    private static String setLogger(ConfigSettings configuration,
                                    String instructionFile) {

        String logFile = null;
        String logDir = configuration.getFileSystemLog();
        if (logDir != null) {
            String instructionName = new File(instructionFile).getName();
            instructionName = instructionName.substring(0, instructionName.lastIndexOf('.'));
            logFile = logDir + "/" + instructionName + ".log";
            //String oldLogFile = Logger.getLogFilepath();
            Logger.logDebug("Error Logger", "Moving error log to " + logFile);
            //Logger.setLogFilepath(logFile);
            Logger.logDebug("Error Logger", "Moved error log to " + logFile);
            //FileSystemInterface.rmIfExist(oldLogFile);
        }
        return logFile;
    }

    /**
     * Generates a log file location given a crop name, crop type, and process ID. (Given by the process calling this method).
     * <p>
     * Currently works by placing logs in the intermediate file directory.
     *
     * @return The logfile location for this process
     */
    private static String getLogName(GobiiLoaderMetadata metadata, String cropName, String process) {
        String destination = metadata.getGobiiFile().getDestination();
        return destination + "/" + cropName + "_Process-" + process + ".log";
    }

    public static String getLogName(String destination, String cropName, String process) {
        File destinationFile = new File(destination);
        if(destinationFile.isFile()) {
            destination = destinationFile.getParent();
        }
        return destination + "/" + cropName + "_Process-" + process + ".log";
    }

}

