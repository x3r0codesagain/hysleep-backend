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
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    Booking booking = Booking.builder()
        .status("ONGOING")
        .user(user)
        .bookingDate(new Date())
        .room(room)
        .build();

    bookingRepository.save(booking);
    return mapper.map(booking, BookingResponse.class);
  }
}
