package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"aspectType", "columnCoordinates"})
public class MatrixAspect  extends CoordinatesAspect {

    public final String aspectType = "MATRIX";

    public MatrixAspect(ColumnCoordinates columnCoordinates) {
        super(columnCoordinates);
    }

    public MatrixAspect(int row, int column) {
        super(row, column);
    }

}
