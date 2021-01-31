package org.gobiiproject.gobiiprocess.digester.aspectsdigest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.gobii.masticator.AspectMapper;
import org.gobii.masticator.Masticator;
import org.gobii.masticator.aspects.AspectParser;
import org.gobii.masticator.aspects.FileAspect;
import org.gobii.masticator.reader.ReaderResult;
import org.gobii.masticator.reader.TableReader;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.Val;
import org.gobiiproject.gobiidomain.services.gdmv3.Utils;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiCropConfig;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectMaps;
import org.gobiiproject.gobiimodel.dto.gdmv3.templates.DnaRunTemplateDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.DigesterResult;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.*;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.LoaderTemplate;
import org.gobiiproject.gobiimodel.utils.SimpleTimer;
import org.gobiiproject.gobiimodel.utils.email.ProcessMessage;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiiprocess.JobStatus;
import org.gobiiproject.gobiiprocess.digester.GobiiDigester;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.LoaderTemplateDao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public abstract class AspectDigest {

    final ObjectMapper mapper = new ObjectMapper();

    final String propertyGroupSeparator = ".";

    LoaderInstruction loaderInstruction;
    GobiiCropConfig cropConfig;
    JobStatus jobStatus;

    LoaderTemplateDao loaderTemplateDao;

    /**
     * Constructor
     *
     * @param loaderInstruction Instruction file with digest instrcution
     * @param configSettings    Configuration for GDM system
     * @throws GobiiException when unable to read crop configuration
     * or get job entity for given name.
     */
    AspectDigest(LoaderInstruction loaderInstruction,
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
    }

    abstract public DigesterResult digest();

    /**
     * Masticator is the module which process refactored instruction file.
     * To avoid making changes directly in masticator as it is maintained separately,
     * just duplicate parts of it.
     *
     */
    protected Map<String, File> masticate(Map<String, Table> aspects) throws GobiiException {

        Map<String, File> intermediateDigestFileMap = new HashMap<>();

        SimpleTimer.start("FileRead");
        FileAspect aspect;
        try {

            HashMap<String, Object> aspectMapObject = new HashMap<>();
            aspectMapObject.put("aspects", aspects);
            String loaderInstructionJson = new ObjectMapper().writeValueAsString(aspectMapObject);
            aspect = AspectParser.parse(loaderInstructionJson);
        } catch (JsonProcessingException e) {
            throw new GobiiException(
                String.format("Unable to process aspect file as json object"),
                e);
        }

        File dataFile = new File(loaderInstruction.getInputFile());
        if (! dataFile.exists()) {
            throw new GobiiException(
                String.format("Data file at %s does not exist", loaderInstruction.getInputFile()));
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

        Masticator masticator = new Masticator(aspect, dataFile);

        List<Thread> threads = new LinkedList<>();


        for (String table : aspect.getAspects().keySet()) {

            String outputFilePath =
                String.format("%s%sdigest.%s", outputDir.getAbsolutePath(), File.separator, table);

            File outputFile = new File(outputFilePath);

            intermediateDigestFileMap.put(table, outputFile);

            try {
                outputFile.createNewFile();
            }
            catch (IOException ioE) {
                throw new GobiiException(
                    String.format("Unable to create digest files %s", outputFilePath));
            }

            final Thread t = new Thread(() -> {
                try (FileWriter fileWriter = new FileWriter(outputFile, true);
                     BufferedWriter writer = new BufferedWriter(fileWriter);) {
                    masticator.run(table, writer);
                } catch (IOException e) {
                    throw new GobiiException(
                        String.format("IOException while processing {}", table),
                        e);
                }
            });

            t.start();

            threads.add(t);
        }


        for (Thread t : threads) {
            try {
                t.join();
            }
            catch (InterruptedException iE) {
                throw new GobiiException(
                    "Unable to finish processing aspect file",
                    iE);
            }
        }



        return intermediateDigestFileMap;


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
