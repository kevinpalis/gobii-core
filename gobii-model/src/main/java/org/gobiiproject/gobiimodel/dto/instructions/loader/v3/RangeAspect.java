package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"aspectType", "rangeStart"})
@Data
public class RangeAspect implements Aspect {

    public final String aspectType = "RANGE";

    private int rangeStart;

    public RangeAspect(int rangeStart) {
        this.rangeStart = rangeStart;
    }

}
