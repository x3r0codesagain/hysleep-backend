package com.app.octo.controller;

import com.app.octo.model.User;
import com.app.octo.model.enums.UserRole;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class UserControllerTest {

  public static final String IMAGE_URL = "IMAGE_URL";
  public static final String FIRST_NAME = "NAME 1";
  public static final String LAST_NAME = "NAME 2";
  public static final String EMAIL = "EMAIL";
  public static final String MALE = "male";
  public static final String PHONE_NO = "12345678";
  public static final String PASSWORD = "password";
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
  private LoginRequest loginRequest;
  private RegisterRequest registerRequest;
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

  @BeforeEach
  private void init() {
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
  }

  @AfterEach
  private void tearDown() {
    verifyNoMoreInteractions(userService);
  }
}
