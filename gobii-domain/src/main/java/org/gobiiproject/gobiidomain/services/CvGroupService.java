package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.noaudit.CvDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.CvGroupDTO;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;

/**
 * Created by VCalaminos on 2/1/2017.
 */
public interface CvGroupService {

    List<CvDTO> getCvsForGroup(Integer groupId) throws GobiiDomainException;
    List<CvGroupDTO> getCvsForType(GobiiCvGroupType groupType) throws GobiiDomainException;
    CvGroupDTO getCvGroupDetailsByGroupName(String groupName, Integer cvGroupTypeId) throws GobiiDomainException;

}
