package org.gobiiproject.gobiiprocess;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

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


}
