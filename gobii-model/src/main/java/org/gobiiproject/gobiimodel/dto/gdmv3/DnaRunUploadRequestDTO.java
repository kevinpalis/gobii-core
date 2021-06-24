package org.gobiiproject.gobiimodel.dto.gdmv3;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;

@Data
public class DnaRunUploadRequestDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Integer projectId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Integer experimentId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Integer dnaRunTemplateId;

    private List<FileDTO> inputFiles;

}
