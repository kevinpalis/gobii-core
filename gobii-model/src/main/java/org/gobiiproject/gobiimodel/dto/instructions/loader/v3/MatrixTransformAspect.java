package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"aspectType", "transformType", "matrix"})
@Data
public class MatrixTransformAspect  {

    public final String aspectType = "TRANSFORM";

    private String transformType;

    private MatrixAspect matrix;

    public MatrixTransformAspect(ColumnCoordinates columnCoordinates) {
        this.matrix = new MatrixAspect(columnCoordinates);
    }

    public MatrixTransformAspect(String transformType, int row, int column) {
        this.transformType = transformType;
        this.matrix = new MatrixAspect(row, column);
    }

}
