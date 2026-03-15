package com.project_management.project_management.controller;

import com.project_management.project_management.Dtos.project.CreateProjectDTO;
import com.project_management.project_management.exception.project.MaximumProjectCreationLimitReached;
import com.project_management.project_management.exception.workspace.WorkSpaceNotFound;
import com.project_management.project_management.service.ProjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/workspace/project")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(final ProjectService projectService){
        this.projectService = projectService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createProject(@Valid @RequestBody CreateProjectDTO createProjectDTO){
        Map<String, Object> response = new HashMap<>();
        try{
            projectService.createProjectInWorkSpace(createProjectDTO);
            response.put("message","project created successfully!");
            response.put("status", 201);
            return ResponseEntity.created(null).body(response);
        } catch (WorkSpaceNotFound e){
            log.error("Error in creating project because workspace not found: {}", e.getMessage());
            response.put("message", e.getMessage());
            response.put("status", 404);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (MaximumProjectCreationLimitReached e){
            log.error("Error in creating project because limit has been reached");
            response.put("message", e.getMessage());
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            log.error("Internal server error in creating project: {}", e.getMessage());
            response.put("message", "Internal Server error. Try again");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
