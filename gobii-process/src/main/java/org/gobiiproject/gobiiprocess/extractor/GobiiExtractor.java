package org.gobiiproject.gobiiprocess.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpStatus;
import org.gobiiproject.gobiiapimodel.payload.HeaderStatusMessage;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.restresources.gobii.GobiiUriFactory;
import org.gobiiproject.gobiiapimodel.types.GobiiHttpHeaderNames;
import org.gobiiproject.gobiiclient.core.common.GenericClientContext;
import org.gobiiproject.gobiiclient.core.common.HttpMethodResult;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.config.ServerConfig;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.dto.auditable.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.children.PropNameId;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.GobiiDataSetExtract;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.GobiiExtractorInstruction;
import org.gobiiproject.gobiimodel.types.GobiiAutoLoginType;
import org.gobiiproject.gobiimodel.types.GobiiExtractFilterType;
import org.gobiiproject.gobiimodel.types.GobiiFileNoticeType;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.types.GobiiFileType;
import org.gobiiproject.gobiimodel.types.GobiiSampleListType;
import org.gobiiproject.gobiimodel.types.ServerType;
import org.gobiiproject.gobiimodel.utils.FileSystemInterface;
import org.gobiiproject.gobiimodel.utils.HelperFunctions;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiimodel.utils.SimpleTimer;
import org.gobiiproject.gobiimodel.utils.email.MailInterface;
import org.gobiiproject.gobiimodel.utils.email.ProcessMessage;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiiprocess.HDF5Interface;
import org.gobiiproject.gobiiprocess.JobStatus;
import org.gobiiproject.gobiiprocess.digester.utils.ExtractSummaryWriter;
import org.gobiiproject.gobiiprocess.extractor.flapjack.FlapjackTransformer;
import org.gobiiproject.gobiiprocess.extractor.hapmap.HapmapTransformer;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;

import static org.gobiiproject.gobiimodel.types.GobiiExtractFilterType.BY_MARKER;
import static org.gobiiproject.gobiimodel.types.GobiiExtractFilterType.BY_SAMPLE;
import static org.gobiiproject.gobiimodel.utils.FileSystemInterface.*;
import static org.gobiiproject.gobiimodel.utils.HelperFunctions.checkFileExistence;
import static org.gobiiproject.gobiimodel.utils.HelperFunctions.tryExec;
import static org.gobiiproject.gobiimodel.utils.HelperFunctions.uppercaseFirstLetter;
import static org.gobiiproject.gobiimodel.utils.error.Logger.logError;

/**
 * Core class for Extraction. Contains the main method for extraction, as well as the overall workflow.
 *
 * @author jdl232
 */
@SuppressWarnings("WeakerAccess")
public class GobiiExtractor {
	private String propertiesFile;
	private boolean verbose;
	private String rootDir = "../";
	private String markerListOverrideLocation = null;

	private static final String SAMPLE_FILE_ID_POSITIONS = "43,44,45";

	private HDF5Interface hdf5Interface = new HDF5Interface();

	/**
	 * Main class of Extractor. Takes optional arguments + location of a json-based instruction file
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {

		new GobiiExtractor().run(args);
	}

	public GobiiExtractor() {

	}

	public void run(String[] args) {
		Options o = new Options()
				.addOption("v", "verbose", false, "Verbose output")
				.addOption("e", "errlog", true, "Error log override location")
				.addOption("r", "rootDir", true, "Fully qualified path to gobii root directory")
				.addOption("c", "config", true, "Fully qualified path to gobii configuration file")
				.addOption("h", "hdfFiles", true, "Fully qualified path to hdf files")
				.addOption("m", "markerList", true, "Fully qualified path to marker list files - (Debugging, forces marker list extract)");
		ExtractorGlobalConfigs.addOptions(o);

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cli = parser.parse(o, args);
			if (cli.hasOption("verbose")) verbose = true;
			if (cli.hasOption("config")) propertiesFile = cli.getOptionValue("config");
			if (cli.hasOption("rootDir")) {
				rootDir = cli.getOptionValue("rootDir");
			}
			if (cli.hasOption("hdfFiles")) hdf5Interface.setPathToHDF5Files(cli.getOptionValue("hdfFiles"));
			if (cli.hasOption("markerList")) markerListOverrideLocation = cli.getOptionValue("markerList");
			ExtractorGlobalConfigs.setFromFlags(cli);
			args = cli.getArgs();//Remaining args passed through

		} catch (org.apache.commons.cli.ParseException exp) {
			new HelpFormatter().printHelp("java -jar Extractor.jar ", "Also accepts input file directly after arguments\n"
					+ "Example: java -jar Extractor.jar -c /home/jdl232/customConfig.properties -v /home/jdl232/testLoad.json", o, null, true);

			System.exit(2);
		}

		String extractorScriptPath = rootDir + "extractors/";
		hdf5Interface.setPathToHDF5(extractorScriptPath + "hdf5/bin/");

		if (propertiesFile == null) propertiesFile = rootDir + "config/gobii-web.xml";

		boolean success = true;
		ConfigSettings configuration = null;
		try {
			configuration = new ConfigSettings(propertiesFile);
			Logger.logDebug("Config file path", "Opened config settings at " + propertiesFile);
		} catch (Exception e) {
			logError("Extractor", "Failure to read Configurations", e);
			return;
		}

		ProcessMessage pm;

		MailInterface mailInterface = new MailInterface(configuration);
		String instructionFile;
		if (args.length == 0 || args[0].equals("")) {
			Scanner s = new Scanner(System.in);
			System.out.println("Enter Extractor Instruction File Location:");
			instructionFile = s.nextLine();
			if (instructionFile.equals("")) instructionFile = "scripts//jdl232_01_pretty.json";
			s.close();
		} else {
			instructionFile = args[0];
		}

		Logger.logInfo("Extractor", "Beginning extract of " + instructionFile);
		SimpleTimer.start("Extract");

		List<GobiiExtractorInstruction> list = parseExtractorInstructionFile(instructionFile);
		if (list == null || list.isEmpty()) {
			Logger.logError("Extractor", "No instruction for file " + instructionFile);
			return;
		}

		String logDir = configuration.getFileSystemLog();
		String logFile = null;
		if (logDir != null) {
			String instructionName = new File(instructionFile).getName();
			instructionName = instructionName.substring(0, instructionName.lastIndexOf('.'));
			logFile = logDir + "/" + instructionName + ".log";
			String oldLogFile = Logger.getLogFilepath();
			Logger.logDebug("Error Logger", "Moving error log to " + logFile);
			Logger.setLogFilepath(logFile);
			Logger.logDebug("Error Logger", "Moved error log to " + logFile);
			FileSystemInterface.rmIfExist(oldLogFile);
		} else {
			Logger.logError("Extractor", "log directory is not defined in config file");
			return;
		}

		GobiiExtractorInstruction firstExtractInstruction=list.get(0);
		String firstCrop = firstExtractInstruction.getGobiiCropType();
		String firstContactEmail = firstExtractInstruction.getContactEmail();
		if (firstCrop == null) firstCrop = divineCrop(instructionFile);
		JobStatus jobStatus = null;

		//Job Id is the 'name' part of the job file  /asd/de/name.json
		String filename = new File(instructionFile).getName();
		String jobFileName = filename.substring(0, filename.lastIndexOf('.'));

		for (GobiiExtractorInstruction inst : list) {
			String crop = inst.getGobiiCropType();
			String extractType = "";
			if (crop == null) crop = divineCrop(instructionFile);
			SpringContextLoaderSingleton.init(crop, configuration);
			try {
				jobStatus = new JobStatus(jobFileName);
			} catch (Exception e) {
				Logger.logError("GobiiFileReader", "Error Checking Status", e);
			}
			jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_INPROGRESS.getCvName(), "Beginning Extract");
			try {
				Path cropPath = Paths.get(rootDir + "crops/" + crop.toLowerCase());
				if (!(Files.exists(cropPath) &&
						Files.isDirectory(cropPath))) {
					Logger.logError("Extractor", "Unknown Crop Type: " + crop);
					return;
				}
				GobiiCropConfig gobiiCropConfig;
				try {
					gobiiCropConfig = configuration.getCropConfig(crop);
				} catch (Exception e) {
					logError("Extractor", "Unknown exception getting crop", e);
					return;
				}
				if (gobiiCropConfig == null) {
					logError("Extractor", "Unknown Crop Type: " + crop + " in the Configuration File");
					return;
				}
				if (hdf5Interface.getPathToHDF5Files() == null)
					hdf5Interface.setPathToHDF5Files(cropPath.toString() + "/hdf5/");


				Integer mapId;
				List<Integer> mapIds = inst.getMapsetIds();
				if (mapIds.isEmpty() || mapIds.get(0) == null) {
					mapId = null;
				} else if (mapIds.size() > 1) {
					logError("Extraction Instruction", "Too many map IDs for extractor. Expected one, recieved " + mapIds.size());
					mapId = null;
				} else {
					mapId = mapIds.get(0);
				}
				jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_METADATAEXTRACT.getCvName(), "Extracting Metadata");
				for (GobiiDataSetExtract extract : inst.getDataSetExtracts()) {
					String jobReadableIdentifier = getJobReadableIdentifier(crop, extract);
					String jobUser = inst.getContactEmail();
					pm = new ProcessMessage();
					pm.setUser(jobUser);

					GobiiExtractFilterType filterType = extract.getGobiiExtractFilterType();
					if (filterType == null) filterType = GobiiExtractFilterType.WHOLE_DATASET;
					if (markerListOverrideLocation != null) filterType = BY_MARKER;
					String extractDir = extract.getExtractDestinationDirectory() + "/";
					tryExec("rm -f " + extractDir + "*");

					String markerFile = extractDir + "marker.file";
					String extendedMarkerFile = markerFile + ".ext";
					String mapsetFile = extractDir + "mapset.file";
					String markerPosFile = markerFile + ".pos";
					String sampleFile = extractDir + "sample.file";
					String projectFile = extractDir + "project.file";
					String extractSummaryFile = extractDir + "summary.file";
					if (inst.isQcCheck()) {//FIXES ERROR - KDC EXPECTS PROJECT SUMMARY IN SUMMARY.FILE
						projectFile = extractDir + "summary.file"; //HACK, NEED FIX AND REMOVE LATER FOR CONSISTENCY
						extractSummaryFile = extractDir + "project_summary.file";
					}
					String chrLengthFile = markerFile + ".chr";
					Path mdePath = FileSystems.getDefault().getPath(extractorScriptPath + "postgres/gobii_mde/gobii_mde.py");
					if (!mdePath.toFile().isFile()) {
						Logger.logDebug("Extractor", mdePath + " does not exist!");
						return;
					}

					String gobiiMDE;//Output of switch

					GobiiFileType fileType = extract.getGobiiFileType();

					String confidentialityMessage;
					String confidentialityLoc = configuration.getFileNoticePath(crop, GobiiFileNoticeType.CONFIDENTIALITY);
					File confidentialityFile = new File(confidentialityLoc);
					if (confidentialityFile.exists()) {
						StringBuilder sb = new StringBuilder();
						for (String line : Files.readAllLines(Paths.get(confidentialityFile.toURI()))) {
							sb.append(line).append(" ");
						}
						confidentialityMessage = sb.toString().trim();
						pm.addConfidentialityMessage(confidentialityMessage);
					}

					//Common terms
					String platformTerm, mapIdTerm, markerListTerm, sampleListTerm, verboseTerm;
					String samplePosFile;//Location of sample position indices (see markerList for an example
					platformTerm = mapIdTerm = markerListTerm = sampleListTerm = verboseTerm = "";
					List<Integer> platforms = extract
							.getPlatforms()
							.stream()
							.map(PropNameId::getId)
							.collect(Collectors.toList());
					if (platforms != null && !platforms.isEmpty()) {
						platformTerm = " --platformList " + commaFormat(platforms);
					}
					if (mapId != null) {
						mapIdTerm = " -D " + mapId;
					}
					if (verbose) {
						verboseTerm = " -v";
					}

					//Dataset can be null
					Integer datasetId = null;
					String datasetName = "null";
					PropNameId datasetPropNameId = extract.getDataSet();
					if (datasetPropNameId != null) {
						datasetId = datasetPropNameId.getId();
						datasetName = datasetPropNameId.getName();
					}

					String markerListFileLocation = null, sampleListFileLocation = null;//Generally list file location, populated as needed

					String errorFile = getLogName(extract, gobiiCropConfig, datasetId);					switch (filterType) {
						case WHOLE_DATASET:
							extractType = "Extract by Dataset";
							gobiiMDE = "python " + mdePath +
									" -c " + HelperFunctions.getPostgresConnectionString(gobiiCropConfig) +
									" --extractByDataset" +
									" -m " + markerFile +
									" -b " + mapsetFile +
									" -s " + sampleFile +
									" -p " + projectFile +
									(mapId == null ? "" : (" -D " + mapId)) +
									" -d " + datasetId +
									" -l" +
									verboseTerm + " ";


							break;
						case BY_MARKER:

							extractType = "Extract by Marker";
							//List takes extra work, as it might be a <List> or a <File>
							//Create a file out of the List if non-null, else use the <File>
							List<String> markerList = extract.getMarkerList();
							if (markerList != null && !markerList.isEmpty()) {
								markerListFileLocation = createTempListFile(extractDir, markerList);
							} else if (extract.getListFileName() != null) {
								markerListFileLocation = extract.getListFileName();
							}
							if (markerListFileLocation != null) {
								markerListTerm = " -X " + markerListFileLocation;

								makeFileUnique(markerListFileLocation,errorFile);
							}
							//else if file is null and list is empty or null - > no term

							if (markerListOverrideLocation != null)
								markerListTerm = " -x " + markerListOverrideLocation;

							String markerGroupTerm = "";
							if (extract.getMarkerGroups() != null && !extract.getMarkerGroups().isEmpty()) {
								markerGroupTerm = " -G " + commaFormat(toIdList(extract.getMarkerGroups()));
							}


							//Actually call the thing
							gobiiMDE = "python " + mdePath +
									" -c " + HelperFunctions.getPostgresConnectionString(gobiiCropConfig) +
									" --extractByMarkers" +
									" -m " + markerFile +
									" -b " + mapsetFile +
									" -s " + sampleFile +
									" -p " + projectFile +
									markerGroupTerm +
									markerListTerm +
									" --datasetType " + extract.getGobiiDatasetType().getId() +
									mapIdTerm +
									platformTerm +
									" -l" +
									verboseTerm + " ";

							break;
						case BY_SAMPLE:
							extractType = "Extract by Sample";
							//List takes extra work, as it might be a <List> or a <File>
							//Create a file out of the List if non-null, else use the <File>
							List<String> sampleList = extract.getSampleList();
							if (sampleList != null && !sampleList.isEmpty()) {
								sampleListFileLocation = createTempListFile(extractDir, sampleList, "sampleList");
							} else if (extract.getListFileName() != null) {
								sampleListFileLocation = extract.getListFileName();
							}
							if (sampleListFileLocation != null) {

								makeFileUnique(sampleListFileLocation,errorFile);
								sampleListTerm = " -Y " + sampleListFileLocation;
							}

							GobiiSampleListType type = extract.getGobiiSampleListType();
							String sampleListTypeTerm = (type == null) ? "" : " --sampleType " + getNumericType(type);

							String PITerm, projectTerm;
							PITerm = projectTerm = "";
							PropNameId PI = extract.getPrincipleInvestigator();
							PropNameId project = extract.getProject();
							if (PI != null && PI.getId() != null) {
								PITerm = " --piId " + PI.getId();
							}
							if (project != null && project.getId() != null) {
								projectTerm = " --projectId " + project.getId();
							}

							gobiiMDE = "python " + mdePath +
									" -c " + HelperFunctions.getPostgresConnectionString(gobiiCropConfig) +
									" --extractBySamples" +
									" -m " + markerFile +
									" -b " + mapsetFile +
									" -s " + sampleFile +
									" -p " + projectFile +
									sampleListTerm +
									sampleListTypeTerm +
									PITerm +
									projectTerm +
									" --datasetType " + extract.getGobiiDatasetType().getId() +
									mapIdTerm +
									platformTerm +
									" -l" +
									verboseTerm + " ";

							break;
						default:
							gobiiMDE = "";
							Logger.logError("GobiiExtractor", "UnknownFilterType " + filterType);
							break;
					}

					if(verbose){
						Logger.logDebug("MDE",gobiiMDE.replace(gobiiCropConfig.getServer(ServerType.GOBII_PGSQL).getPassword(),"<******************>"));
					}

					samplePosFile = sampleFile + ".pos";

					Logger.logInfo("Extractor", "Executing MDEs");

					if(verbose) {
						tryExec(gobiiMDE,extractDir + "mdeOut", errorFile,extractDir+"MDEStdOut");
					}
					else {
						tryExec(gobiiMDE, extractDir + "mdeOut", errorFile);
					}
					//Clean some variables ahead of declaration
					final String defaultMapName = "No Mapset info available";
					String mapName = defaultMapName;
					String postgresName = (mapId == null) ? null : getMapNameFromId(mapId, configuration); //Get name from postgres
					if (postgresName != null) mapName = postgresName;
					GobiiSampleListType type = extract.getGobiiSampleListType();

					String formatName = uppercaseFirstLetter(extract.getGobiiFileType().toString().toLowerCase());

					ExtractSummaryWriter esw = new ExtractSummaryWriter();

					pm.addCriteria("Crop", inst.getGobiiCropType());
					pm.addCriteria("Email", inst.getContactEmail());
					pm.addCriteria("Job ID", jobFileName);
					esw.addItem("Job ID", jobFileName);
					esw.addItem("Submit as", inst.getContactEmail());
					esw.addItem("Format", formatName);
					if (!mapName.equals(defaultMapName)) {
						esw.addItem("Mapset", mapName);
					}
					pm.addCriteria("Principal Investigator", extract.getPrincipleInvestigator());

					pm.addCriteria("Project", extract.getProject());
					pm.addCriteria("Dataset", extract.getDataSet());
					pm.addCriteria("Dataset Type", extract.getGobiiDatasetType());
					esw.addItem("Data Set", extract.getDataSet());
					esw.addItem("Dataset Type", extract.getGobiiDatasetType());
					esw.addPropList("Platform", extract.getPlatforms());
					if (type != null) {
						esw.addItem("List Type", uppercaseFirstLetter(type.toString().toLowerCase()));
					}

					pm.addCriteria("Mapset", mapName);
					pm.addCriteria("Format", formatName);
					pm.addCriteria("Platforms", getPlatformNames(extract.getPlatforms()));

					esw.addItem("Principal Investigator", extract.getPrincipleInvestigator());
					esw.addItem("Project", extract.getProject());

					//turns /data/gobii_bundle/crops/zoan/extractor/instructions/2018_05_15_13_32_12_samples.txt into 2018_05_15_13_32_12_samples.txt
					//We're moving it into the extract directory when we're done now, so lets be vague as to its location.
					//They'll find it if they want to


					//Marker List or List File (see above for selection logic)
					if ((extract.getMarkerList() != null && !extract.getMarkerList().isEmpty()) || (filterType == BY_MARKER && markerListFileLocation != null)) {
						pm.addCriteria("Marker List", markerListFileLocation);
						esw.addItem("Marker List", markerListFileLocation);

					}

					//Marker groups (spelled out, unlike marker and sample files)
					List<PropNameId> mGroup = extract.getMarkerGroups();
					if((mGroup!= null && !mGroup.isEmpty())){
						List<String> markerGroupNames = mGroup.stream().map(PropNameId::getName).collect(Collectors.toList());
						pm.addCriteria("Marker Group", String.join(", ",markerGroupNames));
						esw.addPropList("Marker Group", mGroup);
					}

					if (type != null) {
						pm.addCriteria("Sample List Type", uppercaseFirstLetter(type.toString().toLowerCase()));
					}

					if ((extract.getSampleList() != null && !extract.getSampleList().isEmpty()) || (filterType == BY_SAMPLE && sampleListFileLocation != null)) {
						pm.addCriteria("Sample List", sampleListFileLocation);
						esw.addItem("Sample File", sampleListFileLocation);
					}

					List<Integer> mapsetIds = inst.getMapsetIds();
					//If the only mapset in the list is the mapset displayed above, lets not display it twice...
					boolean mapsetIsAlreadyDisplayed = (mapsetIds.size() == 1) && mapsetIds.contains(mapId);

					if (inst.getMapsetIds() != null && !inst.getMapsetIds().isEmpty() && !mapsetIsAlreadyDisplayed) {
						pm.addCriteria("Mapset List", String.join("<BR>", inst.getMapsetIds().toString())); //This should never happen
					}

					//Note - link to place where it *will* be if there are no errors. Sadly, we won't know if it's right until we've sent all the emails already
					String finalIFPath= configuration.getProcessingPath(firstCrop, GobiiFileProcessDir.EXTRACTOR_DONE) + FilenameUtils.getName(instructionFile);
					pm.addPath("Instruction File", finalIFPath , true, configuration, false);
					pm.addFolderPath("Output Directory", extractDir, configuration);
					pm.addPath("Error Log", logFile, true, configuration, false);
					pm.addPath("Summary File", new File(projectFile).getAbsolutePath(), configuration, false);
					pm.addPath("Sample File", new File(sampleFile).getAbsolutePath(), configuration, false);
					pm.addPath("Marker File", new File(markerFile).getAbsolutePath(), configuration, false);
					if (checkFileExistence(mapsetFile)) {
						pm.addPath("Mapset File", new File(mapsetFile).getAbsolutePath(), configuration,false);
					}

					esw.writeToFile(new File(extractSummaryFile));

					//HDF5
					//noinspection UnnecessaryLocalVariable - Used to clarify use
					String tempFolder = extractDir;
					String genoFile = null;
					if (!extract.getGobiiFileType().equals(GobiiFileType.META_DATA)) {
						jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_FINALASSEMBLY.getCvName(), "Assembling Output Matrix");
						boolean markerFast = (fileType == GobiiFileType.HAPMAP);
						try {
							switch (filterType) {
								case WHOLE_DATASET:
									genoFile = hdf5Interface.getHDF5Genotype(markerFast, errorFile, datasetId, tempFolder);
									break;
								case BY_MARKER:
									genoFile = hdf5Interface.getHDF5GenoFromMarkerList(markerFast, errorFile, tempFolder, markerPosFile);
									break;
								case BY_SAMPLE:
									genoFile = hdf5Interface.getHDF5GenoFromSampleList(markerFast, errorFile, tempFolder, markerPosFile, samplePosFile);
									break;
								default:
									genoFile = null;
									Logger.logError("GobiiExtractor", "UnknownFilterType " + filterType);
									break;
							}
						} catch (FileNotFoundException e) {
							Logger.logError("GobiiExtractor", "Unable to load HDF5 files", e);
						}


						//TODO - GSD-533 Dataset **name** is ssr_allele_size? WTF does this do and why is it here.
						// Adding "/" back to the bi-allelic data made from HDF5
						if (datasetName != null) {
							if (datasetName.toLowerCase().equals("ssr_allele_size")) {
								Logger.logInfo("Extractor", "Adding slashes to bi allelic data in " + genoFile);
								if (addSlashesToBiAllelicData(genoFile, extractDir, extract)) {
									Logger.logInfo("Extractor", "Added slashes to all the bi-allelic data in " + genoFile);
								} else {
									Logger.logError("Extractor", "Not added slashes to all the bi-allelic data in " + genoFile);
								}
							}
						}
					}


					jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_FINALASSEMBLY.getCvName(), "Assembling Output Files");
					if (checkFileExistence(genoFile) || (fileType == GobiiFileType.META_DATA)) {
						switch (fileType) {
							case FLAPJACK:
								String genoOutFile = extractDir + "Dataset.genotype";
								String mapOutFile = extractDir + "Dataset.map";

								//Always regenerate requests - may have different parameters
								boolean extended = HelperFunctions.checkFileExistence(extendedMarkerFile);
								success &= FlapjackTransformer.generateMapFile(extended ? extendedMarkerFile : markerFile, sampleFile, chrLengthFile, tempFolder, mapOutFile, errorFile, extended);
								if (success) {
									pm.addEntity("Map File", FileSystemInterface.lineCount(mapOutFile) + "");
								}
								Logger.logDebug("GobiiExtractor", "Executing FlapJack Genotype file Generation");
								success &= FlapjackTransformer.generateGenotypeFile(markerFile, sampleFile, genoFile, tempFolder, genoOutFile, errorFile);
								pm.addPath("FlapJack Genotype file", new File(genoOutFile).getAbsolutePath(), configuration, true);
								pm.addPath("FlapJack Map file", new File(mapOutFile).getAbsolutePath(), configuration, true);
								getCounts(success, pm, markerFile, sampleFile);
								jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_COMPLETED.getCvName(), "Extract Completed 8uccessfully");
								break;
							case HAPMAP:
								String hapmapOutFile = extractDir + "Dataset.hmp.txt";
								HapmapTransformer hapmapTransformer = new HapmapTransformer();
								Logger.logDebug("GobiiExtractor", "Executing Hapmap Generation");

								String sampleFileWithoutIds = sampleFile+".tmp";
								String locations=SAMPLE_FILE_ID_POSITIONS;
								tryExec("cut --complement -f"+locations+" "+sampleFile,sampleFileWithoutIds, errorFile);//Marker Name, Linkage Group Name, Marker Linkage Group Start

								success &= hapmapTransformer.generateFile(markerFile, sampleFileWithoutIds, extendedMarkerFile, genoFile, hapmapOutFile, errorFile);
								pm.addPath("Hapmap file", new File(hapmapOutFile).getAbsolutePath(), configuration, true);
								getCounts(success, pm, markerFile, sampleFile);
								jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_COMPLETED.getCvName(), "Extract Completed 8uccessfully");

								rmIfExist(sampleFileWithoutIds);

								break;
							case META_DATA:
								jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_COMPLETED.getCvName(), "Successful Data Extract");
								break;
							default:
								Logger.logError("Extractor", "Unknown Extract Type " + extract.getGobiiFileType());
								jobStatus.setError("Unsuccessful Data Extract");
						}
						if (pm.getBody() == null) {
							pm.setBody(jobReadableIdentifier, extractType, SimpleTimer.stop("Extract"), Logger.getFirstErrorReason(), Logger.success(), Logger.getAllErrorStringsHTML());
						}
					} else { //We had no genotype file, so we aborted
						Logger.logError("GobiiExtractor", "No genetic data extracted. Extract failed.");
						pm.setBody(jobReadableIdentifier, extractType, SimpleTimer.stop("Extract"), Logger.getFirstErrorReason(), Logger.success(), Logger.getAllErrorStringsHTML());
						jobStatus.setError("Unsuccessful Data Extract");
					}
					boolean overallSuccess = Logger.success(); //quick and dirty way to make sure errors past the 'end' of processing don't affect output
					//Clean Temporary Files
					rmIfExist(genoFile);
					rmIfExist(chrLengthFile);
					rmIfExist(markerPosFile);
					rmIfExist(extendedMarkerFile);
					rmIfExist(samplePosFile);
					rmIfExist(extractDir + "mdeOut");//remove mde output file
					rmIfExist(extractDir + "position.file");
					rmIfExist(extractDir + "position.list");

					if (extract.getListFileName() != null) {
						File listFile = new File(extract.getListFileName());
						if (listFile.exists()) {
							mvToFolder(extract.getListFileName(), extractDir,false); //Move the list file to the extract directory
						}
					}
					Logger.logDebug("Extractor", "DataSet " + datasetName + " Created");

					/*Perform QC if the instruction is QC-based AND we are a successful extract*/
					if (inst.isQcCheck()) {
						if (overallSuccess) {//QC - Subsection #1 of 1
							Logger.logInfo("Extractor", "qcCheck detected");
							Logger.logInfo("Extractor", "Entering into the QC Subsection #1 of 1...");
							jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_QCPROCESSING.getCvName(), "Processing QC Job");
							performQC(configuration, inst, crop, datasetName, datasetId, extractDir, mailInterface, extractType);
							jobStatus.set(JobProgressStatusType.CV_PROGRESSSTATUS_COMPLETED.getCvName(), "QC Job Complete");
						}
					}
					if (pm.getBody() == null) { //Make sure the PM body is set before we send it
						pm.setBody(jobReadableIdentifier, extractType, SimpleTimer.stop("Extract"), Logger.getFirstErrorReason(), Logger.success(), Logger.getAllErrorStringsHTML());
					}
					if (!inst.isQcCheck()) mailInterface.send(pm);//If it is QC - QC should send any success or failure emails.
				}

			} catch (Exception e) {
				handleCriticalException(configuration, jobStatus, inst.getContactEmail(), e);
			}
		}
		try {
			String instructionFilePath = HelperFunctions.completeInstruction(instructionFile, configuration.getProcessingPath(firstCrop, GobiiFileProcessDir.EXTRACTOR_DONE));
		}
		catch(Exception e){
			handleCriticalException(configuration, jobStatus, firstContactEmail, e);
		}
	}

	/**
	 * Does three things - writes a final log message, sends an emergency 'help I've fallen and can't get up' email message,
	 * and updates the status of the job.
	 * Email message is sent as a last resort to an exception that's uncaught.
	 * Method attempts to use the *absolute minimum* of new objects and callouts - it could be an OOM or Stack exception we're responding to.
	 *
	 * THIS MESSAGE SHOULD NEVER BE SEEN IN NORMAL OPERATION.
	 *
	 * @param configuration ConfigSettings object - a lot of parameters come from here
	 * @param jobStatus - jobstatus object used to finalize the status of the job
	 * @param contactEmail place for email to be sent
	 * @param e exception to pull a stack trace from
	 */
	private void handleCriticalException(ConfigSettings configuration, JobStatus jobStatus, String contactEmail, Exception e) {
		Logger.logError("GobiiExtractor", "Uncaught fatal error found in program.", e);
		HelperFunctions.sendEmail("Hi.\n\n" +

				"I'm sorry, but your extract failed for reasons beyond your control.\n" +
				"I'm going to dump a message of the error here.\n\n\n" +
				org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e), null, false, null, configuration, contactEmail);
		jobStatus.setError(e.getMessage());
	}

	private static String getPlatformNames(List<PropNameId> platforms) {
		StringBuilder names = new StringBuilder();
		for (PropNameId platform : platforms) {
			String tmpName = platform.getName();
			names.append(tmpName).append("<BR>");
		}
		return names.toString();
	}

	/**
	 * Convert a list of gobiiFilePropNameIds to a list of IDs.
	 *
	 * @return list of IDs
	 */
	private List toIdList(List<PropNameId> propertyList) {
		return subPropertyList(propertyList, PropNameId::getId);
	}

	/**
	 * Converts a list of gobiiFilePropNameIds to something else
	 *
	 * @param propertyList properties
	 * @param func         what to do
	 * @return something? usually.
	 */
	private List subPropertyList(List<PropNameId> propertyList, Function<PropNameId, Object> func) {
		return propertyList.stream().map(func).collect(Collectors.toList());
	}

	/***
	 * Get marker and sample count for Email notification Table
	 * @param success if existing process is 'successful'
	 * @param pm Process Message to append to
	 * @param markerFile Marker file location
	 * @param sampleFile Sample file location
	 */
	private void getCounts(boolean success, ProcessMessage pm, String markerFile, String sampleFile) {
		if (success) {
			pm.addEntity("Marker", (FileSystemInterface.lineCount(markerFile) - 1) + "");
			pm.addEntity("Sample", (FileSystemInterface.lineCount(sampleFile) - 1) + "");
		}
	}

	/**
	 * Extractor QC subsection 1
	 *
	 * @param configSettings configuration object for system
	 * @param inst          instruction being processed
	 * @param crop          name of crop being processed (unused
	 * @param datasetName    Name of the dataset corresponding to the dataset ID.
	 * @param datasetId     ID of the dataset being used
	 * @param extractDir    directory of the extract to be called on the load
	 * @param mailInterface the email interface object
	 * @param extractType   type of extract being performed
	 * @throws Exception when an exception has occurred (all of them)
	 */
	private void performQC(ConfigSettings configSettings, GobiiExtractorInstruction inst, String crop, String datasetName, Integer datasetId, String extractDir, MailInterface mailInterface, String extractType) throws Exception {
		if (configSettings.getGlobalServer(ServerType.KDC).getHost() == null) {
			Logger.logInfo("QC", "Unable to continue QC with the KDC host name being null");
			return;
		} else {
			if (configSettings.getGlobalServer(ServerType.KDC).getHost().equals("")) {
				Logger.logInfo("QC", "Unable to continue QC with the KDC host name being empty");
				return;
			}
		}
		if (configSettings.getGlobalServer(ServerType.KDC).getContextPath() == null) {
			Logger.logInfo("QC", "Unable to continue QC with the KDC context path being null");
			return;
		} else {
			if (configSettings.getGlobalServer(ServerType.KDC).getContextPath().equals("")) {
				Logger.logInfo("QC", "Unable to continue QC with the KDC context path being empty");
				return;
			}
		}
		if (!configSettings.getGlobalServer(ServerType.KDC).isActive()) {
			Logger.logInfo("QC", "Unable to continue QC with the KDC server inactive");
			return;
		}
		Logger.logInfo("QC", "KDC Host: " + configSettings.getGlobalServer(ServerType.KDC).getHost());
		Logger.logInfo("QC", "KDC Context Path: " + configSettings.getGlobalServer(ServerType.KDC).getContextPath());
		Logger.logInfo("QC", "KDC Port: " + configSettings.getGlobalServer(ServerType.KDC).getPort());
		Logger.logInfo("QC", "KDC Active: " + configSettings.getGlobalServer(ServerType.KDC).isActive());


		//TODO: Instead of new ServerConfig . . .
		// just do this:         GenericClientContext genericClientContext = new GenericClientContext(configSettings.getGlobalServer(ServerType.KDC));
		ServerConfig serverConfig = new ServerConfig(ServerType.GENERIC,
				configSettings.getGlobalServer(ServerType.KDC).getHost(),
				configSettings.getGlobalServer(ServerType.KDC).getContextPath(),
				configSettings.getGlobalServer(ServerType.KDC).getPort(),
				configSettings.getGlobalServer(ServerType.KDC).isActive(),
				configSettings.getGlobalServer(ServerType.KDC).isDecrypt());
		GenericClientContext genericClientContext = new GenericClientContext(serverConfig);
		RestUri restUriGetQCJobID = new RestUri("/",
				configSettings.getGlobalServer(ServerType.KDC).getContextPath(),
				configSettings.getGlobalServer(ServerType.KDC).getCallResourcePath(RestResourceId.KDC_START));
		restUriGetQCJobID
				.addQueryParam("datasetId", String.valueOf(datasetId))
				.addQueryParam("directory", extractDir)
				.addQueryParam("forcerestart", "true");
		HttpMethodResult httpMethodResult = genericClientContext
				.get(restUriGetQCJobID);
		if (httpMethodResult.getResponseCode() != HttpStatus.SC_OK) {
			Logger.logInfo("QC", "The qcStart method failed: "
					+ httpMethodResult.getUri().toString()
					+ "; failure mode: "
					+ Integer.toString(httpMethodResult.getResponseCode())
					+ " ("
					+ httpMethodResult.getReasonPhrase()
					+ ")");
		} else {
			JsonObject jsonPayload = httpMethodResult.getJsonPayload();
			if (jsonPayload == null) {
				Logger.logInfo("QC", "Null JSON payload");
			} else {
				if (jsonPayload.get("jobId").toString().equals("")) {
					Logger.logInfo("QC", "Empty JSON payload");
				} else {
					Long qcJobID = jsonPayload.get("jobId").getAsLong();
					Logger.logInfo("QC", "New QC job id: " + qcJobID);
					ProcessMessage qcStartPm = new ProcessMessage();
					qcStartPm.setUser(inst.getContactEmail());
					qcStartPm.setSubject("new QC Job #" + qcJobID);
					qcStartPm.addIdentifier("QC Job Identifier", "", String.valueOf(qcJobID));
					qcStartPm.addIdentifier("Dataset Identifier", datasetName, String.valueOf(datasetId));
					qcStartPm.addPath("Output Extraction/QC Directory", extractDir, configSettings, false);
					qcStartPm.setBody("new QC Job #" + qcJobID, "QC", 0, "", true, "");
					//mailInterface.send(qcStartPm);
					RestUri restUriGetQCJobStatus = new RestUri("/",
							configSettings.getGlobalServer(ServerType.KDC).getContextPath(),
							configSettings.getGlobalServer(ServerType.KDC).getCallResourcePath(RestResourceId.KDC_STATUS));
					restUriGetQCJobStatus
							.addQueryParam("jobid")
							.setParamValue("jobid", String.valueOf(qcJobID));
					jsonPayload = null;
					String status = null;
					long maxStatusCheckMillis = configSettings.getGlobalServer(ServerType.KDC).getMaxStatusCheckMins() * 60 * 1000;
					SimpleTimer.start("QC");
					do {
						long qcProcessTimeMillis = System.currentTimeMillis() - SimpleTimer.time("QC");
						if (maxStatusCheckMillis < qcProcessTimeMillis) {
							break;
						}
						try {
							Thread.sleep(configSettings.getGlobalServer(ServerType.KDC).getStatusCheckIntervalSecs() * 1000);
						} catch (InterruptedException interruptedException) {
							Thread.currentThread().interrupt();
							Logger.logError("QC", "qcStatus: " + interruptedException.getMessage());
						}
						httpMethodResult = genericClientContext
								.get(restUriGetQCJobStatus);
						if (httpMethodResult.getResponseCode() != HttpStatus.SC_OK) {
							Logger.logInfo("QC", "The qcStatus method failed: "
									+ httpMethodResult.getUri().toString()
									+ "; failure mode: "
									+ Integer.toString(httpMethodResult.getResponseCode())
									+ " ("
									+ httpMethodResult.getReasonPhrase()
									+ ")");
							break;
						}
						jsonPayload = httpMethodResult.getJsonPayload();
						status = jsonPayload.get("status").getAsString();
					} while ((status.equals("NEW")) || (status.equals("RUNNING")));


					ProcessMessage qcStatusPm = new ProcessMessage();
					qcStatusPm.setUser(inst.getContactEmail());
					qcStatusPm.setSubject(new StringBuilder("QC Job #").append(qcJobID).append(" status").toString());
					qcStatusPm.addIdentifier("QC Job Identifier", "", String.valueOf(qcJobID));
					qcStatusPm.addIdentifier("Dataset Identifier", datasetName, String.valueOf(datasetId));

					int qcDuration = 0;
					if (jsonPayload == null) {
						Logger.logInfo("QC", "Null JSON payload");
					} else {
						int start = jsonPayload.get("start").getAsInt();
						int end = jsonPayload.get("end").getAsInt();
						qcDuration = end - start;
					}

					if ((status.equals("COMPLETED")) || (status.equals("FAILED"))) {
						// If the extract directory does not exist or is not writable, it always makes the last qcDownload method crashing and
						// thus this class crashing
						if (new File(extractDir).exists()) {
							if (new File(extractDir).canWrite()) {
								JsonObject resultsUrls = jsonPayload.get("resultsUrls").getAsJsonObject();
								Set<Map.Entry<String, JsonElement>> entrySet = resultsUrls.entrySet();
								for (Map.Entry<String, JsonElement> entry : entrySet) {
									String key = entry.getKey();
									// Avoiding any downloadable non-data file susceptible to be shown for 	the gobii user
									if (!key.equals("script.groovy")) {
										String fileDownloadLink = entry.getValue().getAsString().substring(1);
										Logger.logInfo("QC", new StringBuilder("fileDownloadLink: ").append(fileDownloadLink).toString());
										String destinationFqpn = Paths.get(extractDir, key).toString();
										Logger.logInfo("QC", new StringBuilder("destinationFqpn: ").append(destinationFqpn).toString());
										RestUri restUriGetQCDownload = new RestUri("/",
												configSettings.getGlobalServer(ServerType.KDC).getContextPath(),
												fileDownloadLink)
												.withHttpHeader(GobiiHttpHeaderNames.HEADER_NAME_CONTENT_TYPE,
														MediaType.APPLICATION_OCTET_STREAM)
												.withHttpHeader(GobiiHttpHeaderNames.HEADER_NAME_ACCEPT,
														MediaType.APPLICATION_OCTET_STREAM)
												.withDestinationFqpn(destinationFqpn);
										httpMethodResult = genericClientContext.get(restUriGetQCDownload);
										if (httpMethodResult.getResponseCode() != HttpStatus.SC_OK) {
											Logger.logInfo("QC", "The qcDownload method failed: "
													+ httpMethodResult.getUri().toString()
													+ "; failure mode: "
													+ Integer.toString(httpMethodResult.getResponseCode())
													+ " ("
													+ httpMethodResult.getReasonPhrase()
													+ ")");
										} else {
											Logger.logInfo("QC", "The qcDownload http method was successful with "
													+ httpMethodResult.getFileName());
											if (httpMethodResult.getFileName() != null) {
												qcStatusPm.addPath(key, httpMethodResult.getFileName(), configSettings, false);

												if (key.equals("stdout.txt") || key.equals("stderr.txt")) {

													qcStatusPm.getFileAttachments().add(httpMethodResult.getFileName());
												}

											}
										}
									}
								}
							}
						}
						int qcDurationInSeconds = qcDuration * 60;
						if (status.equals("COMPLETED")) {
							Logger.logInfo("QC", new StringBuilder("The QC job #").append(qcJobID).append(" has completed").toString());
							qcStatusPm.setBody(new StringBuilder("[GOBII - QC]: job #").append(qcJobID).toString(), extractType, qcDurationInSeconds, Logger.getFirstErrorReason(), true, Logger.getAllErrorStringsHTML());
						} else if (status.equals("FAILED")) {
							Logger.logError("QC", new StringBuilder("The QC job #").append(qcJobID).append(" has failed").toString());
							qcStatusPm.setBody(new StringBuilder("[GOBII - QC]: job #").append(qcJobID).toString(), extractType, qcDurationInSeconds, Logger.getFirstErrorReason(), false, Logger.getAllErrorStringsHTML());
						}
					}//endif Status=Completed || Failed
					else {

						if ((status.equals("CANCELLED")) || (status.equals("UNKNOWN"))) {
							Logger.logError("QC", new StringBuilder("The QC job #").append(qcJobID)
									.append(" was unsuccessful. Its status: " + status).toString());
						} else {
							Logger.logError("QC", new StringBuilder("The process time of the QC job #").append(qcJobID)
									.append(" exceeded the limit: ").append(configSettings.getGlobalServer(ServerType.KDC).getMaxStatusCheckMins()).append(" minutes").toString());
						}
						qcStatusPm.setBody(new StringBuilder("[GOBII - QC]: job #").append(qcJobID).toString(), extractType, qcDuration, Logger.getFirstErrorReason(), false, Logger.getAllErrorStringsHTML());
					}
					mailInterface.send(qcStatusPm);


				}
			}
		}
		Logger.logInfo("QC", "Done with the QC Subsection #1 of 1!");
	}

	/**
	 * To draft JobName for eMail notification
	 *
	 * @param cropName - From inst
	 * @param extract
	 * @return
	 */
	private String getJobReadableIdentifier(String cropName, GobiiDataSetExtract extract) {
		//@Siva get confirmation on lowercase crop name?
		cropName = cropName.charAt(0) + cropName.substring(1).toLowerCase();
		return "[GOBII - Extractor]: " + cropName + " - extraction of \"" + extract.getGobiiFileType() + "\"";
	}


	/**
	 * Parses the extractor instruction file from the JSON object using the Object Mapper
	 *
	 * @param filename File to parse
	 * @return List of Extractor Instructions to work on
	 */
	//Interesting fact, there are no global settings of any kind, each instruction is an island
	public List<GobiiExtractorInstruction> parseExtractorInstructionFile(String filename) {
		ObjectMapper objectMapper = new ObjectMapper();
		GobiiExtractorInstruction[] file = null;

		try {
			file = objectMapper.readValue(new FileInputStream(filename), GobiiExtractorInstruction[].class);
		} catch (Exception e) {
			Logger.logError("Extractor", "ObjectMapper could not read instructions", e);
		}
		if (file == null) return null;
		return Arrays.asList(file);
	}

	/**
	 * Determine crop type by looking at the instruction file's location for the name of a crop.
	 * This is a backup in case the JSON file doesn't specify the crop internally.
	 *
	 * @param instructionFile Location of the instruction file
	 * @return String representation of the Gobii Crop
	 */
	private String divineCrop(String instructionFile) {
		String upper = instructionFile.toUpperCase();
		String from = "/CROPS/";
		int fromIndex = upper.indexOf(from) + from.length();
		String crop = upper.substring(fromIndex, upper.indexOf('/', fromIndex));
		return crop;
	}

	private String getLogName(GobiiDataSetExtract gli, GobiiCropConfig config, Integer dsid) {
		String cropName = config.getGobiiCropType();
		String destination = gli.getExtractDestinationDirectory();
		return destination + "/" + cropName + "_DS-" + dsid + ".log";
	}

	/**
	 * Brute force method to create a list of object,object,object,object
	 *
	 * @param inputList Nonempty list of elements
	 * @return the string value of elements comma separated
	 */
	//Dear next guy - yeah, doing a 'one step unroll' then placing in 'comma - object; comma - object' makes more sense.
	//Just be happy I used a StringBuilder
	//Si, soy tan feliz ahora!
	private String commaFormat(List inputList) {
		StringBuilder sb = new StringBuilder();
		for (Object o : inputList) {
			sb.append(o.toString());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);//Remove final comma
		return sb.toString();
	}

	/**
	 * Turns a list into a newline delimited file.
	 *
	 * @param tmpDir      File path - will append the file name and return
	 * @param markerList List to go into file, newline delimited
	 * @param tmpFilename filename to use, will append ".list" after name as filetype, and use with directory for location of output.
	 * @return location of new file.
	 */
	private String createTempListFile(String tmpDir, List<String> markerList, String tmpFilename) {
		String tempFileLocation = tmpDir + tmpFilename + ".list";
		try {
			FileWriter f = new FileWriter(tempFileLocation);
			for (String marker : markerList) {
				f.write(marker);
				f.write("\n");
			}
			f.close();
		} catch (Exception e) {
			Logger.logError("Extractor", "Could not create temp file " + tempFileLocation, e);
		}
		return tempFileLocation;
	}

	private String createTempListFile(String tmpDir, List<String> markerList) {
		return createTempListFile(tmpDir, markerList, "markerList");
	}


	private boolean addSlashesToBiAllelicData(String genoFile, String extractDir, GobiiDataSetExtract extract) throws Exception {
		Path SSRFilePath = Paths.get(genoFile);
		File SSRFile = new File(SSRFilePath.toString());
		if (SSRFile.exists()) {
			Path AddedSSRFilePath = Paths.get(extractDir, (new StringBuilder("Added")).append(SSRFilePath.getFileName()).toString());
			// Deleting any temporal file existent
			rmIfExist(AddedSSRFilePath.toString());
			File AddedSSRFile = new File(AddedSSRFilePath.toString());
			if (AddedSSRFile.createNewFile()) {
				Scanner scanner = new Scanner(new FileReader(SSRFile));
				FileWriter fileWriter = new FileWriter(AddedSSRFile);
				// Copying the header
				if (scanner.hasNextLine()) {
					fileWriter.write((new StringBuilder(scanner.nextLine())).append(System.lineSeparator()).toString());
				} else {
					Logger.logError("Extractor", "Genotype file emtpy");
					return false;
				}
				if (!(scanner.hasNextLine())) {
					Logger.logError("Extractor", "No genotype data");
					return false;
				}
				Pattern pattern = Pattern.compile("^[0-9]{1,8}$");
				while (scanner.hasNextLine()) {
					String[] lineParts = scanner.nextLine().split("\\t");
					// The first column does not need to be converted
					StringBuilder addedLineStringBuilder = new StringBuilder(lineParts[0]);
					// Converting each column from the next ones
					for (int index = 1; index < lineParts.length; index++) {
						addedLineStringBuilder.append("\t");
						if (!(pattern.matcher(lineParts[index]).find())) {
							Logger.logError("Extractor", "Incorrect SSR allele size format (1): " + lineParts[index]);
							addedLineStringBuilder.append(lineParts[index]);
						} else {
							if ((5 <= lineParts[index].length()) && (lineParts[index].length() <= 8)) {
								int leftNumber = Integer.parseInt(lineParts[index].substring(0, lineParts[index].length() - 4));
								int rightNumber = Integer.parseInt(lineParts[index].substring(lineParts[index].length() - 4));
								if ((leftNumber == 0) && (rightNumber == 0)) {
									addedLineStringBuilder.append("N/N");
								} else {
									addedLineStringBuilder.append(leftNumber);
									addedLineStringBuilder.append("/");
									addedLineStringBuilder.append(rightNumber);
								}
							} else {
								if ((1 <= lineParts[index].length()) && (lineParts[index].length() <= 4)) {
									int number = Integer.parseInt(lineParts[index]);
									if (number == 0) {
										addedLineStringBuilder.append("N/N");
									} else {
										addedLineStringBuilder.append("0/");
										addedLineStringBuilder.append(number);
									}
								} else {
									Logger.logError("Extractor", "Incorrect SSR allele size format (2): " + lineParts[index]);
									addedLineStringBuilder.append(lineParts[index]);
								}
							}
						}
					}
					addedLineStringBuilder.append(System.lineSeparator());
					fileWriter.write(addedLineStringBuilder.toString());
				}
				scanner.close();
				fileWriter.close();
				// Deleting any original data backup file existent
				rmIfExist(Paths.get(SSRFilePath.toString() + ".bak").toString());
				// Backing up the original data file
				mv(SSRFilePath.toString(), SSRFilePath.toString() + ".bak");
				// Renaming the converted data file to the original data file name
				mv(AddedSSRFilePath.toString(), SSRFilePath.toString());
			} else {
				Logger.logError("Extractor", "Unable to create the added SSR file: " + AddedSSRFilePath.toString());
				return false;
			}
		} else {
			Logger.logError("Extractor", "No genotype file: " + SSRFilePath.toString());
			return false;
		}

		return true;
	}

	/**
	 * Simple Look-Up Table
	 *
	 * @param type Un-numbered Enum
	 * @return Database numeric
	 */
	private int getNumericType(GobiiSampleListType type) {
		switch (type) {
			case DNA_SAMPLE:
				return 3;
			case EXTERNAL_CODE:
				return 2;
			case GERMPLASM_NAME:
				return 1;
			default:
				return -1;
		}
	}

	/**
	 * Gets a map name from an ID
	 *
	 * @param mapId  ID of map, if null, returns null
	 * @param config Configuration master object
	 * @return name, or null if error
	 */
	private String getMapNameFromId(Integer mapId, ConfigSettings config) {
		String cropName = config.getCurrentGobiiCropType();
		if (mapId == null) return null;

		try {
			// set up authentication and so forth
			// you'll need to get the current from the instruction file
			GobiiClientContext context = GobiiClientContext.getInstance(config, cropName, GobiiAutoLoginType.USER_RUN_AS);
			//context.setCurrentCropId(cropName);

			if (LineUtils.isNullOrEmpty(context.getUserToken())) {
				logError("Digester", "Unable to login with user " + GobiiAutoLoginType.USER_RUN_AS.toString());
				return null;
			}

			String currentCropContextRoot = GobiiClientContext.getInstance(null, false).getCurrentCropContextRoot();
			GobiiUriFactory gobiiUriFactory = new GobiiUriFactory(currentCropContextRoot, cropName);

			RestUri mapUri = gobiiUriFactory
					.resourceByUriIdParam(RestResourceId.GOBII_MAPSET);
			mapUri.setParamValue("id", mapId.toString());
			GobiiEnvelopeRestResource<MapsetDTO, MapsetDTO> gobiiEnvelopeRestResourceForDatasets = new GobiiEnvelopeRestResource<>(mapUri);
			PayloadEnvelope<MapsetDTO> resultEnvelope = gobiiEnvelopeRestResourceForDatasets
					.get(MapsetDTO.class);

			MapsetDTO dataSetResponse;
			if (!resultEnvelope.getHeader().getStatus().isSucceeded()) {
				System.out.println();
				logError("Extractor", "Map set response response errors");
				for (HeaderStatusMessage currentStatusMesage : resultEnvelope.getHeader().getStatus().getStatusMessages()) {
					logError("HeaderError", currentStatusMesage.getMessage());
				}
				return null;
			} else {
				dataSetResponse = resultEnvelope.getPayload().getData().get(0);
			}
			return dataSetResponse.getName();

		} catch (Exception e) {
			logError("Extractor", "Exception while referencing map sets in Postgresql", e);
			return null;
		}
	}

	/**
	 * In place file uniqueness. First implementation is a simple sort -u
	 */
	private static void makeFileUnique(String inputFilePath, String errorFile){
		String tempPath = inputFilePath+".tmp";
		FileSystemInterface.mv(inputFilePath,tempPath);
		tryExec("sort -u ",inputFilePath,errorFile,tempPath);
		FileSystemInterface.rmIfExist(tempPath);
	}
}
