package org.gobiiproject.gobiiprocess.digester;
import org.apache.commons.cli.*;
public class LoaderGlobalConfigurations{
    private LoaderGlobalConfigurations(){}
    private static boolean singleThreadFileRead=false;
    private static boolean versionOneRead=false;


    /**
     * Adds options to an Options object which will be read in 'setFromFlags'.
     */
    static void addOptions(Options o){
        o.addOption("str", "singleThreadRead", false, "Use a single thread for file reading");
        o.addOption("v1r","version1Read", false, "Use old (version 1) CSVFileReader");
    }
    static void setFromFlags(CommandLine cli){
        if(cli.hasOption("singleThreadRead")) singleThreadFileRead=true;
        if(cli.hasOption("version1Read")) versionOneRead=true;
    }

    public static boolean getSingleThreadFileRead(){
        return singleThreadFileRead;
    }
    public static boolean getVersionOneRead(){
        return versionOneRead;
    }
}