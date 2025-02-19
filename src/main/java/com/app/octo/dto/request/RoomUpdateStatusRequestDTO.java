package com.app.octo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoomUpdateStatusRequestDTO {
    @NotNull(message = "Room ID required")
    private long roomId;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "BOOKED|AVAILABLE|MAINTENANCE", message = "Status must be BOOKED, AVAILABLE, or MAINTENANCE")
    private String status;
}
