package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.DnaSample;

import java.util.List;
import java.util.Set;

public interface DnaSampleDao {

    List<DnaSample> getDnaSamples(Integer pageSize,
                                  Integer rowOffet,
                                  Integer projectId,
                                  Integer dnaSampleId,
                                  Integer germplasmId,
                                  String germplasmExternamCode);

    List<DnaSample> getDnaSamples(Integer pageSize,
                                  Integer rowOffset);

    List<DnaSample> getDnaSamplesByProjectId(Integer projectId,
                                             Integer pageSize,
                                             Integer rowOffset);

    DnaSample getDnaSampleByDnaSampleId(Integer dnaSampleId);

    List<DnaSample> getDnaSamplesByGermplasmId(Integer germplasmId,
                                               Integer pageSize,
                                               Integer rowOffset);

    List<DnaSample> getDnaSamplesByGermplasmExternalCode(String germplasmExternalCode,
                                                         Integer pageSize,
                                                         Integer rowOffset);

    List<DnaSample> getDnaSamples(Set<Integer> dnaSampleIds,
                                  Set<String> dnaSampleNames,
                                  Set<String> dnaSampleUuids,
                                  Set<Integer> germplasmDbIds,
                                  Set<String> germplasmNames,
                                  Set<String> germplasmExternalCodes,
                                  Set<Integer> projectIds,
                                  Integer pageSize,
                                  Integer rowOffset);

    List<DnaSample> getDnaSamples(Set<String> dnaSampleNames,
                                  Integer projectId,
                                  Integer pageSize,
                                  Integer rowOffset) throws GobiiDaoException;

    List<DnaSample> queryByNameAndNum(List<DnaSample> queryParams, Integer projectId);

}
