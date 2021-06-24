package org.gobiiproject.gobiiprocess;

import htsjdk.variant.variantcontext.Allele;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiiprocess.digester.csv.matrixValidation.NucleotideSeparatorSplitter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class designed to work with @HDF5Interface to encode and then decode insertions, deletions, and other non-standard or
 * non-allelic data, cleaning it to an allele and encoded-non-standard only HDF5 and a lookup table with a row for each
 * marker to the dataset
 */
public class HDF5AllelicEncoder {
    private static final String DEFAULT_SEPARATOR="/";
    /**
     * @param inputRow         entire input row as a string
     * @param alleleSeparator  Separator between alleles (usually /). If null, each element will get a separator based on NucleotideSeparatorSplitter's default logic
     * @param elementSeparator Separator between elements (usually tab)
     * @return A pair of values, the data entry, and the inner value of the lookup table entry
     */
    public static EncodedValues encodeRow(String inputRow, String alleleSeparator, String elementSeparator) throws Exception {
        StringBuilder outRow=new StringBuilder();
        RowTranslator currentRowTranslator = new RowTranslator();

        boolean first=true;
        for(String element:inputRow.split(Pattern.quote(elementSeparator))){
            if(!first){
                outRow.append(elementSeparator);
            }
            else{
                first=false;
            }
            //In case first element is an unknown, or otherwise oddball, check for each element if we don't know beforehand (a usual case)
            if(alleleSeparator == null){
                alleleSeparator = NucleotideSeparatorSplitter.findSeparator(element);
            }
            outRow.append(currentRowTranslator.getEncodedString(element,alleleSeparator,false));
        }
        return new EncodedValues(outRow.toString(),currentRowTranslator.encodeRowTransform());
    }

    static class EncodedValues{
        public String encodedRow;
        public String lookupRow;
        public EncodedValues(String encodedRow,String lookupRow){
            this.encodedRow=encodedRow;
            this.lookupRow=lookupRow;
        }
    }

    /**
     * Encodes an entire file using encodeRow on each row of the input file, generating a lookup file
     * @param originalFilePath The Bare input to convert to an encoded file
     * @param encodedFilePath Where the encoded file of fixed width characters (the one to HDF5) should go
     * @param lookupFilePath Where the lookup file should go
     * @param alleleSeparator If null, uses the builtins to determine separator for each element. See NucleotideSeparatorSplitter.getSeparator()
     * @param elementSeparator Usually tab, but put in for strange formats, what the between-value separator is
     */
    public static void createEncodedFile(File originalFilePath, File encodedFilePath, File lookupFilePath, String alleleSeparator, String elementSeparator){
        try(BufferedReader input = new BufferedReader(new FileReader(originalFilePath));
            //In theory, this should prevent any encoding issues when sent to the raw HDF5 interface
            OutputStreamWriter encodedFile = new OutputStreamWriter( new BufferedOutputStream(new FileOutputStream(encodedFilePath)),StandardCharsets.UTF_8);
            OutputStreamWriter lookupFile = new OutputStreamWriter( new BufferedOutputStream(new FileOutputStream(lookupFilePath)))){

            //Note - line by line parse doesn't seem to have too much of a performance impact, oddly enough. See: https://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java/40597140#40597140
            String inputLine;
            int i = 0;
            while((inputLine=input.readLine()) != null) {
                EncodedValues ret = encodeRow(inputLine,alleleSeparator,elementSeparator);

                encodedFile.write(ret.encodedRow);
                encodedFile.write(System.lineSeparator());//Eww.

                //Lookup file is sparse, and may not exist if there are no entries at all
                if(ret.lookupRow.length()>0) { //we have a row in the lookup table
                    lookupFile.write(i+"\t"+ret.lookupRow);
                    lookupFile.write(System.lineSeparator());
                }
                i++;
            }
        }catch (Exception e) {
            Logger.logError("HDF5AllelicEncoder", "Error encoding allele data in HDF5 file",e);
        }
    }

    /**
     * Decodes and adds separators to the output of an HDF5 matrix
     * @param encodedFilePath
     * @param lookupFilePath
     * @param decodedFilePath
     * @param alleleSeparator
     * @param elementSeparator
     */
    public static void createDecodedFile(File encodedFilePath, File lookupFilePath, File decodedFilePath, String alleleSeparator, String elementSeparator){

        BufferedReader lookupFile = null;
        try {
            lookupFile = new BufferedReader(new FileReader(lookupFilePath));
        }catch(Exception e){
            //no lookup file
        }
        try(
                BufferedReader encodedFile = new BufferedReader(
                        new InputStreamReader(new FileInputStream(encodedFilePath), StandardCharsets.UTF_8));
                BufferedWriter decodedFile = new BufferedWriter( new FileWriter(decodedFilePath));
        ){
            //Note - line by line parse doesn't seem to have too much of a performance impact, oddly enough. See: https://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java/40597140#40597140

            String lookupLine=lookupFile!=null?lookupFile.readLine():null;
            String inputLine;
            int i = 0;
            int nextLookupLineRow = lookupLine!=null?Integer.parseInt(lookupLine.split(Pattern.quote("\t"))[0]):-1;
            while((inputLine=encodedFile.readLine()) != null) {
                RowTranslator currentRowTranslator = null;
                //advance through the lookup line to find 'this' line if we're behind
                if(lookupLine!=null && nextLookupLineRow<i){
                    lookupLine=lookupFile.readLine();
                    if(lookupLine != null) {
                        try {
                            nextLookupLineRow = Integer.parseInt(lookupLine.split(Pattern.quote("\t"))[0]);
                        } catch (Exception e) {
                            Logger.logError("HDF5AllelicEncoder", "Invalid lookup table row found when looking for line " + i);
                        }
                    }
                }



                if(lookupLine != null && nextLookupLineRow == i) {
                    decodedFile.write(decodeRow(inputLine,lookupLine,alleleSeparator,elementSeparator));
                }
                else{ //Even if there's nothing to translate, the row still needs to have separators applied, so it gets to go through the sausage factory... Less code paths, but could be optimized
                    decodedFile.write(decodeRow(inputLine,null,alleleSeparator,elementSeparator));
                }
                decodedFile.write(System.lineSeparator());

                i++;
            }
        } catch (Exception e) {
            Logger.logError("HDF5AllelicEncoder", "Error decoding encoded HDF5 file",e);
        }
    }

    //This one's slightly different, as we need to align the output with the input marker list. As each line is currently a marker, we
    // can assume that the lines correspond to the nth entries in the 'marker list', which should be a zero based list of increasing numbers
    //corresponding to rows in the translation table.
    public static void createDecodedFileFromList(File encodedFilePath, File lookupFilePath, String posList, File decodedFilePath, String alleleSeparator, String elementSeparator){
        Iterator<String> posListIterator = Arrays.stream(posList.split(",")).iterator();

        BufferedReader lookupFile = null;
        try {
            lookupFile = new BufferedReader(new FileReader(lookupFilePath));
        }catch(Exception e){
            //no lookup file
        }
        try(
                BufferedReader encodedFile = new BufferedReader( new FileReader(encodedFilePath));
                BufferedWriter decodedFile = new BufferedWriter( new FileWriter(decodedFilePath));
        ){
            //Note - line by line parse doesn't seem to have too much of a performance impact, oddly enough. See: https://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java/40597140#40597140

            String lookupLine=lookupFile!=null?lookupFile.readLine():null;
            String inputLine;
            int nextLookupLineRow = lookupLine!=null?Integer.parseInt(lookupLine.split(Pattern.quote("\t"))[0]):-1;
            while((inputLine=encodedFile.readLine()) != null && posListIterator.hasNext()) {
                int i = Integer.parseInt(posListIterator.next());
                RowTranslator currentRowTranslator = null;
                //advance through the lookup line to find 'this' line if we're behind
                while(lookupLine!=null && nextLookupLineRow<i){
                    lookupLine=lookupFile.readLine();
                    if(lookupLine != null) {
                        try {
                            nextLookupLineRow = Integer.parseInt(lookupLine.split(Pattern.quote("\t"))[0]);
                        } catch (Exception e) {
                            Logger.logError("HDF5AllelicEncoder", "Invalid lookup table row found when looking for line " + i);
                        }
                    }
                }

                if(lookupLine != null && nextLookupLineRow == i){
                    currentRowTranslator = new RowTranslator(lookupLine);
                }
                else{ //Even if there's nothing to translate, the row still needs to have separators applied, so it gets to go through the sausage factory... Less code paths, but could be optimized
                    decodedFile.write(decodeRow(inputLine,null,alleleSeparator,elementSeparator));
                }

                if(currentRowTranslator != null) {
                    decodedFile.write(decodeRow(inputLine,lookupLine,alleleSeparator,elementSeparator));
                }
                else{ //If there's nothing in the row to translate, just copy over. Simple as that, save some time and effort
                    decodedFile.write(inputLine);
                }
                decodedFile.write(System.lineSeparator());
            }
        } catch (Exception e) {
            Logger.logError("HDF5AllelicEncoder", "Error decoding encoded HDF5 file",e);
        }
    }



    public static String decodeRow(String inputRow, String lookupRow, String alleleSeparator, String elementSeparator) throws Exception{
        StringBuilder outRow=new StringBuilder();
        RowTranslator currentRowTranslator = new RowTranslator(lookupRow);

        boolean first=true;
        for(String element:inputRow.split(Pattern.quote(elementSeparator))){
            if(!first){
                outRow.append(elementSeparator);
            }
            else{
                first=false;
            }
            outRow.append(currentRowTranslator.getDecodedString(element,alleleSeparator));
        }
        return outRow.toString();
    }

    static class RowTranslator {
        List<String> nonstandardAlleles = new ArrayList<String>();
        HashSet<String> nonstandardAlleleMap = new HashSet<String>();
        HashSet<String> unencodedAlleles = new HashSet<String>(Arrays.asList("A", "C", "G", "T","N","0","1","2","+","-",".","?"," "));

        /**
         * Create a RowTranslator from an 'encoded translator' key line
         * @param encodedRow
         */
        RowTranslator(String encodedRow) {
            if(encodedRow!=null) {
                decode(encodedRow);
            }
        }

        //An empty one
        RowTranslator(){

        }

        /**
         * Create a RowTranslator from a row of input matrix
         * @param inputRow
         */
        RowTranslator(List<String> inputRow) {
            for(String val:inputRow){
                if(val.contains(DEFAULT_SEPARATOR)){//for now assume the 2 let is coming in with /, all upstreams coded to match
                    for(String inner:val.split(Pattern.quote(DEFAULT_SEPARATOR))){
                        orderNonstandardAllele(inner);
                    }
                }
                else{
                    orderNonstandardAllele(val);
                }
            }
        }


        //decodes a string encoded with 'encode'
        void decode(String encodedRow) {
            String namelessRow = encodedRow.substring(encodedRow.indexOf('\t')+1);//remove everything before first tab, including the tab
            String[] segments = namelessRow.split(";", -1);
            for (String segment : segments) {
                String cleanSegment = cleanSegment(segment);
                orderNonstandardAllele(cleanSegment);
            }
        }

        //returns any insertion segment
        /*static*/ String cleanSegment(String segment) {
            switch (segment.charAt(0)) {
                case 'I':
                    if (segment.length() > 1) {
                        return segment.substring(1);
                    } else {
                        return null;
                    }
                case 'D': {
                    return "";
                }
                default:
                    return null;
            }
        }

        //Encodes the current state of the rowreader into a 'marker row' for data encoded in this marker
        String encodeRowTransform() {
            //Every nonstandard allele is prepended with an "I" for futureproofing
            return nonstandardAlleles.stream().map(this::encodeRowEntry).collect(Collectors.joining(";"));
        }


        private String encodeRowEntry(String nonstandardAlleleValue){
            if(nonstandardAlleleValue.length()==0){
                return "D";
            }
            return "I"+nonstandardAlleleValue;
        }

        //Add a cannonical number to a novel base string
        void orderNonstandardAllele(Allele a) {
            orderNonstandardAllele(a.getBaseString());
        }

        void orderNonstandardAllele(String baseString) {
            if (unencodedAlleles.contains(baseString) || nonstandardAlleleMap.contains(baseString)) {
                return;
            }
            nonstandardAlleles.add(baseString);
            nonstandardAlleleMap.add(baseString);
        }

        /**
         * Encodes a 'single' allele into an HDF5 friendly format.
         * For example, if a 2 letter type is ACCAT/A, getEncodedValue(ACCAT) ->  ‚ (131)
         * getEncodedValue(A) -> A (41)
         * @param allele 'allele', insertion or deletion to encode
         * @return
         */
        char getEncodedAllele(String allele, boolean strict) throws Exception{
            if(nonstandardAlleleMap.contains(allele)){
                return encodeChar(nonstandardAlleles.indexOf(allele));
            }
            else if(unencodedAlleles.contains(allele)) {
                return allele.charAt(0);
            }
            else if(strict){
                throw new Exception("Allele to encode was not part of this transform row");
            }
            else {
                //Add this allele number to the numbered list at the end and go again. Weird tail recursion, but
                //gets the job done
                orderNonstandardAllele(allele);
                return getEncodedAllele(allele, false);
            }
        }

        /**
         * Encodes a genotype string with a separator. Separator specified as second parameter.
         * All encoded values are squashed to one character, and that character
         *  For example, if a 2 letter type is ACCAT/A, with a separator specified of / ->  ‚ (131) A (41)
         *  ,A becomes the encoded string
         * @param input
         * @param separator
         * @param strict true if the RowTranslator should fail if given an unknown value
         * @return
         */
        String getEncodedString(String input, String separator, boolean strict) throws Exception{
            String[] segments=input.split(Pattern.quote(separator));
            String output="";//Not a stringbuilder as it's only 2, maybe 4 characters long. Schmiel the painter doesn't have far to go
            for(String segment: segments){
                output+=getEncodedAllele(segment,strict);
            }
            return output;
        }

        /**
         * "Unpacks" the encoded single-character to allele stream, adding separators in the middle if there is more than one element
         *  For example, if   ‚ (131) A (41) is the input, (131) goes into the lookup table of ACCAT, and (41) A
         *  is in the unencoded alleles, it comes out A. Given a / separator, it becomes ACCAT/A
         *
         * @param encodedString
         * @param separator
         * @return
         * @throws Exception
         */
        String getDecodedString(String encodedString, String separator) throws Exception{
            //Special override for this datatype
            if(encodedString.length() == 8 && encodedString.matches("[0123456789]*")){
                return encodedString.substring(0,4) + separator + encodedString.substring(4,8);
            }
            StringBuilder out = new StringBuilder();
            boolean first=true;
            for(char encodedAllele:encodedString.toCharArray()){
                if(!first){
                    out.append(separator);
                }else{
                    first=false;
                }
                out.append(getDecodedAllele(""+encodedAllele));
            }
            return out.toString();
        }

        String getDecodedAllele(String internalAllele) throws Exception{
            if(internalAllele.length()>1) throw new Exception("More than one character in parse");
            if(unencodedAlleles.contains(internalAllele)){
                return internalAllele;
            }
            char internalChar=internalAllele.charAt(0);

            int index;
            try {
                index = decodeChar(internalChar);
            }
            catch(Exception e){
                //Throw a warning, but pass through the character on catastrophic decode failure. (Probably an unexpected unencoded value, not an encoded value at all.)
                index = -1;
                Logger.logWarning("HDF5AlleleEncoder","No character to decode to- '" + internalAllele + "'");
                return internalAllele;
            }
            if(index>=nonstandardAlleles.size()){
                throw new Exception("Decoded character from h5 does not map (over translator length) " + index);
            }
            return nonstandardAlleles.get(index);
        }
    }

    private static char encodeChar(int value){
        return (char)(value+129);
    }

    private static int decodeChar(char value) throws Exception{
        int intVal=(int)value;
        if(intVal < 129) throw new Exception("Decoded character from h5 does not map (under 128)");
        return ((int)value)-129;
    }
}
