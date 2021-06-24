package org.gobiiproject.gobiiprocess.digester.csv.matrixValidation;


import org.gobiiproject.gobiiprocess.digester.csv.matrixValidation.DigestMatrix;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@SuppressWarnings("unused")
public class DigestMatrixValidationTest {

    boolean runnerHadError=false;
    List<String> runnerErrorMessage = new ArrayList<String>();

    List<String> output = new ArrayList<String>();

    //Override setError to capture error messages
    MatrixErrorUtil dummy = new MatrixErrorUtil(){
        public void setError(String s){
            runnerHadError=true;
            runnerErrorMessage.add(s);
        }
    };

    private void resetTest(){
        runnerHadError=false;
        runnerErrorMessage = new ArrayList<String>();

    }


    @Test
    public void testNormalCase(){
        resetTest();
        List<String> input = Arrays.asList("AAAA","ACGT","CCGT","GTTT","ACGT");
        DigestMatrix.validateDatasetList(0,input,"NUCLEOTIDE_4_LETTER",dummy);
        noErrorsExpected();
    }
    @Test
    public void testNormalSlashCase(){
        resetTest();
        List<String> input = Arrays.asList("A/A/A/A","A/C/G/T","C/C/G/T","G/T/T/T","A/C/G/T");
        DigestMatrix.validateDatasetList(0,input,"NUCLEOTIDE_4_LETTER",dummy);
        noErrorsExpected();

        //System.out.println("AAAA contains \\"+"A/A/A/A".contains("/"));

        //String[] subEntities = "A/A/A/A".split(Pattern.quote("/"));
        //System.out.println(Arrays.deepToString(subEntities));
    }

    @Test
    public void testNormalIndelCase(){
        resetTest();
        List<String> input = Arrays.asList("A/A/A/ACCAT","A/CTTCT/G/T","C//G/T","G/T/T/T","A/C/G/T");
        DigestMatrix.validateDatasetList(0,input,"NUCLEOTIDE_4_LETTER",dummy);
        noErrorsExpected();
    }
    @Test
    public void testNormalTwoLetterCase(){
        resetTest();
        List<String> input = Arrays.asList("T/T","T/T","C/","G/T","A/C");
        DigestMatrix.validateDatasetList(0,input,"NUCLEOTIDE_2_LETTER",dummy);
        noErrorsExpected();
    }






    private void noErrorsExpected(){
        String firstMessage = "no messagte";
        if(runnerErrorMessage.size()>0) firstMessage = runnerErrorMessage.get(0);
        assertFalse("No error expected, received " + firstMessage,runnerHadError );
    }
    private void expectedError(String expected){
        assertTrue("Errors expected in test" , runnerHadError);
        assertEquals("Error messages are equal", expected, runnerErrorMessage.get(0));
    }
}