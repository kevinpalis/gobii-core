package org.gobiiproject.gobiimodel.entity;


import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Model for MarkerLinkageGroup Entity.
 * Represents database table marker_linkage_group.
 *
 */
@Entity
@Table(name = "marker_linkage_group")
public class MarkerLinkageGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "marker_linkage_group_id")
    private Integer makrerLinkageGroupId;

    @ManyToOne
    @JoinColumn(name = "marker_id")
    private Marker marker = new Marker();

    @Column(name = "start")
    private BigDecimal start;

    @Column(name = "stop")
    private BigDecimal stop;

    @ManyToOne
    @JoinColumn(name = "linkage_group_id")
    private LinkageGroup linkageGroup;

    public Integer getMakrerLinkageGroupId() {
        return makrerLinkageGroupId;
    }

    public void setMakrerLinkageGroupId(Integer makrerLinkageGroupId) {
        this.makrerLinkageGroupId = makrerLinkageGroupId;
    }


    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public BigDecimal getStart() {
        return start;
    }

    public void setStart(BigDecimal start) {
        this.start = start;
    }

    public BigDecimal getStop() {
        return stop;
    }

    public void setStop(BigDecimal stop) {
        this.stop = stop;
    }

    public LinkageGroup getLinkageGroup() {
        return linkageGroup;
    }

    public void setLinkageGroup(LinkageGroup linkageGroup) {
        this.linkageGroup = linkageGroup;
    }
}

