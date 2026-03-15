package com.project_management.project_management.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationError(MethodArgumentNotValidException e){
        Map<String, Object> res = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
          String message = error.getDefaultMessage();
          res.put("message", message);
          res.put("status", 400);
        });
        return res;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?> handleConstraintViolationException(DataIntegrityViolationException ex){
        Map<String, Object> res = new HashMap<>();
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";
        // unique_username
        if(rootMsg.contains("user.unique_email")) {
            res.put("message", "user with this email already exist");
        } else if(rootMsg.contains("user.unique_username")){
            res.put("message", "user with this username already exist. Choose other username");
        } else {
            res.put("message", "Database constraint error");
        }
        res.put("status", 409);
        return ResponseEntity.badRequest().body(res);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> expiredJwtException(ExpiredJwtException e){
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Jwt expired. Login again");
        res.put("status", 401);
        return ResponseEntity.status(401).body(res);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> MalformedJwtException(MalformedJwtException e){
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Jwt token has been tempered. Login again");
        res.put("status", 401);
        return new ResponseEntity<>(res,HttpStatus.UNAUTHORIZED);
    }
}
