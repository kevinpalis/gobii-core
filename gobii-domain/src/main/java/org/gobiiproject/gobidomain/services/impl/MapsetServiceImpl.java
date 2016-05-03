package org.gobiiproject.gobidomain.services.impl;

import org.gobiiproject.gobidomain.services.MapsetService;
import org.gobiiproject.gobiidtomapping.DtoMapMapset;
import org.gobiiproject.gobiidtomapping.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.container.MapsetDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Phil on 4/28/2016.
 */
public class MapsetServiceImpl implements MapsetService {

    Logger LOGGER = LoggerFactory.getLogger(MapsetServiceImpl.class);


    @Autowired
    DtoMapMapset dtoMapMapset;

    @Override
    public MapsetDTO processMapset(MapsetDTO mapsetDTO) {

        MapsetDTO returnVal = mapsetDTO;

        try {

            switch (returnVal.getProcessType()) {
                case READ:
                    returnVal = dtoMapMapset.getMapsetDetails(returnVal);
                    break;

                default:
                    throw new GobiiDtoMappingException("Unsupported procesCv type " + returnVal.getProcessType().toString());

            } // switch()

        } catch (GobiiDtoMappingException e) {

            returnVal.getDtoHeaderResponse().addException(e);
            LOGGER.error(e.getMessage());
        }

        return returnVal;
    }
}