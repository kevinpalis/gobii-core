package org.gobii.masticator.aspects;

import lombok.Data;

@Data
public class MatrixAspect extends CoordinateAspect {

	public MatrixAspect(Integer row, Integer col) {
		super(row, col);
	}


}
