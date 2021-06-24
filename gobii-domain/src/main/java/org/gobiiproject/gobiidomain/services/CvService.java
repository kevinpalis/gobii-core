// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.noaudit.CvDTO;

/**
 * Created by Angel on 4/29/2016.
 * Modified by Yanii on 1/25/2017
 */
public interface CvService {
    CvDTO createCv(CvDTO cvDTO) throws GobiiDomainException;
    CvDTO replaceCv(Integer cvId, CvDTO cvDTO) throws GobiiDomainException;
    CvDTO deleteCv(Integer cvId) throws GobiiDomainException;
    CvDTO getCvById(Integer cvId) throws GobiiDomainException;
    List<CvDTO> getCvs() throws GobiiDomainException;
    List<CvDTO> getCvsByGroupName(String groupName) throws GobiiDomainException;
}
