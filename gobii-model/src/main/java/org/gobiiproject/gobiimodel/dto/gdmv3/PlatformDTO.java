package org.gobiiproject.gobiimodel.dto.gdmv3;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.Platform;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"id", "allowedProcessTypes", "entityNameType", "status"})
@JsonInclude(JsonInclude.Include.ALWAYS)
public class PlatformDTO extends DTOBaseAuditable {

    public static interface Create {}
    public static interface Update {}

    public PlatformDTO() {
        super(GobiiEntityNameType.PLATFORM);
    }
    @Override
    public Integer getId() {
        return platformId;
    }

    @Override
    public void setId(Integer id) {

    }

    @GobiiEntityMap(paramName = "platformId", entity = Platform.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Null(groups = {PlatformDTO.Create.class, PlatformDTO.Update.class})
    private Integer platformId;

    @GobiiEntityMap(paramName = "platformName", entity = Platform.class)
    @NotBlank(groups = {PlatformDTO.Create.class})
    private String platformName;


    @GobiiEntityMap(paramName = "type.cvId", entity = Platform.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    @Positive(groups = {PlatformDTO.Create.class, PlatformDTO.Update.class})
    private Integer platformTypeId;

    @GobiiEntityMap(paramName = "type.term", entity = Platform.class, deep = true)
    @Null(groups = {PlatformDTO.Create.class})
    private String platformTypeName;

    @Null(groups = {PlatformDTO.Create.class, PlatformDTO.Update.class})
    @GobiiEntityMap(paramName = "platformStats.protocolCount", entity = Platform.class, deep = true)
    private Integer protocolCount;

    @Null(groups = {PlatformDTO.Create.class, PlatformDTO.Update.class})
    @GobiiEntityMap(paramName = "platformStats.vendorProtocolCount", entity = Platform.class, deep = true)
    private Integer vendorProtocolCount;

    @Null(groups = {PlatformDTO.Create.class, PlatformDTO.Update.class})
    @GobiiEntityMap(paramName = "platformStats.experimentCount", entity = Platform.class, deep = true)
    private Integer experimentCount;

    @Null(groups = {PlatformDTO.Create.class, PlatformDTO.Update.class})
    @GobiiEntityMap(paramName = "platformStats.markerCount", entity = Platform.class, deep = true)
    private Integer markerCount;

    @Null(groups = {PlatformDTO.Create.class, PlatformDTO.Update.class})
    @GobiiEntityMap(paramName = "platformStats.dnarunCount", entity = Platform.class, deep = true)
    private Integer dnaRunCount;
    
}