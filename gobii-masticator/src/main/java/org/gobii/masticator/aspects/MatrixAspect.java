package org.gobii.masticator.aspects;

public class MatrixAspect extends CoordinateAspect {
	private String datasetType;
	public MatrixAspect(Integer row, Integer col, String datasetType) {
		super(row, col);
		this.datasetType=datasetType;
	}

	public String getDatasetType() {
		return datasetType;
	}
}
