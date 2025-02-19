package com.app.octo.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditPasswordRequest {
  private String currentEmail;
  private char[] currentPassword;
  private char[] password;
}
