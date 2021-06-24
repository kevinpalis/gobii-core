package org.gobiiproject.gobiimodel.entity.stats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "experiment_dataset_count")
@Data
@EqualsAndHashCode(callSuper = false)
public class ExperimentDatasetCount {
    
    @Id
    @Column(name = "experiment_id")
    private Integer experiment_id;
    
}