package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsPlatformDao;
import org.gobiiproject.gobiidao.resultset.core.listquery.DtoListQueryColl;
import org.gobiiproject.gobiidao.resultset.core.listquery.ListSqlId;
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
@SuppressWarnings("serial")
public class DtoMapNameIdFetchPlatforms implements DtoMapNameIdFetch {

    @Autowired
    private RsPlatformDao rsPlatformDao = null;

    @Autowired
    private DtoListQueryColl dtoListQueryColl;

    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdFetchPlatforms.class);


    @Override
    public GobiiEntityNameType getEntityTypeName() throws GobiiException {
        return GobiiEntityNameType.PLATFORM;
    }


    private NameIdDTO makeNameIdDtoForPlatform(ResultSet resultSet) throws SQLException {

        NameIdDTO returnVal = new NameIdDTO();

        returnVal.setId(resultSet.getInt("platform_id"));
        returnVal.setName(resultSet.getString("name"));

        return returnVal;
    }

    private List<NameIdDTO> getPlatformNames() throws GobiiException {

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsPlatformDao.getPlatformNames();
            //List<NameIdDTO> listDTO = new ArrayList<>();

            while (resultSet.next()) {
                returnVal.add(this.makeNameIdDtoForPlatform(resultSet));
            }



        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    private List<NameIdDTO> getPlatformNamesByTypeId(Integer platformTypeId) throws GobiiException{

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsPlatformDao.getPlatformNamesByTypeId(platformTypeId);


            while (resultSet.next()) {
                returnVal.add(this.makeNameIdDtoForPlatform(resultSet));
            }



        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    private List<NameIdDTO> getPlatformNamesByNameList(List<NameIdDTO> nameIdDTOList) {

        try {

            ResultSet resultSet = dtoListQueryColl.getResultSet(ListSqlId.QUERY_ID_PLATFORM_NAMES_BYLIST,
                    new HashMap<String, Object>(){{
                    }}, new HashMap<String, Object>(){{
                        put("nameArray", nameIdDTOList);
                    }});

            //Integer resultSize = 
            DtoMapNameIdUtil.getIdsFromResultSet(nameIdDTOList, resultSet, "name", "platform_id");

        } catch (Exception e) {
            throw new GobiiDaoException(e);
        }

        return nameIdDTOList;
    }

    @Override
    public List<NameIdDTO> getNameIds(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException {

        List<NameIdDTO> returnVal;

        if (GobiiFilterType.NONE == dtoMapNameIdParams.getGobiiFilterType()) {
            returnVal = this.getPlatformNames();
        } else {

            if (GobiiFilterType.NAMES_BY_TYPEID == dtoMapNameIdParams.getGobiiFilterType()) {

                returnVal = this.getPlatformNamesByTypeId(dtoMapNameIdParams.getFilterValueAsInteger());

            } else if (GobiiFilterType.NAMES_BY_NAME_LIST == dtoMapNameIdParams.getGobiiFilterType()) {

                returnVal = this.getPlatformNamesByNameList(dtoMapNameIdParams.getNameIdDTOList());
            }

            else {

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
