package com.app.octo.service;

import com.app.octo.dto.request.RoomRequestDTO;
import com.app.octo.dto.request.RoomUpdateStatusRequestDTO;
import com.app.octo.dto.response.RoomResponseDTO;

import java.util.List;

public interface RoomService {
    List<RoomResponseDTO> getAllRooms();
    RoomResponseDTO getRoomById(long roomId);
    RoomResponseDTO createRoom(RoomRequestDTO roomRequest);
    RoomResponseDTO updateStatus(long roomId, RoomUpdateStatusRequestDTO roomUpdateStatus);
    void deleteRoom(long roomId);
    boolean existsById(long roomId);
}
