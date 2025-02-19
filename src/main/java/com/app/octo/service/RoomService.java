package com.app.octo.service;

import com.app.octo.dto.request.RoomRequestDTO;
import com.app.octo.dto.request.RoomIdRequestDTO;
import com.app.octo.dto.request.RoomUpdateStatusRequestDTO;
import com.app.octo.dto.response.RoomResponseDTO;

import java.util.List;

public interface RoomService {
    List<RoomResponseDTO> getAllRooms();
    RoomResponseDTO getRoomById(RoomIdRequestDTO roomIdRequest);
    RoomResponseDTO createRoom(RoomRequestDTO roomRequest);
    RoomResponseDTO updateStatus(RoomUpdateStatusRequestDTO roomUpdateStatus);
    void deleteRoom(RoomIdRequestDTO roomIdRequest);
    boolean existsById(long roomId);
}
