package org.gobiiproject.gobiiprocess.digester.csv.matrixValidation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiiprocess.digester.LoaderGlobalConfigs;

public class MatrixValidation {

    private final String datasetType, missingFile, markerFile;
    private int noOfElements;
    private IUPACmatrixToBi iupacMatrixToBi;
    private RowProcessor diploidProcessor;
    private MatrixErrorUtil matrixErrorUtil;
    private NucleotideSeparatorSplitter tetraploidSplitter;
    private Iterator<String> vcfRawFileOutput;

    public MatrixErrorUtil getMatrixErrorUtil() {
        return matrixErrorUtil;
    }

    public int getErrorCount() {
        return matrixErrorUtil.getErrorCount();
    }

    public boolean stopProcessing() {
        return matrixErrorUtil.stopProcessing();
    }

    public MatrixValidation(String datasetType, String missingFile, String markerFile) {
        this.datasetType = datasetType;
        this.missingFile = missingFile;
        this.markerFile = markerFile;
        matrixErrorUtil = new MatrixErrorUtil();
        noOfElements = 0;
        iupacMatrixToBi = new IUPACmatrixToBi();
    }


    public boolean setUp() {
        List<String> missingFileElements = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(missingFile))) {
            stream.forEach(missingFileElements::add);
        } catch (IOException e) {
            matrixErrorUtil.incrementErrorCount();
            Logger.logError("SNPSepRemoval", "Exception in reading SNSSepRemoval missing values.");
            return false;
        }

        //Set of missing file entries, in lowercase to enable easy case insensitive mapping
        Set<String> missingFileSet = missingFileElements.stream().map(String::toLowerCase).collect(Collectors.toSet());

        diploidProcessor =  new NucleotideSeparatorSplitter(2,missingFileSet);

	    tetraploidSplitter = new NucleotideSeparatorSplitter(4,missingFileSet);
        return true;
    }

    public ValidationResult validate(int rowNo, int rowOffset, List<String> inputRowList, List<String> outputRowList, boolean isVCF, boolean skipValidation ) {
        ValidationResult ret = new ValidationResult(false,0);
        if (noOfElements == 0) noOfElements = inputRowList.size();
        else if (noOfElements != inputRowList.size()) {
            matrixErrorUtil.setError("Exception in processing matrix file. Irregular size matrix. Expected: " + noOfElements + " actual: " + inputRowList.size());
            return ret.success(false);
        }

        ret.numRows=noOfElements;

        /*
         *   IUPAC conversion
         *   Tried to convert IUPAC to BI.
         *   If it fails for a row that semi processed row is added.
         *   Reason being, as it is already a failure we are processing further to identify all errors once.
         *
         * */
	    if (datasetType.equalsIgnoreCase("IUPAC"))
		    if (!iupacMatrixToBi.process(rowNo + rowOffset, inputRowList, outputRowList, matrixErrorUtil)) {
			    return ret.success(false);
		    }
      
	    if (datasetType.equalsIgnoreCase("NUCLEOTIDE_4_LETTER")){
		    if (!tetraploidSplitter.process(rowNo + rowOffset, inputRowList, outputRowList, matrixErrorUtil)) {
			    return ret.success(false);
		    }
	    }
        /*
         *   SNP sep removal
         *   Tried SNP Separator Removal.
         *   If it fails for a row that semi processed row is added.
         *   Reason being, as it is already a failure we are processing further to identify all errors once.
         *
         * */
        if (datasetType.equalsIgnoreCase("NUCLEOTIDE_2_LETTER") && !isVCF)
            if (!diploidProcessor.process(rowNo + rowOffset, inputRowList, outputRowList, matrixErrorUtil)) {
                return ret.success(false);
            }

        if (isVCF) {
            //Now a passthrough as of HTSJDK
            outputRowList.addAll(inputRowList);
        }

        /*
         * VALIDATION.
         * Validates each element is a valid value or not based on the datasetType.
         * */
        if (datasetType.equalsIgnoreCase("NUCLEOTIDE_4_LETTER") || datasetType.equalsIgnoreCase("DOMINANT_NON_NUCLEOTIDE") || datasetType.equalsIgnoreCase("IUPAC") ||
                datasetType.equalsIgnoreCase("CO_DOMINANT_NON_NUCLEOTIDE") || datasetType.equalsIgnoreCase("SSR_ALLELE_SIZE") ||
                datasetType.equalsIgnoreCase("NUCLEOTIDE_2_LETTER") || datasetType.equalsIgnoreCase("VCF")) {

            //Pass through to output
            if (datasetType.equalsIgnoreCase("DOMINANT_NON_NUCLEOTIDE") || datasetType.equalsIgnoreCase("CO_DOMINANT_NON_NUCLEOTIDE") || datasetType.equalsIgnoreCase("SSR_ALLELE_SIZE"))
                outputRowList.addAll(inputRowList);

            if(skipValidation){
                return ret.success(true);
            }

            boolean datasetValidationStatus=DigestMatrix.validateDatasetList(rowNo + rowOffset, outputRowList, datasetType, matrixErrorUtil);
            return ret.success(datasetValidationStatus);
        } else return ret.success(false);
    }
}

/**
 * Quick method to turn a line reader into an interator. ANY read error effectively equals EoF.
 */
class BufferedReaderIteratorReader implements Iterator<String>{
    BufferedReader br;
    String nextLine;
    private String readNextLine(){
        try{
            return br.readLine();
        } catch(Exception e){
            return null;
        }
    }
    BufferedReaderIteratorReader(BufferedReader br){
        this.br=br;
        this.nextLine=readNextLine();
    }
    @Override
    public boolean hasNext() {
        return nextLine!=null;
    }
    @Override
    public String next() {
        String ret = nextLine;
        nextLine=readNextLine();
        return ret;
    }
    @Override
    public void remove() {
      nextLine=readNextLine();
    }
    @Override
    public void forEachRemaining(Consumer<? super String> action) {
        while(hasNext()){
            action.accept(next());
        }
    }
}


