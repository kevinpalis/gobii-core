package org.gobiiproject.gobiidomain.services.gdmv3;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.gdmv3.ContactDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.LoaderTemplateDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.templates.DnaRunTemplateDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.templates.GenotypeMatrixTemplateDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.templates.MarkerTemplateDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.LoaderTemplate;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.GobiiCvGroupType;
import org.gobiiproject.gobiimodel.types.GobiiLoaderPayloadTypes;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.LoaderTemplateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.PagesPerMinute;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Transactional
public class LoaderTemplateServiceImpl implements LoaderTemplateService {


    @Autowired
    private LoaderTemplateDao loaderTemplateDao;

    @Autowired
    private CvDao cvDao;

    @Autowired
    private ContactDao contactDao;

    @Override
    public LoaderTemplateDTO addMarkerTemplate(LoaderTemplateDTO loaderTemplateDTO
    ) throws Exception {
        return addLoaderTemplate(
            loaderTemplateDTO,
            GobiiLoaderPayloadTypes.MARKERS);
    }

    @Override
    public LoaderTemplateDTO addDnaRunTemplate(LoaderTemplateDTO loaderTemplateDTO
    ) throws Exception {
        return addLoaderTemplate(
            loaderTemplateDTO,
            GobiiLoaderPayloadTypes.SAMPLES);
    }
    
    @Override
    public LoaderTemplateDTO addGenotypeTemplate(LoaderTemplateDTO loaderTemplateDTO
    ) throws Exception {
        return addLoaderTemplate(
            loaderTemplateDTO,
            GobiiLoaderPayloadTypes.MATRIX);
    }

    private LoaderTemplateDTO addLoaderTemplate(LoaderTemplateDTO loaderTemplateDTO,
                                                GobiiLoaderPayloadTypes payloadType
    ) throws Exception {

        LoaderTemplate loaderTemplate  = new LoaderTemplate();

        // Get current user
        String userName = ContactService.getCurrentUserName();
        Contact createdBy = contactDao.getContactByUsername(userName);

        loaderTemplate.setTemplateName(loaderTemplateDTO.getTemplateName());
        loaderTemplate.setTemplate(loaderTemplateDTO.getTemplate());

        loaderTemplate.setCreatedDate(new Date());
        loaderTemplate.setModifiedDate(new Date());
        loaderTemplate.setCreatedBy(createdBy);
        loaderTemplate.setModifiedBy(createdBy);


        // Set marker payload type as template type
        Cv templateType = cvDao.getCvs(payloadType.getTerm(),
            CvGroupTerm.CVGROUP_PAYLOADTYPE.getCvGroupName(),
            GobiiCvGroupType.GROUP_TYPE_SYSTEM).get(0);

        loaderTemplate.setTemplateType(templateType);

        loaderTemplate = loaderTemplateDao.create(loaderTemplate);

        ModelMapper.mapEntityToDto(loaderTemplate, loaderTemplateDTO);

        return loaderTemplateDTO;

    }

    @Override
    public PagedResult<LoaderTemplateDTO> getMarkerTemplates(Integer pageSize,
                                                             Integer pageNum
    ) throws GobiiException {
        return getLoaderTemplates(pageSize, pageNum, GobiiLoaderPayloadTypes.MARKERS);
    }


    /**
     * Get the list for dnarun loader template.
     * @param pageSize  page size
     * @param pageNum   page num to fetch
     * @return  list of dnarun loader templates
     * @throws GobiiException
     */
    @Override
    public PagedResult<LoaderTemplateDTO> getDnaRunTemplates(Integer pageSize,
                                                             Integer pageNum
    ) throws GobiiException {
        return getLoaderTemplates(pageSize, pageNum, GobiiLoaderPayloadTypes.SAMPLES);
    }
    
    /**
     * Get the list for matrix loader template.
     * @param pageSize  page size
     * @param pageNum   page num to fetch
     * @return  list of dnarun loader templates
     * @throws GobiiException
     */
    @Override
    public PagedResult<LoaderTemplateDTO> getGenotypeTemplates(Integer pageSize,
                                                               Integer pageNum
    ) throws GobiiException {
        return getLoaderTemplates(pageSize, pageNum, GobiiLoaderPayloadTypes.MATRIX);
    }


    private PagedResult<LoaderTemplateDTO>
    getLoaderTemplates(Integer pageSize,
                       Integer pageNum,
                       GobiiLoaderPayloadTypes gobiiLoaderPayloadType) throws GobiiException {

        List<LoaderTemplateDTO> loaderTemplateDTOs = new ArrayList<>();

        try {
            Objects.requireNonNull(pageSize, "pageSize: Required non null");
            Objects.requireNonNull(pageNum, "pageNum: Required non null");

            Integer rowOffset = pageNum*pageSize;

            List<LoaderTemplate> loaderTemplates =
                loaderTemplateDao.list(
                    pageSize,
                    rowOffset,
                    gobiiLoaderPayloadType);

            for(LoaderTemplate loaderTemplate : loaderTemplates) {
                LoaderTemplateDTO loaderTemplateDTO = new LoaderTemplateDTO();
                ModelMapper.mapEntityToDto(loaderTemplate, loaderTemplateDTO);
                loaderTemplateDTOs.add(loaderTemplateDTO);
            }
            return PagedResult.createFrom(pageNum, loaderTemplateDTOs);
        }
        catch (NullPointerException nE) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.BAD_REQUEST,
                nE.getMessage());
        }
    }

    /**
     * @return an empty marker template {@link MarkerTemplateDTO}
     * @throws GobiiException
     */
    @Override
    public MarkerTemplateDTO getEmptyMarkerTemplate() throws GobiiException {

        MarkerTemplateDTO markerTemplateDTO = new MarkerTemplateDTO();
        // Get marker properties
        List<Cv> markerProperties = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_MARKER_PROP.getCvGroupName(),
            null);
        for(Cv markerProperty : markerProperties) {
            markerTemplateDTO
                .getMarkerProperties()
                .put(markerProperty.getTerm(), new ArrayList<>());
        }
        return markerTemplateDTO;
    }
   
    /**
     * @return an empty genotype template {@link MarkerTemplateDTO}
     * @throws GobiiException
     */
    @Override
    public GenotypeMatrixTemplateDTO getEmptyGenotypeTemplate() throws GobiiException {
        return new GenotypeMatrixTemplateDTO();
    }

    /**
     * @return an empty dnarun template {@link DnaRunTemplateDTO}
     * @throws GobiiException
     */
    @Override
    public DnaRunTemplateDTO getEmptyDnaRunTemplate() throws GobiiException {
        DnaRunTemplateDTO dnaRunTemplateDTO = new DnaRunTemplateDTO();

        // Add dnarun properties
        List<Cv> dnaRunProperties = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_DNARUN_PROP.getCvGroupName(),
            null);
            dnaRunProperties.forEach(dnaRunProperty -> dnaRunTemplateDTO
            .getDnaRunProperties()
            .put(dnaRunProperty.getTerm(), new ArrayList<>()));

        // Add dna sample properties
        List<Cv> dnaSampleProperties = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_DNASAMPLE_PROP.getCvGroupName(), null);
            dnaSampleProperties.forEach(dnaSampleProperty -> dnaRunTemplateDTO
            .getDnaSampleProperties()
            .put(dnaSampleProperty.getTerm(), new ArrayList<>()));

        // Add germplasm properties
        List<Cv> germplasmProperties = cvDao.getCvListByCvGroup(
            CvGroupTerm.CVGROUP_GERMPLASM_PROP.getCvGroupName(),
            null);

        germplasmProperties.forEach(germplasmProperty -> dnaRunTemplateDTO
            .getGermplasmProperties()
            .put(germplasmProperty.getTerm(), new ArrayList<>()));

        return dnaRunTemplateDTO;
    }




}
