package com.project_management.project_management.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleError(MethodArgumentNotValidException e){
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
    public Map<String, Object> handleException(Exception e){
        Map<String, Object> res = new HashMap<>();
       res.put("message", "Internal Server error");
       res.put("status", 500);
       return res;
    }
}
