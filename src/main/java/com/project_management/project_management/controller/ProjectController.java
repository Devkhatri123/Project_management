package com.project_management.project_management.controller;

import com.project_management.project_management.Dtos.project.CreateProjectDTO;
import com.project_management.project_management.Dtos.project.chat.ChatMessageDTO;
import com.project_management.project_management.exception.project.MaximumProjectCreationLimitReached;
import com.project_management.project_management.exception.project.ProjectNotFound;
import com.project_management.project_management.exception.workspace.WorkSpaceNotFound;
import com.project_management.project_management.model.User;
import com.project_management.project_management.service.ProjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/workspace/project")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ProjectController(final ProjectService projectService, final SimpMessagingTemplate messagingTemplate){
        this.projectService = projectService;
        this.simpMessagingTemplate = messagingTemplate;
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
    @GetMapping("/{project_id}/assignees")
    public ResponseEntity<?> getProjectAssignees(@PathVariable String project_id){
        Map<String, Object> response = new HashMap<>();
        try {
            Set<User> project_assignees = projectService.getProjectAssignees(project_id);
            response.put("assignees", project_assignees);
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        } catch (ProjectNotFound e) {
            log.error("project not found, invalid id: {}", project_id);
            response.put("message", e.getMessage());
            response.put("status", 404);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            log.error("Internal server error in fetching project assignees: {}", e.getMessage());
            response.put("message", "Internal Server error. Try again");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Chat logic

    @MessageMapping("/chat/send")
    public void sendPrivateMessage(@Payload ChatMessageDTO chatMessageDTO){
    }
}
