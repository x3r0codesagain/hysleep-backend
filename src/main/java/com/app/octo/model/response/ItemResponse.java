package com.app.octo.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class ItemResponse {
  private String sku;
  private String name;
}
