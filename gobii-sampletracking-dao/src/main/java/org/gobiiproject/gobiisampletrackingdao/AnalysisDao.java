package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;
import java.util.Set;

import org.gobiiproject.gobiimodel.entity.Analysis;

public interface AnalysisDao {
    /**
     * Gets analyses for given list of analysis ids
     * @param analysisIds - List of analysis ids for which
     *                    respective analysis need to be fetched
     * @return List of Analysis entity
     */
    List<Analysis> getAnalysesByAnalysisIds(Set<Integer> analysisIds);

    List<Analysis> getAnalysesByAnalysisNames(Set<String> analysisNames) throws GobiiDaoException;

	List<Analysis> getAnalyses(Integer offset, Integer pageSize);

    Analysis createAnalysis(Analysis analysis) throws Exception;

	Analysis getAnalysis(Integer id);

	Analysis updateAnalysis(Analysis analysis);

	void deleteAnalysis(Analysis analysis) throws Exception;
    
}
