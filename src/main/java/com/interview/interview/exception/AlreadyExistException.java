package com.interview.interview.exception;

public class AlreadyExistException extends BaseException {
  public AlreadyExistException(String message) {
    super(message, 409);
  }
}
