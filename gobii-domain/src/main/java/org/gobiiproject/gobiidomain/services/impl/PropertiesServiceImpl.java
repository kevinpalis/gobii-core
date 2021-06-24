/**
 * PropertiesServiceImpl.java
 * 
 * Default PropertiesService implementation.
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-18
 */

package org.gobiiproject.gobiidomain.services.impl;

import java.util.List;
import java.util.Objects;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.PropertiesService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.cvnames.CvGroupTerm;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.modelmapper.CvMapper;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesServiceImpl implements PropertiesService {

    @Autowired
    private CvDao cvDao;

    // @Override
    // public PagedResult<CvPropertyDTO> getProjectProperties(Integer page, Integer pageSize) throws Exception {
    //     return this.getProperties(page, pageSize, CvGroup.CVGROUP_PROJECT_PROP);
    // }


    @Override
    public PagedResult<CvPropertyDTO> getProperties(Integer page, Integer pageSize, CvGroupTerm cvGroupTerm) throws Exception {

        try {
            Objects.requireNonNull(pageSize);
            Objects.requireNonNull(page);
            List<Cv> cvs = cvDao.getCvs(null, cvGroupTerm.getCvGroupName(), null, page, pageSize);
            List<CvPropertyDTO> converted = CvMapper.convert(cvs);
            return PagedResult.createFrom(page, converted);
        } catch (GobiiException gE) {
            throw gE;
        } catch (Exception e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }

}