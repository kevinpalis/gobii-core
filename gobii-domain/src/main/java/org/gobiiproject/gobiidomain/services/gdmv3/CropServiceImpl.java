package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.CropsDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CropServiceImpl implements CropService {

    Logger LOGGER = LoggerFactory.getLogger(CropServiceImpl.class);

    private ConfigSettings configSettings = new ConfigSettings();

    /**
     * Get all the crops defined in the system.
     * No paging done as there are only very few crops available.
     * @return Paged result of active crop names.
     * @throws GobiiException
     */
    @Override
    public PagedResult<CropsDTO> getCrops() throws GobiiException {

        List<CropsDTO> crops = new ArrayList<>();

        try {

            List<String> userGroups = KeycloakService.getUserGroups();

            //Get all the crop that user authorized ot access
            Set<String> userAuthorizedCrops =  userGroups.stream().map((group) -> {
                String[] cropsPaths =  URLUtils.stripStartAndEndPathSeparator(group).split("/");
                if(cropsPaths.length > 0) {
                    return cropsPaths[0];
                }
                return null;
            }).collect(Collectors.toSet());

            //remove nulls from userAuthorized crop to avoid nulls getting access
            userAuthorizedCrops.remove(null);

            // get only active crops
            configSettings.getActiveCropConfigs().forEach(cropConfig -> {
                CropsDTO crop = new CropsDTO();
                crop.setCropType(cropConfig.getGobiiCropType());
                if(userAuthorizedCrops.contains(cropConfig.getGobiiCropType())) {
                    crop.setUserAuthorized(true);
                }
                else {
                    crop.setUserAuthorized(false);
                }
                crops.add(crop);
            });
        }
        catch (Exception e) {
            LOGGER.error("Unable to read crop data", e);
            throw new GobiiDomainException(e);
        }

        return PagedResult.createFrom(0, crops);

    }
}
