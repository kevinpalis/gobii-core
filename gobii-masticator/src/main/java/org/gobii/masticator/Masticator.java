package org.gobii.masticator;

import com.google.gson.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
        writer.write(String.join(reader.getDelimiter(), reader.getHeader()) + "\n");

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

		Logger logger = LoggerFactory.getLogger("Masticator (Main)");

		Map<String, String> argMap =
				zipmap(takeNth(2, args), takeNth(1, 2, args));

		FileAspect aspect = null;
		if (argMap.containsKey(ARG_ASPECT_FILE) && !(argMap.get(ARG_ASPECT_FILE).trim().equals("-"))) {
			try {
				aspect = AspectParser.parse(slurp(argMap.get(ARG_ASPECT_FILE)));
			} catch (IOException e) {
				logger.error(String.format("File for aspect at %s not found", argMap.get(ARG_ASPECT_FILE)));
			} catch (JsonParseException e) {
				e.printStackTrace();
				logger.error("Malformed Aspect",e);
			}
		} else {
			try {
				logger.info("Reading aspect file from std in ...");
				aspect = AspectParser.parse(slurp(System.in));
			} catch (JsonParseException e) {
//				e.printStackTrace();
				logger.error("Malformed Aspect",e);
			}
		}

		File data = null;

		if (argMap.containsKey(ARG_DATA_FILE)) {
			data = new File(argMap.get(ARG_DATA_FILE));
			if (! data.exists()) {
				logger.error(String.format("Data file at %s does not exist", argMap.get(ARG_DATA_FILE)));
			}
		} else {
			logger.info(usage());
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

		if(argMap.containsKey(ARG_CONNECTION_STRING)){
			logger.info("Running IFL");
			for(String key:getTableKeys(argMap.get(ARG_ASPECT_FILE))){
				String inputDir = outputDir.getAbsolutePath();
				String inputFile = String.format("%s%sdigest.%s", outputDir.getAbsolutePath(), File.separator, key);

				runIfl(argMap.get(ARG_CONNECTION_STRING),inputFile,inputDir,inputDir);
			}
		}
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

	private static final String BASE_IFL_PATH="/data/gobii_bundle/loaders/postgres/gobii_ifl/gobii_ifl.py";
	private static void runIfl(String connectionString, String inputFile, String inputDir, String outputDir) throws IOException {
		//It's ugly, but it works
		String iflExec = String.format(BASE_IFL_PATH+" -c %s -i %s -d %s -o %s", connectionString, inputFile, inputDir, outputDir);
		Runtime.getRuntime().exec(iflExec);
	}
}