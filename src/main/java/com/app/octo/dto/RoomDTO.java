package com.app.octo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private long roomId;

    private String roomNumber;

    private String status;

    private String floor;
}
