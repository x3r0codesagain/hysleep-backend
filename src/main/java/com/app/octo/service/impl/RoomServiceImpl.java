package com.app.octo.service.impl;

import com.app.octo.dto.request.RoomRequestDTO;
import com.app.octo.dto.request.RoomIdRequestDTO;
import com.app.octo.dto.request.RoomUpdateStatusRequestDTO;
import com.app.octo.dto.response.RoomResponseDTO;
import com.app.octo.model.Category;
import com.app.octo.model.Room;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.exception.AppException;
import com.app.octo.repository.CategoryRepository;
import com.app.octo.repository.RoomRepository;
import com.app.octo.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Optional;

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
    public RoomResponseDTO getRoomById(RoomIdRequestDTO roomIdRequest) {
        Long roomId = roomIdRequest.getId();
        if (roomId == null || !roomRepository.existsById(roomId)) {  // Periksa null
            throw new RuntimeException("Room not found with id: " + roomId);
        }
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
    public RoomResponseDTO updateStatus(RoomUpdateStatusRequestDTO roomUpdateStatus) {
        long roomId = roomUpdateStatus.getRoomId();

        // Periksa apakah room dengan ID tersebut ada
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room with ID " + roomId + " not found.");
        }

        // Ambil room dari database setelah memastikan bahwa room ada
        Optional<Room> room = roomRepository.findById(roomId);

        System.out.println("Room ID: " + roomId);
        System.out.println("====================");
        System.out.println(room);
        System.out.println("====================");

        if (room.isPresent()) {
            Room roomToUpdate = room.get();
            roomToUpdate.setStatus(roomUpdateStatus.getStatus());
            Room updatedRoom = roomRepository.save(roomToUpdate);
            return mapToResponse(updatedRoom);
        } else {
            // Walaupun ini sebenarnya tidak perlu karena sebelumnya sudah diperiksa dengan existsById
            throw new EntityNotFoundException("Room with ID " + roomId + " not found.");
        }
    }

    @Override
    public void deleteRoom(RoomIdRequestDTO roomIdRequest) {
        long roomId = roomIdRequest.getId();
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
                room.getCategory()
        );
    }
}
