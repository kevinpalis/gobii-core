package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.gdmv3.FileDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.FileManifestDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

import java.io.InputStream;

public interface FileService {

    FileManifestDTO initiateFileUpload(String cropType) throws GobiiDomainException;

    FileDTO updateFileChunk(String fileName,
                            String mimeType,
                            String cropType,
                            InputStream inputStream,
                            String parentDirectory) throws GobiiDomainException;

    PagedResult<FileDTO> listFilesByFilePath(String filePath, 
                                             String cropType, 
                                             Integer pageSize, 
                                             Integer pageNum) throws GobiiDomainException;

}
