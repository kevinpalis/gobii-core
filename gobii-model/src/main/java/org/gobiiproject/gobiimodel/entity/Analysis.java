package org.gobiiproject.gobiimodel.entity;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Model for Analysis Entity.
 * Represents the database table dataset.
 *
 */
@Entity
@Table(name = "analysis")
@NamedEntityGraph(name = "graph.analysis",
    attributeNodes = {
        @NamedAttributeNode(value = "type"),
        @NamedAttributeNode(value = "reference")
    }
)
@Data
@EqualsAndHashCode(callSuper=false)
public class Analysis extends BaseEntity {

    @Id
    @Column(name="analysis_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer analysisId;

    @Column(name = "name")
    private String analysisName;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", referencedColumnName = "cv_id")
    private Cv type = new Cv();

    @Column(name = "program")
    private String program;

    @Column(name = "programversion")
    private String programVersion;

    @Column(name = "algorithm")
    private String algorithm;

    @Column(name = "sourcename")
    private String sourceName;

    @Column(name = "sourceversion")
    private String sourceVersion;

    @Column(name = "sourceuri")
    private String sourceUri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", referencedColumnName = "reference_id")
    private Reference reference;

    //@Column(name="parameters", columnDefinition = "jsonb")
    //@Convert(converter = JsonbConverter.class)
    //private JsonNode parameters;
    @Column(name="parameters", columnDefinition = "jsonb")
    @Type(type = "CvPropertiesType")
    private Map<String, String> parameters;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "cv_id")
    private Cv status = new Cv();

    @Column(name = "timeexecuted")
    private Date timeExecuted;
}
