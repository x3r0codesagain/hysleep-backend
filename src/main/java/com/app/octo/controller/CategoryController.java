package com.app.octo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.octo.model.exception.AppException;
import com.app.octo.model.response.ApiResponse;
import com.app.octo.model.response.CategoryGetResponse;
import com.app.octo.model.response.CategoryResponse;
import com.app.octo.model.request.CategoryRequest;
import com.app.octo.model.request.CategoryUpdateRequest;
import com.app.octo.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/public/get-all")
    public ResponseEntity<ApiResponse<List<CategoryGetResponse>>> getAllCategories(){
        try {
            ApiResponse<List<CategoryGetResponse>> category = categoryService.getAllCategories();
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            ApiResponse<List<CategoryGetResponse>> errorResponse = new ApiResponse<List<CategoryGetResponse>>();
            errorResponse.setErrorCode("INTERNAL_SERVER_ERROR");
            errorResponse.setErrorMessage("Error fetching categories: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);

        }
    }
    @PostMapping("/public/create-category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request){
        try {
            CategoryResponse newCategory = categoryService.createCategory(request.getCategoryName());
            return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
        } catch (AppException e) {
            CategoryResponse response = new CategoryResponse();
            response.setErrorCode(e.getCode().name());
            response.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(response, e.getCode());
        } catch (Exception e) {
            CategoryResponse errorResponse = new CategoryResponse();
            errorResponse.setErrorCode("INTERNAL_SERVER_ERROR");
            errorResponse.setErrorMessage("Error creating category: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    @PostMapping("/public/update-name")
    public ResponseEntity<CategoryResponse> updateCategoryName(@Valid @RequestBody CategoryUpdateRequest request){
        try {
            CategoryResponse updateCategory = categoryService.updateCategoryName(request);
            return ResponseEntity.ok(updateCategory);
        } catch (AppException e) {
            CategoryResponse response = new CategoryResponse();
            response.setErrorCode(e.getCode().name());
            response.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(response, e.getCode());
        } catch (Exception e) {
            CategoryResponse response = new CategoryResponse();
            response.setErrorCode("INTERNAL_SERVER_ERROR");
            response.setErrorMessage("Error updating category: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        } 
    }

    @PostMapping("/public/delete-category")
    public ResponseEntity<CategoryResponse> deleteCategory(@RequestParam long categoryId){
        try{
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.noContent().build();
        } catch (AppException e) {
            CategoryResponse response = new CategoryResponse();
            response.setErrorCode(e.getCode().name());
            response.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(response, e.getCode());
        } catch (Exception e) {
            CategoryResponse response = new CategoryResponse();
            response.setErrorCode("INTERNAL_SERVER_ERROR");
            response.setErrorMessage("Error deleting category: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        } 

    }
}
