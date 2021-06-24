package org.gobiiproject.gobiimodel.dto.auditable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;
import org.gobiiproject.gobiimodel.dto.children.EntityPropertyDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;


/**
 * Created by Phil on 4/6/2016.
 */
public class ProjectDTO extends DTOBaseAuditable {

    public ProjectDTO() {
        super(GobiiEntityNameType.PROJECT);
    }

    @Override
    public Integer getId() {
        return this.projectId;
    }

    @Override
    public void setId(Integer id) {
        this.projectId = id;
    }


    // we are waiting until we a have a view to retirn
    // properties for that property: we don't know how to represent them yet

    private Integer projectId = 0;

    private String projectName;
    private String projectCode;
    private String projectDescription;
    private Integer piContact;
    private Integer projectStatus;

    @GobiiEntityParam(paramName = "projectStatus")
    public Integer getProjectStatus() {
        return projectStatus;
    }

    @GobiiEntityColumn(columnName = "status")
    public void setProjectStatus(Integer projectStatus) {
        this.projectStatus = projectStatus;
    }

    @GobiiEntityParam(paramName = "projectId")
    public Integer getProjectId() {
        return projectId;
    }

    @GobiiEntityColumn(columnName = "project_id")
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @GobiiEntityParam(paramName = "projectName")
    public String getProjectName() {
        return projectName;
    }


    @GobiiEntityColumn(columnName = "name")
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @GobiiEntityParam(paramName = "projectCode")
    public String getProjectCode() {
        return projectCode;
    }


    @GobiiEntityColumn(columnName = "code")
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    @GobiiEntityParam(paramName = "projectDescription")
    public String getProjectDescription() {
        return projectDescription;
    }

    @GobiiEntityColumn(columnName = "description")
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    @GobiiEntityParam(paramName = "piContact")
    public Integer getPiContact() {
        return piContact;
    }

    @GobiiEntityColumn(columnName = "pi_contact")
    public void setPiContact(Integer piContact) {
        this.piContact = piContact;
    }



    private Map<String, String> principleInvestigators = null;
    public Map<String, String> getPrincipleInvestigators() {
        return principleInvestigators;
    }
    public void setPrincipleInvestigators(Map<String, String> principleInvestigators) {
        this.principleInvestigators = principleInvestigators;
    }

    private List<EntityPropertyDTO> properties = new ArrayList<>();
    public List<EntityPropertyDTO> getProperties() {
        return properties;
    }
    public void setProperties(List<EntityPropertyDTO> properties) {
        this.properties = properties;
    }



}
