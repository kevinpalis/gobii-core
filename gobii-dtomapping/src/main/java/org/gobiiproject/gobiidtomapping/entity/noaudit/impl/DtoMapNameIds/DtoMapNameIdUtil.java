package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.types.GobiiFilterType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

/**
 * Created by VCalaminos on 9/20/2018.
 */
public class DtoMapNameIdUtil {


    public static Integer getIdsFromResultSet(List<NameIdDTO> nameIdDTOList, ResultSet resultSet, String columnName, String columnId) throws SQLException {

        return DtoMapNameIdUtil.getIdsFromResultSet(nameIdDTOList, resultSet, columnName, columnId, GobiiFilterType.NAMES_BY_NAME_LIST);
    }


    public static Integer getIdsFromResultSet(List<NameIdDTO> nameIdDTOList, ResultSet resultSet, String columnName, String columnId, GobiiFilterType gobiiFilterType) throws SQLException {

        /** Implemented binary search for a more efficient way of looking for a name in the list **/

        Collections.sort(nameIdDTOList);
        Integer index;

        List<NameIdDTO> nameList = new ArrayList<>();

        while (resultSet.next()) {

            NameIdDTO searchNameDTO = new NameIdDTO();
            searchNameDTO.setName(resultSet.getString(columnName));

            NameIdDTOComparator nameIdDTOComparator = new NameIdDTOComparator();
            index = Collections.binarySearch(nameIdDTOList, searchNameDTO, nameIdDTOComparator);

            if (index > -1) {

                nameIdDTOList.get(index).setId(resultSet.getInt(columnId));

                if (gobiiFilterType == GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_ABSENT) {
                    nameIdDTOList.remove(nameIdDTOList.get(index));

                } else if (gobiiFilterType == GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS) {
                    nameList.add(nameIdDTOList.get(index));
                }

            }

        }

        if (gobiiFilterType == GobiiFilterType.NAMES_BY_NAME_LIST_RETURN_EXISTS) {

            nameIdDTOList.removeAll(nameIdDTOList);
            nameIdDTOList.addAll(nameList);
        }

        return resultSet.getFetchSize();

    }

    public static void checkCallLimit(Integer callLimit, String gobiiEntityNameType) {

        if (callLimit == null) {

            throw new GobiiDtoMappingException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.RESOURCE_LIMIT,
                    "The max GET limit for names/"
                    + gobiiEntityNameType
                    + " should not be null");
        }

        if (callLimit <= 0) {

            throw new GobiiDtoMappingException(GobiiStatusLevel.VALIDATION,
                    GobiiValidationStatusType.RESOURCE_LIMIT,
                    "The max GET limit for names/"
                    + gobiiEntityNameType
                    + " should have a value");

        }

    }
}
