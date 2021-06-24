package org.gobiiproject.gobiimodel.dto.auditable;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;
import org.gobiiproject.gobiimodel.dto.children.EntityPropertyDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

/**
 * Created by Angel on 4/13/2016.
 * Modified by AVB on 9/30/2016.
 */
public class MapsetDTO extends DTOBaseAuditable {

	public MapsetDTO() {
		super(GobiiEntityNameType.MAPSET);
	}

	private Integer mapsetId;
	private String name;
	private String code;
	private String description;
	private Integer referenceId;
	private Integer mapType;
	private Integer statusId;
    private List<EntityPropertyDTO> properties = new ArrayList<>();

    @Override
	public Integer getId() {
    	return this.mapsetId;
	}

	@Override
	public void setId(Integer id) {
    	this.mapsetId = id;
	}

	@GobiiEntityParam(paramName = "mapsetId")
	public Integer getMapsetId() {
		return mapsetId;
	}

	@GobiiEntityColumn(columnName ="mapset_id")
	public void setMapsetId(Integer mapsetId) {
		this.mapsetId = mapsetId;
	}

	@GobiiEntityParam(paramName = "name")
	public String getName() {
		return name;
	}

	@GobiiEntityColumn(columnName ="name")
	public void setName(String name) {
		this.name = name;
	}

	@GobiiEntityParam(paramName = "code")
	public String getCode() {
		return code;
	}

	@GobiiEntityColumn(columnName ="code")
	public void setCode(String code) {
		this.code = code;
	}

	@GobiiEntityParam(paramName = "description")
	public String getDescription() {
		return description;
	}

	@GobiiEntityColumn(columnName ="description")
	public void setDescription(String description) {
		this.description = description;
	}

	@GobiiEntityParam(paramName = "referenceId")
	public Integer getReferenceId() {
		return referenceId;
	}

	@GobiiEntityColumn(columnName ="reference_id")
	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}

	@GobiiEntityParam(paramName = "mapType")
	public Integer getMapType() {
		return mapType;
	}

	@GobiiEntityColumn(columnName ="type_id")
	public void setMapType(Integer mapType) {
		this.mapType = mapType;
	}

	@GobiiEntityParam(paramName = "status")
	public Integer getStatusId() {
		return statusId;
	}

	@GobiiEntityColumn(columnName ="status")
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public List<EntityPropertyDTO> getProperties() {
		return properties;
	}

	public void setProperties(List<EntityPropertyDTO> properties) {
		this.properties = properties;
	}

}
