package com.ritsard.baisard.domain.inventory.service;

import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<CategoryDto> getCategories();

    CategoryDto getCategory(UUID uuidCategory);

    void newCategory(CategoryDto categoryDto);

    void updateCategory(UUID uuidCategory, CategoryDto categoryDto);

    void deleteCategory(UUID uuidCategory);
}
