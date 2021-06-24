package org.gobii.masticator.aspects;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CoordinateAspect extends ElementAspect {

    private Integer row;
    private Integer col;

}
