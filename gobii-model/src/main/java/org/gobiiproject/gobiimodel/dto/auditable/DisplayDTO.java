package org.gobiiproject.gobiimodel.dto.auditable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;
import org.gobiiproject.gobiimodel.dto.entity.children.TableColDisplay;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

/**
 * Created by Phil on 4/6/2016.
 * Modified by Yanii on 1/27/2017
 */
public class DisplayDTO extends DTOBaseAuditable {

	public DisplayDTO() {
		super(GobiiEntityNameType.DISPLAY);
	}

	boolean includeDetailsList = false;

	private Integer displayId;

	private String tableName;
	private String columnName;
	private String displayName;
	private Integer displayRank;

	public boolean isIncludeDetailsList() {
		return includeDetailsList;
	}

	public void setIncludeDetailsList(boolean includeDetailsList) {
		this.includeDetailsList = includeDetailsList;
	}

	@Override
	public Integer getId() {
		return this.displayId;
	}

	@Override
	public void setId(Integer id) {
		this.displayId = id;
	}

	@GobiiEntityParam(paramName = "displayId")
	public Integer getDisplayId() {
		return displayId;
	}

	@GobiiEntityColumn(columnName = "display_id")
	public void setDisplayId(Integer displayId) {
		this.displayId = displayId;
	}

	@GobiiEntityParam(paramName = "tableName")
	public String getTableName() {
		return tableName;
	}

	@GobiiEntityColumn(columnName = "table_name")
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@GobiiEntityParam(paramName = "columnName")
	public String getColumnName() {
		return columnName;
	}

	@GobiiEntityColumn(columnName = "column_name")
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	@GobiiEntityParam(paramName = "displayName")
	public String getDisplayName() {
		return displayName;
	}

	@GobiiEntityColumn(columnName = "display_name")
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@GobiiEntityParam(paramName = "displayRank")
	public Integer getDisplayRank() {
		return displayRank;
	}

	@GobiiEntityColumn(columnName = "rank")
	public void setDisplayRank(Integer displayRank) {
		this.displayRank = displayRank;
	}


	Map<String,List<TableColDisplay>> tableNamesWithColDisplay = new HashMap<>();


	public Map<String, List<TableColDisplay>> getTableNamesWithColDisplay() {
		return tableNamesWithColDisplay;
	}

	public void setTableNamesWithColDisplay(Map<String, List<TableColDisplay>> tableNamesWithColDisplay) {
		this.tableNamesWithColDisplay = tableNamesWithColDisplay;
	}



}
