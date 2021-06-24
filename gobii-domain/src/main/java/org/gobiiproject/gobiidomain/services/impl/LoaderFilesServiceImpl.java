package org.gobiiproject.gobiidomain.services.impl;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.LoaderFilesService;
import org.gobiiproject.gobiidtomapping.instructions.DtoMapLoaderFiles;
import org.gobiiproject.gobiimodel.dto.instructions.loader.LoaderFilePreviewDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by Angel on 11/2016.
 */
public class LoaderFilesServiceImpl implements LoaderFilesService {

    private Logger LOGGER = LoggerFactory.getLogger(LoaderFilesServiceImpl.class);

    @Autowired
    private DtoMapLoaderFiles dtoMapLoaderFiles = null;


    @Override
    public LoaderFilePreviewDTO makeDirectory(String cropType, String directoryName) throws GobiiDomainException {

        LoaderFilePreviewDTO returnVal;

        try {
            returnVal = dtoMapLoaderFiles.makeDirectory(cropType, directoryName);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }

    @Override
    public LoaderFilePreviewDTO getPreview(String cropType, String directoryName, String fileFormat) throws GobiiDomainException {

        LoaderFilePreviewDTO returnVal;

        try {

            returnVal = dtoMapLoaderFiles.getPreview(cropType, directoryName, fileFormat);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }
} // LoaderFileServiceImpl
