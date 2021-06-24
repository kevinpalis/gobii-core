package org.gobiiproject.gobiidomain.services;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.ExtractorInstructionFilesDTO;


/**
 * Created by Phil on 4/12/2016.
 */
public interface ExtractorInstructionFilesService {

    ExtractorInstructionFilesDTO createInstruction(String cropType, ExtractorInstructionFilesDTO extractorInstructionFilesDTO) throws GobiiDomainException;
    ExtractorInstructionFilesDTO getStatus(String cropType, String instructionFileName) throws GobiiDomainException;

}
