package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.util.List;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiFilterType;

/**
 * Created by Phil on 10/16/2016.
 */
public class DtoMapNameIdParams {

    private GobiiEntityNameType gobiiEntityNameType;
    private GobiiFilterType gobiiFilterType;
    private Object filterValue;
    private List<NameIdDTO> nameIdDTOList;
    private Integer callLimit = null;

    public DtoMapNameIdParams(GobiiEntityNameType gobiiEntityNameType,
                              GobiiFilterType gobiiFilterType,
                              Object filterValue,
                              List<NameIdDTO> nameIdDTOList,
                              Integer callLimit) {
        this.gobiiEntityNameType = gobiiEntityNameType;
        this.gobiiFilterType = gobiiFilterType;
        this.filterValue = filterValue;
        this.nameIdDTOList = nameIdDTOList;
        this.callLimit = callLimit;
    }

    public DtoMapNameIdParams(GobiiEntityNameType gobiiEntityNameType, GobiiFilterType gobiiFilterType, Object filterValue, Integer callLimit) {
        this(gobiiEntityNameType, gobiiFilterType, filterValue, null, callLimit);
    }

    public GobiiEntityNameType getEntityType() {
        return gobiiEntityNameType;
    }

    public void setEntity(GobiiEntityNameType entity) {
        this.gobiiEntityNameType = entity;
    }

    public GobiiFilterType getGobiiFilterType() {
        return gobiiFilterType;
    }

    public void setGobiiFilterType(GobiiFilterType gobiiFilterType) {
        this.gobiiFilterType = gobiiFilterType;
    }

    public Object getFilterValue() {
        return filterValue;
    }

    public Integer getFilterValueAsInteger() {
        return ((Integer) filterValue);
    }

    public String getFilterValueAsString() {
        return (filterValue.toString());
    }

    public void setFilterValue(Object filterValue) {
        this.filterValue = filterValue;
    }

    public Integer getCallLimit() {
        return callLimit;
    }

    public void setCallLimit(Integer callLimit) {
        this.callLimit = callLimit;
    }

    public List<NameIdDTO> getNameIdDTOList() {
        return nameIdDTOList;
    }

    public void setNameIdDTOList(List<NameIdDTO> nameIdDTOList) {
        this.nameIdDTOList = nameIdDTOList;
    }
}
