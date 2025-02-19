package com.app.octo.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateRequest {
    @NotBlank(message = "Category ID is required")
    private long categoryId;
    @NotBlank(message = "Category name is required")
    private String categoryName;
}
