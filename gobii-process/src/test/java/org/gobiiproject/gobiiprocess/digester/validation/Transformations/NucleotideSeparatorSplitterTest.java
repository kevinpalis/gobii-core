package org.gobiiproject.gobiiprocess.digester.validation.Transformations;


import org.gobiiproject.gobiiprocess.digester.csv.matrixValidation.MatrixErrorUtil;
import org.gobiiproject.gobiiprocess.digester.csv.matrixValidation.NucleotideSeparatorSplitter;

import org.junit.*;



import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@SuppressWarnings("unused")
public class NucleotideSeparatorSplitterTest {

    static boolean runnerHadError = false;
    static List<String> runnerErrorMessage = new ArrayList<String>();
    Set<String> normalMissingSegments = Stream.of("?","uncallable","unc").collect(Collectors.toSet());
    NucleotideSeparatorSplitter nss = new NucleotideSeparatorSplitter(4,normalMissingSegments);

    /**
     * Template code to run the nucleotide splitter
     * @param input
     * @return
     */
    private List<String> runSplitter(List<String> input){
        //Reset error flag and values
        runnerHadError=false;
        runnerErrorMessage = new ArrayList<String>();

        List<String> output = new ArrayList<String>();

        //Override setError to capture error messages
        MatrixErrorUtil dummy = new MatrixErrorUtil(){
            public void setError(String s){
                runnerHadError=true;
                runnerErrorMessage.add(s);
            }
        };

        //run
        nss.process(0,input,output,dummy);

        //return output
        return output;
    }


    @Test
    public void testNormalCase(){
        List<String> input = Arrays.asList("AAAA","ACGT","CCGT","GTTT","ACGT");
        List<String> expectedOutput = Arrays.asList("A/A/A/A","A/C/G/T","C/C/G/T","G/T/T/T","A/C/G/T");

        List<String> output = runSplitter(input);
        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);

    }

    @Test
    public void testNormalSeparatorsCase(){
        List<String> input = Arrays.asList("A/A/A/A","A/C/G/T","C/C/G/T","G/T/T/T","A/C/G/T");
        List<String> expectedOutput = Arrays.asList("A/A/A/A","A/C/G/T","C/C/G/T","G/T/T/T","A/C/G/T");

        List<String> output = runSplitter(input);
        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);
    }

    @Test
    public void testPipeSeparatorsCase(){
        List<String> input = Arrays.asList("A|A|A|A","A|C|G|T","C|C|G|T","G|T|T|T","A|C|G|T");
        List<String> expectedOutput = Arrays.asList("A/A/A/A","A/C/G/T","C/C/G/T","G/T/T/T","A/C/G/T");

        List<String> output = runSplitter(input);
        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);
    }


    @Test
    public void testExtraSeparatorsCase(){
        List<String> input = Arrays.asList("A/A/A/A","A/C//G/T","C/C/G/T","G/T/T/T","A/C/G/T");
       // List<String> expectedOutput = Arrays.asList("AAAA","ACGT","CCGT","GTTT","ACGT");

        List<String> output = runSplitter(input);
        expectedError("Unexpected Length Element A/C//G/T  in row 0");
    }

    @Test
    public void testMissingAlleleCase(){
        List<String> input = Arrays.asList("A/A/A/A","A/C//T","C/C/G/T","G/T/T/T","A/C/G/T");
        List<String> expectedOutput = Arrays.asList("A/A/A/A","A/C//T","C/C/G/T","G/T/T/T","A/C/G/T");

        List<String> output = runSplitter(input);
        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);
    }

    @Test
    public void testFewerSeparatorsCase(){
        List<String> input = Arrays.asList("A/A/A/A","A/C/G/T","C/C/G/T","GT/T/T","A/C/G/T");
       // List<String> expectedOutput = Arrays.asList("AAAA","ACGT","CCGT","GTTT","ACGT");

        List<String> output = runSplitter(input);
        expectedError("Unexpected Length Element GT/T/T  in row 0");
    }

    @Test
    public void testUnusualSeparatorsCase(){
        List<String> input = Arrays.asList("A/A/A/A","A/C|G|T","C/C/G/T","G/T/T/T","A/C/G/T");
        //List<String> expectedOutput = Arrays.asList("AAAA","ACGT","CCGT","GTTT","ACGT");

        List<String> output = runSplitter(input);
        expectedError("Unexpected Length Element A/C|G|T  in row 0");
    }

    @Test
    public void testUnknownElementCase(){
        List<String> input = Arrays.asList("A/A/A/A","A/C/G/T","?","G/T/T/T","A/C/G/T");
        List<String> expectedOutput = Arrays.asList("A/A/A/A","A/C/G/T","N/N/N/N","G/T/T/T","A/C/G/T");

        List<String> output = runSplitter(input);
        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);
    }

    @Test
    public void testLongUnknownElementCase(){
        List<String> input = Arrays.asList("A/A/A/A","A/C/G/T","Uncallable","G/T/T/T","A/C/G/T");
        List<String> expectedOutput = Arrays.asList("A/A/A/A","A/C/G/T","N/N/N/N","G/T/T/T","A/C/G/T");

        List<String> output = runSplitter(input);
        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);
    }


    @Test
    public void testUnknownInAlleleCase(){
        List<String> input = Arrays.asList("A/A/A/A","A/?/G/T","C/C/G/T","G/T/T/T","A/C/G/T");
        List<String> expectedOutput = Arrays.asList("A/A/A/A","A/?/G/T","C/C/G/T","G/T/T/T","A/C/G/T");

        List<String> output = runSplitter(input);
        //expectedError("Unexpected allele ? in A?GT  in row 0");
        //This case now caught downstream

        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);

    }

    @Test
    public void testInsertionAlleleCase(){
        List<String> input = Arrays.asList("ACCAC/A/A/A","A/?/CCAT/T","C/C/G/TAA","G/T/T/T","A/C/G/T");
        List<String> expectedOutput = Arrays.asList("ACCAC/A/A/A","A/?/CCAT/T","C/C/G/TAA","G/T/T/T","A/C/G/T");

        List<String> output = runSplitter(input);
        //expectedError("Unexpected allele ? in A?GT  in row 0");
        //This case now caught downstream

        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);

    }

    @Test
    public void testUnknownInAlleleStartCase(){
        List<String> input = Arrays.asList("?/A/A/A","A/C/G/T","C/C/G/T","G/T/T/T","A/C/G/T");
        List<String> expectedOutput = Arrays.asList("?/A/A/A","A/C/G/T","C/C/G/T","G/T/T/T","A/C/G/T");

        List<String> output = runSplitter(input);

        //expectedError("Unexpected allele ? in ?AAA  in row 0");
        //This case now caught downstream

        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);

    }

    @Test
    public void testAllUnknownElementsCase(){
        List<String> input = Arrays.asList("?","?","Uncallable","uNcAlLAblE","UNC");
        List<String> expectedOutput = Arrays.asList("N/N/N/N","N/N/N/N","N/N/N/N","N/N/N/N","N/N/N/N");

        List<String> output = runSplitter(input);
        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);
    }

    @Test
    public void test2LetterColonAndSingleLetter(){
        List<String> input = Arrays.asList("?","A:C","C","A/C","C");
        List<String> expectedOutput = Arrays.asList("N/N","A/C","C/C","A/C","C/C");
        //Reset error flag and values
        runnerHadError=false;
        runnerErrorMessage = new ArrayList<String>();

        List<String> output = new ArrayList<String>();

        //Override setError to capture error messages
        MatrixErrorUtil dummy = new MatrixErrorUtil(){
            public void setError(String s){
                runnerHadError=true;
                runnerErrorMessage.add(s);
            }
        };

        new NucleotideSeparatorSplitter(2,normalMissingSegments).process(0,input,output,dummy);

        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);
    }

    @Test
    public void test2LetterSemicolons(){
        List<String> input = Arrays.asList("?","Unc","C;C","A:C","C;G");
        List<String> expectedOutput = Arrays.asList("N/N","N/N","C/C","A/C","C/G");
        //Reset error flag and values
        runnerHadError=false;
        runnerErrorMessage = new ArrayList<String>();

        List<String> output = new ArrayList<String>();

        //Override setError to capture error messages
        MatrixErrorUtil dummy = new MatrixErrorUtil(){
            public void setError(String s){
                runnerHadError=true;
                runnerErrorMessage.add(s);
            }
        };

        new NucleotideSeparatorSplitter(2,normalMissingSegments).process(0,input,output,dummy);

        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);
    }

    @Test
    public void test2LetterINS(){
        List<String> input = Arrays.asList("?","INS","INS:INS","INSINS","A:INS");
        List<String> expectedOutput = Arrays.asList("N/N","+/+","+/+","+/+","A/+");
        //Reset error flag and values
        runnerHadError=false;
        runnerErrorMessage = new ArrayList<String>();

        List<String> output = new ArrayList<String>();

        //Override setError to capture error messages
        MatrixErrorUtil dummy = new MatrixErrorUtil(){
            public void setError(String s){
                runnerHadError=true;
                runnerErrorMessage.add(s);
            }
        };

        new NucleotideSeparatorSplitter(2,normalMissingSegments).process(0,input,output,dummy);

        noErrorsExpected();
        assertEquals("Transform equals input",expectedOutput,output);
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