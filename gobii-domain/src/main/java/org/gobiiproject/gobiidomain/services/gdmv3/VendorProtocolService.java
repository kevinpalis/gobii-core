/**
 * ContactService.java
 * 
 * Interface for GDM V3 Contact service
 */

package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.dto.gdmv3.VendorProtocolDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface VendorProtocolService {

    PagedResult<VendorProtocolDTO> getVendorProtocols(
            Integer page, Integer pageSize) throws Exception;
    
 }