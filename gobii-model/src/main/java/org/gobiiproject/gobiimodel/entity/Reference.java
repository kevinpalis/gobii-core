package org.gobiiproject.gobiimodel.entity;


import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Model for Reference Entity.
 * Represents database table reference.
 *
 */
@Entity
@Table(name = "reference")
@Data
@EqualsAndHashCode(callSuper=false)
public class Reference extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id")
    private Integer referenceId;

    @Column(name="name")
    private String referenceName;

    @Column(name="version")
    private String version;

    @Column(name="link")
    private String link;

    @Column(name="file_path")
    private String filePath;

}
