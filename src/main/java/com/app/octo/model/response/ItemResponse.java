package com.app.octo.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ItemResponse {
  private String sku;
  private String name;
}
