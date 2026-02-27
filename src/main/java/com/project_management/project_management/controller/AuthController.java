package com.project_management.project_management.controller;

import com.project_management.project_management.Dtos.*;
import com.project_management.project_management.enums.TokenExpired;
import com.project_management.project_management.exception.user.EmailAlreadyExists;
import com.project_management.project_management.exception.user.IncorrectEmail;
import com.project_management.project_management.exception.user.IncorrectPassword;
import com.project_management.project_management.exception.user.InvalidSelectedRole;
import com.project_management.project_management.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO){
     Map<String, Object> response = new HashMap<>();
     try{
      authService.register(registerRequestDTO);
      response.put("message", "Registration successful! We've sent a link to your email. If you don't see it in 5 minutes, click to resend.");
      response.put("status", 201);
      return ResponseEntity.created(null).body(response);
     } catch (EmailAlreadyExists | InvalidSelectedRole e){
         response.put("message", e.getMessage());
         response.put("status", 400);
         return ResponseEntity.badRequest().body(response);
     } catch (MessagingException e){
         log.error("Exception in sending email after registration for email: {}", registerRequestDTO.email());
         response.put("message", "Internal Server error. Account can't be created. Try again");
         response.put("status", 500);
         return ResponseEntity.internalServerError().body(response);
     } catch (RuntimeException e){
         log.error("exception in registration: {}", e.getMessage());
         response.put("message", "Internal Server error");
         response.put("status", 500);
         return ResponseEntity.internalServerError().body(response);
     }
    }

    @PostMapping("/verifyEmail")
    public  ResponseEntity<?> verify(@RequestBody VerifyDTO verifyDTO){
     Map<String, Object> response = new HashMap<>();
     try{
         authService.verify(verifyDTO);
         response.put("message", "Verification successful!");
         response.put("status", 200);
         return ResponseEntity.ok().body(response);
     } catch (IncorrectEmail e) {
         log.error("Invalid email for verification: {}", verifyDTO.email());
         response.put("message", e.getMessage());
         response.put("status", 400);
         return ResponseEntity.badRequest().body(response);
     } catch (RuntimeException e){
         log.error("exception in email verification: {}", e.getMessage());
         response.put("message", "Internal Server error. Try again");
         response.put("status", 500);
         return ResponseEntity.internalServerError().body(response);
     }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        Map<String, Object> response = new HashMap<>();
        try{
          response = authService.login(loginRequest);
          response.put("status", 200);
          return ResponseEntity.ok().body(response);
        }catch (IncorrectEmail | IncorrectPassword e){
            log.error(e.getMessage());
            response.put("message", e.getMessage());
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
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
            log.error("Incorrect email. Forget password link can't be created");
            response.put("message",e.getMessage());
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        } catch (MessagingException e){
            log.error("exception in sending forget password link email: {}", e.getMessage());
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
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
        } catch (MessagingException e) {
            log.error("exception in sending password changed email: {}", resetToken);
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @PostMapping("/resendEmail/{email}")
    public ResponseEntity<?> resendEmail(@PathVariable String email ,@RequestParam String emailType){
        Map<String, Object> response = new HashMap<>();
        try {
            authService.resendEmail(email, emailType);
            response.put("message", "email sent successfully!");
            response.put("status", 201);
            return ResponseEntity.created(null).body(response);
        } catch (IncorrectEmail e) {
            log.error("Email cannot be resend because email is not correct: {}", email);
            response.put("message", e.getMessage());
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        } catch (MessagingException e) {
            log.error("Email cannot be resend because there is internal problem in sending email: {}", e.getMessage());
            response.put("message", "Internal server error. Email can't be sent. Try again");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        } catch (RuntimeException e){
            log.error("exception in sending email: {}", e.getMessage());
            response.put("message", "Internal Server error");
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
