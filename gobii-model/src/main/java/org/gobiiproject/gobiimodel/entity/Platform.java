/**
 * Platform.java
 * 
 * Platform Entity for GDMV3
 */
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "platform")
@EqualsAndHashCode(callSuper=false)
@NamedEntityGraph(name="graph.platform",
    attributeNodes = {
        @NamedAttributeNode( value = "type")
    }
)
public class Platform extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "platform_id")
    private Integer platformId;

    @Column(name="name")
    private String platformName;

    @Column(name="code")
    private String platformCode;

    @Column(name="description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", referencedColumnName = "cv_id")
    private Cv type;

    @Column(name="props", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode properties;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "cv_id")
    private Cv status;

    @OneToOne(mappedBy = "platform")
    private PlatformStats platformStats;

}
