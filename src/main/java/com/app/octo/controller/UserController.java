package com.app.octo.controller;

import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.LoginRequest;
import com.app.octo.model.request.RegisterRequest;
import com.app.octo.model.response.BaseResponse;
import com.app.octo.model.response.UserResponse;
import com.app.octo.security.UserAuthProvider;
import com.app.octo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
  @Autowired
  private UserService userService;

  @Autowired
  private UserAuthProvider userAuthProvider;


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
      userResponse.setErrorMessage("Internal Error");
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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
      userResponse.setErrorMessage("Internal Error");
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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
      userResponse.setErrorMessage("Internal Error");
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
