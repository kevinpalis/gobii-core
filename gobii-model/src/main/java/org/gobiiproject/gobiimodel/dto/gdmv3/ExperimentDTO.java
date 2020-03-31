/**
 * ExperimentDTO.java
 * 
 * Experiment DTO Class
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-28
 */

package org.gobiiproject.gobiimodel.dto.gdmv3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = false, value={
    "id", "allowedProcessTypes", "entityNameType", "status"
})
public class ExperimentDTO extends DTOBaseAuditable {
    
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
    private Integer projectId;

    @GobiiEntityMap(paramName = "project.projectName", entity = Experiment.class, deep = true)
    private String projectName;

    @GobiiEntityMap(paramName = "vendorProtocol.vendorProtocolId", entity = Experiment.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer vendorProtocolId;
    
    @GobiiEntityMap(paramName = "vendorProtocol.name", entity = Experiment.class, deep = true)
    private String vendorProtocolName;
    
    @GobiiEntityMap(paramName = "vendorProtocol.protocol.platform.platformId", entity = Experiment.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer platformId;

    @GobiiEntityMap(paramName = "vendorProtocol.protocol.platform.platformName", entity = Experiment.class, deep = true)
    private String platformName;

    
}