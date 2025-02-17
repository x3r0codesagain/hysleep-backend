package com.app.octo.repository;

import com.app.octo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
  Room findByRoomIdAndStatus(Long id, String status);
}