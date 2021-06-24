package org.gobiiproject.gobiidtomapping.entity.auditable.sampletracking;

import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMap;
import org.gobiiproject.gobiimodel.dto.auditable.sampletracking.ExperimentDTO;

import java.util.List;

/**
 * Created by VCalaminos on 5/1/2019.
 */
public interface DtoMapExperiment extends DtoMap<ExperimentDTO> {

    ExperimentDTO create(ExperimentDTO experimentDTO) throws GobiiDtoMappingException;
    ExperimentDTO replace(Integer experimentId, ExperimentDTO experimentDTO) throws GobiiDtoMappingException;
    ExperimentDTO get(Integer experimentId) throws GobiiDtoMappingException;
    List<ExperimentDTO> getList() throws GobiiDtoMappingException;

}
