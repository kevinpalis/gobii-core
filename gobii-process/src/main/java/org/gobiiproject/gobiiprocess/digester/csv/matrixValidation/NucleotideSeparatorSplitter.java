package org.gobiiproject.gobiiprocess.digester.csv.matrixValidation;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiimodel.utils.error.Logger;

import java.nio.CharBuffer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A separation routine for multi-allelic nucleotides for GDM-446. Takes a delimited string and converts it into an
 * undelimited N character string of alleles, with N being the number of alleles its looking for.
 */
public class NucleotideSeparatorSplitter implements RowProcessor {
    private static final String OUTPUT_SEPARATOR = "/";
    private int nucleotideCount;
    private static String UNKNOWN_ALLELE = "N";
    private String unknownSegment;

    private static Set<String> validSeparators = new HashSet<>(Arrays.asList(
            ",",
            "/",
            ":", //GSD-165 add : to valid separators
            ";", //As per 4/28/2020 standup, 165 needs semicolons as well
            "|"
    ));

    private Set<String> missingSegmentsFromFile;

    public NucleotideSeparatorSplitter(int nucleotideCount, Set<String> missingSegmentsFromFile){
        this.nucleotideCount = nucleotideCount;
        this.missingSegmentsFromFile = missingSegmentsFromFile;
        this.unknownSegment = String.join(OUTPUT_SEPARATOR,StringUtils.repeat(UNKNOWN_ALLELE,nucleotideCount).split(""));
    }

    public boolean process(int rowNo, List<String> inrow, List<String> outrow, MatrixErrorUtil matrixErrorUtil) {
        boolean returnStatus = true;
        for (String element : inrow) {

            if(element.contains("INS")){ //Replace INS with + for Intertek data, based on 4/30 convo
                element=element.replaceAll("INS","+");
            }

            if (element.length() < 1) {
                String errMsg = "SNPSepRemoval Unsupported empty Allele Call in row " + rowNo;
                matrixErrorUtil.setError(errMsg);
                returnStatus = false;
            } else {
                String result=unknownSegment;
                String error = null;
                if(missingSegmentsFromFile.contains(element.toLowerCase())){
                    //noinspection ConstantConditions - This is for readability
                    result = unknownSegment;
                }else{

                    //Override for one character in 2 letter - duplicate the letter GSD-166
                    if((element.length() == 1) && (nucleotideCount == 2)){
                        Logger.logDebug("NucleotideSeparatorSplitter","Found single letter homozygous in biallelic data, converted to two versions of it");
                        element = element+element;
                    }

                    error = validateInputElement(element);
                    if(error==null) {
                        try {
                            result = processInputElement(element);
                            error = validateOutputElement(result);
                        }
                        catch(Exception e){
                                error=e.getMessage();
                            }
                    }
                }
                outrow.add(result);

                if (error!=null) {
                    String baseErrMsg = error + "  in row " + rowNo;
                    matrixErrorUtil.setError(baseErrMsg);
                    returnStatus = false;
                }
            }
        }
        return returnStatus;
    }

    // Important assumptions - element is already whitespace stripped.
    //missingSegmentsFromFile is all lowercase, so 'contains' becomes case insensitive

    /**
     * Returns null on success, or error message on failure. Checks pre-split element maintains some basic characteristics.
     * @param element pre-processing input element
     * @return null on success, error message on failure
     */
    private String validateInputElement(String element){
        int expectedLengthWithSeparators = (nucleotideCount * 2) - 1;
        int length = element.length();



        boolean hasSeparators=(length != nucleotideCount);
        String separatorCharacter = findSeparator(element);
        if(separatorCharacter!=null) {
          String[] subelements= element.split(Pattern.quote(separatorCharacter));
          if(subelements.length != nucleotideCount){
              return "Unexpected Length Element " + element; // incorrect length
          }
        }
        else{
            if(element.length()!=nucleotideCount){
                return "Unexpected Length Element " + element; // incorrect length
            }
        }

        return null;
    }

    /**
     * Replaces separators from input elements
     * @param element input element
     * @return processed input element
     */
    private String processInputElement(String element) throws Exception {
        int length = element.length();
        boolean hasSeparators=((length != nucleotideCount));
        if(hasSeparators && length>1) { //If nucleotideCount=1, there's no separator character
            String separator = findSeparator(element);
            if(separator==null){
                throw new Exception("No separator found for element of unusual length " + element);
            }
            //Pattern.quote to escape literals like / and | being interpreted as regular expression syntax
            return element.replaceAll(Pattern.quote( separator ), OUTPUT_SEPARATOR);
        }
        //There are no separators, so each allele is 'naked'
        else if(length > 1){
            //Really complicated way of saying split out every character, put OUTPUT_SEPARATOR between them. What a one-liner, though...
            return String.join(OUTPUT_SEPARATOR, element.split(""));
        }
        else return element;
    }

    /**
     * From the list of valid potential separator characters, find the first one that matches
     * @param separatedString
     * @return
     */
    public static String findSeparator(String separatedString){
        for(String character:separatedString.split("")){
            if(validSeparators.contains(character))return character;
        }
        return null;
    }

    /**
     * Returns null on success, or error message on failure. Checks element after split is still the right length
     * @param element post-processing input element
     * @return null on success, error message on failure
     */
    private String validateOutputElement(String element) {
        //if (element.length() != nucleotideCount) {
         //   return "Unexpected Segment Count after split in " + element;
        //}
        return null;
    }
}