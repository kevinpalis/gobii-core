/**
 * ReferenceServiceImpl.java
 * 
 * @author Rodolfo N. Duldulao, Jr.
 */
package org.gobiiproject.gobidomain.services.gdmv3;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.gobiiproject.gobidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.gdmv3.ReferenceDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Reference;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.ReferenceDao;
import org.springframework.beans.factory.annotation.Autowired;

public class ReferenceServiceImpl implements ReferenceService {

    @Autowired
    private ReferenceDao referenceDao;

    @Autowired
    private ContactDao contactDao;

    @Transactional
    @Override
    public PagedResult<ReferenceDTO> getReferences(Integer page, Integer pageSize) {
        Integer offset = page * pageSize;
        List<Reference> references = referenceDao.getReferences(offset, pageSize);

        List<ReferenceDTO> referenceDTOs = new java.util.ArrayList<>();
        references.forEach(reference -> {
            ReferenceDTO referenceDTO = new ReferenceDTO();
            ModelMapper.mapEntityToDto(reference, referenceDTO);
            referenceDTOs.add(referenceDTO);
        });

        return PagedResult.createFrom(page, referenceDTOs);

    }

    @Transactional
    @Override
    public ReferenceDTO createReference(ReferenceDTO request, String createdBy) throws Exception {
        Reference reference = new Reference();

        reference.setReferenceName(request.getReferenceName());
        reference.setVersion(request.getVersion());
        
        //not in spec : filePath and link
        //audit
        Contact contact = contactDao.getContactByUsername(createdBy);
        if (contact != null) reference.setCreatedBy(contact.getContactId());
        reference.setCreatedDate(new Date());
        
        Reference createdReference = referenceDao.createReference(reference);
        ReferenceDTO referenceDTO = new ReferenceDTO();

        ModelMapper.mapEntityToDto(createdReference, referenceDTO);

        return referenceDTO;
    }

    @Transactional
    @Override
    public ReferenceDTO getReference(Integer referenceId) throws Exception  {
        Reference reference = this.loadReference(referenceId);
        ReferenceDTO referenceDTO = new ReferenceDTO();
        ModelMapper.mapEntityToDto(reference, referenceDTO);
        return referenceDTO;      
    }

    private Reference loadReference(Integer referenceId) throws Exception {
        Reference reference = referenceDao.getReference(referenceId);
        if (reference == null) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                "Reference not found"
            );
        }
        return reference;

    }

    @Transactional
    @Override
    public ReferenceDTO updateReference(Integer referenceId, ReferenceDTO request, String updatedBy) throws Exception {
        Reference reference = this.loadReference(referenceId);
        
        boolean updated = false;
        if (!LineUtils.isNullOrEmpty(request.getReferenceName())){
            reference.setReferenceName(request.getReferenceName());
            updated = true;
        }

        if (!LineUtils.isNullOrEmpty(request.getVersion())) {
            reference.setVersion(request.getVersion());
            updated = true;
        }

        if (updated) {
            Contact contact = contactDao.getContactByUsername(updatedBy);
            if (contact != null) reference.setModifiedBy(contact.getContactId());
            reference.setModifiedDate(new Date());

            reference = referenceDao.updateReference(reference);
            
        }

        ReferenceDTO referenceDTO = new ReferenceDTO();
        ModelMapper.mapEntityToDto(reference, referenceDTO);

        return referenceDTO;
    }
    
}