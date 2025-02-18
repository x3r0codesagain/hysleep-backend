package com.app.octo.controller;

import com.app.octo.model.User;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.enums.UserRole;
import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.EditProfileRequest;
import com.app.octo.model.request.LoginRequest;
import com.app.octo.model.request.RegisterRequest;
import com.app.octo.model.response.UserResponse;
import com.app.octo.security.UserAuthProvider;
import com.app.octo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class UserControllerTest {

  public static final String IMAGE_URL = "IMAGE_URL";
  public static final String FIRST_NAME = "NAME 1";
  public static final String LAST_NAME = "NAME 2";
  public static final String EMAIL = "EMAIL";
  public static final String CURRENT_EMAIL = "CURRENT_EMAIL";
  public static final String MALE = "male";
  public static final String PHONE_NO = "12345678";
  public static final String PASSWORD = "password";
  public static final String CURRENT_PASSWORD = "current_password";
  public static final Long ID = 1L;
  public static final String TOKEN = "token";
  @InjectMocks
  private UserController userController;

  @Mock
  private UserAuthProvider userAuthProvider;

  @Mock
  private UserService userService;

  private User user;
  private UserResponse userResponse;
  private UserResponse userResponseAdmin;
  private UserResponse userResponseEmployee;
  private LoginRequest loginRequest;
  private RegisterRequest registerRequest;
  private EditProfileRequest editProfileRequest;
  private MockMvc mockMvc;

  @Test
  public void login_success() throws Exception {
    when(userService.login(loginRequest)).thenReturn(userResponse);
    when(userAuthProvider.generateToken(EMAIL, UserRole.ROLE_USER.name())).thenReturn(TOKEN);

    this.mockMvc.perform(post("/api/v1/users/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(loginRequest)))
            .andExpect(status().isOk());

    verify(userService).login(loginRequest);
    verify(userAuthProvider).generateToken(EMAIL, UserRole.ROLE_USER.name());
  }

  @Test
  public void loginFailedUserNotFound_throwAppException() throws Exception {
    when(userService.login(loginRequest)).thenThrow(new AppException(ErrorCodes.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

    this.mockMvc.perform(post("/api/v1/users/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(loginRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.NOT_FOUND.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo(ErrorCodes.USER_NOT_FOUND.getMessage())));

    verify(userService).login(loginRequest);
  }

  @Test
  public void loginFailedInvalidPassword_throwException() throws Exception {
    when(userService.login(loginRequest)).thenThrow(new AppException("Invalid password", HttpStatus.BAD_REQUEST));

    this.mockMvc.perform(post("/api/v1/users/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Invalid password")));

    verify(userService).login(loginRequest);
  }

  @Test
  public void loginFailed_throwException() throws Exception {
    when(userService.login(loginRequest)).thenThrow(HttpServerErrorException.InternalServerError.class);

    this.mockMvc.perform(post("/api/v1/users/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(loginRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Internal Error")));

    verify(userService).login(loginRequest);
  }

  @Test
  public void register_success() throws Exception {
    when(userService.register(registerRequest)).thenReturn(userResponse);

    this.mockMvc.perform(post("/api/v1/users/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequest)))
            .andExpect(status().isOk());

    verify(userService).register(registerRequest);
  }

  @Test
  public void registerFailedIncomplete_throwAppException() throws Exception {
    when(userService.register(registerRequest)).thenThrow(new AppException("Incomplete Request", HttpStatus.BAD_REQUEST));

    this.mockMvc.perform(post("/api/v1/users/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Incomplete Request")));

    verify(userService).register(registerRequest);
  }

  @Test
  public void registerFailedAccountExist_throwAppException() throws Exception {
    when(userService.register(registerRequest)).thenThrow(new AppException("Account Exists", HttpStatus.BAD_REQUEST));

    this.mockMvc.perform(post("/api/v1/users/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Account Exists")));

    verify(userService).register(registerRequest);
  }

  @Test
  public void registerFailedServer_throwException() throws Exception {
    when(userService.register(registerRequest)).thenThrow(HttpServerErrorException.InternalServerError.class);

    this.mockMvc.perform(post("/api/v1/users/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Internal Error")));

    verify(userService).register(registerRequest);
  }

  @Test
  public void registerAdmin_success() throws Exception {
    when(userService.registerAdmin(registerRequest)).thenReturn(userResponseAdmin);

    this.mockMvc.perform(post("/api/v1/users/register-admin")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequest)))
            .andExpect(status().isOk());

    verify(userService).registerAdmin(registerRequest);
  }

  @Test
  public void registerAdminAccountExist_throwAppException() throws Exception {
    when(userService.registerAdmin(registerRequest)).thenThrow(new AppException("Account Exists", HttpStatus.BAD_REQUEST));

    this.mockMvc.perform(post("/api/v1/users/register-admin")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Account Exists")));

    verify(userService).registerAdmin(registerRequest);
  }

  @Test
  public void registerAdminFailed_throwException() throws Exception {
    when(userService.registerAdmin(registerRequest)).thenThrow(HttpServerErrorException.InternalServerError.class);

    this.mockMvc.perform(post("/api/v1/users/register-admin")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Internal Error")));

    verify(userService).registerAdmin(registerRequest);
  }

  @Test
  public void registerEmployee_success() throws Exception {
    when(userService.registerEmployee(registerRequest)).thenReturn(userResponseEmployee);

    this.mockMvc.perform(post("/api/v1/users/register-emp")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequest)))
            .andExpect(status().isOk());

    verify(userService).registerEmployee(registerRequest);
  }

  @Test
  public void registerEmployeeAccountExist_throwAppException() throws Exception {
    when(userService.registerEmployee(registerRequest)).thenThrow(new AppException("Account Exists", HttpStatus.BAD_REQUEST));

    this.mockMvc.perform(post("/api/v1/users/register-emp")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Account Exists")));

    verify(userService).registerEmployee(registerRequest);
  }

  @Test
  public void registerEmployeeFailed_throwException() throws Exception {
    when(userService.registerEmployee(registerRequest)).thenThrow(HttpServerErrorException.InternalServerError.class);

    this.mockMvc.perform(post("/api/v1/users/register-emp")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(registerRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Internal Error")));

    verify(userService).registerEmployee(registerRequest);
  }

  @Test
  public void updateUser_success() throws Exception {
    when(userService.editUserProfile(editProfileRequest)).thenReturn(userResponse);

    this.mockMvc.perform(post("/api/v1/users/public/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(editProfileRequest)))
            .andExpect(status().isOk());

    verify(userService).editUserProfile(editProfileRequest);
  }

  @Test
  public void updateUserFailedEmailTaken_throwAppException() throws Exception {
    when(userService.editUserProfile(editProfileRequest)).thenThrow(new AppException("Account Exists", HttpStatus.BAD_REQUEST));

    this.mockMvc.perform(post("/api/v1/users/public/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(editProfileRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Account Exists")));

    verify(userService).editUserProfile(editProfileRequest);
  }

  @Test
  public void updateUserFailedServer_throwException() throws Exception {
    when(userService.editUserProfile(editProfileRequest)).thenThrow(HttpServerErrorException.InternalServerError.class);

    this.mockMvc.perform(post("/api/v1/users/public/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(editProfileRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())))
            .andExpect(jsonPath("$.errorMessage", equalTo("Internal Error")));

    verify(userService).editUserProfile(editProfileRequest);
  }

  @BeforeEach
  public void init() {
    initMocks(this);
    this.mockMvc = standaloneSetup(this.userController).build();

    user = User.builder()
        .userRole(UserRole.ROLE_USER)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(EMAIL)
        .password(PASSWORD)
        .build();
    user.setId(ID);

    userResponse = UserResponse.builder()
        .userRole(UserRole.ROLE_USER)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(EMAIL)
        .build();

    userResponseAdmin = UserResponse.builder()
        .userRole(UserRole.ROLE_ADMIN)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(EMAIL)
        .build();

    userResponseEmployee= UserResponse.builder()
        .userRole(UserRole.ROLE_EMPLOYEE)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(EMAIL)
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
            .currentEmail(CURRENT_EMAIL)
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .email(EMAIL)
            .currentPassword(CURRENT_PASSWORD.toCharArray())
            .password(PASSWORD.toCharArray())
            .build();
  }

  @AfterEach
  private void tearDown() {
    verifyNoMoreInteractions(userService);
  }
}
