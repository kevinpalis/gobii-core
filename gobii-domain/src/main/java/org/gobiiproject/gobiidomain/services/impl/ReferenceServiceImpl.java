package org.gobiiproject.gobiidomain.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.ReferenceService;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapReference;
import org.gobiiproject.gobiimodel.dto.auditable.ReferenceDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Angel on 5/4/2016.
 */
public class ReferenceServiceImpl implements ReferenceService {

    Logger LOGGER = LoggerFactory.getLogger(ReferenceServiceImpl.class);

    @Autowired
    DtoMapReference dtoMapReference = null;

    @Override
    public ReferenceDTO createReference(ReferenceDTO referenceDTO) throws GobiiDomainException {

        ReferenceDTO returnVal;

        try {

            returnVal = dtoMapReference.create(referenceDTO);

            // When we have roles and permissions, this will be set programmatically
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

        return returnVal;
    }

    @Override
    public ReferenceDTO replaceReference(Integer referenceId, ReferenceDTO referenceDTO) throws GobiiDomainException {

        ReferenceDTO returnVal;

        try {

            if(null == referenceDTO.getReferenceId() ||
                    referenceDTO.getReferenceId().equals(referenceId)) {

                ReferenceDTO existingReferenceDTO = dtoMapReference.get(referenceId);
                if(null != existingReferenceDTO.getReferenceId() && existingReferenceDTO.getReferenceId().equals(referenceId)) {

                    returnVal = dtoMapReference.replace(referenceId, referenceDTO);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

                } else {

                    throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                            GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                            "The specified referenceId ("
                                    + referenceId
                                    + ") does not match an existing reference.");
                }
            } else {

                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "The referenceId specified in the dto ("
                                + referenceDTO.getReferenceId()
                                + ") does not match the referenceId passed as a parameter "
                                + "("
                                + referenceId
                                + ")");
            }


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

        return returnVal;
    }

    @Override
    public List<ReferenceDTO> getReferences() throws GobiiDomainException {

        List<ReferenceDTO> returnVal;

        returnVal = dtoMapReference.getList();
        for(ReferenceDTO currentReferenceDTO : returnVal) {
            currentReferenceDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
            currentReferenceDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
        }

        if(null == returnVal) {
            returnVal = new ArrayList<>();
        }

        return returnVal;
    }

    @Override
    public ReferenceDTO getReferenceById(Integer referenceId) throws GobiiDomainException {

        ReferenceDTO returnVal;

        returnVal = dtoMapReference.get(referenceId);
        if (null == returnVal) {
            throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "The specified referenceId ("
                            + referenceId
                            + ") does not match an existing reference ");
        }

        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        
        return returnVal;
    }
}