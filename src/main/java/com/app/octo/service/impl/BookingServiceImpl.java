package com.app.octo.service.impl;

import com.app.octo.model.Booking;
import com.app.octo.model.Room;
import com.app.octo.model.User;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.BookingRequest;
import com.app.octo.model.response.BookingResponse;
import com.app.octo.repository.BookingRepository;
import com.app.octo.repository.RoomRepository;
import com.app.octo.repository.UserRepository;
import com.app.octo.service.BookingService;
import org.apache.commons.lang3.time.DateUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class BookingServiceImpl implements BookingService {

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private Mapper mapper;

  @Override
  public BookingResponse bookRoom(BookingRequest request) {

    Room room = roomRepository.findByRoomIdAndStatus(request.getRoomId(), "AVAILABLE");

    if (Objects.isNull(room)) {
      throw new AppException(ErrorCodes.ROOM_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
    }

    User user = userRepository.findByEmail(request.getUserEmail()).orElseGet(() -> null);

    if (Objects.isNull(user)) {
      throw new AppException(ErrorCodes.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
    }

    room.setStatus("BOOKED");

    roomRepository.save(room);

    Date now = new Date();
    Date end = DateUtils.addHours(now, request.getDuration());

    Booking booking = Booking.builder()
        .status("ONGOING")
        .user(user)
        .bookingDate(now)
        .startDate(now)
        .endDate(end)
        .room(room)
        .build();

    bookingRepository.save(booking);
    return mapper.map(booking, BookingResponse.class);
  }

  @Override
  public BookingResponse cancelBooking(Long id) {
    BookingResponse response = processBookingCancelOrDone(id, "CANCELLED");

    return response;
  }

  @Override
  public BookingResponse doneBooking(Long id) {
    BookingResponse response = processBookingCancelOrDone(id, "DONE");

    return response;
  }

  private BookingResponse processBookingCancelOrDone(Long id, String status) {
    Booking booking = bookingRepository.findByBookingId(id);

    if (Objects.isNull(booking)) {
      throw new AppException(ErrorCodes.DATA_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
    }

    booking.setStatus(status);

    Room room = roomRepository.findByRoomIdAndStatus(booking.getRoom().getRoomId(), "BOOKED");

    if (Objects.isNull(room)) {
      throw new AppException(ErrorCodes.DATA_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
    }

    room.setStatus("AVAILABLE");

    booking.setRoom(room);

    bookingRepository.save(booking);
    roomRepository.save(room);

    BookingResponse response = mapper.map(booking, BookingResponse.class);
    return response;
  }

  @Override
  public List<BookingResponse> changeStatusAfterTime() {
    List<Booking> bookings = bookingRepository.findAllByStatus("ONGOING");
    List<BookingResponse> responses = new ArrayList<>();
    Date now = new Date();
    bookings.stream().forEach(booking -> {
      if (now.after(booking.getEndDate())) {
        responses.add(doneBooking(booking.getBookingId()));
      }
    });

    return responses;
  }
}
