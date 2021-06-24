package org.gobiiproject.gobiimodel.dto.gdmv3;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Cv;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties({"propertyGroupType"})
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class DatasetTypeDTO {


    public static interface Create {}

    @GobiiEntityMap(paramName = "cvId", entity = Cv.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer datasetTypeId;

    @GobiiEntityMap(paramName = "term", entity = Cv.class)
    @NotBlank(groups = {DatasetTypeDTO.Create.class})
    private String datasetTypeName;

    @GobiiEntityMap(paramName = "definition", entity = Cv.class)
    private String datasetTypeDescription;

    @GobiiEntityMap(paramName="cvGroup.cvGroupType", entity = Cv.class, deep = true)
    private Integer propertyGroupType;

    @JsonProperty("userDefined")
    public boolean isUserDefined() {
        return this.propertyGroupType != null && this.propertyGroupType == 2;
    }

}