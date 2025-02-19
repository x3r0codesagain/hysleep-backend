package com.app.octo.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;

import com.app.octo.model.Category;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.CategoryRequest;
import com.app.octo.model.request.CategoryUpdateRequest;
import com.app.octo.model.response.ApiResponse;
import com.app.octo.model.response.CategoryGetResponse;
import com.app.octo.model.response.CategoryResponse;
import com.app.octo.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CategoryControllerTest {

    private static final String CATEGORY_NAME = "VIP";
    private static final long CATEGORY_ID = 1;

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
    void createCategory_success() throws Exception {
        when(categoryService.createCategory(categoryRequest.getCategoryName())).thenReturn(categoryResponse);


        this.mockMvc.perform(post("/api/v1/category/public/create-category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryRequest)))
                        .andExpect(status().isCreated());

        verify(categoryService).createCategory(categoryRequest.getCategoryName());
    }

    @Test
    void createCategoryExist_throwAppError() throws Exception {
        when(categoryService.createCategory(categoryRequest.getCategoryName()))
        .thenThrow(new AppException(ErrorCodes.CATEGORY_EXISTS.getMessage(), HttpStatus.NOT_FOUND));


        this.mockMvc.perform(post("/api/v1/category/public/create-category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryRequest)))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.NOT_FOUND.name())))
                        .andExpect(jsonPath("$.errorMessage", equalTo(ErrorCodes.CATEGORY_EXISTS.getMessage())));

        verify(categoryService).createCategory(categoryRequest.getCategoryName());
    }

    @Test
    void createCategory_throwException() throws Exception {
        when(categoryService.createCategory(categoryRequest.getCategoryName()))
        .thenThrow(HttpServerErrorException.InternalServerError.class);


        this.mockMvc.perform(post("/api/v1/category/public/create-category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryRequest)))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())));

        verify(categoryService).createCategory(categoryRequest.getCategoryName());
    }

    @Test
    void updateCategoryName_success() throws Exception {
        when(categoryService.updateCategoryName(categoryUpdateRequest)).thenReturn(categoryResponse);


        this.mockMvc.perform(post("/api/v1/category/public/update-name")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryUpdateRequest)))
                        .andExpect(status().isOk());

        verify(categoryService).updateCategoryName(categoryUpdateRequest);
    }

    @Test
    void updateCategoryName_throwAppError() throws Exception {
        when(categoryService.updateCategoryName(categoryUpdateRequest))
        .thenThrow(new AppException(ErrorCodes.CATEGORY_EXISTS.getMessage(), HttpStatus.NOT_FOUND));


        this.mockMvc.perform(post("/api/v1/category/public/update-name")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryUpdateRequest)))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.NOT_FOUND.name())))
                        .andExpect(jsonPath("$.errorMessage", equalTo(ErrorCodes.CATEGORY_EXISTS.getMessage())));

        verify(categoryService).updateCategoryName(categoryUpdateRequest);
    }

    @Test
    void updateCategoryName_throwException() throws Exception {
        when(categoryService.updateCategoryName(categoryUpdateRequest))
        .thenThrow(HttpServerErrorException.InternalServerError.class);


        this.mockMvc.perform(post("/api/v1/category/public/update-name")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryUpdateRequest)))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())));

        verify(categoryService).updateCategoryName(categoryUpdateRequest);
    }

    @Test
    void deleteCategory_success() throws Exception {
        doNothing().when(categoryService).deleteCategory(CATEGORY_ID);

        this.mockMvc.perform(post("/api/v1/category/public/delete-category")
                        .param("categoryId", String.valueOf(CATEGORY_ID))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());
        
        verify(categoryService).deleteCategory(CATEGORY_ID);
    }

    @Test
    void deleteCategoryNotFound_throwAppError() throws Exception {
        doThrow(new AppException(ErrorCodes.CATEGORY_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND))
        .when(categoryService).deleteCategory(CATEGORY_ID);

        this.mockMvc.perform(post("/api/v1/category/public/delete-category")
                        .param("categoryId", String.valueOf(CATEGORY_ID))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.NOT_FOUND.name())))
                        .andExpect(jsonPath("$.errorMessage", equalTo(ErrorCodes.CATEGORY_NOT_FOUND.getMessage())));
        
        verify(categoryService).deleteCategory(CATEGORY_ID);
    }

    @Test
    void deleteCategoryDataIntegrityViolation_throwAppError() throws Exception {
        doThrow(new AppException("Cannot delete category. It is linked to existing rooms.", HttpStatus.BAD_REQUEST))
        .when(categoryService).deleteCategory(CATEGORY_ID);

        this.mockMvc.perform(post("/api/v1/category/public/delete-category")
                        .param("categoryId", String.valueOf(CATEGORY_ID))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.name())))
                        .andExpect(jsonPath("$.errorMessage", equalTo("Cannot delete category. It is linked to existing rooms.")));
        
        verify(categoryService).deleteCategory(CATEGORY_ID);
    }

    @Test
    void deleteCategory_throwException() throws Exception {
        doThrow(HttpServerErrorException.InternalServerError.class)
        .when(categoryService).deleteCategory(CATEGORY_ID);

        this.mockMvc.perform(post("/api/v1/category/public/delete-category")
                        .param("categoryId", String.valueOf(CATEGORY_ID))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.name())));
        
        verify(categoryService).deleteCategory(CATEGORY_ID);
    }
    



    @BeforeEach
    public void init(){
        initMocks(this);
        this.mockMvc = standaloneSetup(this.categoryController).build();

        category = Category.builder()
                    .categoryId(CATEGORY_ID)
                    .categoryName(CATEGORY_NAME)
                    .build();
        
        categoryRequest = CategoryRequest.builder()
                    .categoryName(CATEGORY_NAME)
                    .build();

        categoryUpdateRequest = CategoryUpdateRequest.builder()
                    .categoryId(CATEGORY_ID)
                    .categoryName(CATEGORY_NAME)
                    .build();

        categoryResponse = CategoryResponse.builder()
                    .categoryId(CATEGORY_ID)
                    .categoryName(CATEGORY_NAME)
                    .build();

        ApiResponse<List<CategoryGetResponse>> categoryGetResponse = new ApiResponse<>();
        categoryGetResponse.setData(new ArrayList<>());    
    }
    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(categoryService);
    }
}
