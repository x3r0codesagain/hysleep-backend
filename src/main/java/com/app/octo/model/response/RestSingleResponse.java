package com.app.octo.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestSingleResponse<T extends BaseResponse> extends BaseRestResponse{

  private T value;

  public RestSingleResponse(String errorMessage, String errorCode, boolean status, T value) {
    super(errorMessage, errorCode, status);
    this.value = value;
  }
}
