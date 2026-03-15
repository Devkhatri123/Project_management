package com.project_management.project_management.controller;

import com.project_management.project_management.Dtos.task.CreateTaskDTO;
import com.project_management.project_management.exception.project.ProjectNotFound;
import com.project_management.project_management.exception.task.StatusNotFound;
import com.project_management.project_management.exception.user.UserNotFound;
import com.project_management.project_management.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/workspace/project/task")
public class TaskController {
    private final TaskService taskService;

    public TaskController(final TaskService taskService){
        this.taskService = taskService;
    }
    @PostMapping("/")
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskDTO createTaskDTO){
        Map<String, Object> response = new HashMap<>();
        try {
            taskService.createTask(createTaskDTO);
            response.put("message", "Task created successfully!");
            response.put("status", 201);
            return ResponseEntity.created(null).body(response);
        } catch (ProjectNotFound | UserNotFound | StatusNotFound e){
            log.error("Resource not found: {}", e.getMessage());
            response.put("message", e.getMessage());
            response.put("status", 404);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(404));
        } catch (RuntimeException e){
            log.error("Internal Server error: {}", e.getMessage());
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
