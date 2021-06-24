package org.gobiiproject.gobiimodel.entity;


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

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.array.StringArrayType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

/**
 * Model for Marker Entity.
 * Represents the database table project.
 *
 * props - is a jsonb column. It is converted to jackson.fasterxml JsonNode using a
 * user defined hibernate converter class.
 */
@Entity
@Table(name = "marker")
@NamedEntityGraph(name = "graph.marker",
    attributeNodes = 
        @NamedAttributeNode("platform")
)
@TypeDefs({
    @TypeDef(
        name = "string-array",
        typeClass = StringArrayType.class
    )
})
public class Marker {

    @Id
    @Column(name="marker_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer markerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id")
    private Platform platform = new Platform();

    @Column(name="variant_id")
    private Integer variantId;

    @Column(name="name")
    private String markerName;

    @Column(name="ref")
    private String ref;

    @Column(name="alts", columnDefinition = "text[]")
    @Type(type = "string-array")
    private String[] alts;

    @Column(name="sequence")
    private String sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id")
    private Reference reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strand_id", referencedColumnName = "cv_id")
    private Cv strand;

    @Column(name="primers", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode primers;

    @Column(name="probsets", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode probsets;

    @Column(name="dataset_marker_idx", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode datasetMarkerIdx;

    @Column(name="props", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode properties;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "cv_id")
    private Cv status;

    public Integer getMarkerId() {
        return markerId;
    }

    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Integer getVariantId() {
        return variantId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String[] getAlts() {
        return alts;
    }

    public void setAlts(String[] alts) {
        this.alts = alts;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Cv getStrand() {
        return strand;
    }

    public void setStrand(Cv strand) {
        this.strand = strand;
    }

    public JsonNode getPrimers() {
        return primers;
    }

    public void setPrimers(JsonNode primers) {
        this.primers = primers;
    }

    public JsonNode getProbsets() {
        return probsets;
    }

    public void setProbsets(JsonNode probsets) {
        this.probsets = probsets;
    }

    public JsonNode getDatasetMarkerIdx() {
        return datasetMarkerIdx;
    }

    public void setDatasetMarkerIdx(JsonNode datasetMarkerIdx) {
        this.datasetMarkerIdx = datasetMarkerIdx;
    }

    public JsonNode getProperties() {
        return properties;
    }

    public void setProperties(JsonNode properties) {
        this.properties = properties;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public Cv getStatus() {
        return status;
    }

    public void setStatus(Cv status) {
        this.status = status;
    }

}
