package org.gobiiproject.gobiidtomapping.entity.auditable.impl.sampletracking;

import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.noaudit.ProjectSamplesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by VCalaminos on 5/2/2019.
 */
public class DtoMapDnaSampleImpl {

    Logger LOGGER = LoggerFactory.getLogger(DtoMapDnaSampleImpl.class);

    //@Autowired
    //private RsDnaSampleDao rsSampleTrackingDnaSampleDao;


    public ProjectSamplesDTO createSamples(ProjectSamplesDTO projectSamplesDTO) throws GobiiDtoMappingException {

        //ProjectSamplesDTO returnVal = null;

        try {

            return projectSamplesDTO;

        } catch (Exception e) {
            LOGGER.error("Gobii Mapping Error", e);
            throw new GobiiDtoMappingException(e);
        }


    }


    public ProjectSamplesDTO get(Integer projectId) throws GobiiDtoMappingException {

        ProjectSamplesDTO returnVal = null;
        return returnVal;

    }

}
