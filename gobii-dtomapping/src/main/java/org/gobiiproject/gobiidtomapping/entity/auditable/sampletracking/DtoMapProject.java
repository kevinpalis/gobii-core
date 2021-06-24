package org.gobiiproject.gobiidtomapping.entity.auditable.sampletracking;

import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiidtomapping.entity.auditable.DtoMap;
import org.gobiiproject.gobiimodel.dto.auditable.sampletracking.ProjectDTO;

import java.util.List;

public interface DtoMapProject extends DtoMap<ProjectDTO> {
    ProjectDTO create(ProjectDTO projectDTO) throws GobiiDtoMappingException;
    ProjectDTO replace(Integer projectId, ProjectDTO projectDTO) throws GobiiDtoMappingException;
    ProjectDTO get(Integer projectId) throws GobiiDtoMappingException;
    List<ProjectDTO> getList() throws GobiiDtoMappingException;
    List<ProjectDTO> getList(Integer pageToken, Integer pageSize) throws GobiiDtoMappingException;
    List<ProjectDTO> getProjectsForLoadedDatasets() throws GobiiDtoMappingException;
}
