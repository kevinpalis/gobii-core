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
@Table(name = "project_stats")
@Data
@EqualsAndHashCode(callSuper=false)
public class ProjectStats {
    @Id
    @Column(name="project_id")
    private Integer projectId;

    @Column(name="experiment_count")
    private Integer experimentCount;

    @Column(name="dataset_count")
    private Integer datasetCount;

    @Column(name="marker_count")
    private Integer markerCount;

    @Column(name="dnarun_count")
    private Integer dnarunCount;

    @OneToOne
    @JoinColumn(name = "project_id")
    @MapsId
    private Project project;
}
