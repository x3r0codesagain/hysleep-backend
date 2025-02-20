package com.app.octo.controller;

import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.EditPasswordRequest;
import com.app.octo.model.request.EditProfileRequest;
import com.app.octo.model.request.GetUserRequest;
import com.app.octo.model.request.LoginRequest;
import com.app.octo.model.request.RegisterRequest;
import com.app.octo.model.response.UserResponse;
import com.app.octo.security.UserAuthProvider;
import com.app.octo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
  public static final String INTERNAL_ERROR = "Internal Error";

  private final UserService userService;

  private final UserAuthProvider userAuthProvider;


  @PostMapping("/login")
  public ResponseEntity<UserResponse> login(@RequestBody LoginRequest loginRequest) {

    try {
      UserResponse userResponse = userService.login(loginRequest);

      userResponse.setToken(userAuthProvider.generateToken(userResponse.getEmail(),
          userResponse.getUserRole().name()));

      return new ResponseEntity<>(userResponse, HttpStatus.OK);
    } catch (AppException appException) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(appException.getCode().name());
      userResponse.setErrorMessage(appException.getMessage());
      return new ResponseEntity<>(userResponse, appException.getCode());
    } catch (Exception ex) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      userResponse.setErrorMessage("Internal Server Error");
      return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {

    try {
      UserResponse userResponse = userService.register(request);
      userResponse.setToken(userAuthProvider.generateToken(userResponse.getEmail(), userResponse.getUserRole().name()));

      return new ResponseEntity<>(userResponse, HttpStatus.OK);
    } catch (AppException appException) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(appException.getCode().name());
      userResponse.setErrorMessage(appException.getMessage());
      return new ResponseEntity<>(userResponse, appException.getCode());
    } catch (Exception ex) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      userResponse.setErrorMessage(INTERNAL_ERROR);
      return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/register-admin")
  public ResponseEntity<UserResponse> registerAdmin(@RequestBody RegisterRequest request) {

    try {
      UserResponse userResponse = userService.registerAdmin(request);
      userResponse.setToken(userAuthProvider.generateToken(userResponse.getEmail(), userResponse.getUserRole().name()));

      return new ResponseEntity<>(userResponse, HttpStatus.OK);
    } catch (AppException appException) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(appException.getCode().name());
      userResponse.setErrorMessage(appException.getMessage());
      return new ResponseEntity<>(userResponse, appException.getCode());
    } catch (Exception ex) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      userResponse.setErrorMessage(INTERNAL_ERROR);
      return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/register-emp")
  public ResponseEntity<UserResponse> registerEmployee(@RequestBody RegisterRequest request) {

    try {
      UserResponse userResponse = userService.registerEmployee(request);
      userResponse.setToken(userAuthProvider.generateToken(userResponse.getEmail(), userResponse.getUserRole().name()));

      return new ResponseEntity<>(userResponse, HttpStatus.OK);
    } catch (AppException appException) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(appException.getCode().name());
      userResponse.setErrorMessage(appException.getMessage());
      return new ResponseEntity<>(userResponse, appException.getCode());
    } catch (Exception ex) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      userResponse.setErrorMessage(INTERNAL_ERROR);
      return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/public/update")
  public ResponseEntity<UserResponse> updateProfile(@RequestBody EditProfileRequest request) {

    try {
      UserResponse userResponse = userService.editUserProfile(request);
      userResponse.setToken(userAuthProvider.generateToken(userResponse.getEmail(), userResponse.getUserRole().name()));

      return new ResponseEntity<>(userResponse, HttpStatus.OK);
    } catch (AppException appException) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(appException.getCode().name());
      userResponse.setErrorMessage(appException.getMessage());
      return new ResponseEntity<>(userResponse, appException.getCode());
    } catch (Exception ex) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      userResponse.setErrorMessage(INTERNAL_ERROR);
      return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/public/update-password")
  public ResponseEntity<UserResponse> updatePassword(@RequestBody EditPasswordRequest request) {

    try {
      UserResponse userResponse = userService.editPassword(request);
      userResponse.setToken(userAuthProvider.generateToken(userResponse.getEmail(), userResponse.getUserRole().name()));

      return new ResponseEntity<>(userResponse, HttpStatus.OK);
    } catch (AppException appException) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(appException.getCode().name());
      userResponse.setErrorMessage(appException.getMessage());
      return new ResponseEntity<>(userResponse, appException.getCode());
    } catch (Exception ex) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      userResponse.setErrorMessage(INTERNAL_ERROR);
      return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/public/find")
  public ResponseEntity<UserResponse> getUser(@RequestBody GetUserRequest request) {
    try {
      UserResponse response = userService.findByEmail(request.getEmail());

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (AppException appException) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(appException.getCode().name());
      userResponse.setErrorMessage(appException.getMessage());
      return new ResponseEntity<>(userResponse, appException.getCode());
    } catch (Exception ex) {
      UserResponse userResponse = new UserResponse();
      userResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      userResponse.setErrorMessage(INTERNAL_ERROR);
      return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
