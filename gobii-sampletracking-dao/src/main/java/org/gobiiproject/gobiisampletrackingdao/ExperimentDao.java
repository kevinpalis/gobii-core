package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;

import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.entity.VendorProtocol;

public interface ExperimentDao {

    List<Experiment> getExperiments(Integer pageSize, Integer rowOffset,
                                    Integer projectId);

	Experiment getExperiment(Integer i) throws GobiiDaoException;

	VendorProtocol getVendorProtocol(Integer vendorProtocolId);

	Experiment createExperiment(Experiment experiment) throws Exception;

	Experiment updateExperiment(Experiment target) throws Exception;

	void deleteExperiment(Experiment experiment);

}
