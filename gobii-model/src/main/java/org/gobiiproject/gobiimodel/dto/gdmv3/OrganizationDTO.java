package org.gobiiproject.gobiimodel.dto.gdmv3;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.Organization;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"id", "allowedProcessTypes", "entityNameType", "status"})
@JsonInclude(JsonInclude.Include.ALWAYS)
public class OrganizationDTO extends DTOBaseAuditable  {

    public static interface Create{}

    @Override
    public Integer getId() {
        return organizationId;
    }

    @Override
    public void setId(Integer id) {
    }
 
    public OrganizationDTO() {
        super(GobiiEntityNameType.ORGANIZATION);
    }

    @GobiiEntityMap(paramName = "organizationId", entity = Organization.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer organizationId;

    @GobiiEntityMap(paramName = "name", entity = Organization.class)
    @NotEmpty(groups = {OrganizationDTO.Create.class})
    private String organizationName;

    @GobiiEntityMap(paramName = "address", entity = Organization.class)
    private String organizationAddress;
   
    @GobiiEntityMap(paramName = "website", entity = Organization.class )
    private String organizationWebsite;
}