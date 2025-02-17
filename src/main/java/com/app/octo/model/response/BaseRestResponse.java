package com.app.octo.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRestResponse {
  private String errorMessage;
  private String errorCode;
  private boolean status;
}
