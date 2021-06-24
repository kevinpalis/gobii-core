package org.gobiiproject.gobiidomain.services.gdmv3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.EntityDoesNotExistException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvGroupDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.modelmapper.CvMapper;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.springframework.beans.factory.annotation.Autowired;

public class CvServiceImpl implements CvService {

    @Autowired
    private CvDao cvDao;

    @Transactional
    @Override
    public CvDTO createCv(CvDTO request) throws Exception {
        Cv cv = new Cv();
        // check cvGroup Id
        CvGroup cvGroup = this.loadCvGroup(request.getCvGroupId());
        cv.setCvGroup(cvGroup);

        cv.setTerm(request.getCvName());
        cv.setDefinition(request.getCvDescription());
        cv.setRank(0);

        Cv newStatus = cvDao.getNewStatus();
        cv.setStatus(newStatus.getCvId());

        cvDao.createCv(cv);

        CvDTO resultDTO = new CvDTO();
        ModelMapper.mapEntityToDto(cv, resultDTO);

        // set status
        resultDTO.setCvStatus(newStatus.getTerm());

        return resultDTO;
    }

    @Transactional
    @Override
    public CvDTO updateCv(Integer id, CvDTO request) throws Exception {
        // check
        boolean updated = false;
        Cv cv = this.loadCv(id);

        // check if change of name
        if (!LineUtils.isNullOrEmpty(request.getCvName())) {
            cv.setTerm(request.getCvName());
            updated = true;
        }
        ;

        // check if change description
        if (!LineUtils.isNullOrEmpty(request.getCvDescription())) {
            cv.setDefinition(request.getCvDescription());
            updated = true;
        }

        // check if change of group
        if (request.getCvGroupId() != null) {
            // check if new group id exists
            CvGroup cvGroup = this.loadCvGroup(request.getCvGroupId());
            cv.setCvGroup(cvGroup);
            updated = true;
        }


        Cv updatedCv = cv;
        if (updated) {
            Cv modifiedCv = cvDao.getModifiedStatus();
            cv.setStatus(modifiedCv.getCvId());
            updatedCv =  cvDao.updateCv(cv);
        }

        CvDTO updatedCvDTO = new CvDTO();

        ModelMapper.mapEntityToDto(updatedCv, updatedCvDTO);

        return updatedCvDTO;
    }

    @Transactional
    @Override
    public PagedResult<CvDTO> getCvs(Integer page,
                                     Integer pageSize,
                                     String cvGroupName,
                                     String cvGroupType
    ) throws Exception {
        GobiiCvGroupType groupType = null;
        if (!LineUtils.isNullOrEmpty(cvGroupType)) {
            groupType = this.getCvGroupType(cvGroupType.toLowerCase());   
        }

        List<Cv> cvs = cvDao.getCvs(null, cvGroupName, groupType, page, pageSize);
        List<CvDTO> cvDTOs = new ArrayList<>();

        Cv newStatus = cvDao.getNewStatus();
        Cv modStatus = cvDao.getModifiedStatus();
        cvs.forEach(cv -> {
            CvDTO cvDTO = new CvDTO();
            ModelMapper.mapEntityToDto(cv, cvDTO);

            if (cvDTO.getStatus() == newStatus.getCvId()) {
                cvDTO.setCvStatus(newStatus.getTerm());
            } else {
                cvDTO.setCvStatus(modStatus.getTerm());
            }

            cvDTOs.add(cvDTO);

        });

        return PagedResult.createFrom(page, cvDTOs);
    }

    @Transactional
    @Override
    public CvDTO getCv(Integer id) throws Exception {
        Cv cv = this.loadCv(id);

        CvDTO cvDTO = new CvDTO();
        ModelMapper.mapEntityToDto(cv, cvDTO);

        // get status cv
        Cv statusCv = cvDao.getCvByCvId(cv.getStatus());

        cvDTO.setCvStatus(statusCv.getTerm());

        return cvDTO;
    }



    @Transactional
    @Override
    public void deleteCv(Integer id) throws Exception {
        Cv cv = this.loadCv(id);

        // check group
        if (cv.getCvGroup().getCvGroupType() != GobiiCvGroupType.GROUP_TYPE_USER.getGroupTypeId()) {
            throw new GobiiDomainException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.BAD_REQUEST,
                    "Cannot delete system defined cv");
        }

        cvDao.deleteCv(cv);

    }

    private Cv loadCv(Integer id) throws Exception {
        Cv cv = cvDao.getCvByCvId(id);
        if (cv == null) {
            throw new EntityDoesNotExistException("cv");
        }
        return cv;
    }

    @Override
    @Transactional
    public PagedResult<CvGroupDTO> getCvGroups(Integer page,
                                               Integer pageSize,
                                               String cvGroupTypeName) {
        Integer offset = page * pageSize;
        GobiiCvGroupType cvGroupType;

        if(cvGroupTypeName != null) {
            cvGroupType = this.getCvGroupType(cvGroupTypeName.toLowerCase());
        }
        else {
            cvGroupType = GobiiCvGroupType.GROUP_TYPE_UNKNOWN;
        }

        List<CvGroup> cvGroups = cvDao.getCvGroups(pageSize, offset, cvGroupType);

        List<CvGroupDTO> cvGroupDTOs = new ArrayList<>();
        cvGroups.forEach(group -> {
            CvGroupDTO cvGroupDTO = new CvGroupDTO();
            ModelMapper.mapEntityToDto(group, cvGroupDTO);
            cvGroupDTOs.add(cvGroupDTO);
        });

        return PagedResult.createFrom(page, cvGroupDTOs);
    }

    private CvGroup loadCvGroup(Integer cvGroupId) throws InvalidException {
        CvGroup cvGroup = cvDao.getCvGroupById(cvGroupId);
        if (!Optional.ofNullable(cvGroup)
                     .map(v -> v.getCvGroupType())
                     .orElse(0)
                     .equals(GobiiCvGroupType.GROUP_TYPE_USER.getGroupTypeId())
        ) {
            throw new InvalidException("cv group");
        }
        return cvGroup;
    }

    private boolean checkPropertiesExist(List<CvPropertyDTO> props) {
        return Optional.ofNullable(props).map(v -> v.size()).orElse(0) > 0;
    }


    private GobiiCvGroupType getCvGroupType(String cvGroupType) {
        if (cvGroupType.equals("system_defined") || cvGroupType.equals("1")) return GobiiCvGroupType.GROUP_TYPE_SYSTEM;
        if (cvGroupType.equals("user_defined") || cvGroupType.equals("2")) return GobiiCvGroupType.GROUP_TYPE_USER;
        return GobiiCvGroupType.GROUP_TYPE_UNKNOWN;
    }
    
}