package com.interview.interview.util;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response<T> {
  private String message;
  private T data;
  private int statusCode;
  private boolean isSuccess;

  public ResponseEntity<Response<T>> sendSuccessResponse(int statusCode, String message, T data) {
    this.data = data;
    this.message = message;
    this.statusCode = statusCode;
    this.isSuccess = true;
    return sendSuccessResponse();
  }

  public ResponseEntity<Response<T>> sendErrorResponse(int statusCode, String message) {
    this.message = message;
    this.statusCode = statusCode;
    this.isSuccess = false;
    return sendSuccessResponse();
  }

  private ResponseEntity<Response<T>> sendSuccessResponse() {
    return ResponseEntity.status(this.statusCode).body(this);
  }

}
