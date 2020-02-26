// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiimodel.dto.entity.children;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;

public class TableColDisplay {

	private Integer displayId; // column name
	private String columnName; // column name
	private String displayName; // display name


	private Integer rank;

	public Integer getDisplayId() {
		return displayId;
	}

	@GobiiEntityParam(paramName = "display_id")
	public void setDisplayId(Integer displayId) {
		this.displayId = displayId;
	}

	public String getColumnName() {
		return columnName;
	}

	@GobiiEntityParam(paramName = "column_name")
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDisplayName() {
		return displayName;
	}

	@GobiiEntityParam(paramName = "display_name")
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Integer getRank() {
		return rank;
	}

	@GobiiEntityParam(paramName = "rank")
	public void setRank(Integer rank) {
		this.rank = rank;
	}

	
}
