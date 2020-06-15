 package org.gobiiproject.gobiiprocess.digester.HelperFunctions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiimodel.utils.FileSystemInterface;
import org.gobiiproject.gobiimodel.utils.error.Logger;

/**
 * PGArray method converts takes an input and output file and converts a column inside it into an array.
 * @author Yaw Nti'addae
 * @author Josh Lamos-Sweeney - cleanup of file processing, conversion into routine instead of standalone
 */
public class PGArray {

    private int columnIndex = -1;
    private String strColumn;
    private String strMarkerFile;
    private String strOutput;

    /**
     * Converter of a single file column into a json array format for loading into postgres jsonb columns.
     * Takes the 'strColumn' column and converts it to a jsonb array, using any non-alphabetic characters as delimiters
     * @param strMarkerFile Input file
     * @param strOutput Output file
     * @param strColumn name of column to be converted into an array format
     */
    public PGArray(String strMarkerFile, String strOutput, String strColumn){
        this.strMarkerFile = strMarkerFile;
        this.strOutput = strOutput;
        this.strColumn = strColumn;
    }

    public void process(){
        try {
            Scanner input = new Scanner(new File(strMarkerFile));
            if(!input.hasNext()){
                Logger.logError("PGArray", "File " + strMarkerFile + " is missing or empty");
                return;//Fail fast
            }
            String strHeader = input.nextLine();
            String[] arrHeader = strHeader.split("\t");
            for(int i=0; i<arrHeader.length; i++){
                if(arrHeader[i].toUpperCase().equals(strColumn.toUpperCase())){
                    columnIndex = i;
                    break;
                }
            }
            if(columnIndex == -1){
                //Create output file by moving input file.
                FileSystemInterface.mv(strMarkerFile,strOutput);
                return;
            }

            File out = new File(strOutput);
            BufferedWriter writer = new BufferedWriter(new FileWriter(out));
            writer.write(strHeader);
            writer.newLine();
            while(input.hasNextLine()){
                String line = input.nextLine();
                String[] arr = line.split("\t");
                arr[columnIndex] = delimiterSeparatedStringToPgArrayString(arr[columnIndex]);
                writer.write(StringUtils.join(arr, "\t"));
                writer.newLine();
            }
            input.close();
            writer.close();
        } catch (FileNotFoundException err) {
            Logger.logError("PGArray","Error processing converting column to array file.",err);

        } catch (Exception err) {
            Logger.logError("PGArray","Error processing converting column to array file.",err);
        }
    }

    public static String delimiterSeparatedStringToPgArrayString(String dss){
        return String.format("{%s}", Arrays.stream(dss.split("[^0-9A-Za-z\\-+.]"))
                                           .filter(StringUtils::isNotEmpty)
                                           .map(m -> String.format("\"\"%s\"\"", m))
                                           .collect(Collectors.joining(",")));
    }
}
