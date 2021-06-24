package org.gobiiproject.gobiiprocess.digester.digest3;

import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.GenotypeUploadRequestDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.LoaderInstruction3;

public class DigestFactory3 {

    public Digest3 getDigest(LoaderInstruction3 loaderInstruction,
                             ConfigSettings configSettings) {
        switch (loaderInstruction.getLoadType()) {
            case "SAMPLES":
                return new SamplesDigest(loaderInstruction, configSettings);
            case "MARKER":
                return new MarkersDigest(loaderInstruction, configSettings);
            case "MATRIX":
                GenotypeUploadRequestDTO uploadRequest = Digest3.mapper.convertValue(
                    loaderInstruction.getUserRequest(), 
                    GenotypeUploadRequestDTO.class);                    
                switch(uploadRequest.getFileType()) {
                    case HAPMAP:
                        return new HapmapsDigest(loaderInstruction, configSettings);
                    case VCF:
                        return new VcfDigest(loaderInstruction, configSettings);
                    case INTERTEK:
                        return new InterteksDigest(loaderInstruction, configSettings);
                    default:
                        throw new GobiiException("Invalid loader instruction");
                }
            default:
                throw new GobiiException("Invalid loader instruction");
        }
    }
}
