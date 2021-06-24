/**
 * ProjectsController.java
 * Gobii API endpoint controllers for projects
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import static org.gobiiproject.gobiimodel.config.Roles.CURATOR;

import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.ProjectService;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.ProjectDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiiweb.security.CropAuth;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Scope(value = "request")
@RestController
@RequestMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3)
@Api()
@CrossOrigin
@Slf4j
@AllArgsConstructor
public class ProjectsController {
    
    private ProjectService projectService;

    /**
     * getProjectsList 
     * 
     * Gets list of projects
     * 
     * @param page 0-based page of data used in conjunction with pageSize
     * @param pageSize number of items in response list.
     * @return
     */

    @GetMapping("/projects")
    @ResponseBody 
    public ResponseEntity<BrApiMasterListPayload<ProjectDTO>> getProjectsList(
            @RequestParam(required=false, defaultValue = "0") Integer page,
            @RequestParam(required=false, defaultValue = "1000") Integer pageSize,
            @RequestParam(required=false) String piContactId,
            @PathVariable String cropType) throws Exception {
        log.debug("Querying projects List");
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);

        PagedResult<ProjectDTO> pagedResult =  projectService.getProjects(
            Math.max(0, page),
            pageSizeToUse,
            piContactId,
            cropType
        );
        BrApiMasterListPayload<ProjectDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);   
        return ResponseEntity.ok(payload);
    }

    /**
     * createProject
     * 
     * Create new project
     * @since 2020-03-13
     */
    @CropAuth(CURATOR)
    @PostMapping("/projects")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ProjectDTO>> createProject(
            @RequestBody @Validated(ProjectDTO.Create.class) final ProjectDTO project,
            BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);

        //Get the current user
        String userName = AuthUtils.getCurrentUser();

        ProjectDTO createdDTO = projectService.createProject(project, userName);
        BrApiMasterPayload<ProjectDTO> result = ControllerUtils.getMasterPayload(createdDTO);
        return ResponseEntity.created(null).body(result);
    }

    /**
     * Get Project endpoint handler
     * 
     * @param projectId
     * @return
     * @throws Exception
     */
    @GetMapping("/projects/{projectId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ProjectDTO>> getProject(
        @PathVariable Integer projectId
    ) throws Exception {
        ProjectDTO project = projectService.getProject(projectId);
        if (project == null) {
            throw new NullPointerException("Project does not exist");
        }
        BrApiMasterPayload<ProjectDTO> result = ControllerUtils.getMasterPayload(project);
        return ResponseEntity.ok(result);
    }


    /**
     * For Patch Project
     * @return
     */
    @CropAuth(CURATOR)
    @PatchMapping("/projects/{projectId}")
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload<ProjectDTO>> patchProject(
        @PathVariable Integer projectId,
        @RequestBody @Validated(ProjectDTO.Update.class) final ProjectDTO project,
        BindingResult bindingResult
    ) throws Exception {
        ControllerUtils.checkBindingErrors(bindingResult);
        String userName = AuthUtils.getCurrentUser();
        ProjectDTO dto = projectService.patchProject(projectId, project, userName);
        BrApiMasterPayload<ProjectDTO> payload = ControllerUtils.getMasterPayload(dto);
        return ResponseEntity.ok(payload);
    }

    /**
     * Delete Project
     * 
     * @return
     * @throws Exception
     */
    @CropAuth(CURATOR)
    @DeleteMapping("/projects/{projectId}")
    @ResponseBody
    public ResponseEntity<String> deleteProject(
        @PathVariable Integer projectId
    ) throws Exception {
        projectService.deleteProject(projectId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }


    /**
     * List Project Properties
     * 
     */
    @GetMapping("/project/properties")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<CvPropertyDTO>> getProjectProperties(
            @RequestParam(required=false, defaultValue = "0") Integer page,
            @RequestParam(required=false, defaultValue = "1000") Integer pageSize) throws Exception {
        log.debug("Querying project properties List");
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<CvPropertyDTO> pagedResult =  projectService.getProjectProperties(
            Math.max(0, page),
            pageSizeToUse
        );
        BrApiMasterListPayload<CvPropertyDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);   
        return ResponseEntity.ok(payload);
    }


}
