package org.gobiiproject.gobiidomain.services.impl;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.SampleService;
import org.gobiiproject.gobiidtomapping.DtoMapSample;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.headerlesscontainer.DnaSampleDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Phil on 4/21/2016.
 */
public class SampleServiceImpl implements SampleService {

    Logger LOGGER = LoggerFactory.getLogger(SampleServiceImpl.class);


    @Autowired
    DtoMapSample dtoMapSample = null;


    @Override
    public DnaSampleDTO getSampleDetailsByExternalCode(String externalCode) throws GobiiDomainException {

        DnaSampleDTO returnVal;

        try {
            returnVal = dtoMapSample.getSampleDetailsByExternalCode(externalCode);

            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);


            if (returnVal.getGermplasmId() == null || returnVal.getGermplasmId() < 1) {
                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "The specified external code  ("
                                + externalCode
                                + ") does not match an existing germplasm ");
            }

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }


    @Override
    public List<DataSetDTO> getDatasetForLoadedSamplesOfDataType(String externalCode, String datasetType) throws GobiiDomainException {

        List<DataSetDTO> returnVal = null;

        try {

            returnVal = dtoMapSample.getDatasetForLoadedSamplesOfDataType(externalCode, datasetType);

            for (DataSetDTO currentDataSetDTO : returnVal) {

                currentDataSetDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);

            }


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }


        return returnVal;


    }

}
