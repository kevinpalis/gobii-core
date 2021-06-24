package org.gobiiproject.gobiimodel.dto.gdmv3;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;

@Data
public class MarkerUploadRequestDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Integer mapId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Integer platformId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Integer markerTemplateId;

    private List<FileDTO> inputFiles;
    
}
