package com.app.octo.service;

import com.app.octo.model.request.BookingRequest;
import com.app.octo.model.request.GetAllByStatusRequest;
import com.app.octo.model.response.BookingResponse;
import com.app.octo.model.response.ListResponse;

import java.util.List;

public interface BookingService {
  BookingResponse bookRoom(BookingRequest request);

  BookingResponse cancelBooking(Long id);
  BookingResponse doneBooking(Long id);
  ListResponse<BookingResponse> changeStatusAfterTime();
  ListResponse<BookingResponse> getAllByStatus(GetAllByStatusRequest request);
  ListResponse<BookingResponse> getAll();
}
