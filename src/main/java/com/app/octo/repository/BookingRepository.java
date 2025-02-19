package com.app.octo.repository;

import com.app.octo.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  Booking findByBookingId(Long id);
  List<Booking> findAllByStatus(String status);
  List<Booking> findByUser_idAndStatus(Long userId, String status);
  List<Booking> findByUser_id(Long userId);
}
