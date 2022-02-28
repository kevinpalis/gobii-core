package org.gobii.masticator;

import com.google.gson.*;

import java.io.*;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.aspects.AspectParser;
import org.gobii.masticator.aspects.FileAspect;
import org.gobii.masticator.reader.ReaderResult;
import org.gobii.masticator.reader.TableReader;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.Val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.gobii.Util.slurp;
import static org.gobii.Util.takeNth;
import static org.gobii.Util.zipmap;

@Data
@AllArgsConstructor
public class Masticator {

	private static Logger logger = LoggerFactory.getLogger(Masticator.class);

	private FileAspect fileAspect;
	private File file;

	public void run(String table, Writer writer) throws IOException {

		logger.info("Masticating {}", table);

        TableReader reader = AspectMapper.map(fileAspect.getAspects().get(table)).build(file);
        if(!table.equalsIgnoreCase("matrix)")) { //Omit header on 'matrix' 
			writer.write(String.join(reader.getDelimiter(), reader.getHeader()) + "\n");
		}
		for (ReaderResult read = reader.read(); ! (read instanceof End) ; read = reader.read()) {

			if (read instanceof Val) {
				writer.write(read.value());
				writer.write('\n');
				writer.flush();
			}
		}

		writer.flush();
	}


	private static final String ARG_ASPECT_FILE = "-a";
	private static final String ARG_DATA_FILE = "-d";
	private static final String ARG_OUTPUT_DIRECTORY = "-o";
	private static final String ARG_CONNECTION_STRING = "-s";



	public static void main(String[] args) throws Exception {

		masticate(args,null,null, true, true);
	}

	/**
	 * Calls the masticator with arguments, including an optional pre-parsed aspect file. If not supplied, will use -a
	 * (ARG_ASPECT_FILE) to find the aspect as normal. This allows a running program to mess with the aspect in memory
	 * and pass the new aspect directly down, or use a virtual aspect file not on disk, without having to deal with
	 * standard input.
	 * @param args argument list, as per main(String[] args)
	 * @param aspect Optional FileAspect for the base aspect to be used in place of -a
	 * @param iflPath path to base IFLs
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void masticate(String[] args, FileAspect aspect, String iflPath, boolean createIntermediateFiles, boolean runIFLs) throws IOException, InterruptedException {
		Logger logger = LoggerFactory.getLogger("Masticator (Main)");

		Map<String, String> argMap =
				zipmap(takeNth(2, args), takeNth(1, 2, args));

		if(aspect==null) {
			if (argMap.containsKey(ARG_ASPECT_FILE) && !(argMap.get(ARG_ASPECT_FILE).trim().equals("-"))) {
				try {
					aspect = AspectParser.parse(slurp(argMap.get(ARG_ASPECT_FILE)));
				} catch (IOException e) {
					logger.error(String.format("File for aspect at %s not found", argMap.get(ARG_ASPECT_FILE)));
				} catch (JsonParseException e) {
					e.printStackTrace();
					logger.error("Malformed Aspect", e);
				}
			} else {
				try {
					logger.info("Reading aspect file from std in ...");
					aspect = AspectParser.parse(slurp(System.in));
				} catch (JsonParseException e) {
					logger.error("Malformed Aspect", e);
				}
			}
		}


		File outputDir = null;

		if (argMap.containsKey(ARG_OUTPUT_DIRECTORY)) {
			outputDir = new File(argMap.get(ARG_OUTPUT_DIRECTORY));
			if (! outputDir.exists()) {
				outputDir.mkdirs();
			}
			if (! outputDir.isDirectory()) {
				logger.error("Output Path is not a directory");
			}
		} else {
			logger.error(usage());
		}

		if(createIntermediateFiles) {
			createIntermediateFiles(argMap.getOrDefault(ARG_DATA_FILE,null),aspect, logger, outputDir);
		}
		if(runIFLs) {
			runIFLs(iflPath, logger, argMap.getOrDefault(ARG_CONNECTION_STRING,null), aspect, outputDir);
		}
	}

	public static void createIntermediateFiles(String argDataFile, FileAspect aspect, Logger logger, File outputDir) throws IOException, InterruptedException {
		File data = null;

		if (argDataFile!=null) {
			data = new File(argDataFile);
			if (! data.exists()) {
				logger.error(String.format("Data file at %s does not exist", argDataFile));
			}
		} else {
			logger.info(usage());
		}

		Masticator masticator = new Masticator(aspect, data);

		List<Thread> threads = new LinkedList<>();

		for (String table : aspect.getAspects().keySet()) {
			String outputFilePath = String.format("%s%sdigest.%s", outputDir.getAbsolutePath(), File.separator, table);
			File outputFile = new File(outputFilePath);
			outputFile.createNewFile();

			final Thread t = new Thread(() -> {
				try (FileWriter fileWriter = new FileWriter(outputFile, false);
					 BufferedWriter writer = new BufferedWriter(fileWriter);) {
					masticator.run(table, writer);
				} catch (IOException e) {
					logger.error("IOException while processing {}", table, e);
				}
			});

			t.start();

			threads.add(t);
		}


		for (Thread t : threads) {
			t.join();
		}
	}

	public static void runIFLs(String iflPath, Logger logger, String connectionString, FileAspect aspect, File outputDir) throws IOException {
		if(connectionString!=null){
			logger.info("Running IFL");
			Set<String> aspectSet = aspect.getAspects().keySet();

			//Aspect housekeeping

			aspectSet.remove("matrix"); // Matrix file is not processed

			if(aspectSet.contains("germplasm")){
				loadSingleIFL(iflPath, logger, connectionString, outputDir, "germplasm");
				aspectSet.remove("germplasm");//HashSet.KeySet has a working remove, this could have been messy otherwise
			}
			if(aspectSet.contains("dnasample")){
				loadSingleIFL(iflPath, logger, connectionString, outputDir, "dnasample");
				aspectSet.remove("dnasample");
			}

			//Once those two are done, there are no more required orderings in the table, so the rest can be done
			for(String key:aspectSet){
				loadSingleIFL(iflPath, logger, connectionString, outputDir, key);
			}
		}
		else{
			logger.info("No Connection String");
		}
	}

	public static void loadSingleIFL(String iflPath, Logger logger, String connectionString, File outputDir, String key) throws IOException {
		logger.info("Loading " + key);
		String inputDir = outputDir.getAbsolutePath()+"/"; // TODO - better line separator - fix bug found in dev testing
		String inputFile = String.format("%s%sdigest.%s", outputDir.getAbsolutePath(), File.separator, key);

		runIfl(connectionString,inputFile,inputDir,iflPath);
	}

	private static String usage() {
		return "masticator -a {File|-} -d File -o Directory\n\t-a aspect\n\t-d data file\n\t-o output directory\n\t[-s connection string]";
	}

	private static List<String> getTableKeys(String inFile) throws IOException {
		JsonElement aspectElement = JsonParser.parseString(slurp(inFile)).getAsJsonObject().get("aspects");
		List<String> tableNames = new ArrayList<String>();
		for (Map.Entry jsonObject : aspectElement.getAsJsonObject().entrySet()) {
			String tableName = jsonObject.getKey().toString();
			if(tableName.equals("matrix")){
				continue; //Ignore Matrix from tables
			}
			tableNames.add(tableName);
		}
		return tableNames;
	}

	private static List<String> getTableKeys(FileAspect baseAspect) throws IOException {
		List<String> tableNames = new ArrayList<String>();
		for (Map.Entry jsonObject : baseAspect.getAspects().entrySet()) {
			String tableName = jsonObject.getKey().toString();
			if(tableName.equals("matrix")){
				continue; //Ignore Matrix from tables
			}
			tableNames.add(tableName);
		}
		return tableNames;
	}

	private static final String BASE_IFL_PATH="/gobii_bundle/loaders/gobii_ifl/gobii_ifl.py";
	private static void runIfl(String connectionString, String inputFile, String outputDir, String iflPath) throws IOException {
		//It's ugly, but it works
		if(iflPath==null){
			iflPath=BASE_IFL_PATH;
		}
		String iflExec = String.format(iflPath+" -c %s -i %s -o %s", connectionString, inputFile, outputDir);
		Process proc = Runtime.getRuntime().exec(iflExec);
		try {
			proc.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		proc.getErrorStream().transferTo(System.err);
		proc.getInputStream().transferTo(System.out);
	}
}