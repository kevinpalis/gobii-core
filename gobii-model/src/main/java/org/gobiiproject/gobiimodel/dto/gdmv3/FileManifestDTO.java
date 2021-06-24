package org.gobiiproject.gobiimodel.dto.gdmv3;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileManifestDTO {

    private String fileUploadId;

    private String fileUploadUrl;

    private String serverDirectoryPath;

    private String type;

    private Map<String, String> mimeTypesByFileNames;

    private String cropType;

    private String createdBy;

    private Date createdDate;

    @JsonSerialize(using = ToStringSerializer.class)
    private String modifiedBy;

    @JsonFormat
      (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date modifiedDate;
}
