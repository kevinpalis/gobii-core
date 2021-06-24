package org.gobiiproject.gobiidomain.services.brapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.ContactDTO;
import org.gobiiproject.gobiimodel.dto.brapi.StudiesDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Experiment;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiisampletrackingdao.ExperimentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
public class StudiesServiceImpl implements StudiesService {

    Logger LOGGER = LoggerFactory.getLogger(StudiesServiceImpl.class);

    @Autowired
    private ExperimentDao experimentDao;

    @Override
    public PagedResult<StudiesDTO> getStudies(Integer pageSize,
                                              Integer pageNum,
                                              Integer projectId
    ) throws GobiiException {

        try {

            Objects.requireNonNull(pageSize, "pageSize: Required non null.");
            Objects.requireNonNull(pageNum, "pageNum: Required non null.");

            List<StudiesDTO> studies = new ArrayList<>();
            Integer rowOffset = pageNum*pageSize;
            List<Experiment> experiments = experimentDao.getExperiments(
                pageSize,
                rowOffset,
                null);

            for (Experiment experiment : experiments) {
                StudiesDTO study = new StudiesDTO();
                ModelMapper.mapEntityToDto(experiment, study);
                if(experiment.getProject().getContact() != null) {
                    ContactDTO contactDTO = new ContactDTO();
                    ModelMapper.mapEntityToDto(
                            experiment.getProject().getContact(),
                            contactDTO);
                    study.getContacts().add(contactDTO);
                }
                studies.add(study);
            }

            return PagedResult.createFrom(pageNum, studies);

        } catch (NullPointerException e) {
            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }

    }

}
