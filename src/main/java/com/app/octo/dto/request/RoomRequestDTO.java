package com.app.octo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequestDTO {
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotBlank(message = "Floor is required")
    private String floor;

    @NotBlank(message = "Room Description is required")
    private String roomDescription;

    @NotNull(message = "Room Category is required")
    private long categoryId;
}
