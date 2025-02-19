package com.app.octo.service.impl;

import com.app.octo.dto.request.RoomIdRequestDTO;
import com.app.octo.dto.request.RoomRequestDTO;
import com.app.octo.dto.request.RoomUpdateStatusRequestDTO;
import com.app.octo.dto.response.RoomResponseDTO;
import com.app.octo.model.Category;
import com.app.octo.model.Room;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.exception.AppException;
import com.app.octo.repository.CategoryRepository;
import com.app.octo.repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RoomServiceTest {

    @InjectMocks
    private RoomServiceImpl roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Room room;
    private Category category;
    private RoomRequestDTO roomRequestDTO;
    private RoomIdRequestDTO roomIdRequestDTO;
    private RoomUpdateStatusRequestDTO roomUpdateStatusRequestDTO;

    @BeforeEach
    public void init() {
        initMocks(this);

        category = Category.builder()
                .categoryId(1L)
                .categoryName("Deluxe")
                .build();

        room = Room.builder()
                .roomId(1L)
                .roomNumber("101")
                .status("AVAILABLE")
                .floor("1")
                .roomDescription("A cozy room")
                .category(category)
                .build();

        roomRequestDTO = RoomRequestDTO.builder()
                .roomNumber("101")
                .floor("1")
                .roomDescription("A cozy room")
                .categoryId(1L)
                .build();

        roomIdRequestDTO = new RoomIdRequestDTO();
        roomIdRequestDTO.setRoomId(1L);

        roomUpdateStatusRequestDTO = RoomUpdateStatusRequestDTO.builder()
                .roomId(1L)
                .status("BOOKED")
                .build();
    }

//    @AfterEach
//    public void tearDown() {
//        verifyNoMoreInteractions(roomRepository, categoryRepository);
//    }

    @Test
    void getAllRooms_success() {
        List<Room> rooms = Arrays.asList(room);
        when(roomRepository.findAll()).thenReturn(rooms);

        List<RoomResponseDTO> response = roomService.getAllRooms();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(room.getRoomId(), response.get(0).getRoomId());

        verify(roomRepository).findAll();
    }

    @Test
    void getRoomById_success() {
        when(roomRepository.existsById(1L)).thenReturn(true);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RoomResponseDTO response = roomService.getRoomById(roomIdRequestDTO);

        assertNotNull(response);
        assertEquals(room.getRoomId(), response.getRoomId());
//        verify(roomRepository).existsById(1L);
        verify(roomRepository).findById(1L);
    }

    @Test
    void getRoomById_roomNotFound_throwsRuntimeException() {
        when(roomRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roomService.getRoomById(roomIdRequestDTO));
        assertEquals("Room not found with id: 1", exception.getMessage());
    }

    @Test
    void createRoom_success() {
        when(categoryRepository.findByCategoryId(1L)).thenReturn(category);
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room savedRoom = invocation.getArgument(0);
            savedRoom.setRoomId(1L);
            return savedRoom;
        });

        RoomResponseDTO response = roomService.createRoom(roomRequestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getRoomId());
        assertEquals("AVAILABLE", response.getStatus());
        verify(categoryRepository).findByCategoryId(1L);
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void createRoom_categoryNotFound_throwsAppException() {
        when(categoryRepository.findByCategoryId(1L)).thenReturn(null);

        AppException exception = assertThrows(AppException.class,
                () -> roomService.createRoom(roomRequestDTO));
        assertEquals(ErrorCodes.CATEGORY_NOT_FOUND.getMessage(), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getCode());

        verify(categoryRepository).findByCategoryId(1L);
    }

    @Test
    void updateStatus_success() {
        Room updatedRoom = Room.builder()
                .roomId(1L)
                .roomNumber("101")
                .status("BOOKED")
                .floor("1")
                .roomDescription("A cozy room")
                .category(category)
                .build();

        when(roomRepository.existsById(1L)).thenReturn(true);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(updatedRoom);

        RoomResponseDTO response = roomService.updateStatus(roomUpdateStatusRequestDTO);

        assertNotNull(response);
        assertEquals("BOOKED", response.getStatus());
        verify(roomRepository).existsById(1L);
        verify(roomRepository).findById(1L);
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void updateStatus_roomNotFound_throwsEntityNotFoundException() {
        when(roomRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> roomService.updateStatus(roomUpdateStatusRequestDTO));
        assertEquals("Room with ID 1 not found.", exception.getMessage());

        verify(roomRepository).existsById(1L);
    }

    @Test
    void deleteRoom_success() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        doNothing().when(roomRepository).delete(room);

        roomService.deleteRoom(roomIdRequestDTO);

        verify(roomRepository).findById(1L);
        verify(roomRepository).delete(room);
    }

    @Test
    void deleteRoom_roomNotFound_throwsRuntimeException() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roomService.deleteRoom(roomIdRequestDTO));
        assertEquals("Room not found with id: 1", exception.getMessage());

        verify(roomRepository).findById(1L);
    }

    @Test
    void existsById_returnsTrueOrFalse() {
        when(roomRepository.existsById(1L)).thenReturn(true);
        when(roomRepository.existsById(2L)).thenReturn(false);

        assertTrue(roomService.existsById(1L));
        assertFalse(roomService.existsById(2L));

        verify(roomRepository).existsById(1L);
        verify(roomRepository).existsById(2L);
    }
}
