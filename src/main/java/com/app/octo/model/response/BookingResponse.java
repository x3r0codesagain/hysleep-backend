package com.app.octo.model.response;

import com.app.octo.dto.RoomDTO;
import com.app.octo.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse extends BaseResponse {

  private long bookingId;

  private UserDTO user;

  private RoomDTO room;

  private Date bookingDate;

  private String status;
}
