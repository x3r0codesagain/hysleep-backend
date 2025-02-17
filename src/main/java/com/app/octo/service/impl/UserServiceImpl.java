package com.app.octo.service.impl;

import com.app.octo.model.User;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.enums.UserRole;
import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.LoginRequest;
import com.app.octo.model.request.RegisterRequest;
import com.app.octo.model.response.UserResponse;
import com.app.octo.repository.UserRepository;
import com.app.octo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder; //to avoid password saved in plain text - hashed
  private final Mapper mapper;

  @Override
  public UserResponse findByEmail(String email) {
    User user = userRepository.findByEmail(email).orElseThrow(()
        -> new AppException(ErrorCodes.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
    return mapper.map(user, UserResponse.class);
  }

  @Override
  public UserResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()
        -> new AppException(ErrorCodes.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));


    if(passwordEncoder.matches(
        CharBuffer.wrap(loginRequest.getPassword()), user.getPassword())){
      UserResponse userResponse = mapper.map(user, UserResponse.class);
      return userResponse;
    }

    throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
  }

  @Override
  public UserResponse register(RegisterRequest registerRequest) {
    if (Objects.isNull(registerRequest) || StringUtils.isBlank(registerRequest.getFirstName())
        || StringUtils.isBlank(registerRequest.getLastName()) || Objects.isNull(
        registerRequest.getPassword()) || registerRequest.getPassword().length < 1
        || StringUtils.isBlank(registerRequest.getEmail())) {
      throw new AppException("Incomplete Request", HttpStatus.BAD_REQUEST);
    }

    User userFromDB = userRepository.findByEmail(registerRequest.getEmail())
        .orElseGet(() -> null);

    if(Objects.nonNull(userFromDB)) {
      throw new AppException("Account Exists", HttpStatus.BAD_REQUEST);
    }



    User user = mapper.map(registerRequest, User.class);
    setAdditionalDataToUser(user, UserRole.ROLE_USER);


    user.setPassword(passwordEncoder.encode(CharBuffer.wrap(registerRequest.getPassword()))); //Store in hashed
    
    userRepository.save(user);

    UserResponse userResponse = mapper.map(user, UserResponse.class);
    return userResponse;
  }

  @Override
  public UserResponse registerAdmin(RegisterRequest registerRequest) {
    User userFromDB = userRepository.findByEmail(registerRequest.getEmail())
        .orElseGet(() -> null);

    if(Objects.nonNull(userFromDB)) {
      throw new AppException("Account Exists", HttpStatus.BAD_REQUEST);
    }

    User user = mapper.map(registerRequest, User.class);
    setAdditionalDataToUser(user, UserRole.ROLE_ADMIN);

    user.setPassword(passwordEncoder.encode(CharBuffer.wrap(registerRequest.getPassword()))); //Store in hashed

    userRepository.save(user);

    UserResponse userResponse = mapper.map(user, UserResponse.class);
    return userResponse;
  }

  private void setAdditionalDataToUser(User user, UserRole roleUser) {
    user.setUserRole(roleUser);
  }
}
