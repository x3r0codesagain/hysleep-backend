package com.app.octo.model.response;

import lombok.Data;

@Data
public class BaseResponse {
  private String errorCode;
  private String errorMessage;
}
