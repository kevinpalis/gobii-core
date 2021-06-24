package org.gobiiproject.gobiisampletrackingdao.hdf5;

import static org.gobiiproject.gobiimodel.utils.FileSystemInterface.rmIfExist;
import static org.gobiiproject.gobiimodel.utils.HelperFunctions.tryExec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.gobiiproject.gobiimodel.dto.system.Hdf5InterfaceResultDTO;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.FileSystemInterface;
import org.gobiiproject.gobiimodel.utils.HelperFunctions;
import org.gobiiproject.gobiisampletrackingdao.GobiiDaoException;

/**
 * Interface to extract genotypes from HDF5.
 */
public class HDF5Interface {

    private String pathToHDF5;

    private AbstractHdf5ProcessPathSelector hdf5ProcessingPathSelector;

    private static final org.slf4j.Logger ErrorLogger = org.slf4j.LoggerFactory.getLogger(HDF5Interface.class);

    public HDF5Interface(String pathToHDF5, AbstractHdf5ProcessPathSelector hdf5ProcessPathSelector) {
        this.pathToHDF5 = pathToHDF5;
        this.hdf5ProcessingPathSelector = hdf5ProcessPathSelector;
    }

    public String getPathToHDF5() {
        return this.pathToHDF5;
    }

    public void setPathToHDF5(String pathToHDF5) {
        this.pathToHDF5 = pathToHDF5;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getHdf5ProcessingPaths() {
        return (Map<String, String>) hdf5ProcessingPathSelector
            .getHdf5ProcessPaths(
                hdf5ProcessingPathSelector.determineCurrentLookupKey());
    }

    /**
     * Gets a pared down list of markers and samples based on position file and sample position file
     * @param markerFast if the output is 'markerFast' or sample fast
     * @param outputFolderName Location of folder to store temporary files
     * @return String location of the output file on the filesystem.
     * @throws FileNotFoundException if the datasets provided contain an invalid dataset, or the temporary file folder is badly chmodded
     */
    public Hdf5InterfaceResultDTO getHDF5Genotypes(
            boolean markerFast, Map<String, List<String>> datasetMarkerMap,
            Map<String, List<String>> datasetSampleMap,
            String outputFolderName
    ) throws FileNotFoundException {

        Hdf5InterfaceResultDTO hdf5InterfaceResultDTO =
            new Hdf5InterfaceResultDTO();

        StringBuilder genoFileString=new StringBuilder();

        //Hashset does not maintain order. So, sort them before you process.
        List<String> datasetList = new ArrayList<String>(datasetMarkerMap.keySet());

        Collections.sort(datasetList);

        String outputFolder = Paths.get(
                this.getHdf5ProcessingPaths().get("outputDir"),
                outputFolderName).toString();

        boolean createOutputFolder = (new File(outputFolder)).mkdirs();

        if(!createOutputFolder) {
            throw new GobiiDaoException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.NONE,
                    "HDF5 Interface Error. " +
                            "Failed to create temporary output folder.");
        }


        String errorFile = Paths.get(outputFolder,"error").toString();

        try{
            for(String datasetId : datasetList) {

                int dsID=Integer.parseInt(datasetId);

                String positionList = String.join("\n",
                    datasetMarkerMap.get(datasetId));

                String sampleList = String.join(
                    ",", datasetSampleMap.get(datasetId));

                String positionListFileLoc = Paths.get(outputFolder,
                    "position.list").toString();

                FileSystemInterface.rmIfExist(positionListFileLoc);

                FileWriter w = new FileWriter(positionListFileLoc);

                w.write(positionList);

                w.close();

                String genoFile = null;

                if(datasetSampleMap.get(datasetId).size() > 0) {

                    genoFile = getHDF5GenotypeByDatatset(
                            markerFast, dsID, positionListFileLoc,
                            sampleList, outputFolder, errorFile);

                    if(genoFile==null) return null;
                }

                if(genoFile != null) {

                    genoFileString.append(" "+genoFile);

                }
            }
        }
        catch(IOException e) {

            ErrorLogger.error("GobiiExtractor", "MarkerList reading failed", e);
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "HDF5 Interface Error. " +
                    "Failed to read markers.");

        }

        //Coallate genotype files
        String genoFile= Paths.get(outputFolder,"result.genotypes").toString();

        ErrorLogger.debug("MarkerList",
            "Accumulating markers into final genotype file");

        if(genoFileString.length() == 0){
            ErrorLogger.error("HDF5Interface","No genotype data to extract");
            return null;
        }

        String genotypePartFileIdentifier=genoFileString.toString();


        if(markerFast) {
            tryExec("paste" + genotypePartFileIdentifier, genoFile, errorFile);
        }
        else{
            tryExec("cat" + genotypePartFileIdentifier, genoFile, errorFile);
        }

        for(String tempGenoFile:genotypePartFileIdentifier.split(" ")) {
            rmIfExist(tempGenoFile);
        }

        hdf5InterfaceResultDTO.setErrorFile(errorFile);
        hdf5InterfaceResultDTO.setGenotypeFile(genoFile);
        hdf5InterfaceResultDTO.setOutputFolder(outputFolder);

        return hdf5InterfaceResultDTO;

    }


    /**
     * Performs the basic genotype extraction on a dataset given by dataSetId,
     * filtered by the string entry from the marker list and sample list files.
     * If marker list is null, do a dataset extract. Else, do a marker list extract on the dataset.
     * If sampleList is also set, filter by samples afterwards
     * @param markerFast if the output is 'markerFast' or sample fast
     * @param dataSetId Dataset ID to be pulled from
     * @param markerList nullable - determines what markers to extract.
     *                   File containing a list of marker positions, comma separated
     * @param sampleList nullable - list of comma delimited samples to cut out
     * @param tempOutputFolder folder to store intermediate results
     * @return file location of the dataset output.
     */
    public String getHDF5GenotypeByDatatset( boolean markerFast, Integer dataSetId, String markerList,
                                             String sampleList, String tempOutputFolder, String errorFile) {

        String genoFile=tempOutputFolder+"DS-"+dataSetId+".genotype";

        String HDF5File= getFileLoc(dataSetId);
        // %s <orientation> <HDF5 file> <output file>
        String ordering="samples-fast";


        if(markerFast)ordering="markers-fast";

        ErrorLogger.debug("Extractor","HDF5 Ordering is "+ordering);

        if(markerList!=null) {
            String hdf5Extractor= Paths.get(pathToHDF5,"fetchmarkerlist").toString();
            ErrorLogger.info(
                    "Extractor","Executing: " + hdf5Extractor+" "
                            + ordering +" "+HDF5File+" "+markerList+" "+genoFile);
            boolean success=HelperFunctions.tryExec(hdf5Extractor + " " + ordering+" "
                    + HDF5File+" "+markerList+" "+genoFile, null, errorFile);
            if(!success){
                rmIfExist(genoFile);
                return null;
            }
        }
        else {
            String hdf5Extractor=Paths.get(pathToHDF5,"dumpdataset").toString();
            ErrorLogger.info("Extractor","Executing: " + hdf5Extractor+" "
                    +ordering+" "+HDF5File+" "+genoFile);
            boolean success=HelperFunctions.tryExec(hdf5Extractor + " " + ordering + " "
                    + HDF5File + " " + genoFile, null, errorFile);
            if(!success){
                rmIfExist(genoFile);
                return null;
            }

        }
        if(sampleList!=null){
            filterBySampleList(genoFile,sampleList,markerFast, errorFile);
        }
        ErrorLogger.debug("Extractor","Extracting with "+ordering+" "+HDF5File+" "+genoFile);
        return genoFile;
    }

    private String getFileLoc(Integer dataSetId) {
        return this.getHdf5ProcessingPaths().get("dataFiles") + "DS_" + dataSetId + ".h5";
    }

    /**
     * Filters a matrix passed back by the HDF5 extractor by a sample list
     * @param filename path to extract naked matrix
     * @param sampleList Comma separated list of sample positions
     */
    private void filterBySampleList(String filename, String sampleList, boolean markerFast, String errorFile){
        String tmpFile=filename+".tmp";
        FileSystemInterface.mv(filename,tmpFile);
        String cutString=getCutString(sampleList);
        if(!markerFast) {
            String sedString=cutString.replaceAll(",","p;");//1,2,3 => 1p;2p;3   (p added later)
            tryExec("sed -n "+sedString+"p",filename,errorFile,tmpFile); //Sed parameters need double quotes to be a single parameter
        }
        else{
            tryExec("cut -f"+getCutString(sampleList),filename,errorFile,tmpFile);
        }
        rmIfExist(tmpFile);
    }

    /**
     * Converts a string of 1,2,-1,4,5,6,-1,2 (Arbitrary -1's and NOT -1's into a comma delimited set
     * excluding positions where a -1 esists of one higher than the input value.
     *
     * Note: Since input is zero-based list, and the output to SED/CUT is one based, all numbers are incremented here.
     *
     * Examples:
     * 0,1,2,-1,4,5 -> 1,2,3,5,6
     * 7,-1,7,-1,7,-1 -> 8,8,8
     * @param sampleList Input string
     * @return Output string (see above)
     */
    private String getCutString(String sampleList){
        String[] entries=sampleList.split(",");
        StringBuilder cutString=new StringBuilder();//Cutstring -> 1,2,4,5,6
        //int i=1;
        for(String entry:entries){
            int val=-1;
            try {
                //For some reason, spaces are everywhere, and Integer.parseInt is not very lenient
                String entryWithoutSpaces=entry.trim().replaceAll(" ","");
                val=Integer.parseInt(entryWithoutSpaces);
            }catch(Exception e){
                ErrorLogger.debug("GobiiExtractor NFE",e.toString());
            }
            if( val != -1){
                cutString.append((val+1)+",");
            }
            //i++;
        }
        cutString.deleteCharAt(cutString.length()-1);
        return cutString.toString();
    }
}
