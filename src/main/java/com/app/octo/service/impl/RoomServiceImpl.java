package com.app.octo.service.impl;

import com.app.octo.dto.request.RoomRequestDTO;
import com.app.octo.dto.request.RoomUpdateStatusRequestDTO;
import com.app.octo.dto.response.RoomResponseDTO;
import com.app.octo.model.Category;
import com.app.octo.model.Room;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.exception.AppException;
import com.app.octo.repository.CategoryRepository;
import com.app.octo.repository.RoomRepository;
import com.app.octo.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    private final CategoryRepository categoryRepository;

    public RoomServiceImpl(RoomRepository roomRepository, CategoryRepository categoryRepository) {
        this.roomRepository = roomRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<RoomResponseDTO> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public RoomResponseDTO getRoomById(long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        return mapToResponse(room);
    }

    @Override
    public RoomResponseDTO createRoom(RoomRequestDTO roomRequest) {

        Category category = categoryRepository.findByCategoryId(roomRequest.getCategoryId());

        if (Objects.isNull(category)) {
            throw new AppException(ErrorCodes.CATEGORY_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }

        Room room = Room.builder()
                .roomNumber(roomRequest.getRoomNumber())
                .status("AVAILABLE")
                .floor(roomRequest.getFloor())
                .roomDescription(roomRequest.getRoomDescription())
                .category(category)
                .build();
        Room savedRoom = roomRepository.save(room);
        return mapToResponse(savedRoom);
    }

    @Override
    public RoomResponseDTO updateStatus(long id, RoomUpdateStatusRequestDTO roomUpdateStatus) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room with ID " + id + " not found"));

        room.setStatus(roomUpdateStatus.getStatus());
        Room updatedRoom = roomRepository.save(room);
        return new RoomResponseDTO(
                updatedRoom.getRoomId(),
                updatedRoom.getRoomNumber(),
                updatedRoom.getStatus(),
                updatedRoom.getFloor(),
                updatedRoom.getRoomDescription(),
                updatedRoom.getCategory().getCategoryName()
        );
    }

    @Override
    public void deleteRoom(long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        roomRepository.delete(room);
    }

    @Override
    public boolean existsById(long roomId) {
        return roomRepository.existsById(roomId);
    }

    private RoomResponseDTO mapToResponse(Room room) {
        return new RoomResponseDTO(
                room.getRoomId(),
                room.getRoomNumber(),
                room.getStatus(),
                room.getFloor(),
                room.getRoomDescription(),
                room.getCategory().getCategoryName()
        );
    }
}
