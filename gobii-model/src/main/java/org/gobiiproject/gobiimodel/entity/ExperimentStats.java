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
@Table(name = "experiment_stats")
@Data
@EqualsAndHashCode(callSuper=false)
public class ExperimentStats {

    @Id
    @Column(name="experiment_id")
    private Integer experimentId;

    @Column(name="dataset_count")
    private Integer datasetCount;

    @Column(name="marker_count")
    private Integer markerCount;

    @Column(name="dnarun_count")
    private Integer dnarunCount;

    @OneToOne
    @JoinColumn(name = "experiment_id")
    @MapsId
    private Experiment experiment;
}