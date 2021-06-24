package org.gobiiproject.gobiidomain.services.brapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.brapi.SamplesDTO;
import org.gobiiproject.gobiimodel.dto.brapi.SamplesSearchQueryDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.DnaSample;
import org.gobiiproject.gobiimodel.modelmapper.CvMapper;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.DnaSampleDao;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
public class SamplesServiceImpl implements SamplesService {


    @Autowired
    private DnaSampleDao dnaSampleDao;

    @Autowired
    private CvDao cvDao;

    /**
     * Gets the list of dna sample for given search criteria
     * @param pageNum               page number to fetch
     * @param pageSize              size of the page
     * @param sampleDbId            get sample by sample id.
     * @param germplasmDbId         filter by germplasm id.
     * @param observationUnitDbId   filter by experiment id.
     * @return List of Genotype Calls
     */
    @Override
    public PagedResult<SamplesDTO> getSamples(Integer pageSize, Integer pageNum,
                                              Integer sampleDbId, Integer germplasmDbId,
                                              String observationUnitDbId) {

        List<SamplesDTO> samplesDTOs;

        try {

            Objects.requireNonNull(pageNum, "pageNum : Required non null");
            Objects.requireNonNull(pageSize, "pageSize : Required non null");

            int rowOffset = pageNum*pageSize;

            List<DnaSample> dnaSamples = dnaSampleDao.getDnaSamples(
                pageSize,
                rowOffset,
                null,
                sampleDbId,
                germplasmDbId,
                observationUnitDbId);

            samplesDTOs = mapDnaSampleEntityToSampleDTO(dnaSamples);

            return PagedResult.createFrom(pageNum, samplesDTOs);
        }
        catch(NullPointerException e) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                e.getMessage());
        }
    }

    @Override
    public PagedResult<SamplesDTO> getSamplesBySamplesSearchQuery(
        SamplesSearchQueryDTO samplesSearchQuery,
        Integer pageSize,
        Integer pageNum) {

        List<SamplesDTO> samplesDTOs;

        try {

            Objects.requireNonNull(pageNum, "pageNum : Required non null");
            Objects.requireNonNull(pageSize, "pageSize : Required non null");

            Integer rowOffset = pageNum*pageSize;

            List<DnaSample> dnaSamples = dnaSampleDao.getDnaSamples(
                samplesSearchQuery.getSampleDbIds(), samplesSearchQuery.getSampleNames(),
                samplesSearchQuery.getSamplePUIs(), samplesSearchQuery.getGermplasmDbIds(),
                samplesSearchQuery.getGermplasmNames(), samplesSearchQuery.getGermplasmPUIs(),
                samplesSearchQuery.getSampleGroupDbIds(), pageSize, rowOffset);

            samplesDTOs = mapDnaSampleEntityToSampleDTO(dnaSamples);

            return PagedResult.createFrom(pageNum, samplesDTOs);

        }
        catch(NullPointerException e) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                e.getMessage());
        }

    }


    private List<SamplesDTO> mapDnaSampleEntityToSampleDTO(
        List<DnaSample> dnaSamples
    ) throws GobiiException {

        List<SamplesDTO> samplesDTOs = new ArrayList<>();

        List<Cv> cvList = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_DNASAMPLE_PROP.getCvGroupName(),
            null);

        for (DnaSample dnaSample : dnaSamples) {

            if (dnaSample != null) {

                SamplesDTO samplesDTO = new SamplesDTO();

                ModelMapper.mapEntityToDto(dnaSample, samplesDTO);

                if (dnaSample.getProperties() != null && dnaSample.getProperties().size() > 0) {

                    samplesDTO.setAdditionalInfo(CvMapper.mapCvIdToCvTerms(
                        cvList, dnaSample.getProperties()));

                    if (samplesDTO.getAdditionalInfo().containsKey("sample_type")) {
                        samplesDTO.setTissueType(
                            samplesDTO.getAdditionalInfo().get("sample_type"));
                        samplesDTO.getAdditionalInfo().remove("sample_type");
                    }
                }

                samplesDTOs.add(samplesDTO);
            }
        }

        return samplesDTOs;
    }

}
