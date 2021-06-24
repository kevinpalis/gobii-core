package org.gobiiproject.gobiidomain.services;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.instructions.loader.LoaderInstructionFilesDTO;

/**
 * Created by Phil on 4/12/2016.
 */
public interface LoaderInstructionFilesService {
    LoaderInstructionFilesDTO createInstruction(String cropType, LoaderInstructionFilesDTO loaderInstructionFilesDTO) throws GobiiDomainException;
    LoaderInstructionFilesDTO getStatus(String cropType, String instructionFileName) throws GobiiDomainException;
}
