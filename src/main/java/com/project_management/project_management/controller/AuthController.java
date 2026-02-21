package com.project_management.project_management.controller;

import com.project_management.project_management.Dtos.ErrorResponse;
import com.project_management.project_management.Dtos.ForgetPasswordDTO;
import com.project_management.project_management.Dtos.LoginRequest;
import com.project_management.project_management.Dtos.RegisterRequestDTO;
import com.project_management.project_management.enums.TokenExpired;
import com.project_management.project_management.exception.user.EmailAlreadyExists;
import com.project_management.project_management.exception.user.IncorrectEmail;
import com.project_management.project_management.exception.user.IncorrectPassword;
import com.project_management.project_management.exception.user.InvalidSelectedRole;
import com.project_management.project_management.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(final AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequestDTO){
     Map<String, Object> response = new HashMap<>();
     try{
      authService.register(registerRequestDTO);
      response.put("message", "Registration successful! Verification code sent to email");
      response.put("status", 201);
      return ResponseEntity.created(null).body(response);
     } catch (EmailAlreadyExists | InvalidSelectedRole e){
         response.put("message", e.getMessage());
         response.put("status", 400);
       return ResponseEntity.badRequest().body(response);
     } catch (RuntimeException e){
         log.error("exception in registration: {}", e.getMessage());
         response.put("message", "Internal Server error");
         response.put("status", 500);
         return ResponseEntity.internalServerError().body(response);
     }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
                                   @RequestHeader(name = "time_zone") String timeZone){
        Map<String, Object> response = new HashMap<>();
        try{
          response = authService.login(loginRequest);
          response.put("status", 200);
          return ResponseEntity.ok().body(response);
        }catch (IncorrectEmail | IncorrectPassword e){
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e){
            log.error("exception in login: {}", e.getMessage());
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(){
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("user", authService.getLoggedInUser());
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        }catch (RuntimeException e){
            log.error("exception in fetching loggedIn user: {}", e.getMessage());
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshJwtToken(@RequestBody String token,
                                @RequestHeader(name = "time_zone") String timeZone){
        Map<String, Object> response = new HashMap<>();
        try{
            response = authService.refreshToken(token, timeZone);
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        }catch (TokenExpired e){
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 400);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e){
            log.error("exception in refreshing token: {}", e.getMessage());
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @PostMapping("/forgetPasswordToken/{email}")
    public ResponseEntity<?> generateForgetPasswordToken(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            authService.generateForgetPasswordToken(email);
            response.put("message", "Your forget password link has been sent to your email");
            response.put("status", 201);
            return ResponseEntity.created(null).body(response);
        } catch (IncorrectEmail e) {
            log.error(e.getMessage(), email);
            response.put("message",e.getMessage());
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            log.error("exception in creating forget password token: {}", e.getMessage());
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @PatchMapping("/forgetPassword/{resetToken}")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordDTO forgetPasswordDTO, @PathVariable String resetToken){
        Map<String, Object> response = new HashMap<>();
        try{
            authService.forgetPassword(forgetPasswordDTO, resetToken);
            response.put("message", "Your password has been rested!");
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        }catch (TokenExpired e){
            log.error(e.getMessage(), resetToken);
            response.put("message",e.getMessage());
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e){
            log.error("exception in reseting the password against provided token: {}", resetToken);
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
