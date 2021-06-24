package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@GobiiAspectTable(name = "matrix")
@Data
public class MatrixTable implements Table {

    private MatrixAspect matrix;

}
