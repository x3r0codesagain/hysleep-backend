package com.app.octo.service;

import com.app.octo.model.User;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.enums.UserRole;
import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.EditPasswordRequest;
import com.app.octo.model.request.EditProfileRequest;
import com.app.octo.model.request.LoginRequest;
import com.app.octo.model.request.RegisterRequest;
import com.app.octo.model.response.UserResponse;
import com.app.octo.repository.UserRepository;
import com.app.octo.service.impl.UserServiceImpl;
import org.dozer.Mapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.CharBuffer;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserServiceTest {
  public static final String FIRST_NAME = "NAME 1";
  public static final String LAST_NAME = "NAME 2";
  public static final String EMAIL = "EMAIL";
  public static final String PASSWORD = "password";
  public static final Long ID = 1L;
  public static final String TOKEN = "token";

  @InjectMocks
  private UserServiceImpl userServiceImpl;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private Mapper mapper;

  private User user;
  private UserResponse userResponse;
  private UserResponse userResponseAdmin;
  private UserResponse editedUserResponse;
  private LoginRequest loginRequest;
  private RegisterRequest registerRequest;
  private EditProfileRequest editProfileRequest;
  private User editedUser;
  private User existingUser;
  private EditPasswordRequest editPasswordRequest;
  private User newPassUser;

  @Test
  void findUser_success() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(mapper.map(user, UserResponse.class)).thenReturn(userResponse);

    UserResponse response = this.userServiceImpl.findByEmail(EMAIL);
    assertNotNull(response);
    assertEquals(FIRST_NAME, response.getFirstName());
    assertEquals(LAST_NAME, response.getLastName());
    assertEquals(UserRole.ROLE_USER, response.getUserRole());
    assertEquals(TOKEN, response.getToken());

    verify(mapper).map(user, UserResponse.class);
    verify(userRepository).findByEmail(EMAIL);
  }

  @Test
  void findUserNoUserFound_throwAppException() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    try {
      this.userServiceImpl.findByEmail(EMAIL);
    } catch (AppException e) {
      assertEquals(ErrorCodes.USER_NOT_FOUND.getMessage(), e.getMessage());
      assertEquals(HttpStatus.NOT_FOUND, e.getCode());
    }
    verify(userRepository).findByEmail(EMAIL);
  }

  @Test
  void loginUser_success() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.TRUE);
    when(mapper.map(user, UserResponse.class)).thenReturn(userResponse);

    UserResponse response = this.userServiceImpl.login(loginRequest);
    assertNotNull(response);
    assertEquals(FIRST_NAME, response.getFirstName());
    assertEquals(LAST_NAME, response.getLastName());
    assertEquals(UserRole.ROLE_USER, response.getUserRole());
    assertEquals(TOKEN, response.getToken());

    verify(mapper).map(user, UserResponse.class);
    verify(userRepository).findByEmail(EMAIL);
    verify(passwordEncoder).matches(any(), any());
  }

  @Test
  void loginUserPasswordInvalid_throwAppException() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.FALSE);

    try {
      this.userServiceImpl.login(loginRequest);
    } catch (AppException e) {
      assertEquals("Invalid password", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }
    verify(userRepository).findByEmail(EMAIL);
    verify(passwordEncoder).matches(any(), any());
  }

  @Test
  void loginUserUserNotFound_throwAppException() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    try {
      this.userServiceImpl.login(loginRequest);
    } catch (AppException e) {
      assertEquals(ErrorCodes.USER_NOT_FOUND.getMessage(), e.getMessage());
      assertEquals(HttpStatus.NOT_FOUND, e.getCode());
    }
    verify(userRepository).findByEmail(EMAIL);
  }

  @Test
  void registerUser_success() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    when(mapper.map(user, UserResponse.class)).thenReturn(userResponse);
    when(mapper.map(registerRequest, User.class)).thenReturn(user);
    when(passwordEncoder.encode(CharBuffer.wrap(PASSWORD))).thenReturn("encoded");
    when(userRepository.save(user)).thenReturn(user);

    UserResponse response = this.userServiceImpl.register(registerRequest);
    assertNotNull(response);
    assertEquals(FIRST_NAME, response.getFirstName());
    assertEquals(LAST_NAME, response.getLastName());
    assertEquals(UserRole.ROLE_USER, response.getUserRole());
    assertEquals(TOKEN, response.getToken());

    verify(mapper).map(user, UserResponse.class);
    verify(mapper).map(registerRequest, User.class);
    verify(userRepository).findByEmail(EMAIL);
    verify(userRepository).save(user);
    verify(passwordEncoder).encode(CharBuffer.wrap(PASSWORD));
  }

  @Test
  void registerUserAlreadyRegistered_throwAppException() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

    try {
      this.userServiceImpl.register(registerRequest);
    } catch (AppException e) {
      assertEquals("Account Exists", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }

    verify(userRepository).findByEmail(EMAIL);
  }

  @Test
  void registerUserNoEmail_throwAppException() {
    registerRequest.setEmail("");

    try {
      this.userServiceImpl.register(registerRequest);
    } catch (AppException e) {
      assertEquals("Incomplete Request", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }

  }

  @Test
  void registerUserNoFirstNameAndLastName_throwAppException() {
    registerRequest.setFirstName("");
    registerRequest.setLastName("");

    try {
      this.userServiceImpl.register(registerRequest);
    } catch (AppException e) {
      assertEquals("Incomplete Request", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }
  }

  @Test
  void registerAdmin_success() {
    user.setUserRole(UserRole.ROLE_ADMIN);
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    when(mapper.map(user, UserResponse.class)).thenReturn(userResponseAdmin);
    when(mapper.map(registerRequest, User.class)).thenReturn(user);
    when(passwordEncoder.encode(CharBuffer.wrap(PASSWORD))).thenReturn("encoded");
    when(userRepository.save(user)).thenReturn(user);

    UserResponse response = this.userServiceImpl.registerAdmin(registerRequest);
    assertNotNull(response);
    assertEquals(FIRST_NAME, response.getFirstName());
    assertEquals(LAST_NAME, response.getLastName());
    assertEquals(UserRole.ROLE_ADMIN, response.getUserRole());
    assertEquals(TOKEN, response.getToken());

    verify(mapper).map(user, UserResponse.class);
    verify(mapper).map(registerRequest, User.class);
    verify(userRepository).findByEmail(EMAIL);
    verify(userRepository).save(user);
    verify(passwordEncoder).encode(CharBuffer.wrap(PASSWORD));
  }

  @Test
  void registerAdminAlreadyRegistered_throwAppException() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

    try {
      this.userServiceImpl.registerAdmin(registerRequest);
    } catch (AppException e) {
      assertEquals("Account Exists", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }

    verify(userRepository).findByEmail(EMAIL);
  }

  @Test
  void registerEmployee_success() {
    userResponse.setUserRole(UserRole.ROLE_EMPLOYEE);
    user.setUserRole(UserRole.ROLE_EMPLOYEE);
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    when(mapper.map(user, UserResponse.class)).thenReturn(userResponse);
    when(mapper.map(registerRequest, User.class)).thenReturn(user);
    when(passwordEncoder.encode(CharBuffer.wrap(PASSWORD))).thenReturn("encoded");
    when(userRepository.save(user)).thenReturn(user);

    UserResponse response = this.userServiceImpl.registerEmployee(registerRequest);
    assertNotNull(response);
    assertEquals(FIRST_NAME, response.getFirstName());
    assertEquals(LAST_NAME, response.getLastName());
    assertEquals(UserRole.ROLE_EMPLOYEE, response.getUserRole());
    assertEquals(TOKEN, response.getToken());

    verify(mapper).map(user, UserResponse.class);
    verify(mapper).map(registerRequest, User.class);
    verify(userRepository).findByEmail(EMAIL);
    verify(userRepository).save(user);
    verify(passwordEncoder).encode(CharBuffer.wrap(PASSWORD));
  }

  @Test
  void registerEmployeeAlreadyRegistered_throwAppException() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

    try {
      this.userServiceImpl.registerEmployee(registerRequest);
    } catch (AppException e) {
      assertEquals("Account Exists", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }

    verify(userRepository).findByEmail(EMAIL);
  }

  @Test
  void registerEmployeeNoEmail_throwAppException() {
    registerRequest.setEmail("");

    try {
      this.userServiceImpl.registerEmployee(registerRequest);
    } catch (AppException e) {
      assertEquals("Incomplete Request", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }

  }

  @Test
  void registerEmployeeNoFirstNameAndLastName_throwAppException() {
    registerRequest.setFirstName("");
    registerRequest.setLastName("");

    try {
      this.userServiceImpl.registerEmployee(registerRequest);
    } catch (AppException e) {
      assertEquals("Incomplete Request", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }
  }

  @Test
  void editUserProfile_success() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(mapper.map(editedUser, UserResponse.class)).thenReturn(editedUserResponse);
    when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.TRUE);
    when(passwordEncoder.encode(CharBuffer.wrap(PASSWORD))).thenReturn("encoded");
    when(userRepository.save(editedUser)).thenReturn(editedUser);


    UserResponse response = this.userServiceImpl.editUserProfile(editProfileRequest);
    assertNotNull(response);
    assertEquals(FIRST_NAME, response.getFirstName());
    assertEquals(LAST_NAME + "3", response.getLastName());
    assertEquals(UserRole.ROLE_USER, response.getUserRole());
    assertEquals(TOKEN, response.getToken());

    verify(mapper).map(user, UserResponse.class);
    verify(userRepository).findByEmail(EMAIL);
    verify(userRepository).save(user);
    verify(passwordEncoder).encode(CharBuffer.wrap(PASSWORD));
    verify(passwordEncoder).matches(any(), any());
  }

  @Test
  void editUserProfileInvalidPassword_throwAppException() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.FALSE);

    try {
      this.userServiceImpl.editUserProfile(editProfileRequest);
    } catch (AppException e) {
      assertEquals("Invalid password", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }

    verify(userRepository).findByEmail(EMAIL);
    verify(passwordEncoder).matches(any(), any());
  }

  @Test
  void editUserProfileUserNotFound_throwAppException() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    try {
      this.userServiceImpl.editUserProfile(editProfileRequest);
    } catch (AppException e) {
      assertEquals(ErrorCodes.USER_NOT_FOUND.getMessage(), e.getMessage());
      assertEquals(HttpStatus.NOT_FOUND, e.getCode());
    }
    verify(userRepository).findByEmail(EMAIL);
  }

  @Test
  void editUserProfileUserExists_throwAppException() {
    editProfileRequest.setEmail("existing@gmail.com");
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(userRepository.findByEmail("existing@gmail.com")).thenReturn(Optional.of(existingUser));

    try {
      this.userServiceImpl.editUserProfile(editProfileRequest);
    } catch (AppException e) {
      assertEquals("Account Exists", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }
    verify(userRepository, times(2)).findByEmail(any());
  }


  @Test
  void editUserPassword_success() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(mapper.map(newPassUser, UserResponse.class)).thenReturn(userResponse);
    when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.TRUE);
    when(passwordEncoder.encode(CharBuffer.wrap(PASSWORD+"1"))).thenReturn("encoded");
    when(userRepository.save(newPassUser)).thenReturn(newPassUser);


    UserResponse response = this.userServiceImpl.editPassword(editPasswordRequest);
    assertNotNull(response);
    assertEquals(FIRST_NAME, response.getFirstName());
    assertEquals(LAST_NAME, response.getLastName());
    assertEquals(UserRole.ROLE_USER, response.getUserRole());
    assertEquals(TOKEN, response.getToken());

    verify(mapper).map(user, UserResponse.class);
    verify(userRepository).findByEmail(EMAIL);
    verify(userRepository).save(user);
    verify(passwordEncoder).encode(CharBuffer.wrap(PASSWORD+"1"));
    verify(passwordEncoder).matches(any(), any());
  }

  @Test
  void editPasswordInvalidPassword_throwAppException() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.FALSE);

    try {
      this.userServiceImpl.editPassword(editPasswordRequest);
    } catch (AppException e) {
      assertEquals("Invalid password", e.getMessage());
      assertEquals(HttpStatus.BAD_REQUEST, e.getCode());
    }

    verify(userRepository).findByEmail(EMAIL);
    verify(passwordEncoder).matches(any(), any());
  }

  @Test
  void editUserPasswordUserNotFound_throwAppException() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    try {
      this.userServiceImpl.editPassword(editPasswordRequest);
    } catch (AppException e) {
      assertEquals(ErrorCodes.USER_NOT_FOUND.getMessage(), e.getMessage());
      assertEquals(HttpStatus.NOT_FOUND, e.getCode());
    }
    verify(userRepository).findByEmail(EMAIL);
  }

  @BeforeEach
  public void innit() {
    initMocks(this);

    user = User.builder()
        .userRole(UserRole.ROLE_USER)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(EMAIL)
        .password(PASSWORD)
        .build();
    user.setId(ID);

    existingUser = User.builder()
        .userRole(UserRole.ROLE_USER)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email("existing@gmail.com")
        .password(PASSWORD)
        .build();
    existingUser.setId(ID);

    editedUser = User.builder()
        .userRole(UserRole.ROLE_USER)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME+"3")
        .email(EMAIL)
        .password("encoded")
        .build();
    editedUser.setId(ID);

    userResponse = UserResponse.builder()
        .userRole(UserRole.ROLE_USER)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(EMAIL)
        .token(TOKEN)
        .build();

    userResponseAdmin = UserResponse.builder()
        .userRole(UserRole.ROLE_ADMIN)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(EMAIL)
        .token(TOKEN)
        .build();

    editedUserResponse = UserResponse.builder()
        .userRole(UserRole.ROLE_USER)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME+"3")
        .email(EMAIL)
        .token(TOKEN)
        .build();

    loginRequest = LoginRequest.builder()
        .email(EMAIL)
        .password(PASSWORD.toCharArray())
        .build();

    registerRequest = RegisterRequest.builder()
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(EMAIL)
        .password(PASSWORD.toCharArray())
        .build();

    editProfileRequest = EditProfileRequest.builder()
        .currentEmail(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME+"3")
        .email(EMAIL)
        .password(PASSWORD.toCharArray())
        .currentPassword(PASSWORD.toCharArray())
        .build();

    editPasswordRequest = EditPasswordRequest.builder()
        .currentEmail(EMAIL)
        .password((PASSWORD+"1").toCharArray())
        .currentPassword((PASSWORD).toCharArray())
        .build();

    newPassUser = User.builder()
        .userRole(UserRole.ROLE_USER)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .id(ID)
        .email(EMAIL)
        .password("encoded")
        .build();
  }

  @AfterEach
  public void tearDown() {
    verifyNoMoreInteractions(this.userRepository);
    verifyNoMoreInteractions(this.mapper);
    verifyNoMoreInteractions(this.passwordEncoder);
  }
}
