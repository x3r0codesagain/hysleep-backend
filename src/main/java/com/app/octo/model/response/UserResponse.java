package com.app.octo.model.response;

import com.app.octo.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse extends BaseResponse{

  private String firstName;
  private String lastName;
  private String email;
  private UserRole userRole;
  private String token;
}
