package com.app.octo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomIdRequestDTO {
    @NotNull(message = "Room number is required")
    private long id;
}
