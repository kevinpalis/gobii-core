package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
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
 * Created by VCalaminos on 9/24/2018.
 */
@SuppressWarnings("serial")
public class DtoMapNameIdFetchLinkageGroup implements DtoMapNameIdFetch {

    @Autowired
    private DtoListQueryColl dtoListQueryColl;

    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdFetchLinkageGroup.class);

    @Override
    public GobiiEntityNameType getEntityTypeName() { return GobiiEntityNameType.LINKAGE_GROUP; }

    private List<NameIdDTO> getLinkageGroupNamesByNameList(List<NameIdDTO> nameIdDTOList, String mapsetId) {

        try {

            ResultSet resultSet = dtoListQueryColl.getResultSet(ListSqlId.QUERY_ID_LINKAGE_GROUP_NAMES_BYLIST,
                    new HashMap<String, Object>() {{
                        put("mapsetId", mapsetId);
                    }}, new HashMap<String, Object>() {{
                        put("nameArray", nameIdDTOList);
                    }});

            //Integer resultSize = 
            DtoMapNameIdUtil.getIdsFromResultSet(nameIdDTOList, resultSet, "name", "linkage_group_id");

        } catch (Exception e) {
            throw new GobiiDaoException(e);
        }

        return nameIdDTOList;
    }

    @Override
    public List<NameIdDTO> getNameIds(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException {

        List<NameIdDTO> returnVal;

        GobiiFilterType gobiiFilterType = dtoMapNameIdParams.getGobiiFilterType();

        if (GobiiFilterType.NAMES_BY_NAME_LIST == gobiiFilterType) {

            returnVal = this.getLinkageGroupNamesByNameList(dtoMapNameIdParams.getNameIdDTOList(), dtoMapNameIdParams.getFilterValueAsString());

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
