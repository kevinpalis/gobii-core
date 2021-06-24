/**
 * Organization.java
 * 
 * Entity class
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-26
 */

package org.gobiiproject.gobiimodel.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organization")
@Data
@NoArgsConstructor
public class Organization extends BaseEntity {

    @Id
    @Column(name="organization_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer organizationId;


    @Column(name="name")
    private String name;

    @Column(name="address")
    private String address;

    @Column(name="website")
    private String website;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "cv_id")
    private Cv status;

}