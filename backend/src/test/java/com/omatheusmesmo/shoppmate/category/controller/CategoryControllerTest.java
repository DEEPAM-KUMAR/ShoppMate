package com.omatheusmesmo.shoppmate.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omatheusmesmo.shoppmate.auth.service.JwtService;
import com.omatheusmesmo.shoppmate.category.dto.CategoryRequestDTO;
import com.omatheusmesmo.shoppmate.category.dto.CategoryResponseDTO;
import com.omatheusmesmo.shoppmate.category.entity.Category;
import com.omatheusmesmo.shoppmate.category.mapper.CategoryMapper;
import com.omatheusmesmo.shoppmate.category.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryMapper categoryMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category;
    private CategoryResponseDTO categoryResponseDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Food");
        categoryResponseDTO = new CategoryResponseDTO(1L, "Food");
    }

    @Test
    @WithMockUser
    void getAllCategories() throws Exception {
        when(categoryService.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toResponseDTO(any(Category.class))).thenReturn(categoryResponseDTO);

        mockMvc.perform(get("/category")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(categoryResponseDTO))));
    }

    @Test
    @WithMockUser
    void addCategory() throws Exception {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Food");
        when(categoryMapper.toEntity(any(CategoryRequestDTO.class))).thenReturn(category);
        when(categoryService.saveCategory(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponseDTO(any(Category.class))).thenReturn(categoryResponseDTO);

        mockMvc.perform(post("/category").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryResponseDTO)));
    }
}
