package org.gobiiproject.gobiimodel.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.FilenameUtils;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.ServerConfig;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiLoaderInstruction;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiLoaderProcedure;
import org.gobiiproject.gobiimodel.types.ServerType;
import org.gobiiproject.gobiimodel.utils.error.ErrorMessageInterpreter;
import org.gobiiproject.gobiimodel.utils.error.Logger;
//import com.sun.jna.Library;
//import com.sun.jna.Native;

public class HelperFunctions {

    private static final String PARAM_CTCN_USR = "{username}";
    private static final String PARAM_CTCN_PWD = "{password}";

    private static boolean showTempFiles = true;

    /**
     * Takes a string <i>in</i> and filters it. Returns the part of in from the end of <i>from</i>
     * to the beginning of <i>to</i>, exclusive.
     *
     * @param in   string to be filtered
     * @param from first match in substring is beginning of filtered string
     * @param to   last match in substring is end of filtered string
     * @return filtered string, from the character after the end of from to the character before the beginning of to
     */
    public static String filter(String in, String from, String to, String find, String replace) {
        if (in == null) return null;
        String result = "";
        if (from == null) from = "";
        if (to == null) to = "";
        int startIndex, endIndex;
        if (in.contains(from)) {
            startIndex = in.indexOf(from) + from.length();
        } else {
            startIndex = 0;
        }
        String from_in = in.substring(startIndex);
        if (from_in.contains(to) && !to.equals("")) { // If to is null or blank, read to the end
            endIndex = startIndex + from_in.indexOf(to);
        } else {
            endIndex = in.length();
        }
        result = in.substring(startIndex, endIndex);

        if (find != null && replace != null) {
            result = result.replaceAll(find, replace);
        }

        return result;
        //Too clever by a half- if from and too are null, returns substring(0,-1);
    }

    public static String readFile(String path) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(path), StandardCharsets.UTF_8))  {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) {
            return null;
        }
        return contentBuilder.toString();
    }

    /**
     * Helper method which executes a string as a command line argument, and waits for it to complete using Runtime.getRuntime.exec.
     *
     * @return true if successful
     */
    public static boolean tryExec(String toExec) {
        return tryExec(toExec, null, null);
    }


    public static boolean tryExec(String execString, String outputFile, String errorFile) {
        return tryExec(execString, outputFile, errorFile, null);
    }

    /**
     * Like TryExec, but tries an "External Function Call"
     */
    public static void tryFunc(ExternalFunctionCall efc, String outputFile, String errorFile) {
        boolean success = tryExec(efc.getCommand(), outputFile, errorFile);
        if (!success) {
            try {
                Logger.logError(efc.functionName, "Non-zero exit code", errorFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void tryFunc(ExternalFunctionCall efc, String errorFile) {
        tryFunc(efc, null, errorFile);
    }


    public static Process initProcecess(String[] execCommand,
                                        String outputFile,
                                        String errorFile,
                                        String inputFile,
                                        Integer waitSeconds) {

        Process returnVal = null;
        String execCommandProcName = execCommand[0];

        ProcessBuilder builder = new ProcessBuilder(execCommand);
        if (outputFile != null) builder.redirectOutput(new File(outputFile));
        if (errorFile != null) builder.redirectError(new File(errorFile));
        if (inputFile != null) builder.redirectInput(new File(inputFile));

        try {
            returnVal = builder.start();
            if (waitSeconds != null) {
                returnVal.waitFor(waitSeconds, TimeUnit.SECONDS);
            } else {
                returnVal.waitFor();
            }
        } catch (Exception e) {
            Logger.logError(execCommandProcName, "Exception in process", e);
            Logger.logError(execCommandProcName, "Error File Contents", errorFile);
        }

        return returnVal;
    }

    public static String[] makeExecString(String execString) {

        String[] returnVal;

        returnVal = execString.split(" ");

        return returnVal;

    }

    public static String makeProcName(String[] execString) {

        String returnVal = execString[0];

        if (returnVal.equals("python")) {
            returnVal = execString[1];
        }

        return returnVal;
    }

    //Null outputFIle to get output to standard out.
    public static boolean tryExec(String execString, String outputFile, String errorFile, String inputFile) {

        String[] execArray = makeExecString(execString);
        String executedProcName = makeProcName(execArray);

        Process p = initProcecess(execArray, outputFile, errorFile, inputFile, null);

        if(p == null){
            Logger.logError(executedProcName,"Process did not execute correctly - no data returned");
            return false;
        }
        if (p.exitValue() != 0) {
            String message = ErrorMessageInterpreter.getErrorMessage(executedProcName,p.exitValue(),errorFile);
            Logger.logError(executedProcName, message, errorFile);
            return false;
        }
        return true;
    }

    public static String getDestinationFile(GobiiLoaderProcedure procedure, GobiiLoaderInstruction instruction) {
        String destination = procedure.getMetadata().getGobiiFile().getDestination();
        char last = destination.charAt(destination.length() - 1);
        if (last == '\\' || last == '/') {
            return destination + "digest." + instruction.getTable();
        } else return destination + "/" + "digest." + instruction.getTable();
    }

    /**
     * Returns a string from the output of a process
     *
     * @param execString
     * @param errorFile
     * @return
     */
    public static int iExec(String execString, String errorFile) {
        ProcessBuilder builder = new ProcessBuilder(execString.split(" "));
        if (errorFile != null) builder.redirectError(new File(errorFile));
        Process p;
        BufferedReader reader = null;
        try {
            p = builder.start();
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));//What terrible person makes 'InputStream' the type of the output of a process
            p.waitFor();
            if (p.exitValue() != 0) {
                Logger.logError(execString.substring(0, execString.indexOf(" ")), "Exit code " + p.exitValue(), errorFile);
                return -1;
            }
            return Integer.parseInt(reader.readLine().split(" ")[0]);
        } catch (Exception e) {
            Logger.logError(execString.substring(0, execString.indexOf(" ")), e.getMessage(), e);
            return -1;
        }
    }

    //For a folder destination, returns /digest.<tablename>

    /***
     * Returns a valid postgres connection string, including username and password
     * in the clear
     * @param config the config object for the crop
     * @return The connection string
     */
    public static String getPostgresConnectionString(GobiiCropConfig config) {
        ServerConfig postGresConfig = config.getServer(ServerType.GOBII_PGSQL);
        String ret = "postgresql://"
                + postGresConfig.getUserName()
                + ":"
                + postGresConfig.getPassword()
                + "@"
                + postGresConfig.getHost()
                + ":"
                + postGresConfig.getPort()
                + "/"
                + postGresConfig.getContextPath(false);
        return ret;
    }

    public static String getJdbcConnectionString(ServerConfig postGresConfig) {
        String ret = "jdbc:postgresql://"
                + postGresConfig.getHost()
                + ":"
                + postGresConfig.getPort()
                + "/"
                + postGresConfig.getContextPath(false);

        return ret;
    }

    public static String getJdbcConnectionString(GobiiCropConfig config) {

        ServerConfig postGresConfig = config.getServer(ServerType.GOBII_PGSQL);
        String ret = getJdbcConnectionString(postGresConfig);
        return ret;
    }


    /***
     * Returns a valid postgres connection string, but username and password
     * are "parameterized" so that the string can be safely logged or used in
     * error messages. This string can then be made useful for actual connections
     * using the replacePostgressCredentials() method
     * @param config the config object for the crop
     * @return The safe connection string
     */
    public static String getSecurePostgresConnectionString(GobiiCropConfig config) {
        ServerConfig postGresConfig = config.getServer(ServerType.GOBII_PGSQL);
        String ret = "postgresql://"
                + PARAM_CTCN_USR
                + ":"
                + PARAM_CTCN_PWD
                + "@"
                + postGresConfig.getHost()
                + ":"
                + postGresConfig.getPort()
                + "/"
                + postGresConfig.getContextPath(false);
        return ret;
    }


    /***
     * Returns a valid webservice connection string
     * @param config the config object for the crop
     * @return The connection string
     */
    public static String getWebserviceConnectionString(GobiiCropConfig config) {
        ServerConfig webserviceConfig = config.getServer(ServerType.GOBII_WEB);
        String ret = "http://"
                + webserviceConfig.getHost()
                + ":"
                + webserviceConfig.getPort()
                + ("/"
                + webserviceConfig.getContextPath(false)).replaceAll("//","/");//Replace a second / if it exists in the context path
        return ret;
    }

    /***
     * Given a connection string created with getSecurePostgresConnectionString(),
     * returns a new string that contains the real username and password for use in a
     * real connection.
     * @param secureString The connection string that was created with getSecurePostgresConnectionString()
     * @param config the config object for the crop
     * @return The safe connection string
     */
    public static String replacePostgressCredentials(String secureString, GobiiCropConfig config) {

        ServerConfig postGresConfig = config.getServer(ServerType.GOBII_PGSQL);
        String ret = secureString
                .replace(PARAM_CTCN_USR, postGresConfig.getUserName())
                .replace(PARAM_CTCN_PWD, postGresConfig.getPassword());

        return ret;

    }

    public static boolean sendEmail(String jobName, String fileLocation, boolean success, String errorLogLoc, ConfigSettings config, String recipientAddress) {
        return sendEmail(jobName, fileLocation, success, errorLogLoc, config, recipientAddress, null);
    }

    public static boolean sendEmail(String jobName, String fileLocation, boolean success, String errorLogLoc, ConfigSettings config, String recipientAddress, String[] digestTempFiles) {
        String host = config.getEmailSvrDomain();
        String port = config.getEmailServerPort().toString();
        config.getEmailSvrHashType();//ignore
        String password = config.getEmailSvrPassword();
        String protocol = config.getEmailSvrType().toLowerCase();
        String fromUser = config.getEmailSvrUser();
        String emailAddress = recipientAddress;
        String user = fromUser;
        try {
            sendEmail(jobName, fileLocation, success, errorLogLoc, host, port, emailAddress, fromUser, password, user, protocol, digestTempFiles);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param jobName
     * @param fileLocation
     * @param success
     * @param host         smtp.cornell.edu
     * @param port         587
     * @param emailAddress
     * @param fromUser
     * @param password
     * @param username
     * @param protocol     "smtp"
     * @throws Exception
     */
    private static void sendEmail(String jobName, String fileLocation, boolean success, String errorLogLoc, String host, String port, String emailAddress, String fromUser, String password, String username, String protocol, String[] digestTempFiles) throws Exception {
        if (emailAddress == null || emailAddress.equals("")) return;
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.starttls.required", "true");
        props.setProperty("mail.transport.protocol", protocol);
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.host", host);
        props.setProperty("mail.port", port);
        props.setProperty("mail.user", username);
        props.setProperty("mail.password", password);

        Session mailSession = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        Transport transport = mailSession.getTransport(protocol);

        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(fromUser));
        String subject = jobName + (success ? " Completed Successfully" : " Failed");
        message.setSubject(subject);
        String content = subject + "\n";
        if (success && fileLocation != null) content += "Your file is available at " + fileLocation;
        if (success && showTempFiles) {
            if (digestTempFiles != null) {
                for (String file : digestTempFiles) {
                    if (checkFileExistence(file.substring(file.indexOf('\t') + 1, file.length()))) {
                        content += "\nThe loader has created digest file: " + file;
                    }
                }
            }
        }
        if (!success) {
            content += "\nAn unexpected error occurred when processing your request.";
            if (digestTempFiles != null) {
                for (String file : digestTempFiles) {
                    if (checkFileExistence(file.substring(file.indexOf('\t') + 1, file.length()))) {
                        content += "\nThe loader has created digest file: " + file;
                    }
                }
            }
        }
        message.setContent(content, "text/plain");
        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(emailAddress));
        transport.connect(username, password);
        transport.sendMessage(message,
                message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    /**
     * Moves an instruction file from it's current folder to the 'done' folder
     *
     * @param instructionFile Fully qualified path to the instruction file
     */
    public static String completeInstruction(String instructionFile, String doneFolder) {
        //Move instruction file
        FileSystemInterface.mvToFolder(instructionFile, doneFolder ,true);
        return (doneFolder + FilenameUtils.getName(instructionFile));
    }

    /**
     * Checks if a file exists AND is non-empty.
     *
     * @param fileLocation String representation of the file's location (absolute or relative).
     * @return true if non-empty file exists
     */
    public static boolean checkFileExistence(String fileLocation) {
        if (fileLocation == null) return false;
        File f = new File(fileLocation);
        return f.exists() && f.getTotalSpace() != 0;
    }


    /**
     * convert file size in to human readable format
     *
     * @param bytes
     * @return String
     */
    public static String sizeToReadable(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Convert String's first character into uppercase
     *
     * @param original
     * @return
     */
    public static String uppercaseFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    /**
     * Convert miliseconds to human readable time format : 00 days 00 hrs 00 minutes 00 secs
     *
     * @param millis
     * @return
     */
    public static String getDurationReadable(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return sb.toString();
    }

    /**
     * Wizardry.
     * Uses C Library to (On Unix systems) get the PID of the current process.
     * @return Process ID
     *
    public static int getPID(){
    return CLibrary.Instance.getpid();
    }
    private interface CLibrary extends Library{
    CLibrary Instance = (CLibrary) Native.loadLibrary("c",CLibrary.class);
    int getpid();
    }*/
}
