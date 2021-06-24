package org.gobiiproject.gobiidomain.services;

import java.io.File;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;


/**
 * Created by Phil on 4/12/2016.
 */
public interface FilesService {

    String writeJobFileForCrop(String cropType,
                             String jobId,
                             String fileName,
                             GobiiFileProcessDir gobiiFileProcessDir,
                             byte[] byteArray) throws GobiiException, Exception;

    void deleteFileFromProcessDir(String cropType,
                                  String fileName,
                                  GobiiFileProcessDir gobiiFileProcessDir) throws Exception;

    void writeFileToProcessDir(String cropType,
                               String fileName,
                               GobiiFileProcessDir gobiiFileProcessDir,
                               byte[] byteArray) throws Exception;

    File readCropFileForJob(String cropType,
                            String gobiiJobId,
                            String fileName,
                            GobiiFileProcessDir gobiiFileProcessDir) throws GobiiException, Exception;

    String writeExperimentDataFile(String cropType, String fileName, byte[] dataFileBytes) throws Exception;


    String makeDirInProcessDir(String cropType, String dirName,
                               GobiiFileProcessDir gobiiFileProcessDir) throws Exception;

}
