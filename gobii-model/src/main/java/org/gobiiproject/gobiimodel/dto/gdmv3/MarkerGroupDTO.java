package org.gobiiproject.gobiimodel.dto.gdmv3;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.MarkerGroup;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"id", "allowedProcessTypes", "entityNameType"})
public class MarkerGroupDTO extends DTOBaseAuditable{

    public static interface Create{}

    public MarkerGroupDTO() {
        super(GobiiEntityNameType.MARKER_GROUP);
    }
    @Override
    public Integer getId() {
        return markerGroupId;
    }

    @Override
    public void setId(Integer id) {}

    @GobiiEntityMap(paramName = "markerGroupId", entity = MarkerGroup.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Null(groups = {MarkerGroupDTO.Create.class})
    private Integer markerGroupId;

    @GobiiEntityMap(paramName = "name", entity = MarkerGroup.class)
    @NotBlank(groups = {MarkerGroupDTO.Create.class})
    private String markerGroupName;

    @GobiiEntityMap(paramName = "germplasmGroup", entity = MarkerGroup.class)
    private String germplasmGroup;

    
}