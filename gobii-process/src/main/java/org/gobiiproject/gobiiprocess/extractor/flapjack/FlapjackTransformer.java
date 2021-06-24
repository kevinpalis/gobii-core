package org.gobiiproject.gobiiprocess.extractor.flapjack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.gobiiproject.gobiimodel.utils.HelperFunctions;
import org.gobiiproject.gobiimodel.utils.error.Logger;

import static org.gobiiproject.gobiimodel.utils.FileSystemInterface.*;

/**
 * Generates the output files for Flapjack import.
 * This class is split into generating the .map and .genotype files.
 */
public class FlapjackTransformer {

	/**
	 *
	 * @param markerFile marker file name *extended* if the extended flag is true (has map info)
	 * @param sampleFile sample file name
	 * @param chrLengthFile chromosome length file
	 * @param tempDir directory for temp (and output) files. This function does not distinguish between the two
	 * @param outFile output map file
	 * @param errorFile temporary file to redirect error stream to
	 * @param extended if the marker file is extended
	 * @return if successful (always true, ignore)
	 */
	public static boolean generateMapFile(String markerFile, String sampleFile, String chrLengthFile, String tempDir, String outFile, String errorFile, boolean extended) {
		/*
   			1) create the output header
   			2) remove the headers from chrLengthFile
			3) cut the columns related to the marker, the chromosome, and the position from markerFile
			4) remove the headers from such columns
			5) cat all the three files into the new output file
			Example for the marker file process: cut -f1,2,4 derp.txt | tail -n +2 > derp2.txt
		 */
		boolean status = true;
		boolean chrLengthsExists=new File(chrLengthFile).exists();
		status = invokeTryExec("echo # fjFile = MAP",tempDir+"map.header", errorFile, status);

		if(chrLengthsExists) {
			status = invokeTryExec("tail -n +2 "+chrLengthFile, tempDir+"map.chrLengths", errorFile, status);
		}
		String firstLineOfMarkerFile=getFirstLineOfFile(markerFile);
		String locations;
		//extended marker locations of Marker Name (1) Linkage Group Name(28) and MarkerLinkageGroupStart(31)
		String[] markerElements={"marker_name","linkage_group_name","marker_linkage_group_start"};

		locations=getSplitList(markerElements,firstLineOfMarkerFile,"\t");

		if(!extended)locations="1";
		status = invokeTryExec("cut -f"+locations+" "+markerFile,tempDir+"tmp",errorFile, status);//Marker Name, Linkage Group Name, Marker Linkage Group Start
		status = invokeTryExec("tail -n +2 "+tempDir+"tmp",tempDir+"map.body",errorFile, status);
		rm(tempDir+"tmp");
		
		status = invokeTryExec("cat "+tempDir+"map.header "+
						(chrLengthsExists?tempDir:"")+ (chrLengthsExists?"map.chrLengths ":"")+
						tempDir+"map.body", outFile, errorFile, status);

		rm(tempDir+"map.header");
		rmIfExist(tempDir+"map.chrLengths");
		rmIfExist(chrLengthFile);
		rm(tempDir+"map.body");

		return status;
	}

	/**
	 * Adds the qtl header to the qtl file
	 * @param qtlFilePos qtl file location (file at location is updated)
	 * @param tempDir place to store temporaries
	 * @param errorFile place to write errors to
	 * @return true on success, false on error
	 */
	public static boolean addFJQTLHeaderLine(String qtlFilePos, String tempDir, String errorFile){
		return addLineToFileAsHeader("# fjfile = qtl-gobii",qtlFilePos, tempDir, errorFile);
	}
	private static boolean addLineToFileAsHeader(String headerLine, String filePos, String tempDir, String errorFile){
		String headerLoc=tempDir+"header";
		String tempLoc=tempDir+"fileTmp";
		//original to temp
		mv(filePos,tempLoc);
		//create header
		boolean status=invokeTryExec("echo "+headerLine,headerLoc, errorFile, true);
		//combine into original place (making whole operation 'in-fix')
		status=invokeTryExec("cat "+headerLoc+" "+tempLoc, filePos, errorFile, status);

		//cleanup
		rm(headerLoc);
		rm(tempLoc);
		return status;
	}

	private static String getFirstLineOfFile(String filePath){
	String ret = "";
		try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
			ret=br.readLine();
		} catch (IOException e) {
			Logger.logError("FlapjackTransformer",e);
		}
		return ret;
	}

	/**
	 * Convenience method for getSplitList(String[],String,String)
	 * Also used for testing
	 * @param elements Comma-separated list of elements
	 * @param firstLine Line to get a split-command argument on
	 * @param delimiter delimiter of 'firstLine'
	 * @return outputList
	 */
	static String getSplitList(String elements, String firstLine, String delimiter){
		return getSplitList(elements.split(","),firstLine,delimiter);
	}
	private static String getSplitList(String[] elements, String firstLine, String delimiter) {
		return getSplitList(Arrays.asList(elements),firstLine,delimiter);
	}
	private static String getSplitList(List<String> elements, String firstLine, String delimiter){
		String splitList=null;
		String[] tokenizedLine=firstLine.split(delimiter,-1);
		List<String> tokenizedList=Arrays.asList(tokenizedLine);
		for(String element: elements){
			int pos = tokenizedList.indexOf(element);
			if(pos != -1) {
				pos++;//convert 0-index to 1-index
				//add this position to the split list
				if (splitList == null) {
					splitList = ""+pos;
				}
				else{
					splitList += ","+pos;
				}

			}
			else{
				Logger.logDebug("FlapjackTransformer","Missing column named "+ element + " in output file");
			}
		}
		if(splitList==null)splitList="";
		return splitList;
	}

	/**
	 * Generates a genotype file from the MDE outputs
	 * @param markerFile MDE output of markers
	 * @param sampleFile Sample MDE output file
	 * @param genotypeFile Genotype MDE output file
	 * @param tempDir Directory to write temporary files
	 * @param outFile Output genotype file
	 * @param errorFile Temporary file to write error logs to
	 * @return true on success
	 */
	public static boolean generateGenotypeFile(String markerFile, String sampleFile, String genotypeFile, String tempDir, String outFile,String errorFile){
		/*
		 *  Genotype file -
			1) create response file
  			   # fjFile = GENOTYPE
			2)cut marker names
			  transpose marker names
			3) attach to top of genotype matrix
			4) get sample names
			5) add blank in front of first name
			6) paste sample names to genotype file
			7) add response file to top
		 */
		//TODO: run slash adder on genotype file
		boolean status = true;
		String tempFile = tempDir + "tmp";
		String tempFile2 = tempDir + "tmp2";
		String markerList = tempDir + "genotype.markerList";
		String inverseMarkerList = tempDir + "genotype.markerIList";
		
		status = invokeTryExec("echo # fjFile = GENOTYPE",tempDir+"map.response",errorFile, status);
		status = invokeTryExec("echo ",tempDir+"blank.file",errorFile, status);
		
		//Markerlist contains all marker names, sequentially

		status = invokeTryExec("cut -f1 "+markerFile, tempFile,errorFile, status);
		status = invokeTryExec("tail -n +2 "+tempFile, markerList,errorFile, status);
		rm(tempFile);
		//Adds one line, then rotates 90 degrees, so each marker is a tab
		status = invokeTryExec("cat "+tempDir+"blank.file "+markerList, tempFile,errorFile, status);
		status = invokeTryExec("tr '\\n' '\\t'",tempFile2,errorFile, tempFile, status);//Input redirection wheeee

		//GSD-131 - there will be a trailing newline to the above pre-transformed file, so we need to remove the trailing tab. Sed -i for inline
		status = invokeTryExec("sed s/\\t$//", inverseMarkerList,errorFile,tempFile2,status);


		rm(tempFile);
		rm(tempFile2);
		//Note, this file has no line ending.
		
		//Sample list is all samples, no response
		status = invokeTryExec("cut -f1 "+sampleFile, tempFile,errorFile, status);
		status = invokeTryExec("tail -n +2 "+tempFile,tempDir+"genotype.sampleList",errorFile, status);
		rm(tempFile);
		
		status = invokeTryExec("paste "+ tempDir+"genotype.sampleList "+genotypeFile,tempDir+"sample.matrix",errorFile, status);//And now we have a matrix with the samples attached
		status = invokeTryExec("cat "+tempDir+"map.response "+ inverseMarkerList+" " + tempDir+"blank.file "+ tempDir+"sample.matrix",outFile,errorFile, status);

		rm(tempDir+"map.response");
		rm(markerList);
		rm(inverseMarkerList);
		rm(tempDir+"genotype.sampleList");
		rm(tempDir+"sample.matrix");
		rm(tempDir+"blank.file");
		
		return status;
	}
	
	/**
	 * Calls HelperFunctions.tryExec. If return status is false send false. Else sends the @param status back.
	 * @param execString
	 * @param errorFile
	 * @param status
	 * @return
	 */
	private static boolean invokeTryExec(String execString, String outputFile, String errorFile, String inputFile, boolean status) {
		// If return val
		if (HelperFunctions.tryExec(execString, outputFile, errorFile, inputFile) == true) {
			return status;
		} else {
			return false;
		}
	}

	/**
	 * Calls HelperFunctions.tryExec. If return status is false send false. Else sends the @param status back.
	 * @param execString
	 * @param outputFile
	 * @param errorFile
	 * @param status Status of execution till now.
	 * @return
	 */
	private static boolean invokeTryExec(String execString,String outputFile, String errorFile, boolean status){
		// If return val
		if (HelperFunctions.tryExec(execString, outputFile, errorFile) == true) {
			return status;
		} else {
			return false;
		}
	}
}
