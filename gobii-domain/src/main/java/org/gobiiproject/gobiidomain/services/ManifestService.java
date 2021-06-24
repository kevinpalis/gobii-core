package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.auditable.ManifestDTO;

/**
 * Created by Angel on 5/4/2016.
 */
public interface ManifestService {

    List<ManifestDTO> getManifests() throws GobiiDomainException;
    ManifestDTO createManifest(ManifestDTO manifestDTO) throws GobiiDomainException;
    ManifestDTO replaceManifest(Integer manifestId, ManifestDTO manifestDTO) throws GobiiDomainException;
    ManifestDTO getManifestById(Integer manifestId) throws GobiiDomainException;

}
