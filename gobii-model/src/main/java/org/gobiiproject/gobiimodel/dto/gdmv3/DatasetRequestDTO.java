package org.gobiiproject.gobiimodel.dto.gdmv3;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.Dataset;

import lombok.Data;

@Data
public class DatasetRequestDTO {
    //validation groups
    public static interface Create {}
    public static interface Update {}

    @GobiiEntityMap(paramName = "datasetName", entity = Dataset.class)
    @NotBlank(groups = {DatasetRequestDTO.Create.class})
    private String datasetName;

    @Digits(integer = 10, fraction = 0, groups = DatasetRequestDTO.Create.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer experimentId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Integer datasetTypeId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Digits(integer = 10, fraction = 0, groups = DatasetRequestDTO.Create.class)
    private Integer callingAnalysisId;

    private Integer[] analysisIds;

}