package org.gobiiproject.gobiimodel.dto.auditable;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;
import org.gobiiproject.gobiimodel.dto.entity.children.EntityPropertyDTO;
import org.gobiiproject.gobiimodel.dto.entity.children.VendorProtocolDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;

/**
 * Created by VCalaminos on 2016-12-12.
 */
public class ProtocolDTO extends DTOBaseAuditable {

    public ProtocolDTO(){
        super(GobiiEntityNameType.PROTOCOL);
    }


    private Integer protocolId = 0;
    private String name;
    private String description;
    private Integer typeId;
    private Integer platformId;
    private Integer status;
    private List<EntityPropertyDTO> props = new ArrayList<>();

    private List<VendorProtocolDTO> vendorProtocols = new ArrayList<>();

    public List<VendorProtocolDTO> getVendorProtocols() {
        return vendorProtocols;
    }

    public void setVendorProtocols(List<VendorProtocolDTO> vendorProtocols) {
        this.vendorProtocols = vendorProtocols;
    }


    @Override
    public Integer getId(){ return this.protocolId; }

    @Override
    public void setId(Integer id){ this.protocolId = id; }

    @GobiiEntityParam(paramName = "protocolId")
    public Integer getProtocolId(){ return protocolId; }

    @GobiiEntityColumn(columnName = "protocol_id")
    public void setProtocolId(Integer protocolId){ this.protocolId = protocolId; }

    @GobiiEntityParam(paramName = "name")
    public String getName(){ return name;}

    @GobiiEntityColumn(columnName = "name")
    public void setName(String name){ this.name = name;}

    @GobiiEntityParam(paramName = "description")
    public String getDescription(){ return description;}

    @GobiiEntityColumn(columnName = "description")
    public void setDescription(String description){ this.description = description; }

    @GobiiEntityParam(paramName = "typeId")
    public Integer getTypeId(){ return typeId; }

    @GobiiEntityColumn(columnName = "type_id")
    public void setTypeId(Integer typeId){ this.typeId = typeId; }

    @GobiiEntityParam(paramName = "platformId")
    public Integer getPlatformId(){ return platformId; }

    @GobiiEntityColumn(columnName = "platform_id")
    public void setPlatformId(Integer platformId){ this.platformId = platformId; }


    @GobiiEntityParam(paramName = "status")
    public Integer getStatus(){ return status; }

    @GobiiEntityColumn(columnName = "status")
    public void setStatus(Integer status){ this.status = status;}

    public List<EntityPropertyDTO> getProps(){ return props; }

    public void setProps(List<EntityPropertyDTO> props){ this.props = props; }
}
