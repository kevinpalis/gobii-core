package org.gobiiproject.gobiidomain.services.gdmv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.DeleteException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.EntityDoesNotExistException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.UnknownEntityException;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.CvTypeDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.entity.Reference;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiisampletrackingdao.AnalysisDao;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.DatasetDao;
import org.gobiiproject.gobiisampletrackingdao.ReferenceDao;
import org.springframework.beans.factory.annotation.Autowired;

public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private AnalysisDao analysisDao;

    @Autowired
    private CvDao cvDao;

    @Autowired
    private ReferenceDao referenceDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private DatasetDao datasetDao;

    @Transactional
    @Override
    public PagedResult<AnalysisDTO> getAnalyses(Integer page, Integer pageSize) throws Exception {
        List<Analysis> analyses = analysisDao.getAnalyses(page * pageSize, pageSize);
        List<AnalysisDTO> analysisDTOs = new ArrayList<>();

        analyses.forEach((analysis) -> {
            AnalysisDTO analysisDTO = new AnalysisDTO();
            ModelMapper.mapEntityToDto(analysis, analysisDTO);
            analysisDTOs.add(analysisDTO);
        });

        return PagedResult.createFrom(page, analysisDTOs);
    }

    @Transactional
    @Override
    public AnalysisDTO createAnalysis(AnalysisDTO analysisRequest, String user) throws Exception {
        Analysis analysis = new Analysis();
        
        // Get analysis type
        Cv analysisType = this.loadAnalysisType(analysisRequest.getAnalysisTypeId());
        analysis.setType(analysisType);

        if (analysisRequest.getReferenceId() != null) {
            Reference reference = this.loadReference(analysisRequest.getReferenceId());
            analysis.setReference(reference);
        }

        //get new status
        Cv cv = cvDao.getNewStatus();       
        analysis.setStatus(cv);

        ModelMapper.mapDtoToEntity(analysisRequest, analysis, true);

        // audit items
        Contact creator = contactDao.getContactByUsername(user);
        analysis.setCreatedBy(Optional.ofNullable(creator).map( v -> creator.getContactId()).orElse(null));
        analysis.setCreatedDate(new java.util.Date());

        analysis = analysisDao.createAnalysis(analysis);

        AnalysisDTO analysisDTO = new AnalysisDTO();
        ModelMapper.mapEntityToDto(analysis, analysisDTO);
        return analysisDTO;
    }

    @Transactional
    @Override
    public CvTypeDTO createAnalysisType(CvTypeDTO analysisTypeRequest, String creatorId) throws Exception {
        
        CvGroup cvGroup = cvDao.getCvGroupByNameAndType(
            CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName(),
            2
        );
        if (cvGroup == null) throw new GobiiDaoException("Missing CvGroup for Analysis Type");

        Cv cv = new Cv();
        cv.setCvGroup(cvGroup);
        cv.setTerm(analysisTypeRequest.getTypeName());
        cv.setDefinition(analysisTypeRequest.getTypeDescription());

        //get the new row status
        // Get the Cv for status, new row
        Cv status = cvDao.getNewStatus();
        cv.setStatus(status.getCvId());

        //set rank
        cv.setRank(0);
        cv = cvDao.createCv(cv);
        
        CvTypeDTO analysisDTO = new CvTypeDTO();
        ModelMapper.mapEntityToDto(cv, analysisDTO);
        return analysisDTO;

    }

    @Override
    public PagedResult<CvTypeDTO> getAnalysisTypes(Integer page, Integer pageSize) {
        List<Cv> cvs = cvDao.getCvs(null, CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName(), null, page, pageSize);
        List<CvTypeDTO> analysisTypeDTOs = new ArrayList<>();

        cvs.forEach(cv -> {
            CvTypeDTO analysisTypeDTO = new CvTypeDTO();
            ModelMapper.mapEntityToDto(cv, analysisTypeDTO);
            analysisTypeDTOs.add(analysisTypeDTO);
        });

        return PagedResult.createFrom(page, analysisTypeDTOs);
    }

    @Transactional
    @Override
    public AnalysisDTO updateAnalysis(Integer analysisId, AnalysisDTO analysisDTO, String updatedBy) throws Exception {
        Analysis analysis = this.loadAnalysis(analysisId);

        if (analysisDTO.getReferenceId() != null && analysisDTO.getReferenceId() > 0) {
            //check if the reference exists
            Reference reference = this.loadReference(analysisDTO.getReferenceId());
            analysis.setReference(reference);
        }
        else if(analysisDTO.getReferenceId() == 0) {
            analysis.setReference(null);
        }

        if (analysisDTO.getAnalysisTypeId() != null) {
            // Get analysis type
            Cv analysisType = this.loadAnalysisType(analysisDTO.getAnalysisTypeId());
            analysis.setType(analysisType);
        }

        ModelMapper.mapDtoToEntity(analysisDTO, analysis, true); //ignore the nulls

        // audit items
        Contact updater = contactDao.getContactByUsername(updatedBy);
        analysis.setModifiedBy(Optional.ofNullable(updater).map(v -> updater.getContactId()).orElse(null));
        analysis.setModifiedDate(new java.util.Date());

        Cv cv = cvDao.getModifiedStatus();
        analysis.setStatus(cv);

        Analysis updatedAnalysis = analysisDao.updateAnalysis(analysis);
    
        AnalysisDTO updatedAnalysisDTO = new AnalysisDTO();
        ModelMapper.mapEntityToDto(updatedAnalysis, updatedAnalysisDTO);
        return updatedAnalysisDTO;
    }

    @Transactional
    @Override
    public AnalysisDTO getAnalysis(Integer analysisId) throws Exception {  
        Analysis analysis = this.loadAnalysis(analysisId);
        AnalysisDTO analysisDTO = new AnalysisDTO();
        ModelMapper.mapEntityToDto(analysis, analysisDTO);
        return analysisDTO;
    }

    @Transactional
    @Override
    public void deleteAnalysis(Integer analysisId) throws Exception {
        //Check if analysis exists
        Analysis analysis = this.loadAnalysis(analysisId);

        //check for data sets calling analysis or analyses column
        if (
            datasetDao.getDatasetCountByAnalysisId(analysisId) > 0 ||
            datasetDao.getDatasetCountWithAnalysesContaining(analysisId) > 0
        ) {
            throw new DeleteException();
        }

        analysisDao.deleteAnalysis(analysis);
    }

    private Analysis loadAnalysis(Integer analysisId) throws GobiiDaoException {
        Analysis analysis = analysisDao.getAnalysis(analysisId);
        if (analysis == null) {
            throw new EntityDoesNotExistException("Analysis");
        }
        return analysis;
    }

    private Cv loadAnalysisType(Integer cvId) throws Exception {
        // Get analysis type
        Cv analysisType = cvDao.getCvByCvId(cvId);
        if (analysisType == null) {
            throw new UnknownEntityException.Analysis();
        }

        if (!analysisType.getCvGroup().getCvGroupName().equals(CvGroupTerm.CVGROUP_ANALYSIS_TYPE.getCvGroupName())) {
            throw new InvalidException("Analysis Type Id");
        }

        return analysisType;
    }

    private Reference loadReference(Integer referenceId) throws Exception {
        Reference reference = referenceDao.getReference(referenceId);
        if (reference == null) {
            throw new UnknownEntityException.Reference();
        }
        return reference;
    }
 }