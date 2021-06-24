package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.entity.DnaRun;

import java.util.List;
import java.util.Set;

public interface DnaRunDao {

    List<DnaRun> getDnaRuns(Integer pageSize,
                            Integer rowOffset,
                            Integer dnaRunId,
                            String dnaRunName,
                            Integer datasetId,
                            Integer experimentId,
                            Integer dnaSampleId,
                            String dnaSampleName,
                            Integer germplasmId,
                            String germplasmName,
                            boolean fetchAssociations);

    List<DnaRun> getDnaRuns(Set<Integer> dnaRunIds,
                            Set<String> dnaRunNames,
                            Set<Integer> dnaSampleIds,
                            Set<String> dnaSampleNames,
                            Set<String> dnaSampleUuids,
                            Set<String> germplasmExternalCodes,
                            Set<Integer> germplasmIds,
                            Set<String> germplasmNames,
                            Set<String> datasetIds,
                            Set<Integer> experimentIds,
                            Integer pageSize,
                            Integer dnaRunIdCursor,
                            Integer rowOffset,
                            boolean fetchAssociations);

    List<DnaRun> getDnaRuns(Integer pageSize,
                            Integer rowOffset,
                            Integer dnaRunId,
                            String dnaRunName,
                            Integer datasetId,
                            Integer experimentId,
                            Integer dnaSampleId,
                            String dnaSampleName,
                            Integer germplasmId,
                            String germplasmName);

    DnaRun getDnaRunById(Integer dnaRunId);

    DnaRun getDnaRunById(Integer dnaRunId, boolean fetchAssociations);

    List<DnaRun> getDnaRunsByDnaRunIdCursor(Integer pageSize,
                                            Integer dnaRunIdCursor,
                                            Integer datasetId);

    List<DnaRun> getDnaRunsByDatasetId(Integer datasetId,
                                       Integer pageSize,
                                       Integer rowOffset);

    List<DnaRun> getDnaRunsByDatasetId(Integer datasetId,
                                       Integer pageSize,
                                       Integer rowOffset,
                                       boolean fetchAssociations);

    List<DnaRun> getDnaRunsByDanRunIds(Set<Integer> dnaRunIds);


    List<DnaRun> getDnaRunsByDnaRunNames(Set<String> dnaRunNames,
                                         Integer experimentId,
                                         Integer pageSize,
                                         Integer rowOffset) throws GobiiDaoException;

}

