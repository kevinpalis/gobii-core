package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"aspectType", "columnCoordinates"})
public class ColumnAspect  extends CoordinatesAspect {

    public final String aspectType = "COLUMN";

    public ColumnAspect(ColumnCoordinates columnCoordinates) {
        super(columnCoordinates);
    }

    public ColumnAspect(int row, int column) {
        super(row, column);
    }

}
