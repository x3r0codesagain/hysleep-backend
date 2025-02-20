package com.app.octo.controller;

import com.app.octo.dto.request.RoomIdRequestDTO;
import com.app.octo.dto.request.RoomRequestDTO;
import com.app.octo.dto.request.RoomUpdateStatusRequestDTO;
import com.app.octo.dto.response.RoomResponseDTO;
import com.app.octo.model.Category;
import com.app.octo.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RoomControllerTest {

    @InjectMocks
    private RoomController roomController;

    @Mock
    private RoomService roomService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private RoomResponseDTO roomResponseDTO;
    private RoomRequestDTO roomRequestDTO;
    private RoomIdRequestDTO roomIdRequestDTO;
    private RoomUpdateStatusRequestDTO roomUpdateStatusRequestDTO;

    @BeforeEach
    public void init() {
        initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
        objectMapper = new ObjectMapper();

        Category category = Category.builder()
                .categoryId(1L)
                .categoryName("CATEGORY1")
                .build();

        roomResponseDTO = RoomResponseDTO.builder()
                .roomId(1L)
                .roomNumber("101")
                .status("AVAILABLE")
                .floor("Floor 3")
                .roomDescription("Hello World")
                .category(category)
                .build();

        roomRequestDTO = RoomRequestDTO.builder()
                .roomNumber("101")
                .floor("Floor 3")
                .roomDescription("Hello World")
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
//        verifyNoMoreInteractions(roomService);
//    }

    @Test
    void getAllRooms_success() throws Exception {
        List<RoomResponseDTO> rooms = Arrays.asList(roomResponseDTO);
        when(roomService.getAllRooms()).thenReturn(rooms);

        this.mockMvc.perform(post("/api/v1/rooms/public/getAll")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(rooms)));

        verify(roomService).getAllRooms();
    }

    @Test
    void getAllRooms_error() throws Exception {
        String errorMsg = "Database error";
        when(roomService.getAllRooms()).thenThrow(new RuntimeException(errorMsg));

        this.mockMvc.perform(post("/api/v1/rooms/public/getAll")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error fetching rooms: " + errorMsg));

        verify(roomService).getAllRooms();
    }

    @Test
    void getRoomById_success() throws Exception {
        when(roomService.existsById(1L)).thenReturn(true);
        when(roomService.getRoomById(any(RoomIdRequestDTO.class))).thenReturn(roomResponseDTO);

        this.mockMvc.perform(post("/api/v1/rooms/public/getById")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomIdRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(roomResponseDTO)));

        verify(roomService).existsById(1L);
        verify(roomService).getRoomById(any(RoomIdRequestDTO.class));
    }

    @Test
    void getRoomById_notFound() throws Exception {
        when(roomService.existsById(1L)).thenReturn(false);

        this.mockMvc.perform(post("/api/v1/rooms/public/getById")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomIdRequestDTO)))
                .andExpect(status().isNotFound());

        verify(roomService).existsById(1L);
    }

    @Test
    void getRoomById_error() throws Exception {
        String errorMsg = "Unexpected error";
        when(roomService.existsById(1L)).thenReturn(true);
        when(roomService.getRoomById(any(RoomIdRequestDTO.class))).thenThrow(new RuntimeException(errorMsg));

        this.mockMvc.perform(post("/api/v1/rooms/public/getById")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomIdRequestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error fetching room: " + errorMsg));

        verify(roomService).existsById(1L);
        verify(roomService).getRoomById(any(RoomIdRequestDTO.class));
    }

    @Test
    void createRoom_success() throws Exception {
        when(roomService.createRoom(any(RoomRequestDTO.class))).thenReturn(roomResponseDTO);

        this.mockMvc.perform(post("/api/v1/rooms/public/createRoom")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(roomResponseDTO)));

        verify(roomService).createRoom(any(RoomRequestDTO.class));
    }

    @Test
    void createRoom_error() throws Exception {
        String errorMsg = "Creation failed";
        when(roomService.createRoom(any(RoomRequestDTO.class))).thenThrow(new RuntimeException(errorMsg));

        this.mockMvc.perform(post("/api/v1/rooms/public/createRoom")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error creating room: " + errorMsg));

        verify(roomService).createRoom(any(RoomRequestDTO.class));
    }

    @Test
    void updateStatus_success() throws Exception {
        when(roomService.existsById(1L)).thenReturn(true);
        when(roomService.updateStatus(any(RoomUpdateStatusRequestDTO.class))).thenReturn(roomResponseDTO);

        this.mockMvc.perform(post("/api/v1/rooms/public/updateStatus")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomUpdateStatusRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(roomResponseDTO)));

        verify(roomService).existsById(1L);
        verify(roomService).updateStatus(any(RoomUpdateStatusRequestDTO.class));
    }

    @Test
    void updateStatus_notFound() throws Exception {
        when(roomService.existsById(1L)).thenReturn(false);

        this.mockMvc.perform(post("/api/v1/rooms/public/updateStatus")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomUpdateStatusRequestDTO)))
                .andExpect(status().isNotFound());

        verify(roomService).existsById(1L);
    }

    @Test
    void updateStatus_error() throws Exception {
        String errorMsg = "Update failed";
        when(roomService.existsById(1L)).thenReturn(true);
        when(roomService.updateStatus(any(RoomUpdateStatusRequestDTO.class))).thenThrow(new RuntimeException(errorMsg));

        this.mockMvc.perform(post("/api/v1/rooms/public/updateStatus")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomUpdateStatusRequestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error updating status: " + errorMsg));

        verify(roomService).existsById(1L);
        verify(roomService).updateStatus(any(RoomUpdateStatusRequestDTO.class));
    }

    @Test
    void deleteRoom_success() throws Exception {
        when(roomService.existsById(1L)).thenReturn(true);

        this.mockMvc.perform(post("/api/v1/rooms/public/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomIdRequestDTO)))
                .andExpect(status().isNoContent());

        verify(roomService).existsById(1L);
        verify(roomService).deleteRoom(any(RoomIdRequestDTO.class));
    }

    @Test
    void deleteRoom_notFound() throws Exception {
        when(roomService.existsById(1L)).thenReturn(false);

        this.mockMvc.perform(post("/api/v1/rooms/public/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomIdRequestDTO)))
                .andExpect(status().isNotFound());

        verify(roomService).existsById(1L);
    }

    @Test
    void deleteRoom_error() throws Exception {
        String errorMsg = "Delete failed";
        when(roomService.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException(errorMsg))
                .when(roomService).deleteRoom(any(RoomIdRequestDTO.class));

        this.mockMvc.perform(post("/api/v1/rooms/public/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomIdRequestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error deleting room: " + errorMsg));

        verify(roomService).existsById(1L);
        verify(roomService).deleteRoom(any(RoomIdRequestDTO.class));
    }
}
