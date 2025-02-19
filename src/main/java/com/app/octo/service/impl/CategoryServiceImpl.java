package com.app.octo.service.impl;

import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.app.octo.model.Category;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.CategoryUpdateRequest;
import com.app.octo.model.response.ApiResponse;
import com.app.octo.model.response.CategoryGetResponse;
import com.app.octo.model.response.CategoryResponse;
import com.app.octo.repository.CategoryRepository;
import com.app.octo.service.CategoryService;
import org.dozer.Mapper;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    private final Mapper mapper;

    public ApiResponse<List<CategoryGetResponse>> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();
        List<CategoryGetResponse> categoryResponses = categories.stream()
            .map(category -> mapper.map(category, CategoryGetResponse.class))
            .toList();
            ApiResponse<List<CategoryGetResponse>> apiResponse = new ApiResponse<>();
            apiResponse.setData(categoryResponses);
        return apiResponse;
    }

    public CategoryResponse createCategory(String categoryName){
        if(categoryName.isEmpty()){
            throw new AppException("Incomplete Request", HttpStatus.BAD_REQUEST);
        }
        Category categoryExists = categoryRepository.findByCategoryName(categoryName);
        if (Objects.nonNull(categoryExists)) {
            throw new AppException(ErrorCodes.CATEGORY_EXISTS.getMessage(), HttpStatus.NOT_FOUND);
        }

        Category newCategory = Category.builder().categoryName(categoryName).build();

        categoryRepository.save(newCategory);

        return mapper.map(newCategory, CategoryResponse.class);
    }

    public CategoryResponse updateCategoryName(CategoryUpdateRequest request){
        if(request.getCategoryId() == 0 || request.getCategoryName().isEmpty()){
            throw new AppException("Incomplete Request", HttpStatus.BAD_REQUEST);
        }
        Category category = categoryRepository.findByCategoryId(request.getCategoryId());
        if (Objects.isNull(category)) {
            throw new AppException(ErrorCodes.CATEGORY_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }

        Category existingCategory = categoryRepository.findByCategoryName(request.getCategoryName());
        if (Objects.nonNull(existingCategory)) {
            throw new AppException(ErrorCodes.CATEGORY_EXISTS.getMessage(), HttpStatus.NOT_FOUND);
        }

        category.setCategoryName(request.getCategoryName());
        categoryRepository.save(category);
        return mapper.map(category, CategoryResponse.class);
    }

    public void deleteCategory(long categoryId){
        Category category = categoryRepository.findByCategoryId(categoryId);
        if (Objects.isNull(category)) {
            throw new AppException(ErrorCodes.CATEGORY_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        try {
            categoryRepository.delete(category);
        } catch (DataIntegrityViolationException e) {
            throw new AppException("Cannot delete category. It is linked to existing rooms.", HttpStatus.BAD_REQUEST);
        }
    }
    
}
