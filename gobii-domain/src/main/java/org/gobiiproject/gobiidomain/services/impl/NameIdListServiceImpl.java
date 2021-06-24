package org.gobiiproject.gobiidomain.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.NameIdListService;
import org.gobiiproject.gobiidtomapping.entity.noaudit.DtoMapNameIdList;
import org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds.DtoMapNameIdParams;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiFilterType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Phil on 4/6/2016.
 */
public class NameIdListServiceImpl implements NameIdListService {


    //private Logger LOGGER = LoggerFactory.getLogger(NameIdListServiceImpl.class);

    @Autowired
    DtoMapNameIdList dtoMapNameIdList;

    @Override
    public List<NameIdDTO> getNameIdList(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException {

        /** NOTE
         * Added this validation to prevent the issue for GP1-1882.
         * If validation is REMOVED, the service that gets the ID's for given name list should be fixed so that it will work even if the list contains duplicate names
         * **/

        if (dtoMapNameIdParams.getGobiiFilterType().equals(GobiiFilterType.NAMES_BY_NAME_LIST) && !dtoMapNameIdParams.getEntityType().equals(GobiiEntityNameType.DNASAMPLE)) {

            Set<String> duplicateNames = getDuplicateNames(dtoMapNameIdParams.getNameIdDTOList());

            if (duplicateNames.size() != 0) {

                throw new GobiiDomainException(GobiiStatusLevel.VALIDATION,
                        GobiiValidationStatusType.VALIDATION_NOT_UNIQUE,
                        "There were duplicate values in the list: " +
                                duplicateNames);
            }

        }

        List<NameIdDTO> returnVal = dtoMapNameIdList.getNameIdList(dtoMapNameIdParams);
        returnVal.forEach(nameIdDTO -> nameIdDTO.getAllowedProcessTypes().add(GobiiProcessType.READ));



        return returnVal;
    }

    private Set<String> getDuplicateNames(List<NameIdDTO> nameIdDTOListInput) {

        Set<String> returnSet = new HashSet<>();
        Set<String> namesSet = new HashSet<>();

        for (NameIdDTO nameIdDTO : nameIdDTOListInput) {

            if (!namesSet.add(nameIdDTO.getName())) {

                returnSet.add(nameIdDTO.getName());
            }
        }

        return returnSet;

    }
}
