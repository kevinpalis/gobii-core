
/**
 * ContactDTO.java
 * 
 * GDM V3 Version
 * 
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-26
 */
package org.gobiiproject.gobiimodel.dto.gdmv3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.VendorProtocol;

import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIgnoreProperties(ignoreUnknown = false, value={
    "id", "allowedProcessTypes", "entityNameType", "status"
})
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
@EqualsAndHashCode(callSuper=false)
public class VendorProtocolDTO {


    @GobiiEntityMap(paramName = "vendorProtocolId", entity = VendorProtocol.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer vendorProtocolId;

    @GobiiEntityMap(paramName = "name", entity = VendorProtocol.class)
    private String vendorProtocolName;

    @GobiiEntityMap(paramName = "vendor.organizationId", entity = VendorProtocol.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer vendorId;

    @GobiiEntityMap(paramName = "vendor.name", entity = VendorProtocol.class, deep = true)
    private String vendorName;

    @GobiiEntityMap(paramName = "protocol.protocolId", entity = VendorProtocol.class, deep = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer protocolId;

    @GobiiEntityMap(paramName = "protocol.name", entity = VendorProtocol.class, deep = true)
    private String protocolName;


}