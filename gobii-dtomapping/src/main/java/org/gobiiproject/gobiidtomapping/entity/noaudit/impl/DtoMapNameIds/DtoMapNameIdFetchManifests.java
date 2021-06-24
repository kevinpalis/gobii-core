package org.gobiiproject.gobiidtomapping.entity.noaudit.impl.DtoMapNameIds;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiidao.resultset.access.RsManifestDao;
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
public class DtoMapNameIdFetchManifests implements DtoMapNameIdFetch {

    @Autowired
    private RsManifestDao rsManifestDao = null;


    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdFetchManifests.class);


    @Override
    public GobiiEntityNameType getEntityTypeName() throws GobiiException {
        return GobiiEntityNameType.CVGROUP;
    }

    private List<NameIdDTO> getManifestNames() throws GobiiException {

        List<NameIdDTO>  returnVal = new ArrayList<>();

        try {

            ResultSet resultSet = rsManifestDao.getManifestNames();

            while (resultSet.next()) {
                NameIdDTO nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("manifest_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                returnVal.add(nameIdDTO);
            }

        } catch (Exception e) {
            LOGGER.error("Gobii Maping Error", e);
            throw new GobiiDtoMappingException(e);
        }

        return returnVal;
    }//getManifestNames


    @Override
    public List<NameIdDTO> getNameIds(DtoMapNameIdParams dtoMapNameIdParams) throws GobiiException {

        List<NameIdDTO> returnVal;

        if (GobiiFilterType.NONE == dtoMapNameIdParams.getGobiiFilterType()) {
            returnVal = this.getManifestNames();
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
