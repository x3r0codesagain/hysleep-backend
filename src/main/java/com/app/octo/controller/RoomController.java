package com.app.octo.controller;

import com.app.octo.dto.request.RoomRequestDTO;
import com.app.octo.dto.request.RoomUpdateStatusRequestDTO;
import com.app.octo.dto.response.RoomResponseDTO;
import com.app.octo.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;
    private static final Set<String> ALLOWED_STATUSES = Set.of("BOOKED", "AVAILABLE", "MAINTENANCE");

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/public/getAll")
    public ResponseEntity<?> getAllRooms() {
        try {
            List<RoomResponseDTO> rooms = roomService.getAllRooms();
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching rooms: " + e.getMessage());
        }
    }

    @GetMapping("/public/getById/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable("id") long roomId) {
        try {
            if (!roomService.existsById(roomId)) {
                return ResponseEntity.notFound().build();
            }
            RoomResponseDTO room = roomService.getRoomById(roomId);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching room: " + e.getMessage());
        }
    }

    @PostMapping("/public/createRoom")
    public ResponseEntity<?> createRoom(@RequestBody RoomRequestDTO roomRequest) {
        try {
            RoomResponseDTO createdRoom = roomService.createRoom(roomRequest);
            return ResponseEntity.ok(createdRoom);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating room: " + e.getMessage());
        }
    }

    @PutMapping("/public/updateStatus/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable("id") long roomId, @RequestBody RoomUpdateStatusRequestDTO roomUpdateStatus) {
        try {
            if (!roomService.existsById(roomId)) {
                return ResponseEntity.notFound().build();
            }

            String status = roomUpdateStatus.getStatus();
            if (!ALLOWED_STATUSES.contains(status)) {
                return ResponseEntity.badRequest().body("Invalid status. Allowed statuses: BOOKED, AVAILABLE, MAINTENANCE.");
            }

            RoomResponseDTO updatedRoom = roomService.updateStatus(roomId, roomUpdateStatus);
            return ResponseEntity.ok(updatedRoom);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating status: " + e.getMessage());
        }
    }

    @DeleteMapping("/public/delete/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable("id") long roomId) {
        try {
            if (!roomService.existsById(roomId)) {
                return ResponseEntity.notFound().build();
            }
            roomService.deleteRoom(roomId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting room: " + e.getMessage());
        }
    }
}
