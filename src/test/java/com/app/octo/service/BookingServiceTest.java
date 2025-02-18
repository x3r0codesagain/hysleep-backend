package com.app.octo.service;

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
import com.app.octo.repository.BookingRepository;
import com.app.octo.repository.RoomRepository;
import com.app.octo.repository.UserRepository;
import com.app.octo.service.impl.BookingServiceImpl;
import org.apache.commons.lang3.time.DateUtils;
import org.dozer.Mapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingServiceTest {

  public static final long ID = 1L;
  public static final int DURATION = 2;
  public static final String USER_HYSLEEP_COM = "user@hysleep.com";
  public static final Date DATE = new Date();
  public static final String ROOM_NUMBER = "209";
  public static final String FLOOR = "21";
  public static final String AVAILABLE_STATUS = "AVAILABLE";
  public static final String FIRST_NAME = "FIRST_NAME";
  public static final String LAST_NAME = "LAST_NAME";
  public static final String PASSWORD = "#!@@#!!!@";
  public static final String DESC = "DESC";
  public static final String CATEGORY_NAME = "CATEGORY_NAME";
  public static final String BOOKED_STATUS = "BOOKED";
  public static final String DONE = "DONE";
  public static final String CANCELLED = "CANCELLED";
  @InjectMocks
  private BookingServiceImpl bookingService;

  @Mock
  private RoomRepository roomRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private Mapper mapper;

  private BookingRequest bookingRequest;
  private BookingResponse bookingResponse;
  private RoomDTO roomDTO;
  private UserDTO userDTO;
  private User user;
  private Room room;
  private Room bookedRoom;
  private Booking booking;
  private Booking cancelledBooking;

  @Test
  public void bookRoom_success() {
    when(roomRepository.findByRoomIdAndStatus(ID, AVAILABLE_STATUS)).thenReturn(room);
    when(userRepository.findByEmail(USER_HYSLEEP_COM)).thenReturn(Optional.of(user));
    when(roomRepository.save(bookedRoom)).thenReturn(bookedRoom);
    when(bookingRepository.save(any())).thenReturn(booking);
    when(mapper.map(any(), any())).thenReturn(bookingResponse);

    BookingResponse response = bookingService.bookRoom(bookingRequest);
    assertNotNull(response);
    assertEquals("ONGOING", response.getStatus());
    assertEquals(ID, response.getBookingId());

    verify(roomRepository).findByRoomIdAndStatus(ID, AVAILABLE_STATUS);
    verify(userRepository).findByEmail(USER_HYSLEEP_COM);
    verify(roomRepository).save(bookedRoom);
    verify(bookingRepository).save(any());
    verify(mapper).map(any(), any());
  }

  @Test
  public void bookRoomNotFound_throwException() {
    when(roomRepository.findByRoomIdAndStatus(ID, AVAILABLE_STATUS)).thenReturn(null);

    try {
      bookingService.bookRoom(bookingRequest);
    } catch (AppException e) {
      assertEquals(ErrorCodes.ROOM_NOT_FOUND.getMessage(), e.getMessage());
      assertEquals(HttpStatus.NOT_FOUND, e.getCode());
    }


    verify(roomRepository).findByRoomIdAndStatus(ID, AVAILABLE_STATUS);
  }

  @Test
  public void bookRoomUserNotFound_throwException() {
    when(roomRepository.findByRoomIdAndStatus(ID, AVAILABLE_STATUS)).thenReturn(room);
    when(userRepository.findByEmail(USER_HYSLEEP_COM)).thenReturn(Optional.empty());

    try {
      bookingService.bookRoom(bookingRequest);
    } catch (AppException e) {
      assertEquals(ErrorCodes.USER_NOT_FOUND.getMessage(), e.getMessage());
      assertEquals(HttpStatus.NOT_FOUND, e.getCode());
    }

    verify(roomRepository).findByRoomIdAndStatus(ID, AVAILABLE_STATUS);
    verify(userRepository).findByEmail(USER_HYSLEEP_COM);
  }

  @Test
  public void cancelBooking_success() {
    bookingResponse.setStatus("CANCELLED");
    when(bookingRepository.findByBookingId(ID)).thenReturn(booking);
    when(roomRepository.findByRoomIdAndStatus(ID, BOOKED_STATUS)).thenReturn(bookedRoom);
    when(bookingRepository.save(any())).thenReturn(cancelledBooking);
    when(roomRepository.save(room)).thenReturn(room);
    when(mapper.map(any(), any())).thenReturn(bookingResponse);

    BookingResponse response = bookingService.cancelBooking(ID);
    assertNotNull(response);
    assertEquals("CANCELLED", response.getStatus());
    assertEquals(ID, response.getBookingId());

    verify(roomRepository).findByRoomIdAndStatus(ID, BOOKED_STATUS);
    verify(roomRepository).save(bookedRoom);
    verify(bookingRepository).findByBookingId(ID);
    verify(bookingRepository).save(any());
    verify(mapper).map(any(), any());
  }

  @Test
  public void cancelBookingRoomNotFound_throwException() {
    bookingResponse.setStatus("CANCELLED");
    when(bookingRepository.findByBookingId(ID)).thenReturn(booking);
    when(roomRepository.findByRoomIdAndStatus(ID, BOOKED_STATUS)).thenReturn(null);

    try {
      bookingService.cancelBooking(ID);
    } catch (AppException e) {
      assertEquals(ErrorCodes.DATA_NOT_FOUND.getMessage(), e.getMessage());
      assertEquals(HttpStatus.NOT_FOUND, e.getCode());
    }

    verify(roomRepository).findByRoomIdAndStatus(ID, BOOKED_STATUS);
    verify(bookingRepository).findByBookingId(ID);
  }

  @Test
  public void cancelBookingBookingNotFound_throwException() {
    bookingResponse.setStatus("CANCELLED");
    when(bookingRepository.findByBookingId(ID)).thenReturn(null);

    try {
      bookingService.cancelBooking(ID);
    } catch (AppException e) {
      assertEquals(ErrorCodes.DATA_NOT_FOUND.getMessage(), e.getMessage());
      assertEquals(HttpStatus.NOT_FOUND, e.getCode());
    }

    verify(bookingRepository).findByBookingId(ID);
  }

  @Test
  public void doneBooking_success() {
    bookingResponse.setStatus(DONE);
    cancelledBooking.setStatus(DONE);
    when(bookingRepository.findByBookingId(ID)).thenReturn(booking);
    when(roomRepository.findByRoomIdAndStatus(ID, BOOKED_STATUS)).thenReturn(bookedRoom);
    when(bookingRepository.save(any())).thenReturn(cancelledBooking);
    when(roomRepository.save(room)).thenReturn(room);
    when(mapper.map(any(), any())).thenReturn(bookingResponse);

    BookingResponse response = bookingService.doneBooking(ID);
    assertNotNull(response);
    assertEquals(DONE, response.getStatus());
    assertEquals(ID, response.getBookingId());

    verify(roomRepository).findByRoomIdAndStatus(ID, BOOKED_STATUS);
    verify(roomRepository).save(bookedRoom);
    verify(bookingRepository).findByBookingId(ID);
    verify(bookingRepository).save(any());
    verify(mapper).map(any(), any());
  }

  @Test
  public void doneBookingRoomNotFound_throwException() {
    bookingResponse.setStatus(DONE);
    when(bookingRepository.findByBookingId(ID)).thenReturn(booking);
    when(roomRepository.findByRoomIdAndStatus(ID, BOOKED_STATUS)).thenReturn(null);

    try {
      bookingService.doneBooking(ID);
    } catch (AppException e) {
      assertEquals(ErrorCodes.DATA_NOT_FOUND.getMessage(), e.getMessage());
      assertEquals(HttpStatus.NOT_FOUND, e.getCode());
    }

    verify(roomRepository).findByRoomIdAndStatus(ID, BOOKED_STATUS);
    verify(bookingRepository).findByBookingId(ID);
  }

  @Test
  public void doneBookingBookingNotFound_throwException() {
    bookingResponse.setStatus(DONE);
    when(bookingRepository.findByBookingId(ID)).thenReturn(null);

    try {
      bookingService.doneBooking(ID);
    } catch (AppException e) {
      assertEquals(ErrorCodes.DATA_NOT_FOUND.getMessage(), e.getMessage());
      assertEquals(HttpStatus.NOT_FOUND, e.getCode());
    }

    verify(bookingRepository).findByBookingId(ID);
  }

  @Test
  public void changeStatusAfterTime_success() {
    Date newDate = DateUtils.addHours(new Date(), -4);
    booking.setBookingDate(newDate);
    booking.setEndDate(newDate);
    List<Booking> bookings = new ArrayList<>(Arrays.asList(booking));
    cancelledBooking.setStatus(DONE);
    bookingResponse.setStatus(DONE);

    when(bookingRepository.findAllByStatus("ONGOING")).thenReturn(bookings);
    when(bookingRepository.findByBookingId(ID)).thenReturn(booking);
    when(roomRepository.findByRoomIdAndStatus(ID, BOOKED_STATUS)).thenReturn(bookedRoom);
    when(bookingRepository.save(any())).thenReturn(cancelledBooking);
    when(roomRepository.save(room)).thenReturn(room);
    when(mapper.map(any(), any())).thenReturn(bookingResponse);


    ListResponse<BookingResponse> bookingResponses = this.bookingService.changeStatusAfterTime();

    assertEquals(1, bookingResponses.getVal().size());
    bookingResponses.getVal().stream().forEach(response -> {
      assertEquals(DONE, response.getStatus());
    });



    verify(bookingRepository).findByBookingId(ID);
    verify(bookingRepository).findAllByStatus("ONGOING");
    verify(roomRepository).findByRoomIdAndStatus(ID, BOOKED_STATUS);
    verify(roomRepository).save(bookedRoom);
    verify(bookingRepository).findByBookingId(ID);
    verify(bookingRepository).save(any());
    verify(mapper).map(any(), any());
  }


  @BeforeEach
  public void init() {
    initMocks(this);
    Date end = DateUtils.addHours(DATE, DURATION);
    bookingRequest = BookingRequest.builder()
        .roomId(ID)
        .duration(DURATION)
        .userEmail(USER_HYSLEEP_COM)
        .build();

    roomDTO = RoomDTO.builder()
        .floor(FLOOR)
        .roomNumber(ROOM_NUMBER)
        .roomId(ID)
        .status(BOOKED_STATUS)
        .build();

    room = Room.builder()
        .floor(FLOOR)
        .roomDescription(DESC)
        .roomNumber(ROOM_NUMBER)
        .status(AVAILABLE_STATUS)
        .category(new Category(ID, CATEGORY_NAME))
        .roomId(ID)
        .build();
    bookedRoom = Room.builder()
        .floor(FLOOR)
        .roomDescription(DESC)
        .roomNumber(ROOM_NUMBER)
        .status(BOOKED_STATUS)
        .category(new Category(ID, CATEGORY_NAME))
        .roomId(ID)
        .build();

    userDTO = UserDTO.builder()
        .email(USER_HYSLEEP_COM)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .build();

    user = User.builder()
        .id(ID)
        .userRole(UserRole.ROLE_USER)
        .lastName(LAST_NAME)
        .firstName(FIRST_NAME)
        .password(PASSWORD)
        .email(USER_HYSLEEP_COM)
        .build();

    bookingResponse = BookingResponse.builder()
        .bookingDate(DATE)
        .bookingId(ID)
        .room(roomDTO)
        .endDate(end)
        .user(userDTO)
        .status("ONGOING")
        .build();

    booking = Booking.builder()
        .bookingDate(DATE)
        .bookingId(ID)
        .room(bookedRoom)
        .endDate(end)
        .user(user)
        .status("ONGOING")
        .build();

    cancelledBooking = Booking.builder()
        .bookingDate(DATE)
        .bookingId(ID)
        .room(bookedRoom)
        .endDate(end)
        .user(user)
        .status("CANCELLED")
        .build();
  }

  @AfterEach
  public void tearDown() {
    verifyNoMoreInteractions(bookingRepository);
    verifyNoMoreInteractions(roomRepository);
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(mapper);
  }

}
