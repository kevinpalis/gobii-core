package org.gobiiproject.gobiimodel.dto.gdmv3;


import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Cv;

import lombok.Data;

@Data
public class CvDTO {
    public final static String PROPERTY_TYPE_SYSTEM = "SYSTEM_DEFINED";
    public final static String PROPERTY_TYPE_CUSTOM = "USER_DEFINED";

    public static interface Create{}
    public static interface Update{}

    @GobiiEntityMap(paramName = "cvId", entity = Cv.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Null(groups = {CvDTO.Create.class, CvDTO.Update.class})
    private Integer cvId;

    @GobiiEntityMap(paramName = "term", entity = Cv.class)
    @NotBlank(groups = {CvDTO.Create.class})
    private String cvName;

    @GobiiEntityMap(paramName = "definition", entity = Cv.class)
    @NotBlank(groups = {CvDTO.Create.class})
    private String cvDescription;

    @GobiiEntityMap(paramName = "cvGroup.cvGroupId", entity = Cv.class, deep = true)
    @NotNull(groups = {CvDTO.Create.class})
    @Positive(groups = {CvDTO.Create.class})
    @JsonSerialize(using = ToStringSerializer.class )
    private Integer cvGroupId;

    @GobiiEntityMap(paramName = "cvGroup.cvGroupName", entity = Cv.class, deep = true)
    @Null(groups = {CvDTO.Create.class, CvDTO.Update.class})
    private String cvGroupName;

    @GobiiEntityMap(paramName = "status", entity = Cv.class)
    @JsonIgnore
    private Integer status;

    @GobiiEntityMap(paramName = "cvGroup.cvGroupType", entity = Cv.class, deep = true)
    @JsonIgnore
    private Integer cvGroupTypeInt;

    @GobiiEntityMap(paramName = "properties", entity = Cv.class)
    @JsonIgnore
    private java.util.Map<String, String> propertiesMap;

    //map this outside of the mapper
    private String cvStatus;

    //determine from cvGroupTypeInt
    @JsonProperty("cvGroupType")
    public String getCvGroupType() {
        if (cvGroupTypeInt != null && cvGroupTypeInt == 1) return PROPERTY_TYPE_SYSTEM;
        return PROPERTY_TYPE_CUSTOM;
    }

}