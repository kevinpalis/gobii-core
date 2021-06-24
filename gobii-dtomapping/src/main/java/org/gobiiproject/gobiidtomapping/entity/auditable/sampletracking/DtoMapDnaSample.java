package org.gobiiproject.gobiidtomapping.entity.auditable.sampletracking;

import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMap;
import org.gobiiproject.gobiimodel.dto.auditable.sampletracking.DnaSampleDTO;

import java.util.List;

/**
 * Created by VCalaminos on 5/2/2019.
 */
public interface DtoMapDnaSample extends DtoMap<DnaSampleDTO> {

    List<DnaSampleDTO> createSamples(List<DnaSampleDTO> dnaSampleDTOList) throws GobiiDtoMappingException;

}
