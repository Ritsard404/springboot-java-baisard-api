package com.ritsard.baisard.domain.inventory.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ritsard.baisard.domain.inventory.dto.response.CategoryDto;
import com.ritsard.baisard.domain.inventory.entity.Category;
import com.ritsard.baisard.domain.inventory.entity.QCategory;
import com.ritsard.baisard.domain.inventory.entity.QProduct;
import com.ritsard.baisard.domain.inventory.repository.CategoryRepository;
import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.global.utils.Formats;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final AuthManager<Member> authManager;
    private final CategoryRepository categoryRepository;
    private final JPAQueryFactory queryFactory;


    @Override
    public List<CategoryDto> getCategories() {
        QCategory c = QCategory.category;
        QProduct p = QProduct.product;

        Member member = authManager.getMember();
        UUID companyUuid = (member != null && member.getCompany() != null)
                ? member.getCompany().getUuidCompany()
                : null;

        BooleanBuilder where = new BooleanBuilder();

        if (companyUuid != null) {
            where.and(
                    p.company.uuidCompany.eq(companyUuid)
                            .or(p.company.isNull())
            );
        }
        return queryFactory
                .select(Projections.constructor(
                        CategoryDto.class,
                        c.uuidCategory,
                        c.categoryName
                ))
                .from(c)
                .leftJoin(c.products, p)
                .where(where)
                .distinct()
                .orderBy(c.categoryName.asc())
                .fetch();
    }

    @Override
    public CategoryDto getCategory(UUID uuidCategory) {
        return categoryRepository.findDtoById(uuidCategory)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    public void newCategory(CategoryDto dto) {
        String name = dto.getCategoryName().trim(); // Crucial: removes hidden spaces

        if (categoryRepository.existsByCategoryNameIgnoreCaseAndCompany_UuidCompany(name, memberCompany().getUuidCompany())) {
            throw new ConflictException("Category '" + name + "' already exists in your company.");
        }

        categoryRepository.save(Category.builder()
                .categoryName(name)
                .company(memberCompany())
                .build());
    }

    @Override
    public void updateCategory(UUID uuidCategory, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(uuidCategory)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        category.setCategoryName(Formats.capitalize(categoryDto.getCategoryName()));
    }

    @Override
    public void deleteCategory(UUID uuidCategory) {
        if (categoryRepository.existsByUuidCategoryAndIsDeletedFalse(uuidCategory))
            throw new ConflictException("Cannot delete category with existing products");


        Category category = categoryRepository.findById(uuidCategory)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        category.softDelete();
        categoryRepository.save(category);
    }

    private Company memberCompany() {
        Member member = authManager.getMember();
        return member.getCompany();
    }
}
