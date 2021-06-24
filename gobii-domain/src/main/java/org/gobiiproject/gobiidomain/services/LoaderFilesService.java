package org.gobiiproject.gobiidomain.services;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.instructions.loader.LoaderFilePreviewDTO;


/**
 * Created by Angel on 11/2016.
 */
public interface LoaderFilesService {
    LoaderFilePreviewDTO makeDirectory(String cropType, String directoryName) throws GobiiDomainException;
    LoaderFilePreviewDTO getPreview(String cropType, String directoryName, String fileFormat) throws GobiiDomainException;
}
