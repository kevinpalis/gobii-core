package org.gobiiproject.gobiidtomapping.entity.auditable;

import java.util.List;
import org.gobiiproject.gobiidtomapping.core.GobiiDtoMappingException;
import org.gobiiproject.gobiimodel.dto.auditable.ProjectDTO;

/**
 * Created by Phil on 4/6/2016.
 */
public interface DtoMapProject extends DtoMap<ProjectDTO> {

    ProjectDTO create(ProjectDTO projectDTO) throws GobiiDtoMappingException;
    ProjectDTO replace(Integer projectId, ProjectDTO projectDTO) throws GobiiDtoMappingException;
    ProjectDTO get(Integer projectId) throws GobiiDtoMappingException;
    List<ProjectDTO> getList() throws GobiiDtoMappingException;
    List<ProjectDTO> getProjectsForLoadedDatasets() throws GobiiDtoMappingException;

}
