package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.LoaderTemplate;
import org.gobiiproject.gobiimodel.types.GobiiLoaderPayloadTypes;

import java.util.List;

public interface LoaderTemplateDao {
    LoaderTemplate create(LoaderTemplate loaderTemplate) throws GobiiDaoException;
    LoaderTemplate getById(Integer templateId);
    List<LoaderTemplate> list(Integer pageSize,
                              Integer rowOffset,
                              GobiiLoaderPayloadTypes loaderTemplateType)  throws GobiiDaoException;
}
