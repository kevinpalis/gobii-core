package org.gobiiproject.gobiidomain.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.DataSetService;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapAnalysis;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMapDataSet;
import org.gobiiproject.gobiidtomapping.entity.noaudit.DtoMapJob;
import org.gobiiproject.gobiimodel.dto.auditable.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.DataSetDTO;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedList;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Phil on 4/21/2016.
 */
public class DataSetServiceImpl implements DataSetService {

    Logger LOGGER = LoggerFactory.getLogger(DataSetServiceImpl.class);


    @Autowired
    DtoMapDataSet dtoMapDataSet = null;

    @Autowired
    DtoMapAnalysis dtoMapAnalysis = null;

    @Autowired
    DtoMapJob dtoMapJob = null;

    @Override
    public List<DataSetDTO> getDataSets() throws GobiiDomainException {

        List<DataSetDTO> returnVal;

        try {
            returnVal = dtoMapDataSet.getList();

            for (DataSetDTO currentDataSetDTO : returnVal) {
                currentDataSetDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
                currentDataSetDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
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
    public PagedList<DataSetDTO> getDatasetsPaged(Integer pageSize, Integer pageNo, String pgQueryId) throws GobiiDtoMappingException {

        PagedList<DataSetDTO> returnVal;

        try {
            returnVal = dtoMapDataSet.getListPaged(pageSize,pageNo,pgQueryId);

            for (DataSetDTO currentDataSetDTO : returnVal.getDtoList()) {
                currentDataSetDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
                currentDataSetDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);
            }


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }

    @Override
    public List<DataSetDTO> getDataSetsByTypeId(Integer typeId) throws GobiiDomainException {

        List<DataSetDTO> returnVal;

        try {

            returnVal = dtoMapDataSet.getByTypeId(typeId);

            for (DataSetDTO currentDataSetDTO : returnVal) {

                currentDataSetDTO.getAllowedProcessTypes().add(GobiiProcessType.READ);
                currentDataSetDTO.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

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
    public DataSetDTO getDataSetById(Integer dataSetId) throws GobiiDomainException {

        DataSetDTO returnVal;

        try {
            returnVal = dtoMapDataSet.get(dataSetId);
            if (null == returnVal) {
                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "The specified dataSetId ("
                                + dataSetId
                                + ") does not match an existing dataSet ");
            }

            returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
            returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

        return returnVal;
    }

    @Override
    public DataSetDTO createDataSet(DataSetDTO dataSetDTO) throws GobiiDomainException {

        DataSetDTO returnVal;

        returnVal = dtoMapDataSet.create(dataSetDTO);

        // When we have roles and permissions, this will be set programmatically
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        return returnVal;
    }

    @Override
    public DataSetDTO replaceDataSet(Integer dataSetId, DataSetDTO dataSetDTO) throws GobiiDomainException {
        DataSetDTO returnVal;

        try {

            if (null == dataSetDTO.getDataSetId() ||
                    dataSetDTO.getDataSetId().equals(dataSetId)) {


                DataSetDTO existingDataSetDTO = dtoMapDataSet.get(dataSetId);

                if (null != existingDataSetDTO.getDataSetId() && existingDataSetDTO.getDataSetId().equals(dataSetId)) {


                    returnVal = dtoMapDataSet.replace(dataSetId, dataSetDTO);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
                    returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

                } else {

                    throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                            GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                            "The specified dataSetId ("
                                    + dataSetId
                                    + ") does not match an existing dataSet ");
                }

            } else {

                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.BAD_REQUEST,
                        "The dataSetId specified in the dto ("
                                + dataSetDTO.getDataSetId()
                                + ") does not match the dataSetId passed as a parameter "
                                + "("
                                + dataSetId
                                + ")");

            }


        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }


        return returnVal;
    }

    @Override
    public JobDTO getJobDetailsByDatasetId(Integer datasetId) throws GobiiDomainException {

        JobDTO returnVal;

        returnVal = dtoMapJob.getJobDetailsByDatasetId(datasetId);

        returnVal.getAllowedProcessTypes().add(GobiiProcessType.READ);
        returnVal.getAllowedProcessTypes().add(GobiiProcessType.UPDATE);

        return returnVal;

    }

    @Override
    public List<AnalysisDTO> getAnalysesByDatasetId(Integer datasetId) throws GobiiDomainException {

        List<AnalysisDTO> returnVal = new ArrayList<>();

        DataSetDTO dataSetDTO = dtoMapDataSet.get(datasetId);

        for (Integer currentAnalysisId : dataSetDTO.getAnalysesIds()) {
            AnalysisDTO analysisDTO = dtoMapAnalysis.get(currentAnalysisId);
            returnVal.add(analysisDTO);
        }


        return returnVal;
    }

}
