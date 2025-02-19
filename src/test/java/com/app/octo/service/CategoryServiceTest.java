package com.app.octo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.app.octo.dto.response.RoomResponseDTO;
import com.app.octo.model.Room;
import org.dozer.Mapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import com.app.octo.controller.CategoryController;
import com.app.octo.model.Category;
import com.app.octo.model.enums.ErrorCodes;
import com.app.octo.model.exception.AppException;
import com.app.octo.model.request.CategoryRequest;
import com.app.octo.model.request.CategoryUpdateRequest;
import com.app.octo.model.response.ApiResponse;
import com.app.octo.model.response.CategoryGetResponse;
import com.app.octo.model.response.CategoryResponse;
import com.app.octo.repository.CategoryRepository;
import com.app.octo.service.impl.CategoryServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CategoryServiceTest {

    private static final String CATEGORY_NAME = "VIP";
    private static final long CATEGORY_ID = 1;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Mapper mapper;
    
    private Category category;
    private CategoryUpdateRequest categoryUpdateRequest;
    private CategoryResponse categoryResponse;
//    private CategoryGetResponse categoryGetResponse;

    @Test
    void getAllCategory_success(){
        List<Category> categories = List.of(new Category(1, "Category A"),
                                            new Category(2, "Category B"));
        when(categoryRepository.findAll()).thenReturn(categories);
        when(mapper.map(any(Category.class), eq(CategoryGetResponse.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    return new CategoryGetResponse(category.getCategoryId(), category.getCategoryName());
                });

        ApiResponse<List<CategoryGetResponse>> response = categoryService.getAllCategories();

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals(categories.get(0).getCategoryId(), response.getData().get(0).getCategoryId());
        assertEquals(categories.get(0).getCategoryName(), response.getData().get(0).getCategoryName());
        assertEquals(categories.get(1).getCategoryId(), response.getData().get(1).getCategoryId());
        assertEquals(categories.get(1).getCategoryName(), response.getData().get(1).getCategoryName());

        verify(categoryRepository).findAll();
        verify(mapper, times(categories.size())).map(any(Category.class), eq(CategoryGetResponse.class));


    }
    @Test
    void createCategory_success() {
        when(categoryRepository.findByCategoryName(CATEGORY_NAME)).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(mapper.map(any(), any())).thenReturn(categoryResponse);

        CategoryResponse response = categoryService.createCategory(CATEGORY_NAME);

        assertNotNull(response);
        assertEquals(CATEGORY_ID, response.getCategoryId());
        assertEquals(CATEGORY_NAME, response.getCategoryName());

        verify(categoryRepository).findByCategoryName(CATEGORY_NAME);
        verify(categoryRepository).save(any(Category.class));
        verify(mapper).map(any(), any());
    }

    @Test
    void createCategoryEmptyName_throwsException() {
        AppException exception = assertThrows(AppException.class, () -> 
            categoryService.createCategory("")
        );

        assertEquals("Incomplete Request", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());

        verify(categoryRepository, never()).findByCategoryName(any());
        verify(categoryRepository, never()).save(any());
    }
    @Test
    void createCategoryAlreadyExists_throwsException() {
        when(categoryRepository.findByCategoryName(CATEGORY_NAME)).thenReturn(category);

        AppException exception = assertThrows(AppException.class, () -> 
            categoryService.createCategory(CATEGORY_NAME)
        );

        assertEquals(ErrorCodes.CATEGORY_EXISTS.getMessage(), exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());

        verify(categoryRepository).findByCategoryName(CATEGORY_NAME);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategoryName_success() {
        categoryResponse.setCategoryName("Large Room");
        when(categoryRepository.findByCategoryId(categoryUpdateRequest.getCategoryId()))
                .thenReturn(category);
        when(categoryRepository.findByCategoryName(categoryUpdateRequest.getCategoryName()))
                .thenReturn(null);
        when(mapper.map(category, CategoryResponse.class))
                .thenReturn(categoryResponse);

        CategoryResponse response = categoryService.updateCategoryName(categoryUpdateRequest);

        assertNotNull(response);
        assertEquals("Large Room", response.getCategoryName());
        verify(categoryRepository).findByCategoryId(categoryUpdateRequest.getCategoryId());
        verify(categoryRepository).findByCategoryName(categoryUpdateRequest.getCategoryName());
        verify(categoryRepository).save(category);
        verify(mapper).map(category, CategoryResponse.class);
    }

    @Test
    void updateCategoryNameEmptyRequest_throwsException() {
        CategoryUpdateRequest request = new CategoryUpdateRequest(0, "");

        AppException exception = assertThrows(AppException.class, () ->
                categoryService.updateCategoryName(request)
        );

        assertEquals("Incomplete Request", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());

        verify(categoryRepository, never()).findByCategoryName(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategoryNameAlreadyExists_throwsException() {
        when(categoryRepository.findByCategoryId(categoryUpdateRequest.getCategoryId()))
                .thenReturn(category);
        when(categoryRepository.findByCategoryName(categoryUpdateRequest.getCategoryName()))
                .thenReturn(category);

        AppException exception = assertThrows(AppException.class, () ->
                categoryService.updateCategoryName(categoryUpdateRequest)
        );

        assertEquals(ErrorCodes.CATEGORY_EXISTS.getMessage(), exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());

        verify(categoryRepository).findByCategoryId(categoryUpdateRequest.getCategoryId());
        verify(categoryRepository).findByCategoryName(categoryUpdateRequest.getCategoryName());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategoryNameIdNotFound_throwsException() {
        when(categoryRepository.findByCategoryId(categoryUpdateRequest.getCategoryId()))
                .thenReturn(null);

        AppException exception = assertThrows(AppException.class, () ->
                categoryService.updateCategoryName(categoryUpdateRequest)
        );

        assertEquals(ErrorCodes.CATEGORY_NOT_FOUND.getMessage(), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getCode());

        verify(categoryRepository).findByCategoryId(categoryUpdateRequest.getCategoryId());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_success(){
        when(categoryRepository.findByCategoryId(CATEGORY_ID)).thenReturn(category);
        doNothing().when(categoryRepository).delete(category);

        categoryService.deleteCategory(CATEGORY_ID);

        verify(categoryRepository).findByCategoryId(CATEGORY_ID);
        verify(categoryRepository).delete(category);

    }
    @Test
    void deleteCategoryNotFound_throwsException(){
        when(categoryRepository.findByCategoryId(CATEGORY_ID)).thenReturn(null);
        AppException exception = assertThrows(AppException.class, () ->
                categoryService.deleteCategory(CATEGORY_ID)
        );

        assertEquals(ErrorCodes.CATEGORY_NOT_FOUND.getMessage(), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getCode());

        verify(categoryRepository).findByCategoryId(CATEGORY_ID);
        verify(categoryRepository, never()).delete(any());

    }

    @Test
    void deleteCategoryDataIntegrityViolation_throwsException(){
        when(categoryRepository.findByCategoryId(CATEGORY_ID)).thenReturn(category);
        doThrow(new DataIntegrityViolationException("Cannot delete category. It is linked to existing rooms."))
                .when(categoryRepository).delete(category);

        AppException exception = assertThrows(AppException.class, () ->
                categoryService.deleteCategory(CATEGORY_ID)
        );

        assertEquals("Cannot delete category. It is linked to existing rooms.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());

        verify(categoryRepository).findByCategoryId(CATEGORY_ID);
        verify(categoryRepository).delete(category);

    }
    @BeforeEach
    public void init(){
        initMocks(this);

        category = Category.builder()
                    .categoryId(CATEGORY_ID)
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
    }

      @AfterEach
        public void tearDown() {
            verifyNoMoreInteractions(categoryRepository);
            verifyNoMoreInteractions(mapper);
        }

}
