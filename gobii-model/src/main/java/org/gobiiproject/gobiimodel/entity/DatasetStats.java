package org.gobiiproject.gobiimodel.entity;

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
@Table(name = "dataset_stats")
@Data
@EqualsAndHashCode(callSuper=false)
public class DatasetStats {
    @Id
    @Column(name="dataset_id")
    private Integer datasetId;


    @Column(name="marker_count")
    private Integer markerCount;

    @Column(name="dnarun_count")
    private Integer dnarunCount;

    @OneToOne
    @JoinColumn(name = "dataset_id")
    @MapsId
    private Dataset dataset;
}
