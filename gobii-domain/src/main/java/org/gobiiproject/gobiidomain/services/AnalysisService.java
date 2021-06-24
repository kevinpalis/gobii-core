package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.dto.auditable.AnalysisDTO;

/**
 * Created by Phil on 4/21/2016.
 * Modified by Yanii on 1/27/2017
 */
public interface AnalysisService {

    List<AnalysisDTO> getAnalyses() throws GobiiDomainException;
    AnalysisDTO createAnalysis(AnalysisDTO analysisDTO) throws GobiiDomainException;
    AnalysisDTO replaceAnalysis(Integer analysisId, AnalysisDTO analysisDTO) throws GobiiDomainException;
    AnalysisDTO getAnalysisById(Integer analysisId) throws GobiiDomainException;
}
