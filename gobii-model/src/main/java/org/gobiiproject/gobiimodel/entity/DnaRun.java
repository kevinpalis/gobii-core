package org.gobiiproject.gobiimodel.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.JsonNode;

import org.hibernate.annotations.Type;

/**
 * Model for Dnarun(dnarun) Entity in database.
 *
 * props - is a jsonb column. It is converted to jackson.fasterxml JsonNode using a
 * user defined hibernate converter.
 */
@Entity
@Table(name = "dnarun")
public class DnaRun {

    @Id
    @Column(name="dnarun_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dnaRunId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dnasample_id")
    private DnaSample dnaSample;

    @Column(name="name")
    private String dnaRunName;

    @Column(name="dataset_dnarun_idx", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode datasetDnaRunIdx;

    @Column(name="props", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode properties;


    public Integer getDnaRunId() {
        return dnaRunId;
    }

    public void setDnaRunId(Integer dnaRunId) {
        this.dnaRunId = dnaRunId;
    }

    public String getDnaRunName() {
        return dnaRunName;
    }

    public void setDnaRunName(String dnaRunName) {
        this.dnaRunName = dnaRunName;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public DnaSample getDnaSample() {
        return dnaSample;
    }

    public void setDnaSample(DnaSample dnaSample) {
        this.dnaSample = dnaSample;
    }


    public JsonNode getDatasetDnaRunIdx() {
        return datasetDnaRunIdx;
    }

    public void setDatasetDnaRunIdx(JsonNode datasetDnaRunIdx) {
        this.datasetDnaRunIdx = datasetDnaRunIdx;
    }

    public JsonNode getProperties() {
        return properties;
    }

    public void setProperties(JsonNode properties) {
        this.properties = properties;
    }
}
