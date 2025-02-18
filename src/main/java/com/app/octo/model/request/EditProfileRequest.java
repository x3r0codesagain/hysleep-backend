package com.app.octo.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileRequest {
  private String currentEmail;
  private String firstName;
  private String lastName;
  private String email;
  private char[] currentPassword;
  private char[] password;
}
