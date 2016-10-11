package org.gobiiproject.gobidomain.services;

import org.gobiiproject.gobidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.headerlesscontainer.LoaderInstructionFilesDTO;

import java.util.List;

/**
 * Created by Phil on 4/12/2016.
 */
public interface LoaderInstructionFilesService {
    LoaderInstructionFilesDTO createInstruction(LoaderInstructionFilesDTO loaderInstructionFilesDTO);
    List<LoaderInstructionFilesDTO> getInstructions(LoaderInstructionFilesDTO loaderInstructionFilesDTOToCreate) throws GobiiDomainException;
}
