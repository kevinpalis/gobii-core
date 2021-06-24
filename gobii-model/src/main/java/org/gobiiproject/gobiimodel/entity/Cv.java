package org.gobiiproject.gobiimodel.entity;

import java.io.Serializable;

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

@Entity
@Table(name = "cv")
@SuppressWarnings("serial")
@Data
@NamedEntityGraph(name = "graph.cv",
    attributeNodes = {
        @NamedAttributeNode(value="cvGroup")
    }
)
public class Cv implements Serializable{

    @Id
    @Column(name = "cv_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cvId;

    @Column(name="term")
    private String term;

    @Column(name="definition")
    private String definition;

    @Column(name = "rank")
    private Integer rank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cvgroup_id")
    private CvGroup cvGroup;

    @Column(name = "abbreviation")
    private String abbreviation;

    @Column(name = "dbxref_id")
    private Integer dbxrefId;

    @Column(name = "status")
    private Integer status;


    // @Column(name="props", columnDefinition = "jsonb")
    // @Convert(converter = JsonbConverter.class)
    // private JsonNode props;

    @Column(name="props", columnDefinition = "jsonb")
    @Type(type = "CvPropertiesType")
    private java.util.Map<String, String> properties;

    public Integer getCvId() {
        return cvId;
    }

    public void setCvId(Integer cvId) {
        this.cvId = cvId;
    }


}
