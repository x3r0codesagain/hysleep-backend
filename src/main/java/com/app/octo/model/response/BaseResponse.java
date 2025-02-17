package com.app.octo.model.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BaseResponse {
  private String errorCode;
  private String errorMessage;
}
