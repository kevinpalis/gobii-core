package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidao.resultset.access.RsAnalysisDao;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIdFetch;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiFilterType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Phil on 10/16/2016.
 */
public class DtoMapNameIdFetchAnalyses implements DtoMapNameIdFetch {

    @Autowired
    private RsAnalysisDao rsAnalysisDao = null;
    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdFetchAnalyses.class);


    @Override
    public GobiiEntityNameType getEntityTypeName() throws GobiiException {
        return GobiiEntityNameType.ANALYSIS;
    }


    private List<NameIdDTO> getAllNameIdsForAnalysis(Integer callLimit) throws GobiiException {

        List<NameIdDTO> returnVal = new ArrayList<>();

        ResultSet resultSet = rsAnalysisDao.getNameIdsForAnalysisNames(callLimit);
        //List<NameIdDTO> listDTO = new ArrayList<>();

        try {

            while (resultSet.next()) {
                NameIdDTO nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("analysis_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                returnVal.add(nameIdDTO);
            }

        } catch (SQLException e) {
            LOGGER.error("error retrieving analysis names", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    private List<NameIdDTO> getNameIdListForAnalysisNameByTypeId(Integer typeId) {

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsAnalysisDao.getAnalysisNamesByTypeId(typeId);

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("analysis_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                returnVal.add(nameIdDTO);
            }


        } catch (SQLException e) {
            LOGGER.error("error retrieving analysis names", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }


    @Override
    public List<NameIdDTO> getNameIds(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException {

        List<NameIdDTO> returnVal;

        if (GobiiFilterType.NONE == dtoMapNameIdParams.getGobiiFilterType()) {

            // check call limit
            Integer callLimit = dtoMapNameIdParams.getCallLimit();

            DtoMapNameIdUtil.checkCallLimit(callLimit, GobiiEntityNameType.ANALYSIS.toString());

            returnVal = this.getAllNameIdsForAnalysis(callLimit);
        } else {

            if (GobiiFilterType.NAMES_BY_TYPEID == dtoMapNameIdParams.getGobiiFilterType()) {

                returnVal = this.getNameIdListForAnalysisNameByTypeId(dtoMapNameIdParams.getFilterValueAsInteger());

            } else {
                throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.NONE,
                        "Unsupported filter type for "
                                + this.getEntityTypeName().toString().toLowerCase()
                                + ": " + dtoMapNameIdParams.getGobiiFilterType());
            }
        }

        return returnVal;
    }
}
