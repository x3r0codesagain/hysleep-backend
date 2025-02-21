package com.app.octo.controller;

import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.BookingRequest;
import com.app.octo.model.request.GetAllByStatusRequest;
import com.app.octo.model.response.BookingResponse;
import com.app.octo.model.response.ListResponse;
import com.app.octo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/booking")
public class BookingController {

  private final BookingService bookingService;

  @PostMapping("/public/book")
  public ResponseEntity<BookingResponse> bookRoom(@RequestBody BookingRequest bookingRequest) {
    try {
      BookingResponse response = bookingService.bookRoom(bookingRequest);

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (AppException e) {
      BookingResponse response = new BookingResponse();
      response.setErrorCode(e.getCode().name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, e.getCode());
    } catch (Exception e) {
      BookingResponse response = new BookingResponse();
      response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/public/cancel")
  public ResponseEntity<BookingResponse> cancelBooking(@RequestParam Long id) {
    try {
      BookingResponse response = bookingService.cancelBooking(id);

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (AppException e) {
      BookingResponse response = new BookingResponse();
      response.setErrorCode(e.getCode().name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, e.getCode());
    } catch (Exception e) {
      BookingResponse response = new BookingResponse();
      response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/public/done")
  public ResponseEntity<BookingResponse> doneBooking(@RequestParam Long id) {
    try {
      BookingResponse response = bookingService.doneBooking(id);

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (AppException e) {
      BookingResponse response = new BookingResponse();
      response.setErrorCode(e.getCode().name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, e.getCode());
    } catch (Exception e) {
      BookingResponse response = new BookingResponse();
      response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/public/change")
  public ResponseEntity<ListResponse<BookingResponse>> doneBookingAfter() {
    try {
      ListResponse<BookingResponse> response = bookingService.changeStatusAfterTime();

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (AppException e) {
      ListResponse<BookingResponse> response = new ListResponse<>();
      response.setErrorCode(e.getCode().name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, e.getCode());
    } catch (Exception e) {
      ListResponse<BookingResponse> response = new ListResponse<>();
      response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/public/getAllFiltered")
  public ResponseEntity<ListResponse<BookingResponse>> getAllByStatus(
      @RequestBody GetAllByStatusRequest request) {
    try {
      ListResponse<BookingResponse> response = bookingService.getAllByStatus(request);

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (AppException e) {
      ListResponse<BookingResponse> response = new ListResponse<>();
      response.setErrorCode(e.getCode().name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, e.getCode());
    } catch (Exception e) {
      ListResponse<BookingResponse> response = new ListResponse<>();
      response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/admin/getAll")
  public ResponseEntity<ListResponse<BookingResponse>> getAll() {
    try {
      ListResponse<BookingResponse> response = bookingService.getAll();

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (AppException e) {
      ListResponse<BookingResponse> response = new ListResponse<>();
      response.setErrorCode(e.getCode().name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, e.getCode());
    } catch (Exception e) {
      ListResponse<BookingResponse> response = new ListResponse<>();
      response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
      response.setErrorMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
