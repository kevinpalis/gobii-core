package org.gobiiproject.gobiidomain.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.MarkerGroupService;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapMarkerGroup;
import org.gobiiproject.gobiimodel.dto.auditable.MarkerGroupDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Phil on 4/21/2016.
 */
public class MarkerGroupServiceImpl implements MarkerGroupService {

    Logger LOGGER = LoggerFactory.getLogger(MarkerGroupServiceImpl.class);

    @Autowired
    DtoMapMarkerGroup dtoMapMarkerGroup = null;

    @Override
    public MarkerGroupDTO createMarkerGroup(MarkerGroupDTO markerGroupDTO) throws GobiiDomainException {

        MarkerGroupDTO returnVal;

        try {

            returnVal = dtoMapMarkerGroup.create(markerGroupDTO);

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
    public MarkerGroupDTO replaceMarkerGroup(Integer markerGroupId, MarkerGroupDTO markerGroupDTO) throws GobiiDomainException {

        MarkerGroupDTO returnVal;

        try {

            if(null == markerGroupDTO.getMarkerGroupId() ||
                    markerGroupDTO.getMarkerGroupId().equals(markerGroupId)) {

                MarkerGroupDTO existingMapsetDTO = dtoMapMarkerGroup.get(markerGroupId);
                if(null != existingMapsetDTO.getMarkerGroupId() && existingMapsetDTO.getMarkerGroupId().equals(markerGroupId)) {

                    returnVal = dtoMapMarkerGroup.replace(markerGroupId, markerGroupDTO);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

                } else {

                    throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                            GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                            "The specified markerGroupId ("
                                    + markerGroupId
                                    + ") does not match an existing markergroup.");
                }
            } else {

                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "The markerGroupId specified in the dto ("
                                + markerGroupDTO.getMarkerGroupId()
                                + ") does not match the markerGroupId passed as a parameter "
                                + "("
                                + markerGroupId
                                + ")");
            }


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

        return returnVal;

    }

    @Override
    public List<MarkerGroupDTO> getMarkerGroups() throws GobiiDomainException {

        List<MarkerGroupDTO> returnVal;

        returnVal = dtoMapMarkerGroup.getList();
        for(MarkerGroupDTO currentMarkerGroupDTO : returnVal) {
            currentMarkerGroupDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
            currentMarkerGroupDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
        }

        if(null == returnVal) {
            returnVal = new ArrayList<>();
        }

        return returnVal;
    }

    @Override
    public MarkerGroupDTO getMarkerGroupById(Integer markerGroupId) throws GobiiDomainException {

        MarkerGroupDTO returnVal;

        returnVal = dtoMapMarkerGroup.get(markerGroupId);
        if (null == returnVal) {
            throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "The specified markerGroupId ("
                            + markerGroupId
                            + ") does not match an existing marker group ");
        }
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        return returnVal;
    }

}
