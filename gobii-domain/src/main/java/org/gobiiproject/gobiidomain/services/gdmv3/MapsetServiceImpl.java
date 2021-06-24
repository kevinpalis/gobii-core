package org.gobiiproject.gobiidomain.services.gdmv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.EntityAlreadyExistsException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.EntityDoesNotExistException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidTypeException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.UnknownEntityException;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MapsetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.entity.Mapset;
import org.gobiiproject.gobiimodel.entity.Reference;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.MapsetDao;
import org.gobiiproject.gobiisampletrackingdao.ReferenceDao;
import org.springframework.beans.factory.annotation.Autowired;

public class MapsetServiceImpl implements MapsetService {

    @Autowired
    private MapsetDao mapsetDao;

    @Autowired
    private CvDao cvDao;

    @Autowired
    private ReferenceDao referenceDao;

    @Autowired
    private ContactDao contactDao;

    @Transactional
    @Override
    public PagedResult<MapsetDTO> getMapsets(Integer page, Integer pageSize, Integer mapsetTypeId) throws Exception {
        Integer offset = page * pageSize;
        List<Mapset> mapsets = mapsetDao.getMapsets(pageSize, offset, mapsetTypeId);
        List<MapsetDTO> mapsetDTOs = new ArrayList<>();
        mapsets.forEach(mapset -> {
            MapsetDTO mapsetDTO = new MapsetDTO();
            ModelMapper.mapEntityToDto(mapset, mapsetDTO);
            mapsetDTOs.add(mapsetDTO);
        });

        return PagedResult.createFrom(page, mapsetDTOs);
    }

    @Transactional
    @Override
    public MapsetDTO createMapset(MapsetDTO mapset, String createdBy) throws Exception {
        // check if mapset has mapsetTypeId and if it's a valid mapsetTypeId
        Cv type = this.getType(mapset.getMapsetTypeId());

        // check same name mapset -- this should have been implemented in DB if name should be unique
        if (mapsetDao.getMapsetByName(mapset.getMapsetName()) != null) {
            throw new EntityAlreadyExistsException.Mapset();
        }

        // check the referenceId
        Reference reference = null;
        if (mapset.getReferenceId() != null) {
            reference = this.getReference(mapset.getReferenceId());
        }

        Mapset createdMapset = new Mapset();
        createdMapset.setMapsetName(mapset.getMapsetName());
        if (mapset.getMapsetDescription() != null)
            createdMapset.setMapsetDescription(mapset.getMapsetDescription());
        createdMapset.setType(type);
        createdMapset.setReference(reference);
        // audit items
        Contact creator = contactDao.getContactByUsername(createdBy);
        createdMapset.setCreatedBy(Optional.ofNullable(creator).map(v -> v.getContactId()).orElse(null));
        createdMapset.setCreatedDate(new java.util.Date());

        // other required colums
        createdMapset
                .setMapsetCode(String.format("%s_%s", type.getTerm(), createdMapset.getMapsetName()).replace(' ', '_'));

        // status
        Cv status = cvDao.getNewStatus();
        createdMapset.setStatus(status); 

        createdMapset = mapsetDao.createMapset(createdMapset);
        MapsetDTO createdMapsetDTO = new MapsetDTO();
        ModelMapper.mapEntityToDto(createdMapset, createdMapsetDTO);
        return createdMapsetDTO;
    }

    @Transactional
    @Override
    public MapsetDTO getMapset(Integer mapsetId) throws Exception {
        Mapset mapset = this.loadMapset(mapsetId);
        MapsetDTO mapsetDTO = new MapsetDTO();
        ModelMapper.mapEntityToDto(mapset, mapsetDTO);
        return mapsetDTO;
    }

    @Transactional
    @Override
    public MapsetDTO updateMapset(Integer mapsetId, MapsetDTO patchData, String editedBy) throws Exception {
        Mapset mapset = this.loadMapset(mapsetId);
        if (patchData.getMapsetName() != null) {
            //check if other mapset already exists
            Mapset other = mapsetDao.getMapsetByName(patchData.getMapsetName());
            if (other != null && other.getMapsetId() != mapset.getMapsetId()) {
                throw new EntityAlreadyExistsException.Mapset();
            }
            mapset.setMapsetName(patchData.getMapsetName());
        }

        if (patchData.getMapsetDescription() != null) {
            mapset.setMapsetDescription(patchData.getMapsetDescription()); 
        }

        if (patchData.getMapsetTypeId() != null) {
            Cv type = this.getType(patchData.getMapsetTypeId());
            mapset.setType(type);
        }

        if (patchData.getReferenceId() != null) {
            Reference reference = this.getReference(patchData.getReferenceId());
            mapset.setReference(reference);
        }

        // audit items
        Contact creator = contactDao.getContactByUsername(editedBy);
        mapset.setModifiedBy(Optional.ofNullable(creator).map( v -> v.getContactId()).orElse(null));
        mapset.setModifiedDate(new java.util.Date());

        // updated status
        Cv status = cvDao.getModifiedStatus();
        mapset.setStatus(status);

        mapset = mapsetDao.updateMapset(mapset);
        MapsetDTO updatedMapsetDTO = new MapsetDTO();
        ModelMapper.mapEntityToDto(mapset, updatedMapsetDTO);
        return updatedMapsetDTO;

    }

    private Reference getReference(Integer referenceId) throws Exception {
        Reference reference = referenceDao.getReference(referenceId);
        if (reference == null) {
            throw new UnknownEntityException.Reference();
        }
        return reference;

    }

    private Cv getType(Integer cvId) throws Exception {
        Cv type = cvDao.getCvByCvId(cvId);
        if (type == null) {
            throw new UnknownEntityException.MapsetType();
        }

        // check type is correct type
        if (!type.getCvGroup().getCvGroupName().equals(CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName())) {
            throw new InvalidTypeException();
        }
        return type;
    }

    @Transactional
    @Override
    public void deleteMapset(Integer mapsetId) throws Exception {
        Mapset mapset = this.loadMapset(mapsetId);
        mapsetDao.deleteMapset(mapset);
    }

    @Transactional
    @Override
    public CvTypeDTO createMapsetType(String mapsetTypeName, String mapsetTypeDescription, String user) {

        CvGroup cvGroup = cvDao.getCvGroupByNameAndType(CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName(), 2 // TODO:
                                                                                                            // this is
                                                                                                            // custom
                                                                                                            // type
        );
        if (cvGroup == null)
            throw new GobiiDaoException("Missing CvGroup for Mapset Type");

        Cv cv = new Cv();
        cv.setCvGroup(cvGroup);
        cv.setTerm(mapsetTypeName);

        if (mapsetTypeDescription != null)
            cv.setDefinition(mapsetTypeDescription);

        // get the new row status
        // Get the Cv for status, new row
        Cv status = cvDao.getNewStatus();
        cv.setStatus(status.getCvId());

        // set rank
        cv.setRank(0);
        cv = cvDao.createCv(cv);

        CvTypeDTO mapsetDTO = new CvTypeDTO();
        ModelMapper.mapEntityToDto(cv, mapsetDTO);
        return mapsetDTO;
    }

    @Transactional
    @Override
    public PagedResult<CvTypeDTO> getMapsetTypes(Integer page, Integer pageSize) {
        List<Cv> cvs = cvDao.getCvs(null, CvGroupTerm.CVGROUP_MAPSET_TYPE.getCvGroupName(), null, page, pageSize);
        List<CvTypeDTO> mapsetTypeDTOs = new ArrayList<>();

        cvs.forEach(cv -> {
            CvTypeDTO mapsetTypeDTO = new CvTypeDTO();
            ModelMapper.mapEntityToDto(cv, mapsetTypeDTO);
            mapsetTypeDTOs.add(mapsetTypeDTO);
        });

        return PagedResult.createFrom(page, mapsetTypeDTOs);
    }

    private Mapset loadMapset(Integer mapsetId) throws Exception {
        Mapset mapset = mapsetDao.getMapset(mapsetId);
        if (mapset == null) {
            throw new EntityDoesNotExistException.Mapset();
        }
        return mapset;
    }

}