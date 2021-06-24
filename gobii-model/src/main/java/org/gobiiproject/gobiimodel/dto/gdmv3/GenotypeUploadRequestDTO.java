package org.gobiiproject.gobiimodel.dto.gdmv3;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.gobiiproject.gobiimodel.types.GobiiFileType;

import lombok.Data;

@Data
public class GenotypeUploadRequestDTO {


    @NotNull(message = "Required datasetId")
    private String datasetId;

    @NotNull(message = "Required mapId")
    private String mapId;

    @NotNull(message = "Required fileType")
    private GobiiFileType fileType;

    @NotEmpty(message = "Required input file paths")
    private List<FileDTO> inputFiles;

    private boolean loadMarkers;

    private boolean loadDnaRunNamesAsSamplesAndGermplasms;

    private Integer genotypeTemplateId;

}
