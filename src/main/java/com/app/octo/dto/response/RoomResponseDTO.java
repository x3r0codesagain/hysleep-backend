package com.app.octo.dto.response;

import com.app.octo.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponseDTO {
    private long roomId;
    private String roomNumber;
    private String status;
    private String floor;
    private String roomDescription;
    private Category category;
}
