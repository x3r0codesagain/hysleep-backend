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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.octo.dto.response.RoomResponseDTO;
import com.app.octo.model.response.ApiResponse;
import com.app.octo.model.response.CategoryGetResponse;
import com.app.octo.model.response.CategoryResponse;
import com.app.octo.model.request.CategoryRequest;
import com.app.octo.model.request.CategoryUpdateRequest;
import com.app.octo.service.CategoryService;

@RestController
@RequestMapping("api/v1/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/public/get-all")
    public ResponseEntity<?> getAllCategories(){
        try {
            ApiResponse<List<CategoryGetResponse>> category = categoryService.getAllCategories();
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching categories: " + e.getMessage());
        }
    }
    @PostMapping("/public/create-category")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request){
        try {
            CategoryResponse newCategory = categoryService.createCategory(request.getCategoryName());
            return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating category: " + e.getMessage());
        } 
    }
    @PostMapping("/public/update-name")
    public ResponseEntity<?> updateCategoryName(@RequestBody CategoryUpdateRequest request){
        try {
            CategoryResponse newCategory = categoryService.updateCategoryName(request);
            return ResponseEntity.ok(newCategory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating category: " + e.getMessage());
        } 
    }

    @PostMapping("/public/delete-category")
    public ResponseEntity<?> deleteCategory(@RequestParam long categoryId){
        try{
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting category: " + e.getMessage());
        } 

    }
}
