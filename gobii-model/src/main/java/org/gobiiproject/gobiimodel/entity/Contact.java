package org.gobiiproject.gobiimodel.entity;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="contact")
@NamedEntityGraph(name = "contact.organization",
    attributeNodes = @NamedAttributeNode("organization")
)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Contact extends BaseEntity {
    @Id
    @Column(name="contact_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contactId;

    @Column(name="lastname")
    private String lastName;
    
    @Column(name="firstname")
	private String firstName;	
    
    @Column(name="code")
    private String code;	
    
    @Column(name="email")
    private String email;
    
    @Column(name="roles")
    @Type(type = "IntArrayType")
    private Integer[] roles;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name="organization_id", referencedColumnName="organization_id")
    private Organization organization;

    @Column(name="username")
    private String username;


    public Integer getOrganizationId() {
        if (this.organization == null) return null;
        return this.organization.getOrganizationId();
    }

    // public void setOrganizationId(Integer organizationId) {
    //     this.organizationId = organizationId;
    // }

    
}