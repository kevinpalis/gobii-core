/**
 * VendorProtocol.java
 * 
 * Entity class for Vendor Protocol
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-31
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
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "vendor_protocol")
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "vendor_protocol.vendor",
        attributeNodes = @NamedAttributeNode("vendor")
    ),
    @NamedEntityGraph(
        name = "vendor_protocol.protocol",
        attributeNodes = @NamedAttributeNode("protocol")
    )
})
@NoArgsConstructor
public class VendorProtocol  {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_protocol_id")
    private Integer vendorProtocolId;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "vendor_id", referencedColumnName = "organization_id")
    private Organization vendor;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "protocol_id", referencedColumnName = "protocol_id")
    private Protocol protocol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "cv_id")
    private Cv status;
    
}