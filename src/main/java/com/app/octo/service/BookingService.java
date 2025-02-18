package com.app.octo.service;

import com.app.octo.model.request.BookingRequest;
import com.app.octo.model.response.BookingResponse;

import java.util.List;

public interface BookingService {
  BookingResponse bookRoom(BookingRequest request);

  BookingResponse cancelBooking(Long id);
  BookingResponse doneBooking(Long id);
  List<BookingResponse> changeStatusAfterTime();
}
