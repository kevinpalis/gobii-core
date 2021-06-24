package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidao.resultset.access.RsProjectDao;
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
public class DtoMapNameIdFetchProjects implements DtoMapNameIdFetch {

    @Autowired
    private RsProjectDao rsProjectDao = null;


    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdFetchProjects.class);


    @Override
    public GobiiEntityNameType getEntityTypeName() throws GobiiException {
        return GobiiEntityNameType.PROJECT;
    }


    private NameIdDTO makeNameIdDto( ResultSet resultSet) throws  SQLException {

        NameIdDTO returnVal = new NameIdDTO();

        returnVal.setGobiiEntityNameType(GobiiEntityNameType.PROJECT);
        returnVal.setId(resultSet.getInt("project_id"));
        returnVal.setName(resultSet.getString("name"));

        return  returnVal;

    }

    private List<NameIdDTO> getNameIdListForProjectNameByContactId(Integer contactId) throws GobiiException {

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {
                ResultSet resultSet = rsProjectDao.getProjectNamesForContactId(contactId);

                while (resultSet.next()) {
                    returnVal.add(makeNameIdDto(resultSet));
                }

        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;

    } // getNameIdListForContactsByRoleName()

    private List<NameIdDTO> getNameIdListForProjects() {

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsProjectDao.getProjectNames();

            while (resultSet.next()) {

                NameIdDTO nameIdDTO = makeNameIdDto(resultSet);
                nameIdDTO.setGobiiFkEntityNameType(GobiiEntityNameType.CONTACT);
                nameIdDTO.setFkId(resultSet.getInt("contact_id"));

                returnVal.add(nameIdDTO);
            }

        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    @Override
    public List<NameIdDTO> getNameIds(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException {

        List<NameIdDTO> returnVal;

        if (GobiiFilterType.NONE == dtoMapNameIdParams.getGobiiFilterType()) {
            returnVal = this.getNameIdListForProjects();
        } else {

            if (GobiiFilterType.NAMES_BY_TYPEID == dtoMapNameIdParams.getGobiiFilterType()) {

                returnVal = this.getNameIdListForProjectNameByContactId(dtoMapNameIdParams.getFilterValueAsInteger());

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
