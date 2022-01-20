package org.gobiiproject.gobiiprocess.digester;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.cli.*;
import org.gobii.Util;
import org.gobii.masticator.aspects.AspectParser;
import org.gobii.masticator.aspects.FileAspect;
import org.gobii.masticator.aspects.MatrixAspect;
import org.gobii.masticator.aspects.TableAspect;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.dto.instructions.validation.ValidationConstants;
import org.gobiiproject.gobiimodel.dto.instructions.validation.ValidationResult;
import org.gobiiproject.gobiimodel.dto.instructions.validation.errorMessage.Failure;
import org.gobiiproject.gobiimodel.types.DatasetOrientationType;
import org.gobiiproject.gobiimodel.utils.InstructionFileValidator;
import org.gobiiproject.gobiimodel.utils.email.ProcessMessage;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiiprocess.HDF5Interface;
import org.gobiiproject.gobiiprocess.digester.utils.validation.DigestFileValidator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


import static org.gobiiproject.gobiimodel.utils.HelperFunctions.getJdbcConnectionString;
import static org.gobiiproject.gobiimodel.utils.HelperFunctions.tryExec;

/**
 * New lightweight(ish) loader based on Digestor.sh, for use with the masticator classes.
 */
public class EBSLoader {
    //Hardcoded parameters
    private static final String VARIANT_CALL_TABNAME = "matrix";
    private final String HDF5MatrixLoadPath="/data/gobii_bundle/loader/hdf5/bin";
    private final String IFLPath="loaders/gobii_ifl/gobii_ifl.py";


    //Sane and often correct defaults
    private String pathRoot="/gobii_bundle/";
    private String cropName="dev";
    private String dbHost="gobii-db";
    private String dbPort="5432";
    private String dbUser="ebsuser";//new default
    private String dbName="gobii_dev";
    private String metaDBName="gobii_meta";
    private String hdf5Path = "crops/dev/hdf5/";
    private boolean verbose = false;
    private String aspectFilePath="core/intertek.json";

    private String validationFile="core/validationConfig.json";

    private String md5File = "core/md5List.txt";//backup purposes only

    private enum InputEntity{
        Project, Platform, Experiment, Dataset, Germplasm_Species, Germplasm_Type
    }

    private HashMap<InputEntity, String> inputEntityValues = new HashMap<InputEntity, String>();



    //Defaultless items
    private int matrixElementSize;
    private String dbPass;
    private String inputFile;
    private String baseDirectory = "/data/gobii_bundle/crops/"+cropName+"/loader/digest";
    private String aspectInFull;

    public static void main(String[] args) throws Exception {

        int errorCode = 0;
        EBSLoader loader = new EBSLoader();
        loader.baseDirectory = "/data/gobii_bundle/crops/"+loader.cropName+"/loader/digest";

        String[] remainingArgs = loader.parseOpts(args);

        //Map aspects

        //Connect to Postgres Metadata
        String dbConnectionString = loader.getMetaConnectionString();

        //Because *someone* keeps putting weird special characters in the password, I have to parse it out instead of keeping it nice and tidy
        //postgres://
        String user = dbConnectionString.substring(13,dbConnectionString.indexOf(':',13));
        String password = dbConnectionString.substring(1+dbConnectionString.indexOf(':',13), dbConnectionString.indexOf("@",13));
        String jdbcConnector = "jdbc:postgresql://" + dbConnectionString.substring(1+dbConnectionString.indexOf('@'));

        Connection dbConn = DriverManager.getConnection(jdbcConnector,user,password);
        DatabaseMetaData dbMeta = dbConn.getMetaData();

        File aspectFile = new File(loader.pathRoot + loader.aspectFilePath);

        FileAspect baseAspect;
        if(!aspectFile.exists()) {
            //assume it's a name from postgres
            loader.aspectInFull=getAspectFromPostres(dbConn,loader.aspectFilePath);
            baseAspect = AspectParser.parse(loader.aspectInFull);
        }
        else{
            baseAspect = AspectParser.parse(Util.slurp(aspectFile));
        }




        //validate tables
        loader.validate(baseAspect);

        String intermediateDirectory = loader.createIntermediateFolder();
        String outputDirectory = loader.createOutputFolder();

        //checksums
        String md5Sum = md5Hash(loader.inputFile);
        if(md5Sum == null  || !loader.checkMD5(md5Sum,dbConn,dbMeta)){
            System.err.println("Non-unique checksum");
            errorCode = 4;
            System.exit(errorCode);
        }


        //create intermediates
        if(!loader.createIntermediates(intermediateDirectory)){
            errorCode = 1;
            System.exit(errorCode);
        }

        //validateIntermediates
        String validationFile =  loader.pathRoot + loader.validationFile;
        try {
            boolean hasErrors = loader.validateMetadata(intermediateDirectory, validationFile, DatasetOrientationType.MARKER_FAST);//TODO - choose orientation correctly
            if(hasErrors){
                errorCode = 2;
                System.exit(errorCode);

            }
        } catch (Exception e) {
            System.err.println("Validation Error: " + e.getMessage() );
            errorCode = 3;
            System.exit(errorCode);
        }

        //IFL load intermediates

        boolean hasMatrix=false;
        for(TableAspect table : baseAspect.getAspects().values()){
            String tableName = table.getTable();
            System.out.println("Reading table: " + table.getTable());
            if(tableName.equals("matrix")){
                hasMatrix = true;
                continue;//don't process the matrix
            }
            boolean success = loader.runIFL(intermediateDirectory, tableName, outputDirectory);
            if(!success){
                System.err.println("Error in Intermediate File Load " + table.getTable());
                errorCode=5;
                System.exit(errorCode);
            }
        }

        if(hasMatrix){
            TableAspect matrixTable = baseAspect.getAspects().get(VARIANT_CALL_TABNAME);
            ConfigSettings derp = new ConfigSettings();
            MatrixAspect aspect = (MatrixAspect) matrixTable.getAspects().get(VARIANT_CALL_TABNAME);//TODO - what if this isn't here
            //TODO - maybe dataset type should be in matrix?
            String datasetType = "2_letter_nucleotide";//TODO - fix this
            ProcessMessage dummy = new ProcessMessage();
            int datasetId = 1;
            String errorFilePath="logger";
            String variantFilename = intermediateDirectory + matrixTable.getTable();
            File variantFile = new File(variantFilename);
            //TODO - methodize
            HDF5Interface.setPathToHDF5(loader.HDF5MatrixLoadPath);
            HDF5Interface.setPathToHDF5Files(loader.hdf5Path);
            HDF5Interface.createHDF5FromDataset(dummy, datasetType, derp, datasetId, loader.cropName, errorFilePath, variantFile);
        }



        //Print success?
        if(loader.verbose){
            System.out.println("Data successfully loaded to " + loader.dbHost+"/"+loader.dbName);
        }

        int jobNum = new Random().nextInt();//TODO - actual people number
        loader.addMD5(md5Sum,dbConn,dbMeta,"EBS Job " + jobNum );
    }

    private String createIntermediateFolder(){
        String inputFileName = new File(inputFile).getName();

        String intermediatePath = baseDirectory + "/" + inputFileName;

        new File(intermediatePath).mkdir();
        return intermediatePath;
    }

    private String createOutputFolder(){
        String inputFileName = new File(inputFile).getName();

        String outputPath = baseDirectory + "/" + inputFileName + "/output";

        new File(outputPath).mkdir();
        return outputPath;
    }

    private boolean createIntermediates(String intermediatePath) throws IOException {
        String aspectPath = pathRoot+aspectFilePath;

        if(aspectInFull != null){
            aspectPath = intermediatePath+"/aspect.tmp";
            createTempAspectFile(aspectPath,aspectInFull);
        }

        String command = masticatorCommand(aspectPath,inputFile,intermediatePath);

        if(verbose){
            System.out.println(command);
        }

        return tryExec(command);
    }

    private boolean validate(FileAspect aspect){
        // Instruction file Validation
        InstructionFileValidator instructionFileValidator  = new InstructionFileValidator(aspect);
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

        return false;//TODO
    }




    private String[] parseOpts(String[] args){
        Options o = new Options()
                .addOption("pr", "pathRoot", true, "Fully qualified path to gobii bundle root")
                .addOption("a", "aspect", true, "Aspect file path or name")
                .addOption("crop", "cropName", true, "Name of crop being loaded (and paths to place output)")
                .addOption("dbh", "dbHost", true, "Database hostname")
                .addOption("dbp", "dPort", true, "Database port")
                .addOption("dbu", "dbUser", true, "Database username")
                .addOption("dbpw", "dbPassword", true, "Database password")
                .addOption("dbn", "dbName", true, "Database to connect to's name")
                .addOption("bd", "baseDir", true, "Fully qualified path to intermediate and output base directories")
                .addOption("v", "verbose", false, "Enable verbose console output")
                .addOption("i", "inputFile", true, "input file path")
                .addOption("h", "hdfFiles", true, "Fully qualified path to hdf files");

        addEntityOption("dst", InputEntity.Dataset,o);
        addEntityOption("exp", InputEntity.Experiment,o);
        addEntityOption("grs", InputEntity.Germplasm_Species,o);
        addEntityOption("grt", InputEntity.Germplasm_Type,o);
        addEntityOption("pltfm", InputEntity.Platform,o);
        addEntityOption("prjct", InputEntity.Project,o);


        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cli = parser.parse(o, args);
            if (cli.hasOption("pathRoot")) pathRoot=cli.getOptionValue("pathRoot");
            if (cli.hasOption("aspect")) aspectFilePath=cli.getOptionValue("aspect");
            if (cli.hasOption("verbose")) verbose=true;
            if (cli.hasOption("dbHost")) dbHost = cli.getOptionValue("dbHost");
            if (cli.hasOption("dbPort")) dbPort = cli.getOptionValue("dbPort");
            if (cli.hasOption("dbUser")) dbUser = cli.getOptionValue("dbUser");
            if (cli.hasOption("dbName")) dbName = cli.getOptionValue("dbName");
            if (cli.hasOption("dbPassword")) dbPass = cli.getOptionValue("dbPassword");
            if (cli.hasOption("baseDir")) baseDirectory = cli.getOptionValue("baseDir");
            if (cli.hasOption("hdfFiles")) hdf5Path=cli.getOptionValue("hdfFiles");
            if (cli.hasOption("inputFile")) inputFile=cli.getOptionValue("inputFile");

            for(InputEntity entity: InputEntity.values()){
                if(cli.hasOption(entity.toString())){
                    inputEntityValues.put(entity, cli.getOptionValue(entity.toString()));
                }
            }
            LoaderGlobalConfigs.setFromFlags(cli);
            args = cli.getArgs();//Remaining args passed through
            if(inputFile == null || dbPass == null){
                throw new ParseException("No required ops specified");
            }

        } catch (org.apache.commons.cli.ParseException exp) {
            new HelpFormatter().printHelp("java -jar EBSLoader.jar ", "" +
                    "Example: java -jar EBSLoader.jar -dbpw secretP@ssword -a testAspect -i crops/dev/files/filea.txt", o, null, true);
            System.exit(2);
        }

        return args;
    }

    private static void addEntityOption(String shortOpt,  InputEntity entity, Options o){
        o.addOption(shortOpt, entity.toString(),true, entity.toString() + " name to use for loading. Will create a new shell entity if no entity of this name exists");
    }


    /**
     * Generate a string MD5 hash of a file
     * @param file file path
     * @return String version of MD5 hash if file exists, null on failure
     */
    private static String md5Hash(String file){
        HashFunction md5 = Hashing.md5();
        String stringHash=null;
        try {
            stringHash=com.google.common.io.Files.asByteSource(new File(file)).hash(md5).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringHash;
    }

    private boolean checkMD5(String md5Sum, Connection jdbcConnection, DatabaseMetaData dbMeta) throws IOException, SQLException {
        if(tableExists(dbMeta, "PUBLIC", "JOB")){ //TODO - still a bit jank
            return hasMD5InPostgres(jdbcConnection,md5Sum);
        }
        else{
            return checkMD5Standalone(md5Sum);
        }
    }


    /**
     * Super simple 'check if a table with this name exists' method
     * @param dbMeta DatabaseMetaData JDBC type, to find the schema from
     * @param tableName name of table. Postgres is case agnostic, so this should work no matter the input case
     * @return
     */
    private static boolean tableExists(DatabaseMetaData dbMeta, String tableName){
        return tableExists(dbMeta,null,tableName);
    }

    /**
     * Super simple 'check if a table with this name exists' method
     * @param dbMeta DatabaseMetaData JDBC type, to find the schema from
     * @param tableName name of table. Postgres is case agnostic, so this should work no matter the input case
     * @return
     */
    private static boolean tableExists(DatabaseMetaData dbMeta, String schemaName,String tableName){
        try{
            ResultSet rs = dbMeta.getTables(null,schemaName,tableName,new String[]{"TABLE"});
            boolean hadAResult=rs.next();
            rs.close();
            return hadAResult;
        }catch(Exception e){
            //Something went wrong in postgres, assume the PG connection's bad, and just return nope to does the table exist here
            return false;
        }
    }

    private boolean checkMD5Standalone(String md5Sum) throws IOException {
        boolean isUnique = true;
        File md5File = new File(pathRoot + this.md5File);
        BufferedReader reader = new BufferedReader(new FileReader(md5File));
        while(reader.ready()){
            String hash = reader.readLine();
            if(hash.equals(md5Sum)) {
                isUnique = false;
                break;//break while loop
            }
        }
        reader.close();
        return isUnique;
    }

    private void addMD5(String md5Sum, Connection dbConn, DatabaseMetaData dbMeta, String jobName) throws IOException, SQLException {
        if(tableExists(dbMeta, "CHECKSUM")){
            setMD5InPostgres(dbConn,md5Sum,jobName);
        }
        else{
            addMD5Standalone(md5Sum);
        }

    }

    private void addMD5Standalone(String md5Sum) throws IOException {
        File md5File = new File(pathRoot + this.md5File);
        BufferedWriter writer = new BufferedWriter(new FileWriter(md5File, true));
        writer.write(md5Sum);
        writer.newLine();
        writer.flush();
        writer.close();
    }


    private boolean runIFL(String intermediatePath, String ifName, String outputPath){
        String command = iflCommand(intermediatePath,ifName,outputPath);
        if(verbose){
            System.out.println(command);
        }

        return tryExec(command);

    }

    private static String getAspectFromPostres(Connection jdbcConnection, String aspectName) throws SQLException {
        Statement statement = jdbcConnection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT aspect from template WHERE name = '" + aspectName+"'");
        rs.next();
        String ret = rs.getString(1); //why are columns one-indexed?
        rs.close();
        statement.close();
        return ret;
    }

    private static boolean hasMD5InPostgres(Connection jdbcConnection, String md5Hash) throws SQLException {
        Statement statement = jdbcConnection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * from job WHERE checksum_id = '" + md5Hash+"'");
        boolean ret = rs.next(); //True if there's at least one result
        rs.close();
        statement.close();
        return ret;
    }

    private static void setMD5InPostgres(Connection jdbcConnection, String md5Hash, String jobName) throws SQLException {
        Statement statement = jdbcConnection.createStatement();
        int type = 1; //load
        int crop = 1;//dev - TODO
        int status = 0; //TODO - dunno what's a good number here
        String message = "EBSLoader load";

        statement.executeUpdate("INSERT INTO public.job( name, crop_id, type_id, status, message, checksum_id)\n" +
                "VALUES ('"+jobName+"', "+type+", "+crop+", "+status+", '"+message+"', '"+md5Hash+"');");
        statement.close();
    }

    //Building commands
    private String iflCommand(String intermediatePath, String ifName, String outputPath){
        return pathRoot + IFLPath+ " -v -c "+ pgURL()+" -i " +intermediatePath+"/digest."+ifName+" -o "+outputPath;
    }
    private String masticatorCommand(String aspectPath, String dataPath, String intermediatePath){
        return "java -jar Masticator.jar -a "+aspectPath+" -d "+dataPath+" -o "+ intermediatePath;
    }


    //Postgres connection string
    private String pgURL(){
        return pgURL(dbUser,dbPass,dbHost,dbPort,dbName);
    }
    private static String pgURL(String user, String pass, String host, String port, String dbName){
        return "postgresql://"+user + ":"+pass+"@"+host+":"+port+"/"+dbName;
    }



    //Validate Metadata
    private boolean validateMetadata(String intermediateDirectory, String validationFilePath, DatasetOrientationType matrixOrientation) throws Exception {

        String dbConnectionString = getConnectionString();
        String directory = intermediateDirectory;
        //Validation logic before loading any metadata

        //TODO: This doesn't work at all any more
 /*       DigestFileValidator digestFileValidator = new DigestFileValidator(directory, validationFilePath, dbConnectionString);
        digestFileValidator.performValidation(dbConnectionString, matrixOrientation);
 */       if(true)return false; //TODO - unskip


        //Call validations here, update 'success' to false with any call to ErrorLogger.logError()
        List<Path> pathList =
                Files.list(Paths.get(directory))
                        .filter(Files::isRegularFile).filter(path -> String.valueOf(path.getFileName()).endsWith(".json")).collect(Collectors.toList());
        if (pathList.size() < 1) {
            Logger.logError("Validation","Unable to find validation checks");
        }
        ValidationResult[] validationResults = new ObjectMapper().readValue(pathList.get(0).toFile(), ValidationResult[].class);
        boolean hasAnyFailedStatuses=false;
        for(ValidationResult status : validationResults){
            if(status.status.equalsIgnoreCase(ValidationConstants.FAILURE)){
                hasAnyFailedStatuses=true;
                for(Failure f : status.failures){
                    System.out.println(f.reason + " " + (f.columnName!=null&&f.columnName.size()>0?f.columnName.get(0):"") + " " + (f.values!=null && f.values.size()>0?f.values.get(0):""));
                }
            }
        }

        if (hasAnyFailedStatuses) {
            Logger.logError("Validation", "Validation failures");
        }


        return hasAnyFailedStatuses;
    }

    private String getConnectionString(){
        return "postgresql://"
                + dbUser
                + ":"
                + dbPass
                + "@"
                + dbHost
                + ":"
                + dbPort
                + "/"
                + dbName;
    }
    private String getMetaConnectionString(){
        return "postgresql://"
                + dbUser
                + ":"
                + dbPass
                + "@"
                + dbHost
                + ":"
                + dbPort
                + "/"
                + metaDBName;
    }

    //Technically we can pass in the aspect as a string, but it requires a lot of special character manipulation
    private void createTempAspectFile(String tempAspectFile, String aspectInFull) throws IOException {
        FileWriter fw = new FileWriter(tempAspectFile);
        fw.write(aspectInFull);
        fw.close();
    }
}


