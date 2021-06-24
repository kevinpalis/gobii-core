package org.gobiiproject.gobiiprocess.digester.csv.matrixValidation;

import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiimodel.types.DataSetType;

class DigestMatrix {

    static boolean validateDatasetList(int lineNumber, List<String> rowList, String type, MatrixErrorUtil matrixErrorUtil) {
        AllowedEntities allowedEntities;
        DataSetType dataSetType = DataSetType.valueOf(type);
        boolean returnStatus = true;
        switch (dataSetType) {
            case NUCLEOTIDE_2_LETTER:
            case IUPAC:
            case VCF:
                allowedEntities = new AllowedNucleotides(2); //TODO - VCF 4 letter?
                break;
            case CO_DOMINANT_NON_NUCLEOTIDE:
                allowedEntities = new AllowedEntitiesSet(new HashSet<String>(initCoDominantList()));
                break;
            case DOMINANT_NON_NUCLEOTIDE:
                allowedEntities = new AllowedEntitiesSet(new HashSet<String>(initDominantList()));
                break;
          case NUCLEOTIDE_4_LETTER:
               allowedEntities = new AllowedNucleotides(4);
               break;
            case SSR_ALLELE_SIZE:
                allowedEntities = new AllowedSSRAlleleSize(8);
                break;
            default:
                matrixErrorUtil.setError("Validate Dataset Matrix. Invalid dataset type " + dataSetType);
                return false;
        }

        for (String element : rowList)
            if (!allowedEntities.isAllowed(element)) {
                matrixErrorUtil.setError("Validate Dataset Matrix Invalid data found in post-processed matrix line: " + lineNumber + " Data:" + element);
                returnStatus = false;
            }
        return returnStatus;
    }

    /***
     * Assign data in respective data types.
     */
    private static List<String> initNucleotide2letterList() {
        List<String> elements = new ArrayList<>(Arrays.asList("AA", "TT", "CC", "GG", "AT", "TA", "AG", "GA", "AC", "CA", "TG", "GT", "TC", "CT", "GC", "CG", "NN",
                "++", "--", "+-", "-+", "AN", "NA", "CN", "NC", "GN", "NG", "TN", "NT"));
        for (char c : "ACGTN".toCharArray()) {
            elements.add(c + "+");
            elements.add(c + "-");
            elements.add("+" + c);
            elements.add("-" + c);
        }
        return elements;
    }

    private static List<String> initDominantList() {
        return Arrays.asList("0", "1", "N");
    }

    private static List<String> initCoDominantList() {
        return Arrays.asList("0", "1", "2", "N");
    }

    private interface AllowedEntities{
        boolean isAllowed(String entity);
    }

    private static class AllowedEntitiesSet implements AllowedEntities{
        private Set<String> elements;
        private AllowedEntitiesSet(Set<String> elements){
            this.elements = elements;
        }

        @Override
        public boolean isAllowed(String entity) {
            return elements.contains(entity);
        }
    }
    private static class AllowedNucleotides implements AllowedEntities{
        private int numberOfElements;
        Set<String> allowedSet = new HashSet<String>(Arrays.asList("A","C","G","T","N","+","-"));
        private AllowedNucleotides(int numberOfElements){
            this.numberOfElements = numberOfElements;
        }
        @Override
        public boolean isAllowed(String entity) {
            //There are the right number of elements
            if(!entity.contains("/")) {
                if (entity.length() != numberOfElements) return false;
                //Each element is a valid element
                for (String s : entity.split("")) {
                    if (!allowedSet.contains(s)) {
                        return false;
                    }
                }
                return true;
            }
            String[] subEntities = entity.split(Pattern.quote("/"),-1);
            if(subEntities.length != numberOfElements){
                return false;
            }
            for(String subEntity:subEntities){
                if(subEntity.length()>0) {
                    for (String s : subEntity.split("")) {
                        if (!allowedSet.contains(s)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }
    private static class AllowedSSRAlleleSize implements AllowedEntities{
        private int numberOfElements;
        private AllowedSSRAlleleSize(int numberOfElements){
            this.numberOfElements = numberOfElements;
        }
        @Override
        public boolean isAllowed(String entity) {
            //There are the right number of elements
            if(entity.length()!=numberOfElements) return false;
            //Right number of elements and is numeric
            return StringUtils.isNumeric(entity);
        }
    }
}
