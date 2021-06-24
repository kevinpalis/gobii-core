package org.gobiiproject.gobiiprocess.digester;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiLoaderProcedure;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.LoaderInstruction3;
import org.gobiiproject.gobiiprocess.digester.digest1.Digest1;
import org.gobiiproject.gobiiprocess.digester.digest3.DigestFactory3;

public class DigestFactory {

    public Digest getDigest(File instructionFile,
                            ConfigSettings configSettings) throws GobiiException {

        ObjectMapper mapper = new ObjectMapper();
        
        // Try to map to loader instruction v3, if there is an exception, try to map it to v1
        LoaderInstruction3 loaderInstruction3;
        try {
            loaderInstruction3 = mapper.readValue(instructionFile, LoaderInstruction3.class);
        }
        catch(IOException e) {
            throw new GobiiException("Unable to process instruction file");
        }
        if(loaderInstruction3.getInstructionType() != null &&
            loaderInstruction3.getInstructionType().equals("v3")) {
            return new DigestFactory3().getDigest(loaderInstruction3, configSettings);
        }
        else {
            try {
                GobiiLoaderProcedure loaderProcedure = 
                    mapper.readValue(instructionFile, GobiiLoaderProcedure.class);
                String instructionFileName = instructionFile.getName();
                String jobName = instructionFileName
                    .substring(0, instructionFileName.lastIndexOf('.'));
                loaderProcedure.setJobName(jobName);
                return (new Digest1(loaderProcedure, configSettings)); 
            }
            catch(IOException e2) {
                throw new GobiiException("Invalid Instruction file");
            }
        }
    }
}
