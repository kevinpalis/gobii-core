package org.gobiiproject.gobiidomain.services.impl;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.LoaderInstructionFilesService;
import org.gobiiproject.gobiidtomapping.instructions.DtoMapLoaderInstructions;
import org.gobiiproject.gobiimodel.dto.instructions.loader.LoaderInstructionFilesDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by Phil on 4/12/2016.
 */
public class LoaderInstructionFileServiceImpl implements LoaderInstructionFilesService {

    private Logger LOGGER = LoggerFactory.getLogger(LoaderInstructionFileServiceImpl.class);

    @Autowired
    private DtoMapLoaderInstructions dtoMapLoaderInstructions = null;


    @Override
    public LoaderInstructionFilesDTO getStatus(String cropType, String getInstructions) throws GobiiDomainException {

        LoaderInstructionFilesDTO returnVal;

        try {
            returnVal = dtoMapLoaderInstructions.getStatus(cropType, getInstructions);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }

    @Override
    public LoaderInstructionFilesDTO createInstruction(String cropType, LoaderInstructionFilesDTO LoaderInstructionFilesDTO)
            throws GobiiDomainException {
        LoaderInstructionFilesDTO returnVal;

        returnVal = dtoMapLoaderInstructions.createInstruction(cropType, LoaderInstructionFilesDTO);

        // When we have roles and permissions, this will be set programmatically
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        return returnVal;
    }
} // LoaderInstructionFileServiceImpl
