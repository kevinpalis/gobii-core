package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.gobiiproject.gobiidao.GobiiDaoException;
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
 * Created by VCalaminos on 9/19/2018.
 */
@SuppressWarnings("serial")
public class DtoMapNameIdFetchMarkerNames implements DtoMapNameIdFetch {

    @Autowired
    private DtoListQueryColl dtoListQueryColl;

    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdFetchMarkerNames.class);

    @Override
    public GobiiEntityNameType getEntityTypeName() { return GobiiEntityNameType.MARKER; }


    private List<NameIdDTO> getMarkerNamesByNameList(List<NameIdDTO> nameIdDTOList, String platformId, GobiiFilterType gobiiFilterType) {

        try {

            ResultSet resultSet = dtoListQueryColl.getResultSet(ListSqlId.QUERY_ID_MARKER_NAMES_BYLIST,
                    new HashMap<String, Object>() {{
                        put("platformId", platformId);
                    }}, new HashMap<String, Object>() {{
                        put("nameArray", nameIdDTOList);
                    }});

            //Integer resultSize = 
            DtoMapNameIdUtil.getIdsFromResultSet(nameIdDTOList, resultSet, "name", "marker_id", gobiiFilterType);

        } catch (Exception e) {
            throw new GobiiDaoException(e);
        }

        return nameIdDTOList;

    }

    private List<NameIdDTO> getAllNameIdsForMarkerNames(Integer callLimit) throws GobiiException {

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = dtoListQueryColl.getResultSet(ListSqlId.QUERY_ID_MARKER_NAMES_ALL,
                    new HashMap<String, Object>() {{
                        put("callLimit", callLimit);
                    }}, new HashMap<String, Object>(){{}});

            NameIdDTO nameIdDTO;
            while(resultSet.next()) {

                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("marker_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                returnVal.add(nameIdDTO);
            }

        } catch (Exception e) {
            throw new GobiiDaoException(e);
        }

        return returnVal;
    }

    @Override
    public List<NameIdDTO> getNameIds(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException {

        List<NameIdDTO> returnVal;

        GobiiFilterType gobiiFilterType = dtoMapNameIdParams.getGobiiFilterType();

        if (GobiiFilterType.NONE == gobiiFilterType) {

            // check call limit

            Integer callLimit = dtoMapNameIdParams.getCallLimit();

            DtoMapNameIdUtil.checkCallLimit(callLimit, GobiiEntityNameType.MARKER.toString());

            returnVal = this.getAllNameIdsForMarkerNames(dtoMapNameIdParams.getCallLimit());

        } else if (GobiiFilterType.NAMES_BY_NAME_LIST == gobiiFilterType || GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_ABSENT == gobiiFilterType || GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS == gobiiFilterType) {

            returnVal = this.getMarkerNamesByNameList(dtoMapNameIdParams.getNameIdDTOList(), dtoMapNameIdParams.getFilterValueAsString(), gobiiFilterType);

        } else {

            throw new GobiiDtoMappingException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.NONE,
                    "Unsupported filter type for "
                            + this.getEntityTypeName().toString().toLowerCase()
                            + ": " + dtoMapNameIdParams.getGobiiFilterType());
        }

        return returnVal;
    }

}
