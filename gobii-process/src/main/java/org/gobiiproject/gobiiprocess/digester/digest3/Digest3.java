package org.gobiiproject.gobiiprocess.digester.digest3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import org.gobii.masticator.aspects.AspectParser;
import org.gobii.masticator.aspects.FileAspect;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.cvnames.DatasetType;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectMaps;
import org.gobiiproject.gobiimodel.dto.gdmv3.FileDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.DigesterResult;
import org.gobiiproject.gobiimodel.dto.instructions.loader.MasticatorResult;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.*;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.LoaderTemplate;
import org.gobiiproject.gobiimodel.utils.GobiiFileUtils;
import org.gobiiproject.gobiimodel.utils.SimpleTimer;
import org.gobiiproject.gobiiprocess.JobStatus;
import org.gobiiproject.gobiiprocess.digester.Digest;
import org.gobiiproject.gobiiprocess.digester.GobiiDigester;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.LoaderTemplateDao;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public abstract class Digest3 implements Digest {

    static final ObjectMapper mapper = new ObjectMapper();

    final String propertyGroupSeparator = ".";
    
    private final int maxLinesToLookForHeader = 1000;

    LoaderInstruction3 loaderInstruction;
    GobiiCropConfig cropConfig;
    JobStatus jobStatus;

    LoaderTemplateDao loaderTemplateDao;

    protected final Map<String, String> dataTypeToTransformType = Map.of(
        DatasetType.CV_DATASETTYPE_IUPAC.getDatasetTypeName(), "IUPAC2BI", 
        DatasetType.CV_DATASETTYPE_NUCLEOTIDE_2_LETTER.getDatasetTypeName(), "TWOLETTERNUCLEOTIDE", 
        DatasetType.CV_DATASETTYPE_NUCLEOTIDE_4_LETTER.getDatasetTypeName(), "FOURLETTERNUCLEOTIDE"
    );


    protected CvDao cvDao;
    protected Cv newStatus;

    /**
     * Constructor
     *
     * @param loaderInstruction Instruction file with digest instrcution
     * @param configSettings    Configuration for GDM system
     * @throws GobiiException when unable to read crop configuration
     * or get job entity for given name.
     */
    Digest3(LoaderInstruction3 loaderInstruction,
            ConfigSettings configSettings) throws GobiiException {
        this.loaderInstruction = loaderInstruction;
        SpringContextLoaderSingleton.init(loaderInstruction.getCropType(),
            configSettings);
        try {
            this.cropConfig = configSettings.getCropConfig(loaderInstruction.getCropType());
        }
        catch (Exception e) {
            throw new GobiiException("Unable to read crop config");
        }
        this.jobStatus = new JobStatus(loaderInstruction.getJobName());
        this.loaderTemplateDao =
            SpringContextLoaderSingleton.getInstance().getBean(LoaderTemplateDao.class);
        this.cvDao = SpringContextLoaderSingleton.getInstance().getBean(CvDao.class);
        this.newStatus = cvDao.getNewStatus();
        // creates new directtory or cleans one if already exists
        setupOutputDirectory();            
    }

    abstract public DigesterResult digest();

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * Masticator is the module. process the refactored instruction file.
     * To avoid making changes directly in masticator as it is maintained separately,
     * just duplicate parts of it.
     *
     */
    protected Map<String, MasticatorResult> masticate(
        File dataFile,
        String fileDelimitter,
        Map<String, Table> aspects) throws GobiiException {

        Map<String, MasticatorResult> masticatorResultByTable  = new HashMap<>();

        SimpleTimer.start("FileRead");
        FileAspect fileAspect;
        try {

            HashMap<String, Object> aspectMapObject = new HashMap<>();
            aspectMapObject.put("aspects", aspects);
            String loaderInstructionJson = new ObjectMapper().writeValueAsString(aspectMapObject);
            fileAspect = AspectParser.parse(loaderInstructionJson);
        } catch (JsonProcessingException e) {
            throw new GobiiException(
                String.format("Unable to process aspect file as json object"),
                e);
        }

        File outputDir = new File(loaderInstruction.getOutputDir());

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        if (! outputDir.isDirectory()) {
            throw new GobiiException(
                String.format(
                    "Output Path %s is not a directory",
                    loaderInstruction.getOutputDir()));
        }

        List<MasticatorThread> threads = new ArrayList<>();

        // Spawn maticator threads for each table in the aspects
        for (String table : fileAspect.getAspects().keySet()) {

            String outputFilePath =
                String.format("%s%sdigest.%s", outputDir.getAbsolutePath(), File.separator, table);

            File outputFile = GobiiFileUtils.getFile(outputFilePath);
            
            final MasticatorThread masticatorThread = 
                new MasticatorThread(table, fileAspect, dataFile, outputFile, fileDelimitter);
            
            masticatorThread.start();
            threads.add(masticatorThread);
        }


        // Join spawned threads for all the tables
        for (MasticatorThread t : threads) {
            try {
                t.join();
                MasticatorResult masticatorResult = 
                    new MasticatorResult(t.getTableName(), 
                                         t.getOutFile(), 
                                         t.getTotalLinesWritten());
                masticatorResultByTable.put(masticatorResult.getTableName(), masticatorResult);
            }
            catch (InterruptedException iE) {
                throw new GobiiException(
                    "Unable to finish processing aspect file",
                    iE);
            }
        }

        return masticatorResultByTable;
    }

    protected void setupOutputDirectory() throws GobiiException {
        
        File outputDir = new File(loaderInstruction.getOutputDir());
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        if (! outputDir.isDirectory()) {
            throw new GobiiException(
                String.format(
                    "Output Path %s is not a directory",
                    loaderInstruction.getOutputDir()));
        }
        try {
            org.apache.commons.io.FileUtils.cleanDirectory(outputDir);
        }
        catch(IOException e) {
            throw new GobiiException("Unable to setup output directory for digester");
        }

    }

    protected FileHeader getFileHeaderByIdentifier(File file,
                                                   String headerIdentifier) throws GobiiException {
        String fileHeaderLine="";
        int headerLineNumberIndex = 0;
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(file);
        }
        catch (FileNotFoundException e) {
            throw new GobiiException("Input file does not exist");
        }
        while(fileScanner.hasNextLine() && 
              headerLineNumberIndex < maxLinesToLookForHeader) {
            fileHeaderLine = fileScanner.nextLine();
            if(fileHeaderLine.startsWith(headerIdentifier)) {
                break;
            }
            headerLineNumberIndex++;
        }
        fileScanner.close();

        if(headerLineNumberIndex == maxLinesToLookForHeader) {
            throw new GobiiException("Unable to read file header. " +
                                     "Could not find hapmap header in first 1000 lines.");
        }

        String[] headerColumns = fileHeaderLine.split(GobiiFileUtils.TAB_SEP);

        FileHeader fileHeader = new FileHeader();
        fileHeader.setHeaderLineNumber(headerLineNumberIndex);
        fileHeader.setHeaders(headerColumns);

        return fileHeader;
    }

    /**
     * @param file
     * @param headerLineNumber
     * @return
     * @throws GobiiException
     */
    protected FileHeader getFileHeaderByLineNumber(File file, 
                                                   int headerLineNumber) throws GobiiException {

        Integer headerLineNumberIndex = headerLineNumber;
        String fileHeaderLine="";
        Scanner fileScanner;

        if(headerLineNumber < 1) {
            throw new GobiiException("Invalid Header Line Number");
        }

        try {
            fileScanner = new Scanner(file);
        }
        catch (FileNotFoundException e) {
            throw new GobiiException("Input file does not exist");
        }
        while(headerLineNumberIndex > 0 && fileScanner.hasNextLine()) {
            fileHeaderLine = fileScanner.nextLine();
            headerLineNumberIndex--;
        }
        fileScanner.close();

        if(headerLineNumberIndex > 0) {
            throw new GobiiException("Unable to read file header. " +
                                     "File ended before reaching specified header line number");
        }

        String[] headerColumns = fileHeaderLine.split(GobiiFileUtils.TAB_SEP);

        FileHeader fileHeader = new FileHeader();
        fileHeader.setHeaderLineNumber(headerLineNumber-1);
        fileHeader.setHeaders(headerColumns);

        return fileHeader;
    }

    protected List<String> getLoadOrder() throws GobiiException {
        List<String> loaderInstructionList;
        try {

            MapType iflConfigMapType = mapper
                .getTypeFactory()
                .constructMapType(HashMap.class, String.class, IflConfig.class);

            Map<String, IflConfig> iflConfigMap = mapper.readValue(
                GobiiDigester.class.getResourceAsStream("/IFLConfig.json"),
                iflConfigMapType);

            if (iflConfigMap.containsKey(loaderInstruction.getLoadType())) {
                loaderInstructionList =
                    iflConfigMap.get(loaderInstruction.getLoadType()).getLoadOrder();
            } else {
                loaderInstructionList = new ArrayList<>();
            }
        }
        catch (IOException e) {
            throw new GobiiException(
                "Unable to finish processing aspect file",
                e);
        }
        return loaderInstructionList;
    }

    /**
     * @param propertyFieldNames
     * @return
     */
    protected Map<String, List<String>> getFileColumnsApiFieldsMap(
        Object apiTemplate,
        HashSet<String> propertyFieldNames) {

        if(propertyFieldNames == null) {
            propertyFieldNames = new HashSet<>();
        }
        Map<String, List<String>> fileColumnsApiFieldsMap = new HashMap<>();
        List<String> fileField;


        try {
            for (Field apiField : apiTemplate.getClass().getDeclaredFields()) {
                if (!apiField.isAnnotationPresent(GobiiAspectMaps.class)) {
                    continue;
                }

                apiField.setAccessible(true);
                if (propertyFieldNames.contains(apiField.getName())) {
                    Map<String, List<String>> properties =
                        (HashMap<String, List<String>>) apiField.get(apiTemplate);
                    for (String property : properties.keySet()) {
                        fileField = properties.get(property);
                        if (fileField.size() > 0) {
                            if (!fileColumnsApiFieldsMap.containsKey(fileField.get(0))) {
                                fileColumnsApiFieldsMap.put(fileField.get(0), new ArrayList<>());
                            }
                            fileColumnsApiFieldsMap
                                .get(fileField.get(0))
                                .add(apiField.getName() + "." + property);
                        }
                    }
                } else {
                    fileField = (List<String>) apiField.get(apiTemplate);
                    if (fileField.size() > 0) {
                        if (!fileColumnsApiFieldsMap.containsKey(fileField.get(0))) {
                            fileColumnsApiFieldsMap.put(fileField.get(0), new ArrayList<>());
                        }
                        fileColumnsApiFieldsMap.get(fileField.get(0)).add(apiField.getName());
                    }
                }
            }
            return fileColumnsApiFieldsMap;
        }
        catch (NullPointerException | IllegalAccessException e) {
            throw new GobiiException("Unable to map api fileds to file columns");
        }
    }

    /**
     * @param inputFilePath input file path given in instruction file.
     * @return  List of files to digest
     * @throws GobiiException when file does not exist
     */
    protected List<File> getFilesToDigest(List<FileDTO> fileDTOs) throws GobiiException {

        List<File> filesToDigest = new ArrayList<>();        

        for(FileDTO fileDTO : fileDTOs) {
            File inputFile = new File(fileDTO.getServerFilePath());
            if (inputFile.exists()) {

                if(inputFile.isDirectory()) {
                    File[] filesArray = inputFile.listFiles();
                    if(filesArray != null) {
                        filesToDigest.addAll(Arrays.asList(filesArray));
                    }
                }
                else if(inputFile.getName().endsWith(GobiiFileUtils.TAR_GUNZIP_EXTENSION) &&
                    inputFile.getName().endsWith(GobiiFileUtils.GUNZIP_EXTENSION)) {
                    filesToDigest = GobiiFileUtils.extractTarGunZipFile(inputFile);
                }
                else if(inputFile.getName().endsWith(GobiiFileUtils.GUNZIP_EXTENSION)) {
                    filesToDigest.add(GobiiFileUtils.extractGunZipFile(inputFile));
                }
                else {
                    filesToDigest.add(inputFile);
                }
            }
            else {
                throw new GobiiException("Input file does not exist");
            }
        }
        return filesToDigest;
    }

    protected void setPropertyAspect(Map<String, Object> aspectValues,
                                   ColumnAspect columnAspect,
                                   Map<String, Map<String, Cv>> propertiesCvMaps,
                                   String propertyName,
                                   String propertyGroup,
                                   Map<String, CvGroupTerm> propertyFieldsCvGroupMap
    ) throws GobiiException {

        JsonAspect jsonAspect;
        Map<String, Cv> cvMap;

        if (!aspectValues.containsKey(propertyGroup)) {
            // Initialize and set json aspect for properties field.
            jsonAspect = new JsonAspect();
            aspectValues.put(propertyGroup, jsonAspect);
        }
        else {
            jsonAspect = ((JsonAspect)aspectValues.get(propertyGroup));
        }

        if(!propertiesCvMaps.containsKey(propertyGroup)) {
            cvMap = getCvMapByTerm(propertyFieldsCvGroupMap.get(propertyGroup));
            propertiesCvMaps.put(propertyGroup, cvMap);
        }
        else {
            cvMap = propertiesCvMaps.get(propertyGroup);
        }
        if (cvMap.containsKey(propertyName)) {
            String propertyId = cvMap
                .get(propertyName)
                .getCvId()
                .toString();
            jsonAspect.getJsonMap().put(propertyId, columnAspect);
        }

    }

    protected Object getLoaderTemplate(Integer templateId,
                                       Class<?> templateType) throws GobiiException {
        Object template = null;
        try {
            LoaderTemplate loaderTemplate =
                loaderTemplateDao.getById(templateId);
            template = mapper.treeToValue(loaderTemplate.getTemplate(), templateType);
        }
        catch (JsonProcessingException e) {
            throw new GobiiException("Unable to read the template");
        }
        return template;
    }

    private Map<String, Cv> getCvMapByTerm(CvGroupTerm cvGroupTerm) throws GobiiException {
        Map<String, Cv> cvMap = new HashMap<>();
        CvDao cvDao = SpringContextLoaderSingleton.getInstance().getBean(CvDao.class);
        List<Cv> dnaRunPropertiesCvList = cvDao.getCvListByCvGroup(
            cvGroupTerm.getCvGroupName(),
            null);
        for(Cv cv : dnaRunPropertiesCvList) {
            cvMap.put(cv.getTerm(), cv);
        }
        return cvMap;
    }


}
