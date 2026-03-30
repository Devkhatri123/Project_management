package com.project_management.project_management.controller;

import com.project_management.project_management.Dtos.task.ChangeStatusDTO;
import com.project_management.project_management.Dtos.task.CreateTaskDTO;
import com.project_management.project_management.Dtos.task.TaskDTO;
import com.project_management.project_management.exception.LimitReached;
import com.project_management.project_management.exception.project.ProjectNotFound;
import com.project_management.project_management.exception.task.InvalidStatusSelected;
import com.project_management.project_management.exception.task.StatusNotFound;
import com.project_management.project_management.exception.task.TaskCreationLimitHasBeenReached;
import com.project_management.project_management.exception.task.TaskNotFound;
import com.project_management.project_management.exception.user.UserNotFound;
import com.project_management.project_management.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
    public ResponseEntity<?> createTask(@Valid @RequestPart CreateTaskDTO createTaskDTO, @RequestPart List<MultipartFile> attachments){
        Map<String, Object> response = new HashMap<>();
        try {
            taskService.createTask(createTaskDTO, attachments);
            response.put("message", "Task created successfully!");
            response.put("status", 201);
            return ResponseEntity.created(null).body(response);
        } catch (ProjectNotFound | UserNotFound | StatusNotFound e){
            log.error("Resource not found for creating task: {}", e.getMessage());
            response.put("message", e.getMessage());
            response.put("status", 404);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(404));
        } catch (TaskCreationLimitHasBeenReached | LimitReached e){
            log.error("Limit has been reached: {}", e.getMessage());
            response.put("message", e.getMessage());
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        } catch (IOException e){
            log.error("Internal Server error. Attachment cannot be uploaded. Try again: {}", e.getMessage());
            response.put("message", "Internal Server error. Attachment cannot be uploaded");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        } catch (RuntimeException e){
            log.error("Internal Server error. Task cannot be created. Try again: {}", e.getMessage());
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @PutMapping("/{task_id}/status")
    public ResponseEntity<?> changeTaskStatus(@PathVariable String task_id, @Valid @RequestBody ChangeStatusDTO changeStatusDTO){
       Map<String, Object> response = new HashMap<>();
       try {
           taskService.changeTaskStatus(task_id, changeStatusDTO);
           response.put("message", "Task status changed successfully!");
           response.put("status", 200);
           return ResponseEntity.ok().body(response);
       } catch (TaskNotFound | StatusNotFound e) {
           log.error("Resource not found while updating task status");
           response.put("message", e.getMessage());
           response.put("status", 404);
          return new ResponseEntity<>(response, HttpStatusCode.valueOf(404));
       } catch (InvalidStatusSelected e){
           log.error("status cannot changed. Mistake from user end: {}", e.getMessage());
           response.put("message", e.getMessage());
           response.put("status", 400);
           return ResponseEntity.badRequest().body(response);
       } catch (RuntimeException e) {
           log.error("Internal Server error in changing task status: {}", e.getMessage());
           response.put("message", "Internal Server error. Task status cannot be changed. Try again");
           response.put("status", 500);
           return ResponseEntity.internalServerError().body(response);
       }
    }
    @GetMapping("/{task_id}")
    public ResponseEntity<?> getTaskById(@PathVariable String task_id){
        Map<String, Object> response = new HashMap<>();
        try {
            TaskDTO taskDTO = taskService.getTaskById(task_id);
            response.put("task", taskDTO);
            response.put("status", 200);
            return ResponseEntity.ok(response);
        } catch (TaskNotFound e) {
            log.error("Task not found of id: {}", task_id);
            response.put("message", e.getMessage());
            response.put("status", 404);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(404));
        } catch (RuntimeException e) {
            log.error("Internal Server error in fetching task: {}", e.getMessage());
            response.put("message", "Internal Server error. Task not found. Try again");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @DeleteMapping("/{task_id}")
    public ResponseEntity<?> deleteById(@PathVariable String task_id){
        Map<String, Object> response = new HashMap<>();
        try {
            taskService.deleteTask(task_id);
            response.put("message", "task deleted successfully");
            response.put("status", 200);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Internal Server error in deleting task: {}", e.getMessage());
            response.put("message", "Internal Server error. Task couldn't be deleted. Try again");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
