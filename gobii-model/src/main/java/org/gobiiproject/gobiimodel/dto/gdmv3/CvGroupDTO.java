package org.gobiiproject.gobiimodel.dto.gdmv3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.CvGroup;

import lombok.Data;

@Data
public class CvGroupDTO {

    @GobiiEntityMap(paramName = "cvGroupId", entity = CvGroup.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer cvGroupId;

    @GobiiEntityMap(paramName = "cvGroupName", entity = CvGroup.class)
    private String cvGroupName;

    @GobiiEntityMap(paramName = "cvGroupType", entity = CvGroup.class)
    @JsonIgnore
    private Integer cvGroupTypeInt;

    @GobiiEntityMap(paramName = "props", entity = CvGroup.class)
    private JsonNode properties;
    
    @JsonProperty("cvGroupType")
    public String getCvGroupType() {
        if (cvGroupTypeInt != null && cvGroupTypeInt == 1) return CvDTO.PROPERTY_TYPE_SYSTEM;
        return CvDTO.PROPERTY_TYPE_CUSTOM;
    }

}