/**
 * ContactDao.java
 * 
 * Contact DAO object for contact data.
 * @author RNDuldulao, Jr.
 * @since 2020-03-23
 */

package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.VendorProtocol;

import java.util.List;

public interface VendorProtocolDao {
    List<VendorProtocol> getVendorProtocols(Integer page, Integer pageSize);
    VendorProtocol getVendorProtocol(Integer protocolId, Integer vendorId);
    VendorProtocol createVendorProtocol(VendorProtocol vendorProtocol);
 }