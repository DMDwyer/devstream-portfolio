package com.dmdwyer.devstream.common;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@ControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,Object>> badRequest(IllegalArgumentException ex){
    return ResponseEntity.badRequest().body(Map.of("error","BAD_REQUEST","message",ex.getMessage()));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String,Object>> notFound(NoSuchElementException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","NOT_FOUND","message",ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,Object>> invalid(MethodArgumentNotValidException ex){
    var errors = ex.getBindingResult().getFieldErrors().stream()
      .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
      .toList();
    return ResponseEntity.badRequest().body(Map.of("error","VALIDATION_FAILED","details",errors));
  }
}
