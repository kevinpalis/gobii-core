package org.gobiiproject.gobiiprocess;

import org.gobiiproject.gobiiprocess.vcfinterface.HTSInterface;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class HDF5InterfaceTest {
    @Test
    public void testCollateFiles() throws IOException {
        File t1 = new File("testfile1");
        File t2 = new File("testfile2");
        File t3 = new File("testfile3");
        File out = new File("testfileout");

        try (FileWriter w1 = new FileWriter(t1);FileWriter w2 = new FileWriter(t2);FileWriter w3 = new FileWriter(t3)) {
            w1.write("Derp");
            w1.write(System.lineSeparator());
            w1.write("Derp2");
            w2.write("Derp3");
            w2.write(System.lineSeparator());
            w2.write("Derp4");
            w3.write("Derp5");
            w3.write(System.lineSeparator());
            w3.write("Derp6");
            w3.write(System.lineSeparator());
            w3.write("Derp7");
        } catch (IOException e) {
            e.printStackTrace();
        }

        HDF5Interface.coallateFiles("testfile1 testfile2 testfile3","\t","testfileout");

        BufferedReader outReader = new BufferedReader(new FileReader(out));
        String line1 = outReader.readLine();
        String line2 = outReader.readLine();
        String line3 = outReader.readLine();

        Assert.assertEquals("Derp\tDerp3\tDerp5",line1);
        Assert.assertEquals("Derp2\tDerp4\tDerp6",line2);
        Assert.assertEquals("Derp7",line3);

        t1.delete();
        t2.delete();
        t3.delete();
        out.delete();
    }
}
