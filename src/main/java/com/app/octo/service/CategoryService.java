package com.app.octo.service;

import java.util.List;

import com.app.octo.model.request.CategoryRequest;
import com.app.octo.model.request.CategoryUpdateRequest;
import com.app.octo.model.response.ApiResponse;
import com.app.octo.model.response.CategoryGetResponse;
import com.app.octo.model.response.CategoryResponse;

public interface CategoryService {
    ApiResponse<List<CategoryGetResponse>> getAllCategories();
    CategoryResponse createCategory(String categoryName);
    CategoryResponse updateCategoryName(CategoryUpdateRequest request);
    void deleteCategory(long categoryId);
}
