package org.gobiiproject.gobiidomain.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.ExperimentService;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapExperiment;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.auditable.ExperimentDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Angel on 4/19/2016.
 */
public class ExperimentServiceImpl implements ExperimentService<ExperimentDTO> {


    Logger LOGGER = LoggerFactory.getLogger(ExperimentServiceImpl.class);

    @Autowired
    private DtoMapExperiment dtoMapExperiment = null;


    @Override
    public List<ExperimentDTO> getExperiments() throws GobiiDomainException {

        List<ExperimentDTO> returnVal;

        returnVal = dtoMapExperiment.getList();

        for (ExperimentDTO currentExperimentDTO : returnVal) {
            currentExperimentDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
            currentExperimentDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
        }


        if (null == returnVal) {
            returnVal = new ArrayList<>();
        }


        return returnVal;
    }

    @Override
    public ExperimentDTO getExperimentById(Integer experimentId) throws GobiiDomainException {

        ExperimentDTO returnVal;

        returnVal = dtoMapExperiment.get(experimentId);
        if (null == returnVal) {
            throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                    "The specified experimentId ("
                            + experimentId
                            + ") does not match an existing experiment ");
        }

        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);


        

        return returnVal;
    }

    @Override
    public ExperimentDTO createExperiment(ExperimentDTO experimentDTO) throws GobiiDomainException {
        ExperimentDTO returnVal;

        returnVal = dtoMapExperiment.create(experimentDTO);

        // When we have roles and permissions, this will be set programmatically
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        return returnVal;
    }

    @Override
    public ExperimentDTO replaceExperiment(Integer experimentId, ExperimentDTO experimentDTO) throws GobiiDomainException {
        ExperimentDTO returnVal;


        if (null == experimentDTO.getExperimentId() ||
                experimentDTO.getExperimentId().equals(experimentId)) {


            ExperimentDTO existingExperimentDTO = dtoMapExperiment.get(experimentId);

            if (null != existingExperimentDTO.getExperimentId() && existingExperimentDTO.getExperimentId().equals(experimentId)) {


                returnVal = dtoMapExperiment.replace(experimentId, experimentDTO);
                returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
                returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

            } else {

                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "The specified experimentId ("
                                + experimentId
                                + ") does not match an existing experiment ");
            }

        } else {

            throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "The experimentId specified in the dto ("
                            + experimentDTO.getExperimentId()
                            + ") does not match the experimentId passed as a parameter "
                            + "("
                            + experimentId
                            + ")");

        }


        return returnVal;
    }

    @Override
    public List<ExperimentDTO> getExperimentsByProjectIdForLoadedDatasets(Integer projectId) throws GobiiDomainException {


        List<ExperimentDTO> returnVal = null;

        try {

            returnVal = dtoMapExperiment.getExperimentsByProjectIdForLoadedDatasets(projectId);

            for (ExperimentDTO currentExperimentDTO : returnVal) {

                currentExperimentDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
                currentExperimentDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

            }

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
    public boolean updateExperimentDataFile(Integer experimentId, String dataFilePath) throws GobiiException {

        return false;
    }


} // ExperimentServiceImpl
