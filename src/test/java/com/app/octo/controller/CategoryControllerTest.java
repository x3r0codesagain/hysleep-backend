package com.app.octo.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.app.octo.model.Category;
import com.app.octo.model.request.CategoryRequest;
import com.app.octo.model.request.CategoryUpdateRequest;
import com.app.octo.model.response.ApiResponse;
import com.app.octo.model.response.CategoryGetResponse;
import com.app.octo.model.response.CategoryResponse;
import com.app.octo.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CategoryControllerTest {

    public static final String CATEGORY_ID = "1";
    public static final String CATEGORY_NAME = "Luxury";

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    private Category category;
    private CategoryRequest categoryRequest;
    private CategoryUpdateRequest categoryUpdateRequest;
    private CategoryResponse categoryResponse;
    private ApiResponse<List<CategoryGetResponse>> categoryGetResponse;
    private MockMvc mockMvc;

    @Test
    public void create_category_success() throws Exception {
        when(categoryService.createCategory(categoryRequest.getCategoryName())).thenReturn(categoryResponse);


        this.mockMvc.perform(post("/api/v1/category/public/create-category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated());

        verify(categoryService).createCategory(categoryRequest.getCategoryName());
    }

    @Test
    public void create_category_failed_server() throws Exception {
        when(categoryService.createCategory(categoryRequest.getCategoryName())).thenReturn(categoryResponse);


        this.mockMvc.perform(post("/api/v1/category/public/create-category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated());

        verify(categoryService).createCategory(categoryRequest.getCategoryName());
    }

    @BeforeEach
    public void init(){
        initMocks(this);
        this.mockMvc = standaloneSetup(this.categoryController).build();

        category = Category.builder()
                    .categoryId(1)
                    .categoryName("VIP")
                    .build();
        
        categoryRequest = CategoryRequest.builder()
                    .categoryName("VIP")
                    .build();

        categoryUpdateRequest = CategoryUpdateRequest.builder()
                    .categoryId(1)
                    .categoryName("VIP")
                    .build();

        categoryResponse = CategoryResponse.builder()
                    .categoryId(1)
                    .categoryName("VIP")
                    .build();

        ApiResponse<List<CategoryGetResponse>> categoryGetResponse = new ApiResponse<>();
        categoryGetResponse.setData(new ArrayList<>());    
    }
    @AfterEach
    private void tearDown() {
        verifyNoMoreInteractions(categoryService);
    }
}
