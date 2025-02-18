package com.app.octo.config;

import com.app.octo.model.response.BookingResponse;
import com.app.octo.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ExecutorScheduler {
  @Autowired
  private BookingService bookingService;

  @Scheduled(fixedRate = 3600000)
  public void autoUpdateBooking() {
    try {
      List<BookingResponse> result = bookingService.changeStatusAfterTime();
      log.info("Result:  " + result);
    } catch (Exception e) {
      log.error("unable to update");
    }
  }
}
