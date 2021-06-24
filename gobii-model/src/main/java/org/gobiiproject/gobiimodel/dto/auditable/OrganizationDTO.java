package org.gobiiproject.gobiimodel.dto.auditable;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;
import org.gobiiproject.gobiimodel.dto.children.VendorProtocolDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;


/**
 * Created by Angel on 5/6/2016.
 */
public class OrganizationDTO extends DTOBaseAuditable {

    public OrganizationDTO() {
        super(GobiiEntityNameType.ORGANIZATION);
    }


    // we are waiting until we a have a view to retirn
    // properties for that property: we don't know how to represent them yet
    private Integer organizationId = 0;
    private String name;
    private String address;
    private String website;
    private Integer statusId;

    private List<VendorProtocolDTO> vendorProtocols = new ArrayList<>();

    public List<VendorProtocolDTO> getVendorProtocols() {
        return vendorProtocols;
    }

    public void setVendorProtocols(List<VendorProtocolDTO> vendorProtocols) {
        this.vendorProtocols = vendorProtocols;
    }

    @Override
    public Integer getId() {
        return this.organizationId;
    }

    @Override
    public void setId(Integer id) {
        this.organizationId = id;
    }

    @GobiiEntityParam(paramName = "organizationId")
    public Integer getOrganizationId() {return organizationId;}
    @GobiiEntityColumn(columnName = "organization_id")
    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    @GobiiEntityParam(paramName = "name")
    public String getName() {
        return name;
    }
    @GobiiEntityColumn(columnName = "name")
    public void setName(String name) {
        this.name = name;
    }

    @GobiiEntityParam(paramName = "address")
    public String getAddress() {
        return address;
    }
    @GobiiEntityColumn(columnName = "address")
    public void setAddress(String address) {
        this.address = address;
    }

    @GobiiEntityParam(paramName = "website")
    public String getWebsite() {return website;}
    @GobiiEntityColumn(columnName = "website")
    public void setWebsite(String website) {
        this.website = website;
    }

    @GobiiEntityParam(paramName = "status")
    public Integer getStatusId() {
        return statusId;
    }

    @GobiiEntityColumn(columnName ="status")
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

}
