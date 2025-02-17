package com.app.octo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorInfoDTO {

  private String errorCode;
  private String errorMessage;
}
