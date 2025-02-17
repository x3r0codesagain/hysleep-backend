package com.app.octo.security;

import com.app.octo.dto.ErrorInfoDTO;
import com.app.octo.model.exception.AppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(value = {AppException.class})
  @ResponseBody
  public ResponseEntity<ErrorInfoDTO> handleException(AppException appException) {
    return ResponseEntity.status(appException.getCode()).body(ErrorInfoDTO.builder()
        .errorCode(appException.getMessage())
        .errorMessage(appException.getMessage()).build());
  }
}
