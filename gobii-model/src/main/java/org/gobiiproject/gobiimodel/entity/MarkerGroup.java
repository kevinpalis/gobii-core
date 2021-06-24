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

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="marker_group")
@NoArgsConstructor
public class MarkerGroup extends BaseEntity {

    @Id
    @Column(name="marker_group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer markerGroupId;

    @Column(name="name")
    private String name;

    @Column(name="code")
    private String code;

    @Column(name="markers")
    @Type(type = "JsonNodeType")
    private JsonNode markers;

    @Column(name="germplasm_group")
    private String germplasmGroup;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "cv_id")
    private Cv status;
    
}