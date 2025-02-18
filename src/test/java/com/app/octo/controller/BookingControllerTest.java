package com.app.octo.controller;

import com.app.octo.dto.RoomDTO;
import com.app.octo.dto.UserDTO;
import com.app.octo.model.Booking;
import com.app.octo.model.Category;
import com.app.octo.model.Room;
import com.app.octo.model.User;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.enums.UserRole;
import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.BookingRequest;
import com.app.octo.model.response.BookingResponse;
import com.app.octo.model.response.ListResponse;
import com.app.octo.service.BookingService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class BookingControllerTest {

    public static final String FIRST_NAME = "NAME 1";
    public static final String LAST_NAME = "NAME 2";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "password";
    public static final Long USER_ID = 1L;

    public static final String CATEGORY_NAME = "CATEGORY 1";
    public static final long CATEGORY_ID = 1L;

    public static final String ROOM_NUMBER = "NUMBER 1";
    public static final String ROOM_STATUS = "ROOM_STATUS";
    public static final String ROOM_DESCRIPTION = "DESCRIPTION";
    public static final String FLOOR = "FLOOR 1";
    public static final Long ROOM_ID = 1L;

    public static final Date BOOKING_DATE = new Date();
    public static final String BOOKING_STATUS = "BOOKING_STATUS";
    public static final Date START_DATE = new Date();;
    public static final Date END_DATE = new Date();;
    public static final Long ID = 1L;

    public static final Integer DURATION = 1;


    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingService bookingService;

    private Booking booking;
    private User user;
    private Room room;
    private Category category;
    private UserDTO userDTO;
    private RoomDTO roomDTO;
    private BookingResponse bookingResponse;
    private ListResponse<BookingResponse> bookingChangeResponse;
    private BookingRequest bookingRequest;
    private MockMvc mockMvc;


    @Test
    void book_success() throws Exception {
        when(bookingService.bookRoom(bookingRequest)).thenReturn(bookingResponse);


        this.mockMvc.perform(post("/api/v1/booking/public/book")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookingRequest)))
                .andExpect(status().isOk());

        verify(bookingService).bookRoom(bookingRequest);
    }

    @Test
    void bookFailedUserNotFound_throwAppException() throws Exception {
        when(bookingService.bookRoom(bookingRequest)).thenThrow(new AppException(ErrorCodes.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        this.mockMvc.perform(post("/api/v1/booking/public/book")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookingRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errorMessage", equalTo(ErrorCodes.USER_NOT_FOUND.getMessage())));

        verify(bookingService).bookRoom(bookingRequest);
    }

    @Test
    void bookFailed_throwException() throws Exception {
        when(bookingService.bookRoom(bookingRequest)).thenThrow(HttpServerErrorException.InternalServerError.class);

        this.mockMvc.perform(post("/api/v1/booking/public/book")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookingRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())));

        verify(bookingService).bookRoom(bookingRequest);
    }

    @Test
    void cancel_success() throws Exception {
        when(bookingService.cancelBooking(ID)).thenReturn(bookingResponse);


        this.mockMvc.perform(post("/api/v1/booking/public/cancel")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", String.valueOf(ID)))
                .andExpect(status().isOk());

        verify(bookingService).cancelBooking(ID);
    }

    @Test
    void cancelBookingNotFound_throwAppException() throws Exception {
        when(bookingService.cancelBooking(ID)).thenThrow(new AppException(ErrorCodes.DATA_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        this.mockMvc.perform(post("/api/v1/booking/public/cancel")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", String.valueOf(ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errorMessage", equalTo(ErrorCodes.DATA_NOT_FOUND.getMessage())));

        verify(bookingService).cancelBooking(ID);
    }

    @Test
    void cancelFailed_throwException() throws Exception {
        when(bookingService.cancelBooking(ID)).thenThrow(HttpServerErrorException.InternalServerError.class);

        this.mockMvc.perform(post("/api/v1/booking/public/cancel")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", String.valueOf(ID)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())));

        verify(bookingService).cancelBooking(ID);
    }

    @Test
    void doneBooking_success() throws Exception {
        when(bookingService.doneBooking(ID)).thenReturn(bookingResponse);

        this.mockMvc.perform(post("/api/v1/booking/public/done")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", String.valueOf(ID)))
                .andExpect(status().isOk());

        verify(bookingService).doneBooking(ID);
    }

    @Test
    void doneBookingNotFound_throwAppException() throws Exception {
        when(bookingService.doneBooking(ID)).thenThrow(new AppException(ErrorCodes.DATA_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        this.mockMvc.perform(post("/api/v1/booking/public/done")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", String.valueOf(ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errorMessage", equalTo(ErrorCodes.DATA_NOT_FOUND.getMessage())));

        verify(bookingService).doneBooking(ID);
    }

    @Test
    void doneBookingFailed_throwException() throws Exception {
        when(bookingService.doneBooking(ID)).thenThrow(HttpServerErrorException.InternalServerError.class);

        this.mockMvc.perform(post("/api/v1/booking/public/done")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", String.valueOf(ID)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())));

        verify(bookingService).doneBooking(ID);
    }

    @Test
    void changeStatus_success() throws Exception {
        when(bookingService.changeStatusAfterTime()).thenReturn(bookingChangeResponse);

        this.mockMvc.perform(post("/api/v1/booking/public/change")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService).changeStatusAfterTime();
    }

    @Test
    void changeStatusNotFound_throwException() throws Exception {
        when(bookingService.changeStatusAfterTime()).thenThrow(new AppException(ErrorCodes.DATA_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        this.mockMvc.perform(post("/api/v1/booking/public/change")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService).changeStatusAfterTime();
    }

    @Test
    void changeStatusFailed_throwException() throws Exception {
        when(bookingService.changeStatusAfterTime()).thenThrow(HttpServerErrorException.InternalServerError.class);

        this.mockMvc.perform(post("/api/v1/booking/public/change")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())));

        verify(bookingService).changeStatusAfterTime();
    }



    @BeforeEach
    public void init() {
        initMocks(this);
        this.mockMvc = standaloneSetup(this.bookingController).build();

        user = User.builder()
                .userRole(UserRole.ROLE_USER)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        user.setId(USER_ID);

        category = Category.builder()
                .categoryName(CATEGORY_NAME)
                .build();
        category.setCategoryId(CATEGORY_ID);

        room = Room.builder()
                .roomNumber(ROOM_NUMBER)
                .status(ROOM_STATUS)
                .roomDescription(ROOM_DESCRIPTION)
                .floor(FLOOR)
                .category(category)
                .build();
        room.setRoomId(ROOM_ID);

        booking = Booking.builder()
                .user(user)
                .room(room)
                .bookingDate(BOOKING_DATE)
                .status(BOOKING_STATUS)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .build();
        booking.setBookingId(ID);


        userDTO = UserDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .build();

        roomDTO = RoomDTO.builder()
                .roomId(ROOM_ID)
                .roomNumber(ROOM_NUMBER)
                .status(ROOM_STATUS)
                .floor(FLOOR)
                .build();

        bookingResponse = BookingResponse.builder()
                .bookingId(ID)
                .user(userDTO)
                .room(roomDTO)
                .bookingDate(BOOKING_DATE)
                .status(BOOKING_STATUS)
                .build();

        ListResponse<BookingResponse> bookingChangeResponse = new ListResponse<>();
        bookingChangeResponse.setVal(new ArrayList<>());

        bookingRequest = BookingRequest.builder()
                .roomId(ROOM_ID)
                .userEmail(EMAIL)
                .duration(DURATION)
                .build();
    }

    @AfterEach
    private void tearDown() {
        verifyNoMoreInteractions(bookingService);
    }
}
