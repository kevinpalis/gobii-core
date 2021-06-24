package org.gobiiproject.gobiiprocess.vcfinterface;
import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.CloseableTribbleIterator;
import htsjdk.tribble.FeatureCodec;
import htsjdk.tribble.TribbleException;
import htsjdk.tribble.example.CountRecords;
import htsjdk.tribble.index.Index;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class HTSInterface {
    public static void main(String[] args) throws IOException {
        String filetoread="C:\\Users\\jdl232.RS-BTOM1YJDL232\\Downloads\\" +"notareal.vcf";
        if(args.length > 0){
            filetoread=args[0];
        }
        File f = new File( filetoread);
        getAllVariantsFromFile(f);
    }
    static void getAllVariantsFromFile(File f) throws IOException {
        FeatureCodec<VariantContext, LineIterator> codec = new VCFCodec();
        Index index = CountRecords.loadIndex(f, codec);
       AbstractFeatureReader<VariantContext, LineIterator> reader = AbstractFeatureReader.getFeatureReader(f.getAbsolutePath(),codec, index);
       CloseableTribbleIterator<VariantContext> ctr = reader.iterator();
       for(VariantContext vc:ctr){
           System.out.print(vc.getID() + "\t");
           System.out.print(vc.getReference() + "|" + Arrays.deepToString(vc.getAlternateAlleles().toArray()) + "\t");
            for(Genotype gc:vc.getGenotypes()) {
                boolean innerfirst=true;
                for (Allele a : gc.getAlleles()) {
                    if(innerfirst)innerfirst=false;
                    else System.out.print("\\");
                    System.out.print(a.getBaseString());
                }
                System.out.print("\t");
            }
           System.out.println();
       }

    }

    static void writeVariantOnlyFile(File inFile, File outFile) throws Exception{
        writeVariantOnlyFile(inFile,outFile,"/","\t", false);
    }


    /** 'file' handle for vcf file read stream into HTS interface for legacy validation*/
    private static CloseableTribbleIterator<VariantContext> ctr;
    public static void setupVariantOnlyInputLine(File inFile) throws IOException{
        FeatureCodec<VariantContext, LineIterator> codec = new VCFCodec();
        Index index = CountRecords.loadIndex(inFile, codec);
        AbstractFeatureReader<VariantContext, LineIterator> reader = AbstractFeatureReader.getFeatureReader(inFile.getAbsolutePath(),codec, index);
        ctr = reader.iterator();
    }

    /**
     * NOTE: REQUIRES setupVariantOnlyInputLine to be called first
     * @param variantSeparator
     * @return
     */
    public static List<String> getVariantOnlyInputLine( String variantSeparator){
        if(ctr==null || (!ctr.hasNext())){
            return null;//no new line
        }
        //Return list of alleles with separator sepcified
        return ctr.next().getGenotypes().stream().map(x ->formattedAlleleString(x,variantSeparator)).collect(Collectors.toList());
    }

   public static void writeVariantOnlyFile(File inFile, 
                                           File outFile, 
                                           String variantSeparator, 
                                           String segmentSeparator,
                                           boolean append
   ) throws IOException, TribbleException {

        FeatureCodec<VariantContext, LineIterator> codec = new VCFCodec();
        Index index = CountRecords.loadIndex(inFile, codec);

        AbstractFeatureReader<VariantContext, LineIterator> reader = 
            AbstractFeatureReader.getFeatureReader(inFile.getAbsolutePath(),codec, index);

        CloseableTribbleIterator<VariantContext> ctr = reader.iterator();
        
        FileOutputStream fos = new FileOutputStream(outFile, append);

        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        PrintWriter printWriter= new PrintWriter(bos);
        
        boolean firstLine=true;
        for(VariantContext vc:ctr){
            if(firstLine){
                firstLine=false;
            }
            else{
                printWriter.println();
            }
            boolean lineFirst=true;
            for(Genotype gc:vc.getGenotypes()) {
                if(lineFirst){
                    lineFirst=false;
                }
                else{
                    printWriter.print(segmentSeparator);
                }
                printWriter.print(formattedAlleleString(gc, variantSeparator));
            }
        }

        printWriter.flush();

        printWriter.close();
    }


    /**
     * Returns a string representation of the alleles with separators, separated by the 'variantseparator'
     * Example: If the genotype has four alleles, ACGT  A  A  . , and the separator is '/', the resulting
     * string would be ACGT/A/A/.
     * @param variantSeparator Separator between genotypes
     * @param gc Genotype object to convert alleles from
     */
    private static String formattedAlleleString(Genotype gc,String variantSeparator) {
      return gc.getAlleles().stream()
              .map(Allele::getBaseString).map(HTSInterface::formatBaseStringToChar)
              .collect(Collectors.joining(variantSeparator));
    }

    private static String MISSING_ALLELE="N";
    private static String formatBaseStringToChar(String baseString){
        if(baseString.length() < 1) return MISSING_ALLELE; //Empty
        //If BaseString is > 1, is Indel
        return baseString;
    }


    /**
     * Convenience method for converting an entire variant context into formattedAlleleStringLists.
     * Useful to pretend to be the 'input row' of a CSVFileReader read.
     * @param vc VariantContext HTSJDK object
     * @param variantSeparator separator used by the formattedAlleleString() call
     * @return List of formatted allele strings representing an entire variant context 'row'
     */
    private static List<String> formattedAlleleStringList(VariantContext vc,String variantSeparator){
        return vc.getGenotypes().stream().map(x -> formattedAlleleString(x,variantSeparator)).collect(Collectors.toList());
    }


    static void attemptToReadFile(File inFile) throws IOException {
        try {
            FeatureCodec<VariantContext, LineIterator> codec = new VCFCodec();
            Index index = CountRecords.loadIndex(inFile, codec);
            AbstractFeatureReader.getFeatureReader(inFile.getAbsolutePath(), codec, index);
        }catch(Exception e){
            System.out.println("Error found while parsing:  " + e.getMessage());
        }
        System.out.println("Input file seems to be in order");
    }
}