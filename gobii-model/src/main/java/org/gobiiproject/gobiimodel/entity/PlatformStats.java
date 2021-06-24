package org.gobiiproject.gobiimodel.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "platform_stats")
@Data
@EqualsAndHashCode(callSuper=false)
public class PlatformStats {
    @Id
    @Column(name="platform_id")
    private Integer platformId;

    @Column(name="protocol_count")
    private Integer protocolCount;

    @Column(name="vendor_protocol_count")
    private Integer vendorProtocolCount;

    @Column(name="experiment_count")
    private Integer experimentCount;

    @Column(name="marker_count")
    private Integer markerCount;

    @Column(name="dnarun_count")
    private Integer dnarunCount;

    @OneToOne
    @JoinColumn(name = "platform_id")
    @MapsId
    private Platform platform;

}
