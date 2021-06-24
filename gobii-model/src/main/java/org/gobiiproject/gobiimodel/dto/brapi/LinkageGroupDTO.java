package org.gobiiproject.gobiimodel.dto.brapi;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;

import java.math.BigDecimal;

/**
 * Brapi DTO for LinkageGroups
 */
public class LinkageGroupDTO {


    private String linkageGroupName;

    private BigDecimal maxPosition;

    private Long markerCount;

    public String getLinkageGroupName() {
        return linkageGroupName;
    }

    @GobiiEntityColumn(columnName = "linkage_group_name")
    public void setLinkageGroupName(String linkageGroupName) {
        this.linkageGroupName = linkageGroupName;
    }

    public BigDecimal getMaxPosition() {
        return maxPosition;
    }

    @GobiiEntityColumn(columnName = "max_position")
    public void setMaxPosition(BigDecimal maxPosition) {
        this.maxPosition = maxPosition;
    }

    public Long getMarkerCount() {
        return markerCount;
    }

    @GobiiEntityColumn(columnName = "marker_count")
    public void setMarkerCount(Long markerCount) {
        this.markerCount = markerCount;
    }

}
