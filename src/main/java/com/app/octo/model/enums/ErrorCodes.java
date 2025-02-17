package com.app.octo.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {
  USER_NOT_FOUND("User is not available in database."),
  INCOMPLETE_DATA("Recheck data input."),
  DATA_EXISTS("The following data has been inputted previously."),
  UNABLE_TO_CONFIRM("There is an error during confirmation please retry."),
  DATA_NOT_FOUND("No data available"),
  INVALID_REQUEST("Invalid Request");

  private String message;
}
