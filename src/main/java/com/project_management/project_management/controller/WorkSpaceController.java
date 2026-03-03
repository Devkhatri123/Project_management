package com.project_management.project_management.controller;

import com.project_management.project_management.Dtos.workspace.CreateWorkSpaceDTO;
import com.project_management.project_management.Dtos.workspace.UpdateWorkSpace;
import com.project_management.project_management.exception.user.workspace.MaximumWorkSpaceCreationLimitReached;
import com.project_management.project_management.exception.user.workspace.WorkSpaceIsLocked;
import com.project_management.project_management.exception.user.workspace.WorkSpaceNotFound;
import com.project_management.project_management.service.WorkSpaceService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/workspace")
public class WorkSpaceController {
    private final WorkSpaceService workSpaceService;
    @Autowired
    public WorkSpaceController(final WorkSpaceService workSpaceService){
        this.workSpaceService = workSpaceService;
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createWorkSpace(@Valid @RequestBody CreateWorkSpaceDTO createWorkSpaceDTO){
        Map<String, Object> response = new HashMap<>();
        try{
         workSpaceService.createWorkSpace(createWorkSpaceDTO);
         response.put("message","workspace created successfully!");
         response.put("status", 201);
         return ResponseEntity.created(null).body(response);
     } catch (MaximumWorkSpaceCreationLimitReached e) {
         response.put("message", e.getMessage());
         response.put("status", 400);
         return ResponseEntity.badRequest().body(response);
     } catch (RuntimeException e) {
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @PatchMapping("/")
    public ResponseEntity<?> updateWorkSpace(@Valid @RequestBody UpdateWorkSpace updateWorkSpace){
        Map<String, Object> response = new HashMap<>();
        try {
            workSpaceService.updateWorkSpace(updateWorkSpace);
            response.put("message", "workspace deleted successfully!");
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        } catch (WorkSpaceNotFound e) {
            log.error("workspace not found of key: {}", updateWorkSpace.workspace_key());
            response.put("message", e.getMessage());
            response.put("status", 404);
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        } catch (WorkSpaceIsLocked e){
            log.error("workspace is locked, so this workspace can't updated of key:{}", updateWorkSpace.workspace_key());
            response.put("message", e.getMessage());
            response.put("status", 404);
            return ResponseEntity.badRequest().body(response);
        }
    }
    @DeleteMapping("/{workspace_key}")
    public ResponseEntity<Map<String, Object>> deleteWorkSpace(@PathVariable String workspace_key){
        Map<String, Object> response = new HashMap<>();
        try {
            workSpaceService.deleteWorkSpace(workspace_key);
            response.put("message", "workspace deleted successfully");
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            response.put("message", "Something went wrong in deleting workspace. Try again");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
