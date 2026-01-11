package com.ritsard.baisard.domain.inventory.repository;

import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;
import com.ritsard.baisard.domain.inventory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByCategoryNameIgnoreCase(String categoryName);

    boolean existsByUuidCategoryAndIsDeletedFalse(UUID uuidCategory);


    @Query("""
                SELECT new com.ritsard.baisard.domain.inventory.dto.response.CategoryDto(
                    c.uuidCategory,
                    c.categoryName
                )
                FROM Category c
                ORDER BY c.categoryName ASC
            """)
    List<CategoryDto> findAllDto();

    @Query("""
                SELECT new com.ritsard.baisard.domain.inventory.dto.response.CategoryDto(
                    c.uuidCategory,
                    c.categoryName
                )
                FROM Category c
                WHERE c.uuidCategory = :uuidCategory
            """)
    Optional<CategoryDto> findDtoById(UUID uuidCategory);
}
