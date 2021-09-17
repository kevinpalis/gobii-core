package org.gobiiproject.gobiiprocess;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

public class HDF5AllelicEncoderTest {

    @Test
    public void testEncodeDecode() throws IOException {
        File input = new File("encodeinput");
        File encoded = new File("encodeddata");
        File lookup = new File("lookupfile");
        File output = new File("decodedinput");

        try (FileWriter inputwriter = new FileWriter(input)) {
            inputwriter.write("A/C\tG/T\tN/N");
            inputwriter.write(System.lineSeparator());
            inputwriter.write("ACAT/GAG\t/\tA/A");
            inputwriter.write(System.lineSeparator());
            inputwriter.write("A/C\tG/T\tN/N");
            inputwriter.write(System.lineSeparator());
            inputwriter.write("A/GAG\tG/GAGA\tA/C");
            inputwriter.write(System.lineSeparator());
            inputwriter.write("A/C\tG/T\tN/N");
            inputwriter.write(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }

        HDF5AllelicEncoder.createEncodedFile(input,encoded,lookup,"/","\t");
        HDF5AllelicEncoder.createDecodedFile(encoded,lookup,output,"/","\t");


        BufferedReader encodedReader = new BufferedReader(new FileReader(encoded));
        BufferedReader outReader = new BufferedReader(new FileReader(output));
        String line1 = outReader.readLine();
        String line2 = outReader.readLine();
        String line3 = outReader.readLine();
        String line4 = outReader.readLine();
        String line5 = outReader.readLine();

        BufferedReader lookupReader = new BufferedReader(new FileReader(lookup));

        Assert.assertEquals("1\tIACAT;IGAG",lookupReader.readLine());
        Assert.assertEquals("3\tIGAG;IGAGA",lookupReader.readLine());

        Assert.assertEquals("AC\tGT\tNN",encodedReader.readLine());
        //Assert.assertEquals((char)128 + ""+(char)129+"\t"+"\t"+"AA",encodedReader.readLine());

        Assert.assertEquals("A/C\tG/T\tN/N",line1);
        Assert.assertEquals("ACAT/GAG\t\tA/A",line2);//given there's no 'length' to the missing data, there is no separator
        Assert.assertEquals("A/C\tG/T\tN/N",line3);
        Assert.assertEquals("A/GAG\tG/GAGA\tA/C",line4);
        Assert.assertEquals("A/C\tG/T\tN/N",line5);


        input.delete();
        encoded.delete();
        lookup.delete();
        output.delete();
    }

    @Test
    public void testEncodeDecode4letRWExample() throws IOException {
        File input = new File("encodeinput2");
        File encoded = new File("encodeddata2");
        File lookup = new File("lookupfile2");
        File output = new File("decodedinput2");

        try (FileWriter inputwriter = new FileWriter(input)) {
            inputwriter.write("A/A/A/A\tA/A/C/T");
            inputwriter.write(System.lineSeparator());
            inputwriter.write("A/C/G/T\tUncallable");
            inputwriter.write(System.lineSeparator());
            inputwriter.write("T/T/T/T\tUNKNOWN");
            inputwriter.write(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }

        HDF5AllelicEncoder.createEncodedFile(input,encoded,lookup,null,"\t");
        HDF5AllelicEncoder.createDecodedFile(encoded,lookup,output,"/","\t");


        BufferedReader encodedReader = new BufferedReader(new FileReader(encoded));
        BufferedReader outReader = new BufferedReader(new FileReader(output));
        String line1 = outReader.readLine();
        String line2 = outReader.readLine();
        String line3 = outReader.readLine();

        Assert.assertEquals("A/A/A/A\tA/A/C/T",line1);

        input.delete();
        encoded.delete();
        lookup.delete();
        output.delete();
    }

    @Test
    public void testCharEncode() throws Exception {
        HDF5AllelicEncoder testEncoder = new HDF5AllelicEncoder();
        char testChar = 'B';
        char testOut;
        String testString = "A/BABA";
        String testOutString;
        //testOut = testEncoder.encodeChar(2);
        //HDF5AllelicEncoder.RowTranslator rt = new HDF5AllelicEncoder.RowTranslator();
        //rt.unencodedAlleles= new HashSet<String>(Arrays.asList("A"));
        //rt.nonstandardAlleles= Arrays.asList("BABA");
        //rt.nonstandardAlleleMap = new HashSet<String>(rt.nonstandardAlleles);
        //testOutString = rt.getEncodedString(testString, "/",false);
        //Assert.assertEquals("encodeChar codes one character",testOut+"",(char)131+"");
        //Assert.assertTrue("TestOut produces only one character", ((testOut+"").length()) == 1);

        //Assert.assertEquals("OutString matches ",testOutString, "A"+HDF5AllelicEncoder.encodeChar(0));
    }

    @Test
    public void testGSDEncode() throws IOException {
        File input = new File("encodeinput3");
        File encoded = new File("encodeddata3");
        File lookup = new File("lookupfile3");
        File output = new File("decodedinput3");

        try (FileWriter inputwriter = new FileWriter(input)) {
            inputwriter.write("ATGTT/ATGTT\tATGTT/ATGTT\tN/N");
            inputwriter.write(System.lineSeparator());
            inputwriter.write("CGATGGTTTC/CGATGGTTTC\tCGATGGTTTC/CGATGGTTTC\tN/N");
            inputwriter.write(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }

        HDF5AllelicEncoder.createEncodedFile(input,encoded,lookup,"/","\t");
        HDF5AllelicEncoder.createDecodedFile(encoded,lookup,output,"/","\t");


        BufferedReader encodedReader = new BufferedReader(new FileReader(encoded));
        BufferedReader outReader = new BufferedReader(new FileReader(output));
        String line1 = outReader.readLine();
        String line2 = outReader.readLine();

        BufferedReader lookupReader = new BufferedReader(new FileReader(lookup));

        char e1 = (char)129;
        //String firstAltx2 = ""+e1+e1;
        String firstAltx2 = "��"; // This might not be portable, if it breaks, just destroy this test
        Assert.assertEquals("First Line encoded as alt/alt",firstAltx2+"\t"+firstAltx2+"\tNN",encodedReader.readLine());
        Assert.assertEquals("Second Line encoded as alt/alt",firstAltx2+"\t" + firstAltx2+"\tNN",encodedReader.readLine());

        input.delete();
        encoded.delete();
        lookup.delete();
        output.delete();
    }



}
