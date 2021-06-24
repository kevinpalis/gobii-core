package org.gobiiproject.gobiisampletrackingdao;


import java.util.List;

import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;

public interface CvDao {

    List<Cv> getCvListByCvGroup(String cvGroupName, GobiiCvGroupType cvGroupType);
    List<Cv> getCvs(String cvTerm, String cvGroupName, GobiiCvGroupType cvType);
    Cv getCvByCvId(Integer cvId) throws Exception;

    //new - added by rnduldulaojr
    List<Cv> getCvs(String cvTerm, String cvGroupName, GobiiCvGroupType cvType, Integer page, Integer pageSize);
    CvGroup getCvGroupByNameAndType(String cvGroupName, Integer type);
    CvGroup getCvGroupById(Integer id);
    Cv createCv(Cv cv);
    
    //convenience routines
    Cv getNewStatus();
    Cv getModifiedStatus();
	Cv updateCv(Cv cv);
	void deleteCv(Cv cv) throws Exception;
	List<CvGroup> getCvGroups(Integer pageSize, Integer offset, GobiiCvGroupType type);
}
