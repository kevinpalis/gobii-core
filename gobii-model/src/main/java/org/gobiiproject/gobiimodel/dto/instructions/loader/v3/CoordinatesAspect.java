package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
public class CoordinatesAspect implements Aspect {

    private ColumnCoordinates columnCoordinates;

    public CoordinatesAspect(ColumnCoordinates columnCoordinates) {
        this.columnCoordinates = columnCoordinates;
    }

    public CoordinatesAspect(int row, int column) {
        this.columnCoordinates = new ColumnCoordinates(row, column);
    }

    public ColumnCoordinates getColumnCoordinates() {
        return columnCoordinates;
    }

    public void setColumnCoordinates(ColumnCoordinates columnCoordinates) {
        this.columnCoordinates = columnCoordinates;
    }
}
