package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"aspectType", "columnCoordinates", "separator"})
@Data
public class ArrayColumnAspect  extends ColumnAspect {

    public final String aspectType = "ARRAYCOLUMN";

    private final String separator;

    public ArrayColumnAspect(ColumnCoordinates columnCoordinates, String separator) {
        super(columnCoordinates);
        this.separator = separator;
    }

    public ArrayColumnAspect(int row, int column, String separator) {
        super(row, column);
        this.separator = separator;
    }

}
