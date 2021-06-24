package org.gobiiproject.gobiimodel.dto.brapi;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.types.BrapiVariantTypes;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;


@JsonIgnoreProperties(ignoreUnknown = true, value={
        "id", "allowedProcessTypes", "entityNameType", "createdBy",
        "modifiedBy", "modifiedDate", "createdDate"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantDTO extends DTOBaseAuditable {

    @GobiiEntityMap(paramName="markerId", entity = Marker.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer variantDbId;

    private List<String> variantNames = new ArrayList<>();

    private List<String> variantSetDbIds = new ArrayList<>();

    private String variantType = BrapiVariantTypes.MARKER.toString();

    @GobiiEntityMap(paramName="alts", entity = Marker.class)
    private String[] alternateBases;

    private List<Integer> ciend;

    private List<Integer> cipos;

    private List<String> filtersFailed;

    private List<String> filtersPassed;

    @GobiiEntityMap(paramName="reference.referenceName", entity = Marker.class,
            deep=true)
    private String referenceName;

    @GobiiEntityMap(paramName="ref", entity = Marker.class)
    private String referenceBases;

    @GobiiEntityMap(paramName="platform.platformName", entity = Marker.class,
            deep=true)
    private String platformName;

    private String svlen;

    public List<Integer> getCiend() {
        return ciend;
    }

    public void setCiend(List<Integer> ciend) {
        this.ciend = ciend;
    }

    public List<Integer> getCipos() {
        return cipos;
    }

    public void setCipos(List<Integer> cipos) {
        this.cipos = cipos;
    }

    public List<String> getFiltersFailed() {
        return filtersFailed;
    }

    public void setFiltersFailed(List<String> filtersFailed) {
        this.filtersFailed = filtersFailed;
    }

    public List<String> getFiltersPassed() {
        return filtersPassed;
    }

    public void setFiltersPassed(List<String> filtersPassed) {
        this.filtersPassed = filtersPassed;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getReferenceBases() {
        return referenceBases;
    }

    public void setReferenceBases(String referenceBases) {
        this.referenceBases = referenceBases;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getSvlen() {
        return svlen;
    }

    public void setSvlen(String svlen) {
        this.svlen = svlen;
    }

    public int getVariantDbId() {
        return variantDbId;
    }

    public void setVariantDbId(int variantDbId) {
        this.variantDbId = variantDbId;
    }

    public List<String> getVariantNames() {
        return variantNames;
    }

    public void setVariantNames(List<String> variantNames) {
        this.variantNames = variantNames;
    }


    public String getVariantType() {
        return variantType;
    }

    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    public void setVariantDbId(Integer variantDbId) {
        this.variantDbId = variantDbId;
    }

    public List<String> getVariantSetDbIds() {
        return variantSetDbIds;
    }

    public void setVariantSetDbIds(List<String> variantSetDbIds) {
        this.variantSetDbIds = variantSetDbIds;
    }

    public String[] getAlternateBases() {
        return alternateBases;
    }

    public void setAlternateBases(String[] alternateBases) {
        this.alternateBases = alternateBases;
    }


    @Override
    public Integer getId() {
        return this.variantDbId;
    }

    @Override
    public void setId(Integer id) {
        this.variantDbId = id;
    }


    public VariantDTO() { super(GobiiEntityNameType.MARKER);}

}
