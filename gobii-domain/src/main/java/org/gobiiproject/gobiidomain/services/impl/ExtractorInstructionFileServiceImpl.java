package org.gobiiproject.gobiidomain.services.impl;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.ExtractorInstructionFilesService;
import org.gobiiproject.gobiidtomapping.instructions.DtoMapExtractorInstructions;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.instructions.extractor.ExtractorInstructionFilesDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by Angel on 6/8/2016.
 */
public class ExtractorInstructionFileServiceImpl implements ExtractorInstructionFilesService {

    private Logger LOGGER = LoggerFactory.getLogger(ExtractorInstructionFileServiceImpl.class);

    @Autowired
    private DtoMapExtractorInstructions dtoMapExtractorInstructions = null;


    @Override
    public ExtractorInstructionFilesDTO createInstruction(String cropType, ExtractorInstructionFilesDTO ExtractorInstructionFilesDTO)
            throws GobiiException {
        ExtractorInstructionFilesDTO returnVal;

        returnVal = dtoMapExtractorInstructions.writeInstructions(cropType,ExtractorInstructionFilesDTO);

        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);

        return returnVal;
    }

    @Override
    public ExtractorInstructionFilesDTO getStatus(String cropType, String jobId)  throws GobiiException {

        ExtractorInstructionFilesDTO returnVal;

        try {
            returnVal = dtoMapExtractorInstructions.getStatus(cropType,jobId);

            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }
} // ExtractorInstructionFileServiceImpl
