package org.gobiiproject.gobidomain.services;

import org.gobiiproject.gobiimodel.dto.entity.auditable.VariantSetDTO;
import org.gobiiproject.gobiimodel.dto.entity.noaudit.SamplesBrapiDTO;

import java.util.List;

public interface VariantSetsService {

    List<VariantSetDTO> listVariantSets(Integer pageNum, Integer pageSize,
                                        Integer variantSetDbId);

}
