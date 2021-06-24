package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.access.RsCvDao;
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
@SuppressWarnings("all")
public class DtoMapNameIdFetchCvTerms implements DtoMapNameIdFetch {

    @Autowired
    private RsCvDao rsCvDao = null;

    @Autowired
    private DtoListQueryColl dtoListQueryColl;

    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdFetchCvTerms.class);


    @Override
    public GobiiEntityNameType getEntityTypeName() {
        return GobiiEntityNameType.CV;
    }

    private NameIdDTO makeCvNameId(ResultSet resultSet) throws SQLException   {
        NameIdDTO returnVal = new NameIdDTO();
        returnVal.setId(resultSet.getInt("cv_id"));
        returnVal.setName(resultSet.getString("term").toString());
        return returnVal;

    }

    private List<NameIdDTO> getCvTermsForGroup(String cvGroupName) throws GobiiException {

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsCvDao.getCvTermsByGroup(cvGroupName);

            while (resultSet.next()) {
                returnVal.add(makeCvNameId(resultSet));
            }

        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;

    } // getCvTermsForGroup()

    private List<NameIdDTO> getCvTerms() throws GobiiException {

        List<NameIdDTO> returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsCvDao.getCvNames();


            while (resultSet.next()) {
                returnVal.add(makeCvNameId(resultSet));
            }



        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;

    }

    private List<NameIdDTO> getCvTermsForGroupByNameList(List<NameIdDTO> nameIdDTOList, String cvGroupName) {

        try {

            ResultSet resultSet = dtoListQueryColl.getResultSet(ListSqlId.QUERY_ID_CV_BY_GROUP_AND_LIST,
                    new HashMap<String, Object>() {{
                        put("cvGroupName", cvGroupName);
                    }}, new HashMap<String, Object>(){{
                        put("nameArray", nameIdDTOList);
                    }});


            Integer resultSize = DtoMapNameIdUtil.getIdsFromResultSet(nameIdDTOList, resultSet, "term", "cv_id");

        } catch (Exception e) {
            throw new GobiiDaoException(e);
        }

        return nameIdDTOList;

    }

    @Override
    public List<NameIdDTO> getNameIds(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException {

        List<NameIdDTO> returnVal;

        if (GobiiFilterType.NONE == dtoMapNameIdParams.getGobiiFilterType()) {
            returnVal = this.getCvTerms();
        } else {

            if (GobiiFilterType.NAMES_BY_TYPE_NAME == dtoMapNameIdParams.getGobiiFilterType()) {

                returnVal = this.getCvTermsForGroup(dtoMapNameIdParams.getFilterValueAsString());

            } else if (GobiiFilterType.NAMES_BY_NAME_LIST == dtoMapNameIdParams.getGobiiFilterType()) {

                returnVal = this.getCvTermsForGroupByNameList(dtoMapNameIdParams.getNameIdDTOList(), dtoMapNameIdParams.getFilterValueAsString());

            }else {
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
