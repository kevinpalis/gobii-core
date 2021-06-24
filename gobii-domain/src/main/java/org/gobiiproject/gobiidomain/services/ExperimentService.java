// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-24
// ************************************************************************
package org.gobiiproject.gobiidomain.services;

import java.util.List;
import org.gobiiproject.gobiidomain.GobiiDomainException;

/**
 * Created by Angel on 4/19/2016.
 */
public interface ExperimentService<T> {

    T createExperiment(T experimentDTO) throws GobiiDomainException;
    T replaceExperiment(Integer experimentId, T experimentDTO) throws GobiiDomainException;
    List<T> getExperiments() throws GobiiDomainException;
    T getExperimentById(Integer experimentId) throws GobiiDomainException;
    List<T> getExperimentsByProjectIdForLoadedDatasets(Integer experimentId) throws GobiiDomainException;
    boolean updateExperimentDataFile(Integer experimentId, String dataFilePath) throws GobiiDomainException;

}
