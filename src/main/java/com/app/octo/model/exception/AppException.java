package com.app.octo.model.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class AppException extends RuntimeException{

  private final HttpStatus code;

  public AppException(String message, HttpStatus code) {
    super(message);
    this.code = code;
  }
}
