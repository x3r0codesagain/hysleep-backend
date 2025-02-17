package com.app.octo.controller;

import com.app.octo.model.Item;
import com.app.octo.model.response.ItemResponse;
import com.app.octo.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/v1/item")
public class ItemController {

  @Autowired
  private ItemRepository itemRepository;


  @PostMapping("/add")
  public ResponseEntity<ItemResponse> addItem() {

    try {
      Item item = Item.builder()
              .sku(UUID.randomUUID().toString()).name("trial").build();

      itemRepository.save(item);

      ItemResponse response = new ItemResponse(item.getSku(), item.getSku());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }
}
