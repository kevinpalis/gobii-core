/**
 * ProjectDao.java
 * 
 * V3 API Project DAO interface
 * @author Rodolfo N. Duldulao, Jr.
 */
package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;

import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Project;

public interface ProjectDao {

    List<Project> getProjects(
        Integer pageNum,
        Integer pageSize,
        String piContactUserName
    );

    Project createProject(
        Project projectToBeCreated
    ) throws Exception;

    Project patchProject(
        Project projectToBePatched
    ) throws Exception;
    
    List<Cv> getCvProperties(
        Integer pageNum,
        Integer pageSize  
    );

    Project getProject(Integer projectId) throws GobiiDaoException;
    
    void deleteProject(Project project) throws Exception;
}