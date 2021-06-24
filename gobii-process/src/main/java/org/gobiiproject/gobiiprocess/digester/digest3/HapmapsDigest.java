package org.gobiiproject.gobiiprocess.digester.digest3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.instructions.loader.DigesterResult;
import org.gobiiproject.gobiimodel.dto.instructions.loader.MasticatorResult;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.ColumnAspect;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.DatasetDnaRunTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.DatasetMarkerTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.DnaRunTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.DnaSampleTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.FileHeader;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.GermplasmTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.LoaderInstruction3;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MarkerTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MatrixAspect;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MatrixTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MatrixTransformAspect;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MatrixTransformTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.RangeAspect;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.RowAspect;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.Table;
import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.entity.Project;
import org.gobiiproject.gobiimodel.utils.AspectUtils;
import org.gobiiproject.gobiimodel.utils.GobiiFileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HapmapsDigest extends GenotypeMatrixDigest {

    
    private final String headerIdentifier = "rs#";


    private final String[] hapMapRequiredColumns = {
        "rs#",
        "alleles",
        "chrom",
        "pos",
        "strand",
        "assembly#",
        "center",
        "protLSID",
        "assayLSID",
        "panelLSID",
        "QCcode"
    };

    private final Map<String, String> aspectTableColumnsToHapmapColumns = 
        Map.of("markerName", "rs#", 
               "linkageGroupName", "chrom", 
               "markerPositionStart", "pos",
               "markerPositionStop", "pos");

    public HapmapsDigest(LoaderInstruction3 loaderInstruction, 
                         ConfigSettings configSettings) throws GobiiException {
        super(loaderInstruction, configSettings);
    }

 
    public DigesterResult digest() throws GobiiException {        

        List<File> filesToDigest = new ArrayList<>();
        Map<String, File> intermediateDigestFileMap = new HashMap<>();
        Map<String, Integer> totalLinesWrittenForEachTable = new HashMap<>();

        Map<String, Table> aspects = new HashMap<>();
                

        filesToDigest = getFilesToDigest(this.uploadRequest.getInputFiles());

        // To keep track of files having uniform dnarun names columsn across files
        String[] previousFileHeaders = {};

        
        String dataType = getDataType();

        // Digested files are merged for each table.
        for(File fileToDigest : filesToDigest) {

            // Ignore non text file
            if(!GobiiFileUtils.isFileTextFile(fileToDigest)) {
                continue;
            }

            // Get file header
            FileHeader fileHeader = 
                getFileHeaderByIdentifier(fileToDigest, headerIdentifier);

            // Make sure file header is valid hapmap header and dnarun names 
            // match dnarun names from previous file.
            if(previousFileHeaders.length == 0) {
                if(!Arrays.equals(
                        hapMapRequiredColumns, 
                        Arrays.copyOfRange(fileHeader.getHeaders(), 
                                           0, 
                                           hapMapRequiredColumns.length))) {
                    throw new GobiiException(String.format(
                        "Invalid hapmap file %s with header columns not matching " +
                        "required hapmap columns", fileToDigest.getAbsolutePath()));
                }
                if(fileHeader.getHeaders().length == hapMapRequiredColumns.length) {
                    throw new GobiiException(
                        String.format("File %s does not have any samples", 
                                      fileToDigest.getAbsolutePath()));
                }
                previousFileHeaders = fileHeader.getHeaders();
                // Set Dna run aspect only once as dnarun names will be same across 
                // all the files.
                String datasetDnaRunTableName = 
                    AspectUtils.getTableName(DatasetDnaRunTable.class);
                aspects.put(datasetDnaRunTableName, 
                            getDatasetDnaRunTable(fileHeader.getHeaderLineNumber(), 1));

                if(uploadRequest.isLoadDnaRunNamesAsSamplesAndGermplasms()) {
                    String dnaRunTableName = AspectUtils.getTableName(DnaRunTable.class);                         
                    String dnaSampleTableName = AspectUtils.getTableName(DnaSampleTable.class);
                    String germplasmTableName = AspectUtils.getTableName(GermplasmTable.class);
                    DnaRunTable dnaRunTable = getDnaRunTable(fileHeader.getHeaderLineNumber());
                    aspects.put(dnaRunTableName, dnaRunTable);

                    DnaSampleTable dnaSampleTable = 
                       getDnaSampleTable(fileHeader.getHeaderLineNumber()) ;
                    aspects.put(dnaSampleTableName, dnaSampleTable);

                    GermplasmTable germplasmTable = 
                        getGermplasmTable(fileHeader.getHeaderLineNumber()); 
                    aspects.put(germplasmTableName, germplasmTable);
                }

            }
            else if(!Arrays.equals(fileHeader.getHeaders(), previousFileHeaders)) {
                throw new GobiiException("Files dont have same columns.");
            }
            // Set dataset_marker table aspect               
            String datasetMarkerTableName = AspectUtils.getTableName(DatasetMarkerTable.class);
            Integer hdf5MarkerIndexStart = 1;
            if(totalLinesWrittenForEachTable.containsKey(datasetMarkerTableName)) {
                hdf5MarkerIndexStart = 
                    totalLinesWrittenForEachTable.get(datasetMarkerTableName) + 1;
            }

            DatasetMarkerTable datasetMarkerTable = 
                getDatasetMarkerTable(fileHeader.getHeaders(), 
                                      fileHeader.getHeaderLineNumber(),
                                      hdf5MarkerIndexStart);
            aspects.put(datasetMarkerTableName, datasetMarkerTable); 


            // Set Transform or normal matrix aspect based on data type
            if(StringUtils.isNotEmpty(dataType) &&
                dataTypeToTransformType.containsKey(dataType)) {
                String matrixTableName = AspectUtils.getTableName(MatrixTransformTable.class);
                aspects.put(matrixTableName, 
                            getMatrixTransformTable(fileHeader.getHeaderLineNumber(), 
                                                    dataType));
            }
            else {
                String matrixTableName = AspectUtils.getTableName(MatrixTable.class);
                aspects.put(matrixTableName, getMatrixTable(fileHeader.getHeaderLineNumber()));
            }
            if(uploadRequest.isLoadMarkers()) {
                String markerTableName = AspectUtils.getTableName(MarkerTable.class);
                aspects
                    .put(markerTableName, 
                         getMarkerTable(fileHeader.getHeaders(), 
                                        fileHeader.getHeaderLineNumber()));
            }

            // Masticate and set the output.
            Map<String, MasticatorResult> masticatedFilesMap = 
                masticate(fileToDigest, GobiiFileUtils.TAB_SEP, aspects);

            // Update the intermediate file map incase if there is any new table
            masticatedFilesMap.forEach((table, masticatorResult) -> {
                intermediateDigestFileMap.put(table, masticatorResult.getOutputFile());
                int updatedCount = masticatorResult.getTotalLinesWritten(); 
                if(totalLinesWrittenForEachTable.containsKey(table)) {
                    updatedCount += totalLinesWrittenForEachTable.get(table);
                }
                totalLinesWrittenForEachTable.put(table, updatedCount);
            });

        }
       
        // Get the load order from ifl config
        List<String> loadOrder = getLoadOrder();
        if(loadOrder == null || loadOrder.size() <= 0) {
            loadOrder = new ArrayList<>(intermediateDigestFileMap.keySet());
        }

        DigesterResult digesterResult = new DigesterResult
                .Builder()
                .setSuccess(true)
                .setSendQc(false)
                .setCropType(loaderInstruction.getCropType())
                .setCropConfig(cropConfig)
                .setIntermediateFilePath(loaderInstruction.getOutputDir())
                .setLoadType(loaderInstruction.getLoadType())
                .setLoaderInstructionsMap(intermediateDigestFileMap)
                .setLoaderInstructionsList(loadOrder)
                .setDatasetType(dataType)
                .setDatasetId(getDataset().getDatasetId())
                .setJobName(loaderInstruction.getJobName())
                .setContactEmail(loaderInstruction.getContactEmail())
                .build();

        return digesterResult;
        
    }

    private DatasetMarkerTable getDatasetMarkerTable(
        String[] headers, 
        int headerLineNumber,
        int hdf5Start) {

        DatasetMarkerTable datasetMarkerTable = new DatasetMarkerTable();

        datasetMarkerTable.setDatasetId(uploadRequest.getDatasetId());

        String platformId = getPlatform().getPlatformId().toString(); 
        datasetMarkerTable.setPlatformId(platformId);

        // Set column aspect for marker name.
        int markerNameColumnIndex = 
            ArrayUtils.indexOf(headers, 
                               aspectTableColumnsToHapmapColumns.get("markerName"));
        // data lines starts after header line
        ColumnAspect markerNameColumn = 
            new ColumnAspect(headerLineNumber+1, markerNameColumnIndex);
        datasetMarkerTable.setMarkerName(markerNameColumn);
        
        // Set range aspect for hdf5 index.
        datasetMarkerTable.setDatasetMarkerIdx(new RangeAspect(hdf5Start));
        
        return datasetMarkerTable;
    }

    private DatasetDnaRunTable getDatasetDnaRunTable(int headerLineNumber,
                                                     int hdf5Start) throws GobiiException {
        
       DatasetDnaRunTable datasetDnaRunTable = new DatasetDnaRunTable();
        
       datasetDnaRunTable.setDatasetId(uploadRequest.getDatasetId());

       // Get Experiment id
       Experiment experiment = getExperiment();
       datasetDnaRunTable.setExperimentId(experiment.getExperimentId().toString());
       
       datasetDnaRunTable.setPlatformId(getPlatform().getPlatformId().toString());

       // Dnarun names starts after required columns in the file
       RowAspect dnaRunNameRow = new RowAspect(headerLineNumber, 
                                               hapMapRequiredColumns.length);
       datasetDnaRunTable.setDnaRunName(dnaRunNameRow);
        
       // Set Range aspect for hdf5 index                   
       datasetDnaRunTable.setDatasetDnaRunIdx(new RangeAspect(hdf5Start));
       
       return datasetDnaRunTable;

    }

    private MatrixTransformTable getMatrixTransformTable(int headerLineNumber, 
                                                         String dataType) {
        MatrixTransformTable matrixTable = new MatrixTransformTable();
        MatrixTransformAspect matrixAspect = new MatrixTransformAspect(
            dataTypeToTransformType.get(dataType),
            headerLineNumber+1, 
            hapMapRequiredColumns.length);
        matrixTable.setMatrix(matrixAspect);
        return matrixTable;
    }

    private MatrixTable getMatrixTable(int headerLineNumber) {
        MatrixTable matrixTable = new MatrixTable();
        MatrixAspect matrixAspect = new MatrixAspect(headerLineNumber+1, 
                                                     hapMapRequiredColumns.length);

        matrixTable.setMatrix(matrixAspect);
        return matrixTable;
    }

    private MarkerTable getMarkerTable(String[] headers, int headerLineNumber) {
        MarkerTable markerTable = new MarkerTable();
        
        // Set column aspect for marker name.
        int markerNameColumnIndex = 
            ArrayUtils.indexOf(headers, 
                               aspectTableColumnsToHapmapColumns.get("markerName"));
        // data lines starts after header line
        ColumnAspect markerNameColumn = 
            new ColumnAspect(headerLineNumber+1, markerNameColumnIndex);

        markerTable.setMarkerName(markerNameColumn);
        markerTable.setPlatformId(getPlatform().getPlatformId().toString());
        markerTable.setStatus(newStatus.getCvId().toString());

        return markerTable;
    }

    private DnaRunTable getDnaRunTable(int headerLineNumber) {
        DnaRunTable dnaRunTable = new DnaRunTable();
        RowAspect dnaRunNameRow = new RowAspect(headerLineNumber, hapMapRequiredColumns.length);
        dnaRunTable.setDnaRunName(dnaRunNameRow);
        dnaRunTable.setDnaSampleName(dnaRunNameRow);
        dnaRunTable.setDnaSampleNum(new RangeAspect(1));

        // Get Experiment id
        Experiment experiment = getExperiment();
        dnaRunTable.setExperimentId(experiment.getExperimentId().toString());

        // Get Project Id
        Project project = experiment.getProject();
        dnaRunTable.setProjectId(project.getProjectId().toString());
       
        dnaRunTable.setStatus(newStatus.getCvId().toString());
        
        return dnaRunTable;
    }

    private DnaSampleTable getDnaSampleTable(int headerLineNumber) {

        DnaSampleTable dnaSampleTable = new DnaSampleTable();
        RowAspect dnaSampleNameRow = new RowAspect(headerLineNumber, hapMapRequiredColumns.length);

        dnaSampleTable.setDnaSampleName(dnaSampleNameRow);
        dnaSampleTable.setDnaSampleUuid(dnaSampleNameRow);
        dnaSampleTable.setGermplasmExternalCode(dnaSampleNameRow);
        dnaSampleTable.setDnaSampleNum(new RangeAspect(1));

        Project project = getExperiment().getProject();
        dnaSampleTable.setProjectId(project.getProjectId().toString());

        dnaSampleTable.setStatus(newStatus.getCvId().toString());            

        return dnaSampleTable;
    }

    private GermplasmTable getGermplasmTable(int headerLineNumber) {

        GermplasmTable germplasmTable = new GermplasmTable();
        RowAspect germplasmNameRow = new RowAspect(headerLineNumber, hapMapRequiredColumns.length);

        germplasmTable.setGermplasmName(germplasmNameRow);
        germplasmTable.setGermplasmExternalCode(germplasmNameRow);

        germplasmTable.setStatus(newStatus.getCvId().toString());

        return germplasmTable;
    }
}
