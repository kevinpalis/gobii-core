package org.gobiiproject.gobiimodel.entity;


import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Model for LinkageGroup Entity.
 * Represents database table linkage_group.
 *
 */
@Entity
@Table(name = "linkage_group")
public class LinkageGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "linkage_group_id")
    private Integer linkageGroupId;

    @Column(name = "name")
    private String linkageGroupName;

    @Column(name = "start")
    private BigDecimal linkageGrpupStart;

    @Column(name = "stop")
    private BigDecimal linkageGroupStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id", referencedColumnName = "mapset_id")
    private Mapset mapset;

    public Integer getLinkageGroupId() {
        return linkageGroupId;
    }

    public void setLinkageGroupId(Integer linkageGroupId) {
        this.linkageGroupId = linkageGroupId;
    }

    public String getLinkageGroupName() {
        return linkageGroupName;
    }

    public void setLinkageGroupName(String linkageGroupName) {
        this.linkageGroupName = linkageGroupName;
    }

    public BigDecimal getLinkageGrpupStart() {
        return linkageGrpupStart;
    }

    public void setLinkageGrpupStart(BigDecimal linkageGrpupStart) {
        this.linkageGrpupStart = linkageGrpupStart;
    }

    public BigDecimal getLinkageGroupStop() {
        return linkageGroupStop;
    }

    public void setLinkageGroupStop(BigDecimal linkageGroupStop) {
        this.linkageGroupStop = linkageGroupStop;
    }

    public Mapset getMapset() {
        return mapset;
    }

    public void setMapset(Mapset mapset) {
        this.mapset = mapset;
    }
}

