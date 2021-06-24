package org.gobiiproject.gobiidtomapping.entity.auditable;

import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.auditable.AnalysisDTO;

/**
 * Created by Phil on 4/21/2016.
 */
public interface DtoMapAnalysis extends DtoMap<AnalysisDTO> {

    //DtoMap methods
    AnalysisDTO create(AnalysisDTO  analysisDTO) throws GobiiDtoMappingException;
    AnalysisDTO replace(Integer analysisId, AnalysisDTO analysisDTO) throws GobiiDtoMappingException;
    AnalysisDTO get(Integer  analysisId) throws GobiiDtoMappingException;
    List<AnalysisDTO> getList() throws GobiiDtoMappingException;




}
