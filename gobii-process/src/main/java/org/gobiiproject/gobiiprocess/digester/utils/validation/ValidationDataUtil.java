package org.gobiiproject.gobiiprocess.digester.utils.validation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.entity.*;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.Failure;
import org.gobiiproject.gobiiprocess.digester.utils.validation.errorMessage.FailureTypes;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;
import org.gobiiproject.gobiisampletrackingdao.*;

import java.util.*;
import java.util.stream.Collectors;

public class ValidationDataUtil {

    private final static int pageSize = 1000;

    static final int MAX_ENTITIES_PER_QUERY = 2000;

    private static <T> T getBean(Class<T> objectType) {
        return SpringContextLoaderSingleton.getInstance().getBean(objectType);
    }

    /**
     * Gets the allowed values for foreign key
     */
    public static Map<String, String> getAllowedForeignKeyList(String foreignKey,
                                                               List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        Map<String, String> foreignKeyIdNameMap = new HashMap<>();
        try {
            int offset = 0;
            if (foreignKey.equalsIgnoreCase(ValidationConstants.LINKAGE_GROUP)) {
                List<Mapset> mapsets = new ArrayList<>();
                MapsetDao mapsetDao = getBean(MapsetDao.class);
                while (mapsets.size() == pageSize || offset == 0) {
                    mapsets = mapsetDao.getMapsets(pageSize, offset, null);
                    mapsets.forEach(mapset -> {
                        foreignKeyIdNameMap.put(
                            mapset.getMapsetId().toString(),
                            mapset.getMapsetName());
                    });
                    offset += pageSize;
                }
            } else if (foreignKey.equalsIgnoreCase(ValidationConstants.DNARUN)) {
                List<Experiment> experiments = new ArrayList<>();
                ExperimentDao experimentDao = getBean(ExperimentDao.class);
                while (experiments.size() == pageSize || offset == 0) {
                    experiments = experimentDao.getExperiments(pageSize, 0, null);
                    experiments.forEach(experiment -> {
                        foreignKeyIdNameMap.put(
                            experiment.getExperimentId().toString(),
                            experiment.getExperimentName());
                    });
                    offset += pageSize;
                }
            } else if (foreignKey.equalsIgnoreCase(ValidationConstants.DNASAMPLE_NAME)) {
                List<Project> projects = new ArrayList<>();
                ProjectDao projectDao = getBean(ProjectDao.class);
                while (projects.size() == pageSize || offset == 0) {
                    projects = projectDao.getProjects(offset/pageSize, pageSize, null);
                    projects.forEach(project -> {
                        foreignKeyIdNameMap.put(
                            project.getProjectId().toString(),
                            project.getProjectName());
                    });
                    offset += pageSize;
                }
            } else {
                ValidationUtil.createFailure(FailureTypes.UNDEFINED_FOREIGN_KEY,
                    new ArrayList<>(),
                    foreignKey,
                    failureList);
            }
        } catch (Exception e) {
            ValidationUtil.createFailure(FailureTypes.EXCEPTION,
                new ArrayList<>(),
                e.getMessage(),
                failureList);
        }
        return foreignKeyIdNameMap;
    }

    /**
     * Verifies whether the Platform Id is valid or not
     */
    public static Map<String, String> validatePlatformId(String platformId,
                                                         List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        Map<String, String> platformIdNameMap = new HashMap<>();
        try {
            Integer intPlatformId = Integer.parseInt(platformId);
            PlatformDao platformDao = getBean(PlatformDao.class);
            Platform platform = platformDao.getPlatform(intPlatformId);
            if(platform != null) {
                platformIdNameMap.put(platform.getPlatformId().toString(),
                    platform.getPlatformName());
            }
            else {
                ValidationUtil.createFailure(FailureTypes.UNDEFINED_PLATFORM_ID,
                    new ArrayList<>(),
                    "Platform not found",
                    failureList);
            }
        } catch (Exception e) {
            ValidationUtil.createFailure(FailureTypes.EXCEPTION,
                new ArrayList<>(),
                e.getMessage(),
                failureList);
        }
        return platformIdNameMap;
    }

    /**
     * Verifies whether the Project Id is valid or not
     */
    public static Map<String, String> validateProjectId(String projectId,
                                                        List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        Map<String, String> projectIdNameMap = new HashMap<>();
        try {
            Integer intProjectId = Integer.parseInt(projectId);
            ProjectDao projectDao = getBean(ProjectDao.class);
            Project project = projectDao.getProject(intProjectId);
            if(project != null) {
                projectIdNameMap.put(project.getProjectId().toString(),
                    project.getProjectName());
            }
            else {
                ValidationUtil.createFailure(FailureTypes.UNDEFINED_PROJECT_ID,
                    new ArrayList<>(),
                    "Project not found",
                    failureList);
            }
        } catch (Exception e) {
            ValidationUtil.createFailure(FailureTypes.EXCEPTION,
                new ArrayList<>(),
                e.getMessage(),
                failureList);
        }
        return projectIdNameMap;
    }

    /**
     * Verifies whether the Experiment Id is valid or not
     */
    public static Map<String, String> validateExperimentId(String experimentId,
                                                           List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        Map<String, String> experimentIdNameMap = new HashMap<>();
        try {
            Integer intExperimentId = Integer.parseInt(experimentId);
            ExperimentDao experimentDao = getBean(ExperimentDao.class);
            Experiment experiment = experimentDao.getExperiment(intExperimentId);
            if(experiment != null) {
                experimentIdNameMap.put(
                    experiment.getExperimentId().toString(),
                    experiment.getExperimentName());
            }
            else {
                ValidationUtil.createFailure(FailureTypes.UNDEFINED_EXPERIMENT_ID,
                    new ArrayList<>(),
                    "Experiment not found",
                    failureList);
            }
        } catch (Exception e) {
            ValidationUtil.createFailure(FailureTypes.EXCEPTION,
                new ArrayList<>(),
                e.getMessage(),
                failureList);
        }
        return experimentIdNameMap;
    }

    public static Map<String, String> validateMapId(String mapId,
                                                    List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        Map<String, String> mapsetIdNameMap = new HashMap<>();
        try {
            Integer intMapSetId = Integer.parseInt(mapId);
            MapsetDao mapsetDao = getBean(MapsetDao.class);
            Mapset mapset = mapsetDao.getMapset(intMapSetId);
            if(mapset != null) {
                mapsetIdNameMap.put(
                    mapset.getMapsetId().toString(),
                    mapset.getMapsetName());
            }
            else {
                ValidationUtil.createFailure(FailureTypes.UNDEFINED_MAP_ID,
                    new ArrayList<>(),
                    "Genome Map not found",
                    failureList);
            }
        } catch (Exception e) {
            ValidationUtil.createFailure(
                FailureTypes.EXCEPTION,
                new ArrayList<>(),
                e.getMessage(),
                failureList);
        }
        return mapsetIdNameMap;
    }


    public static List<String> validateNames(List<String> names,
                                             String gobiiEntityNameType,
                                             String filterValue,
                                             List<Failure> failureList
    ) throws MaximumErrorsValidationException {

        List<String> results = new ArrayList<>();
        if(gobiiEntityNameType.toLowerCase().equals("cv")) {
            Set<String> namesSet = new HashSet<>(names);
            results = findInvalidCvNames(namesSet, filterValue, failureList);
        }
        else {
            int numEntities = names.size();
            int maxEntitiesPerCall = MAX_ENTITIES_PER_QUERY;

            for (int i = 0; i < numEntities; i += maxEntitiesPerCall) {
                Set<String> namesSubSet;
                try {
                    namesSubSet = new HashSet<>(names.subList(i, i + maxEntitiesPerCall));
                } catch (IndexOutOfBoundsException e) {
                    namesSubSet = new HashSet<>(names.subList(i, numEntities));
                }
                results.addAll(findInvalidNames(namesSubSet,
                    gobiiEntityNameType,
                    filterValue,
                    failureList));
            }
        }
        return results;
    }

    public static List<String> validateSampleNums(List<DnaSample> queryParams,
                                                  String filterValue,
                                                  List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        List<String> results = new ArrayList<>();
        try {
            int numEntities = queryParams.size();

            int maxEntitiesPerCall = MAX_ENTITIES_PER_QUERY;

            Set<String> dnaSampleNameNumSet =
                queryParams
                    .stream()
                    .map(dnaSample -> {
                        if(StringUtils.isNotEmpty(dnaSample.getDnaSampleNum())) {
                            return dnaSample.getDnaSampleName() + dnaSample.getDnaSampleNum();
                        }
                        return dnaSample.getDnaSampleName();
                    }).collect(Collectors.toSet());


            for(int i=0; i < numEntities; i+=maxEntitiesPerCall) {
                List<DnaSample> queryParamsSubList = queryParams.subList(i, i+maxEntitiesPerCall);
                DnaSampleDao dnaSampleDao = getBean(DnaSampleDao.class);
                Integer intProjectId = Integer.parseInt(filterValue);
                List<DnaSample> dnaSamples = dnaSampleDao.queryByNameAndNum(
                    queryParamsSubList,
                    intProjectId);
                for(DnaSample dnaSample : dnaSamples) {
                    String dnaSampleNameNum = dnaSample.getDnaSampleName();
                    if(StringUtils.isNotEmpty(dnaSample.getDnaSampleNum())) {
                        dnaSampleNameNum += dnaSample.getDnaSampleNum();
                    }
                    if(!dnaSampleNameNumSet.contains(dnaSampleNameNum)) {
                        results.add(dnaSample.getDnaSampleName());
                    }
                }
            }
        } catch (Exception e) {
            ValidationUtil.createFailure(FailureTypes.EXCEPTION,
                new ArrayList<>(),
                e.getMessage(),
                failureList);
        }
        return results;
    }


    private static List<String> findInvalidCvNames(Set<String> names,
                                                   String filterValue,
                                                   List<Failure> failureList
    ) throws MaximumErrorsValidationException {
        List<String> invalidNames = new ArrayList<>();
        CvGroupTerm cvGroupTerm;
        try {

            switch (filterValue) {
                case "species_name":
                    cvGroupTerm = CvGroupTerm.CVGROUP_GERMPLASM_SPECIES;
                    break;
                case "type_name":
                    cvGroupTerm = CvGroupTerm.CVGROUP_GERMPLASM_TYPE;
                    break;
                case "strand_name":
                    cvGroupTerm = CvGroupTerm.CVGROUP_MARKER_STRAND;
                    break;
                default:
                    ValidationUtil.createFailure(FailureTypes.UNDEFINED_CV,
                        new ArrayList<>(),
                        filterValue,
                        failureList);
                    return invalidNames;
            }

            // Get Cvs for given cvgroup
            CvDao cvDao = getBean(CvDao.class);
            List<Cv> cvs = cvDao.getCvListByCvGroup(cvGroupTerm.getCvGroupName(), null);

            // Remove valid cv names from input names
            Set<String> validCvNames = cvs.stream().map(Cv::getTerm).collect(Collectors.toSet());
            names.removeAll(validCvNames);
            return new ArrayList<>(names);
        }
        catch (MaximumErrorsValidationException e) {
            throw e;
        } catch (Exception e) {
            ValidationUtil.createFailure(FailureTypes.EXCEPTION,
                new ArrayList<>(),
                e.getMessage(),
                failureList);
        }
        return invalidNames;
    }


    /**
     *
     * @param names
     * @param gobiiEntityNameType
     * @param filterValue
     * @param failureList
     * @return
     * @throws MaximumErrorsValidationException
     */
    private static List<String> findInvalidNames(Set<String> names,
                                                 String gobiiEntityNameType,
                                                 String filterValue,
                                                 List<Failure> failureList
    ) throws MaximumErrorsValidationException {

        List<String> invalidNames = new ArrayList<>();
        Integer intFilterValue;

        try {
            intFilterValue = Integer.parseInt(filterValue);
        } catch (NumberFormatException nE) {
            ValidationUtil.createFailure(FailureTypes.UNDEFINED_FOREIGN_KEY,
                new ArrayList<>(),
                filterValue,
                failureList);
            return invalidNames;
        }

        try {
            switch (gobiiEntityNameType.toLowerCase()) {
                case "dnarun":
                    invalidNames = findInvalidDnaRunNames(names, intFilterValue);
                    break;
                case "dnasample":
                    invalidNames = findInvalidDnaSampleNames(names, intFilterValue);
                    break;
                case "germplasm":
                    invalidNames = findInvalidGermplasmExternalCodes(names);
                    break;
                case "linkage_group":
                    invalidNames = findInvalidLinkageGroupNames(names, intFilterValue);
                    break;
                case "marker":
                    invalidNames = findInvalidMarkerNames(names, intFilterValue);
                    break;
                case "reference":
                    invalidNames = findInvalidReferences(names);
                    break;
                case "platform":
                    invalidNames = findInvalidPlatforms(names);
                    break;
                default:
                    ValidationUtil.createFailure(FailureTypes.UNDEFINED_CONDITION_TYPE,
                        new ArrayList<>(),
                        filterValue,
                        failureList);
                    return invalidNames;
            }
        }
        catch (MaximumErrorsValidationException e) {
            throw e;
        } catch (Exception e) {
            ValidationUtil.createFailure(FailureTypes.EXCEPTION,
                new ArrayList<>(),
                e.getMessage(),
                failureList);
        }
        return invalidNames;
    }



    /**
     * @param names         List of dnaRun names for which id needs to be fetched
     * @param filterValue   for DnaRun filter value is experiment id
     * @return NameIdDto list
     */
    private static List<String> findInvalidDnaRunNames(Set<String> names,
                                                       Integer filterValue
    ) throws GobiiDaoException {

        List<String> invalidNames = new ArrayList<>();
        Set<String> validNames = new HashSet<>();

        // Exit to make sure empty nameset not getting queried
        if(CollectionUtils.isEmpty(names)) {
            return invalidNames;
        }

        DnaRunDao dnaRunDao = getBean(DnaRunDao.class);
        List<DnaRun> dnaRuns = new ArrayList<>();
        Integer pageSize = names.size();
        Integer rowOffset = 0;

        while (rowOffset == 0 || dnaRuns.size() == pageSize) {
            dnaRuns = dnaRunDao.getDnaRunsByDnaRunNames(
                names,
                filterValue,
                pageSize,
                rowOffset);
            for(DnaRun dnaRun : dnaRuns) {
                validNames.add(dnaRun.getDnaRunName());
            }
            rowOffset += pageSize;
        }

        names.removeAll(validNames);

        return new ArrayList<>(names);
    }

    private static List<String> findInvalidLinkageGroupNames(Set<String> names,
                                                             Integer filterValue
    ) throws GobiiDaoException {

        List<String> invalidNames = new ArrayList<>();
        Set<String> validNames = new HashSet<>();

        // Exit to make sure empty nameset not getting queried
        if(CollectionUtils.isEmpty(names)) {
            return invalidNames;
        }

        LinkageGroupDao linkageGroupDao = getBean(LinkageGroupDao.class);
        List<LinkageGroup> linkageGroups = new ArrayList<>();
        Integer pageSize = names.size();
        Integer rowOffset = 0;

        while (rowOffset == 0 || linkageGroups.size() == pageSize) {
            linkageGroups = linkageGroupDao.getLinkageGroupsByNames(
                names,
                filterValue,
                pageSize,
                rowOffset);
            for(LinkageGroup linkageGroup : linkageGroups) {
                validNames.add(linkageGroup.getLinkageGroupName());
            }
            rowOffset += pageSize;
        }

        names.removeAll(validNames);

        return new ArrayList<>(names);
    }

    private static List<String> findInvalidReferences(Set<String> names
    ) throws GobiiDaoException {

        List<String> invalidNames = new ArrayList<>();
        Set<String> validNames = new HashSet<>();

        // Exit to make sure empty nameset not getting queried
        if(CollectionUtils.isEmpty(names)) {
            return invalidNames;
        }

        ReferenceDao referenceDao = getBean(ReferenceDao.class);
        List<Reference> references  = new ArrayList<>();
        Integer pageSize = names.size();
        Integer rowOffset = 0;

        while (rowOffset == 0 || references.size() == pageSize) {
            references = referenceDao.getReferences(
                names,
                pageSize,
                rowOffset);
            for(Reference reference : references) {
                validNames.add(reference.getReferenceName());
            }
            rowOffset += pageSize;
        }

        names.removeAll(validNames);

        return new ArrayList<>(names);
    }

    private static List<String> findInvalidPlatforms(Set<String> names
    ) throws GobiiDaoException {

        List<String> invalidNames = new ArrayList<>();
        Set<String> validNames = new HashSet<>();

        // Exit to make sure empty nameset not getting queried
        if(CollectionUtils.isEmpty(names)) {
            return invalidNames;
        }

        PlatformDao platformDao = getBean(PlatformDao.class);
        List<Platform> platforms  = new ArrayList<>();
        Integer pageSize = names.size();
        Integer rowOffset = 0;

        while (rowOffset == 0 || platforms.size() == pageSize) {
            platforms = platformDao.getPlatforms(
                names,
                pageSize,
                rowOffset);
            for(Platform platform : platforms) {
                validNames.add(platform.getPlatformName());
            }
            rowOffset += pageSize;
        }

        names.removeAll(validNames);

        return new ArrayList<>(names);
    }

    private static List<String> findInvalidMarkerNames(Set<String> names,
                                                       Integer filterValue
    ) throws GobiiDaoException {

        List<String> invalidNames = new ArrayList<>();
        Set<String> validNames = new HashSet<>();

        // Exit to make sure empty nameset not getting queried
        if(CollectionUtils.isEmpty(names)) {
            return invalidNames;
        }

        MarkerDao markerDao = getBean(MarkerDao.class);
        List<Marker> markers = new ArrayList<>();
        Integer pageSize = names.size();
        Integer rowOffset = 0;

        while (rowOffset == 0 || markers.size() == pageSize) {
            markers = markerDao.queryMarkersByNamesAndPlatformId(
                names,
                filterValue,
                pageSize,
                rowOffset);
            for(Marker marker : markers) {
                validNames.add(marker.getMarkerName());
            }
            rowOffset += pageSize;
        }

        names.removeAll(validNames);

        return new ArrayList<>(names);
    }

    private static List<String> findInvalidGermplasmExternalCodes(
        Set<String> names) throws GobiiDaoException {

        List<String> invalidNames = new ArrayList<>();
        Set<String> validNames = new HashSet<>();

        // Exit to make sure empty nameset not getting queried
        if(CollectionUtils.isEmpty(names)) {
            return invalidNames;
        }

        GermplasmDao germplasmDao = getBean(GermplasmDao.class);
        List<Germplasm> germplasms = new ArrayList<>();

        Integer pageSize = names.size();
        Integer rowOffset = 0;

        while (rowOffset == 0 || germplasms.size() == pageSize) {
            germplasms = germplasmDao.getGermplamsByExternalCodes(
                names,
                pageSize,
                rowOffset);
            for(Germplasm germplasm : germplasms) {
                validNames.add(germplasm.getExternalCode());
            }
            rowOffset += pageSize;
        }

        names.removeAll(validNames);

        return new ArrayList<>(names);
    }

    /**
     * @param names         List of dnaRun names for which id needs to be fetched
     * @param filterValue   for DnaSample filter value is project id
     * @return NameIdDto list
     */
    private static List<String> findInvalidDnaSampleNames(Set<String> names,
                                                          Integer filterValue
    ) throws GobiiDaoException {

        List<String> invalidNames = new ArrayList<>();
        Set<String> validNames = new HashSet<>();

        if(CollectionUtils.isEmpty(names)) {
            return invalidNames;
        }

        DnaSampleDao dnaSampleDao = getBean(DnaSampleDao.class);

        List<DnaSample> dnaSamples = new ArrayList<>();
        Integer pageSize = names.size();
        Integer offset = 0;

        while(offset == 0 || dnaSamples.size() == pageSize) {
            dnaSamples = dnaSampleDao.getDnaSamples(
                names,
                filterValue,
                pageSize,
                offset);
            for(DnaSample dnaSample : dnaSamples) {
                validNames.add(dnaSample.getDnaSampleName());
            }
            offset += pageSize;
        }

        names.removeAll(validNames);

        return new ArrayList<>(names);
    }

}
