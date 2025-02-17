package com.app.octo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUpdateStatusRequestDTO {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "BOOKED|AVAILABLE|MAINTENANCE", message = "Status must be BOOKED, AVAILABLE, or MAINTENANCE")
    private String status;
}
