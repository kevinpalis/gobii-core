package org.gobiiproject.gobiidomain.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.MapsetService;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapMapset;
import org.gobiiproject.gobiimodel.dto.auditable.MapsetDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Phil on 4/28/2016.
 * Modified by AVB on 9/30/2016.
 * Modified by Yanii on 1/23/2017.
 */
public class MapsetServiceImpl implements MapsetService {

    Logger LOGGER = LoggerFactory.getLogger(MapsetServiceImpl.class);

    @Autowired
    DtoMapMapset dtoMapMapset;

    @Override
    public List<MapsetDTO> getAllMapsetNames() throws GobiiDomainException {

        List<MapsetDTO> returnVal;

        try {
            returnVal = dtoMapMapset.getList();
            if (null == returnVal) {
                returnVal = new ArrayList<>();
            }

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }

    @Override
    public MapsetDTO createMapset(MapsetDTO mapsetDTO) throws GobiiDomainException {

        MapsetDTO returnVal;

        try {

            returnVal = dtoMapMapset.create(mapsetDTO);

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
    public MapsetDTO replaceMapset(Integer mapsetId, MapsetDTO mapsetDTO) throws GobiiDomainException {

        MapsetDTO returnVal;

        try {

            if(null == mapsetDTO.getMapsetId() ||
                    mapsetDTO.getMapsetId().equals(mapsetId)) {

                MapsetDTO existingMapsetDTO = dtoMapMapset.get(mapsetId);
                if(null != existingMapsetDTO.getMapsetId() && existingMapsetDTO.getMapsetId().equals(mapsetId)) {

                    returnVal = dtoMapMapset.replace(mapsetId, mapsetDTO);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

                } else {

                    throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                            GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                            "The specified mapsetId ("
                                + mapsetId
                                + ") does not match an existing mapset.");
                }
            } else {

                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "The mapsetId specified in the dto ("
                                + mapsetDTO.getMapsetId()
                                + ") does not match the mapsetId passed as a parameter "
                                + "("
                                + mapsetId
                                + ")");
            }


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }

        return returnVal;

    }

    @Override
    public List<MapsetDTO> getMapsets() throws GobiiDomainException {

        List<MapsetDTO> returnVal;

        returnVal = dtoMapMapset.getMapsets();
        for(MapsetDTO currentMapsetDTO : returnVal) {
            currentMapsetDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
            currentMapsetDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
        }

        if(null == returnVal) {
            returnVal = new ArrayList<>();
        }

        return returnVal;
    }

    @Override
    public MapsetDTO getMapsetById(Integer mapsetId) throws GobiiDomainException {

        MapsetDTO returnVal;

        returnVal = dtoMapMapset.get(mapsetId);
        if (null == returnVal) {
            throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "The specified mapsetId ("
                            + mapsetId
                            + ") does not match an existing mapset ");
        }
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        return returnVal;
    }
}
