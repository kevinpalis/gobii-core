/**
 * ExperimentDTO.java
 * 
 * Experiment DTO Class
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-28
 */

package org.gobiiproject.gobiimodel.dto.gdmv3;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonIgnoreProperties(ignoreUnknown = false, value={
    "id", "allowedProcessTypes", "entityNameType", "status"
})
@EqualsAndHashCode(callSuper=false)
public class ExperimentDTO extends DTOBaseAuditable {
    
    public interface Create {}
    public interface Update {}

    public ExperimentDTO() {
        super(GobiiEntityNameType.EXPERIMENT);
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public void setId(Integer id) {

    }

    @GobiiEntityMap(paramName = "experimentId", entity = Experiment.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer experimentId;

    @GobiiEntityMap(paramName = "experimentName", entity = Experiment.class)
    private String experimentName;

    @GobiiEntityMap(paramName = "project.projectId",  entity = Experiment.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    @Positive(groups = {ProjectDTO.Create.class})
    @Null(groups = {ProjectDTO.Update.class})
    private Integer projectId;

    @GobiiEntityMap(paramName = "project.projectName", entity = Experiment.class, deep = true)
    @NotBlank(groups = {ProjectDTO.Create.class})
    private String projectName;

    @GobiiEntityMap(paramName = "vendorProtocol.protocol.protocolId",
        entity = Experiment.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    @Positive(groups = {ProjectDTO.Create.class})
    private Integer protocolId;
    
    @GobiiEntityMap(paramName = "vendorProtocol.protocol.name",
        entity = Experiment.class, deep = true)
    private String protocolName;

    @GobiiEntityMap(paramName = "vendorProtocol.vendor.organizationId",
        entity = Experiment.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    @Positive(groups = {ProjectDTO.Create.class})
    private Integer vendorId;

    @GobiiEntityMap(paramName = "vendorProtocol.vendor.name",
        entity = Experiment.class, deep = true)
    private String vendorName;

    @GobiiEntityMap(paramName = "vendorProtocol.protocol.platform.platformId",
        entity = Experiment.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer platformId;

    @GobiiEntityMap(paramName = "vendorProtocol.protocol.platform.platformName",
        entity = Experiment.class, deep = true)
    private String platformName;

    
    @GobiiEntityMap(paramName="experimentStats.datasetCount", entity=Experiment.class, deep=true)
    private Integer datasetCount = 0;

    @GobiiEntityMap(paramName="experimentStats.markerCount", entity=Experiment.class, deep=true)
    private Integer markerCount = 0;

    @GobiiEntityMap(paramName="experimentStats.dnarunCount", entity=Experiment.class, deep=true)
    private Integer dnaRunCount = 0;

}