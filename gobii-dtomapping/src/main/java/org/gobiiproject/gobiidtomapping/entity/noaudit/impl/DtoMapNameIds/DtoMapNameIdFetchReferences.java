package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsReferenceDao;
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
public class DtoMapNameIdFetchReferences implements DtoMapNameIdFetch {

    @Autowired
    private RsReferenceDao rsReferenceDao = null;

    @Autowired
    private DtoListQueryColl dtoListQueryColl;

    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdFetchReferences.class);


    @Override
    public GobiiEntityNameType getEntityTypeName() throws GobiiException {
        return GobiiEntityNameType.REFERENCE;
    }

    private List<NameIdDTO> getReferenceNames() {

        List<NameIdDTO>  returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsReferenceDao.getReferenceNames();
            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("reference_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                returnVal.add(nameIdDTO);
            }


        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }

    private List<NameIdDTO> getReferencesByNameList(List<NameIdDTO> nameIdDTOList) {

        try {

            ResultSet resultSet = dtoListQueryColl.getResultSet(ListSqlId.QUERY_ID_REFERENCE_BY_LIST,
                    new HashMap<String, Object>(){{
                    }}, new HashMap<String, Object>(){{
                        put("nameArray", nameIdDTOList);
                    }});


            //Integer resultSize = 
            DtoMapNameIdUtil.getIdsFromResultSet(nameIdDTOList, resultSet, "name", "reference_id");

        } catch (Exception e) {
            throw new GobiiDaoException(e);
        }

        return nameIdDTOList;
    }


    @Override
    public List<NameIdDTO> getNameIds(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException {

        List<NameIdDTO> returnVal;

        if (GobiiFilterType.NONE == dtoMapNameIdParams.getGobiiFilterType()) {
            returnVal = this.getReferenceNames();
        } else {

            if (GobiiFilterType.NAMES_BY_NAME_LIST == dtoMapNameIdParams.getGobiiFilterType()) {

                returnVal = this.getReferencesByNameList(dtoMapNameIdParams.getNameIdDTOList());

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
