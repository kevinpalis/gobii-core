package org.gobiiproject.gobiiprocess.digester.digest3;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.GenotypeUploadRequestDTO;
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
import org.gobiiproject.gobiimodel.types.GobiiLoaderPayloadTypes;
import org.gobiiproject.gobiimodel.utils.AspectUtils;
import org.gobiiproject.gobiimodel.utils.GobiiFileUtils;
import org.gobiiproject.gobiiprocess.digester.utils.TransposeMatrix;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InterteksDigest extends GenotypeMatrixDigest {

    
    private final String headerIdentifier = "DNA \\ Assay";

    private GenotypeUploadRequestDTO uploadRequest;

    public InterteksDigest(LoaderInstruction3 loaderInstruction, 
                           ConfigSettings configSettings) throws GobiiException {
        super(loaderInstruction, configSettings);
    }

 
    public DigesterResult digest() throws GobiiException {        

        List<File> filesToDigest = new ArrayList<>();
        Map<String, File> intermediateDigestFileMap = new HashMap<>();
        Map<String, Integer> totalLinesWrittenForEachTable = new HashMap<>();

        Map<String, Table> aspects = new HashMap<>();
    
        filesToDigest = getFilesToDigest(uploadRequest.getInputFiles());

        // To keep track of hdf5 start index for markers
        int hdf5MarkerIndexStart = 1;

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

            // Set Dna run aspect only once as dnarun names will be same across 
            // all the files.
            if(hdf5MarkerIndexStart == 1) {
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
            // Set dataset_marker table aspect               
            String datasetMarkerTableName = AspectUtils.getTableName(DatasetMarkerTable.class);

            DatasetMarkerTable datasetMarkerTable = 
                getDatasetMarkerTable(fileHeader.getHeaderLineNumber(),
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
                    .put(markerTableName, getMarkerTable(fileHeader.getHeaderLineNumber()));
            }

            // Masticate and set the output.
            Map<String, MasticatorResult> masticatedFilesMap = 
                masticate(fileToDigest, GobiiFileUtils.COMMA_SEP, aspects);

            // Update the intermediate file map incase if there is any new table
            for(String table: masticatedFilesMap.keySet()) {

                MasticatorResult masticatorResult = masticatedFilesMap.get(table);

                intermediateDigestFileMap.put(table, masticatorResult.getOutputFile());

                int updatedCount = masticatorResult.getTotalLinesWritten(); 

                if(totalLinesWrittenForEachTable.containsKey(table)) {

                    updatedCount += totalLinesWrittenForEachTable.get(table);

                    if(table.equals(GobiiLoaderPayloadTypes.MATRIX.getTerm()) 
                       && totalLinesWrittenForEachTable.get(table) > 0) {
                        hdf5MarkerIndexStart += fileHeader.getHeaders().length;
                    } 
                }
                totalLinesWrittenForEachTable.put(table, updatedCount);
            }
        }
        
        // Get the load order from ifl config
        List<String> loadOrder = getLoadOrder();
        if(loadOrder == null || loadOrder.size() <= 0) {
            loadOrder = new ArrayList<>(intermediateDigestFileMap.keySet());
        }

        // Transpose matrix as Intertek is Sample fast
        String transposeOutputFilePath = 
            intermediateDigestFileMap.get("matrix").getAbsolutePath()+".transpose";
        TransposeMatrix.transposeMatrix(
            "tab", 
            intermediateDigestFileMap.get("matrix").getAbsolutePath(), 
            transposeOutputFilePath,
            loaderInstruction.getOutputDir());
        File transposedFile = new File(transposeOutputFilePath);
        if(!transposedFile.exists()) {
            throw new GobiiException("Unable to transpose file for sample fast.");
        }
        intermediateDigestFileMap.put("matrix", transposedFile);

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
                .setJobStatusObject(jobStatus)
                .setDatasetId(getDataset().getDatasetId())
                .setJobName(loaderInstruction.getJobName())
                .setContactEmail(loaderInstruction.getContactEmail())
                .build();

        return digesterResult;
    }


    private DatasetMarkerTable getDatasetMarkerTable(
        int headerLineNumber,
        int hdf5Start) {

        DatasetMarkerTable datasetMarkerTable = new DatasetMarkerTable();

        datasetMarkerTable.setDatasetId(uploadRequest.getDatasetId());
        datasetMarkerTable.setPlatformId(getPlatform().getPlatformId().toString());

        // marker names starts from second column in header row
        RowAspect markerNameRow = new RowAspect(headerLineNumber, 1);
        datasetMarkerTable.setMarkerName(markerNameRow);
        
        // Set range aspect for hdf5 index.
        datasetMarkerTable.setDatasetMarkerIdx(new RangeAspect(hdf5Start));
        
        return datasetMarkerTable;
    }

    private DatasetDnaRunTable getDatasetDnaRunTable(int headerLineNumber,
                                                     int hdf5Start) throws GobiiException {
        
       DatasetDnaRunTable datasetDnaRunTable = new DatasetDnaRunTable();
        
       datasetDnaRunTable.setDatasetId(getDataset().getDatasetId().toString());
       datasetDnaRunTable.setPlatformId(getPlatform().getPlatformId().toString());

       // Get Experiment id
       datasetDnaRunTable.setExperimentId(getExperiment().getExperimentId().toString());

       // Dnarun names is the first column
       ColumnAspect dnaRunNameColumn = new ColumnAspect(headerLineNumber +1, 0);
       datasetDnaRunTable.setDnaRunName(dnaRunNameColumn);
        
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
            1);
        matrixTable.setMatrix(matrixAspect);
        return matrixTable;
    }

    private MatrixTable getMatrixTable(int headerLineNumber) {
        MatrixTable matrixTable = new MatrixTable();
        MatrixAspect matrixAspect = new MatrixAspect(headerLineNumber+1, 
                                                     1);

        matrixTable.setMatrix(matrixAspect);
        return matrixTable;
    }

    private MarkerTable getMarkerTable(int headerLineNumber) {
        MarkerTable markerTable = new MarkerTable();
        
        // marker names starts from second column in header row
        RowAspect markerNameRow = new RowAspect(headerLineNumber, 1);

        markerTable.setMarkerName(markerNameRow);
        markerTable.setPlatformId(getPlatform().getPlatformId().toString());
        markerTable.setStatus(newStatus.getCvId().toString());

        return markerTable;
    }

    private DnaRunTable getDnaRunTable(int headerLineNumber) {

        DnaRunTable dnaRunTable = new DnaRunTable();

        // Dnarun names is the first column
        ColumnAspect dnaRunNameColumn = new ColumnAspect(headerLineNumber +1, 0);

        dnaRunTable.setDnaRunName(dnaRunNameColumn);
        dnaRunTable.setDnaSampleName(dnaRunNameColumn);
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
        
        // Dnarun names is the first column. map dnarun names as sample names
        ColumnAspect dnaSampleNameColumn = new ColumnAspect(headerLineNumber +1, 0);

        dnaSampleTable.setDnaSampleName(dnaSampleNameColumn);
        dnaSampleTable.setDnaSampleUuid(dnaSampleNameColumn);
        dnaSampleTable.setGermplasmExternalCode(dnaSampleNameColumn);
        dnaSampleTable.setDnaSampleNum(new RangeAspect(1));

        Project project = getExperiment().getProject();
        dnaSampleTable.setProjectId(project.getProjectId().toString());

        dnaSampleTable.setStatus(newStatus.getCvId().toString());            

        return dnaSampleTable;
    }

    private GermplasmTable getGermplasmTable(int headerLineNumber) {

        GermplasmTable germplasmTable = new GermplasmTable();
        
        // Dnarun names is the first column. map dnarun names as column names.
        ColumnAspect germplasmNameColumn = new ColumnAspect(headerLineNumber +1, 0);

        germplasmTable.setGermplasmName(germplasmNameColumn);
        germplasmTable.setGermplasmExternalCode(germplasmNameColumn);

        germplasmTable.setStatus(newStatus.getCvId().toString());

        return germplasmTable;
    }
}
