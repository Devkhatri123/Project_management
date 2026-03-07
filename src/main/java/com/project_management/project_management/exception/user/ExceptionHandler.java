package com.project_management.project_management.exception.user;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e){
       Map<String, Object> res = new HashMap<>();
       res.put("message", "Internal Server error");
       res.put("status", 500);
       return ResponseEntity.internalServerError().body(res);
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
