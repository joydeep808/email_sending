package com.interview.interview.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.interview.interview.util.Response;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class Handler {

  @ExceptionHandler(exception = ConstraintViolationException.class)
  public ResponseEntity<Response<Map<String, String>>> handleNotFoundException(ConstraintViolationException e) {
    Map<String, String> errors = new HashMap<>();
    e.getConstraintViolations().forEach(violation -> {
      errors.put(violation.getPropertyPath().toString(), violation.getMessage());
    });
    return new Response<Map<String, String>>().sendSuccessResponse(400, "Validation Error", errors);
  }

}
