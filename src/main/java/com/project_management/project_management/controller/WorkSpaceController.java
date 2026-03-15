package com.project_management.project_management.controller;

import com.project_management.project_management.Dtos.workspace.CreateWorkSpaceDTO;
import com.project_management.project_management.Dtos.workspace.InvitationDTO;
import com.project_management.project_management.Dtos.workspace.UpdateWorkSpace;
import com.project_management.project_management.exception.Token.TokenExpired;
import com.project_management.project_management.exception.user.UserNotFound;
import com.project_management.project_management.exception.workspace.*;
import com.project_management.project_management.service.WorkSpaceService;
import jakarta.mail.MessagingException;
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
            log.error("something went wrong in creating workspace: {}", e.getMessage());
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
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            log.error("something went wrong in updating workspace: {}", e.getMessage());
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
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
            log.error("Something went wrong in deleting workspace: {}", e.getMessage());
            response.put("message", "Something went wrong in deleting workspace. Try again");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteUserToWorkSpaceThroughEmail(@Valid @RequestBody InvitationDTO invitationDTO) {
        Map<String, Object> response = new HashMap<>();
        try{
        workSpaceService.inviteUserToWorkSpace(invitationDTO);
        response.put("message", "Invitation sent successfully!");
        log.info("Invitation sent successfully! to user: {}", invitationDTO.userToBeInvitedEmail());
        response.put("status", 201);
        return ResponseEntity.created(null).body(response);
      } catch (WorkSpaceIsLocked | MaximumWorkSpaceEmployeesLimitHasBeenReached e){
        response.put("message", e.getMessage());
        response.put("status", 400);
        log.error("Invitation cannot be sent because: {}", e.getMessage());
        return ResponseEntity.badRequest().body(response);
      } catch (UserHasAlreadyJoinedTheWorkSpace e){
        response.put("message", e.getMessage());
        response.put("status", 400);
        log.error("User of email: {} has already joined the workspace", invitationDTO.userToBeInvitedEmail());
        return ResponseEntity.badRequest().body(response);
      } catch (UserNotFound e){
       log.error("something went wrong in finding user of email: {}, from database", invitationDTO.userToBeInvitedEmail());
       response.put("message", e.getMessage());
       response.put("status", 404);
       return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
      } catch (WorkSpaceNotFound e) {
        response.put("message", e.getMessage());
        response.put("status", 404);
        log.error("Invitation cannot be sent because workspace not found of key: {}", invitationDTO.workspace_key());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
      } catch (MessagingException e) {
        response.put("message", "Invitation cannot be sent, having issue internally in sending invitation email to user. Try again");
        response.put("status", 500);
        log.error("Internal server error in sending email to user of email: {} and error message is: {}", invitationDTO.userToBeInvitedEmail(), e.getMessage());
        return ResponseEntity.internalServerError().body(response);
      } catch (RuntimeException e) {
        log.error("Something went wrong in inviting user to workspace: {}", e.getMessage());
        response.put("message", "Internal server error. Try again");
        response.put("status", 500);
        return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/{work_space_key}/join/{userToBeJoinedEmail}/invitation/{invitation_link}")
    public ResponseEntity<?> joinWorkSpaceFromInvitationLink(@PathVariable String work_space_key, @PathVariable String userToBeJoinedEmail, @PathVariable String invitation_link){
       Map<String, Object> response = new HashMap<>();
        try {
           workSpaceService.joinWorkSpaceFromInvitationLink(work_space_key, userToBeJoinedEmail, invitation_link);
           response.put("message", "Workspace joined successfully!");
           response.put("status", 200);
           log.info("Workspace joined successfully! of email: {}", userToBeJoinedEmail);
           return ResponseEntity.ok().body(response);
        } catch (WorkSpaceNotFound | UserNotFound e){
           log.error("Error in joining workspace because resource not found: {}", e.getMessage());
           response.put("message", e.getMessage());
           response.put("status", 404);
           return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
       } catch (MaximumWorkSpaceEmployeesLimitHasBeenReached | WorkSpaceInvitationLinkNotFound | TokenExpired | UserHasAlreadyJoinedTheWorkSpace e) {
            log.error("Error in joining workspace: {}", e.getMessage());
            response.put("message", e.getMessage());
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
       } catch (RuntimeException e) {
            log.error("Internal server error in joining workspace: {}", e.getMessage());
            response.put("message", "Internal Server error. Try again");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
       }
       }
       @GetMapping("/{work_space_key}")
       public void getWorkSpaceByKey(@PathVariable String work_space_key) throws WorkSpaceNotFound {
        workSpaceService.getWorkSpaceBy_WorkSpace_Key(work_space_key);
       }
}
