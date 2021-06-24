package org.gobiiproject.gobiidtomapping.entity.auditable;

import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.auditable.ExperimentDTO;

/**
 * Created by Angel on 4/19/2016.
 */
public interface DtoMapExperiment extends DtoMap<ExperimentDTO> {

    ExperimentDTO create(ExperimentDTO experimentDTO) throws GobiiDtoMappingException;
    ExperimentDTO replace(Integer experimentId, ExperimentDTO experimentDTO) throws GobiiDtoMappingException;
    ExperimentDTO get(Integer experimentId) throws GobiiDtoMappingException;
    List<ExperimentDTO> getList() throws GobiiDtoMappingException;
    List<ExperimentDTO> getExperimentsByProjectIdForLoadedDatasets(Integer projectId) throws GobiiDtoMappingException;

}
