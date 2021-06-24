package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
 * Created by VCalaminos on 9/18/2018.
 */
@SuppressWarnings("serial")
public class DtoMapNameIdFetchDnaSampleNames implements DtoMapNameIdFetch {

    @Autowired
    private DtoListQueryColl dtoListQueryColl;

    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdFetchDnaSampleNames.class);

    @Override
    public GobiiEntityNameType getEntityTypeName() { return GobiiEntityNameType.DNASAMPLE; }

    private Integer getDnaSamplesFromResultSet(List<NameIdDTO> nameIdDTOList, ResultSet resultSet, String columnName, String columnId, GobiiFilterType gobiiFilterType) throws SQLException {

        Collections.sort(nameIdDTOList);
        //Integer index;
        Integer nameId;

        List<NameIdDTO> nameList = new ArrayList<>();

        while (resultSet.next()) {

            NameIdDTO searchNameDTO = new NameIdDTO();
            searchNameDTO.setName(resultSet.getString(columnName));

            searchNameDTO.getParameters().put("dnaSampleNum", resultSet.getString("num"));

            for (int i = 0; i < nameIdDTOList.size(); i ++) {
                // i is the index
                // yourArrayList.get(i) is the element

                NameIdDTO currentNameIdDTO = nameIdDTOList.get(i);

                String currentName = currentNameIdDTO.getName();
                String searchName = searchNameDTO.getName();

                if (currentNameIdDTO.getParameters().containsKey("dnaSampleNum")) {
                    String currentNum = currentNameIdDTO.getParameters().get("dnaSampleNum").toString();
                    String searchNum = searchNameDTO.getParameters().get("dnaSampleNum").toString();

                    if (currentName.equals(searchName) && currentNum.equals(searchNum)) {

                        nameId = resultSet.getInt(columnId);

                        nameIdDTOList.get(i).setId(nameId);

                        if (gobiiFilterType == GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_ABSENT) {
                            nameIdDTOList.remove(nameIdDTOList.get(i));

                        } else if (gobiiFilterType == GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS) {
                            nameList.add(nameIdDTOList.get(i));
                        }
                    }
                } else {
                    if (currentName.equals(searchName)) {

                        nameId = resultSet.getInt(columnId);

                        nameIdDTOList.get(i).setId(nameId);

                        if (gobiiFilterType == GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_ABSENT) {
                            nameIdDTOList.remove(nameIdDTOList.get(i));

                        } else if (gobiiFilterType == GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS) {
                            nameList.add(nameIdDTOList.get(i));
                        }
                    }
                }
            }
        }

        if (gobiiFilterType == GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS) {

            nameIdDTOList.removeAll(nameIdDTOList);
            nameIdDTOList.addAll(nameList);
        }

        return resultSet.getFetchSize();

    }

    private List<NameIdDTO> getDnaSampleNamesByNameList(List<NameIdDTO> nameIdDTOList, String projectId, GobiiFilterType gobiiFilterType) {

        try {

            ResultSet resultSet = dtoListQueryColl.getResultSet(ListSqlId.QUERY_ID_DNASAMPLE_NAMES_BYLIST,
                    new HashMap<String, Object>() {{
                        put("projectId", projectId);
                    }}, new HashMap<String, Object>() {{
                        put("nameArray", nameIdDTOList);
                    }});

            //Integer resultSize = 
            getDnaSamplesFromResultSet(nameIdDTOList, resultSet, "name", "dnasample_id", gobiiFilterType);

        } catch (Exception e) {
            throw new GobiiDaoException(e);
        }

        return nameIdDTOList;
    }

    private List<NameIdDTO> getAllNameIdsForDnaSampleNames(Integer callLimit) throws GobiiException {

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = dtoListQueryColl.getResultSet(ListSqlId.QUERY_ID_DNASAMPLE_NAMES_ALL,
                    new HashMap<String, Object>() {{
                        put("callLimit", callLimit);
                    }}, new HashMap<String, Object>() {{
                    }});

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {

                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("dnasample_id"));
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

            DtoMapNameIdUtil.checkCallLimit(callLimit, GobiiEntityNameType.DNASAMPLE.toString());

            returnVal = this.getAllNameIdsForDnaSampleNames(callLimit);

        } else if (GobiiFilterType.NAMES_BY_NAME_LIST == gobiiFilterType || GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_ABSENT == gobiiFilterType || GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS == gobiiFilterType) {

            returnVal = this.getDnaSampleNamesByNameList(dtoMapNameIdParams.getNameIdDTOList(), dtoMapNameIdParams.getFilterValueAsString(), gobiiFilterType);

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
