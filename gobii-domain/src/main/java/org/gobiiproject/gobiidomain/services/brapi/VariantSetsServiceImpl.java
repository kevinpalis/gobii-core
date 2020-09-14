package org.gobiiproject.gobiidomain.services.brapi;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.JobType;
import org.gobiiproject.gobiimodel.dto.brapi.AnalysisDTO;
import org.gobiiproject.gobiimodel.dto.brapi.FileFormatDTO;
import org.gobiiproject.gobiimodel.dto.brapi.VariantSetDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Analysis;
import org.gobiiproject.gobiimodel.entity.Dataset;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.GobiiJobStatus;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiisampletrackingdao.DatasetDao;
import org.gobiiproject.gobiisampletrackingdao.GobiiDaoException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

@Transactional
@Slf4j
public class VariantSetsServiceImpl implements VariantSetsService {

    final String unphasedSep = "/";
    final String unknownChar = "N";

    private String fileUrlFormat = "/variantsets/{0, number}/calls/download";

    @Autowired
    private DatasetDao datasetDao;


    @Override
    public PagedResult<VariantSetDTO>
    getVariantSets(Integer pageSize, Integer pageNum,
                   Integer variantSetDbId, String variantSetName,
                   Integer studyDbId, String studyName) throws GobiiException {

        List<VariantSetDTO> variantSets = new ArrayList<>();

        //To map variantset by datasetid to avoid mapping more than once
        HashMap<Integer, VariantSetDTO> variantSetDtoMapByDatasetId = new HashMap<>();

        //To map analysisdto by analysisid to avoid mapping more than once
        HashMap<Integer, AnalysisDTO> analysisDtoMapByAnalysisId = new HashMap<>();

        try {

            Objects.requireNonNull(pageSize, "pageSize: Required non null");
            Objects.requireNonNull(pageNum, "pageNum: Required non null");

            Integer rowOffset = pageNum*pageSize;

            List<Object[]> resultTuple = datasetDao.getDatasetsWithAnalysesAndCounts(
                pageSize,
                rowOffset,
                variantSetDbId,
                variantSetName,
                studyDbId,
                studyName);


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

                    variantSetDTO.setAvailableFormats(new ArrayList<>());

                    FileFormatDTO fileFormat = new FileFormatDTO();

                    fileFormat.setDataFormat("tabular");

                    fileFormat.setFileFormat("text/csv");

                    fileFormat.setSepUnphased(unphasedSep);

                    fileFormat.setUnknownString(unknownChar);

                    //Set dataset download url
                    fileFormat.setFileURL(
                        MessageFormat.format(this.fileUrlFormat, dataset.getDatasetId()));

                    variantSetDTO.getAvailableFormats().add(fileFormat);

                    //Set Marker and DnaRun Counts
                    variantSetDTO.setVariantCount(markerCount);
                    variantSetDTO.setCallSetCount(dnaRunCount);

                    //Map extract ready of dataset
                    mapVariantSetExtractReady(dataset, variantSetDTO);

                    variantSetDtoMapByDatasetId.put(dataset.getDatasetId(), variantSetDTO);

                    //Map Calling analysis
                    if(dataset.getCallingAnalysis() != null) {
                        if(analysisDtoMapByAnalysisId
                            .containsKey(dataset.getCallingAnalysis().getAnalysisId())) {
                            variantSetDTO
                                .getAnalyses()
                                .add(analysisDtoMapByAnalysisId
                                    .get(dataset.getCallingAnalysis().getAnalysisId()));
                        }
                        else {
                            analysisDTO = new AnalysisDTO();
                            ModelMapper.mapEntityToDto(dataset.getCallingAnalysis(), analysisDTO);
                            variantSetDTO.getAnalyses().add(analysisDTO);
                            analysisDtoMapByAnalysisId.put(
                                dataset.getCallingAnalysis().getAnalysisId(),
                                analysisDTO);
                        }
                    }
                }
                else {
                    variantSetDTO = variantSetDtoMapByDatasetId.get(dataset.getDatasetId());
                }

                if(analysis != null) {

                    if(analysisDtoMapByAnalysisId.containsKey(analysis.getAnalysisId())) {
                        variantSetDTO
                            .getAnalyses()
                            .add(analysisDtoMapByAnalysisId.get(analysis.getAnalysisId()));
                    }
                    else {

                        analysisDTO = new AnalysisDTO();
                        ModelMapper.mapEntityToDto(analysis, analysisDTO);
                        variantSetDTO.getAnalyses().add(analysisDTO);
                        analysisDtoMapByAnalysisId.put(
                            dataset.getCallingAnalysis().getAnalysisId(),
                            analysisDTO);
                    }
                }
            }

            return PagedResult.createFrom(pageNum, variantSets);
        }
        catch (NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new GobiiDomainException(
                     GobiiStatusLevel.ERROR,
                     GobiiValidationStatusType.UNKNOWN,
                     e.getMessage());
        }


    }

    public VariantSetDTO getVariantSetById(Integer variantSetDbId) throws GobiiException {

        //Overload getvariantsets by passing
        PagedResult<VariantSetDTO> variantSets = this.getVariantSets(
            2,
            0,
            variantSetDbId,
            null,
            null,
            null);

        if(CollectionUtils.isEmpty(variantSets.getResult())) {
            throw new GobiiDaoException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.ENTITY_DOES_NOT_EXIST,
                "VariantSet for given id does not exist");
        }
        return variantSets.getResult().get(0);

    }


    private void mapVariantSetExtractReady(Dataset dataset, VariantSetDTO variantSetDTO) {

        variantSetDTO.setAdditionalInfo(new HashMap<>());

        try {
            if(dataset.getJob() == null) {
                variantSetDTO.getAdditionalInfo().put("extractReady", false);
            }
            else {
                variantSetDTO.getAdditionalInfo().put(
                    "extractReady",
                    isVariantSetExtractReady(dataset));
            }

        }
        catch (Exception e) {

            log.error(e.getMessage(), e);

            throw new GobiiDomainException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage());

        }

    }

    private boolean isVariantSetExtractReady(Dataset dataset) {
        return !dataset.getJob().getType().getTerm().equals(JobType.CV_JOBTYPE_LOAD.getCvName())
            || dataset.getJob().getStatus().getTerm().equals(GobiiJobStatus.COMPLETED.getCvTerm());
    }

}
