package org.gobiiproject.gobiimodel.dto.gdmv3;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDTO {

    private String type;

    private String fileName;

    private String fileUrlPath;

    private String serverFilePath;

    private String fileManifestPath;

    private long totalBytes;

}
