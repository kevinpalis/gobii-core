package org.gobiiproject.gobiidomain.services.gdmv3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.DeleteException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.EntityDoesNotExistException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.UnknownEntityException;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.DatasetRequestDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiisampletrackingdao.AnalysisDao;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.DatasetDao;
import org.gobiiproject.gobiisampletrackingdao.DnaRunDao;
import org.gobiiproject.gobiisampletrackingdao.ExperimentDao;
import org.gobiiproject.gobiisampletrackingdao.MarkerDao;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatasetServiceImpl implements DatasetService {

	@Autowired
	private DatasetDao datasetDao;

	@Autowired
	private ExperimentDao experimentDao;

	@Autowired
	private CvDao cvDao;
	
	@Autowired
	private AnalysisDao analysisDao;

	@Autowired
	private ContactDao contactDao;

	@Autowired
	private MarkerDao markerDao;

	@Autowired
	private DnaRunDao dnaRunDao;
	
	@Transactional
	@Override
	public DatasetDTO createDataset(DatasetRequestDTO request, String user) throws Exception {
		//check if the experiment exists
		log.debug("Creating new dataset");
		Experiment experiment = experimentDao.getExperiment(request.getExperimentId());
		if (
			experiment == null
		) {
			throw new UnknownEntityException("Experiment");
		}

		//check if the calling analysis id exists
		Analysis callingAnalysis = analysisDao.getAnalysis(request.getCallingAnalysisId());
		if (
			callingAnalysis == null
		) {
			throw new UnknownEntityException("Calling Analysis Id");
		}

		//check analysis ids
		List<Analysis> analyses = this.checkAndGetAnalysesFromIds(request.getAnalysisIds());
		
		//check datasetTypeId
		Cv datasetType = null;
		if (
			request.getDatasetTypeId() != null
		) {
			datasetType = this.getDatasetCv(request.getDatasetTypeId());
		}

		//everything is ok
		Dataset dataset = new Dataset();
		dataset.setDatasetName(request.getDatasetName());
		dataset.setExperiment(experiment);
		dataset.setAnalyses(request.getAnalysisIds());
		dataset.setCallingAnalysis(callingAnalysis);
		dataset.setType(datasetType);

		//status item
		Cv newStatus = cvDao.getNewStatus();
		dataset.setStatus(newStatus);

		// audit items
		Contact creator = contactDao.getContactByUsername(user);
		dataset.setCreatedBy(Optional.ofNullable(creator).map(v -> v.getContactId()).orElse(null));
		dataset.setCreatedDate(new java.util.Date());

		Dataset savedDataset = datasetDao.saveDataset(dataset);
		
		DatasetDTO datasetDTO = new DatasetDTO();
		//manual load the analysis DTOs
		if (analyses != null) {
			List<AnalysisDTO> analysisDTOs = this.getAnalysisDTOs(analyses);
			datasetDTO.setAnalyses(analysisDTOs);
		}
		
		ModelMapper.mapEntityToDto(savedDataset, datasetDTO);
		//force set -- TODO:  debug why piContactId and piContactName is null 
		datasetDTO.setPiContactFirstName(savedDataset.getExperiment().getProject().getContact().getFirstName());
		datasetDTO.setPiContactLastName(savedDataset.getExperiment().getProject().getContact().getLastName());
		datasetDTO.setPiContactId(savedDataset.getExperiment().getProject().getContact().getContactId());
		
		return datasetDTO;
	}

	private Cv getDatasetCv(Integer id) throws Exception {
		Cv datasetType = cvDao.getCvByCvId(id);
		
		if (datasetType == null ) {
			throw new UnknownEntityException("Dataset Type Id");
		}

		//check if the cv is a dataset type
		if (!datasetType.getCvGroup().getCvGroupName().equals(CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName())) {
			throw new InvalidException("Dataset Type Id");
		}

		return datasetType;
	}

	@Transactional
	@Override
	public PagedResult<DatasetDTO> getDatasets(Integer page, Integer pageSize, Integer experimentId,
			Integer datasetTypeId)  throws Exception {
		Integer rowOffset = page * pageSize;
		List<Object[]> tupleList = datasetDao.getDatasetWithAnalysesAndStats(pageSize, rowOffset, null, null,  datasetTypeId, experimentId, null);

		HashMap<Integer, DatasetDTO> datasetMap = new LinkedHashMap<>();
		
		for (Object[] tuple: tupleList) {

			AnalysisDTO analysisDTO = new AnalysisDTO();

			Dataset dataset  = (Dataset) tuple[0];
			Analysis analysis = (Analysis) tuple[1];

			if (analysis != null) ModelMapper.mapEntityToDto(analysis, analysisDTO);

			if (datasetMap.containsKey(dataset.getDatasetId()) && analysis != null) {
				datasetMap.get(dataset.getDatasetId()).getAnalyses().add(analysisDTO);
			} else {
				DatasetDTO datasetDTO = new DatasetDTO();
				ModelMapper.mapEntityToDto(dataset, datasetDTO);

				if (analysis != null) {
					List<AnalysisDTO> analyses = new ArrayList<>();
					analyses.add(analysisDTO);
					datasetDTO.setAnalyses(analyses);
				}
				datasetMap.put(dataset.getDatasetId(), datasetDTO);
			}
		}

		List<DatasetDTO> datasetDTOs = datasetMap.values().stream().collect(Collectors.toList());

		return PagedResult.createFrom(page, datasetDTOs);
	}

	private List<Analysis> checkAndGetAnalysesFromIds(Integer[] analysisIdArray) throws Exception {
		//check analysis ids
		Set<Integer> analysisIds = new HashSet<>(
			Arrays.asList(
				Optional.ofNullable(analysisIdArray).orElse(
					new Integer[]{}
				)
			)
		);

		List<Analysis> analyses = null;
		if (
			!analysisIds.isEmpty()
		) {
			analyses = analysisDao.getAnalysesByAnalysisIds(analysisIds);
			if (analyses.size() != analysisIds.size()) {
				throw new UnknownEntityException("Analysis Id");
			}
		}
		return analyses;
	}

	private List<AnalysisDTO> getAnalysisDTOs(List<Analysis> analyses) {
		List<AnalysisDTO> analysisDTOs = new ArrayList<>();
		if (analyses == null) return analysisDTOs;
		analyses.forEach(analysis -> {
			AnalysisDTO analysisDTO = new AnalysisDTO();
			ModelMapper.mapEntityToDto(analysis, analysisDTO);
			analysisDTOs.add(analysisDTO);
		});
		return analysisDTOs;
	}

	@Transactional
	@Override
	public DatasetDTO getDataset(Integer datasetId) throws Exception {
		Dataset dataset = this.loadDataset(datasetId);
		DatasetDTO datasetDTO = new DatasetDTO();

		ModelMapper.mapEntityToDto(dataset, datasetDTO);
		//set the analyses
		Set<Integer> analysisIds = new HashSet<>(
				Arrays.asList(
					Optional.ofNullable(dataset.getAnalyses()).orElse(new Integer[]{})
				)
			);
		
		if (analysisIds.size() > 0) {
			//convert
			List<AnalysisDTO> analysesDTOs = this.getAnalysisDTOs(
				analysisDao.getAnalysesByAnalysisIds(analysisIds)
			);

			datasetDTO.setAnalyses(
				analysesDTOs
			);
		}

		return datasetDTO;
	}

	@Transactional
	@Override
	public DatasetDTO updateDataset(Integer datasetId, DatasetRequestDTO request, String user) throws Exception {
		//check if  datasetId exists
		Dataset targetDataset = this.loadDataset(datasetId);

		boolean modified = false;
		//check if datasetName change
		if (!LineUtils.isNullOrEmpty(request.getDatasetName())) {
			targetDataset.setDatasetName(request.getDatasetName());
			modified = true;
		}

		//check if experiment change
		// if (checkIdItemsExists(request.getExperimentId())) {
		// 	//check if experiment exists
		// 	Experiment experiment = experimentDao.getExperiment(request.getExperimentId());
		// 	if (experiment == null) {
		// 		throw new InvalidException("Experiment");
		// 	}

		// 	targetDataset.setExperiment(experiment);
		// 	modified = true;
		// }

		//check if callingAnalysisId
		if (checkIdItemsExists(request.getCallingAnalysisId())) {
			Analysis analysis = analysisDao.getAnalysis(request.getCallingAnalysisId());
			if (analysis == null) {
				throw new InvalidException("Calling Analysis");
			}
			targetDataset.setCallingAnalysis(analysis);
			modified = true;
		}

		//check for datasetTypeId
		if (checkIdItemsExists(request.getDatasetTypeId())) {
			Cv cv = this.getDatasetCv(request.getDatasetTypeId());
			targetDataset.setType(cv);
			modified = true;
		}

		//check for analysisIds
		if (checkAnalysisIdsExist(request.getAnalysisIds())) {
			this.checkAndGetAnalysesFromIds(request.getAnalysisIds());
			targetDataset.setAnalyses(request.getAnalysisIds());
			modified = true;
		}

		Dataset updatedDataset = targetDataset;
		
		if (modified) {
			//audit items
			//status item
			Cv modifiedStatus = cvDao.getModifiedStatus();
			targetDataset.setStatus(modifiedStatus);

			// audit items
			Contact creator = contactDao.getContactByUsername(user);
			targetDataset.setModifiedBy(Optional.ofNullable(creator).map(v->v.getContactId()).orElse(null));
			targetDataset.setModifiedDate(new java.util.Date());

			updatedDataset = datasetDao.updateDataset(targetDataset);
		} 

		DatasetDTO datasetDTO = new DatasetDTO();
		ModelMapper.mapEntityToDto(updatedDataset, datasetDTO);

		//set the analysis
		if (checkAnalysisIdsExist(targetDataset.getAnalyses())) {
			datasetDTO.setAnalyses(
				this.getAnalysisDTOs(
					this.checkAndGetAnalysesFromIds(
						targetDataset.getAnalyses()
					)
				)
			);
		}
		return datasetDTO;

	}

	@Transactional
	@Override
	public void deleteDataset(Integer datasetId) throws Exception {
		Dataset dataset = this.loadDataset(datasetId);
		//check run counts
		//check marker 
		List<Marker> markers = markerDao.getMarkersByDatasetId(datasetId, 1, 0);
		if (checkConnectedListExists(markers)) {
			throw new DeleteException();
		}

		//check dnarun
		List<DnaRun> dnaRuns = dnaRunDao.getDnaRunsByDatasetId(datasetId, 1, 0);
		if (checkConnectedListExists(dnaRuns)) {
			throw new DeleteException();
		}
		datasetDao.deleteDataset(dataset);
	}

	//---Dataset Types
	@Transactional
	@Override
    public PagedResult<CvTypeDTO> getDatasetTypes(Integer page, Integer pageSize) {
        List<Cv> cvs = cvDao.getCvs(null, CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName(), null, page, pageSize);
        List<CvTypeDTO> cvTypeDTOs = new ArrayList<>();

        cvs.forEach(cv -> {
            CvTypeDTO cvTypeDTO = new CvTypeDTO();
            ModelMapper.mapEntityToDto(cv, cvTypeDTO);
            cvTypeDTOs.add(cvTypeDTO);
		});
		
        return PagedResult.createFrom(page, cvTypeDTOs);
    }

	@Transactional
	@Override
	public CvTypeDTO createDatasetType(String datasetTypeName,
                                       String datasetTypeDescription,
                                       String user) {
        CvGroup cvGroup = cvDao.getCvGroupByNameAndType(
            CvGroupTerm.CVGROUP_DATASET_TYPE.getCvGroupName(),
            2 //TODO:  this is custom type
        );
        if (cvGroup == null) throw new GobiiDaoException("Missing CvGroup for Dataset Type");

        Cv cv = new Cv();

        cv.setTerm(datasetTypeName);

        cv.setCvGroup(cvGroup);
		cv.setTerm(datasetTypeName);
		
		if (datasetTypeDescription != null)
        	cv.setDefinition(datasetTypeDescription);

        //get the new row status
        // Get the Cv for status, new row
        Cv status = cvDao.getNewStatus();
        cv.setStatus(status.getCvId());

        //set rank
        cv.setRank(0);
        cv = cvDao.createCv(cv);
        
        CvTypeDTO datasetDTO = new CvTypeDTO();
        ModelMapper.mapEntityToDto(cv, datasetDTO);
        return datasetDTO;

	}
	
	private Dataset loadDataset(Integer datasetId) throws EntityDoesNotExistException {
		Dataset dataset = datasetDao.getDataset(datasetId);
		if (dataset == null) {
			throw new EntityDoesNotExistException.Dataset();
		}
		return dataset;
	}

	private boolean checkIdItemsExists(Integer id) {
		return Optional.ofNullable(id).orElse(0) > 0;
	}

	private boolean checkAnalysisIdsExist(Integer[] ids) {
		return Optional.ofNullable(ids).map( v -> v.length).orElse(0) > 0;
	}

	private boolean checkConnectedListExists(List<?> list) {
		return Optional.ofNullable(list).map( v -> v.size()).orElse(0) > 0;
 	}

}