/**
 * MapsetDTO.java
 * 
 * DTO class for GDM v3 API.
 * 
 * @author Rodolfo N. Duldulao, Jr.
 */
package org.gobiiproject.gobiimodel.dto.gdmv3;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.Mapset;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"id", "allowedProcessTypes", "entityNameType"})
public class MapsetDTO extends DTOBaseAuditable {

    public static interface Create{};
    public static interface Update{}

    public MapsetDTO(){
        super(GobiiEntityNameType.MAPSET);
    }

    @Override
    public Integer getId() {
        return mapsetId;
    }

    @Override
    public void setId(Integer id) {
    }

    @GobiiEntityMap(paramName = "mapsetId", entity = Mapset.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Null(groups = {MapsetDTO.Create.class, MapsetDTO.Update.class})
    private Integer mapsetId;

    @GobiiEntityMap(paramName = "mapsetName", entity = Mapset.class)
    @NotBlank(groups = {MapsetDTO.Create.class})
    private String mapsetName;

    @GobiiEntityMap(paramName = "mapsetDescription", entity = Mapset.class)
    private String mapsetDescription;

    @GobiiEntityMap(paramName = "type.cvId", entity = Mapset.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(groups = {MapsetDTO.Create.class})
    @Digits(integer = 10, fraction = 0, groups = {MapsetDTO.Create.class})
    private Integer mapsetTypeId;

    @GobiiEntityMap(paramName = "type.term", entity = Mapset.class, deep = true)
    @Null(groups = {MapsetDTO.Create.class, MapsetDTO.Update.class})
    private String mapsetTypeName;

    @GobiiEntityMap(paramName = "reference.referenceId", entity = Mapset.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer referenceId;

    @GobiiEntityMap(paramName = "reference.referenceName", entity = Mapset.class, deep = true)
    @Null(groups = {MapsetDTO.Create.class, MapsetDTO.Update.class})
    private String referenceName; 
  
}