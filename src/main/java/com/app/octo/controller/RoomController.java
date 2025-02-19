package com.app.octo.controller;

import com.app.octo.dto.request.RoomRequestDTO;
import com.app.octo.dto.request.RoomIdRequestDTO;
import com.app.octo.dto.request.RoomUpdateStatusRequestDTO;
import com.app.octo.dto.response.RoomResponseDTO;
import com.app.octo.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/public/getAll")
    public ResponseEntity<Object> getAllRooms() {
        try {
            List<RoomResponseDTO> rooms = roomService.getAllRooms();
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            log.error("e: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error fetching rooms: " + e.getMessage());
        }
    }

    @PostMapping("/public/getById")
    public ResponseEntity<Object> getRoomById(@Valid @RequestBody RoomIdRequestDTO roomIdRequest) {
        try {
            long roomId = roomIdRequest.getRoomId();
            if (!roomService.existsById(roomId)) {
                return ResponseEntity.notFound().build();
            }
            RoomResponseDTO room = roomService.getRoomById(roomIdRequest);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching room: " + e.getMessage());
        }
    }

    @PostMapping("/public/createRoom")
    public ResponseEntity<Object> createRoom(@Valid @RequestBody RoomRequestDTO roomRequest) {
        try {
            RoomResponseDTO createdRoom = roomService.createRoom(roomRequest);
            return ResponseEntity.ok(createdRoom);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating room: " + e.getMessage());
        }
    }

    @PostMapping("/public/updateStatus")
    public ResponseEntity<Object> updateStatus(@Valid @RequestBody RoomUpdateStatusRequestDTO roomUpdateStatus) {
        try {
            Long roomId = roomUpdateStatus.getRoomId();
            if (!roomService.existsById(roomId)) {
                return ResponseEntity.notFound().build();
            }
            RoomResponseDTO updatedRoom = roomService.updateStatus(roomUpdateStatus);
            return ResponseEntity.ok(updatedRoom);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating status: " + e.getMessage());
        }
    }

    @PostMapping("/public/delete")
    public ResponseEntity<Object> deleteRoom(@Valid @RequestBody RoomIdRequestDTO roomIdRequest) {
        try {
            Long roomId = roomIdRequest.getRoomId();
            if (!roomService.existsById(roomId)) {
                return ResponseEntity.notFound().build();
            }
            roomService.deleteRoom(roomIdRequest);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting room: " + e.getMessage());
        }
    }
}
