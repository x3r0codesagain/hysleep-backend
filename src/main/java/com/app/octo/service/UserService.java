package com.app.octo.service;

import com.app.octo.model.request.EditPasswordRequest;
import com.app.octo.model.request.EditProfileRequest;
import com.app.octo.model.request.LoginRequest;
import com.app.octo.model.request.RegisterRequest;
import com.app.octo.model.response.UserResponse;

public interface UserService {
  UserResponse findByEmail(String email);
  UserResponse register(RegisterRequest registerRequest);
  UserResponse login(LoginRequest loginRequest);
  UserResponse registerAdmin(RegisterRequest registerRequest);
  UserResponse registerEmployee(RegisterRequest registerRequest);
  UserResponse editUserProfile(EditProfileRequest editProfileRequest);
  UserResponse editPassword(EditPasswordRequest editPasswordRequest);
}
