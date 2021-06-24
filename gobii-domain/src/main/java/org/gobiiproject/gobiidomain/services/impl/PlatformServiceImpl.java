package org.gobiiproject.gobiidomain.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.PlatformService;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapPlatform;
import org.gobiiproject.gobiimodel.dto.auditable.PlatformDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Phil on 4/27/2016.
 */
public class PlatformServiceImpl implements PlatformService {

    Logger LOGGER = LoggerFactory.getLogger(PlatformServiceImpl.class);

    @Autowired
    DtoMapPlatform dtoMapPlatform;



    @Override
    public List<PlatformDTO> getPlatforms() throws GobiiDomainException {

        List<PlatformDTO> returnVal;

            returnVal = dtoMapPlatform.getList();
            for(PlatformDTO currentPlatformDTO : returnVal ) {
                currentPlatformDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
                currentPlatformDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
            }


            if (null == returnVal) {
                returnVal = new ArrayList<>();
            }

        return returnVal;
    }

    @Override
    public PlatformDTO getPlatformById(Integer platformId) throws GobiiDomainException{

        PlatformDTO returnVal;

            returnVal = dtoMapPlatform.get(platformId);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);


            if (null == returnVal.getPlatformId() || returnVal.getPlatformId()<1) {
                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "The specified platformId ("
                                + platformId
                                + ") does not match an existing platform ");
            }

        return returnVal;
    }@Override

    public PlatformDTO getPlatformDetailsByVendorProtocolId(Integer vendorProtocolId) throws GobiiDomainException{

        PlatformDTO returnVal;

        returnVal = dtoMapPlatform.getPlatformDetailsByVendorProtocolId(vendorProtocolId);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);


        if (null == returnVal.getPlatformId() || returnVal.getPlatformId()<1) {
            throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "The specified vendorProtocol id ("
                            + vendorProtocolId
                            + ") does not belong to an existing platform ");
        }

        return returnVal;
    }

    @Override
    public PlatformDTO createPlatform(PlatformDTO platformDTO) throws GobiiDomainException {
        PlatformDTO returnVal;


            returnVal = dtoMapPlatform.create(platformDTO);

            // When we have roles and permissions, this will be set programmatically
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        return returnVal;
    }

    @Override
    public PlatformDTO replacePlatform(Integer platformId, PlatformDTO platformDTO) throws GobiiDomainException {
        PlatformDTO returnVal;


            if (null == platformDTO.getPlatformId() ||
                    platformDTO.getPlatformId().equals(platformId)) {


                PlatformDTO existingPlatformDTO = dtoMapPlatform.get(platformId);
                if (null != existingPlatformDTO.getPlatformId() && existingPlatformDTO.getPlatformId().equals(platformId)) {


                    returnVal = dtoMapPlatform.replace(platformId, platformDTO);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

                } else {

                    throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                            GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                            "The specified platformId ("
                                    + platformId
                                    + ") does not match an existing platform ");
                }

            } else {

                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "The platformId specified in the dto ("
                                + platformDTO.getPlatformId()
                                + ") does not match the platformId passed as a parameter "
                                + "("
                                + platformId
                                + ")");

            }


        return returnVal;
    }
}
