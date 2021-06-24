package org.gobiiproject.gobiidomain.services;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.system.ConfigSettingsDTO;

/**
 * Created by Phil on 4/12/2016.
 */
public interface ConfigSettingsService {
    ConfigSettingsDTO getConfigSettings() throws GobiiException;
}
