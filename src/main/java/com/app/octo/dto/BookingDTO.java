package com.app.octo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BookingDTO {
  private long bookingId;

  private UserDTO user;

  private RoomDTO room;

  private Date bookingDate;

  private Date endDate;

  private String status;
}
