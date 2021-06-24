package org.gobii.masticator.aspects;

import lombok.Data;

@Data
public class ArrayColumnAspect extends CoordinateAspect {
	private String separator;
	public ArrayColumnAspect(Integer row, Integer col, String separator) {
		super(row, col);
		this.separator = separator;
	}
}
