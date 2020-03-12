package org.gobiiproject.gobidomain.services.brapi;

import org.codehaus.janino.Mod;
import org.gobiiproject.gobidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.JobType;
import org.gobiiproject.gobiimodel.dto.brapi.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.brapi.VariantSetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.*;
import org.gobiiproject.gobiisampletrackingdao.DatasetDao;
import org.gobiiproject.gobiisampletrackingdao.GobiiDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.crypto.Data;
import java.text.MessageFormat;
import java.util.*;


public class VariantSetsServiceImpl implements VariantSetsService {

    Logger LOGGER = LoggerFactory.getLogger(VariantSetsServiceImpl.class);

    private String fileUrlFormat = "/variantsets/{0, number}/calls/download";

    @Autowired
    private DatasetDao datasetDao;


    @Override
    public PagedResult<VariantSetDTO> getVariantSets(Integer pageSize, Integer pageNum,
                                      Integer variantSetDbId, String variantSetName,
                                      Integer studyDbId, String studyName) throws GobiiException {

        PagedResult<VariantSetDTO> returnVal = new PagedResult<>();

        List<VariantSetDTO> variantSets = new ArrayList<>();

        //To map variantsetdto by datasetid to avoid mapping more than once
        HashMap<Integer, VariantSetDTO> variantSetDtoMapByDatasetId = new HashMap<>();

        //To map analysisdto by analysisid to avoid mapping more than once
        HashMap<Integer, AnalysisDTO> analysisDtoMapByAnalysisId = new HashMap<>();

        Objects.requireNonNull(pageSize, "pageSize: Required non null");
        Objects.requireNonNull(pageNum, "pageNum: Required non null");

        try {

            Integer rowOffset = pageNum*pageSize;

            List<Object[]> resultTuple = datasetDao.getDatasetsWithAnalysesAndCounts(
                    pageSize, rowOffset,
                    variantSetDbId, variantSetName,
                    studyDbId, studyName);

            Set<Integer> analysisIds = new HashSet<>();

            for (Object[] tuple : resultTuple) {

                VariantSetDTO variantSetDTO;
                AnalysisDTO analysisDTO;

                Dataset dataset = (Dataset) tuple[0];
                Analysis analysis = (Analysis) tuple[1];
                Integer markerCount = (Integer) tuple[2];
                Integer dnaRunCount = (Integer) tuple[3];

                if(!variantSetDtoMapByDatasetId.containsKey(dataset.getDatasetId())) {
                    variantSetDTO = new VariantSetDTO();

                    ModelMapper.mapEntityToDto(dataset, variantSetDTO);

                    variantSets.add(variantSetDTO);

                    //Set dataset download url
                    variantSetDTO.setFileUrl(
                            MessageFormat.format(this.fileUrlFormat, dataset.getDatasetId()));

                    //Set Marker and DnaRun Counts
                    variantSetDTO.setVariantCount(markerCount);
                    variantSetDTO.setCallSetCount(dnaRunCount);

                    //Map extract ready of dataset
                    mapVariantSetExtractReady(dataset, variantSetDTO);

                    variantSetDtoMapByDatasetId.put(dataset.getDatasetId(), variantSetDTO);

                    //Map Calling analysis
                    if(dataset.getCallingAnalysis() != null) {
                        if(analysisDtoMapByAnalysisId.containsKey(
                                dataset.getCallingAnalysis().getAnalysisId())) {
                            variantSetDTO.getAnalyses().add(analysisDtoMapByAnalysisId.get(
                                    dataset.getCallingAnalysis().getAnalysisId()));
                        }
                        else {
                            analysisDTO = new AnalysisDTO();
                            ModelMapper.mapEntityToDto(dataset.getCallingAnalysis(), analysisDTO);
                            variantSetDTO.getAnalyses().add(analysisDTO);
                            analysisDtoMapByAnalysisId.put(dataset.getCallingAnalysis().getAnalysisId(),
                                    analysisDTO);
                        }
                    }
                }
                else {
                    variantSetDTO = variantSetDtoMapByDatasetId.get(dataset.getDatasetId());
                }

                if(analysis != null) {
                    if(analysisDtoMapByAnalysisId.containsKey(analysis.getAnalysisId())) {
                        variantSetDTO.getAnalyses().add(analysisDtoMapByAnalysisId.get(
                                analysis.getAnalysisId()));
                    }
                    else {
                        analysisDTO = new AnalysisDTO();
                        ModelMapper.mapEntityToDto(analysis, analysisDTO);
                        variantSetDTO.getAnalyses().add(analysisDTO);
                        analysisDtoMapByAnalysisId.put(dataset.getCallingAnalysis().getAnalysisId(),
                                analysisDTO);
                    }
                }


                analysisIds.addAll(Arrays.asList(dataset.getAnalyses()));


            }

            returnVal.setResult(variantSets);
            returnVal.setCurrentPageNum(pageNum);
            returnVal.setCurrentPageSize(variantSets.size());

            return returnVal;
        }
        catch (GobiiException ge) {
            throw ge;
        }
        catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDomainException(
                     GobiiStatusLevel.ERROR,
                     GobiiValidationStatusType.UNKNOWN,
                     e.getMessage());

        }


    }

    public VariantSetDTO getVariantSetById(Integer variantSetDbId) {


        try {
            //Overload getvariantsets by passing
            PagedResult<VariantSetDTO> variantSets = this.getVariantSets(1000, 0,
                    variantSetDbId, null,
                    null, null);

            if(variantSets.getResult() != null && variantSets.getResult().size() < 1) {
                throw new GobiiDaoException(GobiiStatusLevel.ERROR,
                        GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                        "VariantSet for given id does not exist");

            }

            return variantSets.getResult().get(0);

        }
        catch (GobiiException ge) {
            throw ge;
        }
        catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDomainException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage());

        }


    }


    private void mapVariantSetExtractReady(Dataset dataset, VariantSetDTO variantSetDTO) {

        try {
            if(dataset.getJob() == null) {
                variantSetDTO.setExtractReady(false);
            }
            else {
                variantSetDTO.setExtractReady(
                        (dataset.getJob().getType().getTerm() == JobType.CV_JOBTYPE_LOAD.getCvName() &&
                                dataset.getJob().getStatus().getTerm() == GobiiJobStatus.COMPLETED.getCvTerm()) ||
                                (dataset.getJob().getType().getTerm() != JobType.CV_JOBTYPE_LOAD.getCvName()));

            }

        }
        catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

            throw new GobiiDomainException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage());

        }

    }

}
