
package org.gobiiproject.gobiimodel.dto.auditable.sampletracking;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Project;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true, value={
        "id", "allowedProcessTypes", "entityNameType"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDTO extends DTOBaseAuditable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GobiiEntityMap(paramName="projectId", entity = Project.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer projectId;

    @GobiiEntityMap(paramName="piContactId", entity=Project.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer piContactId;

    @GobiiEntityMap(paramName="projectName", entity=Project.class)
    private String projectName;

    @GobiiEntityMap(paramName="status.term", entity = Project.class, deep=true)
    private String projectStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GobiiEntityMap(paramName="projectCode", entity=Project.class)
    private String projectCode;

    @GobiiEntityMap(paramName="projectDescription", entity=Project.class)
    private String projectDescription;

    private Map<String, String> properties = new HashMap<>();

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

    public Integer getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Integer id) {
        this.projectId = id;
    }

    public Integer getPiContactId() {
        return this.piContactId;
    }

    public void setPiContactId(Integer piContactId) {
        this.piContactId = piContactId;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectStatus() {
        return this.projectStatus;
    }

    public void setProjectStatus(String status) {
        this.projectStatus = status;
    }

    public String getProjectCode() {
        return this.projectCode;
    }

    public void setProjectCode(String code) {
        this.projectCode = code;
    }

    public String getProjectDescription() {
        return this.projectDescription;
    }

    public void setProjectDescription(String description) {
        this.projectDescription = description;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
