package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

public class ColumnCoordinates {

    private int row;
    private int column;

    public ColumnCoordinates(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
