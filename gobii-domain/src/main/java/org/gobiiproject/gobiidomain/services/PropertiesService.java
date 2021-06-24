/**
 * PropertiesService.java
 * 
 * Contains project properties related services
 * @author Rodolf N. Duldulao, Jr.
 * @since 2020-03-18
 * 
 */

package org.gobiiproject.gobiidomain.services;

import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

public interface PropertiesService {
    //PagedResult<CvPropertyDTO> getProjectProperties(Integer pageNum, Integer pageSize) throws Exception;
    PagedResult<CvPropertyDTO> getProperties(Integer pageNum, Integer pageSize, CvGroupTerm cvGroupTerm) throws Exception;
}