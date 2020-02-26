package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidao.resultset.access.RsExperimentDao;
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
public class DtoMapNameIdFetchExperiments implements DtoMapNameIdFetch {

    @Autowired
    private RsExperimentDao rsExperimentDao = null;

    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdFetchExperiments.class);


    @Override
    public GobiiEntityNameType getEntityTypeName() throws GobiiException {
        return GobiiEntityNameType.EXPERIMENT;
    }


    private NameIdDTO makeNameIdDtoForExperiment(ResultSet resultSet) throws SQLException {

        NameIdDTO returnVal = new NameIdDTO();

        returnVal.setGobiiEntityNameType(GobiiEntityNameType.EXPERIMENT);
        returnVal.setGobiiFkEntityNameType(GobiiEntityNameType.PROJECT);
        returnVal.setId(resultSet.getInt("experiment_id"));
        returnVal.setFkId(resultSet.getInt("project_id"));
        returnVal.setName(resultSet.getString("name"));

        return returnVal;
    }



    private List<NameIdDTO> getExperimentNames() throws GobiiException {

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsExperimentDao.getExperimentNames();

            while (resultSet.next()) {
                returnVal.add(this.makeNameIdDtoForExperiment(resultSet));
            }
        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    private List<NameIdDTO> getExperimentNamesByProjectId(Integer projectId) throws GobiiException{

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsExperimentDao.getExperimentNamesByProjectId(projectId);

            List<NameIdDTO> listDTO = new ArrayList<>();

            while (resultSet.next()) {
                returnVal.add(this.makeNameIdDtoForExperiment(resultSet));
            }


        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            new GobiiDtoMappingException(e);
        }

        return returnVal;

    } // getNameIdListForContactsByRoleName()


    @Override
    public List<NameIdDTO> getNameIds(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException {

        List<NameIdDTO> returnVal;

        if (GobiiFilterType.NONE == dtoMapNameIdParams.getGobiiFilterType()) {
            returnVal = this.getExperimentNames();
        } else {

            if (GobiiFilterType.NAMES_BY_TYPEID == dtoMapNameIdParams.getGobiiFilterType()) {

                returnVal = this.getExperimentNamesByProjectId(dtoMapNameIdParams.getFilterValueAsInteger());

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
