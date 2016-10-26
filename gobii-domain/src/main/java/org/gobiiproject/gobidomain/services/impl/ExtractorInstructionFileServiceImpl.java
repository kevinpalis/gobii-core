package org.gobiiproject.gobidomain.services.impl;

import org.gobiiproject.gobidomain.GobiiDomainException;
import org.gobiiproject.gobidomain.services.ExtractorInstructionFilesService;
import org.gobiiproject.gobiidtomapping.DtoMapExtractorInstructions;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.container.ExtractorInstructionFilesDTO;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Angel on 6/8/2016.
 */
public class ExtractorInstructionFileServiceImpl implements ExtractorInstructionFilesService {

    private Logger LOGGER = LoggerFactory.getLogger(ExtractorInstructionFileServiceImpl.class);

    @Autowired
    private DtoMapExtractorInstructions dtoMapExtractorInstructions = null;


    @Override
    public ExtractorInstructionFilesDTO processExtractorFileInstructions(ExtractorInstructionFilesDTO extractorInstructionFilesDTO) {

        ExtractorInstructionFilesDTO returnVal = extractorInstructionFilesDTO;

        try {

            switch (returnVal.getGobiiProcessType()) {

                case CREATE:
                    returnVal = dtoMapExtractorInstructions.writeInstructions(extractorInstructionFilesDTO.getCropType(),extractorInstructionFilesDTO);
                    break;

                case READ:
                    returnVal = dtoMapExtractorInstructions.readInstructions(extractorInstructionFilesDTO.getCropType(),extractorInstructionFilesDTO.getInstructionFileName());
                    break;

                default:
                    returnVal.getStatus().addStatusMessage(GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.BAD_REQUEST,
                            "Unsupported proces type " + returnVal.getGobiiProcessType().toString());

            } // switch

        } catch (Exception e) {

            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii service error", e);
        }

        return returnVal;

    } // processExtractorFileInstructions

    @Override
    public ExtractorInstructionFilesDTO getInstruction(String cropType, String instructionFileName)  throws GobiiException {

        ExtractorInstructionFilesDTO returnVal;

        try {
            returnVal = dtoMapExtractorInstructions.readInstructions(cropType,instructionFileName);

//            for(ExtractorInstructionFilesDTO currentExtractorInstructionFilesDTO : returnVal ) {
//                //currentExtractorInstructionFilesDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
//            }




        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }

    @Override
    public ExtractorInstructionFilesDTO createInstruction(String cropType, ExtractorInstructionFilesDTO ExtractorInstructionFilesDTO)
            throws GobiiException {
        ExtractorInstructionFilesDTO returnVal;

        returnVal = dtoMapExtractorInstructions.writeInstructions(cropType,ExtractorInstructionFilesDTO);

        // When we have roles and permissions, this will be set programmatically
//        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
//        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        return returnVal;
    }    

} // ExtractorInstructionFileServiceImpl
