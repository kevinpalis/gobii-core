package org.gobiiproject.gobiidtomapping.impl;

import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsCvDao;
import org.gobiiproject.gobiidao.resultset.access.RsProjectDao;
import org.gobiiproject.gobiidao.resultset.core.ParamExtractor;
import org.gobiiproject.gobiidao.resultset.sqlworkers.read.SpGetProjectDetailsByProjectId;
import org.gobiiproject.gobiidtomapping.DtoMapCv;
import org.gobiiproject.gobiidtomapping.DtoMapProject;
import org.gobiiproject.gobiidtomapping.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.container.ProjectDTO;
import org.gobiiproject.gobiimodel.dto.container.EntityPropertyDTO;
import org.gobiiproject.gobiimodel.dto.header.DtoHeaderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Phil on 4/6/2016.
 */
public class DtoMapProjectImpl implements DtoMapProject {

    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdListImpl.class);

    @Autowired
    private RsProjectDao rsProjectDao;

    public ProjectDTO getProjectDetail(ProjectDTO projectDTO) throws GobiiDtoMappingException {


        ProjectDTO returnVal = new ProjectDTO();

        try {

            ResultSet resultSet = rsProjectDao.getProjectDetailsForProjectId(projectDTO.getProjectId());

            boolean retrievedOneRecord = false;
            while (resultSet.next()) {

                if (true == retrievedOneRecord) {
                    throw (new GobiiDtoMappingException("There are more than one project records for project id: " + projectDTO.getProjectId()));
                }


                retrievedOneRecord = true;

                int projectId = resultSet.getInt("project_id");
                String projectName = resultSet.getString("name");
                String projectCode = resultSet.getString("code");
                String projectDescription = resultSet.getString("description");
                int piContact = resultSet.getInt("pi_contact");
                int modifiedBy = resultSet.getInt("modified_by");
                Date modifiedDate = resultSet.getDate("modified_date");
                int createdBy = resultSet.getInt("created_by");
                int projectStatus = resultSet.getInt("status");
                Date createdDate = resultSet.getDate("created_date");

                returnVal.setProjectId(projectId);
                returnVal.setProjectName(projectName);
                returnVal.setProjectCode(projectCode);
                returnVal.setProjectDescription(projectDescription);
                returnVal.setPiContact(piContact);
                returnVal.setModifiedDate(modifiedDate);
                returnVal.setModifiedBy(modifiedBy);
                returnVal.setCreatedDate(createdDate);
                returnVal.setCreatedBy(createdBy);
                returnVal.setProjectStatus(projectStatus);
            }


            addPropertiesToProject(returnVal);

        } catch (SQLException e) {
            returnVal.getDtoHeaderResponse().addException(e);
            LOGGER.error(e.getMessage());
        } catch (GobiiDaoException e) {
            returnVal.getDtoHeaderResponse().addException(e);
            LOGGER.error(e.getMessage());
        }


        return returnVal;
    }

    private void addPropertiesToProject(ProjectDTO projectDTO) throws GobiiDaoException, SQLException {

        ResultSet propertyResultSet = rsProjectDao.getPropertiesForProject(projectDTO.getProjectId());
        while (propertyResultSet.next()) {
            String propertyName = propertyResultSet.getString("property_name");
            String propertyValue = propertyResultSet.getString("property_value");
            Integer propertyId = propertyResultSet.getInt("property_id");
            EntityPropertyDTO currentProjectProperty =
                    new EntityPropertyDTO(projectDTO.getProjectId(),
                            propertyId,
                            propertyName,
                            propertyValue);
            projectDTO.getProperties().add(currentProjectProperty);
        }

    }

    private boolean validateProjectRequest(ProjectDTO projectDTO) {

        boolean returnVal = true;

        try {

            String projectName = projectDTO.getProjectName();
            Integer piContactId = projectDTO.getPiContact();
            ResultSet resultSetExistingProject =
                    rsProjectDao.getProjectsByNameAndPiContact(projectName, piContactId);

            if (resultSetExistingProject.next()) {
                projectDTO.getDtoHeaderResponse().addStatusMessage(DtoHeaderResponse.StatusLevel.OK,
                        DtoHeaderResponse.ValidationStatusType.VALIDATION_COMPOUND_UNIQUE,
                        "A project with name " + projectName + " and contact id " + piContactId + "already exists");
            }

        } catch (SQLException e) {
            projectDTO.getDtoHeaderResponse().addException(e);
            LOGGER.error(e.getMessage());
            returnVal = false;

        } catch (GobiiDaoException e) {
            projectDTO.getDtoHeaderResponse().addException(e);
            LOGGER.error(e.getMessage());
            returnVal = false;
        }


        return returnVal;

    }

    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO) throws GobiiDtoMappingException {

        ProjectDTO returnVal = projectDTO;

        try {


            if (validateProjectRequest(returnVal)) {

                Map<String, Object> parameters = ParamExtractor.makeParamVals(projectDTO);

                Integer projectId = rsProjectDao.createProject(parameters);
                returnVal.setProjectId(projectId);

                List<EntityPropertyDTO> projectProperties = projectDTO.getProperties();

                upsertProjectProperties(projectId, projectProperties);

            }

        } catch (GobiiDaoException e) {
            returnVal.getDtoHeaderResponse().addException(e);
            LOGGER.error(e.getMessage());
        }

        return returnVal;
    }

    private void upsertProjectProperties(Integer projectId, List<EntityPropertyDTO> projectProperties) throws GobiiDaoException {

        for (EntityPropertyDTO currentProperty : projectProperties) {

            Map<String, Object> spParamsParameters = new HashMap<>();
            spParamsParameters.put("projectId", projectId);
            spParamsParameters.put("propertyName", currentProperty.getPropertyName());
            spParamsParameters.put("propertyValue", currentProperty.getPropertyValue());

            Integer propertyId = rsProjectDao.createUpdateProperty(spParamsParameters);
            currentProperty.setEntityIdId(projectId);
            currentProperty.setPropertyId(propertyId);
        }

    }

    @Override
    public ProjectDTO updateProject(ProjectDTO projectDTO) throws GobiiDtoMappingException {

        ProjectDTO returnVal = projectDTO;

        try {

            Map<String, Object> parameters = ParamExtractor.makeParamVals(projectDTO);
            rsProjectDao.updateProject(parameters);

            List<EntityPropertyDTO> projectProperties = projectDTO.getProperties();

            upsertProjectProperties(returnVal.getProjectId(), projectProperties);


        } catch (GobiiDaoException e) {
            returnVal.getDtoHeaderResponse().addException(e);
            LOGGER.error(e.getMessage());
        }

        return returnVal;

    }
}
