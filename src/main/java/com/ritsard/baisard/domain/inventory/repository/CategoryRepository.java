package com.ritsard.baisard.domain.inventory.repository;

import com.ritsard.baisard.domain.inventory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByCategoryNameIgnoreCase(String categoryName);

    boolean existsByUuidCategoryAndIsDeletedFalse(UUID uuidCategory);


}
