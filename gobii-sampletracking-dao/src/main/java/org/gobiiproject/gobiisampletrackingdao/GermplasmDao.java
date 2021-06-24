package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.Germplasm;

import java.util.List;
import java.util.Set;

public interface GermplasmDao {
    List<Germplasm> getGermplamsByExternalCodes(Set<String> externalCodes,
                                                Integer pageSize,
                                                Integer rowOffset) throws GobiiDaoException;
}
