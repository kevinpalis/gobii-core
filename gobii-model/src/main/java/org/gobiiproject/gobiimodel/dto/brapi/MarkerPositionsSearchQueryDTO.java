package org.gobiiproject.gobiimodel.dto.brapi;

import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;

public class MarkerPositionsSearchQueryDTO {

    @Size(max = 1000, message = "Only 1000 linkageGroupNames allowed per query")
    private Set<String> linkageGroupNames;

    @Size(max = 1000, message = "Only 1000 mapDbIds allowed per query")
    private Set<Integer> mapDbIds;

    @Size(max = 1000, message = "Only 1000 mapNames allowed per query")
    private Set<String> mapNames;

    private BigDecimal minPosition;

    private BigDecimal maxPosition;

    @Size(max = 1000, message = "Only 1000 variantDbIds allowed per query")
    private Set<Integer> variantDbIds;

    @Size(max = 1000, message = "Only 1000 variantSetDbIds allowed per query")
    private Set<String> variantSetDbIds;

    public Set<String> getLinkageGroupNames() {
        return linkageGroupNames;
    }

    public void setLinkageGroupNames(Set<String> linkageGroupNames) {
        this.linkageGroupNames = linkageGroupNames;
    }

    public Set<Integer> getMapDbIds() {
        return mapDbIds;
    }

    public void setMapDbIds(Set<Integer> mapDbIds) {
        this.mapDbIds = mapDbIds;
    }

    public Set<String> getMapNames() {
        return mapNames;
    }

    public void setMapNames(Set<String> mapNames) {
        this.mapNames = mapNames;
    }

    public BigDecimal getMinPosition() {
        return minPosition;
    }

    public void setMinPosition(BigDecimal minPosition) {
        this.minPosition = minPosition;
    }

    public BigDecimal getMaxPosition() {
        return maxPosition;
    }

    public void setMaxPosition(BigDecimal maxPosition) {
        this.maxPosition = maxPosition;
    }

    public Set<Integer> getVariantDbIds() {
        return variantDbIds;
    }

    public void setVariantDbIds(Set<Integer> variantDbIds) {
        this.variantDbIds = variantDbIds;
    }

    public Set<String> getVariantSetDbIds() {
        return variantSetDbIds;
    }

    public void setVariantSetDbIds(Set<String> variantSetDbIds) {
        this.variantSetDbIds = variantSetDbIds;
    }
}
