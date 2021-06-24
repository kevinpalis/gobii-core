/**
 * GobiiProjectService.java
 * 
 * Service interface for Gobii v3 project endpoints
 * @author Rodolfo N. Duldulao, Jr.
 */
package org.gobiiproject.gobiidomain.services.gdmv3;

import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.ProjectDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
   
public interface ProjectService{
    ProjectDTO getProject(Integer projectId) throws Exception;
    PagedResult<ProjectDTO>  getProjects(Integer page,
                                         Integer pageSize,
                                         String piContactId,
                                         String cropType) throws Exception;
    ProjectDTO createProject(ProjectDTO request, String createdBy) throws Exception;
    ProjectDTO patchProject(Integer projectId, ProjectDTO request, String editedBy) throws Exception;
    void deleteProject(Integer projectId) throws Exception;
    String getDefaultProjectEditor();

    //
    PagedResult<CvPropertyDTO> getProjectProperties(Integer page, Integer pageSize) throws Exception;
}