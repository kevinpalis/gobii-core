package org.gobiiproject.gobiidtomapping.entity.auditable.impl.sampletracking;

import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsProjectDao;
import org.gobiiproject.gobiidao.resultset.core.ParamExtractor;
import org.gobiiproject.gobiidao.resultset.core.ResultColumnApplicator;
import org.gobiiproject.gobiidao.resultset.core.listquery.DtoListQueryColl;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
import org.gobiiproject.gobiidtomapping.core.EntityProperties;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.auditable.sampletracking.DtoMapProject;
import org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIdListImpl;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.auditable.sampletracking.ProjectDTO;
import org.gobiiproject.gobiimodel.dto.children.EntityPropertyDTO;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DtoMapProjectImpl implements DtoMapProject {
    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdListImpl.class);

    @Autowired
    private RsProjectDao rsSampleTrackingProjectDao;

    @Autowired
    private DtoListQueryColl dtoListSampleTrackingQueryColl;


    @Override
    public List<ProjectDTO> getList() throws GobiiDtoMappingException {
        List<ProjectDTO> returnVal = this.getList(null, null);
        return returnVal;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProjectDTO> getList(Integer pageToken,
                                    Integer pageSize) throws GobiiDtoMappingException {
        List<ProjectDTO> returnVal = null;
        try {

            Map<String, Object> sqlParams = new HashMap<>();

            if(pageToken != null) {
                sqlParams.put("pageToken", pageToken);
            }

            if(pageSize != null) {
                sqlParams.put("pageSize", pageSize);
            }

            returnVal = (List<ProjectDTO>) dtoListSampleTrackingQueryColl.getList(
                    ListSqlId.QUERY_ID_PROJECT_ALL,
                    null,
                    sqlParams
            );

            if(returnVal == null) {
                return new ArrayList<>();
            }

        }
        catch (GobiiException gE) {

            LOGGER.error(gE.getMessage(), gE);

            throw new GobiiDtoMappingException(
                    gE.getGobiiStatusLevel(),
                    gE.getGobiiValidationStatusType(),
                    gE.getMessage());

        }
        catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }
        return returnVal;
    }


    public ProjectDTO get(Integer projectId) throws GobiiDtoMappingException {


        ProjectDTO returnVal = new ProjectDTO();

        try {
            ResultSet resultSet = rsSampleTrackingProjectDao.getProjectDetailsForProjectId(projectId);
            if(resultSet.next()) {
                ResultColumnApplicator.applyColumnValues(resultSet, returnVal);
                if(resultSet.next()) {
                    throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                            GobiiValidationStatusType.VALIDATION_NOT_UNIQUE,
                            "Multiple resources found. Violation of Unique Project Id constraint." +
                                    " Please contact your Data Administrator to resolve this. " +
                                    "Changing underlying database schemas and constraints " +
                                    "without consulting GOBii Team is not recommended.");
                }
            }
            else {
                throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "Project not found for given id.");
            }
        }
        catch (GobiiException gE) {
            LOGGER.error(gE.getMessage(), gE);
            throw new GobiiDtoMappingException(
                    gE.getGobiiStatusLevel(),
                    gE.getGobiiValidationStatusType(),
                    gE.getMessage());
        }
        catch (Exception e) {
            LOGGER.error("Gobii Mapping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    public ProjectDTO get(String projectName) throws GobiiDtoMappingException {

        ProjectDTO returnVal = new ProjectDTO();
        return returnVal;
    }


    @Override
    public ProjectDTO create(ProjectDTO projectDTO) throws GobiiDtoMappingException {

        try {

            ProjectDTO returnVal = projectDTO;
            Map<String, Object> parameters = ParamExtractor.makeParamVals(projectDTO);
            Integer projectId = rsSampleTrackingProjectDao.createProject(parameters);
            upsertProjectProperties(projectId, projectDTO.getProperties());
            returnVal.setId(projectId);
            return returnVal;

        }
        catch (GobiiException gE) {
            LOGGER.error(gE.getMessage(), gE);
            throw new GobiiDtoMappingException(
                    gE.getGobiiStatusLevel(),
                    gE.getGobiiValidationStatusType(),
                    gE.getMessage());
        }
        catch (Exception e) {
            LOGGER.error("Gobii Mapping Error", e);
            throw new GobiiDtoMappingException(e);
        }
    }

    private void upsertProjectProperties(
            Integer projectId,
            Map<String, String> projectProperties) throws GobiiDaoException {

        for (Map.Entry<String, String> currentProperty : projectProperties.entrySet()) {
            EntityPropertyDTO entityProperty = new EntityPropertyDTO(
                    null, null,
                    currentProperty.getKey(), currentProperty.getValue());
            Map<String, Object> spParamsParameters =
                    EntityProperties.propertiesToParams(projectId, entityProperty);

            //Integer propertyId = 
            rsSampleTrackingProjectDao.createUpdateProjectProperty(spParamsParameters);
        }

    }


    @Override
    public ProjectDTO replace(Integer projectId, ProjectDTO projectDTO) throws GobiiDtoMappingException {

        ProjectDTO returnVal = projectDTO;

        try {

            Map<String, Object> parameters = ParamExtractor.makeParamVals(returnVal);
            parameters.put("projectId", projectId);
            rsSampleTrackingProjectDao.updateProject(parameters);


        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;

    }

    @Override
    public List<ProjectDTO> getProjectsForLoadedDatasets() throws GobiiDtoMappingException {

        List<ProjectDTO> returnVal = new ArrayList<>();

        return  returnVal;
    }


}
